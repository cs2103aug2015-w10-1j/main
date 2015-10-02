package procrastinate.task;

import java.util.Date;

public class Deadline extends Task {
	private Date date;

	public Deadline(String description, Date date) {
		super(TaskType.DEADLINE, description);
		this.date = date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Date getDate() {
		return date;
	}
}
