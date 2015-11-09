//@@author A0124321Y
package procrastinate.command;

import procrastinate.task.Dream;
import procrastinate.task.TaskEngine;
import procrastinate.ui.UI;

public class EditDream extends Edit {
    public EditDream(int lineNum, String description) {
        super(CommandType.EDIT, lineNum);

        addDescription(description);
    }

    @Override
    public String run(UI ui, TaskEngine taskEngine) {
        String feedback = null;

        if (isInvalid(lineNum, taskEngine)) {
            feedback = String.format(INVALID_LINE_NUMBER, lineNum);
            return feedback;
        }

        // make feedback for preview zone
        feedback = String.format(EDIT_DREAM, lineNum, description);

        if (isPreview()) {
            assert feedback != null;
            return feedback;
        }

        // make task
        oldTask = getTask(lineNum, taskEngine);
        if (description.isEmpty()) {
            newTask = new Dream(oldTask.getDescription());
        } else {
            newTask = new Dream(description);
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
