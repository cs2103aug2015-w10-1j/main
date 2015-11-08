package procrastinate.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingUtilities;

import org.junit.BeforeClass;
import org.junit.Test;

import javafx.embed.swing.JFXPanel;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import procrastinate.task.Dream;
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
    // HelpScreen Testing
    // ================================================================================
    // @Test
    public void HelpScreen_InitTest() {
        UITestHelper uiTestHelper = new UITestHelper();
        assertTrue(uiTestHelper.getNewHelpScreen() != null);
    }

    // ================================================================================
    // MainScreen Testing
    // ================================================================================
    // @Test
    public void MainScreen_InitTest() {
        UITestHelper uiTestHelper = new UITestHelper();
        assertTrue(uiTestHelper.getNewMainScreen() != null);
        assertTrue(uiTestHelper.getMainScreenVBox() != null);

        assertTrue(uiTestHelper.getMainScreenVBox().getChildren().size() == 4);

    }

    // @Test
    public void MainScreen_UpdateTest() {
        UITestHelper uiTestHelper = new UITestHelper();
        uiTestHelper.getNewMainScreen();

        VBox dreamCategoryBox = uiTestHelper.getDreamsTaskList();
        VBox dreamTaskList = (VBox) dreamCategoryBox.getChildren().get(1);
        assertTrue(dreamTaskList.getChildren().size() == 0);

        uiTestHelper.addDreamToMainScreen(new Dream("A Dream"));
        assertTrue(dreamTaskList.getChildren().size() == 1);
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
