//@@author A0124321Y
package procrastinate.command;

import procrastinate.task.TaskEngine;
import procrastinate.ui.UI;

public class Undo extends CrudCmd {
    public Undo() {
        super(CommandType.UNDO);
    }

    @Override
    public String run(UI ui, TaskEngine taskEngine) {
        String feedback = null;

        if (!taskEngine.hasPreviousOperation()) {
            feedback = NOTHING_TO_UNDO;
            return feedback;
        }

        feedback = UNDO;

        if (isPreview()) {
            return feedback;
        }

        if (taskEngine.undo()) {
            return feedback;
        } else {
            ui.createErrorDialog(ERROR_SAVE_HEADER, ERROR_SAVE_MESSAGE);
            return FEEDBACK_TRY_AGAIN;
        }
    }

}