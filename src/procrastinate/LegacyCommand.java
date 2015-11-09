//@@author A0080485B-unused
// Was refactored into individual procrastinate.command classes by A0124321Y
// Skeleton code for the Command class
package procrastinate;

import java.util.Date;

@Deprecated
public class LegacyCommand {

    public static enum CommandType {
        ADD_DEADLINE, ADD_EVENT, ADD_DREAM, EDIT, EDIT_PARTIAL, EDIT_TO_DREAM, DELETE, UNDO, DONE,
        SEARCH, SEARCH_ON, SHOW_OUTSTANDING, SHOW_DONE, SHOW_ALL, SHOW_SUMMARY,
        SET_PATH, EXIT, HELP, INVALID;
    }

    // Required field for all command types
    private CommandType type_;

    // Optional fields; availability depends on command type
    private String description_;
    private Date date_;
    private Date startDate_;
    private Date endDate_;
    private int lineNumber_;
    private String pathDirectory_;
    private String pathFilename_;

    public LegacyCommand(CommandType type) {
        this.type_ = type;
    }

    // ================================================================================
    // Setter methods (using chaining)
    // ================================================================================

    public LegacyCommand addDescription(String description) {
        description_ = description.trim();
        return this;
    }

    public LegacyCommand addDate(Date date) {
        date_ = date;
        return this;
    }

    public LegacyCommand addStartDate(Date startDate) {
        startDate_ = startDate;
        return this;
    }

    public LegacyCommand addEndDate(Date endDate) {
        endDate_ = endDate;
        return this;
    }

    public LegacyCommand addLineNumber(int lineNumber) {
        lineNumber_ = lineNumber;
        return this;
    }

    public LegacyCommand addPathDirectory(String pathDirectory) {
        pathDirectory_ = pathDirectory;
        return this;
    }

    public LegacyCommand addPathFilename(String fileName) {
        pathFilename_ = fileName;
        return this;
    }

    // ================================================================================
    // Getter methods
    // ================================================================================

    public CommandType getType() {
        return type_;
    }

    public String getDescription() {
        return description_;
    }

    public Date getDate() {
        return date_;
    }

    public Date getStartDate() {
        return startDate_;
    }

    public Date getEndDate() {
        return endDate_;
    }

    public int getLineNumber() {
        return lineNumber_;
    }

    public String getPathDirectory() {
        return pathDirectory_;
    }

    public String getPathFilename() {
        return pathFilename_;
    }

}
