//@@author A0121597B
package procrastinate.ui;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.SystemTray;
import java.io.IOException;

public class WindowHandler {

    // ================================================================================
    // Fixed variables
    // ================================================================================

    private static final String LOCATION_CSS_STYLESHEET = "views/procrastinate.css";
    private static final String LOCATION_MAIN_WINDOW_LAYOUT = "views/MainWindowLayout.fxml";
    private static final String LOCATION_TITLE_BAR_FXML = "views/TitleBar.fxml";
    private static final String LOCATION_WINDOW_ICON = "images/icon.png";

    private static final String WINDOW_TITLE = "Procrastinate";
    private static final double WINDOW_WIDTH = 500;
    private static final double WINDOW_MIN_WIDTH = 500;
    private static final double WINDOW_HEIGHT = 600;
    private static final double WINDOW_MIN_HEIGHT = 600;

    private static final String ICON_CLOSE = "\uf00d";
    private static final String ICON_MINIMISE = "\uf068";
    private static final String SELECTOR_CENTER_SCREEN = "#centerScreen";
    private static final String STYLE_CLASS_MAIN_WINDOW = "mainWindow";
    private static final int WRAPPER_PREF_WIDTH = 800;
    private static final int WRAPPER_PREF_HEIGHT = 800;

    // ================================================================================
    // Class variables
    // ================================================================================

    private Stage primaryStage;
    private Parent root;
    private SystemTray systemTray;
    private SystemTrayHandler systemTrayHandler;

    private BooleanProperty exitIndicator = new SimpleBooleanProperty(false);

    private static double xOffset, yOffset;

    // ================================================================================
    // FXML field variables
    // ================================================================================

    @FXML private BorderPane mainBorderPane;
    @FXML private Label close;
    @FXML private Label minimise;
    @FXML private Label statusLabel;
    @FXML private StackPane centerScreen;
    @FXML private TextField userInputField;

    // ================================================================================
    // WindowHandler methods
    // ================================================================================

    protected WindowHandler(Stage stage) {
        this.primaryStage = stage;
    }

    protected void loadWindowConfigurations(boolean showTray) {
        loadMainWindowLayout();
        if (showTray) {
            initTray();
        }
        overwriteDecorations();
        configurePrimaryStage();
    }

    protected void bindAsExitIndicator(BooleanProperty isExit) {
        exitIndicator.bindBidirectional(isExit);
        systemTrayHandler.bindExitIndicator(isExit);
    }

    private void loadMainWindowLayout() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(LOCATION_MAIN_WINDOW_LAYOUT));
        loader.setController(this); // Required due to different package declaration from Main
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    private void initTray() {
        if (isSysTraySupported()) {
            systemTrayHandler = new SystemTrayHandler(primaryStage, userInputField);
            // userInputField is passed to SystemTrayHandler to request for focus whenever the window is shown
            systemTray = systemTrayHandler.initialiseTray();
            assert (systemTray != null);
        }
    }

    /**
     * Removes all window decorations, replacing a custom title bar and allow dragging of window
     */
    private void overwriteDecorations() {
        createTitleBar();
        setStyleAndMouseEvents();
    }

    /**
     * Removes all window decorations sets mouse events to enable dragging of window
     */
    //@@author A0121597B-reused
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
        AnchorPane wrapperPane = new AnchorPane(root);
        wrapperPane.setPrefSize(WRAPPER_PREF_WIDTH, WRAPPER_PREF_HEIGHT);
        wrapperPane.getStyleClass().add(STYLE_CLASS_MAIN_WINDOW);
        wrapperPane.getStylesheets().add(getClass().getResource(LOCATION_CSS_STYLESHEET).toExternalForm());
        root = wrapperPane;
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
            close.setOnMouseClicked(mouseEvent -> exitIndicator.set(true));

            minimise.setText(ICON_MINIMISE);
            if (systemTrayHandler != null) {
                minimise.setOnMouseClicked(mouseEvent -> systemTrayHandler.windowHideOrShow());
            } else {
                minimise.setOnMouseClicked(mouseEvent -> primaryStage.setIconified(true));
            }

            ((BorderPane)root).setTop(titleBar);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //@@author A0121597B generated
    private boolean isSysTraySupported() {
        return  SystemTray.isSupported();
    }

    protected Label getStatusLabel() {
        return statusLabel;
    }

    protected StackPane getCenterScreen() {
        return centerScreen;
    }

    protected TextField getUserInputField() {
        return userInputField;
    }

}
