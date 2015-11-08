//@@author A0124321Y
package procrastinate.command;

import procrastinate.task.Task;
import procrastinate.task.TaskEngine;
import procrastinate.ui.UI;

public class Done extends CrudCmd {
    public Done(int lineNum) {
        super(CommandType.DONE);
        this.lineNum = lineNum;
    }

    @Override
    public String run(UI ui, TaskEngine taskEngine) {
        String feedback = null;

        if (isInvalid(lineNum, taskEngine)) {
            feedback = String.format(INVALID_LINE_NUMBER, lineNum);
            return feedback;
        }
        Task task = getTask(lineNum, taskEngine);
        boolean done = task.isDone();

        // make feedback for preview zone
        if (done) {
            feedback = String.format(UNDONE, task.getTypeString(), task.getDescription());
        } else {
            feedback = String.format(DONE, task.getTypeString(), task.getDescription());
        }

        if (isPreview()) {
            assert feedback != null;
            return feedback;
        }

        boolean success;
        if (done) {
            success = taskEngine.undone(task.getId());
        } else {
            success = taskEngine.done(task.getId());
        }

        if (!success) {
            // display error msg if add fails
            ui.createErrorDialog(ERROR_SAVE_HEADER, ERROR_SAVE_MESSAGE);
            feedback = FEEDBACK_TRY_AGAIN;
            return feedback;
        }
        return feedback;
    }
}