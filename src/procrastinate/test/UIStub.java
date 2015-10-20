package procrastinate.test;

import java.util.List;

import javafx.beans.property.StringProperty;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import procrastinate.task.Task;
import procrastinate.ui.UI;

public class UIStub extends UI {
    public UIStub() {
        super(true);
    }
    @Override
    public void initialize() {
    }
    @Override
    public void setUpBinding(StringProperty userInput, StringProperty statusLabelText) {
    }
    @Override
    public void setUpStage(Stage primaryStage) {
    }
    @Override
    public void updateTaskList(List<Task> tasks) {
    }
    @Override
    public TextField getUserInputField() {
        return new TextField();
    }
    @Override
    public void checkForHelpOverlay() {
    }
    @Override
    public void showHelp() {
    }
    @Override
    public void showMain() {
    }
}
