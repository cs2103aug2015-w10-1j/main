package procrastinate.ui;

import java.util.List;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import procrastinate.task.Task;

public class SummaryScreen extends MultiCategoryScreen {

    // ================================================================================
    // Message strings
    // ================================================================================

    private static final int CATEGORY_MAX_CHILD_DREAMS = 1;
    private static final int CATEGORY_MAX_CHILD_FUTURE = 2;
    private static final int CATEGORY_MAX_CHILD_OVERDUE = 2;
    private static final int CATEGORY_MAX_CHILD_UPCOMING = 2;

    private static final int UPCOMING_SUBCATEGORY_ONE_MAX_CHILD = 2;
    private static final int UPCOMING_SUBCATEGORY_TWO_MAX_CHILD = 2;

    private static final int MAX_SUMMARY_COUNT = 21;
    private static final int SUMMARY_HEADER_SIZE_COUNT = 2;
    private static final int SUMMARY_NORMAL_SIZE_COUNT = 1;

    private static final int TIME_STRING_POSITION = 2;

    private static final String ELLIPSIS_STRING = "... ";
    private static final String ELLIPSIS_MESSAGE_TASKS_HIDDEN = " tasks hidden ...";
    private static final String ELLIPSIS_MESSAGE_TASK_HIDDEN = " task hidden ...";

    private static final String FX_BACKGROUND_IMAGE_NO_TASKS = "-fx-background-image: url('/procrastinate/ui/images/no-tasks.png')";

