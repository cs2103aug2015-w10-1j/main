package procrastinate;

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

    private static final String DEBUG_VIEW_LOADED = "View is now loaded!";
    private static final String STATUS_READY = "Ready!";
    private static final String STATUS_PARSING_COMMAND = "Parsing command: ";

    @FXML private Label statusLabel;
    @FXML private TextField userInput;
    @FXML private BorderPane borderPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Utilities.printDebug(DEBUG_VIEW_LOADED);
        setStatus(STATUS_READY);
    }

    @FXML
    private void onKeyPressHandler(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER)) {
            setStatus(STATUS_PARSING_COMMAND + getInput());
            clearInput();
        } else if (getInput().isEmpty()) {
            setStatus(STATUS_READY);
        } else {
            setStatus(getInput());
        }
    }

    // ================================================================================
    // UI utility methods
    // ================================================================================

    private void clearInput() {
        userInput.clear();
    }

    private String getInput() {
        return userInput.getText();
    }

    private void setStatus(String status) {
        statusLabel.setText(status);
    }

}
