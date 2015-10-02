package procrastinate;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {

    // ================================================================================
    // Message strings
    // ================================================================================

    private static final String WINDOW_TITLE = "Procrastinate";
    private static final double WINDOW_WIDTH = 500;
    private static final double WINDOW_MIN_WIDTH = 500;
    private static final double WINDOW_HEIGHT = 600;
    private static final double WINDOW_MIN_HEIGHT = 600;

    private static final String TRAY_MENU_SHOW_OR_HIDE = "Show/Hide";
    private static final String TRAY_MENU_EXIT = "Exit";
    private static final String TRAY_MESSAGE_DESCRIPTION = "Access or exit Procrastinate from here.";
    private static final String TRAY_MESSAGE_TITLE = "Procrastinate is still running!";

    private static final String IMAGE_ICON = "icon.png";

    // ================================================================================
    // Class variables
    // ================================================================================

    private static double xOffset, yOffset;

    private Stage primaryStage;
    private TrayIcon sysTrayIcon; // required for displaying message through the tray icon
    private boolean shownMinimiseMessage = false;
    private boolean isHidden = false;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Parent root;
        try {
            root = FXMLLoader.load(getClass().getResource("MainWindowLayout.fxml"));
            initPrimaryStage(primaryStage, root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ================================================================================
    // Utility methods
    // ================================================================================

    private void initPrimaryStage(Stage primaryStage, Parent root) {
        this.primaryStage = primaryStage;
        configurePrimaryStage(primaryStage, root);
        if (isSysTraySupported()) {
            configureSysTray(primaryStage);
            createSysTray(primaryStage);
        }
        primaryStage.show();
    }

    private void configurePrimaryStage(Stage primaryStage, Parent root) {
        primaryStage.setTitle(WINDOW_TITLE);
        primaryStage.setMinHeight(WINDOW_MIN_HEIGHT);
        primaryStage.setMinWidth(WINDOW_MIN_WIDTH);
        primaryStage.setScene(new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT));
        //overwriteDecorations(primaryStage, root);
    }

    // Removes all borders and buttons, enables dragging of window through frame
    // Unused for now
    @SuppressWarnings("unused")
    private void overwriteDecorations(Stage primaryStage, Parent root) {
        primaryStage.initStyle(StageStyle.UNDECORATED);
        root.setOnMousePressed((event) -> {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            });
        root.setOnMouseDragged((event) -> {
                primaryStage.setX(event.getScreenX() - xOffset);
                primaryStage.setY(event.getScreenY() - yOffset);
            });
    }

    // ================================================================================
    // System tray methods
    // ================================================================================

    private void configureSysTray(Stage primaryStage) {
        Platform.setImplicitExit(false); // Set this up before creating the trays
        primaryStage.setOnCloseRequest(e -> {
            if (isSysTraySupported()) {
                primaryStage.hide();
                isHidden = true;
                if (isWindowsOs()) {
                    showMinimiseMessage();
                }
            } else {
                System.exit(0);
            }
        });
    }

    private void createSysTray(Stage primaryStage) {
        SystemTray sysTray = SystemTray.getSystemTray();
        Image sysTrayIconImage = createSysTrayIconImage();
        PopupMenu sysTrayPopup = createSysTrayMenu(primaryStage);
        sysTrayIcon = createSysTrayIcon(sysTrayIconImage, sysTrayPopup, primaryStage);
        try {
            sysTray.add(sysTrayIcon);
        } catch (AWTException e) {
            e.printStackTrace();
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

    private PopupMenu createSysTrayMenu(Stage primaryStage) {
        PopupMenu menu = new PopupMenu();

        MenuItem menuExit = new MenuItem(TRAY_MENU_EXIT);
        menuExit.addActionListener(e -> System.exit(0));

        MenuItem menuShow = new MenuItem(TRAY_MENU_SHOW_OR_HIDE);
        menuShow.addActionListener(e -> windowHideOrShow());

        menu.add(menuShow);
        menu.add(menuExit);
        return menu;
    }

    private Image createSysTrayIconImage() {
        // Load image as system tray icon image
        Image iconImage = Toolkit.getDefaultToolkit().getImage(IMAGE_ICON);
        return iconImage;
    }

    private TrayIcon createSysTrayIcon(Image iconImage, PopupMenu popupMenu, Stage primaryStage) {
        TrayIcon trayIcon = new TrayIcon(iconImage, WINDOW_TITLE, popupMenu);
        trayIcon.setImageAutoSize(true);
        trayIcon.setPopupMenu(popupMenu);
        trayIcon.addMouseListener(createIconClickListener());
        return trayIcon;
    }

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
        if (isHidden) {
            Platform.runLater(() -> {
                primaryStage.show();
                primaryStage.toFront();
            });
            isHidden = false;
        } else {
            Platform.runLater(() ->
                            primaryStage.hide()
            );
            isHidden = true;
        }
    }

    private MouseListener createIconClickListener(){
        MouseListener iconClickListener = new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (isWindowsOs() && isLeftClick(e)) {
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