    private static final String LINE_BREAK = "\n";
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
     * Shows the summary view that limits the number of tasks in each category
     * The exact number of lines given to each category is declared as a final int variable.
     * This just takes into account the height of the time stamp and does not take into
     * regard the length of the description given, assuming that most descriptions are short.
     *
     * @param taskList to build the summary view from
     */
    private void setupSummaryView(List<Task> taskList) {
        // need to reset summary count which is used to check how much the 'Upcoming' category should be summarised.
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

    /**
     * Used for all other categories except 'Upcoming'. The exact number of lines given to each category
     * is passed in as maxChildSize.
     * @param categoryTaskList the category's VBox to be summarised
     * @param maxChildSize int declared to take into account the number of lines each category should have.
     */
    private void adjustMaxChildInCategory(VBox categoryTaskList, int maxChildSize) {
        int numTaskLeft;
        summaryCount -= SUMMARY_HEADER_SIZE_COUNT;
        if (categoryTaskList.getChildren().size() > maxChildSize) {
            numTaskLeft = categoryTaskList.getChildren().size();

            categoryTaskList.getChildren().remove(maxChildSize, categoryTaskList.getChildren().size());
            checkForMultiLineEvents(categoryTaskList);
            numTaskLeft -= categoryTaskList.getChildren().size();

            HBox ellipsis = buildEllipsis(numTaskLeft);
            categoryTaskList.getChildren().add(ellipsis);
            summaryCount -= SUMMARY_NORMAL_SIZE_COUNT;
        }
        summaryCount -= categoryTaskList.getChildren().size() * SUMMARY_NORMAL_SIZE_COUNT;
    }

    /**
     * Used specially for the 'Upcoming' category to limit its number of subcategories to at most 2.
     * The number of lines given to each subcategory is different and is declared above as a final int.
     * The first subcategory shown will have its own ellipsis while the second will share one with all
     * other subcategories.
     */
    private void adjustUpcomingCategoryChildren() {
        int totalSubcategoryDisplayed = 0;
        int totalTasksInSubcategories = 0;
        checkUpcomingSubcategoriesAndTasks(totalSubcategoryDisplayed, totalTasksInSubcategories);

        if (summaryCount < 0) {
            // Only summarise the view in this category if the number is less than 0. Else it should still fit on screen.
            int subcategoryCount = 0;
            int numTaskLeft = 0;
            int indexToRemoveFrom = -1;
            int firstSubcategoryIndex = -1;
            int secondSubcategoryIndex = -1;

            for (int i = 0; i < upcomingSubcategories.size(); i++) {
                VBox currSubcategory = upcomingSubcategories.get(i);
                if ((subcategoryCount == CATEGORY_MAX_CHILD_UPCOMING) && (currSubcategory.getChildren().size() > 0)) {
                    numTaskLeft += currSubcategory.getChildren().size();
                    if (indexToRemoveFrom == -1) {
                        indexToRemoveFrom = i;
                    }
                    continue;
                }

                if (currSubcategory.getChildren().size() > 0) {
                    subcategoryCount++;
                    // Keep track of the index for retrieval later on
                    if (firstSubcategoryIndex == -1) {
                        firstSubcategoryIndex = i;
                    } else if ((firstSubcategoryIndex != -1) && (secondSubcategoryIndex == -1)) {
                        secondSubcategoryIndex = i;
                    }
                }
            }

            // Adjust the first and second subcategory if they are available.
            numTaskLeft = adjustSecondSubcategoryChildren(subcategoryCount, numTaskLeft, secondSubcategoryIndex);
            adjustFirstSubcategoryChildren(subcategoryCount, firstSubcategoryIndex);

            // Removes the third subcategory onwards if available.
            if (indexToRemoveFrom != -1) {
                upcomingTaskList.getChildren().remove(indexToRemoveFrom, upcomingTaskList.getChildren().size());
            }

            HBox ellipsis = buildEllipsis(numTaskLeft);
            upcomingTaskList.getChildren().add(ellipsis);
        }
    }

    private int adjustSecondSubcategoryChildren(int subcategoryCount, int numTaskLeft, int secondSubcategoryIndex) {
        if (subcategoryCount > 1) {
            VBox secondSubcategory = upcomingSubcategories.get(secondSubcategoryIndex);
            if (secondSubcategory.getChildren().size() > UPCOMING_SUBCATEGORY_TWO_MAX_CHILD) {
                numTaskLeft += secondSubcategory.getChildren().size();
                secondSubcategory.getChildren().remove(UPCOMING_SUBCATEGORY_TWO_MAX_CHILD, secondSubcategory.getChildren().size());
                checkForMultiLineEvents(secondSubcategory);
                numTaskLeft -= secondSubcategory.getChildren().size();
            }
        }
        return numTaskLeft;
    }

    private void adjustFirstSubcategoryChildren(int subcategoryCount, int firstSubcategoryIndex) {
        if (subcategoryCount > 0) {
            VBox firstSubcategory = upcomingSubcategories.get(firstSubcategoryIndex);
            if (firstSubcategory.getChildren().size() > UPCOMING_SUBCATEGORY_ONE_MAX_CHILD) {
                int firstSubcategoryTaskLeft = 0;
                firstSubcategoryTaskLeft += firstSubcategory.getChildren().size();
                firstSubcategory.getChildren().remove(UPCOMING_SUBCATEGORY_ONE_MAX_CHILD, firstSubcategory.getChildren().size());
                checkForMultiLineEvents(firstSubcategory);
                firstSubcategoryTaskLeft -= firstSubcategory.getChildren().size();

                HBox ellipsis = buildEllipsis(firstSubcategoryTaskLeft);
                firstSubcategory.getChildren().add(ellipsis);
            }
        }
    }

    /**
     * Counts the total number of tasks and subcategories available in the current display to determine if
     * the category should be summarised.
     * @param totalSubcategoryDisplayed
     * @param totalTasksInSubcategories
     */
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

    private void checkForMultiLineEvents(VBox categoryTaskList) {
        // Check for events that spans 2 lines
        boolean isMultiLineEventFound = false;
        for (int i=0; i<categoryTaskList.getChildren().size(); i++) {
            if (((GridPane)categoryTaskList.getChildren().get(i)).getChildren().get(TIME_STRING_POSITION).toString().contains(LINE_BREAK)) {
                isMultiLineEventFound = true;
                break;
            }
        }
        if (isMultiLineEventFound) {
            // Remove the last task found
            categoryTaskList.getChildren().remove(categoryTaskList.getChildren().size()-1);
        }
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
