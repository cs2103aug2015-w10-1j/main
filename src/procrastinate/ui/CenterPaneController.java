//@@author A0121597B
package procrastinate.ui;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.control.ScrollBar;
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

    private static final String SELECTOR_SCROLL_BAR = ".scroll-bar";
    private static final String SELECTOR_SCROLL_PANE = "#scrollPane";

    // ================================================================================
    // Animation time values
    // ================================================================================

    private static final double OPACITY_FULL = 1;
    private static final double OPACITY_ZERO = 0;

    // Time values are in milliseconds
    private static final double TIME_HELP_SCREEN_FADEIN = 150;
    private static final double TIME_HELP_SCREEN_FADEOUT = 200;

    private static final double TIME_SPLASH_SCREEN_FADE = 4000;
    private static final double TIME_SPLASH_SCREEN_FULL_OPACITY = 3000;
    private static final double TIME_SPLASH_SCREEN_INTERRUPT = 2700;

    // ================================================================================
    // Class variables
    // ================================================================================

    private static double xOffset, yOffset;

    // Changed to protected for testing purposes.
    protected CenterScreen currentScreen;
    protected ImageOverlay currentOverlay;

    private Timeline splashScreenTimeline;

    private Node mainScreenNode;
    private Node doneScreenNode;
    private Node searchScreenNode;
    private Node summaryScreenNode;

    private Node helpOverlayNode;
    private Node splashOverlayNode;

    private ImageOverlay helpOverlay;
    private ImageOverlay splashOverlay;

    private DoneScreen doneScreen;
    private MainScreen mainScreen;
    private SearchScreen searchScreen;
    private SummaryScreen summaryScreen;

    private StackPane centerStackPane;

    // ================================================================================
    // CenterPaneController methods
    // ================================================================================

    protected CenterPaneController(StackPane centerStackPane) {
        this.centerStackPane = centerStackPane;
        createScreens();
        createOverlays();
        setToSummaryScreen();
    }

    /**
     * Switches the current screen in the StackPane that is set within the
     * center region of the BorderPane of the main window.
     *
     * @param taskList
     *            that contains the tasks related to each screen
     * @param screenView
     */
    protected void updateScreen(List<Task> taskList, ScreenView screenView) {
        switch (screenView) {
            case SCREEN_DONE: {
                if (currentScreen != doneScreen) {
                    startScreenSwitchSequence(doneScreenNode, doneScreen);
                }
                doneScreen.updateTaskList(taskList);
                break;
            }

            case SCREEN_MAIN: {
                if (currentScreen != mainScreen) {
                    startScreenSwitchSequence(mainScreenNode, mainScreen);
                }
                mainScreen.updateTaskList(taskList);
                break;
            }

            case SCREEN_SEARCH: {
                if (currentScreen != searchScreen) {
                    startScreenSwitchSequence(searchScreenNode, searchScreen);
                }
                searchScreen.updateTaskList(taskList);
                break;
            }

            case SCREEN_SUMMARY: {
                if (currentScreen != summaryScreen) {
                    // Special exception for summary screen, which requires the
                    // entire screen to be loaded before summarising can start.
                    switchToSummaryScreen();
                }
                summaryScreen.updateTaskList(taskList);
                break;
            }

            default:
                System.out.println(MESSAGE_UNABLE_RECOGNISE_SCREEN_TYPE);
                break;
        }
    }

    protected void initialUpdateMainScreen(List<Task> taskList) {
        mainScreen.updateTaskList(taskList);
    }

    /**
     * Starts the fade out transition that lasts for TIME_HELP_SCREEN_FADEOUT
     * seconds if the stack contains it and it is the current overlay screen.
     * Each call will create a new FadeTransition to be used for fading the
     * overlay out.
     */
    protected void hideHelpOverlay() {
        if (currentOverlay != helpOverlay || !centerStackPane.getChildren().contains(helpOverlayNode)) {
            return;
        }
        FadeTransition helpOverlayFadeOut = getFadeOutTransition(TIME_HELP_SCREEN_FADEOUT, helpOverlayNode);
        helpOverlayFadeOut.setOnFinished(e -> {
            centerStackPane.getChildren().remove(helpOverlayNode);
            currentOverlay = null;
        });
        helpOverlayFadeOut.play();
    }

    /**
     * Fast-forwards the fade animation if user starts typing. The splash screen
     * is automatically removed from the centerStackPane once it has finished
     * playing.
     */
    protected void hideSplashOverlay() {
        if (currentOverlay == splashOverlay && centerStackPane.getChildren().contains(splashOverlayNode)) {
            Duration interruptTime = Duration.millis(TIME_SPLASH_SCREEN_INTERRUPT);
            // Only fast forward the timeline if the current time of the
            // animation is smaller than the given interrupt time. Else, just
            // wait for the animation to end.
            if (splashScreenTimeline.getCurrentTime().lessThan(interruptTime)) {
                splashScreenTimeline.jumpTo(Duration.millis(TIME_SPLASH_SCREEN_INTERRUPT));
            }
            splashScreenTimeline.jumpTo(Duration.millis(TIME_SPLASH_SCREEN_FADE));
        }
    }

    /**
     * Shows the SplashOverlay which is only used at start-up. The main
     * animation of the overlay is contained within the
     * buildSplashScreenAnimation method.
     */
    protected void showSplashOverlay() {
        currentOverlay = splashOverlay;
        centerStackPane.getChildren().add(splashOverlayNode);

        buildSplashScreenAnimation();
        splashScreenTimeline.play();
    }

    /**
     * Shows the HelpOverlay only if the HelpOverlay is not present. Each call
     * creates a new FadeTransition to be used for fading the overlay in.
     */
    protected void showHelpOverlay() {
        if (currentOverlay == helpOverlay || centerStackPane.getChildren().contains(helpOverlay)) {
            return;
        }
        currentOverlay = helpOverlay;
        centerStackPane.getChildren().add(helpOverlayNode);
        helpOverlayNode.toFront();

        FadeTransition helpOverlayFadeIn = getFadeInTransition(TIME_HELP_SCREEN_FADEIN, helpOverlayNode);
        helpOverlayFadeIn.play();
    }

    /**
     * A handle to help switch between pages of the HelpOverlay if it is
     * currently being shown.
     */
    protected void nextHelpPage() {
        if (currentOverlay != helpOverlay) {
            return;
        }
        ((HelpOverlay) helpOverlay).nextPage();
    }

    // Methods below for scrolling current screen with key input. Scroll bar
    // value is incremented/decremented twice to enable the user scroll faster
    protected void scrollUpCurrentScreen() {
        ScrollPane currScrollPane = ((ScrollPane) (currentScreen.getNode().lookup(SELECTOR_SCROLL_PANE)));
        ScrollBar currScrollBar = (ScrollBar) currScrollPane.lookup(SELECTOR_SCROLL_BAR);
        currScrollBar.decrement();
        currScrollBar.decrement();
    }

    protected void scrollDownCurrentScreen() {
        ScrollPane currScrollPane = ((ScrollPane) (currentScreen.getNode().lookup(SELECTOR_SCROLL_PANE)));
        ScrollBar currScrollBar = (ScrollBar) currScrollPane.lookup(SELECTOR_SCROLL_BAR);
        currScrollBar.increment();
        currScrollBar.increment();
    }

    // ================================================================================
    // Utility methods
    // ================================================================================

    /**
     * A handle to pass the search string from Logic to the SearchScreen.
     *
     * @param searchString
     *            that the user searched for, to be used as the search query
     *            header on the SearchScreen.
     */
    protected void receiveSearchStringAndPassToSearchScreen(String searchString) {
        searchScreen.updateSearchStringLabel(searchString);
    }

    /**
     * Combines the screen changing transitions of the outgoing and incoming
     * screens by playing them in sequence.
     *
     * @param nodeToSwitchIn
     *            must be the corresponding Node of the CenterScreen passed in.
     * @param screenToSwitchIn
     *            the CenterScreen to be switched in and should not be contained
     *            within the centerStackPane.
     */
    private void startScreenSwitchSequence(Node nodeToSwitchIn, CenterScreen screenToSwitchIn) {
        SequentialTransition incomingScreenTransition = screenToSwitchIn.getScreenSwitchInSequence();
        incomingScreenTransition.setOnFinished(incoming -> currentScreen = screenToSwitchIn);

        SequentialTransition outgoingScreenTransition = currentScreen.getScreenSwitchOutSequence();
        outgoingScreenTransition.setOnFinished(outgoing -> {
            centerStackPane.getChildren().remove(currentScreen.getNode());
            centerStackPane.getChildren().add(nodeToSwitchIn);
            incomingScreenTransition.play();
        });
        outgoingScreenTransition.play();
    }

    /**
     * Exception case for switching to SummaryScreen, which wouldn't show
     * correctly if the screen switch transition of the outgoing screen is
     * played together.
     */
    private void switchToSummaryScreen() {
        centerStackPane.getChildren().add(summaryScreenNode);
        centerStackPane.getChildren().remove(currentScreen.getNode());
        summaryScreen.getScreenSwitchInSequence().play();
        currentScreen = summaryScreen;
    }

    private FadeTransition getFadeOutTransition(double timeInMs, Node transitingNode) {
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(timeInMs), transitingNode);
        fadeTransition.setFromValue(OPACITY_FULL);
        fadeTransition.setToValue(OPACITY_ZERO);
        fadeTransition.setInterpolator(Interpolator.EASE_OUT);
        return fadeTransition;
    }

    private FadeTransition getFadeInTransition(double timeInMs, Node transitingNode) {
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(timeInMs), transitingNode);
        fadeTransition.setFromValue(OPACITY_ZERO);
        fadeTransition.setToValue(OPACITY_FULL);
        fadeTransition.setInterpolator(Interpolator.EASE_IN);
        return fadeTransition;
    }

    /**
     * Creates a splash screen that maintains full opacity for
     * TIME_SPLASH_SCREEN_FULL_OPACITY seconds before completely fading out in
     * (TIME_SPLASH_SCREEN_FADE-TIME_SPLASH_SCREEN_FULL_OPACITY) seconds or
     * until the user starts to type.
     */
    private void buildSplashScreenAnimation() {
        Duration fullOpacityDuration = Duration.millis(TIME_SPLASH_SCREEN_FULL_OPACITY);
        KeyValue fullOpacityKeyValue = new KeyValue(splashOverlayNode.opacityProperty(), OPACITY_FULL);
        KeyFrame fullOpacityFrame = new KeyFrame(fullOpacityDuration, fullOpacityKeyValue);

        Duration zeroOpacityDuration = Duration.millis(TIME_SPLASH_SCREEN_FADE);
        KeyValue zeroOpacityKeyValue = new KeyValue(splashOverlayNode.opacityProperty(), OPACITY_ZERO);
        KeyFrame zeroOpacityFrame = new KeyFrame(zeroOpacityDuration, zeroOpacityKeyValue);

        splashScreenTimeline = new Timeline(fullOpacityFrame, zeroOpacityFrame);
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

    private void createScreens() {
        createMainScreen();
        createDoneScreen();
        createSearchScreen();
        createSummaryScreen();
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

    private void createSearchScreen() {
        this.searchScreen = new SearchScreen(LOCATION_CENTER_SCREEN_LAYOUT);
        this.searchScreenNode = searchScreen.getNode();
        addMouseDragListeners(searchScreenNode);
    }

    private void createSummaryScreen() {
        this.summaryScreen = new SummaryScreen(LOCATION_CENTER_SCREEN_LAYOUT);
        this.summaryScreenNode = summaryScreen.getNode();
        addMouseDragListeners(summaryScreenNode);
    }

    private void setToSummaryScreen() {
        centerStackPane.getChildren().add(summaryScreenNode);
        currentScreen = summaryScreen;
    }

    // @@author A0121597B-reused
    // Required since each screen node is wrapped inside a scrollPane.
    private void addMouseDragListeners(Node screenNode) {
        Node scrollPaneNode = ((ScrollPane) screenNode.lookup(SELECTOR_SCROLL_PANE)).getContent();
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

    // @@author A0121597B generated
    protected Node getMainScreen() {
        return mainScreenNode;
    }

    protected Node getHelpOverlay() {
        return helpOverlayNode;
    }
}
