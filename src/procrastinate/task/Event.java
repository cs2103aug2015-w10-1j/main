//@@author A0080485B
package procrastinate.task;

import java.util.Date;
import java.util.UUID;

public class Event extends Task {

	private Date startDate;
	private Date endDate;

	public Event(String description, Date startDate, Date endDate) {
		super(TaskType.EVENT, description);
        assert(endDate.compareTo(startDate) >= 0);
		this.startDate = startDate;
		this.endDate = endDate;
	}

	protected Event(String description, Date startDate, Date endDate, boolean done, UUID id) {
		super(TaskType.EVENT, description, done, id);
        assert(endDate.compareTo(startDate) >= 0);
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
    public boolean isWithin(Date startDate, Date endDate) {
        assert (endDate.compareTo(startDate) >= 0);
        return this.startDate.compareTo(startDate) >= 0 && this.startDate.compareTo(endDate) < 0
                || this.endDate.compareTo(startDate) >= 0 && this.endDate.compareTo(endDate) < 0;
    }

	@Override
    public int compareTo(Task other) {
        int superResult = super.compareTo(other);
        if (superResult != 0) {
            return superResult;
        }
	    if (other.getType() == TaskType.DREAM) {
	        return -1;
	    } else {
	        Date otherDate = null;
	        Date otherEndDate = null;
	        if (other.getType() == TaskType.DEADLINE) {
	            otherDate = ((Deadline) other).getDate();
	        } else {
	            otherDate = ((Event) other).getStartDate();
	            otherEndDate = ((Event) other).getEndDate();
	        }
	        if (!this.getStartDate().equals(otherDate)) {
                if (this.isDone()) {
                    return -1 * this.getStartDate().compareTo(otherDate); // flip order for done tasks
                } else {
                    return this.getStartDate().compareTo(otherDate);
                }
	        } else if (otherEndDate != null && !this.getEndDate().equals(otherEndDate)) {
	            return this.getEndDate().compareTo(otherEndDate);
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
