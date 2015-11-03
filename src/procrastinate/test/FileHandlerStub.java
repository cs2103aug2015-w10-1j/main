package procrastinate.test;

import procrastinate.FileHandler;
import procrastinate.task.TaskState;

public class FileHandlerStub extends FileHandler {
    public FileHandlerStub() {
        super(true);
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
        return true;
    }
}
