package procrastinate.ui;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.converter.NumberStringConverter;

import procrastinate.Logic;
import procrastinate.task.Task;

public class UI {

    private static final Logger logger = Logger.getLogger(UI.class.getName());

    // ================================================================================
    // Message strings
    // ================================================================================

    private static final String DEBUG_UI_INIT = "UI initialised. View is now loaded!";

    private static final String MESSAGE_WELCOME = "What would you like to Procrastinate today?";

    private static final String STATUS_READY = "Ready!";
    private static final String STATUS_PREVIEW_COMMAND = "Preview: ";

    private static final String UI_NUMBER_SEPARATOR = ". ";

    // ================================================================================
    // Class variables
    // ================================================================================

    private Logic logic;

    private IntegerProperty taskCount = new SimpleIntegerProperty(1);

    private ObservableList<String> taskList = FXCollections.observableArrayList();

    private StringProperty statusLabelText = new SimpleStringProperty();
    private StringProperty taskCountFormatted = new SimpleStringProperty();
    private StringProperty taskCountString = new SimpleStringProperty();
    private StringProperty userInput = new SimpleStringProperty();

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

    private Parent root;

    public UI() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainWindowLayout.fxml"));
//        loader.setRoot(this);
        loader.setController(this);
        try {
            root = loader.load();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void setUpStage(Stage primaryStage) {
        primaryStage.setScene(new Scene(root, 500, 500));
        primaryStage.show();
    }

    public void initialize() {
        initUI();
        attachHandlersAndListeners();
        initBinding();

//        initLogic(); // Must be after UI is up as it causes Logic to call UI

        setStatus(STATUS_READY);

        logger.log(Level.INFO, DEBUG_UI_INIT);
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

    private void initLogic() {
        logic = Logic.getInstance();
        logic.setUi(this);
    }

    private void initUI() {
        taskListView.setPlaceholder(new Label(MESSAGE_WELCOME));
    }

    private void attachHandlersAndListeners() {
        userInputField.setOnKeyReleased(createKeyReleaseHandler());
        userInputField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.trim().isEmpty()) {
                setStatus(STATUS_READY);
            } else {
                setStatus(STATUS_PREVIEW_COMMAND + logic.previewCommand(newValue));
            }
        });
    }

    private void initBinding() {
        userInput.bindBidirectional(userInputField.textProperty());
        statusLabelText.bindBidirectional(statusLabel.textProperty());
        taskCountString.bindBidirectional(taskCount, new NumberStringConverter());
        taskCountFormatted.bind(Bindings.concat(taskCountString).concat(UI_NUMBER_SEPARATOR));
    }

    private EventHandler<KeyEvent> createKeyReleaseHandler() {
        return (keyEvent) -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                String input = getInput();
                clearInput(); // Must come before setStatus as key release handler resets status
                if (!input.trim().isEmpty()) {
                    if (!logic.hasLastPreviewedCommand()) {
                        logic.previewCommand(input);
                    }
                    String feedback = logic.executeLastPreviewedCommand();
                    setStatus(feedback);
                } else {
                    setStatus(STATUS_READY);
                }
            }
        };
    }

    // ================================================================================
    // Utility methods
    // ================================================================================

    private void updateListView() {
        taskListView.setItems(taskList);
    }

    private void clearInput() {
        userInputField.clear();
    }

    private String getInput() {
        return userInput.get();
    }

    private void setStatus(String status) {
        statusLabelText.set(status);
    }

}