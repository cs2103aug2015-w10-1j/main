package procrastinate;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.FileWriter;
import java.io.BufferedWriter;
import com.google.gson.*;

import procrastinate.task.Task;
import procrastinate.task.TaskState;

public class FileHandler {

    private static final Logger logger = Logger.getLogger(FileHandler.class.getName());

    // ================================================================================
    // Message strings
    // ================================================================================

    private static final String DEBUG_FILE_INIT = "FileHandler initialised. Using file ";
    private static final String DEBUG_FILE_WRITE_SUCCESS = "Wrote to file:\n";
    private static final String DEBUG_FILE_WRITE_FAILURE = "Could not write to file:\n";

    private static final String FILE_NAME = "storage.txt";

    // ================================================================================
    // Class variables
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
		file = new File(this.directoryPath + FILE_NAME);
        logger.log(Level.INFO, DEBUG_FILE_INIT + file.getAbsolutePath());
    }

    // ================================================================================
    // FileHandler methods
    // ================================================================================

    /**
     * Converts TaskState into json format and writes to disk
     * @param taskState
     */
    public void saveTaskState(TaskState taskState) {
    	String json = jsonify(taskState);
        try {
			jsonToFile(json);
		} catch (IOException e) {
			logger.log(Level.WARNING, DEBUG_FILE_WRITE_FAILURE + json);
			e.printStackTrace();
		}
    }

    public TaskState loadTaskState() {
        return new TaskState(new ArrayList<Task>());
    }

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
