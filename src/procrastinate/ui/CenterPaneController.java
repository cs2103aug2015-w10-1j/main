package procrastinate.ui;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import procrastinate.task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CenterPaneController {

    // ================================================================================
    // Screen change keys
    // ================================================================================

    protected static final int SCREEN_MAIN = 1;
    protected static final int SCREEN_HELP = 2;  // Maybe should use arraylist of these integers/string to reference the integers?

    // ================================================================================
    // Message strings
    // ================================================================================

    private static final String LOCATION_CENTER_SCREEN_LAYOUT = "views/CenterScreen.fxml";
    private static final String LOCATION_HELP_OVERLAY_LAYOUT = "views/HelpOverlay.fxml";

    // ================================================================================
    // Animation time values
    // ================================================================================

    private static final double OPACITY_FULL = 1;
    private static final double OPACITY_ZERO = 0;

    // Time values are in milliseconds
    private static final double TIME_HELP_SCREEN_FADEOUT = 300;

    private static final double TIME_SPLASH_SCREEN_FADE = 3000;
    private static final double TIME_SPLASH_SCREEN_FULL_OPACITY = 2000;
    private static final double TIME_SPLASH_SCREEN_INTERRUPT = 2700;

    // ================================================================================
    // Class variables
    // ================================================================================

    private HashMap<Integer, Node> controlledScreens;   // CHANGE TO SWITCH

    private FadeTransition helpOverlayFadeOut;
    private Timeline splashScreenTimeline;

    private Node mainScreenNode;
    private Node helpOverlayNode;

    private MainScreen mainScreen;
    private HelpOverlay helpOverlay;

    private StackPane centerStackPane;
    protected Node currentScreen;       // Changed to protected for testing purposes.

    // ================================================================================
    // CenterPaneController methods
    // ================================================================================

    protected CenterPaneController(StackPane centerStackPane) {
        this.controlledScreens = new HashMap<>();
        this.centerStackPane = centerStackPane;

        initialiseScreens();
        currentScreen = mainScreenNode;
        mainScreenNode.setOpacity(OPACITY_FULL); // Setup straight into main screen.
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
        currentScreen = screen;
        setScreen(screen);
    }

    /**
     * Updates the given task list onto the MainScreen view
     * @param taskList
     */
    protected void updateMainScreen(List<Task> taskList) {
        mainScreen.updateTaskList(taskList);
    }

    /**
     * Hides the current screen overlay for:
     *      - splashScreen: Fast-forwards the fade animation if user starts typing, and builds the actual helpScreen
     *                      once the animation is terminated
     *      - helpScreen: Starts the fade out transition that lasts for 0.5 seconds
     */
    protected void hideScreenOverlay() {
        if (currentScreen != helpOverlayNode) {
            Duration interruptTime = Duration.millis(TIME_SPLASH_SCREEN_INTERRUPT);
            // Only fast forward the timeline if the current time of the animation is smaller than the given
            // interrupt time. Else, just wait for the animation to end.
            if (splashScreenTimeline.getCurrentTime().lessThan(interruptTime)) {
                splashScreenTimeline.jumpTo(Duration.millis(TIME_SPLASH_SCREEN_INTERRUPT));
            }
            splashScreenTimeline.jumpTo(Duration.millis(TIME_SPLASH_SCREEN_FADE));
            // TODO: Set up the helpScreen labels below (Reference/cheat sheet)
        } else {
            helpOverlayFadeOut.playFromStart();
        }
    }

    /**
     * Creates a splash screen that maintains full opacity for 2 seconds before completely fading out in 1 second
     * or until the user starts to type.
     * currentScreen variable is not updated to helpScreen to differentiate splashScreen from helpScreen in the
     * hideScreenOverlay method.
     */
    protected void showSplashScreen() {
        helpOverlayNode.toFront();
        helpOverlayNode.setOpacity(OPACITY_FULL);

        // Set SplashScreen opacity at full for 2 seconds.
        Duration fullOpacityDuration = Duration.millis(TIME_SPLASH_SCREEN_FULL_OPACITY);
        KeyValue fullOpacityKeyValue = new KeyValue(helpOverlayNode.opacityProperty(), OPACITY_FULL);
        KeyFrame fullOpacityFrame = new KeyFrame(fullOpacityDuration, fullOpacityKeyValue);

        // Set SplashScreen to fade out completely at time = 3 seconds
        Duration zeroOpacityDuration = Duration.millis(TIME_SPLASH_SCREEN_FADE);
        KeyValue zeroOpacityKeyValue = new KeyValue(helpOverlayNode.opacityProperty(), OPACITY_ZERO);
        KeyFrame zeroOpacityFrame = new KeyFrame(zeroOpacityDuration, zeroOpacityKeyValue);

        splashScreenTimeline= new Timeline(fullOpacityFrame, zeroOpacityFrame);
        splashScreenTimeline.setOnFinished(e -> setScreen(mainScreenNode));
        splashScreenTimeline.play();
    }

    private FadeTransition getFadeOutTransition(double timeInMs, Node transitingNode) {
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(timeInMs), transitingNode);
        fadeTransition.setFromValue(OPACITY_FULL);
        fadeTransition.setToValue(OPACITY_ZERO);
        return fadeTransition;
    }

    /**
     * Changes the top most screen to the screen specified
     * @param screen
     */
    private void setScreen(Node screen) {
        currentScreen.setOpacity(OPACITY_ZERO);
        screen.toFront();
        screen.setOpacity(OPACITY_FULL);
        currentScreen = screen;
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
        screensList.add(createHelpOverlay());
        helpOverlayFadeOut = getFadeOutTransition(TIME_HELP_SCREEN_FADEOUT, helpOverlayNode);
        helpOverlayFadeOut.setOnFinished(e -> setScreen(mainScreenNode));
        return screensList;
    }

    private Node createHelpOverlay() {
        this.helpOverlay = new HelpOverlay(LOCATION_HELP_OVERLAY_LAYOUT);
        this.helpOverlayNode = helpOverlay.getNode();
        helpOverlayNode.setOpacity(OPACITY_ZERO);
        mapScreen(SCREEN_HELP, helpOverlayNode);
        return helpOverlayNode;
    }

    private Node createMainScreen() {
        this.mainScreen = new MainScreen(LOCATION_CENTER_SCREEN_LAYOUT);
        this.mainScreenNode = mainScreen.getNode();
        mainScreenNode.setOpacity(OPACITY_ZERO);
        mapScreen(SCREEN_MAIN, mainScreenNode);
        return mainScreenNode;
    }

    // ================================================================================
    // Test methods
    // ================================================================================

    protected Node getMainScreen() {
        return mainScreenNode;
    }

    protected Node getHelpOverlay() {
        return helpOverlayNode;
    }
}
