package procrastinate.test;

import java.io.File;

import procrastinate.FileHandler;
import procrastinate.task.TaskState;

public class FileHandlerStub extends FileHandler {
    private File saveFile;
    public FileHandlerStub() {
        super(true);
        setPath("/default/", "storage.json");
    }
    @Override
    public boolean saveTaskState(TaskState taskState) {
        return true;
    }
    @Override
    public TaskState loadTaskState() {
        return new TaskState();
    }
    @Override
    public boolean setPath(String dir, String filename) {
        saveFile = new File(dir + filename);
        return true;
    }
    @Override
    public String getFilename() {
        return saveFile.getName();
    }
    @Override
    public File getSaveFile() {
        return saveFile;
    }
}
