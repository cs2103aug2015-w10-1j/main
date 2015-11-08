//@@author A0124321Y
package procrastinate.command;

import java.io.File;
import java.io.IOException;

import procrastinate.FileHandler;
import procrastinate.task.TaskEngine;
import procrastinate.ui.UI;

public class SetPath extends CleanCommand implements SetPathFeedback {
    private String pathDir, pathFilename;

    public SetPath(String pathDir, String pathFilename) {
        super(CommandType.SET_PATH);
        this.pathDir = pathDir;
        this.pathFilename = pathFilename == null ? FileHandler.DEFAULT_FULL_FILENAME : pathFilename;
    }

    @Override
    public String run(UI ui, TaskEngine taskEngine) {
        String feedback = null;
        String parsedPathDirectory = null;
        File targetDirectory = new File(pathDir);

        // normalise path
        try {
            parsedPathDirectory = targetDirectory.getCanonicalPath();
        } catch (IOException e) {
            parsedPathDirectory = targetDirectory.getAbsolutePath();
        }

        if (!parsedPathDirectory.endsWith(File.separator)) {
            parsedPathDirectory += File.separator;
        }

        // feedback for preview zone
        feedback = String.format(SET_LOCATION, parsedPathDirectory, pathFilename);

        if (isPreview()) {
            return feedback;
        }

        // sets the path
        if (taskEngine.set(parsedPathDirectory, pathFilename)) {
            return feedback;
        } else {
            ui.createErrorDialog(ERROR_SET_LOCATION_HEADER,
                                 String.format(ERROR_SET_LOCATION_MESSAGE,
                                               parsedPathDirectory, pathFilename));
            feedback = FEEDBACK_TRY_AGAIN;
            return feedback;
        }
    }
}
