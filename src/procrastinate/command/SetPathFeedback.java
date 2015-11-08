package procrastinate.command;

public interface SetPathFeedback extends Feedback {
    public static final String SET_LOCATION = "Set save location to %1$s %2$s";

    public static final String ERROR_SET_LOCATION_HEADER = "Could not set save location:";
    public static final String ERROR_SET_LOCATION_MESSAGE = "%1$s%2$s\n\n" + FEEDBACK_TRY_AGAIN;
}
