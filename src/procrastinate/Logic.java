package procrastinate;

import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.util.converter.NumberStringConverter;

import java.net.URL;
import java.util.ResourceBundle;

public class Logic implements Initializable {

    // ================================================================================
    // Message Strings
    // ================================================================================
    private static final String DEBUG_VIEW_LOADED = "View is now loaded!";

    private static final String FEEDBACK_ADD_DREAM = "Adding dream: ";

    private static final String STATUS_READY = "Ready!";
    private static final String STATUS_PREVIEW_COMMAND = "Preview command: ";

    // ================================================================================
    // Class Variables
    // ================================================================================
    private IntegerProperty taskCount = new SimpleIntegerProperty(1);

    private ObservableList taskList = FXCollections.observableArrayList();

    private StringProperty statusLabelText = new SimpleStringProperty();
    private StringProperty taskCountFormatted = new SimpleStringProperty();
    private StringProperty taskCountString = new SimpleStringProperty();
    private StringProperty userInput = new SimpleStringProperty();

    private TaskEngine taskEngine;

    // ================================================================================
    // FXML Field Variables
    // ================================================================================
    @FXML private BorderPane borderPane;
    @FXML private Label statusLabel;
    @FXML private ListView<String> taskListView;
    @FXML private TextField userInputField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Utilities.printDebug(DEBUG_VIEW_LOADED);

        initUI();
        attachHandlersAndListeners();
        initBinding();

        initTaskEngine();

        setStatus(STATUS_READY);
    }

    private String executeCommand(String userCommand) {

        Command command = Parser.parse(userCommand);

        switch (command.getType()) {

            case ADD_DREAM:
            	String description = command.getDescription();
            	Task newDream = new Task(description);
            	taskEngine.add(newDream);
                addDreamToTaskList(newDream);
                return FEEDBACK_ADD_DREAM + description;

            case EXIT:
                System.exit(0);

            default:
                throw new Error("Error with parser: unknown command type returned");

        }

    }

    private void addDreamToTaskList(Task task) {
        taskList.add(taskCountFormatted.get() + task.getDescription());
        taskCount.set(taskCount.get() + 1);
        updateListView();
    }

    // ================================================================================
    // Init methods
    // ================================================================================

    private void initTaskEngine() {
        taskEngine = new TaskEngine();
    }

    private void initUI() {
        taskListView.setPlaceholder(new Label("What would you like to Procrastinate today?"));
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
        taskCountFormatted.bind(Bindings.concat(taskCountString).concat(". "));

    }

    private EventHandler<KeyEvent> createKeyReleaseHandler() {
        return (KeyEvent keyEvent) -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                if (!getInput().trim().isEmpty()) {
                    String userCommand = getInput();
                    clearInput();

                    String feedback = executeCommand(userCommand);
                    setStatus(feedback);
                } else {
                    setStatus(STATUS_READY);
                }
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
