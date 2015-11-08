//@@author A0124321Y
package procrastinate.command;

public interface FeedbackSearch extends Feedback {
    public static final String SEARCH = "Searching for tasks";
    public static final String SEARCH_CONTAINING = " containing '%1$s'";
    public static final String SEARCH_ON = " on %1$s";
    public static final String SEARCH_DUE = " due by %1$s";
    public static final String SEARCH_FROM_TO = " from %1$s to %2$s";

    public static final String SEARCH_STRING_DESCRIPTION = "'%1$s'";
    public static final String SEARCH_STRING_NO_DESCRIPTION = "all tasks";
    public static final String SEARCH_STRING_ON = " on ";
    public static final String SEARCH_STRING_DUE = " due ";
    public static final String SEARCH_STRING_FROM_TO = " from %1$s to %2$s";
}
