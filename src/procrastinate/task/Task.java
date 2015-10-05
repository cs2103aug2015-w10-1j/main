package procrastinate.task;

import java.util.UUID;

public abstract class Task {

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

	/**
	 * For two tasks to be indentical, they must be the same type
	 * and have the same description
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
		Task t = (Task)o;
		if (t.getType().toString().equals(this.getType().toString())
				&& t.getDescription().equals(this.getDescription())) {
			return true;
		} else {
			return false;
		}
	}

}
