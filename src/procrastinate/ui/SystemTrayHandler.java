package procrastinate.ui;

import javafx.application.Platform;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
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

    private Stage primaryStage;
    private SystemTray sysTray;
    private TrayIcon sysTrayIcon;
    private TextField userInputField;

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
        menuExit.addActionListener(actionEvent -> System.exit(0));

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
        return trayIcon;
    }

    // ================================================================================
    // Utility methods
    // ================================================================================

    protected void windowHideOrShow() {
        if (primaryStage.isShowing()) {
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
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        };
    }

}
