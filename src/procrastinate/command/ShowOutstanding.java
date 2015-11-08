//@@author A0124321Y
package procrastinate.command;

import procrastinate.task.TaskEngine;
import procrastinate.ui.UI;

public class ShowOutstanding extends Show {

    public ShowOutstanding() {
        super(CommandType.SHOW_OUTSTANDING);
    }

    @Override
    public String run(UI ui, TaskEngine taskEngine) {
        assert ui == null && taskEngine == null;

        return SHOW_OUTSTANDING;
    }
}