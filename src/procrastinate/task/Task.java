//@@author A0080485B
package procrastinate.task;

import java.util.Date;
import java.util.UUID;

import com.google.gson.annotations.SerializedName;

public abstract class Task implements Comparable<Task> {

    protected static final String FIELD_TYPE = "type";
    protected static final String FIELD_ID = "id";
    protected static final String FIELD_DESCRIPTION = "description";
    protected static final String FIELD_DONE = "done";

    private static final String ERROR_UNKNOWN_TYPE = "Error: unknown task type";

	public static enum TaskType {
		DEADLINE, EVENT, DREAM;
	}

	@SerializedName(FIELD_TYPE)
	private final TaskType type_; // Cannot change type; subclass object already created
    @SerializedName(FIELD_ID)
    private UUID id_;
    @SerializedName(FIELD_DESCRIPTION)
	private String description_;
    @SerializedName(FIELD_DONE)
	private boolean isDone_;

	//@@author A0124321Y
	public Task(TaskType type, String description) {
	    this(type, description, false, UUID.randomUUID());
	}
	//@@author

	protected Task(TaskType type, String description, boolean isDone, UUID id) {
        this.type_ = type;
        this.description_ = description;
        this.isDone_ = isDone;
        this.id_ = id;
    }

	public static Task copy(Task task) {
	    switch (task.getType()) {
	        case DEADLINE : {
	            Deadline other = (Deadline) task;
                return new Deadline(other.getDescription(), other.getDate(), other.isDone(), other.getId());
	        }
	        case EVENT : {
	            Event other = (Event) task;
                return new Event(other.getDescription(), other.getStartDate(), other.getEndDate(),
                        other.isDone(), other.getId());
	        }
	        case DREAM : {
	            Dream other = (Dream) task;
	            return new Dream(other.getDescription(), other.isDone(), other.getId());
	        }
	        default : {
	            throw new Error(ERROR_UNKNOWN_TYPE);
	        }
	    }
	}

    public static Task copyWithNewId(Task task) {
        Task newTask = copy(task);
        newTask.id_ = UUID.randomUUID();
        return newTask;
    }

	public boolean contains(String term) { // case insensitive
	    return description_.toLowerCase().contains(term.toLowerCase());
	}

	public abstract boolean isWithin(Date startDate, Date endDate);

	// ================================================================================
    // Getter methods
    // ================================================================================

	public TaskType getType() {
		return type_;
	}

    public String getTypeString() {
        return type_.toString().toLowerCase();
    }

	public String getDescription() {
		return description_;
	}

	public UUID getId() {
	    return id_;
	}

	public boolean isDone() {
	    return isDone_;
	}

	// ================================================================================
    // Setter methods
    // ================================================================================

	public void setDescription(String description) {
		description_ = description;
	}

	public void setDone(boolean isDone) {
	    isDone_ = isDone;
	}

    @Override
    public int compareTo(Task other) {
        if (isDone_ == other.isDone_) {
            return 0;
        } else {
            if (isDone_) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    //@@author A0124321Y
	/**
	 * For two tasks to be identical, they must be the same type
	 * and have the same description and done status
	 */
	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (o == this) {
			return true;
		}
		if (!(o instanceof Task)) {
			return false;
		}
		Task t = (Task) o;
		if (t.type_ == type_
				&& t.description_.equals(description_)
				&& t.isDone_ == isDone_) {
			return true;
		} else {
			return false;
		}
	}
   //@@author

    @Override
    public String toString() {
        return getDescription();
    }

}
