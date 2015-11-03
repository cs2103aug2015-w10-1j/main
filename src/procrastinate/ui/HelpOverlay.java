package procrastinate.ui;

import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;

public class HelpOverlay extends ImageOverlay {

    // ================================================================================
    // Message strings
    // ================================================================================

    private static final int WRAPPER_PREF_HEIGHT = 430;
    private static final int WRAPPER_PREF_WIDTH = 400;

    private static final String LOCATION_REFERENCE_SHEET = "images/referencesheet.png";

    private static final String STYLE_CONTAINER_PADDING = "-fx-padding: 0 30 0 30;";
    private static final String STYLE_WRAPPER_BACKGROUND_RADIUS = "-fx-background-radius: 20;";
    private static final String STYLE_WRAPPER_BACKGROUND_COLOR = "-fx-background-color: #365fac;";

    // ================================================================================
    // Class variables
    // ================================================================================

    // ================================================================================
    // HelpOverlay Constructor
    // ================================================================================

    protected HelpOverlay() {
        setImage();
        adjustStylesAndAddWrapper();
    }

    // ================================================================================
    // HelpOverlay methods
    // ================================================================================

    @Override
    protected void setImage() {
        // Set to first page of Help sheet
        imageView.setImage(new Image(HelpOverlay.class.getResource(LOCATION_REFERENCE_SHEET).toExternalForm()));
        imageView.fitWidthProperty().set(400);
    }

    // TODO: Two more methods here to set between page 1 and 2

    private void adjustStylesAndAddWrapper() {
        container.setStyle(STYLE_CONTAINER_PADDING);
        container.getChildren().clear();
        container.getChildren().add(createWrapper());
    }

    private VBox createWrapper() {
        VBox wrapper = new VBox(imageView);
        wrapper.setAlignment(Pos.TOP_CENTER);
        wrapper.setPrefSize(WRAPPER_PREF_WIDTH, WRAPPER_PREF_HEIGHT);
        wrapper.setStyle(STYLE_WRAPPER_BACKGROUND_COLOR
                + STYLE_WRAPPER_BACKGROUND_RADIUS);
        return wrapper;
    }
}
