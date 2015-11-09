//@@author A0121597B
package procrastinate.ui;

import java.util.Arrays;
import java.util.List;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import procrastinate.task.Task;

/**
 * <h1>SummaryScreen is a subclass of MultiCategoryScreen and is used to show an overview of
 * the pending tasks.</h1>
 *
 * It resizes all the pending tasks to fit into the screen without scrolling by removing
 * tasks from view and replacing them with ellipses.
 */
public class SummaryScreen extends MultiCategoryScreen {

    // ================================================================================
    // Message Strings
    // ================================================================================

    private static final String DESCRIPTION_ELLIPSIS_STRING = "...";

    private static final String ELLIPSIS_STRING = "... ";
    private static final String ELLIPSIS_MESSAGE_TASKS_HIDDEN = " tasks hidden ...";
    private static final String ELLIPSIS_MESSAGE_TASK_HIDDEN = " task hidden ...";

    private static final String FX_BACKGROUND_IMAGE_NO_TASKS = "-fx-background-image: url('/procrastinate/ui/images/no-summary.png')";

    private static final String SELECTOR_SCROLLPANE = "#scrollPane";

    // ================================================================================
    // Constants
    // ================================================================================

    private static final double SCROLLPANE_TOTAL_TOP_BOTTOM_PADDING = 20.0;

    private static final double MAINVBOX_CHILDREN_SPACING = 5.0;

    private static final int DESCRIPTION_MAX_LENGTH = 50;
    private static final int DESCRIPTION_ELLIPSIS_LENGTH = 3;

    private static final int PARTITION_COUNT_OVERDUE = 2;
    private static final int PARTITION_COUNT_UPCOMING = 3;
    private static final int PARTITION_COUNT_FUTURE = 1;
    private static final int PARTITION_COUNT_DREAMS = 1;

    private static final int TEST_ELLIPSIS_COUNT = 666;

    // ================================================================================
    // Class variables
    // ================================================================================

    private static double ellipsisBoxHeight_ = -1;

    private boolean isSummarising_ = false;

    // ================================================================================
    // SummaryScreen Constructor
    // ================================================================================

