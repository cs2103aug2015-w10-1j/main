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

        // make task
        task = new Deadline(description, date);

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

    public CleanCommand addDate(Date date) {
        this.date = date;
        return this;
    }

    public Date getDate() {
        return date;
    }
}
