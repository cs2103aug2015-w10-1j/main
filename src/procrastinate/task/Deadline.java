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
}
