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
    public List<Task> taskList;
    public UIStub() {
        taskList = new ArrayList<Task>();
    }
    public UIStub(Stage stage) {
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
    public void setStatus(String status) {
    }
    @Override
    public void updateTaskList(List<Task> taskList, ScreenView screenView) {
        this.taskList = taskList;
    }
    @Override
    public void resetIsExit() {
    }
    @Override
    public void attachHandlersAndListeners(EventHandler<KeyEvent> keyReleaseHandler, EventHandler<KeyEvent> keyPressHandler,
            ChangeListener<String> userInputListener, ChangeListener<Boolean> isExitListener) {
    }
    @Override
    public void passSearchStringToSearchScreen(String searchTerm) {
    }
    @Override
    public void showHelpOverlay() {
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
