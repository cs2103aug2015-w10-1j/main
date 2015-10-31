package procrastinate.ui;

import java.util.List;

import javafx.scene.Node;
import javafx.scene.layout.VBox;
import procrastinate.task.Deadline;
import procrastinate.task.Event;
import procrastinate.task.Task;

public class DoneScreen extends CenterScreen {

    // ================================================================================
    // Message strings
    // ================================================================================

    private static final String CATEGORY_DONE = "Showing all completed tasks";

    private static final String LOCATION_EMPTY_VIEW = "images/no-tasks.png";

    // ================================================================================
    // Class variables
    // ================================================================================

    private Node doneNode;
    private VBox doneTaskList;

    private VBox mainVBox;

    // ================================================================================
    // DoneScreen Constructor
    // ================================================================================

    protected DoneScreen(String filePath) {
        super(filePath);
        createCategories();
        retrieveFxmlElements();
    }

    // ================================================================================
    // Init methods
    // ================================================================================

    @Override
    protected void createCategories() {
        CategoryBox doneBox = new CategoryBox(CATEGORY_DONE);
        this.doneNode = doneBox.getCategoryBox();
        this.doneTaskList = doneBox.getTaskListVBox();
    }

    private void retrieveFxmlElements() {
        this.mainVBox = getMainVBox();
    }

    // ================================================================================
    // Display methods
    // ================================================================================

    protected void updateTaskList(List<Task> taskList) {
        clearTaskList();
        mainVBox.getChildren().add(doneNode);

        String dateString;

        for (Task task : taskList) {
            System.out.println(task);
            if (task.isDone()) {
                taskCount.set(taskCount.get() + 1);

                switch (task.getType()) {

                    case DEADLINE: {
                        dateString = dateFormatWithFriendlyDayAndYear.format(((Deadline) task).getDate());
                        addDoneTask(taskCountFormatted.get(), task, dateString);
                        break;
                    }

                    case EVENT: {
                        dateString = dateFormatWithFriendlyDayAndYear.format(((Event) task).getStartDate())
                                    + EVENT_DATE_SEPARATOR_GENERAL
                                    + dateFormatWithFriendlyDayAndYear.format(((Event)task).getEndDate());
                        addDoneTask(taskCountFormatted.get(), task, dateString);
                        break;
                    }

                    case DREAM: {
                        addDoneTask(taskCountFormatted.get(), task, null);
                        break;
                    }

                    default: {
                        System.out.println(MESSAGE_UNABLE_TO_DETERMINE_TYPE);
                        break;
                    }
                }
            }
        }
    }

    private void addDoneTask(String taskCount, Task task, String dateString) {
        TaskEntry taskEntry;
        if (dateString == null) {
            taskEntry = new TaskEntry(taskCount, task.getDescription(), task.isDone());
        } else {
            taskEntry = new TaskEntry(taskCount, task.getDescription(), dateString, task.isDone());
        }
        doneTaskList.getChildren().add(taskEntry.getEntryDisplay());
    }

    // ================================================================================
    // Utility methods
    // ================================================================================

    /**
     * Used when updating the task list, removes all tasks and resets the task counter
     */
    private void clearTaskList() {
        taskCount.set(0);
        doneTaskList.getChildren().clear();
    }

    @SuppressWarnings("unused")
    private void checkIfMainVBoxIsEmpty(VBox mainVBox) {
        if (FX_BACKGROUND_IMAGE_NO_TASKS == null) {
            String image = MainScreen.class.getResource(LOCATION_EMPTY_VIEW).toExternalForm();
            FX_BACKGROUND_IMAGE_NO_TASKS = "-fx-background-image: url('" + image + "');";
        }
        if (mainVBox.getChildren().isEmpty()) {
            mainVBox.setStyle(FX_BACKGROUND_IMAGE_NO_TASKS);
        }
    }
}