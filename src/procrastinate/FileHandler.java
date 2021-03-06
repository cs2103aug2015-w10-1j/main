package procrastinate;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.nio.file.FileAlreadyExistsException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import procrastinate.task.DateAdapter;
import procrastinate.task.Task;
import procrastinate.task.TaskDeserializer;
import procrastinate.task.TaskState;

import java.lang.reflect.Type;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileHandler {

    private static final Logger logger = Logger.getLogger(FileHandler.class.getName());

    // ================================================================================
    // Message strings
    // ================================================================================

    private static final String DEBUG_FILE_INIT = "FileHandler initialised. Using file %1$s";
    private static final String DEBUG_FILE_WRITE_SUCCESS = "Wrote to file:\n";
    private static final String DEBUG_FILE_WRITE_FAILURE = "Could not write to file";
    private static final String DEBUG_FILE_LOAD_SUCCESS = "Loaded %1$s task(s) from file";
    private static final String DEBUG_FILE_LOAD_NOT_FOUND = "File not found; creating new file";
    private static final String DEBUG_FILE_PARSE_FAILURE = "Unrecognisable file format";
    private static final String DEBUG_CONFIG_EXISTS = "Config exists, reading from %1$s ";
    private static final String DEBUG_CONFIG_ABSENT = "Config file missing, making file %1$s ";
    private static final String DEBUG_CONFIG_WRITE_FAILURE = "Could not write to configuration file";
    private static final String DEBUG_SET_PATH_FAILURE = "Could not set to new path %1$s";
    private static final String DEBUG_SET_PATH_SUCCESS = "Path set to %1$s";
    private static final String DEBUG_FILE_INDETERMINANT = "%1$s existence cannot be determined";

    // ================================================================================
    // Defaults
    // ================================================================================

    public static final String DEFAULT_FULL_FILENAME = "storage.json";
    private static final String CONFIG_PATH = "settings.config";

    // ================================================================================
    // Instance variables
    // ================================================================================

    private String fullFilename = DEFAULT_FULL_FILENAME;
    private File saveFile;
    private File configFile;
    private BufferedWriter bw = null;

    //@@author A0124321Y
    /**
     * FileHandler constructor. loads configuration and storage information.
     * Absence of config file is considered as first launch. Config and save files will
     * be initialised accordingly. For subsequent launches, loads config and save files.
     */
    public FileHandler() throws IOException {
        File saveFile = null;
        configFile = Paths.get(CONFIG_PATH).toFile();

        Path savePath = loadConfig();
        if (Files.exists(savePath)) {
            saveFile = savePath.toFile();
        } else {
            saveFile = makeFile(savePath);
            makeEmptyState();
        }
        this.saveFile = saveFile;
        this.fullFilename = savePath.getFileName().toString();

        logger.log(Level.INFO, String.format(DEBUG_FILE_INIT, saveFile.getCanonicalPath()));
    }
    //@@author

    //@@author A0080485B
    public FileHandler(boolean isUnderTest) {
    }

    //@@author A0124321Y
    /**
     * Converts TaskState into json format and writes to disk
     * @param taskState
     */
    public boolean saveTaskState(TaskState taskState) {
        String json = jsonify(taskState);
        try {
            jsonToFile(json);
        } catch (IOException e) {
            logger.log(Level.SEVERE, DEBUG_FILE_WRITE_FAILURE);
            return false;
        }
        return true;
    }

    //@@author A0124321Y
    /**
     * Loads TaskState from a json file
     * @return TaskState
     */
    public TaskState loadTaskState() {
        return loadTaskState(saveFile);
    }

    //@@author A0124321Y
    /**
     * Sets new path for save file.
     * Will not overwrite a file, fails if the path already exists.
     * On contrary, all non-existent directory and file will be created.
     *
     * @param dir must end with '/'. filename should not have file extension
     * @return true on success, false otherwise
     */
    public boolean setPath(String dir, String filename) {
        assert dir.endsWith(File.separator);

        if (filename == null || filename.isEmpty()) {
            filename = DEFAULT_FULL_FILENAME;
        }

        Path newPath = Paths.get(dir + filename);

        try {
            saveFile = updateSaveFile(newPath).toFile();
            updateConfig(newPath);
            fullFilename = filename;

            logger.log(Level.INFO, String.format(DEBUG_SET_PATH_SUCCESS, newPath));
            return true;
        } catch (IOException e) {
            logger.log(Level.SEVERE, String.format(DEBUG_SET_PATH_FAILURE, newPath));
            return false;
        }
    }

    //@@author A0124321Y
    public String getFilename() {
        return fullFilename;
    }

    //@@author A0124321Y
    public File getSaveFile() {
        return saveFile;
    }

    //@@author A0124321Y
    public File getConfigFile() {
        return configFile;
    }

    //@@author A0124321Y
    /**
     * Loads from existing configuration if it exists, otherwise initialise configuration
     * file with default settings. If config file does not exists, storage file is assumed
     * to not exists
     *
     * @return savePath, the path of the storage file.
     */
    private Path loadConfig() throws IOException {
        BufferedReader reader = null;
        BufferedWriter writer = null;
        Path p = null;

        if (Files.exists(configFile.toPath())) {
            reader = new BufferedReader(new FileReader(configFile));

            String line = reader.readLine();
            reader.close();
            if (line != null) {
                fullFilename = line;

                p = Paths.get(line);
                saveFile = p.toFile();
            } else {
                writer = new BufferedWriter(new FileWriter(configFile));
                writer.write(DEFAULT_FULL_FILENAME);
                writer.flush();

                p = Paths.get(DEFAULT_FULL_FILENAME);
                saveFile = p.toFile();
            }
            reader.close();
            logger.log(Level.INFO, String.format(DEBUG_CONFIG_EXISTS,  configFile.getCanonicalPath()));
        } else if (Files.notExists(configFile.toPath())) {
            Files.createFile(configFile.toPath());
            writer = new BufferedWriter(new FileWriter(configFile));

            writer.write(Paths.get(DEFAULT_FULL_FILENAME).toAbsolutePath().toString());
            writer.flush();

            p = Paths.get(DEFAULT_FULL_FILENAME);
            saveFile = p.toFile();
            logger.log(Level.INFO, String.format(DEBUG_CONFIG_ABSENT,  configFile.getCanonicalPath()));
        } else {
            logger.log(Level.SEVERE, String.format(DEBUG_FILE_INDETERMINANT, configFile.getCanonicalPath()));
        }

        if (writer !=null) {
            writer.close();
        }
        return p;
    }

    //@@author A0124321Y
    /**
     * Writes new configuration to file. Save path will be converted to absolute path
     * to make it easier for advance users to edit
     *
     * @param savePath Must be an existing path
     * @return
     */
    private boolean updateConfig(Path savePath) throws IOException {
        assert Files.exists(savePath);
        assert Files.isRegularFile(savePath);

        String abPath = savePath.toAbsolutePath().normalize().toString();
        BufferedWriter writer = null;
        boolean success = false;

        // overwrite the contents of the file.
        try {
            writer = new BufferedWriter(new FileWriter(configFile));
            writer.write(abPath);
            writer.flush();

            success = true;
        } catch (IOException e) {
            logger.log(Level.SEVERE, DEBUG_CONFIG_WRITE_FAILURE);
            throw e;
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return success;
    }

    //@@author A0124321Y
    /**
     * Move save file to a new location. Does not overwrite if file exists
     *
     * @param savePath
     * @return
     * @throws IOException
     */
    private Path updateSaveFile(Path savePath) throws IOException {
        File oldSave = saveFile;
        Path parentDir = savePath.getParent();

        if (parentDir != null) {
            Files.createDirectories(parentDir);
        }

        Files.move(oldSave.toPath(), savePath);

        return savePath;
    }

    //@@author A0124321Y
    /**
     * Loads TaskState from json formatted file
     *
     * @return TaskState parsed from file, or an empty TaskState if the file is not found or invalid
     */
    private TaskState loadTaskState(File file) {
        BufferedReader br = null;
        Gson gson = new GsonBuilder().registerTypeAdapter(Task.class, new TaskDeserializer())
                .registerTypeAdapter(Date.class, new DateAdapter()).create();
        Type type = new TypeToken<TaskState>() {}.getType();

        try {
            br = new BufferedReader(new FileReader(file));
            TaskState taskState = gson.fromJson(br, type);

            if (taskState == null) {
                taskState = new TaskState();
            }

            logger.log(Level.INFO, String.format(DEBUG_FILE_LOAD_SUCCESS, taskState.getTasks().size()));
            return taskState;
        } catch (FileNotFoundException e) {
            logger.log(Level.WARNING, DEBUG_FILE_LOAD_NOT_FOUND);
            return new TaskState();
        } catch (JsonParseException e) {
            logger.log(Level.WARNING, DEBUG_FILE_PARSE_FAILURE);
            return new TaskState();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };
    //@@author

    // ================================================================================
    // Utility methods
    // ================================================================================

    //@@author A0124321Y
    private void jsonToFile(String json) throws IOException {
        File parentDir = saveFile.getAbsoluteFile().getParentFile();
        if (parentDir != null) {
            parentDir.mkdirs();
        }
        saveFile.createNewFile();
        bw = new BufferedWriter(new FileWriter(saveFile));
        bw.write(json);
        bw.close();
        logger.log(Level.INFO, DEBUG_FILE_WRITE_SUCCESS + json);
    }

    //@@author A0124321Y
    private String jsonify(TaskState taskState) {
        Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls()
                .registerTypeAdapter(Date.class, new DateAdapter()).create();
        String json = gson.toJson(taskState);

        return json;
    }

    //@@author A0124321Y
    /**
     * Make a new save file based on given path.
     *
     * @param path of new file
     * @throws FileAlreadyExistsException
     * @author Gerald
     */
    private File makeFile(Path target) throws IOException {
        if (Files.notExists(target)) {
            return makeNewFile(target);
        } else {
            throw new FileAlreadyExistsException(target.toString() + " already exists");
        }
    }

    //@@author A0124321Y
    private File makeNewFile(Path target) throws IOException {
        assert Files.notExists(target);

        Path parentDir = target.toAbsolutePath().getParent();
        if (parentDir != null) {
            Files.createDirectories(parentDir.normalize());
        }
        return Files.createFile(target).toFile();
    }

    //@@author A0124321Y
    /**
     * Makes a empty state when a file is first initialised so that the json
     * file has the right structure
     */
    private void makeEmptyState() throws IOException {
        saveTaskState(new TaskState());
    }
    //@@author
}