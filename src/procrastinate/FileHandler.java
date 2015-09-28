package procrastinate;

import java.io.File;
import java.io.PrintWriter;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;

public class FileHandler {
    private String dir = ".";
    private File file;
    private BufferedWriter bw = null;
    
    public FileHandler(String dir) {
        if (dir != null) {
            this.dir = dir;
        }
        File file = new File(dir);
        
        try {
            bw = new BufferedWriter(new PrintWriter(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
