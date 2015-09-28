package procrastinate;

import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;

public class FileHandler {

    private static final String FILENAME = "storage.txt";

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
        	Utilities.printDebug("Directory is " + directoryPath);
            this.directoryPath = directoryPath;
        }
        file = new File(this.directoryPath + FILENAME);
    }

    /**
     * Write a line of string to file
     *
     * @param str
     * @return true if writing is successful, false otherwise
     *
     * @author Gerald
     */
    public boolean writeToFile(String str) {
    	try {
    		file.createNewFile();
			bw = new BufferedWriter(new FileWriter(file));
			bw.write(str + "\n");
			bw.flush();
			bw.close();
            Utilities.printDebug("Wrote to file: " + str);
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println(e);
		}
        Utilities.printDebug("Could not write to file: " + str);
    	return false;
    }

    public boolean saveTaskState(TaskState taskState) {
        return false;
    }

}
