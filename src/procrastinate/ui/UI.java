package procrastinate.ui;

import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.converter.NumberStringConverter;
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

    private static final String LOCATION_MAIN_WINDOW_LAYOUT = "MainWindowLayout.fxml";

    private static final String MESSAGE_WELCOME = "What would you like to Procrastinate today?";

    private static final String UI_NUMBER_SEPARATOR = ". ";

    // ================================================================================
    // Class variables
    // ================================================================================

    private IntegerProperty taskCount = new SimpleIntegerProperty(1);

    private ObservableList<String> taskList = FXCollections.observableArrayList();

    private Parent root;

    private Stage primaryStage;

    private StringProperty taskCountFormatted = new SimpleStringProperty();
    private StringProperty taskCountString = new SimpleStringProperty();

    private SystemTrayHandler sysTrayHandler;
    private SystemTray sysTray;
    private WindowHandler windowHandler;


    // ================================================================================
    // FXML field variables
    // ================================================================================

    @FXML private BorderPane mainBorderPane;
    @FXML private Label statusLabel;
    @FXML private ListView<String> taskListView;
    @FXML private TextField userInputField;

    // ADDED STUFFS, TO BE REARRANGED LATER ON
    @FXML private StackPane centerScreen;
    private CenterPaneController centerPaneController;
    private boolean isHelpOverlayed;

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

    // This auto gets called from the UI constructor when load is executed.
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
        primaryStage.show();
        logger.log(Level.INFO, DEBUG_UI_LOAD);
    }

    public void updateTaskList(List<Task> tasks) {
        taskList.clear();
        taskCount.set(1);
        for (Task task : tasks) {
            taskList.add(taskCountFormatted.get() + task.getDescription());
            taskCount.set(taskCount.get() + 1);
        }
        updateListView();
    }

    // ================================================================================
    // Init methods
    // ================================================================================

    private void initBinding(StringProperty userInput, StringProperty statusLabelText) {
        // Binds the input and status text to the StringProperty in Logic.
        userInput.bindBidirectional(userInputField.textProperty());
        statusLabelText.bindBidirectional(statusLabel.textProperty());

        taskCountString.bindBidirectional(taskCount, new NumberStringConverter());
        taskCountFormatted.bind(Bindings.concat(taskCountString).concat(UI_NUMBER_SEPARATOR));
    }

    private void initTaskDisplay() {
        taskListView.setPlaceholder(new Label(MESSAGE_WELCOME));
        taskListView.setItems(taskList);        // Initialises the list view and applies the CSS styling

        // Set up controller for new UI
        this.centerPaneController = new CenterPaneController(centerScreen);
        showHelp();                             // Acts as the splash/welcome and help screen for now.
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

    // ================================================================================
    // Utility methods
    // ================================================================================

    private void updateListView() {
        taskListView.setItems(taskList);
    }

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

    public void checkForHelpOverlay() {
        if (isHelpOverlayed) {
            centerPaneController.hideHelpOverlay();
        }
    }

    public void showHelp() {
        centerPaneController.changeScreen(CenterPaneController.SCREEN_HELP);
        isHelpOverlayed = true;
    }

    public void showMain() {
        taskListView.setOpacity(0);
        centerPaneController.changeScreen(CenterPaneController.SCREEN_MAIN);
    }
}
