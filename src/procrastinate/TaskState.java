package procrastinate;

import java.util.List;

public class TaskState {

    public List<Task> deadlines, events, dreams;

    public TaskState(List<Task> deadlines, List<Task> events, List<Task> dreams) {
        this.deadlines = deadlines;
        this.events = events;
        this.dreams = dreams;
    }

}
