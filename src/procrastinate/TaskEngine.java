package procrastinate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import procrastinate.Task.TaskType;

public class TaskEngine {

    private static final Logger logger = Logger.getLogger(TaskEngine.class.getName());

    // ================================================================================
    // Message strings
    // ================================================================================

    private static final String DEBUG_TASK_ENGINE_INIT = "TaskEngine initialised.";
    private static final String DEBUG_ADDED_DEADLINE = "Added deadline: ";
    private static final String DEBUG_ADDED_EVENT = "Added event: ";
    private static final String DEBUG_ADDED_DREAM = "Added dream: ";
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
        previousState = getCurrentState();

        TaskType type = task.getType();
        String description = task.getDescription();

        outstandingTasks.add(task);

        switch (type) {

            case DEADLINE:
                logger.log(Level.INFO, DEBUG_ADDED_DEADLINE + description);
                break;

            case EVENT:
                logger.log(Level.INFO, DEBUG_ADDED_EVENT + description);
                break;

            case DREAM:
                logger.log(Level.INFO, DEBUG_ADDED_DREAM + description);
                break;

        }

        fileHandler.writeToFile(description);
        fileHandler.saveTaskState(getCurrentState());

    }

    public void delete(UUID taskId) {
        Task task = getTaskFromId(taskId);
        if (!outstandingTasks.remove(task) && !completedTasks.remove(task)) {
            throw new Error(ERROR_TASK_NOT_FOUND);
        }

        String description = task.getDescription();
        String type = task.getType().toString().toLowerCase();

        logger.log(Level.INFO, String.format(DEBUG_DELETED_TASK, type, description));

        fileHandler.saveTaskState(getCurrentState());
    }

    public void undo() {
        TaskState backupNewerState = getCurrentState();
        loadState(previousState);
        previousState = backupNewerState;
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

    private TaskState getCurrentState() {
        return new TaskState(outstandingTasks, completedTasks);
    }

    private void loadState(TaskState state) {
        this.outstandingTasks = state.outstandingTasks;
        this.completedTasks = state.completedTasks;
    }

    // ================================================================================
    // Utility methods
    // ================================================================================

    private Task getTaskFromId(UUID id) {
        for (Task task : outstandingTasks) {
            if (task.getId().equals(id)) {
                return task;
            }
        }
        for (Task task : completedTasks) {
            if (task.getId().equals(id)) {
                return task;
            }
        }
        return null;
    }

}
