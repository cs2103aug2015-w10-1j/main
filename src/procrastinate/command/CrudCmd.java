//@@author A0124321Y
package procrastinate.command;

import procrastinate.task.Task;
import procrastinate.task.TaskEngine;

public abstract class CrudCmd extends CleanCommand implements CrudFeedback {
    protected int lineNum;

    public CrudCmd(CommandType type) {
        super(type);
    }

    public int getLineNum() {
        return lineNum;
    }

    /**
     * Retrieves a task from the list of task TaskEngine maintains
     * List is 0-based index.
     * @param lineNumber visible from UI i.e. 1-based index
     * @return task
     */
    public Task getTask(int lineNumber, TaskEngine taskEngine) {
        return taskEngine.getCurrentTaskList().get(lineNumber - 1);
    }

    /**
     * Checks for line number validity
     * @param lineNum
     * @param taskEngine
     * @return
     */
    public boolean isInvalid(int lineNum, TaskEngine taskEngine) {
        return !(lineNum >= 1 && lineNum <= taskEngine.getCurrentTaskList().size());
    }
}
