//@@author A0080485B
package procrastinate.task;

import java.util.ArrayList;
import java.util.List;

public class TaskState {

    private List<Task> tasks;

    public TaskState() {
        this.tasks = new ArrayList<Task>();
    }

    public TaskState(List<Task> tasks) {
        this.tasks = tasks;
    }

    public static TaskState copy(TaskState state) {
        return new TaskState(new ArrayList<Task>(state.tasks));
    }

    public List<Task> getTasks() {
        return tasks;
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

    	if (this.tasks.size() != ts.tasks.size()) {
    		return false;
    	}

    	return tasks.equals(t);
    }
}
