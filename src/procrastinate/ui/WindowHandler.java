package procrastinate.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class WindowHandler {

    // ================================================================================
    // Fixed variables
    // ================================================================================

    private static final String LOCATION_CSS_STYLESHEET = "views/procrastinate.css";
    private static final String LOCATION_TITLE_BAR_FXML = "views/TitleBar.fxml";
    private static final String LOCATION_WINDOW_ICON = "images/icon.png";

    private static final String WINDOW_TITLE = "Procrastinate";
    private static final double WINDOW_WIDTH = 500;
    private static final double WINDOW_MIN_WIDTH = 500;
    private static final double WINDOW_HEIGHT = 600;
    private static final double WINDOW_MIN_HEIGHT = 600;

    private static final String ICON_CLOSE = "\uf00d";
    private static final String ICON_MINIMISE = "\uf068";
    //private static final String SELECTOR_SCROLL_PANE = "#top";
    private static final String SELECTOR_CENTER_SCREEN = "#centerScreen";
    private static final String STYLE_CLASS_MAIN_WINDOW = "mainWindow";
    private static final int WRAPPER_PREF_WIDTH = 800;
    private static final int WRAPPER_PREF_HEIGHT = 800;

    // ================================================================================
    // Class variables
    // ================================================================================

    private Stage primaryStage;
    private Parent root;
    private SystemTrayHandler systemTrayHandler;

    private static double xOffset, yOffset;

    @FXML private Label close;
    @FXML private Label minimise;

    // ================================================================================
    // WindowHandler methods
    // ================================================================================

    protected WindowHandler(Stage primaryStage,Parent root, SystemTrayHandler systemTrayHandler) {
        this.primaryStage = primaryStage;
        this.root = root;
        this.systemTrayHandler = systemTrayHandler;
    }

    protected void initialiseWindow() {
        overwriteDecorations();
        configurePrimaryStage();
    }

    private void configurePrimaryStage() {
        primaryStage.setTitle(WINDOW_TITLE);
        primaryStage.setMinHeight(WINDOW_MIN_HEIGHT);
        primaryStage.setMinWidth(WINDOW_MIN_WIDTH);
        primaryStage.getIcons().addAll(
                new Image(WindowHandler.class.getResource(LOCATION_WINDOW_ICON).toExternalForm())
        );
        Scene primaryScene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);  // This is the 'primary window' that consists of the user input field
        primaryScene.setFill(Color.TRANSPARENT);
        primaryScene.getStylesheets().add(getClass().getResource(LOCATION_CSS_STYLESHEET).toExternalForm());
        primaryStage.setScene(primaryScene);
    }

    /**
     * Removes all window decorations, replacing a custom title bar and allow dragging of window
     */
    //@SuppressWarnings("unused")
    private void overwriteDecorations() {
        if (systemTrayHandler != null) {
            createTitleBar();
            setStyleAndMouseEvents();
        }

    }
    /**
     * Removes all window decorations sets mouse events to enable dragging of window
     */
    private void setStyleAndMouseEvents() {
        primaryStage.initStyle(StageStyle.TRANSPARENT);

        root.setOnMousePressed((mouseEvent) -> {
            xOffset = mouseEvent.getSceneX();
            yOffset = mouseEvent.getSceneY();
        });
        root.setOnMouseDragged((mouseEvent) -> {
            primaryStage.setX(mouseEvent.getScreenX() - xOffset);
            primaryStage.setY(mouseEvent.getScreenY() - yOffset);
        });

        // Since the CenterScreen is wrapped around another Pane, setting the mouse events on it is necessary as well.
//        ScrollPane scrollPane = (ScrollPane) root.lookup(SELECTOR_SCROLL_PANE);
        StackPane centerPane = (StackPane) root.lookup(SELECTOR_CENTER_SCREEN);
        centerPane.setOnMousePressed((mouseEvent) -> {
            xOffset = mouseEvent.getSceneX();
            yOffset = mouseEvent.getSceneY();
        });
        centerPane.setOnMouseDragged((mouseEvent) -> {
            centerPane.getScene().getWindow().setX(mouseEvent.getScreenX() - xOffset);
            centerPane.getScene().getWindow().setY(mouseEvent.getScreenY() - yOffset);
        });

        // Wraps the current root in an AnchorPane to provide drop shadow styling
        AnchorPane ap = new AnchorPane(root);
        ap.setPrefSize(WRAPPER_PREF_WIDTH, WRAPPER_PREF_HEIGHT);
        ap.getStyleClass().add(STYLE_CLASS_MAIN_WINDOW);
        ap.getStylesheets().add(getClass().getResource(LOCATION_CSS_STYLESHEET).toExternalForm());
        root = ap;
    }

    /**
     * Creates a title bar for minimising and closing of Procrastinate.
     */
    private void createTitleBar() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(LOCATION_TITLE_BAR_FXML));
        try {
            loader.setController(this);
            HBox titleBar = loader.load();
            close.setText(ICON_CLOSE);
            close.setOnMouseClicked(mouseEvent -> System.exit(0));
            minimise.setText(ICON_MINIMISE);
            minimise.setOnMouseClicked(mouseEvent -> systemTrayHandler.windowHideOrShow());
            ((BorderPane)root).setTop(titleBar);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
