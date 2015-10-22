package procrastinate.ui;

import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import procrastinate.task.Task;

import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UI {

    private static final Logger logger = Logger.getLogger(UI.class.getName());

    // ================================================================================
    // Message strings
    // ================================================================================

    private static final String DEBUG_UI_INIT = "UI initialised.";
    private static final String DEBUG_UI_LOAD = "View is now loaded!";

    private static final String LOCATION_MAIN_WINDOW_LAYOUT = "views/MainWindowLayout.fxml";

    // ================================================================================
    // Class variables
    // ================================================================================

    private boolean isScreenOverlayed;
    private CenterPaneController centerPaneController;

    private Parent root;

    private Stage primaryStage;

    private DialogPopupHandler dialogPopupHandler;
    private SystemTrayHandler sysTrayHandler;
    private SystemTray sysTray;
    private WindowHandler windowHandler;

    // ================================================================================
    // FXML field variables
    // ================================================================================

    @FXML private BorderPane mainBorderPane;
    @FXML private Label statusLabel;
    @FXML private StackPane centerScreen;
    @FXML private TextField userInputField;

    // ================================================================================
    // UI methods
    // ================================================================================

    public UI() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(LOCATION_MAIN_WINDOW_LAYOUT));
        loader.setController(this); // Required due to different package declaration from Main
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.log(Level.INFO, DEBUG_UI_INIT);
    }

    public UI(boolean isUnderTest) {
    }

    // This gets called automatically from the UI constructor when load is executed.
    public void initialize() {
        initTaskDisplay();
    }

    // Logic Handles
    public void setUpBinding(StringProperty userInput, StringProperty statusLabelText) {
        initBinding(userInput, statusLabelText);
    }

    public void setUpStage(Stage primaryStage) {
        assert (primaryStage != null);
        this.primaryStage = primaryStage;
        initWindow();
        initTray();
        initDialogPopupHandler();
        primaryStage.show();
        logger.log(Level.INFO, DEBUG_UI_LOAD);
    }

    public void updateTaskList(List<Task> tasks) {
        // Pass the updates to the main screen
        centerPaneController.updateMainScreen(tasks);
    }

    // ================================================================================
    // Init methods
    // ================================================================================

    private void initBinding(StringProperty userInput, StringProperty statusLabelText) {
        // Binds the input and status text to the StringProperty in Logic.
        userInput.bindBidirectional(userInputField.textProperty());
        statusLabelText.bindBidirectional(statusLabel.textProperty());
    }

    /**
     * Sets up controller for center pane and overlays a splash screen on top of the main screen display.
     */
    private void initTaskDisplay() {
        this.centerPaneController = new CenterPaneController(centerScreen);
        showSplashScreen();
    }

    private void initTray() {
        if (isSysTraySupported()) {
            sysTrayHandler = new SystemTrayHandler(primaryStage, userInputField);
            // userInputField is passed to SystemTrayHandler to request for focus whenever the window is shown
            sysTray = sysTrayHandler.initialiseTray();
            assert (sysTray != null);
        }
    }

    private void initWindow() {
        windowHandler = new WindowHandler(primaryStage, root);
        windowHandler.initialiseWindow();
    }

    private void initDialogPopupHandler() {
        dialogPopupHandler = new DialogPopupHandler(primaryStage);
    }

    // ================================================================================
    // Utility methods
    // ================================================================================

    private boolean isSysTraySupported() {
        return  SystemTray.isSupported();
    }

    // ================================================================================
    // Getter methods
    // ================================================================================

    public TextField getUserInputField() {
        return userInputField;
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
        centerPaneController.changeScreen(CenterPaneController.SCREEN_HELP);
        isScreenOverlayed = true;
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
     * @param message
     */
    public void createErrorDialog(String message) {
        dialogPopupHandler.createErrorDialogPopup(message);
    }

    /**
     * Used by Logic to create an Error Dialog that displays the Exception message and its stack trace
     * @param e
     */
    public void createErrorDialogWithTrace(Exception e) {
        dialogPopupHandler.createErrorDialogPopupWithTrace(e);
    }
}
