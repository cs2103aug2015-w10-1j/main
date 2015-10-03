package procrastinate.ui;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.converter.NumberStringConverter;

import procrastinate.task.Task;

public class UI {

    private static final Logger logger = Logger.getLogger(UI.class.getName());

    // ================================================================================
    // Message strings
    // ================================================================================

    private static final String DEBUG_UI_INIT = "UI initialised. View is now loaded!";

    private static final String IMAGE_ICON = "icon.png";

    private static final String LOCATION_MAIN_WINDOW_LAYOUT = "MainWindowLayout.fxml";

    private static final String MESSAGE_WELCOME = "What would you like to Procrastinate today?";

    private static final String TRAY_MENU_SHOW_OR_HIDE = "Show/Hide";
    private static final String TRAY_MENU_EXIT = "Exit";
    private static final String TRAY_MESSAGE_DESCRIPTION = "Access or exit Procrastinate from here.";
    private static final String TRAY_MESSAGE_TITLE = "Procrastinate is still running!";

    private static final String UI_NUMBER_SEPARATOR = ". ";

    private static final String WINDOW_TITLE = "Procrastinate";
    private static final double WINDOW_WIDTH = 500;
    private static final double WINDOW_MIN_WIDTH = 500;
    private static final double WINDOW_HEIGHT = 600;
    private static final double WINDOW_MIN_HEIGHT = 600;

    // ================================================================================
    // Class variables
    // ================================================================================

    private IntegerProperty taskCount = new SimpleIntegerProperty(1);

    private ObservableList<String> taskList = FXCollections.observableArrayList();

    private Parent root;

    private Stage primaryStage;

    private StringProperty taskCountFormatted = new SimpleStringProperty();
    private StringProperty taskCountString = new SimpleStringProperty();

    // Window or System Tray related variables
    private static double xOffset, yOffset;

    private TrayIcon sysTrayIcon; // required for displaying message through the tray icon

    private boolean isWindowHidden = false;
    private boolean shownMinimiseMessage = false;

    // ================================================================================
    // FXML field variables
    // ================================================================================

    @FXML private BorderPane mainBorderPane;
    @FXML private Label statusLabel;
    @FXML private ListView<String> taskListView;
    @FXML private TextField userInputField;

    // ================================================================================
    // UI methods
    // ================================================================================

    public UI() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(LOCATION_MAIN_WINDOW_LAYOUT));
        loader.setController(this); // Required due to different package declaration from Main
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // This auto gets called from the UI constructor when load is executed.
    public void initialize() {
        initTaskDisplay();

        logger.log(Level.INFO, DEBUG_UI_INIT);
    }

    public void setUpBinding(StringProperty userInput, StringProperty statusLabelText) {
        initBinding(userInput, statusLabelText);
    }

    public void setUpStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
        initPrimaryStage();
    }

    public void updateTaskList(List<Task> tasks) {
        taskList.clear();
        taskCount.set(1);
        for (Task task : tasks) {
            taskList.add(taskCountFormatted.get() + task.getDescription());
            taskCount.set(taskCount.get() + 1);
        }
        updateListView();
    }

    // ================================================================================
    // Init methods
    // ================================================================================

    private void initBinding(StringProperty userInput, StringProperty statusLabelText) {
        userInput.bindBidirectional(userInputField.textProperty());
        statusLabelText.bindBidirectional(statusLabel.textProperty());
        taskCountString.bindBidirectional(taskCount, new NumberStringConverter());
        taskCountFormatted.bind(Bindings.concat(taskCountString).concat(UI_NUMBER_SEPARATOR));
    }

    private void initPrimaryStage() {
        configurePrimaryStage(primaryStage, root);
        if (isSysTraySupported()) {
            configureSysTray(primaryStage);
            createSysTray(primaryStage);
        }
        primaryStage.show();
    }

    private void initTaskDisplay() {
        taskListView.setPlaceholder(new Label(MESSAGE_WELCOME));
    }

    // ================================================================================
    // Window Configurations
    // ================================================================================

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
        root.setOnMousePressed((mouseEvent) -> {
                xOffset = mouseEvent.getSceneX();
                yOffset = mouseEvent.getSceneY();
            });
        root.setOnMouseDragged((mouseEvent) -> {
                primaryStage.setX(mouseEvent.getScreenX() - xOffset);
                primaryStage.setY(mouseEvent.getScreenY() - yOffset);
            });
    }

    // ================================================================================
    // System tray methods
    // ================================================================================

    private void configureSysTray(Stage primaryStage) {
        Platform.setImplicitExit(false); // Set this up before creating the trays
        primaryStage.setOnCloseRequest(windowEvent -> {
            if (isSysTraySupported()) {
                primaryStage.hide();
                isWindowHidden = true;
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

    // ================================================================================
    // Utility methods
    // ================================================================================

    public void clearInput() {
        userInputField.clear();
    }

    private void updateListView() {
        taskListView.setItems(taskList);
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
        if (isWindowHidden) {
            Platform.runLater(() -> {
                primaryStage.show();
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

    // ================================================================================
    // Getter methods
    // ================================================================================

    public TextField getUserInputField() {
        return userInputField;
    }
}
