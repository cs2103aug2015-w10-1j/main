package procrastinate;

import java.util.Date;

public class Command {

    public static enum Type {
        ADD_DEADLINE, ADD_EVENT, ADD_DREAM, EDIT, DELETE, UNDO, DONE, EXIT;
    }

    private Type type;

    private String description;
    private Date date;
    private Date endDate;
    private int lineNumber;

    public Command(Type type) {
        this.type = type;
    }

    public Command addDescription(String description) {
        this.description = description.trim();
        return this;
    }

    public Command addDate(Date date) {
        this.date = date;
        return this;
    }

    public Command endDate(Date date) {
        this.endDate = date;
        return this;
    }

    public Command addLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
        return this;
    }

    public Type getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public Date getDate() {
        return date;
    }

    public Date getEndDate() {
        return endDate;
    }

    public int getLineNumber() {
        return lineNumber;
    }

}
