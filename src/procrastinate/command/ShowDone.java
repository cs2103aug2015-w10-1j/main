//@@author A0124321Y
package procrastinate.command;

import procrastinate.task.TaskEngine;
import procrastinate.ui.UI;

public class ShowDone extends Show {

    public ShowDone() {
        super(CommandType.SHOW_DONE);
    }

    @Override
    public String run(UI ui, TaskEngine taskEngine) {
        return SHOW_DONE;
    }
}