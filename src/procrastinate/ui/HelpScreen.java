package procrastinate.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class HelpScreen extends CenterScreen {

    // ================================================================================
    // Message strings
    // ================================================================================

    private static final String LOCATION_SPLASH_IMAGE = "images/icon.png";

    private static final String MESSAGE_WELCOME = "What would you like to Procrastinate today?";

    // ================================================================================
    // FXML field variables
    // ================================================================================

    @FXML Label subtitleLabel;
    @FXML ImageView splashImageView;

    // ================================================================================
    // HelpScreen methods
    // ================================================================================

    protected HelpScreen(String filePath) {
        super(filePath);
        subtitleLabel.setText(MESSAGE_WELCOME);
        splashImageView.setImage(new Image(getClass().getResourceAsStream(LOCATION_SPLASH_IMAGE)));
    }
}
