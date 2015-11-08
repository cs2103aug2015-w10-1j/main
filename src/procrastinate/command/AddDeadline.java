//@@author A0124321Y
package procrastinate.command;

import java.util.Date;

import procrastinate.task.Deadline;
import procrastinate.task.TaskEngine;
import procrastinate.ui.UI;

public class AddDeadline extends Add {
    private Date date;

    public AddDeadline(String description, Date date) {
        super(CommandType.ADD_DEADLINE);

        assert date != null;

        addDescription(description);
        addDate(date);
    }

    @Override
    public String run(UI ui, TaskEngine taskEngine) {
        String feedback = null;

        // make feedback for preview zone
        feedback = String.format(ADD_DEADLINE, Feedback.shorten(description, MAX_LENGTH_DESCRIPTION),
                Feedback.formatDateTime(date));

        if (isPreview()) {
            assert feedback != null;
            return feedback;
        }

        // make task
        task = new Deadline(description, date);

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

    public CleanCommand addDate(Date date) {
        this.date = date;
        return this;
    }

    public Date getDate() {
        return date;
    }
}
