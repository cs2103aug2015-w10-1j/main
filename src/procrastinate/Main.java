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

    private static final String WINDOW_TITLE = "Procrastinate";
    private static final double WINDOW_WIDTH = 500;
    private static final double WINDOW_MIN_WIDTH = 500;
    private static final double WINDOW_HEIGHT = 600;
    private static final double WINDOW_MIN_HEIGHT = 600;
    private static double xOffset, yOffset;

    private static final String ICON_IMAGE = "testicon.png";

    private Stage primaryStage;
    private TrayIcon sysTrayIcon; // required for displaying msg through the icon

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Parent root;
        try {
            root = FXMLLoader.load(getClass().getResource("MainWindowLayout.fxml"));
//             overwriteDecorations(primaryStage, root); //Removes all borders and buttons, overwrites mouse events to enable dragging of window
            initPrimaryStage(primaryStage, root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initPrimaryStage(Stage primaryStage, Parent root) {
        this.primaryStage = primaryStage;
        configurePrimaryStage(primaryStage, root);
        if (checkSysTraySupport()) {
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
    }

    private void configureSysTray(Stage primaryStage) {
        primaryStage.setOnCloseRequest(e -> {
            if (checkSysTraySupport()) {
                primaryStage.hide();
                showMinimizeMsg();
            } else {
                System.exit(0);
            }
        });
        Platform.setImplicitExit(false);
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

    private boolean checkSysTraySupport() {
        return  SystemTray.isSupported();
    }

    private void showMinimizeMsg(){
        sysTrayIcon.displayMessage("Procrastinate is still running!", "Access or exit Procrastinate from here.", TrayIcon.MessageType.INFO);
    }

    private PopupMenu createSysTrayMenu(Stage primaryStage) {
        PopupMenu menu = new PopupMenu();

        MenuItem menuExit = new MenuItem("Exit");
        menuExit.addActionListener(e -> System.exit(0));

        MenuItem menuShow = new MenuItem("Show");
        menuShow.addActionListener(e -> Platform.runLater(() -> primaryStage.show()));

        menu.add(menuShow);
        menu.add(menuExit);
        return menu;
    }

    private Image createSysTrayIconImage() {
        // Load image as system tray icon image
        Image iconImage = Toolkit.getDefaultToolkit().getImage(ICON_IMAGE);
        return iconImage;
    }

    private TrayIcon createSysTrayIcon(Image iconImage, PopupMenu popupMenu, Stage primaryStage) {
        TrayIcon trayIcon = new TrayIcon(iconImage, WINDOW_TITLE, popupMenu);
        trayIcon.setImageAutoSize(true);
        trayIcon.setPopupMenu(popupMenu);
        trayIcon.addMouseListener(createIconClickListenr());
        return trayIcon;
    }

    private MouseListener createIconClickListenr(){
        MouseListener iconClickListener = new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Platform.runLater(() -> primaryStage.show());
            }

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

}
