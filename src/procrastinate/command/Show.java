package procrastinate.command;

public abstract class Show extends Command implements FeedbackShow {

    public Show(CommandType type) {
        super(type);
    }
}
