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

    //@@author A0080485B-reused
    private void createSysTray() {
        sysTray_ = SystemTray.getSystemTray();
        popupMenu_ = createPopupMenu();
        sysTrayIcon_ = createSysTrayIcon(createSysTrayIconImage());

        if (!isWindowsOs()) {
            invisibleFrame_ = new Frame();
            invisibleFrame_.setUndecorated(true);
            invisibleFrame_.setResizable(false);
            invisibleFrame_.add(popupMenu_);
        }

        try {
            sysTray_.add(sysTrayIcon_);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    //@@author A0121597B-reused
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
