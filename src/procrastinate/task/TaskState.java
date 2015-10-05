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

    public int getTaskSize() {
    	return tasks.size();
    }

    /**
     * Checks if every task in the list is equal.
     * Equality is defined in Task class
     */
    @Override
    public boolean equals(Object o) {
    	if (o == null) {
    		return false;
    	}
    	if (o == this) {
    		return true;
    	}
    	if (!(o instanceof TaskState)) {
    		return false;
    	}

    	TaskState ts = (TaskState)o;
    	List<Task> t = ts.tasks;

    	if (this.getTaskSize() != ts.getTaskSize()) {
    		return false;
    	}

    	return tasks.equals(t);
    }
}
