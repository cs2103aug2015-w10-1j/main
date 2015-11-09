//@@author A0124321Y
package procrastinate.command;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A feedback interface to consolidate all feedback messages
 * @author Gerald
 *
 */
public interface Feedback {
    public static final String FEEDBACK_TRY_AGAIN = "Please set a different save location and try again";;
    public static final String INVALID_RANGE = "Invalid dates: %2$s is before %1$s";
    public static final String ELLIPSIS = "...";

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

        return description.substring(0, maxLength - 1) + ELLIPSIS;
    }
}
