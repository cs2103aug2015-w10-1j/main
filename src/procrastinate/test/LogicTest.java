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
        System.out.println(getTaskList());
        System.out.println("Test completed!\n");
    }

    @Test
    public void trivialTest() {
        assertEquals(logic.previewCommand("exit"), "Goodbye!");
        assertEquals(logic.previewCommand("help"), "Showing help screen");
        assertEquals(logic.previewCommand("show"), "Showing outstanding tasks");
        assertEquals(logic.previewCommand("show done"), "Showing completed tasks");
        assertEquals(logic.previewCommand("show all"), "Showing all tasks");
        assertEquals(logic.previewCommand("search abc"), "Searching for tasks containing 'abc'");
        execute("trivial");
    }

    @Test
    public void showTest() {
        List<Task> expected = new ArrayList<Task>();

        execute("a");
        execute("b");
        execute("c");
        execute("d");
        execute("e");
        execute("done 3");
        execute("done 3");
        execute("show done");
        expected.clear();
        expected.add(new Dream("c"));
        expected.add(new Dream("d"));
        expected.get(0).setDone();
        expected.get(1).setDone();
        assertEquals(expected, getTaskList());

        execute("show");
        expected.clear();
        expected.add(new Dream("a"));
        expected.add(new Dream("b"));
        expected.add(new Dream("e"));
        assertEquals(expected, getTaskList());

        execute("show all");
        expected.clear();
        expected.add(new Dream("a"));
        expected.add(new Dream("b"));
        expected.add(new Dream("c"));
        expected.add(new Dream("d"));
        expected.add(new Dream("e"));
        expected.get(2).setDone();
        expected.get(3).setDone();
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
