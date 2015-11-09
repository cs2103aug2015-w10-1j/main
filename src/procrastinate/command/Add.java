//@@author A0124321Y
package procrastinate.command;

import procrastinate.task.Task;

public abstract class Add extends CrudCmd implements FeedbackCrud {
    protected Task task;
    protected String description;

    public Add(CommandType type) {
        super(type);
    }

    public CleanCommand addDescription(String description) {
        assert description != null;

        this.description = description.trim();
        return this;
    }

    public String getDescription() {
        return description;
    }
}
