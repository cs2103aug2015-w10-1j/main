package procrastinate.task;

import java.util.Date;
import java.util.UUID;

public class Event extends Task {

	private Date startDate;
	private Date endDate;

	public Event(String description, Date startDate, Date endDate) {
		super(TaskType.EVENT, description);
		this.startDate = startDate;
		this.endDate = endDate;
	}

	protected Event(String description, Date startDate, Date endDate, boolean done, UUID id) {
		super(TaskType.EVENT, description, done, id);
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public Date getStartDate() {
		return startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	@Override
    public int compareTo(Task other) {
	    if (other.getType() == TaskType.DREAM) {
	        return -1;
	    } else {
	        Date otherDate = null;
	        if (other.getType() == TaskType.DEADLINE) {
	            otherDate = ((Deadline) other).getDate();
	        } else {
	            otherDate = ((Event) other).getStartDate();
	        }
	        if (!this.getStartDate().equals(otherDate)) {
	            return this.getStartDate().compareTo(otherDate);
	        } else {
	            return this.getDescription().compareTo(other.getDescription());
	        }
	    }
	}

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (!(o instanceof Event)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        Event t = (Event) o;
        return t.getStartDate().equals(this.getStartDate()) && t.getEndDate().equals(this.getEndDate());
    }
}
