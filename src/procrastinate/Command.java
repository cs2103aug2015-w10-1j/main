package procrastinate;

public class Command {

    public static enum Type {
        ADD_DREAM, EXIT;
    }

    private Type type;
    private String[] params;

    public Command(Type type, String[] params) {
        this.type = type;
        this.params = params;
    }

    public Type getType() {
        return type;
    }

    public String[] getParameters() {
        return params;
    }

}
