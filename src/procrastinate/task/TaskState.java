package procrastinate.task;

import java.util.ArrayList;
import java.util.List;

public class TaskState {

    public List<Task> tasks;

    public TaskState(List<Task> tasks) {
        this.tasks = tasks;
    }

    public static TaskState copy(TaskState state) {
        return new TaskState(new ArrayList<Task>(state.tasks));
    }

}
