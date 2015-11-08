//@@author A0121597B
package procrastinate.ui;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import procrastinate.task.Dream;
import procrastinate.task.Task;
import procrastinate.ui.UI.ScreenView;

public class UITestHelper {

    // Some test variables that are utilised multiple times in a test body
    public CenterPaneController centerPaneController;
    public MultiCategoryScreen mainScreen;
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

    public ImageOverlay getCPCCurrentOverlay() {
        return centerPaneController.getCurrentOverlay();
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

    // ================================================================================
    // HelpScreen methods
    // ================================================================================

    public ImageOverlay getNewHelpScreen() {
        return new HelpOverlay();
    }

    // ================================================================================
    // MainScreen methods
    // ================================================================================

    public MultiCategoryScreen getNewMainScreen() {
        mainScreen = new MainScreen();
        return mainScreen;
    }

    public VBox getMainScreenVBox() {
        return mainScreen.getMainVBox();
    }

    public void addDreamToMainScreen(Dream dream) {
        List<Task> taskList = new ArrayList<>();
        taskList.add(dream);
        mainScreen.updateTaskList(taskList);
    }

    public VBox getDreamsTaskList() {
        // Dreams is the last category box added
        return (VBox) mainScreen.getMainVBox().getChildren().get(3);
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
