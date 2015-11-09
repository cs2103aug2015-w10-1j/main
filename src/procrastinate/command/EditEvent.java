//@@author A0124321Y
package procrastinate.command;

import java.util.Date;

import procrastinate.task.Event;
import procrastinate.task.TaskEngine;
import procrastinate.ui.UI;

public class EditEvent extends Edit {
    private Date startDate;
    private Date endDate;

    public EditEvent(int lineNum, String description, Date startDate, Date endDate) {
        super(CommandType.EDIT, lineNum);

        addDescription(description);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public String run(UI ui, TaskEngine taskEngine) {
        String feedback = null;

        if (isInvalid(lineNum, taskEngine)) {
            feedback = String.format(INVALID_LINE_NUMBER, lineNum);
            return feedback;
        }

        if (endDate.before(startDate)) {
            feedback = String.format(INVALID_RANGE,
                                     Feedback.formatDateTime(startDate),
                                     Feedback.formatDateTime(endDate));
            return feedback;
        }

        // make task
        oldTask = getTask(lineNum, taskEngine);
        if (description.isEmpty()) {
            newTask = new Event(oldTask.getDescription(), startDate, endDate);
        } else {
            newTask = new Event(description, startDate, endDate);
        }

        // make feedback for preview zone
        feedback = ui.fitToStatus(String.format(EDIT, getLineNumber()),
                                  newTask.getDescription(),
                                  newTask.getDateString());

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
