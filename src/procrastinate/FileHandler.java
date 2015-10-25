package procrastinate;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import procrastinate.task.Task;
import procrastinate.task.TaskDeserializer;
import procrastinate.task.TaskState;

import java.lang.reflect.Type;
import java.text.DateFormat;
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

    private static final String DEFAULT_FILENAME = "storage";
    private static final String DEFAULT_FILE_EXTENSION = ".json";

    // ================================================================================
    // Instance variables
    // ================================================================================

    private final String filename = DEFAULT_FILENAME;
    private final String fileExtension = DEFAULT_FILE_EXTENSION;
    private final String fullFilename = filename + fileExtension;
    private File file;
    private BufferedWriter bw = null;

    public FileHandler() throws IOException {
        this("");
    }

    public FileHandler(boolean isUnderTest) {
    }

    /**
     * Make a new file based on give directoryPath. directoryPath can be
     * multi-level directory and any type of path i.e. absolute or relative.
     * Will not overwrite file if it already exists.
     * Will make directories if it does not exists.
     *
     * @param path if only directory is given, default file name is used otherwise
     * provide a filename along json extension
     * @throws IOException
     * @author Gerald
     */
    public FileHandler(String path) throws IOException {
        Path pathOnly = null;
        Path pathWithFileName = null;

        if (hasFileName(path)) {
            pathWithFileName = Paths.get(path).normalize();
            pathOnly = pathWithFileName.toAbsolutePath().getParent();
        } else {
            pathWithFileName = Paths.get(path + fullFilename);
            pathOnly = Paths.get(path);
        }

        assert pathOnly != null;
        assert pathWithFileName != null;

        // if file does not exists, directory might not exists too
        if (Files.notExists(pathWithFileName)) {
            if (Files.notExists(pathOnly)) {
                Files.createDirectories(pathOnly);
            }
            file = Files.createFile(pathWithFileName).toFile();
            makeEmptyState();
        } else {
            file = pathWithFileName.toFile();
        }


        logger.log(Level.INFO, DEBUG_FILE_INIT + file.getCanonicalPath());
    }

    // ================================================================================
    // FileHandler methods
    // ================================================================================

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
        return loadTaskState(file);
    }

    /**
     * Loads TaskState from json formatted file
     *
     * @return TaskState that was saved when the application last closed.
     */
    private TaskState loadTaskState(File file) throws FileNotFoundException {
        BufferedReader br = null;
        Gson gson = new GsonBuilder().registerTypeAdapter(Task.class, new TaskDeserializer()).create();
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
        file.createNewFile();
        bw = new BufferedWriter(new FileWriter(file));
        bw.write(json);
        bw.close();
        logger.log(Level.INFO, DEBUG_FILE_WRITE_SUCCESS + json);
    }

    private String jsonify(TaskState taskState) {
    	Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls()
    	        .setDateFormat(DateFormat.FULL, DateFormat.FULL).create();
    	String json = gson.toJson(taskState);

        return json;
    }

    // ================================================================================
    // FileHandler methods
    // ================================================================================

    /**
     * Makes a empty state when a file is first initialised so that the json
     * file has the right structure
     */
    private void makeEmptyState() throws IOException {
        saveTaskState(new TaskState());
    }

    // uses file extension to check for filename within a path
    private boolean hasFileName(String directoryPath) {
        String pattern = ".*\\" + fileExtension;
        return directoryPath.matches(pattern);
    }

}