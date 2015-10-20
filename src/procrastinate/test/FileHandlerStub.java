package procrastinate.test;

import java.io.FileNotFoundException;
import procrastinate.FileHandler;
import procrastinate.task.TaskState;

public class FileHandlerStub extends FileHandler {
    public FileHandlerStub() {
        super(true);
    }
    public FileHandlerStub(String path) {
        this();
    }
    @Override
    public boolean saveTaskState(TaskState taskState) {
        return true;
    }
    @Override
    public TaskState loadTaskState() throws FileNotFoundException {
        return new TaskState();
    }
}
