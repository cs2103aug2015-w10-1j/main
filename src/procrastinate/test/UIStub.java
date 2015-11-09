//@@author A0080485B
package procrastinate.test;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import procrastinate.task.Task;
import procrastinate.ui.UI;

public class UIStub extends UI {
    private List<Task> taskList_;
    public UIStub() {
        taskList_ = new ArrayList<Task>();
    }
    public UIStub(Stage stage) {
    }
    public List<Task> getTaskList() {
        return taskList_;
    }
    @Override
    public String getInput() {
        return null;
    }
    @Override
    public void setInput(String input) {
    }
    @Override
    public void clearInput() {
    }
    @Override
    public void setPreviewStatus(String status) {
    }
    @Override
    public void setExecuteStatus(String status) {
    }
    @Override
    public String fitToStatus(String before, String text, String after) {
        return before + text + after;
    }
    @Override
    public void initialUpdateTaskList(List<Task> taskList) {
    }
    @Override
    public void updateTaskList(List<Task> taskList, ScreenView screenView) {
        taskList_ = taskList;
    }
    @Override
    public void resetIsExit() {
    }
    @Override
    public void attachHandlersAndListeners(EventHandler<KeyEvent> keyPressHandler,
            ChangeListener<String> userInputListener, ChangeListener<Boolean> isExitListener) {
    }
    @Override
    public void hide() {
    }
    @Override
    public void passSearchStringToSearchScreen(String searchTerm) {
    }
    @Override
    public void showHelpOverlay() {
    }
    @Override
    public void nextHelpPage() {
    }
    @Override
    public void hideHelpOverlay() {
    }
    @Override
    public void hideSplashOverlay() {
    }
    @Override
    public void scrollUpScreen() {
    }
    @Override
    public void scrollDownScreen() {
    }
    @Override
    public void createErrorDialog(String header, String message) {
    }
    @Override
    public void createErrorDialogWithTrace(Exception e) {
    }
    @Override
    public boolean createErrorDialogWithConfirmation(String header, String message, String okLabel) {
        return true;
    }
}
