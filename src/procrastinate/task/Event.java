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

	public Event(String description, Date startDate, Date endDate, boolean done, UUID id) {
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
}
