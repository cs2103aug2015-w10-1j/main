//@@author A0124321Y
package procrastinate.command;

public abstract class CleanCommand implements Preview, Feedback {
    public static enum CommandType {
        ADD_DEADLINE, ADD_EVENT, ADD_DREAM, EDIT, EDIT_PARTIAL, EDIT_TO_DREAM, DELETE, UNDO, DONE,
        SEARCH, SEARCH_ON, SHOW_OUTSTANDING, SHOW_DONE, SHOW_ALL, SHOW_SUMMARY,
        SET_PATH, EXIT, HELP, INVALID;
    }

    // Required field for all command types
    protected CommandType type;

    public CleanCommand(CommandType type) {
        this.type = type;
    }

    public abstract String run();

    public CommandType getType() {
        return type;
    }
}
