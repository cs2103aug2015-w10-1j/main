package procrastinate.ui;

import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;

public class HelpOverlay extends ImageOverlay {

    // ================================================================================
    // Message strings
    // ================================================================================

    private static final String LOCATION_REFERENCE_SHEET = "images/referencesheet.png";

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
    }

    // TODO: Two more methods here to set between page 1 and 2

    private void adjustStylesAndAddWrapper() {
        container.setStyle("-fx-padding: 0 30 0 30;");
        container.getChildren().clear();
        container.getChildren().add(createWrapper());
    }

    private VBox createWrapper() {
        VBox wrapper = new VBox(imageView);
        wrapper.setAlignment(Pos.TOP_CENTER);
        wrapper.setPrefSize(400, 430);
        wrapper.setStyle("-fx-background-color: #365fac;"
                + "-fx-background-radius: 20;");
        return wrapper;
    }
}
