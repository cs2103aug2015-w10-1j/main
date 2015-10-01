package procrastinate;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.FileWriter;
import java.io.BufferedWriter;
import com.google.gson.*;

public class FileHandler {

    private static final Logger logger = Logger.getLogger(FileHandler.class.getName());

    private static final String FILE_NAME = "storage.txt";

    private String directoryPath = "";
    private File file;
    private BufferedWriter bw = null;

    public FileHandler() {
    	this("");
    }

    public FileHandler(String directoryPath) {
        if (directoryPath.length() > 0) {
            if (!directoryPath.endsWith("/")) {
                directoryPath += "/";
            }
            this.directoryPath = directoryPath;
        }
		file = new File(this.directoryPath + FILE_NAME);
        logger.log(Level.INFO, "FileHandler initialised. Using file " + file.getAbsolutePath());
    }

    /**
     * Converts TaskState into json format and writes to disk
     * @param taskState
     */
    public void saveTaskState(TaskState taskState) {
    	String json = jsonify(taskState);
        try {
			jsonToFile(json);
		} catch (IOException e) {
			logger.log(Level.WARNING, "Could not write to file: " + json);
			e.printStackTrace();
		}
    }

	private void jsonToFile(String json) throws IOException {
		file.createNewFile();
		bw = new BufferedWriter(new FileWriter(file));
		bw.write(json);
		bw.close();
		logger.log(Level.INFO, "Wrote to file:\n" + json);
	}

    private String jsonify(TaskState taskState) {
    	GsonBuilder builder = new GsonBuilder().setPrettyPrinting().serializeNulls();
    	Gson gson = builder.create();
    	String json = gson.toJson(taskState);

    	return json;
    }

}
