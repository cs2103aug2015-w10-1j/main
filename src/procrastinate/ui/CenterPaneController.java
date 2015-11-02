package procrastinate.ui;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import procrastinate.task.Task;
import procrastinate.ui.UI.ScreenView;

import java.util.List;

public class CenterPaneController {

    // ================================================================================
    // Message strings
    // ================================================================================

    private static final String LOCATION_CENTER_SCREEN_LAYOUT = "views/CenterScreen.fxml";

    private static final String MESSAGE_UNABLE_RECOGNISE_SCREEN_TYPE = "Unable to recognise ScreenType";

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

 // Changed to protected for testing purposes.
    protected CenterScreen currentScreenView;
    protected ImageOverlay currentOverlay;

    private static double xOffset, yOffset;

    private FadeTransition helpOverlayFadeOut;
    private Timeline splashScreenTimeline;

    private Node mainScreenNode;
    private Node doneScreenNode;

    private Node helpOverlayNode;
    private Node splashOverlayNode;

    private ImageOverlay helpOverlay;
    private ImageOverlay splashOverlay;

    private MultiCategoryScreen mainScreen;

    private SingleCategoryScreen doneScreen;

    private StackPane centerStackPane;

    // ================================================================================
    // CenterPaneController methods
    // ================================================================================

    // New CenterPaneController should only contain one screen node at all times, excluding the overlay nodes.
    protected CenterPaneController(StackPane centerStackPane) {
        this.centerStackPane = centerStackPane;
        createScreens();
        createOverlays();
        setToMainScreen();
    }

    protected void updateScreen(List<Task> taskList, ScreenView screenView) {
        switch (screenView) {

            case SCREEN_DONE: {
                if (currentScreenView == doneScreen) {
                    doneScreen.updateTaskList(taskList);
                    break;
                } else {
                    startScreenSwitchSequence(taskList, doneScreenNode, doneScreen);
                    break;
                }
            }

            case SCREEN_MAIN: {
                if (currentScreenView == mainScreen) {
                    mainScreen.updateTaskList(taskList);
                    break;
                } else {
                    startScreenSwitchSequence(taskList, mainScreenNode, mainScreen);
                    break;
                }
            }

            case SCREEN_SEARCH: {
                break;
            }

            default:
                System.out.println(MESSAGE_UNABLE_RECOGNISE_SCREEN_TYPE);
                break;
        }
    }

    /**
     * Starts the fade out transition that lasts for 0.5 seconds if the stack contains it
     * and it is the current overlay screen.
     */
    protected void hideHelpOverlay() {
        helpOverlayFadeOut = getFadeOutTransition(TIME_HELP_SCREEN_FADEOUT, helpOverlayNode);
        helpOverlayFadeOut.setOnFinished(e -> {
            centerStackPane.getChildren().remove(helpOverlayNode);
        });

        helpOverlayFadeOut.playFromStart();
    }

    /**
     * Fast-forwards the fade animation if user starts typing, which will remove the entire
     * node from the stack once it has finished fading.
     */
    protected void hideSplashOverlay() {
        if (currentOverlay == splashOverlay && centerStackPane.getChildren().contains(splashOverlayNode)) {
            Duration interruptTime = Duration.millis(TIME_SPLASH_SCREEN_INTERRUPT);
            // Only fast forward the timeline if the current time of the animation is smaller than the given
            // interrupt time. Else, just wait for the animation to end.
            if (splashScreenTimeline.getCurrentTime().lessThan(interruptTime)) {
                splashScreenTimeline.jumpTo(Duration.millis(TIME_SPLASH_SCREEN_INTERRUPT));
            }
            splashScreenTimeline.jumpTo(Duration.millis(TIME_SPLASH_SCREEN_FADE));
        }
    }

    /**
     * Creates a splash screen that maintains full opacity for 2 seconds before completely fading out in 1 second
     * or until the user starts to type.
     * currentScreen variable is not updated to helpScreen to differentiate splashScreen from helpScreen in the
     * hideScreenOverlay method.
     */
    protected void showSplashScreen() {
        currentOverlay = splashOverlay;
        centerStackPane.getChildren().add(splashOverlayNode);

        buildSplashScreenAnimation();
        splashScreenTimeline.play();
    }

    protected void showHelpOverlay() {
        currentOverlay = helpOverlay;
        centerStackPane.getChildren().add(helpOverlayNode);
        helpOverlayNode.toFront();
    }

