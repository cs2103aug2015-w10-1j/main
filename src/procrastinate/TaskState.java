package procrastinate;

import java.util.ArrayList;
import java.util.List;

public class TaskState {

    public List<Task> outstandingTasks, completedTasks;

    public TaskState(List<Task> outstandingTasks, List<Task> completedTasks) {
        this.outstandingTasks = outstandingTasks;
        this.completedTasks = completedTasks;
    }

    public static TaskState copy(TaskState state) {
        return new TaskState(new ArrayList<Task>(state.outstandingTasks),
                             new ArrayList<Task>(state.completedTasks));
    }

}
