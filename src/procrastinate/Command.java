package procrastinate;

import java.util.Date;

public class Command {

    public static enum CommandType {
        ADD_DEADLINE, ADD_EVENT, ADD_DREAM, EDIT, DELETE, UNDO, DONE, SEARCH, EXIT, HELP, INVALID;
    }

    // Required field for all command types
    private CommandType type;

    // Optional fields; availability depends on command type
    private String description;
    private Date date;
    private Date endDate;
    private int lineNumber;

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
        this.date = startDate;
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
        return date;
    }

    public Date getEndDate() {
        return endDate;
    }

    public int getLineNumber() {
        return lineNumber;
    }

}
