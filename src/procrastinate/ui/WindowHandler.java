package procrastinate.ui;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class WindowHandler {

    // ================================================================================
    // Fixed variables
    // ================================================================================

    private static final String LOCATION_CSS_STYLESHEET = "procrastinate.css";
    private static final String LOCATION_WINDOW_ICON = "icon.png";

    private static final String WINDOW_TITLE = "Procrastinate";
    private static final double WINDOW_WIDTH = 500;
    private static final double WINDOW_MIN_WIDTH = 500;
    private static final double WINDOW_HEIGHT = 600;
    private static final double WINDOW_MIN_HEIGHT = 600;

    // ================================================================================
    // Class variables
    // ================================================================================

    private Stage primaryStage;
    private Parent root;

    private static double xOffset, yOffset;

    // ================================================================================
    // WindowHandler methods
    // ================================================================================

    protected WindowHandler(Stage primaryStage,Parent root) {
        this.primaryStage = primaryStage;
        this.root = root;
    }

    protected void initialiseWindow() {
        configurePrimaryStage();
//        overwriteDecorations();
    }

    private void configurePrimaryStage() {
        primaryStage.setTitle(WINDOW_TITLE);
        primaryStage.setMinHeight(WINDOW_MIN_HEIGHT);
        primaryStage.setMinWidth(WINDOW_MIN_WIDTH);
        primaryStage.getIcons().addAll(
                new Image(getClass().getResourceAsStream(LOCATION_WINDOW_ICON))
        );

        Scene primaryScene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);  // This is the 'primary window' that consists of the user input field
        primaryScene.getStylesheets().add(getClass().getResource(LOCATION_CSS_STYLESHEET).toExternalForm());
        primaryStage.setScene(primaryScene);
        //overwriteDecorations(primaryStage, root);
    }

    // Removes all borders and buttons, enables dragging of window through frame
    // Unused for now
    @SuppressWarnings("unused")
    private void overwriteDecorations() {
        primaryStage.initStyle(StageStyle.UNDECORATED);
        root.setOnMousePressed((mouseEvent) -> {
            xOffset = mouseEvent.getSceneX();
            yOffset = mouseEvent.getSceneY();
        });
        root.setOnMouseDragged((mouseEvent) -> {
            primaryStage.setX(mouseEvent.getScreenX() - xOffset);
            primaryStage.setY(mouseEvent.getScreenY() - yOffset);
        });
    }
}
