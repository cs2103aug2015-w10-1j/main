package procrastinate.ui;

import java.util.List;

import javafx.scene.layout.VBox;
import procrastinate.task.Task;

public class MainScreen extends MultiCategoryScreen {

    // ================================================================================
    // Message strings
    // ================================================================================

    private static final String LOCATION_EMPTY_VIEW = "images/no-tasks.png";

    private static String FX_BACKGROUND_IMAGE_NO_TASKS; // will be initialised later on.

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
    protected void checkIfMainVBoxIsEmpty(VBox mainVBox) {
        if (FX_BACKGROUND_IMAGE_NO_TASKS == null) {
            String image = MainScreen.class.getResource(LOCATION_EMPTY_VIEW).toExternalForm();
            FX_BACKGROUND_IMAGE_NO_TASKS = "-fx-background-image: url('" + image + "');";
        }
        if (mainVBox.getChildren().isEmpty()) {
            mainVBox.setStyle(FX_BACKGROUND_IMAGE_NO_TASKS);
        }
    }

}
