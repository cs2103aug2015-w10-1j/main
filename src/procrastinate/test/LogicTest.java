package procrastinate.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import procrastinate.Logic;
import procrastinate.task.Dream;
import procrastinate.task.Task;

public class LogicTest {

    private Logic logic;
    private UIStub uiStub;

    @Before
    public void setUp() throws Exception {
        System.out.println("Setting up test...");
        uiStub = new UIStub();
        logic = Logic.getTestInstance(uiStub);
    }

    @After
    public void tearDown() throws Exception {
        System.out.println("Tearing down. Final state:");
        for (Task task : uiStub.taskList) {
            System.out.println(task.getDescription());
        }
        System.out.println("Test completed!");
        System.out.println();
    }

    @Test
    public void trivialTest() {
        assertEquals(logic.previewCommand("exit"), "Goodbye!");
        assertEquals(logic.previewCommand("help"), "Showing help screen");
        assertEquals(logic.previewCommand("show"), "Showing outstanding tasks");
        assertEquals(logic.previewCommand("show done"), "Showing completed tasks");
        assertEquals(logic.previewCommand("show all"), "Showing all tasks");
        assertEquals(logic.previewCommand("search abc"), "Searching for tasks containing 'abc'");
    }

    @Test
    public void showTest() {
        execute("123");
        List<Task> expected = new ArrayList<Task>();
        expected.add(new Dream("123"));
        assertEquals(expected, getTaskList());
    }

    private void execute(String userCommand) {
        logic.previewCommand(userCommand);
        logic.executeLastPreviewedCommand();
    }

    private List<Task> getTaskList() {
        return uiStub.taskList;
    }

}
