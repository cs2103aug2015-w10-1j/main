package procrastinate;

import java.util.List;

public class TaskState {

    public List<Task> outstandingTasks, completedTasks;

    public TaskState(List<Task> outstandingTasks, List<Task> completedTasks) {
        this.outstandingTasks = outstandingTasks;
        this.completedTasks = completedTasks;
    }

}
