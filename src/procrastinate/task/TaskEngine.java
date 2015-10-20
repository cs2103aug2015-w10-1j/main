package procrastinate.task;

import procrastinate.FileHandler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TaskEngine {

    private static final Logger logger = Logger.getLogger(TaskEngine.class.getName());

    // ================================================================================
    // Message strings
    // ================================================================================

    private static final String DEBUG_TASK_ENGINE_INIT = "TaskEngine initialised.";
    private static final String DEBUG_ADDED_TASK = "Added %1$s: %2$s";
    private static final String DEBUG_EDITED_TASK = "Edited #%1$s: %2$s";
    private static final String DEBUG_DELETED_TASK = "Deleted %1$s: %2$s";
    private static final String DEBUG_DONE_TASK = "Done %1$s: %2$s";
    private static final String DEBUG_UNDONE = "Last task operation undone";
    private static final String DEBUG_FILE_NOT_FOUND = "No data file found; creating...";
    private static final String DEBUG_FILE_WRITE_FAILURE = "Could not write to file";

    private static final String ERROR_TASK_NOT_FOUND = "Task not found!";

    // ================================================================================
    // Class variables
    // ================================================================================

    private TaskState previousState = null;
    private TaskState currentState = null;
    private TaskState currentView = null;

    private FileHandler fileHandler;

    private String directoryPath = "";

    public TaskEngine() throws IOException {
        this("");
    }

    public TaskEngine(String directoryPath) throws IOException {
        this.directoryPath = directoryPath;
        initFileHandler();
        initTasks();
        logger.log(Level.INFO, DEBUG_TASK_ENGINE_INIT);
    }

    // ================================================================================
    // TaskEngine methods
    // ================================================================================

    public void add(Task task) throws IOException {
        backupOlderState();

        String description = task.getDescription();
        String type = task.getTypeString();

        getTasks().add(task);

        logger.log(Level.INFO, String.format(DEBUG_ADDED_TASK, type, description));

        writeStateToFile();

    }

    public void edit(UUID taskId, Task newTask) throws IOException {
        backupOlderState();

        int index = getIndexFromId(taskId);
        getTasks().remove(index);
        getTasks().add(index, newTask);

        logger.log(Level.INFO, String.format(DEBUG_EDITED_TASK, index + 1, newTask.getDescription()));

        writeStateToFile();

    }

    public void delete(UUID taskId) throws IOException {
        backupOlderState();

        int index = getIndexFromId(taskId);
        Task task = getTasks().get(index);
        getTasks().remove(index);

        String description = task.getDescription();
        String type = task.getTypeString();

        logger.log(Level.INFO, String.format(DEBUG_DELETED_TASK, type, description));

        writeStateToFile();

    }

    public void done(UUID taskId) throws IOException {
        backupOlderState();

        int index = getIndexFromId(taskId);
        Task task = getTasks().get(index);
        task.setDone();

        String description = task.getDescription();
        String type = task.getTypeString();

        logger.log(Level.INFO, String.format(DEBUG_DONE_TASK, type, description));

        writeStateToFile();

    }

    public void undo() throws IOException {
        if (hasPreviousOperation()) {
            TaskState backupNewerState = getBackupOfCurrentState();
            restoreOlderState();
            previousState = backupNewerState;

            logger.log(Level.INFO, String.format(DEBUG_UNDONE));

            writeStateToFile();
        }
    }

    public boolean hasPreviousOperation() {
        return previousState != null;
    }

    public List<Task> getTasksContaining(String description) {
        List<Task> results = new ArrayList<Task>();
        for (Task task : getTasks()) {
            if (task.contains(description)) {
                results.add(task);
            }
        }
        currentView = new TaskState(results);
        return results;
    }

    public List<Task> getOutstandingTasks() {
        List<Task> outstandingTasks = new ArrayList<Task>();
        for (Task task : getTasks()) {
            if (!task.isDone()) {
                outstandingTasks.add(task);
            }
        }
        currentView = new TaskState(outstandingTasks);
        return outstandingTasks;
    }

    public List<Task> getCompletedTasks() {
        List<Task> completedTasks = new ArrayList<Task>();
        for (Task task : getTasks()) {
            if (task.isDone()) {
                completedTasks.add(task);
            }
        }
        currentView = new TaskState(completedTasks);
        return completedTasks;
    }

    public List<Task> getAllTasks() {
        currentView = currentState;
        return currentState.getTasks();
    }

    public List<Task> getCurrentTaskList() {
        return currentView.getTasks();
    }

    // ================================================================================
    // Init methods
    // ================================================================================

    private void initFileHandler() throws IOException {
        fileHandler = new FileHandler(directoryPath);
    }

    private void initTasks() {
        try {
            loadState(fileHandler.loadTaskState());
        } catch (FileNotFoundException e) {
            loadState(new TaskState());
            logger.log(Level.INFO, DEBUG_FILE_NOT_FOUND);
            try {
                writeStateToFile();
            } catch (IOException e1) {
                logger.log(Level.SEVERE, DEBUG_FILE_WRITE_FAILURE);
            }
        } finally {
            currentView = currentState;
        }
    }

    // ================================================================================
    // State handling methods
    // ================================================================================

    private void backupOlderState() {
        previousState = getBackupOfCurrentState();
    }

    private void restoreOlderState() {
        loadState(previousState);
    }

    private void loadState(TaskState state) {
        currentState = state;
    }

    private void writeStateToFile() throws IOException {
        fileHandler.saveTaskState(getCurrentState());
    }

    private TaskState getBackupOfCurrentState() {
        return TaskState.copy(getCurrentState());
    }

    private TaskState getCurrentState() {
        Collections.sort(currentState.getTasks());
        return currentState;
    }

    // ================================================================================
    // Utility methods
    // ================================================================================

    private int getIndexFromId(UUID id) {
        for (int i = 0; i < getTasks().size(); i++) {
            if (getTasks().get(i).getId().equals(id)) {
                return i;
            }
        }
        throw new Error(ERROR_TASK_NOT_FOUND);
    }

    private List<Task> getTasks() {
        return currentState.getTasks();
    }

}