package procrastinate.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class HelpOverlay {

    // ================================================================================
    // Message strings
    // ================================================================================

    private static final String LOCATION_SPLASH_IMAGE = "images/icon.png";

    private static final String MESSAGE_SUBTITLE = "What would you like to Procrastinate today?";
    private static final String MESSAGE_WELCOME = "Welcome to Procrastinate!";

    // ================================================================================
    // Class variables
    // ================================================================================

    private Node node;

    // ================================================================================
    // FXML field variables
    // ================================================================================

    @FXML private ImageView imageView;
    @FXML private Label subtitleLabel;
    @FXML private Label titleLabel;
    @FXML private VBox container;

    // ================================================================================
    // HelpScreen methods
    // ================================================================================

    protected HelpOverlay(String filePath) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(filePath));
        loader.setController(this); // Required due to different package declaration from Main
        try {
            node = loader.load();
            imageView.setImage(new Image(HelpOverlay.class.getResource(LOCATION_SPLASH_IMAGE).toExternalForm()));
            titleLabel.setText(MESSAGE_WELCOME);
            subtitleLabel.setText(MESSAGE_SUBTITLE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected Node getNode() {
        return this.node;
    }

    protected ImageView getImageView() {
        return imageView;
    }

    protected Label getSubtitleLabel() {
        return subtitleLabel;
    }

    protected Label getTitleLabel() {
        return titleLabel;
    }

    protected VBox getContainer() {
        return container;
    }
}
