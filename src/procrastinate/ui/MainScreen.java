//@@author A0121597B
package procrastinate.ui;

import java.util.List;

import javafx.animation.FadeTransition;
import javafx.scene.layout.VBox;
import procrastinate.task.Task;

/**
 * <h1>MainScreen is a subclass of the MultiCategoryScreen and is used to show all
 * the outstanding tasks or all the different tasks.</h1>
 *
 * The MainScreen is the most frequently used screen which will be shown and updated
 * after most commands are executed.
 */
public class MainScreen extends MultiCategoryScreen {

    // ================================================================================
    // Message Strings
    // ================================================================================

    private static final String FX_BACKGROUND_IMAGE_NO_TASKS = "-fx-background-image: url('/procrastinate/ui/images/no-tasks.png')";

    // ================================================================================
    // MainScreen Constructor
    // ================================================================================

    protected MainScreen() {
        super();
    }

    // ================================================================================
    // MainScreen Methods
    // ================================================================================

    @Override
    protected void updateTaskList(List<Task> taskList) {
        FadeTransition fadeOutDeletedTaskEntry = fadeOutDeletedTaskEntry(taskList);

        fadeOutDeletedTaskEntry.setOnFinished(finish -> {
            getUpdatedDates();
            clearTaskList();

            for (Task task : taskList) {
                taskCount.set(taskCount.get() + 1);

                addTaskByType(task);
            }

            updateDisplay();
            highlightAddedOrEditedTaskEntry(taskList);
        });

        fadeOutDeletedTaskEntry.play();
    }

    @Override
    protected void setBackgroundImageIfMainVBoxIsEmpty(VBox mainVBox) {
        if (mainVBox.getChildren().isEmpty()) {
            mainVBox.setStyle(FX_BACKGROUND_IMAGE_NO_TASKS);
        }
    }

}
