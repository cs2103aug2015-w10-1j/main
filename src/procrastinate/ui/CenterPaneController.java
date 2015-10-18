package procrastinate.ui;

import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import procrastinate.task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CenterPaneController {

    // ================================================================================
    // Screen change keys
    // ================================================================================

    protected static final int SCREEN_MAIN = 1;
    protected static final int SCREEN_SEARCH = 2;
    protected static final int SCREEN_HELP = 3;  // Maybe should use arraylist of these integers/string to reference the integers?

    // ================================================================================
    // Message strings
    // ================================================================================

    private static final String LOCATION_MAIN_SCREEN_LAYOUT = "MainScreen.fxml";
    private static final String LOCATION_HELP_SCREEN_LAYOUT = "HelpScreen.fxml";
    private static final String LOCATION_SEARCH_SCREEN_LAYOUT = "SearchScreen.fxml";

    // ================================================================================
    // Class variables
    // ================================================================================

    private HashMap<Integer, Node> controlledScreens;   // CHANGE TO SWITCH
    /**
     * NEED TO CHANGE HASHMAP TO SWITCH,
     * set up subcontrollers for each screen.
     *
     * or
     * SPLIT OUT SCREENS TO LIKE DISPLAY, HELP ETC.
     */

    private Node mainScreenNode;
    private Node searchScreenNode;
    private Node helpScreenNode;

    private MainScreen mainScreen;
    private HelpScreen helpScreen;

    private StackPane centerStackPane;
    private Node currentScreen;

    // ================================================================================
    // CenterPaneController methods
    // ================================================================================

    protected CenterPaneController(StackPane centerStackPane) {
        this.controlledScreens = new HashMap<Integer, Node>();
        this.centerStackPane = centerStackPane;

        initialiseScreens();
        currentScreen = mainScreenNode;
        mainScreenNode.setOpacity(1); // Setup straight into main screen.
    }

    // ================================================================================
    // Utility methods
    // ================================================================================

    /**
     * Each screen is mapped to a key for use in UI/Logic for screen changing
     * @param screenKey
     */
    protected void changeScreen(int screenKey) {
        Node screen = controlledScreens.get(screenKey);
        setScreen(screen);
    }

    /**
     * Updates the given task list onto the MainScreen view
     * @param taskList
     */
    protected void updateMainScreen(List<Task> taskList) {
        mainScreen.updateTaskList(taskList);
    }

    protected void hideHelpOverlay() {
        helpScreenNode.setOpacity(0);
    }

    /**
     * Changes the top most screen to the screen specified, animations/fading is not yet implemented
     * @param screen
     */
    private void setScreen(Node screen) {
//        currentScreen.setOpacity(0);      // Disabled for now since there are no other screens
        screen.toFront();
        screen.setOpacity(1);
    }

    // ================================================================================
    // Init methods
    // ================================================================================

    private void initialiseScreens() {
        ArrayList<Node> screensList = createScreens();
        centerStackPane.getChildren().addAll(screensList);
    }

    private void mapScreen(int screenKey, Node newScreen) {
        controlledScreens.put(screenKey, newScreen);
    }

    /**
     * This creates and holds a list of the screens that can be easily added onto the center pane
     * @return list of screens
     */
    private ArrayList<Node> createScreens() {
        ArrayList<Node> screensList = new ArrayList<>();

        // Main Screen setup
        screensList.add(createMainScreen());

        // Help Screen setup
        screensList.add(createHelpScreen());

        // Search screen setup

        return screensList;
    }

    private Node createHelpScreen() {
        this.helpScreen = new HelpScreen(LOCATION_HELP_SCREEN_LAYOUT);
        this.helpScreenNode = helpScreen.getNode();
        helpScreenNode.setOpacity(0);
        mapScreen(SCREEN_HELP, helpScreenNode);
        return helpScreenNode;
    }

    private Node createMainScreen() {
        this.mainScreen = new MainScreen(LOCATION_MAIN_SCREEN_LAYOUT);
        this.mainScreenNode = mainScreen.getNode();
        mainScreenNode.setOpacity(0);
        mapScreen(SCREEN_MAIN, mainScreenNode);
        return mainScreenNode;
    }
}
