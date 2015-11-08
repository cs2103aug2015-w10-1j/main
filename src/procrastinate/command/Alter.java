//@@author A0124321Y
package procrastinate.command;

import java.util.Date;

public abstract class Alter extends CleanCommand implements AlterTask {

    public Alter(CommandType type) {
        super(type);
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
