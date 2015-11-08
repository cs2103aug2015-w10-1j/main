//@@author A0121597B
package procrastinate.ui;

import java.util.List;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import procrastinate.task.Task;
import procrastinate.ui.UI.ScreenView;

public class UITestHelper {

    // Some test variables that are utilised multiple times in a test body
    public CenterPaneController centerPaneController;

    // ================================================================================
    // CenterPaneController methods
    // ================================================================================

    public CenterPaneController getNewCenterPaneController(StackPane stackPane) {
        centerPaneController = new CenterPaneController(stackPane);
        return centerPaneController;
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
    // ImageOverlay methods
    // ================================================================================

    public ImageOverlay getNewHelpOverlay() {
        return new HelpOverlay();
    }

    public void switchHelpOverlayPage(HelpOverlay helpOverlay) {
        helpOverlay.nextPage();
    }

    public ImageOverlay getNewSplashOverlay() {
        return new SplashOverlay();
    }

    public ImageView getOverlayImageView(ImageOverlay imageOverlay) {
        return imageOverlay.getImageView();
    }

    public VBox getOverlayContainer(ImageOverlay imageOverlay) {
        return imageOverlay.getContainer();
    }

    // ================================================================================
    // CategoryBox methods
    // ================================================================================

    public CategoryBox getNewCategoryBox(String headerText) {
        return new CategoryBox(headerText);
    }

    public VBox getCategoryBoxVBox(CategoryBox categoryBox) {
        return categoryBox.getTaskListVBox();
    }

    public Label getCategoryBoxLabel(CategoryBox categoryBox) {
        return categoryBox.getCategoryLabel();
    }

    // ================================================================================
    // SubcategoryBox methods
    // ================================================================================

    public SubcategoryBox getNewSubcategoryBox(String headerText) {
        return new SubcategoryBox(headerText);
    }

    public VBox getSubcategoryBoxVBox(SubcategoryBox subcategoryBox) {
        return subcategoryBox.getTaskListVBox();
    }

    public Label getSubcategoryBoxLabel(SubcategoryBox subcategoryBox) {
        return subcategoryBox.getSubcategoryLabel();
    }

    // ================================================================================
    // TaskEntry methods
    // ================================================================================

    public TaskEntry getNewDreamTaskEntry(String lineNum, String des) {
        return new TaskEntry(lineNum, des, false);
    }

    public TaskEntry getNewOthersTaskEntry(String lineNum, String des, String time) {
        return new TaskEntry(lineNum, des, time, false);
    }

    public TaskEntry getNewDoneTaskEntry(String lineNum, String des, String time) {
        return new TaskEntry(lineNum, des, time, true);
    }

    public Label getTaskEntryLineNum(TaskEntry taskEntry) {
        return taskEntry.getLineNum();
    }

    public Label getTaskEntryDescription(TaskEntry taskEntry) {
        return taskEntry.getDescription();
    }

    public Label getTaskEntryTime(TaskEntry taskEntry) {
        return taskEntry.getTime();
    }

    public Node getTaskEntryNode(TaskEntry taskEntry) {
        return taskEntry.getEntryDisplay();
    }
}
