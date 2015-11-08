package procrastinate.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

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

//    @Test
    public void centerPaneController_ShouldBeAbleToSwitchAllScreens() {
        List<Task> emptyList = new ArrayList<Task>();
        UITestHelper isolatedTestHelper = new UITestHelper();
        isolatedTestHelper.getNewCenterPaneController(new StackPane());

        assertTrue(uiTestHelper.getCPCCurrentScreen() instanceof SummaryScreen);

        isolatedTestHelper.changeCPCScreen(emptyList, ScreenView.SCREEN_MAIN);

        assertTrue(uiTestHelper.getCPCCurrentScreen() instanceof MainScreen);

        isolatedTestHelper.changeCPCScreen(emptyList, ScreenView.SCREEN_SEARCH);
        assertTrue(uiTestHelper.getCPCCurrentScreen() instanceof SearchScreen);

        isolatedTestHelper.changeCPCScreen(emptyList, ScreenView.SCREEN_DONE);
        assertTrue(uiTestHelper.getCPCCurrentScreen() instanceof DoneScreen);
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
