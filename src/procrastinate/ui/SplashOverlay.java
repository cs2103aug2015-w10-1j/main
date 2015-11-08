//@@author A0121597B
package procrastinate.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

public class SplashOverlay extends ImageOverlay {

    // ================================================================================
    // Message Strings
    // ================================================================================

    private static final String ID_SUBTITLE_LABEL = "subtitleLabel";
    private static final String ID_TITLE_LABEL = "titleLabel";

    private static final String LOCATION_SPLASH_IMAGE = "images/icon.png";

    private static final String MESSAGE_SUBTITLE = "What would you like to Procrastinate today?";
    private static final String MESSAGE_WELCOME = "Welcome to Procrastinate!";

    // ================================================================================
    // Class Variables
    // ================================================================================

    private Label subtitleLabel_;
    private Label titleLabel_;

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
    // Init Methods
    // ================================================================================

    @Override
    protected void setImage() {
        imageView.setImage(new Image(ImageOverlay.class.getResource(LOCATION_SPLASH_IMAGE).toExternalForm()));
        imageView.fitWidthProperty().set(250);
    }

    private void createLabels() {
        titleLabel_ = new Label(MESSAGE_WELCOME);
        subtitleLabel_ = new Label(MESSAGE_SUBTITLE);
    }

    /**
     * Sets the alignment and positioning of the Labels to be placed under the icon image
     * used in the splash screen. Both labels also have their IDs updated for the CSS styling
     * to be applied.
     */
    private void setLabels() {
        titleLabel_.setAlignment(Pos.CENTER);
        titleLabel_.setTextAlignment(TextAlignment.CENTER);
        titleLabel_.setId(ID_TITLE_LABEL);

        subtitleLabel_.setId(ID_SUBTITLE_LABEL);

        VBox.setVgrow(titleLabel_, Priority.ALWAYS);
        VBox.setVgrow(subtitleLabel_, Priority.ALWAYS);
    }

    private void addLabels() {
        container.getChildren().addAll(titleLabel_, subtitleLabel_);
    }
}
