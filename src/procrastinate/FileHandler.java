package procrastinate;

import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;

public class FileHandler {
    private String dir = "./storage.txt";
    private File file;
    private BufferedWriter bw = null;
    
    public FileHandler() {
    	this("");
    }
    
    public FileHandler(String dir) {
        if (dir.length() > 0) {
        	System.out.println("dir is " + dir);
            this.dir = dir;
        }
        file = new File(this.dir);
    }
    
    /**
     * Write a line of string to file
     * 
     * @param str
     * @return true is writing is successful, false otherwise
     * 
     * @author Gerald
     */
    public Boolean writeToFile(String str) {
    	try {
    		file.createNewFile();
			bw = new BufferedWriter(new FileWriter(file));
			bw.write(str + "\n");
			bw.flush();
			bw.close();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println(e);
		}
    	return false;
    }
}
