//@@author A0121597B
package procrastinate.ui;

import java.util.Date;
import java.util.List;

import javafx.scene.layout.VBox;
import procrastinate.task.Deadline;
import procrastinate.task.Event;
import procrastinate.task.Task;

/**
 * <h1>DoneScreen is a subclass of the SingleCategoryScreen that shows all
 * tasks which are done in a single CategoryBox.</h1>
 *
 * It will only add tasks which are done into its CategoryBox while ignoring all
 * other tasks passed in via the updateTaskList method.
 */
public class DoneScreen extends SingleCategoryScreen {

    // ================================================================================
    // Message strings
    // ================================================================================

    private static final String HEADER_TEXT = "Your completed tasks";

    private static final String FX_BACKGROUND_IMAGE_NO_DONE_TASKS = "-fx-background-image: url('/procrastinate/ui/images/no-done-tasks.png')";

    // ================================================================================
    // DoneScreen Constructor
    // ================================================================================

    protected DoneScreen() {
        super(HEADER_TEXT);
    }

    // ================================================================================
    // DoneScreen Methods
    // ================================================================================

    @Override
    protected void updateTaskList(List<Task> taskList) {
        getUpdatedDates();
        clearTaskList();

        mainVBox.getChildren().add(thisCategoryNode);

        String dateString;

        for (Task task : taskList) {
            if (task.isDone()) {
                taskCount.set(taskCount.get() + 1);

                switch (task.getType()) {

                    case DEADLINE : {
                        Date date =((Deadline) task).getDate();

                        dateString = getDateFormatForDateline(date);

                        addDoneTask(taskCountFormatted.get(), task, dateString);
                        break;
                    }

                    case EVENT : {
                        Date startDate = ((Event) task).getStartDate();
                        Date endDate = ((Event) task).getEndDate();

                        dateString = getDateFormatForEvent(startDate, endDate);

                        addDoneTask(taskCountFormatted.get(), task, dateString);
                        break;
                    }

                    case DREAM : {
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

        checkIfMainVBoxIsEmpty(mainVBox);
    }

    private void addDoneTask(String taskCount, Task task, String dateString) {
        TaskEntry taskEntry;

        if (dateString == null) {
            taskEntry = new TaskEntry(taskCount, task.getDescription(), task.isDone());
        } else {
            taskEntry = new TaskEntry(taskCount, task.getDescription(), dateString, task.isDone());
        }

        thisCategoryTaskList.getChildren().add(taskEntry.getEntryDisplay());
    }

    private String getDateFormatForDateline(Date date) {
        String dateString;

        boolean isSameYear = isSameYear(date, today);
        if (isSameYear) {
            dateString = getDateFormatForDeadlineWithSameYear(date);

        } else {
            dateString = getDateFormatForDeadlineWithDifferentYear(date);
        }

        return dateString;
    }

    private String getDateFormatForEvent(Date startDate, Date endDate) {
        String dateString;

        boolean isStartSameYear = isSameYear(startDate, today);
        if (isStartSameYear) {
            if (isSameDay(startDate, endDate)) {
                dateString = getDateFormatForEventWithSameYearAndInOneDay(startDate, endDate);

            } else {
                dateString = getDateFormatForEventWithSameYearAndDifferentDays(startDate, endDate);
            }

        } else {
            dateString = getDateFormatForEventWithDifferentYearAndDifferentDays(startDate, endDate);
        }

        return dateString;
    }

    // ================================================================================
    // Utility Methods
    // ================================================================================

    private void checkIfMainVBoxIsEmpty(VBox mainVBox) {
        if (thisCategoryTaskList.getChildren().isEmpty()) {
            mainVBox.getChildren().remove(thisCategoryNode);
            mainVBox.setStyle(FX_BACKGROUND_IMAGE_NO_DONE_TASKS);

        } else {
            setMainVBoxBackgroundImage(mainVBox, FX_BACKGROUND_IMAGE_NULL);
        }
    }
}
