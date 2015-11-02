package procrastinate.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

public class SplashOverlay extends ImageOverlay {

    // ================================================================================
    // Message strings
    // ================================================================================

    private static final String LOCATION_SPLASH_IMAGE = "images/icon.png";

    private static final String MESSAGE_SUBTITLE = "What would you like to Procrastinate today?";
    private static final String MESSAGE_WELCOME = "Welcome to Procrastinate!";

    // ================================================================================
    // Class variables
    // ================================================================================

    private Label subtitleLabel;
    private Label titleLabel;

    // ================================================================================
    // SplashOverlay Constructor
    // ================================================================================

    protected SplashOverlay (){
        setImage();
        createLabels();
        setLabels();
        addLabels();
    }

    // ================================================================================
    // SplashOverlay methods
    // ================================================================================

    @Override
    protected void setImage() {
        imageView.setImage(new Image(ImageOverlay.class.getResource(LOCATION_SPLASH_IMAGE).toExternalForm()));
    }

    private void createLabels() {
        titleLabel = new Label(MESSAGE_WELCOME);
        subtitleLabel = new Label(MESSAGE_SUBTITLE);
    }

    private void setLabels() {
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setTextAlignment(TextAlignment.CENTER);
        titleLabel.setId("titleLabel");

        subtitleLabel.setId("subtitleLabel");

        VBox.setVgrow(titleLabel, Priority.ALWAYS);
        VBox.setVgrow(subtitleLabel, Priority.ALWAYS);
    }

    private void addLabels() {
        container.getChildren().addAll(titleLabel, subtitleLabel);
    }
}
