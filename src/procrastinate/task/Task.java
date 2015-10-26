package procrastinate.task;

import java.util.Date;
import java.util.UUID;

public abstract class Task implements Comparable<Task> {

    private static final String ERROR_UNKNOWN_TYPE = "Error: unknown task type";

	public static enum TaskType {
		DEADLINE, EVENT, DREAM;
	}

	private final TaskType type; // Cannot change type; subclass object already created
    private UUID id;
	private String description;
	private boolean done;

	public Task(TaskType type, String description) {
	    this(type, description, false, UUID.randomUUID());
	}

	protected Task(TaskType type, String description, boolean done, UUID id) {
        this.type = type;
        this.description = description;
        this.done = done;
        this.id = id;
    }

	public static Task copy(Task task) {
	    switch (task.getType()) {
	        case DEADLINE: {
	            Deadline other = (Deadline) task;
                return new Deadline(other.getDescription(), other.getDate(), other.isDone(), other.getId());
	        }
	        case EVENT: {
	            Event other = (Event) task;
                return new Event(other.getDescription(), other.getStartDate(), other.getEndDate(), other.isDone(), other.getId());
	        }
	        case DREAM: {
	            Dream other = (Dream) task;
	            return new Dream(other.getDescription(), other.isDone(), other.getId());
	        }
	        default: {
	            throw new Error(ERROR_UNKNOWN_TYPE);
	        }
	    }
	}

	public boolean contains(String term) {
	    return description.contains(term);
	}

	public abstract boolean isWithin(Date startDate, Date endDate);

	// ================================================================================
    // Getter methods
    // ================================================================================

	public TaskType getType() {
		return type;
	}

    public String getTypeString() {
        return type.toString().toLowerCase();
    }

	public String getDescription() {
		return description;
	}

	public UUID getId() {
	    return id;
	}

	public boolean isDone() {
	    return done;
	}

	// ================================================================================
    // Setter methods
    // ================================================================================

	public void setDescription(String description) {
		this.description = description;
	}

	public void setDone() {
	    done = true;
	}

	public void clearDone() {
	    done = false;
	}

    @Override
    public int compareTo(Task other) {
        if (this.isDone() == other.isDone()) {
            return 0;
        } else {
            if (this.isDone()) {
                return 1;
            } else {
                return -1;
            }
        }
    }

	/**
	 * For two tasks to be indentical, they must be the same type
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
		if (t.getType() == this.getType()
				&& t.getDescription().equals(this.getDescription())
				&& t.isDone() == this.isDone()) {
			return true;
		} else {
			return false;
		}
	}

    @Override
    public String toString() {
        return getDescription();
    }

}