    // ================================================================================
    // Utility methods
    // ================================================================================

    private void startScreenSwitchSequence(List<Task> taskList, Node nodeToSwitchIn, CenterScreen screenToSwitchIn) {
        SequentialTransition screenSwitchSequence;
        screenSwitchSequence = currentScreenView.getScreenSwitchOutSequence();
        screenSwitchSequence.setOnFinished(e -> {
            centerStackPane.getChildren().clear();
            centerStackPane.getChildren().add(nodeToSwitchIn);
            screenToSwitchIn.getScreenSwitchInSequence().play();
            screenToSwitchIn.updateTaskList(taskList);
            currentScreenView = screenToSwitchIn;
        });
        screenSwitchSequence.play();
    }

    private FadeTransition getFadeOutTransition(double timeInMs, Node transitingNode) {
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(timeInMs), transitingNode);
        fadeTransition.setFromValue(OPACITY_FULL);
        fadeTransition.setToValue(OPACITY_ZERO);
        return fadeTransition;
    }

    private void buildSplashScreenAnimation() {
        // Set SplashScreen opacity at full for 2 seconds.
        Duration fullOpacityDuration = Duration.millis(TIME_SPLASH_SCREEN_FULL_OPACITY);
        KeyValue fullOpacityKeyValue = new KeyValue(splashOverlayNode.opacityProperty(), OPACITY_FULL);
        KeyFrame fullOpacityFrame = new KeyFrame(fullOpacityDuration, fullOpacityKeyValue);

        // Set SplashScreen to fade out completely at time = 3 seconds
        Duration zeroOpacityDuration = Duration.millis(TIME_SPLASH_SCREEN_FADE);
        KeyValue zeroOpacityKeyValue = new KeyValue(splashOverlayNode.opacityProperty(), OPACITY_ZERO);
        KeyFrame zeroOpacityFrame = new KeyFrame(zeroOpacityDuration, zeroOpacityKeyValue);

        splashScreenTimeline= new Timeline(fullOpacityFrame, zeroOpacityFrame);
        splashScreenTimeline.setOnFinished(e -> {
            centerStackPane.getChildren().remove(splashOverlayNode);
            currentOverlay = null;
        });
    }

    // ================================================================================
    // Init methods
    // ================================================================================

    private void createOverlays() {
        createHelpOverlay();
        createSplashOverlay();
    }

    /**
     * This creates and holds a list of the screens that can be easily added onto the center pane
     * @return list of screens
     */
    private void createScreens() {
        createMainScreen();
        createDoneScreen();
    }

    private void createHelpOverlay() {
        this.helpOverlay = new HelpOverlay();
        this.helpOverlayNode = helpOverlay.getNode();
    }

    private void createSplashOverlay() {
        this.splashOverlay = new SplashOverlay();
        this.splashOverlayNode = splashOverlay.getNode();
    }

    private void createMainScreen() {
        this.mainScreen = new MainScreen(LOCATION_CENTER_SCREEN_LAYOUT);
        this.mainScreenNode = mainScreen.getNode();
        addMouseDragListeners(mainScreenNode);
    }

    private void createDoneScreen() {
        this.doneScreen = new DoneScreen(LOCATION_CENTER_SCREEN_LAYOUT);
        this.doneScreenNode = doneScreen.getNode();
        addMouseDragListeners(doneScreenNode);
    }

    private void setToMainScreen() {
        centerStackPane.getChildren().add(mainScreenNode);
        currentScreenView = mainScreen;
        mainScreenNode.setOpacity(OPACITY_FULL);
    }

    // Required since each screen node is wrapped inside a scrollPane.
    private void addMouseDragListeners(Node screenNode) {
        Node scrollPaneNode = ((ScrollPane)screenNode.lookup("#scrollPane")).getContent();
        scrollPaneNode.setOnMousePressed((mouseEvent) -> {
            xOffset = mouseEvent.getSceneX();
            yOffset = mouseEvent.getSceneY();
        });
        scrollPaneNode.setOnMouseDragged((mouseEvent) -> {
            centerStackPane.getScene().getWindow().setX(mouseEvent.getScreenX() - xOffset);
            centerStackPane.getScene().getWindow().setY(mouseEvent.getScreenY() - yOffset);
        });
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
