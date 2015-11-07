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

public class SummaryScreen extends MultiCategoryScreen {

    // ================================================================================
    // Message strings
    // ================================================================================

    private static final String DESCRIPTION_ELLIPSIS_STRING = "...";

    private static final String ELLIPSIS_STRING = "... ";
    private static final String ELLIPSIS_MESSAGE_TASKS_HIDDEN = " tasks hidden ...";
    private static final String ELLIPSIS_MESSAGE_TASK_HIDDEN = " task hidden ...";

    private static final String FX_BACKGROUND_IMAGE_NO_TASKS = "-fx-background-image: url('/procrastinate/ui/images/no-summary.png')";

    private static final String SELECTOR_SCROLLPANE = "#scrollPane";

    private static final double SCROLLPANE_TOTAL_TOP_BOTTOM_PADDING = 20.0;

    private static final double MAINVBOX_CHILDREN_SPACING = 5.0;

    private static final int PARTITION_COUNT_OVERDUE = 2;
    private static final int PARTITION_COUNT_UPCOMING = 3;
    private static final int PARTITION_COUNT_FUTURE = 1;
    private static final int PARTITION_COUNT_DREAMS = 1;

    private static final int TEST_ELLIPSIS_COUNT = 666;

    private static final int DESCRIPTION_MAX_LENGTH = 50;
    private static final int DESCRIPTION_ELLIPSIS_LENGTH = 3;

    // ================================================================================
    // Class variables
    // ================================================================================

    private static double ellipsisBoxHeight = -1;

    private boolean isSummarising = false;

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

