//@@author A0121597B-reused
package procrastinate.ui;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
        menuExit.addActionListener(actionEvent -> {exitIndicator.set(false);exitIndicator.set(true);});

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

    private TrayIcon createSysTrayIcon(Image iconImage, PopupMenu popupMenu) {
        TrayIcon trayIcon = new TrayIcon(iconImage, TRAY_ICON_TITLE, popupMenu);
        trayIcon.setImageAutoSize(true);
        trayIcon.addMouseListener(createIconClickListener());
        trayIcon.addMouseMotionListener(createIconMouseMotionListener());
        return trayIcon;
    }

    // ================================================================================
    // Utility methods
    // ================================================================================

    protected void windowHideOrShow() {
        if (isMouse && primaryStage.isShowing()) {
            isMouse = false;
            Platform.runLater(() -> {
                primaryStage.show();
                primaryStage.toFront();
                userInputField.requestFocus();
            });
        }
        else if (primaryStage.isShowing()) {
            if (isWindowsOs()) {
                showMinimiseMessage();
            }
            Platform.runLater(() -> primaryStage.hide());
        } else {
            Platform.runLater(() -> {
                primaryStage.show();
                primaryStage.toFront();
                userInputField.requestFocus();
            });
        }
    }

    private boolean isSysTraySupported() {
        return SystemTray.isSupported();
    }

    private boolean isWindowsOs() {
        return System.getProperty(OS_CHECK_NAME).startsWith(OS_CHECK_WINDOWS);
    }

    private void showMinimiseMessage() {
        if (!shownMinimiseMessage) {
            sysTrayIcon.displayMessage(TRAY_MESSAGE_TITLE,
                    TRAY_MESSAGE_DESCRIPTION,
                    TrayIcon.MessageType.INFO);
            shownMinimiseMessage = true;
        }
    }

    private MouseAdapter createIconClickListener() {
        return new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
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
                if (primaryStage.isShowing() && !primaryStage.isFocused()) {
                    isMouse = true;
                }
            }
        };
    }
}
