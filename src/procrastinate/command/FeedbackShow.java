package procrastinate.command;

public interface FeedbackShow extends Feedback {
    public static final String SHOW_ALL = "Showing all tasks";
    public static final String SHOW_DONE = "Showing completed tasks";
    public static final String SHOW_OUTSTANDING = "Showing outstanding tasks";
    public static final String SHOW_SUMMARY = "Showing summary of outstanding tasks";
}
