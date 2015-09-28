package procrastinate;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ResourceBundle;

public class Logic implements Initializable {

    // ================================================================================
    // Message Strings
    // ================================================================================
    private static final String DEBUG_VIEW_LOADED = "View is now loaded!";
    private static final String STATUS_READY = "Ready!";
    private static final String STATUS_PREVIEW_COMMAND = "Preview command: ";
    private static final String FEEDBACK_ADD_DREAM = "Adding dream: ";

    private static FileHandler fileHandler;

    // ================================================================================
    // Class Variables
    // ================================================================================
    private StringProperty userInput = new SimpleStringProperty();
    private StringProperty statusLabelText = new SimpleStringProperty();

    // ================================================================================
    // FXML Field Variables
    // ================================================================================
    @FXML private Label statusLabel;
    @FXML private TextField userInputField;
    @FXML private BorderPane borderPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Utilities.printDebug(DEBUG_VIEW_LOADED);

        initFileHandler();

        attachHandlersAndListeners();
        initBinding();

        setStatus(STATUS_READY);
    }

    private String executeCommand(String userCommand) {

        Command command = Parser.parse(userCommand);

        switch (command.getType()) {

            case ADD_DREAM:
            	String description = command.getDescription();
            	fileHandler.writeToFile(description);
                return FEEDBACK_ADD_DREAM + description;

            case EXIT:
                System.exit(0);

            default:
                throw new Error("Error with parser: unknown command type returned");

        }
    }

    // ================================================================================
    // Init methods
    // ================================================================================

    private void initFileHandler() {
        fileHandler = new FileHandler();
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
    }

    private EventHandler<KeyEvent> createKeyReleaseHandler() {
        return (KeyEvent keyEvent) -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                if (!getInput().isEmpty()) {
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
