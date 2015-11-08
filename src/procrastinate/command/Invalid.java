package procrastinate.command;

import procrastinate.task.TaskEngine;
import procrastinate.ui.UI;

public class Invalid extends CleanCommand {
    private String description;

    public Invalid(String description) {
        super(CommandType.INVALID);
        this.description = description;
    }

    @Override
    public String run(UI ui, TaskEngine taskEngine) {
        assert ui == null && taskEngine == null;
        return description;
    }
}
