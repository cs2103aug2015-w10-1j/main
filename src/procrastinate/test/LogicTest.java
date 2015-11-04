//@@author A0080485B
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
        logic = new LogicUnit(uiStub);
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
        assertEquals(logic.previewCommand("help"), "Showing help screen (use left/right keys to navigate)");
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
        execute("done 3"); // done c
        execute("done 3"); // done d

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
        expected.get(3).setDone(); // done c
        expected.get(4).setDone(); // done d
        assertEquals(expected, getTaskList());
    }

    //@@author A0124321Y
    @Test
    public void search_DescriptionByWordDifferentTaskTypes_ShouldReturnHits() throws ParseException {
        List<Task> expected = new ArrayList<Task>();
        execute("foo 1");
        execute("foo 2 due 1/2/14 12:00am");
        execute("foo 3 from 1/2/14 to 2/2/2014 12:00am");

        execute("search foo");

        expected.add(new Deadline("foo 2", sdf.parse("1/2/14")));
        expected.add(new Event("foo 3", sdf.parse("1/2/14"), sdf.parse("2/2/14")));
        expected.add(new Dream("foo 1"));

        assertEquals(expected, getTaskList());
    }

    //@@author A0124321Y
    @Test
    public void search_DescriptionByWord_ShouldShowHits() {
        List<Task> expected = new ArrayList<Task>();
        execute("foo has bar");
        execute("foo has baz");
        execute("foo is not bar");

        execute("search foo");

        expected.add(new Dream("foo has bar"));
        expected.add(new Dream("foo has baz"));
        expected.add(new Dream("foo is not bar"));
        assertEquals(expected, getTaskList());
    }

    //@@author A0124321Y
    @Test
    public void searchDue_ByDate_ShouldShowTasksTillDate() throws ParseException {
        List<Task> expected = new ArrayList<Task>();
        execute("a due 1/2/14 12:00am");
        execute("b due 1/3/14 12:00am");
        execute("c due 10/1/14 12:00am");
        execute("d due 10/1/15 12:00am");

        execute("search due 10/1/2014");

        expected.add(new Deadline("a", sdf.parse("1/2/14")));
        expected.add(new Deadline("b", sdf.parse("1/3/14")));
        expected.add(new Deadline("c", sdf.parse("10/1/14")));

        assertEquals(expected, getTaskList());
    }

    //@@author A0124321Y
    // start or end dates are not distinct
    @Test
    public void searchDue_ByDateDiffTaskTypes_ShouldShowTasksWithGivenDates() throws ParseException {
        List<Task> expected = new ArrayList<Task>();
        execute("a due 1/2/14 12:00am");
        execute("a from 1/2/14 to 1/3/14 12:00am");
        execute("a from 1/5/14 to 1/6/14 12:00am");
        execute("a due 10/1/14 12:00am");

        execute("search due 1/2/14 12:00am");
        expected.add(new Deadline("a", sdf.parse("1/2/14")));
        expected.add(new Event("a", sdf.parse("1/2/14"), sdf.parse("1/3/14")));
        assertEquals(expected, getTaskList());

        execute("search due 1/3/14 12:00am");
        expected.add(new Event("a", sdf.parse("1/5/14"), sdf.parse("1/6/14")));
        assertEquals(expected, getTaskList());
    }

    //@@author A0124321Y
    @Test
    public void searchOn_ByDate_ShouldShowTasksOnDate() throws ParseException {
        List<Task> expected = new ArrayList<Task>();
        execute("a due 1/1/14 12:00am");
        execute("b due 1/2/14 12:00am");
        execute("c due 2/1/14 12:00am");
        execute("d due 1/1/15 12:00am");

        execute("search on 2/1/2014");

        expected.add(new Deadline("c", sdf.parse("2/1/14")));

        assertEquals(expected, getTaskList());
    }
    //@@author

    private void execute(String userCommand) {
        logic.previewCommand(userCommand);
        logic.executeLastPreviewedCommand();
    }

    private List<Task> getTaskList() {
        return uiStub.taskList;
    }

}