            addTaskByType(shortenTaskDescription(task));
        }
        updateDisplay();
        setupSummaryView();
    }

    protected boolean isSummarising() {
        return isSummarising;
    }

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
    private void setupSummaryView() {
        double currMainVBoxHeight = getCurrentMainVBoxHeight();
        double maxMainVBoxHeight = ((ScrollPane) getNode().lookup(SELECTOR_SCROLLPANE)).getHeight();

        if (currMainVBoxHeight < maxMainVBoxHeight) {
            isSummarising = false;
            return;
        }

        isSummarising = true;
        resizeScreenToFit(maxMainVBoxHeight);
    }

    /**
     * Calculates the number of partitions to split the screen in order to fit
     * the maxMainVBoxHeight and resizes each category to a given size depending
     * on their PartitionCount declared above.
     *
     * @param maxMainVBoxHeight
     *            the height to resize the screen to fit into
     */
    private void resizeScreenToFit(double maxMainVBoxHeight) {
        int numCategoriesPresent = calculateNumberOfCategoriesPresent();
        int numPartitionsToSplit = calculateNumberOfPartitionsToSplit();

        double remainingHeightForTaskDisplay = maxMainVBoxHeight - SCROLLPANE_TOTAL_TOP_BOTTOM_PADDING
                - ((numCategoriesPresent - 1) * MAINVBOX_CHILDREN_SPACING);
        double singlePartitionHeight = remainingHeightForTaskDisplay / numPartitionsToSplit;

        double rollOverHeight = 0;

        HeightNodePair overdueCategoryHeightNodePair = new HeightNodePair(getHeightOfCategoryNode(overdueNode), overdueNode);
        HeightNodePair upcomingCategoryHeightNodePair = new HeightNodePair(getHeightOfCategoryNode(upcomingNode), upcomingNode);
        HeightNodePair futureCategoryHeightNodePair = new HeightNodePair(getHeightOfCategoryNode(futureNode), futureNode);
        HeightNodePair dreamsCategoryHeightNodePair = new HeightNodePair(getHeightOfCategoryNode(dreamsNode), dreamsNode);
        HeightNodePair[] allCategoryHeightNodePair = { overdueCategoryHeightNodePair, upcomingCategoryHeightNodePair,
                futureCategoryHeightNodePair, dreamsCategoryHeightNodePair };

        Arrays.sort(allCategoryHeightNodePair);

        for (HeightNodePair currHeightNodePair : allCategoryHeightNodePair) {
            double currCategoryHeight = currHeightNodePair.getHeight();
            Node currCategoryNode = currHeightNodePair.getNode();
            if (currCategoryHeight == 0) {
                continue;
            }
            if (currCategoryNode == overdueNode) {
                double maxOverdueCategoryHeight = (singlePartitionHeight * PARTITION_COUNT_OVERDUE) + rollOverHeight;
                rollOverHeight = 0;
                if (currCategoryHeight > maxOverdueCategoryHeight) {
                    rollOverHeight += resizeTaskListOfOtherCategoriesToFit(overdueNode, maxOverdueCategoryHeight);
                } else {
                    rollOverHeight += maxOverdueCategoryHeight - currCategoryHeight;
                }
            } else if (currCategoryNode == upcomingNode) {
                double maxUpcomingCategoryHeight = (singlePartitionHeight * PARTITION_COUNT_UPCOMING) + rollOverHeight;
                rollOverHeight = 0;
                if (currCategoryHeight > maxUpcomingCategoryHeight) {
                    rollOverHeight += resizeTaskListOfUpcomingCategoryToFit(upcomingNode, maxUpcomingCategoryHeight);
                } else {
                    rollOverHeight += maxUpcomingCategoryHeight - currCategoryHeight;
                }
            } else if (currCategoryNode == futureNode) {
                double maxFutureCategoryHeight = (singlePartitionHeight * PARTITION_COUNT_FUTURE) + rollOverHeight;
                rollOverHeight = 0;
                if (currCategoryHeight > maxFutureCategoryHeight) {
                    rollOverHeight += resizeTaskListOfOtherCategoriesToFit(futureNode, maxFutureCategoryHeight);
                } else {
                    rollOverHeight += maxFutureCategoryHeight - currCategoryHeight;
                }
            } else if (currCategoryNode == dreamsNode) {
                double maxDreamsCategoryHeight = (singlePartitionHeight * PARTITION_COUNT_DREAMS) + rollOverHeight;
                rollOverHeight = 0;
                if (currCategoryHeight > maxDreamsCategoryHeight) {
                    rollOverHeight += resizeTaskListOfOtherCategoriesToFit(dreamsNode, maxDreamsCategoryHeight);
                } else {
                    rollOverHeight += maxDreamsCategoryHeight - currCategoryHeight;
                }
            } else {
                continue;
            }
        }
    }

    private double resizeTaskListOfUpcomingCategoryToFit(Node upcomingNode, double heightToFit) {
        if (ellipsisBoxHeight == -1) {
            updateEllipsisBoxHeight(upcomingNode);
        }
        assert(ellipsisBoxHeight != -1);

        int numTasksRemoved = 0;
        int totalSubcategoryCount = upcomingSubcategories.size() - 1;

        for (int i = totalSubcategoryCount; i >= 0; i--) {
            VBox currSubcategory = upcomingSubcategories.get(i);
            if (currSubcategory.getChildren().isEmpty()) {
                continue;
            }
            while (!currSubcategory.getChildren().isEmpty()
                    && (getHeightOfCategoryNode(upcomingNode) > (heightToFit - ellipsisBoxHeight))) {
                currSubcategory.getChildren().remove(currSubcategory.getChildren().size() - 1);
                numTasksRemoved++;
                mainVBox.getParent().layout();
            }
            if (currSubcategory.getChildren().isEmpty()) {
                upcomingTaskList.getChildren().remove(upcomingTaskList.getChildren().size() - 1);
            }
            if (getHeightOfCategoryNode(upcomingNode) < (heightToFit - ellipsisBoxHeight)) {
                break;
            }
        }

        if (numTasksRemoved != 0) {
            upcomingTaskList.getChildren().add(buildEllipsis(numTasksRemoved));
            upcomingTaskList.applyCss();
            upcomingTaskList.layout();
            mainVBox.getParent().layout();
        }
        return (heightToFit - getHeightOfCategoryNode(upcomingNode));
    }

    private double resizeTaskListOfOtherCategoriesToFit(Node categoryNode, double heightToFit) {
        int numTasksRemoved = 0;
        if (ellipsisBoxHeight == -1) {
            updateEllipsisBoxHeight(categoryNode);
        }
        assert(ellipsisBoxHeight != -1);

        VBox currCategoryTaskList = getCategoryTaskList(categoryNode);
        while (getHeightOfCategoryNode(categoryNode) > (heightToFit - ellipsisBoxHeight)) {
            currCategoryTaskList.getChildren().remove(currCategoryTaskList.getChildren().size() - 1);
            numTasksRemoved++;
            mainVBox.getParent().layout();
        }

        if (numTasksRemoved != 0) {
            currCategoryTaskList.getChildren().add(buildEllipsis(numTasksRemoved));
            currCategoryTaskList.applyCss();
            currCategoryTaskList.layout();
            mainVBox.getParent().layout();
        }
        return (heightToFit - getHeightOfCategoryNode(categoryNode));
    }

    /**
     * Used to check the height of the ellipsis added which may differ from user
     * to user depending on hardware specifications.
     *
     * @param categoryNode
     *            any Node of a category that is not empty in order to calculate
     *            the height difference when an ellipsis is added.
     */
    private void updateEllipsisBoxHeight(Node categoryNode) {
        double currHeight = getHeightOfCategoryNode(categoryNode);

        VBox currCategoryTaskList = getCategoryTaskList(categoryNode);
        currCategoryTaskList.getChildren().add(buildEllipsis(TEST_ELLIPSIS_COUNT));
        currCategoryTaskList.applyCss();
        currCategoryTaskList.layout();
        mainVBox.getParent().layout();

        currCategoryTaskList.getChildren().remove(currCategoryTaskList.getChildren().size() - 1);

        double newHeight = getHeightOfCategoryNode(categoryNode);
        ellipsisBoxHeight = newHeight - currHeight;
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

    private double getCurrentMainVBoxHeight() {
        mainVBox.getParent().applyCss();
        mainVBox.getParent().layout();
        return mainVBox.getHeight();
    }

    private double getHeightOfCategoryNode(Node categoryNode) {
        return ((VBox) categoryNode).getHeight();
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
