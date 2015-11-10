//@@author A0124321Y
package procrastinate.command;

import procrastinate.task.Task;
import procrastinate.task.TaskEngine;
import procrastinate.ui.UI;

public class EditTaskDescription extends Edit {
    public EditTaskDescription(int lineNum, String description) {
        super(CommandType.EDIT, lineNum);

        addDescription(description);
    }

    @Override
    public String run(UI ui, TaskEngine taskEngine) {
        String feedback = null;

        if (isInvalid(lineNum, taskEngine)) {
            feedback = String.format(INVALID_LINE_NUMBER, lineNum);
            setPreview(true);
            return feedback;
        }

        // make task
        oldTask = getTask(lineNum, taskEngine);
        newTask = Task.copyWithNewId(oldTask);
        newTask.setDescription(description);

        // make feedback for preview zone
        feedback = ui.fitToStatus(String.format(EDIT, getLineNumber()), newTask.getDescription(), newTask.getDateString());

        if (isPreview()) {
            assert feedback != null;
            return feedback;
        }

        // replace old with new
        if (taskEngine.edit(oldTask.getId(), newTask)) {
            return feedback;
        } else {
            // display error msg if add fails
            ui.createErrorDialog(ERROR_SAVE_HEADER, ERROR_SAVE_MESSAGE);
            feedback = FEEDBACK_TRY_AGAIN;
            return feedback;
        }
    }

}
