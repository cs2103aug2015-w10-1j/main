package procrastinate;

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
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.util.converter.NumberStringConverter;

public class UI {

    // ================================================================================
    // Message strings
    // ================================================================================

    private static final String MESSAGE_WELCOME = "What would you like to Procrastinate today?";

    private static final String STATUS_READY = "Ready!";
    private static final String STATUS_PREVIEW_COMMAND = "Preview command: ";

    private static final String UI_NUMBER_SEPARATOR = ". ";

    private static final String DEBUG_UI_INIT = "UI initialised. View is now loaded!";

    // ================================================================================
    // Class variables
    // ================================================================================

    private static Logger logger;

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

    public void initialize() {
        logger = Logger.getLogger(this.getClass().getSimpleName());
        logger.setLevel(Level.ALL);

        initLogic();

        initUI();
        attachHandlersAndListeners();
        initBinding();

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
                setStatus(STATUS_PREVIEW_COMMAND + newValue);
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
        return (KeyEvent keyEvent) -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                if (!getInput().trim().isEmpty()) {
                    String userCommand = getInput();

                    String feedback = logic.executeCommand(userCommand);
                    setStatus(feedback);
                } else {
                    setStatus(STATUS_READY);
                }
                clearInput();
            }
        };
    }

    // ================================================================================
    // UI utility methods
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
