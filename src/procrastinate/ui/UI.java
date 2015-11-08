//@@author A0121597B
package procrastinate.ui;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import procrastinate.task.Task;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UI {

    public static enum ScreenView {
        SCREEN_DONE, SCREEN_MAIN, SCREEN_SEARCH, SCREEN_SUMMARY
    }

    private static final Logger logger = Logger.getLogger(UI.class.getName());

    private static boolean showTray_ = true;

    // ================================================================================
    // Message Strings
    // ================================================================================

    private static final String DEBUG_UI_INIT = "UI initialised.";
    private static final String DEBUG_UI_LOAD = "View is now loaded!";

    // ================================================================================
    // Class Variables
    // ================================================================================

    private Stage primaryStage_;

    private CenterPaneController centerPaneController_;

    private DialogPopupHandler dialogPopupHandler_;

    private WindowHandler windowHandler_;

    private BooleanProperty isExit_ = new SimpleBooleanProperty(false);

    private StringProperty userInput_ = new SimpleStringProperty();
    private StringProperty statusLabelText_ = new SimpleStringProperty();

    // ================================================================================
    // UI Methods
    // ================================================================================

    protected UI() {
    }

    public UI(Stage stage) {
        assert(stage != null);
        primaryStage_ = stage;

        initWindow();
        initDialogPopupHandler();
        initTaskDisplay();
        setupBinding();
        setupAndShowStage();

        logger.log(Level.INFO, DEBUG_UI_INIT);
    }

    // Retrieves the current user input from the TextField.
    public String getInput() {
        return userInput_.get();
    }

    public void setInput(String input) {
        userInput_.set(input);
        getUserInputField().end();
    }

    public void clearInput() {
        getUserInputField().clear();
    }

    // Sets the text of the 'Status' Label directly.
    public void setStatus(String status) {
        Platform.runLater(() -> statusLabelText_.set(status));
    }

    public void updateTaskList(List<Task> taskList, ScreenView screenView) {
        centerPaneController_.updateScreen(taskList, screenView);
    }

    public void initialUpdateTaskList(List<Task> taskList) {
        centerPaneController_.initialUpdateMainScreen(taskList);
    }

    public void resetIsExit() {
        isExit_.set(false);
    }

    // Attaches KeyHandler and Listener to the TextField to dynamically update the 'Status' Label upon input.
    public void attachHandlersAndListeners(EventHandler<KeyEvent> keyPressHandler,
                                           ChangeListener<String> userInputListener,
                                           ChangeListener<Boolean> isExitListener) {
        TextField userInputField = getUserInputField();
        userInputField.setOnKeyPressed(keyPressHandler);
        userInputField.textProperty().addListener(userInputListener);

        isExit_.addListener(isExitListener);
    }

    public void hide() {
        Platform.runLater(() -> primaryStage_.hide());
    }

    // ================================================================================
    // CenterPaneController Methods
    // ================================================================================

    public void passSearchStringToSearchScreen(String searchString) {
        centerPaneController_.receiveSearchStringAndPassToSearchScreen(searchString);
    }

    public void showHelpOverlay() {
        centerPaneController_.showHelpOverlay();
    }

    public void nextHelpPage() {
        centerPaneController_.showNextHelpPage();
    }

    private void showSplashOverlay() {
        centerPaneController_.showSplashOverlay();
    }

    public void hideHelpOverlay() {
        centerPaneController_.hideHelpOverlay();
    }

    public void hideSplashOverlay() {
        centerPaneController_.hideSplashOverlay();
    }

    public void scrollUpScreen() {
        centerPaneController_.scrollUpCurrentScreen();
    }

    public void scrollDownScreen() {
        centerPaneController_.scrollDownCurrentScreen();
    }

    // ================================================================================
    // DialogPopupHandler Methods
    // ================================================================================

    /**
     * Used by Logic to create an Error Dialog with a given message in the popup body
     * @param message to be shown in popup body
     */
    public void createErrorDialog(String header, String message) {
        dialogPopupHandler_.createErrorDialogPopup(header, message);
    }

    /**
     * Used by Logic to create an Error Dialog that displays the Exception message and its stack trace
     * @param e Exception whose trace should be shown
     */
    public void createErrorDialogWithTrace(Exception e) {
        dialogPopupHandler_.createErrorDialogPopupWithTrace(e);
    }

    /**
     * Used by Logic to create an Error Dialog with a given message and
     * choice for confirmation: 'OK' or 'Cancel'
     * @param message to be shown in popup body
     * @return true if 'OK', false if 'Cancel'
     */
    public boolean createErrorDialogWithConfirmation(String header, String message, String okLabel) {
        boolean result = false;
        if (!Platform.isFxApplicationThread()) {
            FutureTask<Boolean> query = new FutureTask<Boolean>(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return dialogPopupHandler_.createErrorDialogPopupWithConfirmation(header, message, okLabel);
                }
            });
            Platform.runLater(query);

            try {
                result = query.get();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            result = dialogPopupHandler_.createErrorDialogPopupWithConfirmation(header, message, okLabel);
        }
        return result;
    }

    // ================================================================================
    // Init Methods
    // ================================================================================

    private void initWindow() {
        windowHandler_ = new WindowHandler(primaryStage_);
        windowHandler_.loadWindowConfigurations(showTray_);
    }

    private void initDialogPopupHandler() {
        dialogPopupHandler_ = new DialogPopupHandler(primaryStage_);
    }

    /**
     * Sets up controller for center pane and overlays a splash screen on top of the main screen display.
     */
    private void initTaskDisplay() {
        this.centerPaneController_ = new CenterPaneController(windowHandler_.getCenterScreen());
    }

    private void setupBinding() {
        assert(windowHandler_ != null);
        windowHandler_.bindAsExitIndicator(isExit_);

        userInput_.bindBidirectional(windowHandler_.getUserInputField().textProperty());
        statusLabelText_.bindBidirectional(windowHandler_.getStatusLabel().textProperty());
    }

    private void setupAndShowStage() {
        assert(primaryStage_ != null);

        primaryStage_.show();
        showSplashOverlay();

        logger.log(Level.INFO, DEBUG_UI_LOAD);
    }

    // ================================================================================
    // Utility Methods
    // ================================================================================

    private TextField getUserInputField() {
        return windowHandler_.getUserInputField();
    }

}
