package procrastinate.task;

import java.util.UUID;

public class Dream extends Task {

	public Dream(String description) {
		super(TaskType.DREAM, description);
	}

	public Dream(String description, boolean done, UUID id) {
		super(TaskType.DREAM, description, done, id);
	}
}
