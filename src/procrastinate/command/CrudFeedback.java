//@@author A0124321Y
package procrastinate.command;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public interface CrudFeedback extends Feedback {
    public static final String ADD_DREAM = "New dream: %1$s";
    public static final String ADD_DEADLINE = "New deadline: %1$s due %2$s";
    public static final String ADD_EVENT = "New event: %1$s %2$s to %3$s";
    public static final String FEEDBACK_ELLIPSIS = "...";

    public static final String EDIT_DREAM = "Edited #%1$s: %2$s";
    public static final String EDIT_DEADLINE = "Edited #%1$s: %2$s due %3$s";
    public static final String EDIT_EVENT = "Edited #%1$s: %2$s %3$s to %4$s";
    public static final String EDIT_PARTIAL = "Please specify the new description/date(s) or press tab";

    public static final String DELETED = "Deleted %1$s: %2$s";

    public static final String DONE = "Done %1$s: %2$s";
    public static final String UNDONE = "Undone %1$s: %2$s";

    public static final String INVALID_RANGE = "Invalid dates: %2$s is before %1$s";
    public static final String INVALID_LINE_NUMBER = "Invalid line number: %1$d";

    public static final String ERROR_SAVE_HEADER = "Could not save changes to file!";
    public static final String ERROR_SAVE_MESSAGE = FEEDBACK_TRY_AGAIN;

    public static final int MAX_LENGTH_DESCRIPTION = 20;
    public static final int MAX_LENGTH_DESCRIPTION_SHORT = 10;
    public static final int MAX_LENGTH_DESCRIPTION_TINY = 7;

    public static final DateFormat dateTimeFormatter = new SimpleDateFormat("d/MM/yy h:mma");
    public static final DateFormat dateFormatter = new SimpleDateFormat("d/MM/yy");

    public static String formatDateTime(Date date) {
        return dateTimeFormatter.format(date);
    }

    public static String formatDate(Date date) {
        return dateFormatter.format(date);
    }

    public static String shorten(String description, int maxLength) {
        if (description.length() <= maxLength) {
            return description;
        }

        return description.substring(0, maxLength - 1) + FEEDBACK_ELLIPSIS;
    }}
