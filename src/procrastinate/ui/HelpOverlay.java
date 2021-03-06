//@@author A0121597B
package procrastinate.ui;

import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;

/**
 * <h1>HelpOverlay contain the reference sheets to be shown as a 'help' screen.</h1>
 *
 * The reference sheets are pictures set within a ImageView and there are currently
 * 2 different pictures to be shown/switched.
 */
public class HelpOverlay extends ImageOverlay {

    // ================================================================================
    // Message Strings
    // ================================================================================

    private static final String LOCATION_REFERENCE_SHEET = "images/referencesheet.png";
    private static final String LOCATION_REFERENCE_SHEET_2 = "images/referencesheet2.png";

    private static final String STYLE_CONTAINER_PADDING = "-fx-padding: 0 30 0 30;";
    private static final String STYLE_WRAPPER_BACKGROUND_RADIUS = "-fx-background-radius: 20;";
    private static final String STYLE_WRAPPER_BACKGROUND_COLOR = "-fx-background-color: #365fac;";

    // ================================================================================
    // Constants
    // ================================================================================

    private static final int WRAPPER_PREF_HEIGHT = 430;
    private static final int WRAPPER_PREF_WIDTH = 400;

    // ================================================================================
    // Class Variables
    // ================================================================================

    private boolean isFirstPage_ = true;

    // ================================================================================
    // HelpOverlay Constructor
    // ================================================================================

    protected HelpOverlay() {
        setImage();
        adjustStylesAndAddWrapper();
    }

    // ================================================================================
    // HelpOverlay Methods
    // ================================================================================

    //@@author A0080485B
    protected void nextPage() {
        if (isFirstPage_) {
            imageView.setImage(new Image(HelpOverlay.class.getResource(LOCATION_REFERENCE_SHEET_2).toExternalForm()));
        } else {
            imageView.setImage(new Image(HelpOverlay.class.getResource(LOCATION_REFERENCE_SHEET).toExternalForm()));
        }

        isFirstPage_ = !isFirstPage_;
    }

    // ================================================================================
    // Init Methods
    // ================================================================================

    //@@author A0121597B
    @Override
    protected void setImage() {
        // Set to first page of Help sheet
        imageView.setImage(new Image(HelpOverlay.class.getResource(LOCATION_REFERENCE_SHEET).toExternalForm()));
        imageView.fitWidthProperty().set(400);
    }

    /**
     * Fits the loaded image into the center of the screen with the specified side padding.
     */
    private void adjustStylesAndAddWrapper() {
        container.setStyle(STYLE_CONTAINER_PADDING);

        container.getChildren().clear();
        container.getChildren().add(createWrapper());
    }

    /**
     * Creates the wrapper required to keep the reference sheet in the center of the screen.
     * It is required as the background color and box surrounding the help commands are rendered
     * within Java and not just from the background image provided.
     *
     * @return VBox    to wrap the present ImageView and provide it with customised styling and sizing.
     */
    private VBox createWrapper() {
        VBox wrapper = new VBox(imageView);

        wrapper.setAlignment(Pos.TOP_CENTER);
        wrapper.setPrefSize(WRAPPER_PREF_WIDTH, WRAPPER_PREF_HEIGHT);
        wrapper.setStyle(STYLE_WRAPPER_BACKGROUND_COLOR +
                         STYLE_WRAPPER_BACKGROUND_RADIUS);

        return wrapper;
    }
}
