package procrastinate;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import procrastinate.task.Task;
import procrastinate.task.TaskDeserializer;
import procrastinate.task.TaskState;

import java.io.*;
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


    // ================================================================================
    // Instance variables
    // ================================================================================

    private final String filename = "storage";
    private final String fileExtension = ".json";
    private final String fullFilename = filename + fileExtension;
    private String filePath = "";
    private File file;
    private BufferedWriter bw = null;

    public FileHandler() throws IOException {
    	this("");
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
    		Files.createFile(pathWithFileName);
    	}

    	file = pathWithFileName.toFile();

    	logger.log(Level.INFO, DEBUG_FILE_INIT + file.getCanonicalPath());
    }

	// ================================================================================
    // FileHandler methods
    // ================================================================================


	/**
     * Converts TaskState into json format and writes to disk
     * @param taskState
     */
    public void saveTaskState(TaskState taskState) throws IOException {
    	String json = jsonify(taskState);
        try {
			jsonToFile(json);
		} catch (IOException e) {
			logger.log(Level.WARNING, DEBUG_FILE_WRITE_FAILURE);
			throw e;
		}
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
    	GsonBuilder builder = new GsonBuilder().setPrettyPrinting().serializeNulls();
    	Gson gson = builder.create();
    	String json = gson.toJson(taskState);

    	return json;
    }

    // uses file extension to check for filename within a path
	private boolean hasFileName(String directoryPath) {
		String pattern = ".*\\" + fileExtension;
		return directoryPath.matches(pattern);
	}

}
