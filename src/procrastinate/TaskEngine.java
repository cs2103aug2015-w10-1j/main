package procrastinate;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import procrastinate.Task.TaskType;

public class TaskEngine {

    private static final Logger logger = Logger.getLogger(TaskEngine.class.getName());

    // ================================================================================
    // Message strings
    // ================================================================================

    private static final String DEBUG_TASK_ENGINE_INIT = "TaskEngine initialised.";

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
                logger.log(Level.INFO, "Added deadline: " + description);
                break;

            case EVENT:
                logger.log(Level.INFO, "Added event: " + description);
                break;

            case DREAM:
                logger.log(Level.INFO, "Added dream: " + description);
                break;

        }

        fileHandler.writeToFile(description);
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
    // Init methods
    // ================================================================================

    private void initLists() {
        outstandingTasks = new ArrayList<Task>();
        completedTasks = new ArrayList<Task>();
    }

    private void initFileHandler() {
        fileHandler = new FileHandler();
    }

}
