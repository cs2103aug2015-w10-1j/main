//@@author A0124321Y
package procrastinate.command;

import java.util.Date;

import procrastinate.task.Event;
import procrastinate.task.TaskEngine;
import procrastinate.ui.UI;

public class AddEvent extends Add {
    private Date startDate;
    private Date endDate;

    public AddEvent(String description, Date startDate, Date endDate) {
        super(CommandType.ADD_EVENT);
        assert startDate != null && endDate != null;

        addDescription(description);
        addStartDate(startDate);
        addEndDate(endDate);
    }

    @Override
    public String run(UI ui, TaskEngine taskEngine) {
        String feedback = null;

        if (endDate.before(startDate)) {
            feedback = String.format(INVALID_RANGE,
                                     Feedback.formatDateTime(startDate),
                                     Feedback.formatDateTime(endDate));
            setPreview(true);
            return feedback;
        }

        // make task
        task = new Event(description, startDate, endDate);

        // make feedback for preview zone
        feedback = ui.fitToStatus(String.format(ADD, task.getTypeString()), description, task.getDateString());

        if (isPreview()) {
            assert feedback != null;
            return feedback;
        }

        // add to task engine
        if (taskEngine.add(task)) {
            return feedback;
        } else {
            // display error msg if add fails
            ui.createErrorDialog(ERROR_SAVE_HEADER, ERROR_SAVE_MESSAGE);
            feedback = FEEDBACK_TRY_AGAIN;
            return feedback;
        }

    }

    public Command addStartDate(Date startDate) {
        this.startDate = startDate;
        return this;
    }

    public Command addEndDate(Date endDate) {
        this.endDate = endDate;
        return this;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

}