    protected SummaryScreen() {
        super();
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

    //@@author A0080485B
    protected boolean isSummarising() {
        return isSummarising_;
    }

    //@@author A0121597B
    @Override
    protected void setBackgroundImageIfMainVBoxIsEmpty(VBox mainVBox) {
        if (mainVBox.getChildren().isEmpty()) {
            mainVBox.setStyle(FX_BACKGROUND_IMAGE_NO_TASKS);
        }
    }

    /**
     * Resizes the current displayed task list to fit into screen without
     * scrolling by removing tasks from view and replacing them with ellipses.
     * The entire screen is partitioned depending on the number of categories
     * present and the ellipses shows the number of tasks hidden in each
     * category.
     */
    //@@author A0080485B
    private void setupSummaryView(List<Task> taskList) {
        double currMainVBoxHeight = getCurrentMainVBoxHeight();
        double maxMainVBoxHeight = ((ScrollPane) getNode().lookup(SELECTOR_SCROLLPANE)).getHeight();

        if (taskList.isEmpty()) {
            isSummarising_ = true;
            return;
        }

        if (currMainVBoxHeight < maxMainVBoxHeight) {
            isSummarising_ = false;
            return;
        }

        isSummarising_ = true;

        // shortenTaskDescription is the first step
        clearTaskList();
        for (Task task : taskList) {
            taskCount.set(taskCount.get() + 1);
            addTaskByType(shortenTaskDescription(task));
        }
        updateDisplay();

        if (getCurrentMainVBoxHeight() < maxMainVBoxHeight) {
            return;
        }

        resizeScreenToFit(maxMainVBoxHeight);
    }

    /**
     * Calculates the number of partitions to split the screen in order to fit
     * the maxMainVBoxHeight and resizes each category to a given size depending
     * on their PartitionCount declared above.
     *
     * @param maxMainVBoxHeight    the height to resize the screen to fit into
     */
    //@@author A0121597B
    private void resizeScreenToFit(double maxMainVBoxHeight) {
        int numCategoriesPresent = calculateNumberOfCategoriesPresent();
        int numPartitionsToSplit = calculateNumberOfPartitionsToSplit();

        double remainingHeightForTaskDisplay = maxMainVBoxHeight -
                                               SCROLLPANE_TOTAL_TOP_BOTTOM_PADDING -
                                               ((numCategoriesPresent - 1) * MAINVBOX_CHILDREN_SPACING);
        double rollOverHeight = 0;
        double singlePartitionHeight = remainingHeightForTaskDisplay / numPartitionsToSplit;

        DoubleNodePair overdueCategoryHeightNodePair = new DoubleNodePair(getHeightOfCategoryNode(overdueNode), overdueNode);
        DoubleNodePair upcomingCategoryHeightNodePair = new DoubleNodePair(getHeightOfCategoryNode(upcomingNode), upcomingNode);
        DoubleNodePair futureCategoryHeightNodePair = new DoubleNodePair(getHeightOfCategoryNode(futureNode), futureNode);
        DoubleNodePair dreamsCategoryHeightNodePair = new DoubleNodePair(getHeightOfCategoryNode(dreamsNode), dreamsNode);
        DoubleNodePair[] allCategoryHeightNodePair = { overdueCategoryHeightNodePair, upcomingCategoryHeightNodePair,
                                                       futureCategoryHeightNodePair, dreamsCategoryHeightNodePair };

        Arrays.sort(allCategoryHeightNodePair);

        for (DoubleNodePair currHeightNodePair : allCategoryHeightNodePair) {
            double currCategoryHeight = currHeightNodePair.getHeight();
            Node currCategoryNode = currHeightNodePair.getNode();

            if (currCategoryHeight == 0) {
                continue;
            }

            if        (currCategoryNode == overdueNode) {
                double maxOverdueCategoryHeight = (singlePartitionHeight * PARTITION_COUNT_OVERDUE) + rollOverHeight;

                rollOverHeight = getLeftoverHeightAfterResize(rollOverHeight, currCategoryHeight,
                                                              maxOverdueCategoryHeight, overdueNode);

            } else if (currCategoryNode == upcomingNode) {
                double maxUpcomingCategoryHeight = (singlePartitionHeight * PARTITION_COUNT_UPCOMING) + rollOverHeight;

                rollOverHeight = getLeftoverHeightAfterResize(rollOverHeight, currCategoryHeight,
                                                              maxUpcomingCategoryHeight, upcomingNode);

            } else if (currCategoryNode == futureNode) {
                double maxFutureCategoryHeight = (singlePartitionHeight * PARTITION_COUNT_FUTURE) + rollOverHeight;

                rollOverHeight = getLeftoverHeightAfterResize(rollOverHeight, currCategoryHeight,
                                                              maxFutureCategoryHeight, futureNode);

            } else if (currCategoryNode == dreamsNode) {
                double maxDreamsCategoryHeight = (singlePartitionHeight * PARTITION_COUNT_DREAMS) + rollOverHeight;

                rollOverHeight = getLeftoverHeightAfterResize(rollOverHeight, currCategoryHeight,
                                                              maxDreamsCategoryHeight, dreamsNode);

            } else {
                continue;
            }
        }
    }

    /**
     * Checks if the current category height exceeds the allocated maximum height and resizes it to fit.
     * It also retrieves the remaining height of each category and returns it as rollOverHeight for other
     * categories to use.
     *
     * @param rollOverHeight        to be updated after each category is resized
     * @param currCategoryHeight    height of the currCategoryNode
     * @param maxCategoryHeight     max height of the currCategoryNode
     * @param currCategoryNode      node to be checked against upcomingNode which is resized differently.
     * @return                      updated rollOverHeight
     */
    private double getLeftoverHeightAfterResize(double rollOverHeight, double currCategoryHeight,
                                                double maxCategoryHeight, Node currCategoryNode) {

        rollOverHeight = 0;
        if        ((currCategoryHeight > maxCategoryHeight) && (currCategoryNode != upcomingNode)) {
            resizeTaskListOfOtherCategoriesToFit(currCategoryNode, maxCategoryHeight);
            rollOverHeight += (maxCategoryHeight - getHeightOfCategoryNode(currCategoryNode));

        } else if ((currCategoryHeight > maxCategoryHeight) && (currCategoryNode == upcomingNode)) {
            resizeTaskListOfUpcomingCategoryToFit(currCategoryNode, maxCategoryHeight);
            rollOverHeight += (maxCategoryHeight - getHeightOfCategoryNode(currCategoryNode));

        } else {
            rollOverHeight += maxCategoryHeight - currCategoryHeight;
        }

        return rollOverHeight;
    }


    // Resizes the upcomingNode if it exceeds the given heightToFit by removing tasks from its
    // subcategories, starting from the last subcategory and removing the SubcategoryBox from
    // view when there are no more tasks contained within.
    private void resizeTaskListOfUpcomingCategoryToFit(Node upcomingNode, double heightToFit) {
        if (ellipsisBoxHeight_ == -1) {
            updateEllipsisBoxHeight(upcomingNode);
        }
        assert(ellipsisBoxHeight_ != -1);

        int numTasksRemoved = 0;
        int totalSubcategoryCount = upcomingSubcategories.size() - 1;

        for (int i = totalSubcategoryCount; i >= 0; i--) {
            VBox currSubcategory = upcomingSubcategories.get(i);
            if (currSubcategory.getChildren().isEmpty()) {
                continue;
            }

            while (!currSubcategory.getChildren().isEmpty() &&
                   (getHeightOfCategoryNode(upcomingNode) > (heightToFit - ellipsisBoxHeight_))) {
                currSubcategory.getChildren().remove(currSubcategory.getChildren().size() - 1);
                numTasksRemoved++;
                mainVBox.getParent().layout();
            }

            if (currSubcategory.getChildren().isEmpty()) {
                upcomingTaskList.getChildren().remove(upcomingTaskList.getChildren().size() - 1);
            }

            if (getHeightOfCategoryNode(upcomingNode) < (heightToFit - ellipsisBoxHeight_)) {
                break;
            }
        }

        if (numTasksRemoved != 0) {
            upcomingTaskList.getChildren().add(getEllipsis(numTasksRemoved));
            upcomingTaskList.applyCss();
            upcomingTaskList.layout();

            mainVBox.getParent().layout();
        }
    }

    // Resizes the given categoryNode by removing tasks from the category if it exceeds the given heightToFit.
    private void resizeTaskListOfOtherCategoriesToFit(Node categoryNode, double heightToFit) {
        int numTasksRemoved = 0;
        if (ellipsisBoxHeight_ == -1) {
            updateEllipsisBoxHeight(categoryNode);
        }
        assert(ellipsisBoxHeight_ != -1);

        VBox currCategoryTaskList = getCategoryTaskList(categoryNode);
        while (getHeightOfCategoryNode(categoryNode) > (heightToFit - ellipsisBoxHeight_)) {
            currCategoryTaskList.getChildren().remove(currCategoryTaskList.getChildren().size() - 1);
            numTasksRemoved++;
            mainVBox.getParent().layout();
        }

        if (numTasksRemoved != 0) {
            currCategoryTaskList.getChildren().add(getEllipsis(numTasksRemoved));
            currCategoryTaskList.applyCss();
            currCategoryTaskList.layout();

            mainVBox.getParent().layout();
        }
    }

    /**
     * Used to check the height of the ellipsis added which may differ from user
     * to user depending on hardware specifications.
     *
     * @param categoryNode    any Node of a category that is not empty in order to calculate
     *                        the height difference when an ellipsis is added.
     */
    private void updateEllipsisBoxHeight(Node categoryNode) {
        double currHeight = getHeightOfCategoryNode(categoryNode);

        VBox currCategoryTaskList = getCategoryTaskList(categoryNode);
        currCategoryTaskList.getChildren().add(getEllipsis(TEST_ELLIPSIS_COUNT));
        currCategoryTaskList.applyCss();
        currCategoryTaskList.layout();

        mainVBox.getParent().layout();

        currCategoryTaskList.getChildren().remove(currCategoryTaskList.getChildren().size() - 1);

        double newHeight = getHeightOfCategoryNode(categoryNode);
        ellipsisBoxHeight_ = newHeight - currHeight;
    }

    private VBox getCategoryTaskList(Node categoryNode) {
        return ((VBox) categoryNode.lookup(SELECTOR_CATEGORY_VBOX));
    }

    private int calculateNumberOfPartitionsToSplit() {
        int numPartitions = 0;

        if (getHeightOfCategoryNode(overdueNode) != 0) {
            numPartitions += PARTITION_COUNT_OVERDUE;
        }

        if (getHeightOfCategoryNode(upcomingNode) != 0) {
            numPartitions += PARTITION_COUNT_UPCOMING;
        }

        if (getHeightOfCategoryNode(futureNode) != 0) {
            numPartitions += PARTITION_COUNT_FUTURE;
        }

        if (getHeightOfCategoryNode(dreamsNode) != 0) {
            numPartitions += PARTITION_COUNT_DREAMS;
        }

        return numPartitions;
    }

    private int calculateNumberOfCategoriesPresent() {
        int numCategories = 0;

        for (Node node : nodeList) {
            if (((VBox) node).getHeight() != 0) {
                numCategories++;
            }
        }

        return numCategories;
    }

    /**
     * Update the mainVBox and allows calculation of it's height after applying
     * the CSS styling and layout.
     *
     * @return    updated height of the mainVBox
     */
    private double getCurrentMainVBoxHeight() {
        mainVBox.getParent().applyCss();
        mainVBox.getParent().layout();
        return mainVBox.getHeight();
    }

    private double getHeightOfCategoryNode(Node categoryNode) {
        return ((VBox) categoryNode).getHeight();
    }

    private HBox getEllipsis(int numTaskLeft) {
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

    //@@author A0080485B
    private Task shortenTaskDescription(Task task) {
        String description = task.getDescription();
        if (description.length() > DESCRIPTION_MAX_LENGTH) {
            task = Task.copy(task);
            task.setDescription(description.substring(0, DESCRIPTION_MAX_LENGTH
                                                         - DESCRIPTION_ELLIPSIS_LENGTH) + DESCRIPTION_ELLIPSIS_STRING);
        }
        return task;
    }
}
