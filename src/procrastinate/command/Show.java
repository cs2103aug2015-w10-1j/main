package procrastinate.command;

public abstract class Show extends CleanCommand implements FeedbackShow {

    public Show(CommandType type) {
        super(type);
    }
}
