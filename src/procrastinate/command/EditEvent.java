//@@author A0124321Y
package procrastinate.command;

import java.util.Date;

import procrastinate.task.Event;
import procrastinate.task.TaskEngine;
import procrastinate.ui.UI;

public class EditEvent extends Edit {
    private String description;
    private Date startDate;
    private Date endDate;

    public EditEvent(int lineNum, String description, Date startDate, Date endDate) {
        super(CommandType.EDIT, lineNum);

        assert description != null;
        assert startDate != null;
        assert endDate != null;

        this.description = description;
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
                                     CrudFeedback.formatDateTime(startDate),
                                     CrudFeedback.formatDateTime(endDate));
            return feedback;
        }

        // make feedback for preview zone
        feedback = String.format(EDIT_EVENT, lineNum,
                                 CrudFeedback.shorten(description, MAX_LENGTH_DESCRIPTION_TINY),
                                 CrudFeedback.formatDateTime(startDate),
                                 CrudFeedback.formatDateTime(endDate));

        if (isPreview()) {
            assert feedback != null;
            return feedback;
        }

        // make task
        oldTask = getTask(lineNum, taskEngine);
        newTask = new Event(description, startDate, endDate);

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
