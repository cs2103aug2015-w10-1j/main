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

public class RootLayoutController implements Initializable {

    @FXML private Label statusLabel;
    @FXML private TextField userInput;
    @FXML private BorderPane borderPane;

    private static final String STATUS_READY = "Status: Ready!";
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("View is now loaded!");
        statusLabel.setText(STATUS_READY);
    }

    @FXML
    private void onKeyPressHandler(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER)) {
            statusLabel.setText("Parsing command: " + userInput.getText());
            userInput.setText("");
        } else if (userInput.getText().isEmpty()) {
            statusLabel.setText(STATUS_READY);
        } else {
            statusLabel.setText(userInput.getText());
        }
    }
}
