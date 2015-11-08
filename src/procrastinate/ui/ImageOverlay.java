//@@author A0121597B
package procrastinate.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.io.IOException;

public abstract class ImageOverlay {

    // ================================================================================
    // Message Strings
    // ================================================================================

    private static final String LOCATION_HELP_OVERLAY_LAYOUT = "views/ImageOverlay.fxml";

    // ================================================================================
    // Class Variables
    // ================================================================================

    protected Node node;

    // ================================================================================
    // FXML Field Variables
    // ================================================================================

    @FXML
    protected ImageView imageView;
    @FXML
    protected VBox container;

    // ================================================================================
    // ImageOverlay Constructor
    // ================================================================================

    protected ImageOverlay() {
        loadLayout();
    }

    // ================================================================================
    // ImageOverlay Methods
    // ================================================================================

    protected abstract void setImage();

    // ================================================================================
    // Init Methods
    // ================================================================================

    private void loadLayout() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(LOCATION_HELP_OVERLAY_LAYOUT));
        loader.setController(this); // Required due to different package
                                    // declaration from Main
        try {
            this.node = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ================================================================================
    // Getter Methods
    // ================================================================================

    // @@author A0121597B generated
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
