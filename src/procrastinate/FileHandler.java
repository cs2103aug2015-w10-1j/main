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

    private static final String DEBUG_FILE_INIT = "FileHandler initialised. Using file ";
    private static final String DEBUG_FILE_WRITE_SUCCESS = "Wrote to file:\n";
    private static final String DEBUG_FILE_WRITE_FAILURE = "Could not write to file";
    private static final String DEBUG_FILE_LOAD_SUCCESS = "Loaded %1$s task(s) from file";
    private static final String DEBUG_FILE_LOAD_FAILURE = "Could not load from file";
    private static final String DEBUG_FILE_PARSE_FAILURE = "Unrecognisable file format";
    private static final String DEBUG_CONFIG_WRITE_FAILURE = "Could not write to configuration file";
    private static final String DEBUG_SET_PATH_FAILURE = "Could not set to new path %1$s";
    private static final String DEBUG_SET_PATH_SUCCESS = "Path set to %1$s";

    // ================================================================================
    // Defaults
    // ================================================================================

    private static final String DEFAULT_FILENAME = "storage";
    private static final String DEFAULT_FILE_EXTENSION = ".json";
    private static final String DEFAULT_FULL_FILENAME = DEFAULT_FILENAME + DEFAULT_FILE_EXTENSION;
    private static final String CONFIG_PATH = "settings.config";

    // ================================================================================
    // Instance variables
    // ================================================================================

    private String fullFilename = DEFAULT_FULL_FILENAME;
    private File saveFile;
    private File configFile;
    private BufferedWriter bw = null;

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
            loadTaskState(saveFile);
        } else {
            saveFile = makeFile(savePath);
            makeEmptyState();
        }
        this.saveFile = saveFile;
        this.fullFilename = savePath.getFileName().toString();

        logger.log(Level.INFO, DEBUG_FILE_INIT + saveFile.getCanonicalPath());
    }

    public FileHandler(boolean isUnderTest) {
    }

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

    /**
     * Loads TaskState from a json file
     * @return TaskState
     */
    public TaskState loadTaskState() throws FileNotFoundException {
        return loadTaskState(saveFile);
    }

    /**
     * Sets new path for save file. Given path must not be an existing file otherwise method fails
     * @param newPath A non-existing path.
     * @return true on success, false otherwise
     */
    public boolean setPath(String newPath) {
        try {
            Path p = updateSaveFile(updateConfig(newPath));
            fullFilename = p.getFileName().toString();
            saveFile = p.toFile();

            logger.log(Level.INFO, String.format(DEBUG_SET_PATH_SUCCESS, newPath));
            return true;
        } catch (IOException e) {
            logger.log(Level.SEVERE, String.format(DEBUG_SET_PATH_FAILURE, newPath));
            return false;
        }
    }

    public boolean setPath(Path newPath) {
        return setPath(newPath.toString());
    }

    public String getFilename() {
        return fullFilename;
    }

    public File getSaveFile() {
        return saveFile;
    }

    public File getConfigFile() {
        return configFile;
    }

    /**
     * Loads from existing configuration if it exists, otherwise initialise configuration
     * file with default settings
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
                System.out.println(p.toString());
                saveFile = p.toFile();
            } else {
                writer = new BufferedWriter(new FileWriter(configFile));
                writer.write(DEFAULT_FULL_FILENAME);
                writer.flush();

                p = Paths.get(DEFAULT_FULL_FILENAME);
                saveFile = p.toFile();
            }
            reader.close();
        } else if (Files.notExists(configFile.toPath())) {
            Files.createFile(configFile.toPath());
            writer = new BufferedWriter(new FileWriter(configFile));

            writer.write(DEFAULT_FULL_FILENAME);
            writer.flush();

            p = Paths.get(DEFAULT_FULL_FILENAME);
            saveFile = p.toFile();
        }

        if (writer !=null) {
            writer.close();
        }
        return p;
    }

    /**
     * Writes new configuration to file
     *
     * @param savePath
     */
    private Path updateConfig(String savePath) throws IOException {
        File oldFile = configFile;
        File tmp = null;
        BufferedWriter writer = null;
        Path p = null;

        if (!hasFileName(savePath)) {
            savePath = savePath + fullFilename;
        }

        // write to a tmp file then replace the old file with the new one
        try {
            tmp = Files.createTempFile(Paths.get(""), "tmp", "").toFile();
            writer = new BufferedWriter(new FileWriter(tmp));
            writer.write(savePath);
            writer.flush();
            tmp.renameTo(oldFile);
            p = Paths.get(savePath);
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
        return p;
    }

    /**
     * Move save file to a new location. Does not overwrite if file exists
     *
     * @param savePath
     * @return
     * @throws IOException
     */
    private Path updateSaveFile(Path savePath) throws IOException {
        File oldSave = saveFile;
        savePath.toFile().getParentFile().mkdirs();

        if (hasFileName(savePath)) {
            Files.move(oldSave.toPath(), savePath);
        } else {
            savePath = savePath.resolve(DEFAULT_FULL_FILENAME);
            Files.move(oldSave.toPath(), savePath);
        }

        return savePath;
    }

    /**
     * Loads TaskState from json formatted file
     *
     * @return TaskState that was saved when the application last closed.
     */
    private TaskState loadTaskState(File file) throws FileNotFoundException {
        BufferedReader br = null;
        Gson gson = new GsonBuilder().registerTypeAdapter(Task.class, new TaskDeserializer())
                .registerTypeAdapter(Date.class, new DateAdapter()).create();
        Type type = new TypeToken<TaskState>() {}.getType();

        try {
            br = new BufferedReader(new FileReader(file));
            TaskState taskState = gson.fromJson(br, type);

            logger.log(Level.INFO, String.format(DEBUG_FILE_LOAD_SUCCESS, taskState.getTasks().size()));
            return taskState;
        } catch (FileNotFoundException e) {
            logger.log(Level.WARNING, DEBUG_FILE_LOAD_FAILURE);
            throw e;
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

    // ================================================================================
    // Utility methods
    // ================================================================================

    private void jsonToFile(String json) throws IOException {
        saveFile.getParentFile().mkdirs();
        saveFile.createNewFile();
        bw = new BufferedWriter(new FileWriter(saveFile));
        bw.write(json);
        bw.close();
        logger.log(Level.INFO, DEBUG_FILE_WRITE_SUCCESS + json);
    }

    private String jsonify(TaskState taskState) {
        Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls()
                .registerTypeAdapter(Date.class, new DateAdapter()).create();
        String json = gson.toJson(taskState);

        return json;
    }

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

    private File makeNewFile(Path target) throws IOException {
        assert Files.notExists(target);

        File file = null;

        if (hasFileName(target)) {
            Files.createDirectories(target.toAbsolutePath().getParent().normalize());
            file = Files.createFile(target).toFile();
        } else {
            Files.createDirectories(target.toAbsolutePath());
            file = Files.createFile(target.resolve(Paths.get(fullFilename))).toFile();
        }

        return file;
    }

    /**
     * Makes a empty state when a file is first initialised so that the json
     * file has the right structure
     */
    private void makeEmptyState() throws IOException {
        saveTaskState(new TaskState());
    }

    // uses file extension to check for filename within a path
    private boolean hasFileName(String directoryPath) {
        String pattern = ".*\\" + DEFAULT_FILE_EXTENSION;
        return directoryPath.matches(pattern);
    }

    private boolean hasFileName(Path directoryPath) {
        return hasFileName(directoryPath.toString());
    }

}