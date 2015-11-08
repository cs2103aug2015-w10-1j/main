//@@author A0121597B
package procrastinate.ui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

/**
 * <h1>ImageOverlay contains an ImageView wrapped in a VBox that is placed on top of
 * a white translucent background to provide an overlay effect.</h1>
 *
 * Different images can be set in the ImageView and additional nodes can be added into
 * the VBox provided.
 */
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
            throw new RuntimeException(e);
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
