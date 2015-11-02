package procrastinate.test;

import java.io.IOException;

import procrastinate.task.TaskEngine;

public class TaskEngineUnit extends TaskEngine {
    public TaskEngineUnit() throws IOException {
    }
    @Override
    protected void initFileHandler() throws IOException {
        fileHandler = new FileHandlerStub();
    }
}
