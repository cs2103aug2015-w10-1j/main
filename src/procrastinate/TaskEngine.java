package procrastinate;

import java.util.ArrayList;
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

    private static final String ERROR_TASK_NOT_FOUND = "Task not found!";

    private FileHandler fileHandler;

    private List<Task> outstandingTasks, completedTasks;

    private TaskState previousState;

    public TaskEngine() {
        initLists();
        initFileHandler();
        logger.log(Level.INFO, DEBUG_TASK_ENGINE_INIT);
    }

    // ================================================================================
    // Public CRUD methods
    // ================================================================================

    public void add(Task task) {
        saveState();

        String description = task.getDescription();
        String type = task.getTypeString();

        outstandingTasks.add(task);

        logger.log(Level.INFO, String.format(DEBUG_ADDED_TASK, type, description));

        writeStateToFile();

    }

    public void edit(UUID taskId, Task newTask) {
        saveState();

        int index = getIndexFromId(taskId);
        if (index == -1) {
            throw new Error(ERROR_TASK_NOT_FOUND);
        }

        if (index < outstandingTasks.size()) {
            outstandingTasks.remove(index);
            outstandingTasks.add(index, newTask);
        } else {
            index -= outstandingTasks.size();
            completedTasks.remove(index);
            completedTasks.add(index, newTask);
        }

        logger.log(Level.INFO, String.format(DEBUG_EDITED_TASK, index + 1, newTask.getDescription()));

        writeStateToFile();

    }

    public void delete(UUID taskId) {
        saveState();

        int index = getIndexFromId(taskId);
        if (index == -1) {
            throw new Error(ERROR_TASK_NOT_FOUND);
        }

        Task task;
        if (index < outstandingTasks.size()) {
            task = outstandingTasks.get(index);
            outstandingTasks.remove(index);
        } else {
            index -= outstandingTasks.size();
            task = completedTasks.get(index);
            completedTasks.remove(index);
        }

        String description = task.getDescription();
        String type = task.getTypeString();

        logger.log(Level.INFO, String.format(DEBUG_DELETED_TASK, type, description));

        writeStateToFile();

    }

    public void undo() {
        if (hasPreviousOperation()) {
            TaskState backupNewerState = getBackupOfCurrentState();
            loadState(previousState);
            previousState = backupNewerState;
        }
    }

    public boolean hasPreviousOperation() {
        return previousState != null;
    }

    public List<Task> getOutstandingTasks() {
        return outstandingTasks;
    }

    public List<Task> getCompletedTasks() {
        return completedTasks;
    }

    // ================================================================================
    // Init methods
    // ================================================================================

    private void initLists() {
        outstandingTasks = new ArrayList<Task>();
        completedTasks = new ArrayList<Task>();
    }

    private void initFileHandler() {
        fileHandler = new FileHandler();
    }

    // ================================================================================
    // State handling methods
    // ================================================================================

    private void writeStateToFile() {
        fileHandler.saveTaskState(getCurrentState());
    }

    private void saveState() {
        previousState = getBackupOfCurrentState();
    }

    private void loadState(TaskState state) {
        this.outstandingTasks = state.outstandingTasks;
        this.completedTasks = state.completedTasks;
    }

    private TaskState getBackupOfCurrentState() {
        return TaskState.copy(getCurrentState());
    }

    private TaskState getCurrentState() {
        return new TaskState(outstandingTasks, completedTasks);
    }

    // ================================================================================
    // Utility methods
    // ================================================================================

    private int getIndexFromId(UUID id) {
        for (int i = 0; i < outstandingTasks.size(); i++) {
            if (outstandingTasks.get(i).getId().equals(id)) {
                return i;
            }
        }
        for (int i = 0; i < completedTasks.size(); i++) {
            if (completedTasks.get(i).getId().equals(id)) {
                return i + outstandingTasks.size();
            }
        }
        return -1;
    }

}
