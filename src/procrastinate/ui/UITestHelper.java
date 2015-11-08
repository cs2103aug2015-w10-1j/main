//@@author A0121597B
package procrastinate.ui;

import java.util.List;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import procrastinate.task.Task;
import procrastinate.ui.UI.ScreenView;

public class UITestHelper {

    // Some test variables that are utilised multiple times in a test body
    public CenterPaneController centerPaneController;

    public TaskEntry dreamTask;
    public TaskEntry otherTask;

    // ================================================================================
    // CenterPaneController methods
    // ================================================================================

    public CenterPaneController getNewCenterPaneController(StackPane stackPane) {
        centerPaneController = new CenterPaneController(stackPane);
        return  centerPaneController;
    }

    public CenterScreen getCPCCurrentScreen() {
        return centerPaneController.getCurrentScreen();
    }

    public Node getCPCCurrentScreenNode() {
        return centerPaneController.getCurrentScreen().getNode();
    }

    public ImageOverlay getCPCCurrentOverlay() {
        return centerPaneController.getCurrentOverlay();
    }

    public Node getCPCCurrentOverlayNode() {
        return centerPaneController.getCurrentOverlay().getNode();
    }

    public void changeCPCScreen(List<Task> taskList, ScreenView screenView) {
        centerPaneController.updateScreen(taskList, screenView);
    }

    public Node getDoneScreenNode() {
        return centerPaneController.getDoneScreenNode();
    }

    public Node getMainScreenNode() {
        return centerPaneController.getMainScreenNode();
    }

    public Node getSearchScreenNode() {
        return centerPaneController.getSearchScreenNode();
    }

    public Node getSummaryScreenNode() {
        return centerPaneController.getSummaryScreenNode();
    }

    public Node getHelpOverlayNode() {
        return centerPaneController.getHelpOverlayNode();
    }

    public Node getSplashOverlayNode() {
        return centerPaneController.getSplashOverlayNode();
    }

    public void showSplash() {
        centerPaneController.showSplashOverlay();
    }

    public void hideSplash() {
        centerPaneController.hideSplashOverlay();
    }

    public void showHelp() {
        centerPaneController.showHelpOverlay();
    }

    public void hideHelp() {
        centerPaneController.hideHelpOverlay();
    }

    // ================================================================================
    // CenterScreen methods
    // ================================================================================

    public Node getCenterScreenNode(CenterScreen centerScreen) {
        return centerScreen.getNode();
    }

    public VBox getCenterScreenVBox(CenterScreen centerScreen) {
        return centerScreen.getMainVBox();
    }

    public void updateScreenTaskList(CenterScreen centerScreen, List<Task> taskList) {
        centerScreen.updateTaskList(taskList);
    }

    // ================================================================================
    // MultiCategoryScreen methods
    // ================================================================================

    public MultiCategoryScreen getNewMainScreen() {
        return new MainScreen();
    }

    public MultiCategoryScreen getNewSearchScreen() {
        return new SearchScreen();
    }

    public MultiCategoryScreen getNewSummaryScreen() {
        return new SummaryScreen();
    }

    public Node getOverdueNode(MultiCategoryScreen multiCategoryScreen) {
        return multiCategoryScreen.getOverdueNode();
    }

    public Node getUpcomingNode(MultiCategoryScreen multiCategoryScreen) {
        return multiCategoryScreen.getUpcomingNode();
    }

    public Node getFutureNode(MultiCategoryScreen multiCategoryScreen) {
        return multiCategoryScreen.getFutureNode();
    }

    public Node getDreamsNode(MultiCategoryScreen multiCategoryScreen) {
        return multiCategoryScreen.getDreamsNode();
    }

    public Node getDoneNode(MultiCategoryScreen multiCategoryScreen) {
        return multiCategoryScreen.getDoneNode();
    }

    public VBox getOverdueTaskList(MultiCategoryScreen multiCategoryScreen) {
        return multiCategoryScreen.getOverdueTaskList();
    }

    public VBox getUpcomingTaskList(MultiCategoryScreen multiCategoryScreen) {
        return multiCategoryScreen.getUpcomingTaskList();
    }

    public VBox getFutureTaskList(MultiCategoryScreen multiCategoryScreen) {
        return multiCategoryScreen.getFutureTaskList();
    }

    public VBox getDreamsTaskList(MultiCategoryScreen multiCategoryScreen) {
        return multiCategoryScreen.getDreamsTaskList();
    }

    public VBox getDoneTaskList(MultiCategoryScreen multiCategoryScreen) {
        return multiCategoryScreen.getDoneTaskList();
    }

    // ================================================================================
    // SingleCategoryScreen methods
    // ================================================================================

    public SingleCategoryScreen getNewDoneScreen() {
        return new DoneScreen();
    }

    public Node getSingleCategoryNode(SingleCategoryScreen singleCategoryScreen) {
        return singleCategoryScreen.getCategoryNode();
    }

    public VBox getSingleCategoryTaskList(SingleCategoryScreen singleCategoryScreen) {
        return singleCategoryScreen.getCategoryTaskList();
    }

    // ================================================================================
    // HelpScreen methods
    // ================================================================================

    public ImageOverlay getNewHelpScreen() {
        return new HelpOverlay();
    }

    // ================================================================================
    // CategoryBox methods
    // ================================================================================

    public CategoryBox getNewCategoryBox() {
        return new CategoryBox("Test");
    }

    public VBox getNewCategoryBoxVBox() {
        CategoryBox categoryBox = new CategoryBox("Test");
        return categoryBox.getTaskListVBox();
    }

    public Label getNewCategoryBoxLabel(String label) {
        CategoryBox categoryBox = new CategoryBox(label);
        return categoryBox.getCategoryLabel();
    }

    // ================================================================================
    // TaskEntry methods
    // ================================================================================

    public TaskEntry getNewDreamTaskEntry(String lineNum, String des) {
        dreamTask = new TaskEntry(lineNum, des, false);
        return dreamTask;
    }

    public Label getDreamTaskEntryLineNum() {
        return dreamTask.getLineNum();
    }

    public Label getDreamTaskEntryDescription() {
        return dreamTask.getDescription();
    }

    public TaskEntry getNewOthersTaskEntry(String lineNum, String des, String time) {
        otherTask = new TaskEntry(lineNum, des, time, false);
        return otherTask;
    }

    public Label getOthersTaskEntryLineNum() {
        return otherTask.getLineNum();
    }

    public Label getOthersTaskEntryDescription() {
        return otherTask.getDescription();
    }

    public Label getOthersTaskEntryTime() {
        return otherTask.getTime();
    }
}
