//@@author A0080485B
package procrastinate.task;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import com.google.gson.annotations.SerializedName;

public class Event extends Task {

    protected static final String FIELD_START_DATE = "startDate";
    protected static final String FIELD_END_DATE = "endDate";

    private static final String dateStringFormat = " from %1$s to %2$s";

    private static final DateFormat dateFormatter = new SimpleDateFormat("d/MM/yy h:mma");

    @SerializedName(FIELD_START_DATE)
	private Date startDate_;
    @SerializedName(FIELD_END_DATE)
	private Date endDate_;

	public Event(String description, Date startDate, Date endDate) {
		super(TaskType.EVENT, description);
        assert(endDate.compareTo(startDate) >= 0);
		this.startDate_ = startDate;
		this.endDate_ = endDate;
	}

	protected Event(String description, Date startDate, Date endDate, boolean isDone, UUID id) {
		super(TaskType.EVENT, description, isDone, id);
        assert(endDate.compareTo(startDate) >= 0);
		this.startDate_ = startDate;
		this.endDate_ = endDate;
	}

	public Date getStartDate() {
		return startDate_;
	}

	public Date getEndDate() {
		return endDate_;
	}

    @Override
    public String getDateString() {
        return String.format(dateStringFormat, dateFormatter.format(startDate_), dateFormatter.format(endDate_));
    }

    @Override
    public boolean isWithin(Date startDate, Date endDate) {
        assert (endDate.compareTo(startDate) >= 0);
        return startDate_.compareTo(startDate) >= 0 && startDate_.compareTo(endDate) < 0
                || endDate_.compareTo(startDate) >= 0 && endDate_.compareTo(endDate) < 0;
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
	            otherDate = ((Event) other).startDate_;
	            otherEndDate = ((Event) other).endDate_;
	        }
	        if (!startDate_.equals(otherDate)) {
                if (isDone()) {
                    return -1 * startDate_.compareTo(otherDate); // flip order for done tasks
                } else {
                    return startDate_.compareTo(otherDate);
                }
	        } else if (otherEndDate != null && !endDate_.equals(otherEndDate)) {
	            return endDate_.compareTo(otherEndDate);
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
        if (!(other instanceof Event)) {
            return false;
        }
        if (!super.equals(other)) {
            return false;
        }
        Event otherEvent = (Event) other;
        return otherEvent.startDate_.equals(startDate_) && otherEvent.endDate_.equals(endDate_);
    }
}
