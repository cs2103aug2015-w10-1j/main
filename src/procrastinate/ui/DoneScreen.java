package procrastinate.ui;

import java.util.Date;
import java.util.List;

import javafx.animation.SequentialTransition;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import procrastinate.task.Deadline;
import procrastinate.task.Event;
import procrastinate.task.Task;

public class DoneScreen extends CenterScreen {

    // ================================================================================
    // Message strings
    // ================================================================================

    private static final String CATEGORY_DONE = "Your completed tasks";

    private static final String LOCATION_EMPTY_VIEW = "images/no-done-tasks.png";

//    private static final int TIME_TRANSITION_FADE = 250;

    private static String FX_BACKGROUND_IMAGE_NO_DONE_TASKS; // will be initialised later on.

    // ================================================================================
    // Class variables
    // ================================================================================

    private Node doneNode;
    private VBox doneTaskList;

    private VBox mainVBox;

    private Date today;

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

    @Override
    protected void updateTaskList(List<Task> taskList) {
        getUpdatedDates();
        clearTaskList();
        mainVBox.getChildren().add(doneNode);

        String dateString;

        for (Task task : taskList) {
            if (task.isDone()) {
                taskCount.set(taskCount.get() + 1);

                switch (task.getType()) {

                    case DEADLINE: {
                        Date date =((Deadline) task).getDate();
                        boolean isSameYear = checkIfTwoDatesOfSameYear(date, today);
                        if (isSameYear) {
                            dateString = getSameYearDeadlineDateFormat(date);
                        } else {
                            dateString = getDifferentYearDeadlineDateFormat(date);
                        }
                        addDoneTask(taskCountFormatted.get(), task, dateString);
                        break;
                    }

                    case EVENT: {
                        Date startDate = ((Event) task).getStartDate();
                        Date endDate = ((Event) task).getEndDate();
                        boolean isStartSameYear = checkIfTwoDatesOfSameYear(startDate, today);
                        if (isStartSameYear) {
                            if (checkIfStartAndEndSameDay(startDate, endDate)) {
                                dateString = getSameYearSameDayEventDateFormat(startDate, endDate);
                            } else {
                                dateString = getSameYearDifferentDayEventDateFormat(startDate, endDate);
                            }
                        } else {
                            dateString = getDifferentYearEventDateFormat(startDate, endDate);
                        }
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

        checkIfMainVBoxIsEmpty(this.mainVBox);
    }

    @Override
    protected SequentialTransition getScreenSwitchOutSequence() {
        SequentialTransition sequentialTransition = new SequentialTransition();
//        sequentialTransition.getChildren().add(generateFadeOutTransition(doneNode, TIME_TRANSITION_FADE));
        return sequentialTransition;
    }

    @Override
    protected SequentialTransition getScreenSwitchInSequence() {
        SequentialTransition sequentialTransition = new SequentialTransition();
//        sequentialTransition.getChildren().add(generateFadeInTransition(doneNode, TIME_TRANSITION_FADE));
        return sequentialTransition;
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
        mainVBox.getChildren().clear();
        doneTaskList.getChildren().clear();
    }

    private void getUpdatedDates() {
        updateDates();
        today = getToday();
    }

    private void checkIfMainVBoxIsEmpty(VBox mainVBox) {
        if (FX_BACKGROUND_IMAGE_NO_DONE_TASKS == null) {
            String image = DoneScreen.class.getResource(LOCATION_EMPTY_VIEW).toExternalForm();
            FX_BACKGROUND_IMAGE_NO_DONE_TASKS = "-fx-background-image: url('" + image + "');";
        }
        if (doneTaskList.getChildren().isEmpty()) {
            mainVBox.getChildren().remove(doneNode);
            mainVBox.setStyle(FX_BACKGROUND_IMAGE_NO_DONE_TASKS);
        }
    }
}
