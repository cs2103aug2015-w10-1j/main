//@@author A0080485B
package procrastinate.task;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class TaskState {

    protected static final String FIELD_TASKS = "tasks";

    @SerializedName(FIELD_TASKS)
    private List<Task> tasks_;

    public TaskState() {
        this.tasks_ = new ArrayList<Task>();
    }

    public TaskState(List<Task> tasks) {
        this.tasks_ = tasks;
    }

    public static TaskState copy(TaskState other) {
        return new TaskState(new ArrayList<Task>(other.tasks_));
    }

    public List<Task> getTasks() {
        return tasks_;
    }

    //@@author A0124321Y
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
    	List<Task> t = ts.tasks_;

    	if (this.tasks_.size() != ts.tasks_.size()) {
    		return false;
    	}

    	return tasks_.equals(t);
    }
}
