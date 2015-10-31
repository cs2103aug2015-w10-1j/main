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
        super(true);
        taskList = new ArrayList<Task>();
    }
    @Override
    public void setUpBinding(StringProperty userInput, StringProperty statusLabelText, BooleanProperty isExit) {
    }
    @Override
    public void updateTaskList(List<Task> tasks) {
        taskList = tasks;
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
}
