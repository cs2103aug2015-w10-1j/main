//@@author A0121597B
package procrastinate.ui;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
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
    // Message Strings
    // ================================================================================

    private static final String MESSAGE_UNABLE_RECOGNISE_SCREEN_TYPE = "Unable to recognise ScreenType";

    private static final String SELECTOR_SCROLL_BAR = ".scroll-bar";
    private static final String SELECTOR_SCROLL_PANE = "#scrollPane";

    private static final String TRANSITION_CUE_POINT_END = "end";

    // ================================================================================
    // Animation Values
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
    // Class Variables
    // ================================================================================

    private static double xOffset, yOffset;

    protected CenterScreen currentScreen;
    protected ImageOverlay currentOverlay;

    private Timeline splashScreenTimeline_;

    private Node mainScreenNode_;
    private Node doneScreenNode_;
    private Node searchScreenNode_;
    private Node summaryScreenNode_;

    private Node helpOverlayNode_;
    private Node splashOverlayNode_;

    private HelpOverlay helpOverlay_;
    private SplashOverlay splashOverlay_;

    private DoneScreen doneScreen_;
    private MainScreen mainScreen_;
    private SearchScreen searchScreen_;
    private SummaryScreen summaryScreen_;

    private StackPane centerStackPane_;

    // ================================================================================
    // CenterPaneController Constructor
    // ================================================================================

    protected CenterPaneController(StackPane centerStackPane) {
        assert(centerStackPane != null);
        this.centerStackPane_ = centerStackPane;
        createScreens();
        createOverlays();
        setToSummaryScreen();
    }

    // ================================================================================
    // CenterScreen Methods
    // ================================================================================

    /**
     * Switches the current screen in the StackPane that is set within the
     * center region of the BorderPane of the main window.
     *
     * @param taskList    that contains the tasks related to each screen
     * @param screenView  corresponding to the screen to switch to upon update
     */
    protected void updateScreen(List<Task> taskList, ScreenView screenView) {
        updateSummaryAndMainScreens(taskList, screenView);

        switch (screenView) {
            case SCREEN_DONE : {
                if (currentScreen != doneScreen_) {
                    startScreenSwitchSequence(doneScreenNode_, doneScreen_);
                }

                doneScreen_.updateTaskList(taskList);
                break;
            }

            case SCREEN_MAIN : {
                if (currentScreen != mainScreen_) {
                    startScreenSwitchSequence(mainScreenNode_, mainScreen_);
                }

                mainScreen_.updateTaskList(taskList);
                break;
            }

            case SCREEN_SEARCH : {
                if (currentScreen != searchScreen_) {
                    startScreenSwitchSequence(searchScreenNode_, searchScreen_);
                }

                searchScreen_.updateTaskList(taskList);
                break;
            }

            case SCREEN_SUMMARY : {
                if (currentScreen != summaryScreen_) {
                    // Special exception for summary screen, which requires the
                    // entire screen to be loaded before summarising can start.
                    switchToSummaryScreen();
                }

                summaryScreen_.updateTaskList(taskList);

                if (!summaryScreen_.isSummarising()) {
                    if (!centerStackPane_.getChildren().contains(splashOverlayNode_)) {
                        startScreenSwitchSequenceNoAnimation(mainScreenNode_, mainScreen_);
                    }
                }
                break;
            }

            default:
                System.out.println(MESSAGE_UNABLE_RECOGNISE_SCREEN_TYPE);
                break;
        }
    }

    /**
     * Used to keep both the Summary and Main screens up to date with one another.
     *
     * @param taskList     to update the screen with
     * @param screenView   must correspond to currentScreen and be either SCREEN_MAIN
     *                     or SCREEN_SUMMARY for any updates to take place.
     */
    private void updateSummaryAndMainScreens(List<Task> taskList, ScreenView screenView) {
        if (currentScreen == mainScreen_ && screenView == ScreenView.SCREEN_MAIN) {
            summaryScreen_.updateTaskList(taskList);
        } else if (currentScreen == summaryScreen_ && screenView == ScreenView.SCREEN_SUMMARY) {
            mainScreen_.updateTaskList(taskList);
        }
    }

    protected void initialUpdateMainScreen(List<Task> taskList) {
        mainScreen_.updateTaskList(taskList);
    }


    /**
     * A handle to pass the search string from Logic to the SearchScreen.
     *
     * @param searchString    that the user searched for, to be used as the search
     *                        query header on the SearchScreen.
     */
    protected void receiveSearchStringAndPassToSearchScreen(String searchString) {
        searchScreen_.updateSearchStringLabel(searchString);
    }

    // Methods below for scrolling current screen with key input. Scroll bar
    // value is incremented/decremented twice to enable the user scroll faster
    protected void scrollDownCurrentScreen() {
        ScrollPane currScrollPane = ((ScrollPane) (currentScreen.getNode().lookup(SELECTOR_SCROLL_PANE)));

        ScrollBar currScrollBar = (ScrollBar) currScrollPane.lookup(SELECTOR_SCROLL_BAR);
        currScrollBar.increment();
        currScrollBar.increment();
    }

    protected void scrollUpCurrentScreen() {
        ScrollPane currScrollPane = ((ScrollPane) (currentScreen.getNode().lookup(SELECTOR_SCROLL_PANE)));

        ScrollBar currScrollBar = (ScrollBar) currScrollPane.lookup(SELECTOR_SCROLL_BAR);
        currScrollBar.decrement();
        currScrollBar.decrement();
    }

    // ================================================================================
    // ImageOverlay Methods
    // ================================================================================

    /**
     * A handle to help switch between pages of the HelpOverlay if it is
     * currently being shown.
     */
    protected void showNextHelpPage() {
        if (currentOverlay != helpOverlay_) {
            return;
        }
        helpOverlay_.nextPage();
    }

    /**
     * Starts the fade out transition that lasts for TIME_HELP_SCREEN_FADEOUT
     * seconds if the stack contains it and it is the current overlay screen.
     * Each call will create a new FadeTransition to be used for fading the
     * overlay out.
     */
    protected void hideHelpOverlay() {
        if (currentOverlay != helpOverlay_ || !centerStackPane_.getChildren().contains(helpOverlayNode_)) {
            return;
        }

        FadeTransition helpOverlayFadeOut = getFadeOutTransition(TIME_HELP_SCREEN_FADEOUT, helpOverlayNode_);
        helpOverlayFadeOut.setOnFinished(e -> {
            centerStackPane_.getChildren().remove(helpOverlayNode_);
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
        if (currentOverlay == splashOverlay_ && centerStackPane_.getChildren().contains(splashOverlayNode_)) {
            Duration interruptTime = Duration.millis(TIME_SPLASH_SCREEN_INTERRUPT);
            // Only fast forward the timeline if the current time of the
            // animation is smaller than the given interrupt time. Else, just
            // wait for the animation to end.
            if (splashScreenTimeline_.getCurrentTime().lessThan(interruptTime)) {
                splashScreenTimeline_.jumpTo(Duration.millis(TIME_SPLASH_SCREEN_INTERRUPT));
            }

            splashScreenTimeline_.jumpTo(Duration.millis(TIME_SPLASH_SCREEN_FADE));
        }
    }

    /**
     * Shows the HelpOverlay only if the HelpOverlay is not present. Each call
     * creates a new FadeTransition to be used for fading the overlay in.
     */
    protected void showHelpOverlay() {
        if (currentOverlay == helpOverlay_ || centerStackPane_.getChildren().contains(helpOverlay_)) {
            return;
        }

        currentOverlay = helpOverlay_;
        centerStackPane_.getChildren().add(helpOverlayNode_);
        helpOverlayNode_.toFront();

        FadeTransition helpOverlayFadeIn = getFadeInTransition(TIME_HELP_SCREEN_FADEIN, helpOverlayNode_);
        helpOverlayFadeIn.play();
    }

    /**
     * Shows the SplashOverlay which is only used at start-up. The main
     * animation of the overlay is contained within the
     * buildSplashScreenAnimation method.
     */
    protected void showSplashOverlay() {
        currentOverlay = splashOverlay_;
        centerStackPane_.getChildren().add(splashOverlayNode_);

        buildSplashScreenAnimation();
        splashScreenTimeline_.play();
    }

    // ================================================================================
    // Transition Methods
    // ================================================================================

    /**
     * Creates a splash screen that maintains full opacity for
     * TIME_SPLASH_SCREEN_FULL_OPACITY seconds before completely fading out in
     * (TIME_SPLASH_SCREEN_FADE-TIME_SPLASH_SCREEN_FULL_OPACITY) seconds or
     * until the user starts to type.
     */
    private void buildSplashScreenAnimation() {
        Duration fullOpacityDuration = Duration.millis(TIME_SPLASH_SCREEN_FULL_OPACITY);
        KeyValue fullOpacityKeyValue = new KeyValue(splashOverlayNode_.opacityProperty(), OPACITY_FULL);
        KeyFrame fullOpacityFrame = new KeyFrame(fullOpacityDuration, fullOpacityKeyValue);

        Duration zeroOpacityDuration = Duration.millis(TIME_SPLASH_SCREEN_FADE);
        KeyValue zeroOpacityKeyValue = new KeyValue(splashOverlayNode_.opacityProperty(), OPACITY_ZERO);
        KeyFrame zeroOpacityFrame = new KeyFrame(zeroOpacityDuration, zeroOpacityKeyValue);

        splashScreenTimeline_ = new Timeline(fullOpacityFrame, zeroOpacityFrame);
        splashScreenTimeline_.setOnFinished(e -> {
                                              centerStackPane_.getChildren().remove(splashOverlayNode_);
                                              currentOverlay = null;
                                              if (!summaryScreen_.isSummarising()) {
                                                  startScreenSwitchSequenceNoAnimation(mainScreenNode_, mainScreen_);
                                              }
        });
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
     * Combines the screen changing transitions of the outgoing and incoming
     * screens by playing them in sequence.
     *
     * @param nodeToSwitchIn    must be the corresponding Node of the CenterScreen passed in.
     * @param screenToSwitchIn  the CenterScreen to be switched in and should not be contained
     *                          within the centerStackPane.
     */
    private void startScreenSwitchSequence(Node nodeToSwitchIn, CenterScreen screenToSwitchIn) {
        ParallelTransition incomingScreenTransition = screenToSwitchIn.getScreenSwitchInSequence();
        incomingScreenTransition.setOnFinished(incoming -> currentScreen = screenToSwitchIn);

        SequentialTransition outgoingScreenTransition = currentScreen.getScreenSwitchOutSequence();
        outgoingScreenTransition.setOnFinished(outgoing -> {
            centerStackPane_.getChildren().remove(currentScreen.getNode());
            centerStackPane_.getChildren().add(nodeToSwitchIn);
            incomingScreenTransition.play();
        });
        outgoingScreenTransition.play();
    }

    private void startScreenSwitchSequenceNoAnimation(Node nodeToSwitchIn, CenterScreen screenToSwitchIn) {
        ParallelTransition incomingScreenTransition = screenToSwitchIn.getScreenSwitchInSequence();
        incomingScreenTransition.setOnFinished(incoming -> currentScreen = screenToSwitchIn);

        centerStackPane_.getChildren().remove(currentScreen.getNode());
        centerStackPane_.getChildren().add(nodeToSwitchIn);

        incomingScreenTransition.jumpTo(TRANSITION_CUE_POINT_END);
        incomingScreenTransition.play();
    }

    /**
     * Exception case for switching to SummaryScreen, which wouldn't show
     * correctly if the screen switch transition of the outgoing screen is
     * played together.
     */
    private void switchToSummaryScreen() {
        centerStackPane_.getChildren().add(summaryScreenNode_);
        centerStackPane_.getChildren().remove(currentScreen.getNode());

        summaryScreen_.getScreenSwitchInSequence().play();

        currentScreen = summaryScreen_;
    }

    // ================================================================================
    // Init Methods
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
        this.helpOverlay_ = new HelpOverlay();
        this.helpOverlayNode_ = helpOverlay_.getNode();
    }

    private void createSplashOverlay() {
        this.splashOverlay_ = new SplashOverlay();
        this.splashOverlayNode_ = splashOverlay_.getNode();
    }

    private void createMainScreen() {
        this.mainScreen_ = new MainScreen();
        this.mainScreenNode_ = mainScreen_.getNode();
        addMouseDragListeners(mainScreenNode_);
    }

    private void createDoneScreen() {
        this.doneScreen_ = new DoneScreen();
        this.doneScreenNode_ = doneScreen_.getNode();
        addMouseDragListeners(doneScreenNode_);
    }

    private void createSearchScreen() {
        this.searchScreen_ = new SearchScreen();
        this.searchScreenNode_ = searchScreen_.getNode();
        addMouseDragListeners(searchScreenNode_);
    }

    private void createSummaryScreen() {
        this.summaryScreen_ = new SummaryScreen();
        this.summaryScreenNode_ = summaryScreen_.getNode();
        addMouseDragListeners(summaryScreenNode_);
    }

    private void setToSummaryScreen() {
        centerStackPane_.getChildren().add(summaryScreenNode_);
        currentScreen = summaryScreen_;
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
            centerStackPane_.getScene().getWindow().setX(mouseEvent.getScreenX() - xOffset);
            centerStackPane_.getScene().getWindow().setY(mouseEvent.getScreenY() - yOffset);
        });
    }

    // ================================================================================
    // Getter Methods
    // ================================================================================

    // @@author A0121597B generated
    protected Node getMainScreen() {
        return mainScreenNode_;
    }

    protected Node getHelpOverlay() {
        return helpOverlayNode_;
    }
}
