//@@author A0080485B
package procrastinate.task;

import java.util.Date;
import java.util.UUID;

public class Dream extends Task {

	public Dream(String description) {
		super(TaskType.DREAM, description);
	}

	protected Dream(String description, boolean done, UUID id) {
		super(TaskType.DREAM, description, done, id);
	}

    @Override
    public boolean isWithin(Date startDate, Date endDate) {
        return false;
    }

    @Override
    public int compareTo(Task other) {
        int superResult = super.compareTo(other);
        if (superResult != 0) {
            return superResult;
        }
        if (other.getType() != TaskType.DREAM) {
            return 1;
        } else {
            return this.getDescription().compareTo(other.getDescription());
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
        if (!(o instanceof Dream)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        return true;
    }
}
