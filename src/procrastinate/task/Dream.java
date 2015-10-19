package procrastinate.task;

import java.util.UUID;

public class Dream extends Task {

	public Dream(String description) {
		super(TaskType.DREAM, description);
	}

	protected Dream(String description, boolean done, UUID id) {
		super(TaskType.DREAM, description, done, id);
	}

    @Override
    public int compareTo(Task other) {
        if (other.getType() != TaskType.DREAM) {
            return 1;
        } else {
            return this.getDescription().compareTo(other.getDescription());
        }
    }
}
