//@@author A0121597B
package procrastinate.ui;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.javafx.tk.Toolkit;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import procrastinate.task.Task;

/**
 * <h1>The main class of the UI component.</h1>
 * It instantiates all the other UI components required and provides
 * handle methods for the Logic component to call upon.
 */
public class UI {

    public static enum ScreenView {
        SCREEN_DONE, SCREEN_MAIN, SCREEN_MAIN_ALL, SCREEN_SEARCH, SCREEN_SUMMARY
    }

    private static final Logger logger = Logger.getLogger(UI.class.getName());

    private static boolean showTray_ = true;

    // ================================================================================
    // Message Strings
    // ================================================================================

    private static final String DEBUG_UI_INIT = "UI initialised.";
    private static final String DEBUG_UI_LOAD = "View is now loaded!";

    private static final String ELLIPSIS = "...";

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

    //@@author A0080485B
    public void setPreviewStatus(String status) {
        Platform.runLater(() -> {
            getStatusLabel().setStyle("-fx-text-fill: #365fac");
            statusLabelText_.set(status);
        });
    }

    public void setExecuteStatus(String status) {
        Platform.runLater(() -> {
            getStatusLabel().setStyle("-fx-text-fill: black");
            statusLabelText_.set(status);
        });
    }

    public String fitToStatus(String before, String text, String after) {
        if (canFitStatus(before + text + after)) {
            return before + text + after;
        }
        String grow = "";
        for (int i = 0; i < text.length(); i++) {
            char nextChar = text.charAt(i);
            if (!canFitStatus(before + grow + nextChar + ELLIPSIS + after)) {
                break;
            }
            grow += nextChar;
        }
        return before + grow + ELLIPSIS + after;
    }

    public void initialUpdateTaskList(List<Task> taskList) {
        centerPaneController_.initialUpdateMainScreen(taskList);
    }

    //@@author A0121597B
    public void updateTaskList(List<Task> taskList, ScreenView screenView) {
        centerPaneController_.updateScreen(taskList, screenView);
    }

    public void resetIsExit() {
        isExit_.set(false);
    }

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

    public void createErrorDialog(String header, String message) {
        dialogPopupHandler_.createErrorDialogPopup(header, message);
    }

    public void createErrorDialogWithTrace(Exception e) {
        dialogPopupHandler_.createErrorDialogPopupWithTrace(e);
    }

    //@@author A0080485B
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

    //@@author A0121597B
    private void initWindow() {
        windowHandler_ = new WindowHandler(primaryStage_);
        windowHandler_.loadWindowConfigurations(showTray_);
    }

    private void initDialogPopupHandler() {
        dialogPopupHandler_ = new DialogPopupHandler(primaryStage_);
    }

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

    //@@author A0080485B
    private Label getStatusLabel() {
        return windowHandler_.getStatusLabel();
    }

    private boolean canFitStatus(String status) {
        double width = Toolkit.getToolkit().getFontLoader().computeStringWidth(status, getStatusLabel().getFont());
        double maxWidth = getUserInputField().getWidth();
        return width < maxWidth;
    }

}
