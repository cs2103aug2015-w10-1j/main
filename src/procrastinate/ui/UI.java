package procrastinate.ui;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import procrastinate.task.Task;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UI {

    public static enum ScreenView {
        SCREEN_DONE, SCREEN_MAIN, SCREEN_SEARCH
    }

    private static final Logger logger = Logger.getLogger(UI.class.getName());

    private static boolean showTray = true;

    // ================================================================================
    // Message strings
    // ================================================================================

    private static final String DEBUG_UI_INIT = "UI initialised.";
    private static final String DEBUG_UI_LOAD = "View is now loaded!";

    // ================================================================================
    // Class variables
    // ================================================================================

    private Stage primaryStage;
    private boolean isScreenOverlayed;
    private CenterPaneController centerPaneController;

    private DialogPopupHandler dialogPopupHandler;
    private WindowHandler windowHandler;

    // ================================================================================
    // UI methods
    // ================================================================================

    public UI() {
    }

    public UI(Stage stage) {
        assert(stage != null);
        primaryStage = stage;
        initWindow(stage);
        initDialogPopupHandler(stage);
        initTaskDisplay();
        logger.log(Level.INFO, DEBUG_UI_INIT);
    }

    // Logic Handles
    public void setUpBinding(StringProperty userInput, StringProperty statusLabelText, BooleanProperty isExit) {
        initBinding(userInput, statusLabelText);
        assert(windowHandler != null);
        windowHandler.bindAsExitIndicator(isExit);
    }

    public void setUpAndShowStage() {
        assert(primaryStage != null);
        primaryStage.show();
        showSplashScreen();
        logger.log(Level.INFO, DEBUG_UI_LOAD);
    }

    public void updateTaskList(List<Task> taskList, ScreenView screenView) {
        centerPaneController.updateScreen(taskList, screenView);
    }

    // ================================================================================
    // Init methods
    // ================================================================================

    private void initBinding(StringProperty userInput, StringProperty statusLabelText) {
        // Binds the input and status text to the StringProperty in Logic.
        userInput.bindBidirectional(windowHandler.getUserInputField().textProperty());
        statusLabelText.bindBidirectional(windowHandler.getStatusLabel().textProperty());
    }

    private void initDialogPopupHandler(Stage stage) {
        dialogPopupHandler = new DialogPopupHandler(stage);
    }

    /**
     * Sets up controller for center pane and overlays a splash screen on top of the main screen display.
     */
    private void initTaskDisplay() {
        this.centerPaneController = new CenterPaneController(windowHandler.getCenterScreen());
    }

    private void initWindow(Stage stage) {
        windowHandler = new WindowHandler(stage);
        windowHandler.loadWindowConfigurations(showTray);
    }

    // ================================================================================
    // Getter methods
    // ================================================================================

    public TextField getUserInputField() {
        return windowHandler.getUserInputField();
    }

    // ================================================================================
    // Center screen methods
    // ================================================================================

    /**
     * Used by Logic to remove the Help Screen overlay once the user starts typing.
     */
    public void checkForScreenOverlay() {
        if (isScreenOverlayed) {
            centerPaneController.hideScreenOverlay();
            isScreenOverlayed = false;
        }
    }

    /**
     * Overlays the current screen with the Help screen.
     */
    public void showHelp() {
        if (!isScreenOverlayed) {
            centerPaneController.showHelpOverlay();
            isScreenOverlayed = true;
        }
    }

    private void showSplashScreen() {
        centerPaneController.showSplashScreen();
        isScreenOverlayed = true;
    }

    // ================================================================================
    // DialogPopupHandler methods
    // ================================================================================

    /**
     * Used by Logic to create an Error Dialog with a given message in the popup body
     * @param message to be shown in popup body
     */
    public void createErrorDialog(String message) {
        dialogPopupHandler.createErrorDialogPopup(message);
    }

    /**
     * Used by Logic to create an Error Dialog that displays the Exception message and its stack trace
     * @param e Exception whose trace should be shown
     */
    public void createErrorDialogWithTrace(Exception e) {
        dialogPopupHandler.createErrorDialogPopupWithTrace(e);
    }

    /**
     * Used by Logic to create an Error Dialog with a given message and
     * choice for confirmation: 'OK' or 'Cancel'
     * @param message to be shown in popup body
     * @return true if 'OK', false if 'Cancel'
     */
    public boolean createErrorDialogWithConfirmation(String message) {
        return dialogPopupHandler.createErrorDialogPopupWithConfirmation(message);
    }
}
