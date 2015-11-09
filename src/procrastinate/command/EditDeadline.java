//@@author A0124321Y
package procrastinate.command;

import java.util.Date;

import procrastinate.task.Deadline;
import procrastinate.task.TaskEngine;
import procrastinate.ui.UI;

public class EditDeadline extends Edit {
    private Date date;

    public EditDeadline(int lineNum, String description, Date date) {
        super(CommandType.EDIT, lineNum);

        addDescription(description);
        this.date = date;
    }

    @Override
    public String run(UI ui, TaskEngine taskEngine) {
        String feedback = null;

        if (isInvalid(lineNum, taskEngine)) {
            feedback = String.format(INVALID_LINE_NUMBER, lineNum);
            return feedback;
        }

        // make feedback for preview zone
        feedback = String.format(EDIT_DEADLINE, lineNum,
                                 Feedback.shorten(description, MAX_LENGTH_DESCRIPTION),
                                 Feedback.formatDateTime(date));

        if (isPreview()) {
            assert feedback != null;
            return feedback;
        }

        // make task
        oldTask = getTask(lineNum, taskEngine);
        if (description.isEmpty()) {
            newTask = new Deadline(oldTask.getDescription(), date);
        } else {
            newTask = new Deadline(description, date);
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
