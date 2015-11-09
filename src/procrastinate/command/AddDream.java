//@@author A0124321Y
package procrastinate.command;

import procrastinate.task.Dream;
import procrastinate.task.TaskEngine;
import procrastinate.ui.UI;

public class AddDream extends Add {
    public AddDream(String description) {
        super(CommandType.ADD_DREAM);
        addDescription(description);
    }

    @Override
    public String run(UI ui, TaskEngine taskEngine) {
        String feedback = null;

        // make task
        task = new Dream(description);

        // make feedback for preview zone
        feedback = ui.fitToStatus(String.format(ADD, task.getTypeString()), description, task.getDateString());

        if (isPreview()) {
            assert feedback != null;
            return feedback;
        }

        if (taskEngine.add(task)) {
            return feedback;
        } else {
            // display error msg if add fails
            ui.createErrorDialog(ERROR_SAVE_HEADER, ERROR_SAVE_MESSAGE);
            feedback = FEEDBACK_TRY_AGAIN;
            return feedback;
        }


    }
}
