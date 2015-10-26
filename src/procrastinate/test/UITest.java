package procrastinate.test;

import static org.junit.Assert.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingUtilities;

import org.junit.BeforeClass;

import javafx.embed.swing.JFXPanel;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import org.junit.Test;

import procrastinate.task.Dream;
import procrastinate.ui.UITestHelper;

public class UITest {

    @BeforeClass
    // Needed to be initialised before testing JavaFX elements
    // http://stackoverflow.com/questions/28501307/javafx-toolkit-not-initialized-in-one-test-class-but-not-two-others-where-is
    public static void initToolkit()
        throws InterruptedException
    {
        final CountDownLatch latch = new CountDownLatch(1);
        SwingUtilities.invokeLater(() -> {
            new JFXPanel();
            latch.countDown();
        });
        if (!latch.await(5L, TimeUnit.SECONDS)) {
            throw new ExceptionInInitializerError();
        }
    }

    // ================================================================================
    // CenterPaneController Testing
    // ================================================================================
//    @Test
    public void centerPaneController_InitTest() {
        // Test for initialise and non-null initial screen.
        StackPane sp = new StackPane();
        UITestHelper uiTestHelper = new UITestHelper();
        uiTestHelper.getNewCenterPaneController(sp);
        assertTrue(uiTestHelper.getCPCCurrentScreen() != null);
    }

    // Test screen switching
//    @Test
    public void centerPaneController_testMainScreen() {
        StackPane sp = new StackPane();
        UITestHelper uiTestHelper = new UITestHelper();
        uiTestHelper.getNewCenterPaneController(sp);
        uiTestHelper.switchToMain();
        assertTrue(uiTestHelper.getMainScreen().equals(uiTestHelper.getCPCCurrentScreen()));
    }

//    @Test
    public void centerPaneController_testHelpScreen() {
        StackPane sp = new StackPane();
        UITestHelper uiTestHelper = new UITestHelper();
        uiTestHelper.getNewCenterPaneController(sp);
        uiTestHelper.switchToHelp();
        assertTrue(uiTestHelper.getHelpScreen().equals(uiTestHelper.getCPCCurrentScreen()));
    }

    // ================================================================================
    // HelpScreen Testing
    // ================================================================================
//    @Test
    public void HelpScreen_InitTest() {
        UITestHelper uiTestHelper = new UITestHelper();
        assertTrue(uiTestHelper.getNewHelpScreen() != null);
    }

    // ================================================================================
    // MainScreen Testing
    // ================================================================================
//    @Test
    public void MainScreen_InitTest() {
        UITestHelper uiTestHelper = new UITestHelper();
        assertTrue(uiTestHelper.getNewMainScreen() != null);
        assertTrue(uiTestHelper.getMainScreenVBox() != null);

        assertTrue(uiTestHelper.getMainScreenVBox().getChildren().size() == 4);

    }

//    @Test
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
