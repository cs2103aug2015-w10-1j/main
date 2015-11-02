package procrastinate.ui;

import java.util.List;

import javafx.scene.layout.VBox;
import procrastinate.task.Task;

public class MainScreen extends MultiCategoryScreen {

    // ================================================================================
    // Message strings
    // ================================================================================

    private static final String FX_BACKGROUND_IMAGE_NO_TASKS = "-fx-background-image: url('/procrastinate/ui/images/no-tasks.png')";

    // ================================================================================
    // MainScreen Constructor
    // ================================================================================

    protected MainScreen(String filePath) {
        super(filePath);
    }

    // ================================================================================
    // MainScreen methods
    // ================================================================================

    @Override
    protected void updateTaskList(List<Task> taskList) {
        getUpdatedDates();
        clearTaskList();

        for (Task task : taskList) {
            taskCount.set(taskCount.get() + 1);

            addTaskByType(task);
        }
        updateDisplay();
    }

    @Override
    protected void setBackgroundImageIfMainVBoxIsEmpty(VBox mainVBox) {
        if (mainVBox.getChildren().isEmpty()) {
            mainVBox.setStyle(FX_BACKGROUND_IMAGE_NO_TASKS);
        }
    }

}
