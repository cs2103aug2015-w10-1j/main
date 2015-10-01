package procrastinate;

import java.awt.*;
import java.io.IOException;

import javafx.application.Application;
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
        configurePrimaryStage(primaryStage, root);
        if (checkSysTraySupport()) {
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

    private void createSysTray(Stage primaryStage) {
        SystemTray sysTray = SystemTray.getSystemTray();
        TrayIcon sysTrayIcon = createSysTrayIcon();
        sysTrayIcon.setImageAutoSize(true);
        PopupMenu sysTrayPopup = createSysTrayMenu(primaryStage);
        sysTrayIcon.setPopupMenu(sysTrayPopup);
        try {
            sysTray.add(sysTrayIcon);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    private TrayIcon createSysTrayIcon() {
        // Load image as system tray icon image
        Image iconImage = Toolkit.getDefaultToolkit().getImage(ICON_IMAGE);
        return new TrayIcon(iconImage, WINDOW_TITLE);
    }

    private PopupMenu createSysTrayMenu(Stage primaryStage) {
        PopupMenu menu = new PopupMenu();

        MenuItem menuExit = new MenuItem("Exit");
        menuExit.addActionListener(e -> System.exit(0));

        menu.add(menuExit);
        return menu;
    }

    private boolean checkSysTraySupport() {
        return  SystemTray.isSupported();
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
