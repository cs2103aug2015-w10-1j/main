package procrastinate.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingUtilities;

import org.junit.BeforeClass;
import org.junit.Test;

import javafx.embed.swing.JFXPanel;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import procrastinate.task.Deadline;
import procrastinate.task.Dream;
import procrastinate.task.Event;
import procrastinate.task.Task;
import procrastinate.ui.CategoryBox;
import procrastinate.ui.CenterPaneController;
import procrastinate.ui.DoneScreen;
import procrastinate.ui.HelpOverlay;
import procrastinate.ui.MainScreen;
import procrastinate.ui.SearchScreen;
import procrastinate.ui.SplashOverlay;
import procrastinate.ui.SubcategoryBox;
import procrastinate.ui.SummaryScreen;
import procrastinate.ui.TaskEntry;
import procrastinate.ui.UI.ScreenView;
import procrastinate.ui.UITestHelper;

// DialogPopupHandler/SummaryScreen/SystemTrayHandler/UI/WindowHandler classes are not included in this test.
// They require the actual GUI window to be shown and the test to be running on the JavaFX Application Thread.
public class UITest {

    public static UITestHelper uiTestHelper = new UITestHelper();
    public static CenterPaneController centerPaneController;

    @BeforeClass
    // @@author A0121597B-reused
    // Needed to be initialised before testing JavaFX elements
    // http://stackoverflow.com/questions/28501307/javafx-toolkit-not-initialized-in-one-test-class-but-not-two-others-where-is
    public static void initToolkit() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        SwingUtilities.invokeLater(() -> {
            new JFXPanel();
            latch.countDown();
        });
        if (!latch.await(5L, TimeUnit.SECONDS)) {
            throw new ExceptionInInitializerError();
        }

        System.out.println("Setting up UITest...");

