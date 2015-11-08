//@@author A0124321Y
package procrastinate.command;

import procrastinate.task.TaskEngine;
import procrastinate.ui.UI;

public abstract class CleanCommand implements Preview, Feedback {
    public static enum CommandType {
        ADD_DEADLINE, ADD_EVENT, ADD_DREAM, EDIT, EDIT_PARTIAL, EDIT_TO_DREAM, DELETE, UNDO, DONE,
        SEARCH, SEARCH_ON, SEARCH_DUE, SEARCH_RANGE, SHOW_OUTSTANDING, SHOW_DONE, SHOW_ALL, SHOW_SUMMARY,
        SET_PATH, EXIT, HELP, INVALID;
    }

    // Required field for all command types
    private CommandType type;
    private boolean preview;

    public CleanCommand(CommandType type) {
        assert type != null;

        this.type = type;
    }

    public abstract String run(UI ui, TaskEngine taskEngine);

    @Override
    public boolean isPreview() {
        return preview;
    }

    @Override
    public void setPreview(boolean preview) {
        this.preview = preview;
    }

    public CommandType getType() {
        return type;
    }
}
