//@@author A0124321Y
package procrastinate.command;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public interface AlterTask extends Feedback {
    public static final String ADD_DREAM = "New dream: %1$s";
    public static final String ADD_DEADLINE = "New deadline: %1$s due %2$s";
    public static final String ADD_EVENT = "New event: %1$s %2$s to %3$s";
    public static final String INVALID_RANGE = "Invalid dates: %2$s is before %1$s";
    public static final String FEEDBACK_ELLIPSIS = "...";

    public static final String EDIT_DREAM = "Edited #%1$s: %2$s";
    public static final String INVALID_LINE_NUMBER = "Invalid line number: %1$d";

    public static final String ERROR_SAVE_HEADER = "Could not save changes to file!";
    public static final String ERROR_SAVE_MESSAGE = FEEDBACK_TRY_AGAIN;

    public static final int MAX_LENGTH_DESCRIPTION = 20;
    public static final int MAX_LENGTH_DESCRIPTION_SHORT = 10;
    public static final int MAX_LENGTH_DESCRIPTION_TINY = 7;

    public static final DateFormat dateTimeFormatter = new SimpleDateFormat("d/MM/yy h:mma");
    public static final DateFormat dateFormatter = new SimpleDateFormat("d/MM/yy");

    public String formatDateTime(Date date);

    public String formatDate(Date date);

    public String shorten(String description, int maxLength);
}
