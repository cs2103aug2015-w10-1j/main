package procrastinate;

import java.util.ArrayList;
import java.util.List;

import procrastinate.Task.TaskType;

public class TaskEngine {

    private FileHandler fileHandler;

    private List<Task> deadlines, events, dreams;

    private TaskState previousState;

    public TaskEngine() {
        initLists();
        initFileHandler();
    }

    public void add(Task task) {
        previousState = getCurrentState();

        TaskType type = task.getType();
        String description = task.getDescription();

        switch (type) {

            case DEADLINE:
                Utilities.printDebug("Added deadline: " + description);
                deadlines.add(task);
                break;

            case EVENT:
                Utilities.printDebug("Added event: " + description);
                events.add(task);
                break;

            case DREAM:
                Utilities.printDebug("Added dream: " + description);
                dreams.add(task);
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

    private TaskState getCurrentState() {
        return new TaskState(deadlines, events, dreams);
    }

    private void loadState(TaskState state) {
        this.deadlines = state.deadlines;
        this.events = state.events;
        this.dreams = state.dreams;
    }

    // ================================================================================
    // Init methods
    // ================================================================================

    private void initLists() {
        deadlines = new ArrayList<Task>();
        events = new ArrayList<Task>();
        dreams = new ArrayList<Task>();
    }

    private void initFileHandler() {
        fileHandler = new FileHandler();
    }

}
