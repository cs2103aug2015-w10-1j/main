package procrastinate.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.io.IOException;

public abstract class ImageOverlay {

    // ================================================================================
    // Message strings
    // ================================================================================

    private static final String LOCATION_HELP_OVERLAY_LAYOUT = "views/ImageOverlay.fxml";

    // ================================================================================
    // Class variables
    // ================================================================================

    protected Node node;

    // ================================================================================
    // FXML field variables
    // ================================================================================

    @FXML protected ImageView imageView;
    @FXML protected VBox container;

    // ================================================================================
    // Overlay Constructor
    // ================================================================================

    protected ImageOverlay() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(LOCATION_HELP_OVERLAY_LAYOUT));
        loader.setController(this); // Required due to different package declaration from Main
        try {
            node = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected abstract void setImage();

    protected Node getNode() {
        return this.node;
    }

    protected ImageView getImageView() {
        return imageView;
    }

    protected VBox getContainer() {
        return container;
    }
}
