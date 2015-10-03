package procrastinate.task;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import procrastinate.FileHandler;

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

    private static final String ERROR_TASK_NOT_FOUND = "Task not found!";

    // ================================================================================
    // Class variables
    // ================================================================================

    private List<Task> tasks;

    private TaskState previousState = null;

    private FileHandler fileHandler;

    public TaskEngine() {
        initTasks();
        initFileHandler();
        logger.log(Level.INFO, DEBUG_TASK_ENGINE_INIT);
    }

    // ================================================================================
    // TaskEngine methods
    // ================================================================================

    public void add(Task task) {
        backupOlderState();

        String description = task.getDescription();
        String type = task.getTypeString();

        tasks.add(task);

        logger.log(Level.INFO, String.format(DEBUG_ADDED_TASK, type, description));

        writeStateToFile();

    }

    public void edit(UUID taskId, Task newTask) {
        backupOlderState();

        int index = getIndexFromId(taskId);
        tasks.remove(index);
        tasks.add(index, newTask);

        logger.log(Level.INFO, String.format(DEBUG_EDITED_TASK, index + 1, newTask.getDescription()));

        writeStateToFile();

    }

    public void delete(UUID taskId) {
        backupOlderState();

        int index = getIndexFromId(taskId);
        Task task = tasks.get(index);
        tasks.remove(index);

        String description = task.getDescription();
        String type = task.getTypeString();

        logger.log(Level.INFO, String.format(DEBUG_DELETED_TASK, type, description));

        writeStateToFile();

    }

    public void done(UUID taskId) {
        backupOlderState();

        int index = getIndexFromId(taskId);
        Task task = tasks.get(index);
        task.setDone();

        String description = task.getDescription();
        String type = task.getTypeString();

        logger.log(Level.INFO, String.format(DEBUG_DONE_TASK, type, description));

        writeStateToFile();

    }

    public void undo() {
        if (hasPreviousOperation()) {
            TaskState backupNewerState = getBackupOfCurrentState();
            loadState(previousState);
            previousState = backupNewerState;
            writeStateToFile();
        }
    }

    public boolean hasPreviousOperation() {
        return previousState != null;
    }

    public List<Task> getOutstandingTasks() {
        List<Task> outstandingTasks = new ArrayList<Task>();
        for (Task task : tasks) {
            if (!task.isDone()) {
                outstandingTasks.add(task);
            }
        }
        return outstandingTasks;
    }

    public List<Task> getCompletedTasks() {
        List<Task> completedTasks = new ArrayList<Task>();
        for (Task task : tasks) {
            if (task.isDone()) {
                completedTasks.add(task);
            }
        }
        return completedTasks;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    // ================================================================================
    // Init methods
    // ================================================================================

    private void initTasks() {
        tasks = new ArrayList<Task>();
        loadState(new TaskStateStub()); // Load example data from stub
    }

    private void initFileHandler() {
        fileHandler = new FileHandler();
    }

    // ================================================================================
    // State handling methods
    // ================================================================================

    private void backupOlderState() {
        previousState = getBackupOfCurrentState();
    }

    private void loadState(TaskState state) {
        tasks = state.tasks;
    }

    private void writeStateToFile() {
        fileHandler.saveTaskState(getCurrentState());
    }

    private TaskState getBackupOfCurrentState() {
        return TaskState.copy(getCurrentState());
    }

    private TaskState getCurrentState() {
        return new TaskState(tasks);
    }

    // ================================================================================
    // Utility methods
    // ================================================================================

    private int getIndexFromId(UUID id) {
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getId().equals(id)) {
                return i;
            }
        }
        throw new Error(ERROR_TASK_NOT_FOUND);
    }

}
