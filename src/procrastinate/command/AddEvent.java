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
            return feedback;
        }

        // make feedback for preview zone
        feedback = String.format(ADD_EVENT,
                                 Feedback.shorten(description, MAX_LENGTH_DESCRIPTION_SHORT),
                                 Feedback.formatDateTime(startDate),
                                 Feedback.formatDateTime(endDate));

        if (isPreview()) {
            assert feedback != null;
            return feedback;
        }

        // make task
        task = new Event(description, startDate, endDate);

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

    public CleanCommand addStartDate(Date startDate) {
        this.startDate = startDate;
        return this;
    }

    public CleanCommand addEndDate(Date endDate) {
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
