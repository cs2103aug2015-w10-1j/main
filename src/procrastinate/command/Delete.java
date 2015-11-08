package procrastinate.command;

import procrastinate.task.Task;
import procrastinate.task.TaskEngine;
import procrastinate.ui.UI;

public class Delete extends CrudCmd {
    public Delete(int lineNum) {
        super(CommandType.DELETE);
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

        // make feedback for preview zone
        feedback = String.format(DELETED, task.getTypeString(), task.getDescription());

        if (isPreview()) {
            assert feedback != null;
            return feedback;
        }

        if (taskEngine.delete(task.getId())) {
            return feedback;
        } else {
            // display error msg if add fails
            ui.createErrorDialog(ERROR_SAVE_HEADER, ERROR_SAVE_MESSAGE);
            feedback = FEEDBACK_TRY_AGAIN;
            return feedback;
        }
    }

}
