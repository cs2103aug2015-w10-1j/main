package procrastinate.test;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import procrastinate.Logic;
import procrastinate.task.Deadline;
import procrastinate.task.Dream;
import procrastinate.task.Event;
import procrastinate.task.Task;

public class LogicTest {

    private static SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");

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
    }

    @Test
    public void addTest() throws ParseException {
        execute("dream");
        execute("deadline due 10/13/17 0");
        execute("event from 10/14/17 to 10/15/17 0");
        execute("another dream");
        execute("urgent deadline due 10/13/16 0");
        List<Task> expected = new ArrayList<Task>();
        expected.add(new Deadline("urgent deadline", sdf.parse("10/13/16")));
        expected.add(new Deadline("deadline", sdf.parse("10/13/17")));
        expected.add(new Event("event", sdf.parse("10/14/17"), sdf.parse("10/15/17")));
        expected.add(new Dream("another dream"));
        expected.add(new Dream("dream"));
        assertEquals(expected, getTaskList());
    }

    @Test
    public void showTest() {
        execute("a");
        execute("b");
        execute("c");
        execute("d");
        execute("e");
        execute("done 3");
        execute("done 3");

        execute("show done");
        List<Task> expected = new ArrayList<Task>();
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
        expected.add(new Dream("e"));
        expected.add(new Dream("c"));
        expected.add(new Dream("d"));
        expected.get(3).setDone();
        expected.get(4).setDone();
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
