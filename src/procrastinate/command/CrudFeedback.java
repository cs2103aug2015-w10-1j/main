//@@author A0124321Y
package procrastinate.command;

public interface CrudFeedback extends Feedback {
    public static final String ADD_DREAM = "New dream: %1$s";
    public static final String ADD_DEADLINE = "New deadline: %1$s due %2$s";
    public static final String ADD_EVENT = "New event: %1$s %2$s to %3$s";

    public static final String EDIT_DREAM = "Edited #%1$s: %2$s";
    public static final String EDIT_DEADLINE = "Edited #%1$s: %2$s due %3$s";
    public static final String EDIT_EVENT = "Edited #%1$s: %2$s %3$s to %4$s";
    public static final String EDIT_PARTIAL = "Please specify the new description/date(s) or press tab";

    public static final String DELETED = "Deleted %1$s: %2$s";

    public static final String DONE = "Done %1$s: %2$s";
    public static final String UNDONE = "Undone %1$s: %2$s";

    public static final String UNDO = "Undid last operation";
    public static final String NOTHING_TO_UNDO = "Nothing to undo";

    public static final String INVALID_LINE_NUMBER = "Invalid line number: %1$d";

    public static final String ERROR_SAVE_HEADER = "Could not save changes to file!";
    public static final String ERROR_SAVE_MESSAGE = FEEDBACK_TRY_AGAIN;

    public static final int MAX_LENGTH_DESCRIPTION = 20;
    public static final int MAX_LENGTH_DESCRIPTION_SHORT = 10;
    public static final int MAX_LENGTH_DESCRIPTION_TINY = 7;
}