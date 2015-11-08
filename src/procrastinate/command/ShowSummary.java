//@@author A0124321Y
package procrastinate.command;

import procrastinate.task.TaskEngine;
import procrastinate.ui.UI;

public class ShowSummary extends Show {

    public ShowSummary() {
        super(CommandType.SHOW_SUMMARY);
    }

    @Override
    public String run(UI ui, TaskEngine taskEngine) {
        assert ui == null && taskEngine == null;

        return SHOW_SUMMARY;
    }
}