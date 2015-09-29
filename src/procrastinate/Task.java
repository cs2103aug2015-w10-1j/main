package procrastinate;

public abstract class Task {
	public static enum TaskType {
		DEADLINE, EVENT, DREAM;
	}
	
	private TaskType type;
	private String description;
	
	public Task(TaskType type, String description) {
		this.type = type;
		this.description = description;
	}
	
	// ================================================================================
    // Getter methods
    // ================================================================================
	
	public TaskType getType() {
		return this.type;
	}
	
	public String getDescription() {
		return this.description;
	}

	// ================================================================================
    // Setter methods
    // ================================================================================
	
	public void setType(TaskType type) {
		this.type = type;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
}

/*
 For reference
import java.util.Date;

public class Task {

    public static enum TaskType {
        DEADLINE, EVENT, DREAM;
    }

    // Required field for all task types
    private TaskType type;
    private String description;

    // Optional fields; availability depends on task type
    private Date date;
    private Date endDate;

    // Dream constructor
    public Task(String description) {
        this.type = TaskType.DREAM;
        this.description = description;
    }

    // Deadline constructor
    public Task(String description, Date date) {
        this.type = TaskType.DEADLINE;
        this.description = description;
        this.date = date;
    }

    // Event constructor
    public Task(String description, Date startDate, Date endDate) {
        this.type = TaskType.EVENT;
        this.description = description;
        this.date = startDate;
        this.endDate = endDate;
    }

    // ================================================================================
    // Setter methods
    // ================================================================================

    public void setType(TaskType type) {
        this.type = type;

        switch (type) {

            case DEADLINE:
                endDate = null;
                assert(date != null);
                break;

            case DREAM:
                date = null;
                endDate = null;
                break;

            case EVENT:
                assert(date != null && endDate != null);
                break;

        }

    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setStartDate(Date startDate) {
        this.date = startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    // ================================================================================
    // Getter methods
    // ================================================================================

    public TaskType getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public Date getDate() {
        return date;
    }

    public Date getStartDate() {
        return date;
    }

    public Date getEndDate() {
        return endDate;
    }
}
//*/