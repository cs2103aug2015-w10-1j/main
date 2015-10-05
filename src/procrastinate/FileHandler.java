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
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import procrastinate.task.Task;
import procrastinate.task.TaskDeserializer;
import procrastinate.task.TaskState;

public class FileHandler {

    private static final Logger logger = Logger.getLogger(FileHandler.class.getName());

    // ================================================================================
    // Message strings
    // ================================================================================

    private static final String DEBUG_FILE_INIT = "FileHandler initialised. Using file ";
    private static final String DEBUG_FILE_WRITE_SUCCESS = "Wrote to file:\n";
    private static final String DEBUG_FILE_WRITE_FAILURE = "Could not write to file";
    private static final String DEBUG_FILE_LOAD_SUCCESS = "Loaded from file %1$s task(s)";
    private static final String DEBUG_FILE_LOAD_FAILURE = "Could not load from file";

    private final String FILENAME = "storage.json";

    // ================================================================================
    // Instance variables
    // ================================================================================

    private String directoryPath = "";
    private File file;
    private BufferedWriter bw = null;

    public FileHandler() {
    	this("");
    }

    public FileHandler(String directoryPath) {
    	if (!directoryPath.isEmpty()) {
    		if (!directoryPath.endsWith("/")) {
    			directoryPath += "/";
    		}
    		this.directoryPath = directoryPath;
    	}

    	file = new File(this.directoryPath + FILENAME);
    	logger.log(Level.INFO, DEBUG_FILE_INIT + file.getAbsolutePath());
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

			logger.log(Level.INFO, String.format(DEBUG_FILE_LOAD_SUCCESS, taskState.tasks.size()));
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

}
