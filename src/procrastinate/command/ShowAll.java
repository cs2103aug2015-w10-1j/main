//@@author A0124321Y
package procrastinate.command;

import procrastinate.task.TaskEngine;
import procrastinate.ui.UI;

public class ShowAll extends Show {

    public ShowAll() {
        super(CommandType.SHOW_ALL);
    }

    @Override
    public String run(UI ui, TaskEngine taskEngine) {
        assert ui == null && taskEngine == null;

        return SHOW_ALL;
    }
}