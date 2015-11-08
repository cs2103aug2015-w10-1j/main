package procrastinate.test;

import static org.junit.Assert.*;

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
import javafx.scene.layout.StackPane;
import procrastinate.task.Deadline;
import procrastinate.task.Dream;
import procrastinate.task.Event;
import procrastinate.task.Task;
import procrastinate.ui.CenterPaneController;
import procrastinate.ui.DoneScreen;
import procrastinate.ui.MainScreen;
import procrastinate.ui.SearchScreen;
import procrastinate.ui.SummaryScreen;
import procrastinate.ui.UI.ScreenView;
import procrastinate.ui.UITestHelper;

public class UITest {

    public static UITestHelper uiTestHelper = new UITestHelper();
    public static CenterPaneController centerPaneController;

    @BeforeClass
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
        taskList.add(new Deadline("today", Date.from(Instant.now().plusSeconds(60))));
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

    // Unable to test summary screen

    // ================================================================================
    // HelpScreen Testing
    // ================================================================================
    // @Test
    public void HelpScreen_InitTest() {
        UITestHelper uiTestHelper = new UITestHelper();
        assertTrue(uiTestHelper.getNewHelpScreen() != null);
    }

    // ================================================================================
    // CategoryBox Testing
    // ================================================================================
    @Test
    public void CategoryBox_InitTest() {
        UITestHelper uiTestHelper = new UITestHelper();
        assertTrue(uiTestHelper.getNewCategoryBox() != null);
        assertTrue(uiTestHelper.getNewCategoryBoxVBox() != null);
        assertTrue(uiTestHelper.getNewCategoryBoxLabel("THIS LABEL").textProperty().get().equals("THIS LABEL"));
    }

    // ================================================================================
    // TaskEntry Testing
    // ================================================================================
    @Test
    public void TaskEntryDream_InitTest() {
        UITestHelper uiTestHelper = new UITestHelper();
        uiTestHelper.getNewDreamTaskEntry("1", "test");
        assertTrue(uiTestHelper.getDreamTaskEntryLineNum().getText().equals("1"));
        assertTrue(uiTestHelper.getDreamTaskEntryDescription().getText().equals("test"));
    }

    @Test
    public void TaskEntryOthers_InitTest() {
        UITestHelper uiTestHelper = new UITestHelper();
        uiTestHelper.getNewOthersTaskEntry("2", "test2", "time");
        assertTrue(uiTestHelper.getOthersTaskEntryLineNum().getText().equals("2"));
        assertTrue(uiTestHelper.getOthersTaskEntryDescription().getText().equals("test2"));
        assertTrue(uiTestHelper.getOthersTaskEntryTime().getText().equals("time"));
    }
}
