//@@author A0124321Y
package procrastinate.command;

import procrastinate.task.Task;

public abstract class Edit extends CrudCmd implements FeedbackCrud {
    protected String description;
    protected Task oldTask;
    protected Task newTask;

    public Edit(CommandType type, int lineNum) {
        super(type);
        this.lineNum = lineNum;
    }

    public Command addDescription(String description) {
        assert description != null;

        this.description = description.trim();
        return this;
    }

    public String getDescription() {
        return description;
    }
}
