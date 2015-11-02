package procrastinate.ui;

import java.util.List;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import procrastinate.task.Task;

public class SummaryScreen extends MultiCategoryScreen {

    // ================================================================================
    // Message strings
    // ================================================================================

    private static final String LOCATION_EMPTY_VIEW = "images/no-tasks.png";

    private static final String ELLIPSIS_STRING = "... ";
    private static final String ELLIPSIS_MESSAGE_TASKS_HIDDEN = " tasks hidden ...";
    private static final String ELLIPSIS_MESSAGE_TASK_HIDDEN = " task hidden ...";

    private static String FX_BACKGROUND_IMAGE_NO_TASKS; // will be initialised later on.

    // ================================================================================
    // Class variables
    // ================================================================================

    private boolean isShowSummaryView = true;

    private int[] summaryCount = {3,2,2,2,0};   // last '0' used as placeholder

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
        setupSummaryViewIfRequested(taskList);
    }


    /**
     * Shows the summary view that limits the number of tasks in each category, with the limits being
     * declared in an int array 'summaryCount'. Each category that does not use up its limit will roll over the
     * additional limit to the next category to optimise the space usage.
     * @param taskList to build the summary view from
     */
    protected void setupSummaryViewIfRequested(List<Task> taskList) {
        if (isShowSummaryView && taskList.size() > 15) {
            for (int i = 0; i < summaryCount.length-1; i++) {
                int currentMax = summaryCount[i];
                Node currNode = nodeList.get(i);
                if (currNode.equals(overdueNode) && overdueTaskList.getChildren().size() < currentMax) {
                    // For 'Overdue' category, a change to the roll over is done since it will
                    // cause more subcategories to be shown under the 'Upcoming' category
//                    summaryCount[i+1] = summaryCount[i+1] + ((summaryCount[i] - overdueTaskList.getChildren().size())/2);
                    continue;
                } else if (mainVBox.getChildren().contains(currNode)) {
                    VBox currentTaskList = ((VBox) currNode.lookup(SELECTOR_CATEGORY_VBOX));
                    if (currNode.equals(upcomingNode)) {
                        int upcomingMax = currentMax * 3;   // multiplication to create buffer for upcoming tasks
                        // For the 'Upcoming' category, each subcategory header is taken as having two tasks.
                        // Each subcategory will be limited to at most 4 for now as well.
                        int numSubcategory = 0;
                        int endSubcategoryAt = 0;
                        int numTaskLeft = 0;
                        int tasksAdded = 0;
                        boolean ellipsisAdded = false;
                        // There will always be at least 2 subcategories shown, and the tasks will only be
                        // hidden if they have more than 3 tasks in each
                        for (VBox vBox : upcomingSubcategories) {
                            if (numSubcategory == summaryCount[i]) {
                                break;
                            } else {
                                if (vBox.getChildren().size() > 0) {
                                    numSubcategory++;
                                    if (vBox.getChildren().size() > 3) {
                                      numTaskLeft = vBox.getChildren().size() - 3;
                                      vBox.getChildren().subList(3, vBox.getChildren().size()).clear();
                                      HBox ellipsis = buildEllipsis(numTaskLeft);
                                      vBox.getChildren().add(ellipsis);
                                      ellipsisAdded = true;
                                    }
                                    upcomingMax -= vBox.getChildren().size();
                                    tasksAdded += vBox.getChildren().size();
                                }
                                endSubcategoryAt++;
                            }
                        }
                        // In the case that 'Overdue' has no tasks, another subcategory will be shown but with 2 tasks only
                        if (upcomingMax > 0 && tasksAdded < 3) {
                            for (int j=endSubcategoryAt+1; j<upcomingSubcategories.size(); j++) {
                                VBox currSubcategory = upcomingSubcategories.get(j);
                                if (currSubcategory.getChildren().size() > 0) {
                                    endSubcategoryAt = j;
                                    if (currSubcategory.getChildren().size() > 2) {
                                        numTaskLeft = currSubcategory.getChildren().size() - 2;
                                        currSubcategory.getChildren().subList(2, currSubcategory.getChildren().size()).clear();
                                        HBox ellipsis = buildEllipsis(numTaskLeft);
                                        currSubcategory.getChildren().add(ellipsis);
                                        ellipsisAdded = true;
                                    }
                                    break;
                                }
                            }
                        }
                        // Remove the last ellipsis added.
                        VBox prevSubcategory = upcomingSubcategories.get(endSubcategoryAt);
                        if (ellipsisAdded) {
                            prevSubcategory.getChildren().remove(prevSubcategory.getChildren().size()-1);
                        }
                        for (int k=endSubcategoryAt+1; k<upcomingSubcategories.size(); k++) {
                            VBox remainingSubcategory = upcomingSubcategories.get(k);
                            numTaskLeft += remainingSubcategory.getChildren().size();
                        }
                        // Add a new ellipsis that has the number of tasks remaining for the entire category
                        HBox ellipsis = buildEllipsis(numTaskLeft);
                        // Remove the remaining subcategories
                        upcomingTaskList.getChildren().remove(endSubcategoryAt-1, upcomingTaskList.getChildren().size());
                        upcomingTaskList.getChildren().add(ellipsis);
                    }
                    else if ((currentTaskList.getChildren().size()) > currentMax) {
                       int numTaskLeft = currentTaskList.getChildren().size() - currentMax;
                       currentTaskList.getChildren().subList(currentMax, currentTaskList.getChildren().size()).clear();
                       HBox ellipsis = buildEllipsis(numTaskLeft);
                       currentTaskList.getChildren().add(ellipsis);
                    } else {
                        summaryCount[i+1] = summaryCount[i+1] + summaryCount[i] - currentTaskList.getChildren().size();
                    }
                } else {
                    summaryCount[i+1] = summaryCount[i+1] + summaryCount[i];
                }
            }
        }
        isShowSummaryView = false;
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

    @Override
    protected void checkIfMainVBoxIsEmpty(VBox mainVBox) {
        if (FX_BACKGROUND_IMAGE_NO_TASKS == null) {
            String image = MultiCategoryScreen.class.getResource(LOCATION_EMPTY_VIEW).toExternalForm();
            FX_BACKGROUND_IMAGE_NO_TASKS = "-fx-background-image: url('" + image + "');";
        }
        if (mainVBox.getChildren().isEmpty()) {
            mainVBox.setStyle(FX_BACKGROUND_IMAGE_NO_TASKS);
        }

    }
}
