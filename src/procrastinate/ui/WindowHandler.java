//@@author A0121597B
package procrastinate.ui;

import java.awt.SystemTray;
import java.io.IOException;

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

/**
 * <h1>WindowHandler handles the configuration and Scene settings of the primary Stage.</h1>
 * It also instantiates the SystemTrayHandler class if SystemTray is supported.
 */
public class WindowHandler {

    // ================================================================================
    // Fixed Variables
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
    private static final String STYLE_CLASS_MAIN_WINDOW = "mainWindow";
    private static final int WRAPPER_PREF_WIDTH = 800;
    private static final int WRAPPER_PREF_HEIGHT = 800;

    // ================================================================================
    // Class Variables
    // ================================================================================

    private Stage primaryStage_;

    private Parent root_;

    private SystemTray systemTray_;

    private SystemTrayHandler systemTrayHandler_;

    private BooleanProperty exitIndicator_ = new SimpleBooleanProperty(false);

    private static double xOffset_, yOffset_;

    // ================================================================================
    // FXML Field Variables
    // ================================================================================

    @FXML
    private BorderPane mainBorderPane;
    @FXML
    private Label close;
    @FXML
    private Label minimise;
    @FXML
    private Label statusLabel;
    @FXML
    private StackPane centerScreen;
    @FXML
    private TextField userInputField;

    // ================================================================================
    // WindowHandler Constructor
    // ================================================================================

    protected WindowHandler(Stage stage) {
        this.primaryStage_ = stage;
    }

    // ================================================================================
    // WindowHandler Methods
    // ================================================================================

    protected void loadWindowConfigurations(boolean showTray) {
        loadMainWindowLayout();
        if (showTray) {
            initTray();
        }
        overwriteDecorations();
        windowSetUp();
    }

    protected void bindAsExitIndicator(BooleanProperty isExit) {
        exitIndicator_.bindBidirectional(isExit);
        systemTrayHandler_.bindExitIndicator(isExit);
    }

    private void loadMainWindowLayout() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(LOCATION_MAIN_WINDOW_LAYOUT));
        loader.setController(this); // Required due to different package
                                    // declaration from Main
        try {
            root_ = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void windowSetUp() {
        configurePrimaryStage();
        setUpScene();
    }

    private void configurePrimaryStage() {
        primaryStage_.setTitle(WINDOW_TITLE);
        primaryStage_.setMinHeight(WINDOW_MIN_HEIGHT);
        primaryStage_.setMinWidth(WINDOW_MIN_WIDTH);

        primaryStage_.getIcons().add(new Image(WindowHandler.class.getResource(LOCATION_WINDOW_ICON).toExternalForm()));
    }

    private void setUpScene() {
        Scene primaryScene = new Scene(root_, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryScene.setFill(Color.TRANSPARENT);
        primaryScene.getStylesheets().add(getClass().getResource(LOCATION_CSS_STYLESHEET).toExternalForm());

        primaryStage_.setScene(primaryScene);
    }

    private void initTray() {
        if (isSysTraySupported()) {
            systemTrayHandler_ = new SystemTrayHandler(primaryStage_, userInputField);
            // userInputField is passed to SystemTrayHandler to request for
            // focus whenever the window is shown
            systemTray_ = systemTrayHandler_.initialiseTray();
            assert(systemTray_ != null);
        }
    }

    /**
     * Removes all window decorations, replacing a custom title bar and allow
     * dragging of window
     */
    private void overwriteDecorations() {
        createTitleBar();
        setTransparentStageStyle();
        setMouseEvents();
        wrapCurrentRoot();
    }

    // @@author A0121597B-reused
    /**
     * Removes all window decorations sets mouse events to enable dragging of
     * window
     */
    private void setMouseEvents() {
        setMouseEventsForWindowDragging();
        setMouseEventsForUserInputFieldFocus();
    }

    private void setMouseEventsForWindowDragging() {
        root_.setOnMousePressed((mouseEvent) -> {
            xOffset_ = mouseEvent.getSceneX();
            yOffset_ = mouseEvent.getSceneY();
        });

        root_.setOnMouseDragged((mouseEvent) -> {
            primaryStage_.setX(mouseEvent.getScreenX() - xOffset_);
            primaryStage_.setY(mouseEvent.getScreenY() - yOffset_);
        });
    }

    private void setMouseEventsForUserInputFieldFocus() {
        // Prevent mouse clicks on the center pane from stealing focus from
        // userInputField
        centerScreen.setOnMousePressed((mouseEvent) -> {
            userInputField.requestFocus();
        });

        centerScreen.setOnMouseDragged((mouseEvent) -> {
            userInputField.requestFocus();
        });
    }

    private void setTransparentStageStyle() {
        primaryStage_.initStyle(StageStyle.TRANSPARENT);
    }

    private void wrapCurrentRoot() {
        // Wraps the current root in an AnchorPane to provide drop shadow
        // styling
        AnchorPane wrapperPane = new AnchorPane(root_);

        wrapperPane.setPrefSize(WRAPPER_PREF_WIDTH, WRAPPER_PREF_HEIGHT);
        wrapperPane.getStyleClass().add(STYLE_CLASS_MAIN_WINDOW);
        wrapperPane.getStylesheets().add(getClass().getResource(LOCATION_CSS_STYLESHEET).toExternalForm());

        root_ = wrapperPane;
    }

    //@@author A0121597B
    /**
     * Creates a title bar for minimising and closing of Procrastinate.
     */
    private void createTitleBar() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(LOCATION_TITLE_BAR_FXML));
        try {
            loader.setController(this);
            HBox titleBar = loader.load();

            close.setText(ICON_CLOSE);
            close.setOnMouseClicked(mouseEvent -> {
                                    exitIndicator_.set(false);
                                    exitIndicator_.set(true);
            });

            minimise.setText(ICON_MINIMISE);
            if (systemTrayHandler_ != null) {
                minimise.setOnMouseClicked(mouseEvent -> systemTrayHandler_.windowHideOrShow());
            } else {
                minimise.setOnMouseClicked(mouseEvent -> primaryStage_.setIconified(true));
            }

            ((BorderPane) root_).setTop(titleBar);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isSysTraySupported() {
        return SystemTray.isSupported();
    }

    // ================================================================================
    // Getter Methods
    // ================================================================================

    //@@author generated
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
