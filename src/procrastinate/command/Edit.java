//@@author A0124321Y
package procrastinate.command;

import procrastinate.task.Task;

public abstract class Edit extends CrudCmd implements CrudFeedback {
    protected Task oldTask;
    protected Task newTask;

    public Edit(CommandType type, int lineNum) {
        super(type);
        this.lineNum = lineNum;
    }
}
