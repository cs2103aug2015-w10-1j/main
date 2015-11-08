//@@author A0124321Y
package procrastinate.command;

import procrastinate.task.TaskEngine;
import procrastinate.ui.UI;

public class EditPartial extends CrudCmd implements FeedbackCrud {
    public EditPartial(int lineNum) {
        super(CommandType.EDIT_PARTIAL);
        this.lineNum = lineNum;
    }

    @Override
    public String run(UI ui, TaskEngine taskEngine) {
        assert ui == null;

        String feedback = null;

        if (isInvalid(lineNum, taskEngine)) {
            feedback = String.format(INVALID_LINE_NUMBER, lineNum);
            return feedback;
        }

        feedback = EDIT_PARTIAL;
        return feedback;
    }
}
