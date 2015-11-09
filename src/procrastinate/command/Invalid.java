package procrastinate.command;

import procrastinate.task.TaskEngine;
import procrastinate.ui.UI;

public class Invalid extends CleanCommand {
    private String description;

    public Invalid(String description) {
        super(CommandType.INVALID);
        addDescription(description);
    }

    @Override
    public String run(UI ui, TaskEngine taskEngine) {
        return description;
    }

    public CleanCommand addDescription(String description) {
        assert description != null;

        this.description = description.trim();
        return this;
    }

    public String getDescription() {
        return description;
    }
}
