# A0121597Breused
###### procrastinate\test\UITest.java
``` java
    // Needed to be initialised before testing JavaFX elements
    // http://stackoverflow.com/questions/28501307/javafx-toolkit-not-initialized-in-one-test-class-but-not-two-others-where-is
    public static void initToolkit() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        SwingUtilities.invokeLater(() -> {
            new JFXPanel();
            latch.countDown();
        });
        if (!latch.await(5L, TimeUnit.SECONDS)) {
            throw new ExceptionInInitializerError();
        }

        System.out.println("Setting up UITest...");

        System.out.println("Setting up CenterPaneController...");
        centerPaneController = uiTestHelper.getNewCenterPaneController(new StackPane());
        assertNotNull(centerPaneController);
        System.out.println("CenterPaneController initialised.");
    }

```
###### procrastinate\ui\CenterPaneController.java
``` java
    // Required since each screen node is wrapped inside a scrollPane.
    private void addMouseDragListeners(Node screenNode) {
        Node scrollPaneNode = ((ScrollPane) screenNode.lookup(SELECTOR_SCROLL_PANE)).getContent();

        scrollPaneNode.setOnMousePressed((mouseEvent) -> {
            xOffset_ = mouseEvent.getSceneX();
            yOffset_ = mouseEvent.getSceneY();
        });

        scrollPaneNode.setOnMouseDragged((mouseEvent) -> {
            centerStackPane_.getScene().getWindow().setX(mouseEvent.getScreenX() - xOffset_);
            centerStackPane_.getScene().getWindow().setY(mouseEvent.getScreenY() - yOffset_);
        });
    }

    // ================================================================================
    // Getter Methods
    // ================================================================================

```
###### procrastinate\ui\DialogPopupHandler.java
``` java
package procrastinate.ui;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

/**
 * <h1>DialogPopupHandler class handles the creation and showing of all the error
 * dialogs used.</h1>
 */
public class DialogPopupHandler {

    // ================================================================================
    // Message Strings
    // ================================================================================

    private static final String MESSAGE_HEADER = "An error has occurred with the following message:";

    private static final String BUTTON_MESSAGE_CANCEL = "Cancel";

    // ================================================================================
    // Class Variables
    // ================================================================================

    private Stage primaryStage_;

    // ================================================================================
    // DialogPopupHandler Constructor
    // ================================================================================

    protected DialogPopupHandler(Stage primaryStage) {
        this.primaryStage_ = primaryStage;
    }

    // ================================================================================
    // DialogPopupHandler Methods
    // ================================================================================

    /**
     * Creates an error dialog with a header and message.
     *
     * @param header     to be displayed as the title
     * @param message    to be displayed in the body
     */
    protected void createErrorDialogPopup(String header, String message) {
        Alert dialog = new Alert(Alert.AlertType.ERROR);
        dialog.initOwner(primaryStage_);

        setHeaderAndContentOfDialog(header, message, dialog);

        dialog.showAndWait();
    }

    /**
     * Creates an error dialog that takes in an Exception and displays the exception message with its stack trace
     * in an expandable region.
     *
     * @param exception    whose stack trace should be shown in the expandable region
     */
    protected void createErrorDialogPopupWithTrace(Exception exception) {
        Alert dialog = new Alert(Alert.AlertType.ERROR);
        dialog.initOwner(primaryStage_);

        // Retrieve the stack trace as String
        String stackTrace = convertStackTraceToString(exception);

        setHeaderAndContentOfDialog(MESSAGE_HEADER, exception.getMessage(), dialog);

        // Place the stack trace into a TextArea for display
        TextArea textArea = getTextAreaWithTrace(stackTrace);

        dialog.getDialogPane().setExpandableContent(textArea);
        dialog.showAndWait();
    }

```
###### procrastinate\ui\DoubleNodePair.java
``` java
package procrastinate.ui;

import javafx.scene.Node;

/**
 * <h1>DoubleNodePair is a class created specially for the SummaryScreen, to allow
 * pairing of a Node and it's height value, a double.</h1>
 *
 * It allows the sorting by the double value (height) to allow the SummaryScreen to
 * make more efficient use of space by handling smaller height Nodes first.
 */
public class DoubleNodePair implements Comparable<DoubleNodePair> {

    // ================================================================================
    // Class Variables
    // ================================================================================

    private double height_;
    private Node node_;

    // ================================================================================
    // DoubleNodePair Constructor
    // ================================================================================

    public DoubleNodePair(double height, Node node) {
        this.height_ = height;
        this.node_ = node;
    }

    // ================================================================================
    // DoubleNodePair Methods
    // ================================================================================

    @Override
    public int compareTo(DoubleNodePair o) {
        return Double.valueOf(this.height_).compareTo(o.height_);
    }

    // ================================================================================
    // Getter Methods
    // ================================================================================

```
###### procrastinate\ui\MultiCategoryScreen.java
``` java
    private Timeline generateHighlightTimeline(GridPane newTaskEntry) {
        Timeline highlightTimeline = new Timeline();

        for (int i = STYLE_BACKGROUND_HIGHLIGHT_FULL_OPACITY; i >= 0; i--) {
            float backgroundColorOpacity = (float) i / STYLE_BACKGROUND_HIGHLIGHT_FULL_OPACITY;
            String styleBackgroundColor = String.format(STYLE_BACKGROUND_HIGHLIGHT_FORMAT, backgroundColorOpacity);

            Duration duration = highlightTimeline.getTotalDuration().add(Duration.millis(STYLE_BACKGROUND_HIGHLIGHT_FRAME_TIME));
            KeyValue keyValue = new KeyValue(newTaskEntry.styleProperty(), styleBackgroundColor, Interpolator.EASE_IN);
            KeyFrame keyFrame = new KeyFrame(duration, keyValue);

            highlightTimeline.getKeyFrames().add(keyFrame);
        }

        highlightTimeline.setRate(STYLE_BACKGROUND_HIGHLIGHT_RATE);
        return highlightTimeline;
    }

```
###### procrastinate\ui\SystemTrayHandler.java
``` java
package procrastinate.ui;

import java.awt.AWTException;
import java.awt.Frame;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * <h1>SystemTrayHandler class handles the tray icon operation and hiding or showing
 * of the main window.</h1>
 */
public class SystemTrayHandler {

    // ================================================================================
    // Message Strings
    // ================================================================================

    private static final String MESSAGE_UNABLE_TO_LOAD_ICON_IMAGE = "Unable to load icon image for system tray.";

    private static final String OS_CHECK_WINDOWS = "Windows";
    private static final String OS_CHECK_NAME = "os.name";

    private static final String TRAY_ICON_TITLE = "Procrastinate";
    private static final String TRAY_IMAGE_ICON = "images/icon.png";
    private static final String TRAY_MENU_SHOW_OR_HIDE = "Show/Hide";
    private static final String TRAY_MENU_EXIT = "Exit";
    private static final String TRAY_MESSAGE_DESCRIPTION = "Access or exit Procrastinate from the tray icon";
    private static final String TRAY_MESSAGE_TITLE = "Procrastinate is still running!";

    // ================================================================================
    // Class Variables
    // ================================================================================

    private boolean isMouseEntered_ = false;
    private boolean shownMinimiseMessage_ = false;

    private BooleanProperty exitIndicator_ = new SimpleBooleanProperty(false);

    private Stage primaryStage_;

    private SystemTray sysTray_;

    private TrayIcon sysTrayIcon_;

    private PopupMenu popupMenu_;

    private Frame invisibleFrame_;

    private TextField userInputField_;

    // ================================================================================
    // SystemTrayHandler Constructor
    // ================================================================================

    protected SystemTrayHandler(Stage primaryStage, TextField userInputField) {
        this.primaryStage_ = primaryStage;
        this.userInputField_ = userInputField;
    }

    // ================================================================================
    // SystemTrayHandler Methods
    // ================================================================================

    protected SystemTray initialiseTray() {
        configureSysTray(primaryStage_);
        createSysTray();
        return sysTray_;
    }

    protected void bindExitIndicator(BooleanProperty isExit) {
        exitIndicator_.bindBidirectional(isExit);
    }

    private void configureSysTray(Stage primaryStage) {
        Platform.setImplicitExit(false);    // Set this up before creating the trays
        // Enables the app to run normally until the app calls exit, even if the last app window is closed.
        primaryStage.setOnCloseRequest(windowEvent -> {
            exitIndicator_.set(false);exitIndicator_.set(true);
        });
    }

```
###### procrastinate\ui\SystemTrayHandler.java
``` java
    private PopupMenu createPopupMenu() {
        PopupMenu menu = new PopupMenu();

        MenuItem menuExit = new MenuItem(TRAY_MENU_EXIT);
        menuExit.addActionListener(actionEvent -> {
            exitIndicator_.set(false);
            exitIndicator_.set(true);
        });

        MenuItem menuShow = new MenuItem(TRAY_MENU_SHOW_OR_HIDE);
        menuShow.addActionListener(actionEvent -> windowHideOrShow());

        menu.add(menuShow);
        menu.add(menuExit);
        return menu;
    }

    private Image createSysTrayIconImage() {
        // Load an image as system tray icon image. Auto resize is enabled in createSysTrayIcon method.
        BufferedImage img = null;

        try {
            img = ImageIO.read(SystemTrayHandler.class.getResource(TRAY_IMAGE_ICON));
        } catch (IOException e) {
            System.err.println(MESSAGE_UNABLE_TO_LOAD_ICON_IMAGE);
        }

        return img;
    }

    private TrayIcon createSysTrayIcon(Image iconImage) {
        TrayIcon trayIcon;

        if (isWindowsOs()) {
            trayIcon = new TrayIcon(iconImage, TRAY_ICON_TITLE, popupMenu_);
        } else {
            trayIcon = new TrayIcon(iconImage, TRAY_ICON_TITLE);
        }

        trayIcon.setImageAutoSize(true);
        trayIcon.addMouseListener(createIconClickListener());
        trayIcon.addMouseMotionListener(createIconMouseMotionListener());

        return trayIcon;
    }

    // ================================================================================
    // Utility methods
    // ================================================================================

    protected void windowHideOrShow() {
        if (isMouseEntered_ && primaryStage_.isShowing()) {
            isMouseEntered_ = false;

            Platform.runLater(() -> {
                primaryStage_.show();
                primaryStage_.toFront();
                userInputField_.requestFocus();
            });

        } else if (primaryStage_.isShowing()) {
            if (isWindowsOs()) {
                showMinimiseMessage();
            }

            Platform.runLater(() -> primaryStage_.hide());

        } else {
            Platform.runLater(() -> {
                primaryStage_.show();
                primaryStage_.toFront();
                userInputField_.requestFocus();
            });
        }
    }

    private boolean isWindowsOs() {
        return System.getProperty(OS_CHECK_NAME).startsWith(OS_CHECK_WINDOWS);
    }

    private void showMinimiseMessage() {
        if (!shownMinimiseMessage_) {
            sysTrayIcon_.displayMessage(TRAY_MESSAGE_TITLE,
                                       TRAY_MESSAGE_DESCRIPTION,
                                       TrayIcon.MessageType.INFO);
            shownMinimiseMessage_ = true;
        }
    }

    private MouseAdapter createIconClickListener() {
        return new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    if (!isWindowsOs()) {
                        invisibleFrame_.setVisible(true);
                        popupMenu_.show(invisibleFrame_, e.getX(), e.getY());
                    }

                } else {
                    windowHideOrShow();
                }
            }
        };
    }

    private MouseAdapter createIconMouseMotionListener() {
        return new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (primaryStage_.isShowing() && !primaryStage_.isFocused()) {
                    isMouseEntered_ = true;
                }
            }
        };
    }
}
```
###### procrastinate\ui\WindowHandler.java
``` java
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

```
