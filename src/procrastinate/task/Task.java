package procrastinate.task;

import java.util.UUID;

public abstract class Task {

	public static enum TaskType {
		DEADLINE, EVENT, DREAM;
	}

    private final UUID id = UUID.randomUUID();
	private final TaskType type; // Cannot change type; subclass object already created
	private String description;
	private boolean done;

	public Task(TaskType type, String description) {
	    this(type, description, false);
	}

	public Task(TaskType type, String description, boolean done) {
        this.type = type;
        this.description = description;
        this.done = done;
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

}