        System.out.println("Setting up CenterPaneController...");
        centerPaneController = uiTestHelper.getNewCenterPaneController(new StackPane());
        assertNotNull(centerPaneController);
        System.out.println("CenterPaneController initialised.");
    }

    // @@author A0121597B
    // ================================================================================
    // CenterPaneController Testing
    // ================================================================================

    @Test
    public void centerPaneController_InitCurrentOverlayShouldBeNull() {
        assertNull(uiTestHelper.getCPCCurrentOverlay());
    }

    @Test
    public void centerPaneController_InitCurrentScreenShouldNotBeNullAndIsSummaryScreen() {
        // The inital start screen should be a summary screen
        assertTrue(uiTestHelper.getCPCCurrentScreen() != null);
        assertTrue(uiTestHelper.getCPCCurrentScreen() instanceof SummaryScreen);
    }

    @Test
    public void centerPaneController_ShouldHaveCreatedAllScreens() {
        assertNotNull(uiTestHelper.getDoneScreenNode());
        assertNotNull(uiTestHelper.getMainScreenNode());
        assertNotNull(uiTestHelper.getSearchScreenNode());
        assertNotNull(uiTestHelper.getSummaryScreenNode());
        assertNotNull(uiTestHelper.getHelpOverlayNode());
        assertNotNull(uiTestHelper.getSplashOverlayNode());
    }

    @Test
    public void centerPaneController_ShouldBeAbleToSwitchAllScreens() throws InterruptedException {
        List<Task> emptyList = new ArrayList<Task>();
        // Due to random nature of tests running, this test needs to be isolated so that the initial
        // start point is always the same and would not have unwanted assertionErrors
        UITestHelper isolatedTestHelper = new UITestHelper();
        isolatedTestHelper.getNewCenterPaneController(new StackPane());

        assertTrue(isolatedTestHelper.getCPCCurrentScreen() instanceof SummaryScreen);
        assertEquals(isolatedTestHelper.getCPCCurrentScreenNode(), isolatedTestHelper.getSummaryScreenNode());

        // Each screen switch requires waiting of the screen switch animation to end before asserts can be carried out
        isolatedTestHelper.changeCPCScreen(emptyList, ScreenView.SCREEN_MAIN);
        Thread.sleep(1000);
        assertTrue(isolatedTestHelper.getCPCCurrentScreen() instanceof MainScreen);
        assertEquals(isolatedTestHelper.getCPCCurrentScreenNode(), isolatedTestHelper.getMainScreenNode());

        isolatedTestHelper.changeCPCScreen(emptyList, ScreenView.SCREEN_SEARCH);
        Thread.sleep(1000);
        assertTrue(isolatedTestHelper.getCPCCurrentScreen() instanceof SearchScreen);
        assertEquals(isolatedTestHelper.getCPCCurrentScreenNode(), isolatedTestHelper.getSearchScreenNode());

        isolatedTestHelper.changeCPCScreen(emptyList, ScreenView.SCREEN_DONE);
        Thread.sleep(1000);
        assertTrue(isolatedTestHelper.getCPCCurrentScreen() instanceof DoneScreen);
        assertEquals(isolatedTestHelper.getCPCCurrentScreenNode(), isolatedTestHelper.getDoneScreenNode());
    }

    @Test
    public void centerPaneController_ShouldBeAbleToShowAllOverlays() throws InterruptedException {
        // Splash overlay testing
        uiTestHelper.showSplash();
        assertEquals(uiTestHelper.getCPCCurrentOverlayNode(), uiTestHelper.getSplashOverlayNode());

        uiTestHelper.hideSplash();
        Thread.sleep(3000); // to wait for splash animation to end
        assertNull(uiTestHelper.getCPCCurrentOverlay());

        // Help overlay testing
        uiTestHelper.showHelp();
        assertEquals(uiTestHelper.getCPCCurrentOverlayNode(), uiTestHelper.getHelpOverlayNode());

        uiTestHelper.hideHelp();
        Thread.sleep(300);
        assertNull(uiTestHelper.getCPCCurrentOverlay());
    }

    // ================================================================================
    // CenterScreen Testing
    // ================================================================================
    // DoneScreen related
    @Test
    public void doneScreen_InitChildrenShouldBeEmpty() {
        DoneScreen doneScreen = (DoneScreen) uiTestHelper.getNewDoneScreen();
        assertNotNull(doneScreen);
        assertEquals(0, uiTestHelper.getSingleCategoryTaskList(doneScreen).getChildren().size());
        assertEquals(0, uiTestHelper.getCenterScreenVBox(doneScreen).getChildren().size());
    }

    @Test
    public void doneScreen_UpdateShouldIgnoreUndoneTasks() throws ParseException {
        DoneScreen doneScreen = (DoneScreen) uiTestHelper.getNewDoneScreen();
        List<Task> taskList = new ArrayList<Task>();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");

        Deadline undoneDeadline = new Deadline("An Undone Deadline", sdf.parse("11/11/16"));
        taskList.add(undoneDeadline);
        uiTestHelper.updateScreenTaskList(doneScreen, taskList);
        assertEquals(0, uiTestHelper.getSingleCategoryTaskList(doneScreen).getChildren().size());

        Deadline doneDeadline = new Deadline("A Done Deadline", sdf.parse("12/12/16"));
        doneDeadline.setDone(true);
        taskList.add(doneDeadline);
        uiTestHelper.updateScreenTaskList(doneScreen, taskList);
        assertEquals(1, uiTestHelper.getSingleCategoryTaskList(doneScreen).getChildren().size());


        Event undoneEvent = new Event("An Undone Event", sdf.parse("11/14/17"), sdf.parse("11/15/17"));
        Event doneEvent = new Event("A Done Event", sdf.parse("11/14/17"), sdf.parse("11/15/17"));
        doneEvent.setDone(true);
        taskList.add(undoneEvent);
        taskList.add(doneEvent);
        uiTestHelper.updateScreenTaskList(doneScreen, taskList);
        assertEquals(2, uiTestHelper.getSingleCategoryTaskList(doneScreen).getChildren().size());

        Dream undoneDream = new Dream("A Undone Dream");
        Dream doneDream = new Dream("A Done Dream");
        doneDream.setDone(true);
        taskList.add(undoneDream);
        taskList.add(doneDream);
        uiTestHelper.updateScreenTaskList(doneScreen, taskList);
        assertEquals(3, uiTestHelper.getSingleCategoryTaskList(doneScreen).getChildren().size());
    }

    // MainScreen related
    @Test
    public void mainScreen_InitChildrenShouldBeEmpty() {
        MainScreen mainScreen = (MainScreen) uiTestHelper.getNewMainScreen();
        assertNotNull(mainScreen);
        assertEquals(0, uiTestHelper.getCenterScreenVBox(mainScreen).getChildren().size());

        assertEquals(0, uiTestHelper.getOverdueTaskList(mainScreen).getChildren().size());
        assertEquals(0, uiTestHelper.getUpcomingTaskList(mainScreen).getChildren().size());
        assertEquals(0, uiTestHelper.getFutureTaskList(mainScreen).getChildren().size());
        assertEquals(0, uiTestHelper.getDreamsTaskList(mainScreen).getChildren().size());
        assertEquals(0, uiTestHelper.getDoneTaskList(mainScreen).getChildren().size());
    }

    @Test
    public void mainScreen_ShouldAddAllTasksCorrectly() throws ParseException {
        MainScreen mainScreen = (MainScreen) uiTestHelper.getNewMainScreen();
        List<Task> taskList = new ArrayList<Task>();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");

        taskList.add(new Deadline("overdue", sdf.parse("11/11/11")));
        taskList.add(new Deadline("today", Date.from(Instant.now().plusSeconds(60))));  // else it will go into overdue
        taskList.add(new Event("tomorrow", Date.from(Instant.now().plusSeconds(86400)), sdf.parse("11/11/16")));
        taskList.add(new Event("future", sdf.parse("12/12/16"), sdf.parse("11/11/17")));
        taskList.add(new Dream("dream"));
        Dream doneDream = new Dream("another done dream");
        doneDream.setDone(true);
        taskList.add(doneDream);

        uiTestHelper.updateScreenTaskList(mainScreen, taskList);
        assertEquals(5, uiTestHelper.getCenterScreenVBox(mainScreen).getChildren().size());

        assertEquals(1, uiTestHelper.getOverdueTaskList(mainScreen).getChildren().size());
        assertEquals(2, uiTestHelper.getUpcomingTaskList(mainScreen).getChildren().size());
        assertEquals(1, uiTestHelper.getFutureTaskList(mainScreen).getChildren().size());
        assertEquals(1, uiTestHelper.getDreamsTaskList(mainScreen).getChildren().size());
        assertEquals(1, uiTestHelper.getDoneTaskList(mainScreen).getChildren().size());
    }

    // SearchScreen related
    @Test
    public void searchScreen_InitChildrenShouldBeEmpty() {
        SearchScreen searchScreen = (SearchScreen) uiTestHelper.getNewSearchScreen();
        assertNotNull(searchScreen);
        assertEquals(0, uiTestHelper.getCenterScreenVBox(searchScreen).getChildren().size());

        assertEquals(0, uiTestHelper.getOverdueTaskList(searchScreen).getChildren().size());
        assertEquals(0, uiTestHelper.getUpcomingTaskList(searchScreen).getChildren().size());
        assertEquals(0, uiTestHelper.getFutureTaskList(searchScreen).getChildren().size());
        assertEquals(0, uiTestHelper.getDreamsTaskList(searchScreen).getChildren().size());
        assertEquals(0, uiTestHelper.getDoneTaskList(searchScreen).getChildren().size());
    }

    @Test
    public void searchScreen_ShouldShowSearchedTasksCorrectly() throws ParseException, InterruptedException {
        SearchScreen searchScreen = (SearchScreen) uiTestHelper.getNewSearchScreen();
        List<Task> taskList = new ArrayList<Task>();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");

        // Basically should show the tasks under the correct header
        taskList.add(new Deadline("overdue", sdf.parse("11/11/11")));
        uiTestHelper.updateScreenTaskList(searchScreen, taskList);
        assertEquals(1, uiTestHelper.getCenterScreenVBox(searchScreen).getChildren().size());
        assertEquals(1, uiTestHelper.getOverdueTaskList(searchScreen).getChildren().size());
        assertEquals(0, uiTestHelper.getUpcomingTaskList(searchScreen).getChildren().size());
        assertEquals(0, uiTestHelper.getFutureTaskList(searchScreen).getChildren().size());
        assertEquals(0, uiTestHelper.getDreamsTaskList(searchScreen).getChildren().size());
        assertEquals(0, uiTestHelper.getDoneTaskList(searchScreen).getChildren().size());

        taskList.clear();
        taskList.add(new Event("future", sdf.parse("12/12/16"), sdf.parse("11/11/17")));
        taskList.add(new Event("another future", sdf.parse("12/12/16"), sdf.parse("11/11/17")));
        uiTestHelper.updateScreenTaskList(searchScreen, taskList);
        Thread.sleep(300);  // to wait for prev category to fade out
        assertEquals(1, uiTestHelper.getCenterScreenVBox(searchScreen).getChildren().size());
        assertEquals(0, uiTestHelper.getOverdueTaskList(searchScreen).getChildren().size());
        assertEquals(0, uiTestHelper.getUpcomingTaskList(searchScreen).getChildren().size());
        assertEquals(2, uiTestHelper.getFutureTaskList(searchScreen).getChildren().size());
        assertEquals(0, uiTestHelper.getDreamsTaskList(searchScreen).getChildren().size());
        assertEquals(0, uiTestHelper.getDoneTaskList(searchScreen).getChildren().size());

        taskList.add(new Deadline("overdue", sdf.parse("11/11/11")));
        uiTestHelper.updateScreenTaskList(searchScreen, taskList);
        assertEquals(2, uiTestHelper.getCenterScreenVBox(searchScreen).getChildren().size());
        assertEquals(1, uiTestHelper.getOverdueTaskList(searchScreen).getChildren().size());
        assertEquals(0, uiTestHelper.getUpcomingTaskList(searchScreen).getChildren().size());
        assertEquals(2, uiTestHelper.getFutureTaskList(searchScreen).getChildren().size());
        assertEquals(0, uiTestHelper.getDreamsTaskList(searchScreen).getChildren().size());
        assertEquals(0, uiTestHelper.getDoneTaskList(searchScreen).getChildren().size());
    }

    // Unable to test SummaryScreen due to actual actual sizing of the window shown

    // ================================================================================
    // ImageOverlay Testing
    // ================================================================================
    @Test
    public void helpOverlay_InitShouldNotBeNullAndContainRequiredPages() {
        HelpOverlay helpOverlay = (HelpOverlay) uiTestHelper.getNewHelpOverlay();
        assertNotNull(helpOverlay);
        // Should only contain the image view
        assertEquals(1, uiTestHelper.getOverlayContainer(helpOverlay).getChildren().size());

        Image firstPage = uiTestHelper.getOverlayImageView(helpOverlay).getImage();
        uiTestHelper.switchHelpOverlayPage(helpOverlay);
        Image secondPage = uiTestHelper.getOverlayImageView(helpOverlay).getImage();
        assertNotEquals(firstPage, secondPage);
    }

    @Test
    public void splashOverlay_InitShouldNotBeNullAndLabelsSetUp() {
        SplashOverlay splashOverlay = (SplashOverlay) uiTestHelper.getNewSplashOverlay();
        assertNotNull(splashOverlay);
        // Should contain 2 extra labels
        assertEquals(3, uiTestHelper.getOverlayContainer(splashOverlay).getChildren().size());
    }

    // ================================================================================
    // CategoryBox Testing
    // ================================================================================
    @Test
    public void categoryBox_InitShouldNotBeNullAndCorrectLabelIsSet() {
        CategoryBox categoryBox = uiTestHelper.getNewCategoryBox("Test header");
        assertNotNull(categoryBox);

        // Check if the correct header is set
        assertEquals("Test header", uiTestHelper.getCategoryBoxLabel(categoryBox).textProperty().get());

        // Check that the task list is empty
        assertEquals(0, uiTestHelper.getCategoryBoxVBox(categoryBox).getChildren().size());
    }

    // ================================================================================
    // CategoryBox Testing
    // ================================================================================
    @Test
    public void subcategoryBox_InitShouldNotBeNullAndCorrectLabelIsSet() {
        SubcategoryBox subcategoryBox = uiTestHelper.getNewSubcategoryBox("Test header");
        assertNotNull(subcategoryBox);

        // Check if the correct header is set
        assertEquals("Test header", uiTestHelper.getSubcategoryBoxLabel(subcategoryBox).textProperty().get());

        // Check that the task list is empty
        assertEquals(0, uiTestHelper.getSubcategoryBoxVBox(subcategoryBox).getChildren().size());
    }

    // ================================================================================
    // TaskEntry Testing
    // ================================================================================
    @Test
    public void taskEntryDream_LabelsShouldBeSetCorrectly() {
        TaskEntry dreamTask = uiTestHelper.getNewDreamTaskEntry("1", "test");
        assertEquals("1", uiTestHelper.getTaskEntryLineNum(dreamTask).getText());
        assertEquals("test", uiTestHelper.getTaskEntryDescription(dreamTask).getText());
        assertEquals("", uiTestHelper.getTaskEntryTime(dreamTask).getText());
    }

    @Test
    public void taskEntryOthers_LabelsShouldBeSetCorrectly() {
        TaskEntry otherTask =  uiTestHelper.getNewOthersTaskEntry("2", "test2", "time");
        assertEquals("2", uiTestHelper.getTaskEntryLineNum(otherTask).getText());
        assertEquals("test2", uiTestHelper.getTaskEntryDescription(otherTask).getText());
        assertEquals("time", uiTestHelper.getTaskEntryTime(otherTask).getText());
    }

    @Test
    public void taskEntry_DoneTasksShouldHaveTickSet() {
        TaskEntry doneTask =  uiTestHelper.getNewDoneTaskEntry("3", "a done task", "time here");
        assertEquals("3", uiTestHelper.getTaskEntryLineNum(doneTask).getText());
        assertEquals("a done task", uiTestHelper.getTaskEntryDescription(doneTask).getText());
        assertEquals("time here", uiTestHelper.getTaskEntryTime(doneTask).getText());

        GridPane gridPane = (GridPane) uiTestHelper.getTaskEntryNode(doneTask);
        // Since the tick is wrapped with line number and added back, it will be at the end of the children list
        assertTrue(gridPane.getChildren().get(gridPane.getChildren().size()-1) instanceof HBox);

        // The combined wrapper should contain an individual HBox for the tick and line number label
        assertEquals(2, ((HBox)gridPane.getChildren().get(gridPane.getChildren().size()-1)).getChildren().size());
        assertTrue(((HBox)gridPane.getChildren().get(gridPane.getChildren().size()-1)).getChildren().get(0) instanceof HBox);
        assertTrue(((HBox)gridPane.getChildren().get(gridPane.getChildren().size()-1)).getChildren().get(1) instanceof HBox);

        // This should be the wrapper for the tick image
        HBox tickImageWrapper = (HBox)((HBox)gridPane.getChildren().get(gridPane.getChildren().size()-1)).getChildren().get(0);
        assertEquals(1, tickImageWrapper.getChildren().size());
        assertTrue(tickImageWrapper.getChildren().get(0) instanceof ImageView);
        ImageView tickImage = (ImageView) tickImageWrapper.getChildren().get(0);
        assertNotNull(tickImage.getImage());

        // This should be the wrapper for the line number
        HBox lineNumberWrapper = (HBox)((HBox)gridPane.getChildren().get(gridPane.getChildren().size()-1)).getChildren().get(1);
        assertEquals(1, lineNumberWrapper.getChildren().size());
        assertTrue(lineNumberWrapper.getChildren().get(0) instanceof Label);
        Label lineNumberLabel = (Label) lineNumberWrapper.getChildren().get(0);
        assertEquals("3", lineNumberLabel.getText());
    }
}