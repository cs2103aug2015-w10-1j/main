package procrastinate.ui;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import procrastinate.task.Dream;
import procrastinate.task.Task;

public class UITestHelper {

    // Some test variables that are utilised multiple times in a test body
    public CenterPaneController centerPaneController;
    public MainScreen mainScreen;
    public TaskEntry dreamTask;
    public TaskEntry otherTask;

    // ================================================================================
    // CenterPaneController methods
    // ================================================================================

    public CenterPaneController getNewCenterPaneController(StackPane stackPane) {
        centerPaneController = new CenterPaneController(stackPane);
        return  centerPaneController;
    }

    public Node getCPCCurrentScreen() {
        return centerPaneController.currentScreen;
    }

    public void switchToMain() {
        centerPaneController.changeScreen(CenterPaneController.SCREEN_MAIN);
    }

    public Node getMainScreen() {
        return centerPaneController.getMainScreen();
    }

    public void switchToHelp() {
        centerPaneController.changeScreen(CenterPaneController.SCREEN_HELP);
    }

    public Node getHelpScreen() {
        return centerPaneController.getHelpOverlay();
    }

    // ================================================================================
    // HelpScreen methods
    // ================================================================================

    public HelpOverlay getNewHelpScreen() {
        return new HelpOverlay("views/HelpScreen.fxml");
    }

    // ================================================================================
    // MainScreen methods
    // ================================================================================

    public MainScreen getNewMainScreen() {
        mainScreen = new MainScreen("views/MainScreen.fxml");
        return mainScreen;
    }

    public VBox getMainScreenVBox() {
        return mainScreen.mainVBox;
    }

    public void addDreamToMainScreen(Dream dream) {
        List<Task> taskList = new ArrayList<>();
        taskList.add(dream);
        mainScreen.updateTaskList(taskList);
    }

    public VBox getDreamsTaskList() {
        // Dreams is the last category box added
        return (VBox) mainScreen.mainVBox.getChildren().get(3);
    }

    // ================================================================================
    // CategoryBox methods
    // ================================================================================

    public CategoryBox getNewCategoryBox() {
        return new CategoryBox("Test");
    }

    public VBox getNewCategoryBoxVBox() {
        CategoryBox categoryBox = new CategoryBox("Test");
        return categoryBox.categoryVBox;
    }

    public Label getNewCategoryBoxLabel(String label) {
        CategoryBox categoryBox = new CategoryBox(label);
        return categoryBox.categoryLabel;
    }

    // ================================================================================
    // TaskEntry methods
    // ================================================================================

    public TaskEntry getNewDreamTaskEntry(String lineNum, String des) {
        dreamTask = new TaskEntry(lineNum, des);
        return dreamTask;
    }

    public Label getDreamTaskEntryLineNum() {
        return dreamTask.lineNum;
    }

    public Label getDreamTaskEntryDescription() {
        return dreamTask.description;
    }

    public TaskEntry getNewOthersTaskEntry(String lineNum, String des, String time) {
        otherTask = new TaskEntry(lineNum, des, time);
        return otherTask;
    }

    public Label getOthersTaskEntryLineNum() {
        return otherTask.lineNum;
    }

    public Label getOthersTaskEntryDescription() {
        return otherTask.description;
    }

    public Label getOthersTaskEntryTime() {
        return otherTask.time;
    }
}
