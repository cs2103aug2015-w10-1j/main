package procrastinate.ui;

import javafx.application.Platform;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class SystemTrayHandler {

    // ================================================================================
    // Message strings
    // ================================================================================

    private static final String TRAY_MENU_SHOW_OR_HIDE = "Show/Hide";
    private static final String TRAY_MENU_EXIT = "Exit";
    private static final String TRAY_MESSAGE_DESCRIPTION = "Access or exit Procrastinate from here.";
    private static final String TRAY_MESSAGE_TITLE = "Procrastinate is still running!";
    private static final String TRAY_IMAGE_ICON = "icon.png";
    private static final String TRAY_ICON_TITLE = "Procrastinate";

    // ================================================================================
    // Class variables
    // ================================================================================

    private boolean isWindowHidden = false;
    private boolean shownMinimiseMessage = false;

    private Stage primaryStage;
    private SystemTray sysTray;
    private javafx.scene.control.TextField userInputField;
    private TrayIcon sysTrayIcon;

    // ================================================================================
    // SystemTrayHandler methods
    // ================================================================================

    public SystemTrayHandler(Stage primaryStage, javafx.scene.control.TextField userInputField) {
        this.primaryStage = primaryStage;
        this.userInputField = userInputField;
    }

    public SystemTray initialiseTray() {
        configureSysTray(primaryStage);
        createSysTray(primaryStage);
        return sysTray;
    }

    private void configureSysTray(Stage primaryStage) {
        Platform.setImplicitExit(false);    // Set this up before creating the trays
        // Enables the app to run normally until the app calls exit, even if the last app window is closed.
        primaryStage.setOnCloseRequest(windowEvent -> {
            if (isSysTraySupported()) {
                primaryStage.hide();
                isWindowHidden = true;
                if (isWindowsOs()) {
                    // Windows check needed as MacOS doesn't recognise balloon messages
                    showMinimiseMessage();
                }
            } else {
                System.exit(0);
            }
        });
    }

    private void createSysTray(Stage primaryStage) {
        sysTray = SystemTray.getSystemTray();
        Image sysTrayIconImage = createSysTrayIconImage();
        PopupMenu sysTrayPopup = createSysTrayMenu(primaryStage);
        sysTrayIcon = createSysTrayIcon(sysTrayIconImage, sysTrayPopup, primaryStage);
        try {
            sysTray.add(sysTrayIcon);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    private PopupMenu createSysTrayMenu(Stage primaryStage) {
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
        Image iconImage = Toolkit.getDefaultToolkit().getImage(TRAY_IMAGE_ICON);
        return iconImage;
    }

    private TrayIcon createSysTrayIcon(Image iconImage, PopupMenu popupMenu, Stage primaryStage) {
        TrayIcon trayIcon = new TrayIcon(iconImage, TRAY_ICON_TITLE, popupMenu);
        trayIcon.setImageAutoSize(true);
        trayIcon.setPopupMenu(popupMenu);
        trayIcon.addMouseListener(createIconClickListener());
        return trayIcon;
    }

    // ================================================================================
    // Utility methods
    // ================================================================================

    private boolean isSysTraySupported() {
        return  SystemTray.isSupported();
    }

    private boolean isWindowsOs() {
        return System.getProperty("os.name").startsWith("Windows");
    }

    private boolean isLeftClick(MouseEvent e) {
        return e.getButton() == MouseEvent.BUTTON1;
    }

    private void windowHideOrShow() {
        if (isWindowHidden) {
            Platform.runLater(() -> {
                primaryStage.show();
                userInputField.requestFocus();
                primaryStage.toFront();
            });
            isWindowHidden = false;
        } else {
            Platform.runLater(() -> primaryStage.hide());
            isWindowHidden = true;
        }
    }

    private void showMinimiseMessage(){
        if (!shownMinimiseMessage) {
            sysTrayIcon.displayMessage(TRAY_MESSAGE_TITLE,
                    TRAY_MESSAGE_DESCRIPTION,
                    TrayIcon.MessageType.INFO);
            shownMinimiseMessage = true;
        }
    }

    private MouseListener createIconClickListener(){
        MouseListener iconClickListener = new MouseListener() {
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
        return iconClickListener;
    }

}
