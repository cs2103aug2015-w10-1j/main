//@@author A0124321Y
package procrastinate.command;

import java.util.Date;

import procrastinate.task.TaskEngine;
import procrastinate.ui.UI;

public abstract class CleanCommand implements Preview, Feedback {
    public static enum CommandType {
        ADD_DEADLINE, ADD_EVENT, ADD_DREAM, EDIT, EDIT_PARTIAL, EDIT_TO_DREAM, DELETE, UNDO, DONE,
        SEARCH, SEARCH_ON, SEARCH_DUE, SEARCH_RANGE, SHOW_OUTSTANDING, SHOW_DONE, SHOW_ALL, SHOW_SUMMARY,
        SET_PATH, EXIT, HELP, INVALID;
    }

    protected int lineNum;
    protected String searchTerm, searchString;
    protected Date searchStartDate, searchEndDate;
    protected boolean searchShowDone;

    private CommandType type;
    private boolean preview;

    public CleanCommand(CommandType type) {
        assert type != null;

        this.type = type;
    }

    public abstract String run(UI ui, TaskEngine taskEngine);

    public String getSearchStr() {
        return searchString;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public Date getSearchStartDate() {
        return searchStartDate;
    }

    public Date getSearchEndDate() {
        return searchEndDate;
    }

    public boolean getSearchShowDone() {
        return searchShowDone;
    }

    public int getLineNumber() {
        return lineNum;
    }

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
