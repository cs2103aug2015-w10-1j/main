//@@author A0124321Y
package procrastinate.command;

import procrastinate.task.TaskEngine;
import procrastinate.ui.UI;

public class EditPartial extends CrudCmd implements CrudFeedback {
    protected int lineNum;

    public EditPartial(int lineNum) {
        super(CommandType.EDIT_PARTIAL);
        this.lineNum = lineNum;
    }

    @Override
    public String run(UI ui, TaskEngine taskEngine) {
        String feedback = null;

        if (isInvalid(lineNum, taskEngine)) {
            feedback = String.format(INVALID_LINE_NUMBER, lineNum);
            return feedback;
        }

        feedback = EDIT_PARTIAL;
        return feedback;
    }

    public int getLineNum() {
        return lineNum;
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
