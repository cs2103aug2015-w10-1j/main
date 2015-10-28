package procrastinate.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;

public class HelpOverlay {

    // ================================================================================
    // Message strings
    // ================================================================================

    private static final String LOCATION_SPLASH_IMAGE = "images/icon.png";

    private static final String MESSAGE_WELCOME = "What would you like to Procrastinate today?";

    // ================================================================================
    // Class variables
    // ================================================================================

    private Node node;

    // ================================================================================
    // FXML field variables
    // ================================================================================

    @FXML Label subtitleLabel;
    @FXML ImageView splashImageView;

    // ================================================================================
    // HelpScreen methods
    // ================================================================================

    protected HelpOverlay(String filePath) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(filePath));
        loader.setController(this); // Required due to different package declaration from Main
        try {
            node = loader.load();
            subtitleLabel.setText(MESSAGE_WELCOME);
            splashImageView.setImage(new Image(HelpOverlay.class.getResource(LOCATION_SPLASH_IMAGE).toExternalForm()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected Node getNode() {
        return this.node;
    }
}
