package procrastinate;

import java.util.ArrayList;
import java.util.List;

import procrastinate.Task.TaskType;

public class TaskEngine {

    private FileHandler fileHandler;

    private List<Task> deadlines, events, dreams;

    public TaskEngine() {
        initLists();
        initFileHandler();
    }

    public void add(Task task) {
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
