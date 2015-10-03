package procrastinate.ui;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Platform;
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
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.converter.NumberStringConverter;

import procrastinate.task.Task;

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

    private static final String WINDOW_TITLE = "Procrastinate";
    private static final double WINDOW_WIDTH = 500;
    private static final double WINDOW_MIN_WIDTH = 500;
    private static final double WINDOW_HEIGHT = 600;
    private static final double WINDOW_MIN_HEIGHT = 600;

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

    // Window or System Tray related variables
    private static double xOffset, yOffset;


    // ================================================================================
    // FXML field variables
    // ================================================================================

    @FXML private BorderPane mainBorderPane;
    @FXML private Label statusLabel;
    @FXML private ListView<String> taskListView;
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

    // This auto gets called from the UI constructor when load is executed.
    public void initialize() {
        initTaskDisplay();
    }

    // Logic Handles
    public void setUpBinding(StringProperty userInput, StringProperty statusLabelText) {
        initBinding(userInput, statusLabelText);
    }

    public void setUpStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
        initWindowAndTray();
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

    private void initWindowAndTray() {
        configurePrimaryStage(primaryStage, root);
        if (isSysTraySupported()) {
            sysTrayHandler = new SystemTrayHandler(primaryStage);
            sysTray = sysTrayHandler.initialiseTray();
        }
        primaryStage.show();
    }

    private void initTaskDisplay() {
        taskListView.setPlaceholder(new Label(MESSAGE_WELCOME));
    }

    // ================================================================================
    // Window Configurations
    // ================================================================================

    private void configurePrimaryStage(Stage primaryStage, Parent root) {
        primaryStage.setTitle(WINDOW_TITLE);
        primaryStage.setMinHeight(WINDOW_MIN_HEIGHT);
        primaryStage.setMinWidth(WINDOW_MIN_WIDTH);
        primaryStage.setScene(new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT));
        //overwriteDecorations(primaryStage, root);
    }

    // Removes all borders and buttons, enables dragging of window through frame
    // Unused for now
    @SuppressWarnings("unused")
    private void overwriteDecorations(Stage primaryStage, Parent root) {
        primaryStage.initStyle(StageStyle.UNDECORATED);
        root.setOnMousePressed((mouseEvent) -> {
                xOffset = mouseEvent.getSceneX();
                yOffset = mouseEvent.getSceneY();
            });
        root.setOnMouseDragged((mouseEvent) -> {
                primaryStage.setX(mouseEvent.getScreenX() - xOffset);
                primaryStage.setY(mouseEvent.getScreenY() - yOffset);
            });
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
}
