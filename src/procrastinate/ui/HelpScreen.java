package procrastinate.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HelpScreen extends CenterScreen {

    // ================================================================================
    // Message strings
    // ================================================================================

    private static final String MESSAGE_WELCOME = "What would you like to Procrastinate today?";

    // ================================================================================
    // FXML field variables
    // ================================================================================

    @FXML Label subtitleLabel;

    // ================================================================================
    // HelpScreen methods
    // ================================================================================

    protected HelpScreen(String filePath) {
        super(filePath);
        subtitleLabel.setText(MESSAGE_WELCOME);
    }
}
