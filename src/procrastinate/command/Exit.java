package procrastinate.command;

import procrastinate.task.TaskEngine;
import procrastinate.ui.UI;

public class Exit extends CleanCommand implements FeedbackExit {

    public Exit() {
        super(CommandType.EXIT);
    }

    @Override
    public String run(UI ui, TaskEngine taskEngine) {
        if (isPreview()) {
            return EXIT;
        } else {
            return FEEDBACK_TRY_AGAIN;
        }
    }
}
