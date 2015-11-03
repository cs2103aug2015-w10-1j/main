package procrastinate.ui;

import java.util.List;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import procrastinate.task.Task;

public class SummaryScreen extends MultiCategoryScreen {

    // ================================================================================
    // Message strings
    // ================================================================================

    private static final int CATEGORY_MAX_CHILD_DREAMS = 2;
    private static final int CATEGORY_MAX_CHILD_FUTURE = 2;
    private static final int CATEGORY_MAX_CHILD_OVERDUE = 3;
    private static final int CATEGORY_MAX_CHILD_UPCOMING = 2;

    private static final int MAX_SUMMARY_COUNT = 21;
    private static final int SUMMARY_HEADER_SIZE_COUNT = 2;
    private static final int SUMMARY_NORMAL_SIZE_COUNT = 1;

    private static final String ELLIPSIS_STRING = "... ";
    private static final String ELLIPSIS_MESSAGE_TASKS_HIDDEN = " tasks hidden ...";
    private static final String ELLIPSIS_MESSAGE_TASK_HIDDEN = " task hidden ...";

    private static final String FX_BACKGROUND_IMAGE_NO_TASKS = "-fx-background-image: url('/procrastinate/ui/images/no-tasks.png')";

    // ================================================================================
    // Class variables
    // ================================================================================

    private int summaryCount;

    // ================================================================================
    // SummaryScreen Constructor
    // ================================================================================

    protected SummaryScreen(String filePath) {
        super(filePath);
    }

    // ================================================================================
    // SummaryScreen methods
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
        setupSummaryView(taskList);
    }

    @Override
    protected void setBackgroundImageIfMainVBoxIsEmpty(VBox mainVBox) {
        if (mainVBox.getChildren().isEmpty()) {
            mainVBox.setStyle(FX_BACKGROUND_IMAGE_NO_TASKS);
        }
    }

    /**
     * Shows the summary view that limits the number of tasks in each category,
     * with the limits being declared in an int array 'summaryCount'. Each
     * category that does not use up its limit will roll over the additional
     * limit to the next category to optimise the space usage.
     *
     * @param taskList
     *            to build the summary view from
     */
    private void setupSummaryView(List<Task> taskList) {
        // need to reset summary count;
        summaryCount = MAX_SUMMARY_COUNT;

        if (mainVBox.getChildren().contains(overdueNode)) {
            adjustMaxChildInCategory(overdueTaskList, CATEGORY_MAX_CHILD_OVERDUE);
        }

        if (mainVBox.getChildren().contains(futureNode)) {
            adjustMaxChildInCategory(futureTaskList, CATEGORY_MAX_CHILD_FUTURE);
        }

        if (mainVBox.getChildren().contains(dreamsNode)) {
            adjustMaxChildInCategory(dreamsTaskList, CATEGORY_MAX_CHILD_DREAMS);
        }

        if (mainVBox.getChildren().contains(upcomingNode)) {
            adjustUpcomingCategoryChildren();
        }
    }

    private void adjustUpcomingCategoryChildren() {
        int totalSubcategoryDisplayed = 0;
        int totalTasksInSubcategories = 0;
        checkUpcomingSubcategoriesAndTasks(totalSubcategoryDisplayed, totalTasksInSubcategories);

        if (summaryCount < 0) {
            int subcategoryCount = 0;
            int numTaskLeft = 0;
            int removeIndex = -1;

            for (int i = 0; i < upcomingSubcategories.size(); i++) {
                VBox currSubcategory = upcomingSubcategories.get(i);
                if ((subcategoryCount == CATEGORY_MAX_CHILD_UPCOMING) && (currSubcategory.getChildren().size() > 0)) {
                    numTaskLeft += currSubcategory.getChildren().size();
                    if (removeIndex == -1) {
                        removeIndex = i;
                    }
                    continue;
                }

                if (currSubcategory.getChildren().size() > 0) {
                    subcategoryCount++;
                }
            }

            if (removeIndex != -1) {
                upcomingTaskList.getChildren().remove(removeIndex, upcomingTaskList.getChildren().size());
            }

            HBox ellipsis = buildEllipsis(numTaskLeft);
            upcomingTaskList.getChildren().add(ellipsis);
        }
    }

    private void checkUpcomingSubcategoriesAndTasks(int totalSubcategoryDisplayed, int totalTasksInSubcategories) {
        for (int i = 0; i < upcomingSubcategories.size(); i++) {
            VBox currSubcategory = upcomingSubcategories.get(i);
            if (currSubcategory.getChildren().size() > 0) {
                totalSubcategoryDisplayed++;
                totalTasksInSubcategories += currSubcategory.getChildren().size();
            }
        }
        summaryCount -= totalSubcategoryDisplayed * SUMMARY_HEADER_SIZE_COUNT;
        summaryCount -= totalTasksInSubcategories * SUMMARY_NORMAL_SIZE_COUNT;
    }

    private void adjustMaxChildInCategory(VBox categoryTaskList, int maxChildSize) {
        int numTaskLeft;
        summaryCount -= SUMMARY_HEADER_SIZE_COUNT;
        if (categoryTaskList.getChildren().size() > maxChildSize) {
            numTaskLeft = categoryTaskList.getChildren().size() - maxChildSize;
            categoryTaskList.getChildren().remove(maxChildSize, categoryTaskList.getChildren().size());
            HBox ellipsis = buildEllipsis(numTaskLeft);
            categoryTaskList.getChildren().add(ellipsis);
            summaryCount -= SUMMARY_NORMAL_SIZE_COUNT;
        }
        summaryCount -= categoryTaskList.getChildren().size() * SUMMARY_NORMAL_SIZE_COUNT;
    }

    private HBox buildEllipsis(int numTaskLeft) {
        String message = ELLIPSIS_STRING + numTaskLeft;
        if (numTaskLeft > 1) {
            message += ELLIPSIS_MESSAGE_TASKS_HIDDEN;
        } else {
            message += ELLIPSIS_MESSAGE_TASK_HIDDEN;
        }
        Label ellipsisMessage = new Label(message);
        HBox ellipsisBox = new HBox(ellipsisMessage);
        ellipsisBox.setAlignment(Pos.CENTER);
        return ellipsisBox;
    }
}
