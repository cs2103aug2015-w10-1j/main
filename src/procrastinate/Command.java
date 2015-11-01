package procrastinate;

import java.util.Date;

public class Command {

    public static enum CommandType {
        ADD_DEADLINE, ADD_EVENT, ADD_DREAM, EDIT, EDIT_PARTIAL, EDIT_TO_DREAM, DELETE, UNDO, DONE,
        SEARCH, SEARCH_ON, SHOW_OUTSTANDING, SHOW_DONE, SHOW_ALL,
        SET_PATH, EXIT, HELP, INVALID;
    }

    // Required field for all command types
    private CommandType type;

    // Optional fields; availability depends on command type
    private String description;
    private Date date;
    private Date startDate;
    private Date endDate;
    private int lineNumber;
    private String pathDirectory;
    private String pathFilename;

    public Command(CommandType type) {
        this.type = type;
    }

    // ================================================================================
    // Setter methods (using chaining)
    // ================================================================================

    public Command addDescription(String description) {
        this.description = description.trim();
        return this;
    }

    public Command addDate(Date date) {
        this.date = date;
        return this;
    }

    public Command addStartDate(Date startDate) {
        this.startDate = startDate;
        return this;
    }

    public Command addEndDate(Date endDate) {
        this.endDate = endDate;
        return this;
    }

    public Command addLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
        return this;
    }

    public Command addPathDirectory(String pathDirectory) {
        this.pathDirectory = pathDirectory;
        return this;
    }

    public Command addPathFilename(String fileName) {
        this.pathFilename = fileName;
        return this;
    }

    // ================================================================================
    // Getter methods
    // ================================================================================

    public CommandType getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public Date getDate() {
        return date;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public String getPathDirectory() {
        return pathDirectory;
    }

    public String getPathFilename() {
        return pathFilename;
    }

}
