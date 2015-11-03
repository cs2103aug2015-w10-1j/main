# A0121597Breused
###### procrastinate\ui\CenterPaneController.java
``` java
    // Required since each screen node is wrapped inside a scrollPane.
    private void addMouseDragListeners(Node screenNode) {
        Node scrollPaneNode = ((ScrollPane)screenNode.lookup("#scrollPane")).getContent();
        scrollPaneNode.setOnMousePressed((mouseEvent) -> {
            xOffset = mouseEvent.getSceneX();
            yOffset = mouseEvent.getSceneY();
        });
        scrollPaneNode.setOnMouseDragged((mouseEvent) -> {
            centerStackPane.getScene().getWindow().setX(mouseEvent.getScreenX() - xOffset);
            centerStackPane.getScene().getWindow().setY(mouseEvent.getScreenY() - yOffset);
        });
    }

    // ================================================================================
    // Test methods
    // ================================================================================

```
###### procrastinate\ui\DialogPopupHandler.java
``` java
package procrastinate.ui;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

public class DialogPopupHandler {

    // ================================================================================
    // Message Strings
    // ================================================================================

    private static final String MESSAGE_TITLE = "Error";
    private static final String MESSAGE_HEADER = "An error has occurred with the following message:";

    private static final String BUTTON_MESSAGE_OK = "OK";
    private static final String BUTTON_MESSAGE_CANCEL = "Cancel";

    // ================================================================================
    // Class variables
    // ================================================================================

    private Stage primaryStage;

    // ================================================================================
    // DialogPopupHandler methods
    // ================================================================================

    protected DialogPopupHandler(Stage primaryStage) {
        // Set up the parent stage to retrieve information if required
        this.primaryStage = primaryStage;
    }

    /**
     * Creates an error dialog that displays the given message string
     *
     * @param message
     */
    protected void createErrorDialogPopup(String message) {
        Alert dialog = new Alert(Alert.AlertType.ERROR);
        dialog.initOwner(primaryStage);

        dialog.setTitle(MESSAGE_TITLE);
        dialog.setHeaderText(MESSAGE_HEADER);
        dialog.setContentText(message);

        dialog.showAndWait();
    }

    /**
     * Creates an error dialog that takes in an Exception and displays the exception message with its stack trace
     * in an expandable region.
     *
     * @param exception
     */
    protected void createErrorDialogPopupWithTrace(Exception exception) {
        Alert dialog = new Alert(Alert.AlertType.ERROR);
        dialog.initOwner(primaryStage);

        // Retrieve the stack trace as String
        StringWriter stringWriter = new StringWriter();
        exception.printStackTrace(new PrintWriter(stringWriter));
        String stackTrace = stringWriter.toString();

        dialog.setTitle(MESSAGE_TITLE);
        dialog.setHeaderText(MESSAGE_HEADER);
        dialog.setContentText(exception.getMessage());

        // Place the stack trace into a TextArea for display
        TextArea textArea = new TextArea();
        textArea.setWrapText(true);
        textArea.setEditable(false);
        textArea.setText(stackTrace);

        dialog.getDialogPane().setExpandableContent(textArea);
        dialog.showAndWait();
    }

    protected boolean createErrorDialogPopupWithConfirmation(String message) {
        Alert dialog = new Alert(Alert.AlertType.ERROR);
        dialog.initOwner(primaryStage);

        dialog.setTitle(MESSAGE_TITLE);
        dialog.setHeaderText(MESSAGE_HEADER);
        dialog.setContentText(message);

        ButtonType okBtn = new ButtonType(BUTTON_MESSAGE_OK, ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelBtn = new ButtonType(BUTTON_MESSAGE_CANCEL, ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getButtonTypes().setAll(okBtn, cancelBtn);

        Optional<ButtonType> choice = dialog.showAndWait();
        if (choice.get().equals(okBtn)) {
            return true;
        } else {
            return false;
        }
    }
}
```
###### procrastinate\ui\SystemTrayHandler.java
``` java
package procrastinate.ui;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class SystemTrayHandler {

    // ================================================================================
    // Message strings
    // ================================================================================

    private static final String TRAY_ICON_TITLE = "Procrastinate";
    private static final String TRAY_IMAGE_ICON = "images/icon.png";
    private static final String TRAY_MENU_SHOW_OR_HIDE = "Show/Hide";
    private static final String TRAY_MENU_EXIT = "Exit";
    private static final String TRAY_MESSAGE_DESCRIPTION = "Access or exit Procrastinate from here.";
    private static final String TRAY_MESSAGE_TITLE = "Procrastinate is still running!";

    private static final String MESSAGE_UNABLE_TO_LOAD_ICON_IMAGE = "Unable to load icon image for system tray.";
    private static final String OS_CHECK_WINDOWS = "Windows";
    private static final String OS_CHECK_NAME = "os.name";

    // ================================================================================
    // Class variables
    // ================================================================================

    private boolean shownMinimiseMessage = false;
    private BooleanProperty exitIndicator = new SimpleBooleanProperty(false);

    private Stage primaryStage;
    private SystemTray sysTray;
    private TrayIcon sysTrayIcon;
    private TextField userInputField;

    private boolean isMouse = false;

    // ================================================================================
    // SystemTrayHandler methods
    // ================================================================================

    protected SystemTrayHandler(Stage primaryStage, TextField userInputField) {
        this.primaryStage = primaryStage;
        this.userInputField = userInputField;
    }

    protected SystemTray initialiseTray() {
        configureSysTray(primaryStage);
        createSysTray();
        return sysTray;
    }

    protected void bindExitIndicator(BooleanProperty isExit) {
        exitIndicator.bindBidirectional(isExit);
    }

    private void configureSysTray(Stage primaryStage) {
        Platform.setImplicitExit(false);    // Set this up before creating the trays
        // Enables the app to run normally until the app calls exit, even if the last app window is closed.
        primaryStage.setOnCloseRequest(windowEvent -> {
            if (isSysTraySupported()) {
                primaryStage.hide();
                if (isWindowsOs()) {
                    // Windows check needed as MacOS doesn't recognise balloon messages
                    showMinimiseMessage();
                }
            } else {
                System.exit(0);
            }
        });
    }

    private void createSysTray() {
        sysTray = SystemTray.getSystemTray();
        Image sysTrayIconImage = createSysTrayIconImage();
        PopupMenu sysTrayPopup = createSysTrayMenu();
        sysTrayIcon = createSysTrayIcon(sysTrayIconImage, sysTrayPopup);
        try {
            sysTray.add(sysTrayIcon);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    private PopupMenu createSysTrayMenu() {
        PopupMenu menu = new PopupMenu();

        MenuItem menuExit = new MenuItem(TRAY_MENU_EXIT);
        menuExit.addActionListener(actionEvent -> exitIndicator.set(true));

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
        Dimension trayIconSize = sysTray.getTrayIconSize();
        Image trayImage = img.getScaledInstance(trayIconSize.width, trayIconSize.height, Image.SCALE_SMOOTH);
        return trayImage;
    }

    private TrayIcon createSysTrayIcon(Image iconImage, PopupMenu popupMenu) {
        TrayIcon trayIcon = new TrayIcon(iconImage, TRAY_ICON_TITLE, popupMenu);
        trayIcon.setImageAutoSize(true);
        trayIcon.setPopupMenu(popupMenu);
        trayIcon.addMouseListener(createIconClickListener());
        trayIcon.addMouseMotionListener(createMouseMotionListener());
        return trayIcon;
    }

    // ================================================================================
    // Utility methods
    // ================================================================================

    protected void windowHideOrShow() {
        if (isMouse && primaryStage.isShowing()) {
            isMouse = false;
        }
        else if (primaryStage.isShowing()) {
            if (isWindowsOs()) {
                showMinimiseMessage();
            }
            Platform.runLater(() -> primaryStage.hide());
        } else {
            Platform.runLater(() -> {
                primaryStage.show();
                userInputField.requestFocus();
                primaryStage.toFront();
            });
        }
    }

    private boolean isSysTraySupported() {
        return SystemTray.isSupported();
    }

    private boolean isWindowsOs() {
        return System.getProperty(OS_CHECK_NAME).startsWith(OS_CHECK_WINDOWS);
    }

    private boolean isLeftClick(MouseEvent e) {
        return e.getButton() == MouseEvent.BUTTON1;
    }

    private void showMinimiseMessage() {
        if (!shownMinimiseMessage) {
            sysTrayIcon.displayMessage(TRAY_MESSAGE_TITLE,
                    TRAY_MESSAGE_DESCRIPTION,
                    TrayIcon.MessageType.INFO);
            shownMinimiseMessage = true;
        }
    }

    private MouseListener createIconClickListener() {
        return new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (isWindowsOs() && isLeftClick(e)) {
                    // Windows check needed as MacOS doesn't differentiate buttons
                    Platform.runLater(() -> {
                        primaryStage.requestFocus();
                        primaryStage.toFront();
                    });
                    windowHideOrShow();
                }
            }

            // Unused methods, left empty.
            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (primaryStage.isShowing() && !primaryStage.isFocused()) {
                    isMouse = true;
                    Platform.runLater(() -> {
                        primaryStage.toFront();
                    });
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        };
    }

    private MouseMotionListener createMouseMotionListener() {
        return new MouseMotionListener() {

            @Override
            public void mouseMoved(MouseEvent e) {
                if (primaryStage.isShowing() && !primaryStage.isFocused()) {
                    isMouse = true;
                }
            }

            // Unused methods, left empty.
            @Override
            public void mouseDragged(MouseEvent e) {
            }
        };
    }
}
```
###### procrastinate\ui\WindowHandler.java
``` java
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

```
