//@@author A0124321Y
package procrastinate.command;

import java.util.Date;

import procrastinate.task.Task;

public abstract class Add extends CleanCommand implements AlterTask {
    protected Task task;
    protected String description;

    public Add(CommandType type) {
        super(type);
    }

    public CleanCommand addDescription(String description) {
        assert description != null;

        this.description = description;
        return this;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String formatDateTime(Date date) {
        return dateTimeFormatter.format(date);
    }

    @Override
    public String formatDate(Date date) {
        return dateFormatter.format(date);
    }

    @Override
    public String shorten(String description, int maxLength) {
        if (description.length() <= maxLength) {
            return description;
        }

        return description.substring(0, maxLength - 1) + FEEDBACK_ELLIPSIS;
    }
}
