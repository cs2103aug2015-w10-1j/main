package procrastinate.test;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TextField;
import procrastinate.task.Task;
import procrastinate.ui.UI;

public class UIStub extends UI {
    public List<Task> taskList;
    public UIStub() {
        taskList = new ArrayList<Task>();
    }
    @Override
    public void setUpBinding(StringProperty userInput, StringProperty statusLabelText, BooleanProperty isExit) {
    }
    @Override
    public void setUpAndShowStage() {
    }
    @Override
    public void updateTaskList(List<Task> taskList, ScreenView screenView) {
        this.taskList = taskList;
    }
    @Override
    public TextField getUserInputField() {
        return new TextField();
    }
    @Override
    public void checkForScreenOverlay() {
    }
    @Override
    public void showHelp() {
    }
    @Override
    public void createErrorDialog(String message) {
    }
    @Override
    public void createErrorDialogWithTrace(Exception e) {
    }
    @Override
    public boolean createErrorDialogWithConfirmation(String message) {
        return true;
    }
}
