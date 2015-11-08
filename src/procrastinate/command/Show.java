package procrastinate.command;

public abstract class Show extends CleanCommand implements ShowFeedback {

    public Show(CommandType type) {
        super(type);
    }
}
