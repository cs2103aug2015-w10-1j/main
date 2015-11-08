//@@author A0080485B
package procrastinate.task;

import java.util.Date;
import java.util.UUID;

import com.google.gson.annotations.SerializedName;

public class Deadline extends Task {

    protected static final String FIELD_DATE = "date";

    @SerializedName(FIELD_DATE)
	private Date date_;

	public Deadline(String description, Date date) {
		super(TaskType.DEADLINE, description);
		this.date_ = date;
	}

	protected Deadline(String description, Date date, boolean isDone, UUID id) {
		super(TaskType.DEADLINE, description, isDone, id);
		this.date_ = date;
	}

	public void setDate(Date date) {
		date_ = date;
	}

	public Date getDate() {
		return date_;
	}

    @Override
    public boolean isWithin(Date startDate, Date endDate) {
        assert (endDate.compareTo(startDate) >= 0);
        return date_.compareTo(startDate) >= 0 && date_.compareTo(endDate) < 0;
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
            if (other.getType() == TaskType.DEADLINE) {
                otherDate = ((Deadline) other).date_;
            } else {
                otherDate = ((Event) other).getStartDate();
            }
            if (!date_.equals(otherDate)) {
                if (isDone()) {
                    return -1 * date_.compareTo(otherDate); // flip order for done tasks
                } else {
                    return date_.compareTo(otherDate);
                }
            } else {
                return getDescription().compareTo(other.getDescription());
            }
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other == this) {
            return true;
        }
        if (!(other instanceof Deadline)) {
            return false;
        }
        if (!super.equals(other)) {
            return false;
        }
        Deadline otherDeadline = (Deadline) other;
        return otherDeadline.date_.equals(date_);
    }
}
