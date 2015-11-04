//@@author A0121597B
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

    private static final int CATEGORY_MAX_CHILD_DREAMS = 2;
    private static final int CATEGORY_MAX_CHILD_FUTURE = 2;
    private static final int CATEGORY_MAX_CHILD_OVERDUE = 3;
    private static final int CATEGORY_MAX_CHILD_UPCOMING = 2;

    private static final int UPCOMING_SUBCATEGORY_ONE_MAX_CHILD = 3;
    private static final int UPCOMING_SUBCATEGORY_TWO_MAX_CHILD = 2;
    private static final int UPCOMING_SUBCATEGORY_TO_REMOVE_FROM_INDEX = 2;

    private static final int MAX_SUMMARY_COUNT = 23;
    private static final int SUMMARY_HEADER_SIZE_COUNT = 2;
    private static final int SUMMARY_NORMAL_SIZE_COUNT = 1;

    private static final int MAX_DESCRIPTION_CHAR_COUNT_IN_ONE_LINE_OTHERS = 25;
    private static final int MAX_DESCRIPTION_CHAR_COUNT_IN_ONE_LINE_DREAMS = 40;

    private static final int POSITION_DESCRIPTION_STRING = 1;
    private static final int POSITION_TIME_STRING = 2;

    private static final String ELLIPSIS_STRING = "... ";
    private static final String ELLIPSIS_MESSAGE_TASKS_HIDDEN = " tasks hidden ...";
    private static final String ELLIPSIS_MESSAGE_TASK_HIDDEN = " task hidden ...";

    private static final String FX_BACKGROUND_IMAGE_NO_TASKS = "-fx-background-image: url('/procrastinate/ui/images/no-summary.png')";

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
     * The exact number of lines given to each category is declared as a final
     * int variables above.
     *
     * @param taskList
     *            to build the summary view from by removing tasks if needed.
     */
    private void setupSummaryView(List<Task> taskList) {
        // Summary count is reset at each call just in case the screen is
        // reused.
        // It is used to check how much the 'Upcoming' category should be
        // summarised.
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
     * Used for all other categories except 'Upcoming'. The exact number of
     * lines given to each category is passed in as maxChildSize.
     *
     * @param categoryTaskList
     *            the category's VBox to be summarised
     * @param maxChildSize
     *            int declared to take into account the number of lines each
     *            category should have.
     */
    private void adjustMaxChildInCategory(VBox categoryTaskList, int maxChildSize) {
        int numTaskLeft;
        summaryCount -= SUMMARY_HEADER_SIZE_COUNT;
        if (categoryTaskList.getChildren().size() > maxChildSize) {
            numTaskLeft = categoryTaskList.getChildren().size();

            categoryTaskList.getChildren().remove(maxChildSize, categoryTaskList.getChildren().size());
            if (categoryTaskList == dreamsTaskList) {
                checkForMultiLineEvents(categoryTaskList, MAX_DESCRIPTION_CHAR_COUNT_IN_ONE_LINE_DREAMS);
            } else {
                checkForMultiLineEvents(categoryTaskList, MAX_DESCRIPTION_CHAR_COUNT_IN_ONE_LINE_OTHERS);
            }
            numTaskLeft -= categoryTaskList.getChildren().size();

            HBox ellipsis = buildEllipsis(numTaskLeft);
            categoryTaskList.getChildren().add(ellipsis);
            summaryCount -= SUMMARY_NORMAL_SIZE_COUNT;
        }
        summaryCount -= categoryTaskList.getChildren().size() * SUMMARY_NORMAL_SIZE_COUNT;
    }

    /**
     * Used specially for the 'Upcoming' category to limit its number of
     * subcategories to at most CATEGORY_MAX_CHILD_UPCOMING. The number of lines
     * given to each subcategory can be different and is declared above as a
     * final int. The first subcategory shown will have its own ellipsis while
     * the second will share one with all other subcategories which shows the
     * entire remaining task count in the 'Upcoming' category.
     */
    private void adjustUpcomingCategoryChildren() {
        int totalSubcategoryDisplayed = 0;
        int totalTasksInSubcategories = 0;
        checkUpcomingSubcategoriesAndTasks(totalSubcategoryDisplayed, totalTasksInSubcategories);
        if (summaryCount < 0) {
            // Only summarise the view in this category if the number is less
            // than -3. Else it should still fit on screen.
            int subcategoryCount = 0;
            int numTaskLeft = 0;
            boolean isExtraChildToRemove = false;
            int firstSubcategoryIndex = -1;
            int secondSubcategoryIndex = -1;

            for (int i = 0; i < upcomingSubcategories.size(); i++) {
                VBox currSubcategory = upcomingSubcategories.get(i);
                if ((subcategoryCount == CATEGORY_MAX_CHILD_UPCOMING) && (currSubcategory.getChildren().size() > 0)) {
                    numTaskLeft += currSubcategory.getChildren().size();
                    if (!isExtraChildToRemove) {
                        isExtraChildToRemove = true;
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
            if (isExtraChildToRemove) {
                upcomingTaskList.getChildren().remove(UPCOMING_SUBCATEGORY_TO_REMOVE_FROM_INDEX, upcomingTaskList.getChildren().size());
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
                secondSubcategory.getChildren().remove(UPCOMING_SUBCATEGORY_TWO_MAX_CHILD,
                        secondSubcategory.getChildren().size());
                checkForMultiLineEvents(secondSubcategory, MAX_DESCRIPTION_CHAR_COUNT_IN_ONE_LINE_OTHERS);
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
                firstSubcategory.getChildren().remove(UPCOMING_SUBCATEGORY_ONE_MAX_CHILD,
                        firstSubcategory.getChildren().size());
                checkForMultiLineEvents(firstSubcategory, MAX_DESCRIPTION_CHAR_COUNT_IN_ONE_LINE_OTHERS);
                firstSubcategoryTaskLeft -= firstSubcategory.getChildren().size();

                HBox ellipsis = buildEllipsis(firstSubcategoryTaskLeft);
                firstSubcategory.getChildren().add(ellipsis);
            }
        }
    }

    /**
     * Counts the total number of tasks and subcategories available in the
     * 'Upcoming' category of the current display to determine if the category
     * should be summarised.
     *
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

    /**
     * Checks the given categoryTaskList that has been reduced to the maximum
     * number of child in the category for any multi-line TaskEntry displays. If
     * one such task is found, the last child in the category will be removed.
     *
     * @param categoryTaskList
     */
    private void checkForMultiLineEvents(VBox categoryTaskList, int maxSingleLineCharCount) {
        boolean isMultiLineEventFound = false;
        for (int i = 0; i < categoryTaskList.getChildren().size(); i++) {
            if (((GridPane) categoryTaskList.getChildren().get(i)).getChildren().get(POSITION_TIME_STRING).toString()
                    .contains(LINE_BREAK)) {
                isMultiLineEventFound = true;
                break;
            } else {
                GridPane taskEntry = ((GridPane) categoryTaskList.getChildren().get(i));
                Label descriptionLabel = ((Label) (taskEntry.getChildren().get(POSITION_DESCRIPTION_STRING)));
                if (descriptionLabel.getText().length() > maxSingleLineCharCount) {
                    isMultiLineEventFound = true;
                    break;
                }
            }
        }
        if (isMultiLineEventFound) {
            categoryTaskList.getChildren().remove(categoryTaskList.getChildren().size() - 1);
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
