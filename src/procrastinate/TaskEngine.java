package procrastinate;

import java.util.ArrayList;
import java.util.List;

import procrastinate.Task.TaskType;

public class TaskEngine {

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
        Utilities.printDebug(DEBUG_TASK_ENGINE_INIT);
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
                Utilities.printDebug("Added deadline: " + description);
                break;

            case EVENT:
                Utilities.printDebug("Added event: " + description);
                break;

            case DREAM:
                Utilities.printDebug("Added dream: " + description);
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
