//@@author A0124321Y
package procrastinate.command;

public abstract class CrudCmd extends CleanCommand implements CrudFeedback {
    protected int lineNum;

    public CrudCmd(CommandType type) {
        super(type);
    }

    public int getLineNum() {
        return lineNum;
    }
}
