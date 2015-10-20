package procrastinate.task;

import java.util.Date;
import java.util.UUID;

public class Deadline extends Task {

	private Date date;

	public Deadline(String description, Date date) {
		super(TaskType.DEADLINE, description);
		this.date = date;
	}

	protected Deadline(String description, Date date, boolean done, UUID id) {
		super(TaskType.DEADLINE, description, done, id);
		this.date = date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Date getDate() {
		return date;
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
            if (!this.getDate().equals(otherDate)) {
                return this.getDate().compareTo(otherDate);
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
        if (!(o instanceof Deadline)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        Deadline t = (Deadline) o;
        return t.getDate().equals(this.getDate());
    }
}
