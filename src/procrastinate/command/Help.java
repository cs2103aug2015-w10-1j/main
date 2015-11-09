package procrastinate.command;

import procrastinate.task.TaskEngine;
import procrastinate.ui.UI;

public class Help extends Command implements FeedbackHelp {

    public Help() {
        super(CommandType.HELP);
    }

    @Override
    public String run(UI ui, TaskEngine taskEngine) {
        assert ui != null;

        String feedback = null;
        feedback = HELP;

        if (!isPreview()) {
            ui.showHelpOverlay();
        }

        return feedback;
    }
}
