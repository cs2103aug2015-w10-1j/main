package procrastinate;

import java.util.Date;

public class Event extends Task {
	private Date startDate;
	private Date endDate;
	
	public Event(String description, Date startDate, Date endDate) {
		super(Task.TaskType.EVENT, description);
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
