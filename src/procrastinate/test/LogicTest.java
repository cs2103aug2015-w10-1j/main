//@@author A0080485B
package procrastinate.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
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
        System.out.println(getResults());
        System.out.println("Test completed!\n");
    }

    @Test
    public void previewTest() {
        assertEquals(preview("exit"), "Goodbye!");
        assertEquals(preview("help"), "Showing help screen (use left/right keys to navigate)");
        assertEquals(preview("show"), "Showing outstanding tasks");
        assertEquals(preview("show done"), "Showing completed tasks");
        assertEquals(preview("show all"), "Showing all tasks");
        assertEquals(preview("show summary"), "Showing summary of outstanding tasks");
        assertEquals(preview("search abc"), "Searching for tasks containing 'abc'");
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
        assertEquals(expected, getResults());

        assertEquals(preview("invalid event from 10/15/17 to 10/14/17 0"),
                     "Invalid dates: 14/10/17 12:00AM is before 15/10/17 12:00AM");
    }

    @Test
    public void editTest() throws ParseException {
        assertEquals(preview("edit 1"), "Invalid line number: 1");

        execute("dream");
        assertEquals(preview("edit 1"), "Please specify the new description/date(s) or press tab");

        // edit description
        execute("edit 1 dream edited");
        List<Task> expected = new ArrayList<Task>();
        expected.add(new Dream("dream edited"));
        assertEquals(expected, getResults());

        // edit to event
        execute("edit 1 from 10/14/17 to 10/15/17 0");
        expected.clear();
        expected.add(new Event("dream edited", sdf.parse("10/14/17"), sdf.parse("10/15/17")));
        assertEquals(expected, getResults());

        // edit to event with new description
        execute("edit 1 event from 10/14/17 to 10/15/17 0");
        expected.clear();
        expected.add(new Event("event", sdf.parse("10/14/17"), sdf.parse("10/15/17")));
        assertEquals(expected, getResults());

        // edit just the description of event
        execute("edit 1 event edited");
        expected.clear();
        expected.add(new Event("event edited", sdf.parse("10/14/17"), sdf.parse("10/15/17")));
        assertEquals(expected, getResults());

        // edit to deadline
        execute("edit 1 due 10/13/17 0");
        expected.clear();
        expected.add(new Deadline("event edited", sdf.parse("10/13/17")));
        assertEquals(expected, getResults());

        // edit to deadline with new description
        execute("edit 1 deadline due 10/13/17 0");
        expected.clear();
        expected.add(new Deadline("deadline", sdf.parse("10/13/17")));
        assertEquals(expected, getResults());

        // edit just the description of deadline
        execute("edit 1 deadline edited");
        expected.clear();
        expected.add(new Deadline("deadline edited", sdf.parse("10/13/17")));
        assertEquals(expected, getResults());

        // edit back to dream
        execute("edit 1 eventually");
        expected.clear();
        expected.add(new Dream("deadline edited"));
        assertEquals(expected, getResults());

        assertEquals(preview("edit 1 from 10/15/17 to 10/14/17 0"),
                     "Invalid dates: 14/10/17 12:00AM is before 15/10/17 12:00AM");

        assertEquals(preview("edit 0 from 10/15/17 to 10/14/17 0"),
                     "Invalid line number: 0");
    }

    @Test
    public void deleteUndoTest() {
        assertEquals(preview("undo"), "Nothing to undo");

        execute("a");
        execute("b");
        execute("c");

        assertEquals(preview("delete 3"), "Deleted dream: c");
        assertEquals(preview("delete 4"), "Invalid line number: 4");

        execute("delete 2");
        List<Task> expected = new ArrayList<Task>();
        expected.add(new Dream("a"));
        expected.add(new Dream("c"));
        assertEquals(expected, getResults());
        assertEquals(preview("delete 3"), "Invalid line number: 3");

        execute("undo");
        expected.add(1, new Dream("b"));
        assertEquals(expected, getResults());
        assertEquals(preview("delete 3"), "Deleted dream: c");
    }

    @Test
    public void doneTest() {
        assertEquals(preview("done 1"), "Invalid line number: 1");

        execute("a");
        execute("show all");

        execute("done 1");
        List<Task> expected = new ArrayList<Task>();
        expected.add(new Dream("a"));
        expected.get(0).setDone(true);
        assertEquals(expected, getResults());

        execute("done 1");
        expected.get(0).setDone(false);
        assertEquals(expected, getResults());
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
        expected.get(0).setDone(true);
        expected.get(1).setDone(true);
        assertEquals(expected, getResults());

        execute("show");
        expected.clear();
        expected.add(new Dream("a"));
        expected.add(new Dream("b"));
        expected.add(new Dream("e"));
        assertEquals(expected, getResults());

        execute("show summary");
        assertEquals(expected, getResults());

        execute("show all");
        expected.clear();
        expected.add(new Dream("a"));
        expected.add(new Dream("b"));
        expected.add(new Dream("e"));
        expected.add(new Dream("c"));
        expected.add(new Dream("d"));
        expected.get(3).setDone(true); // done c
        expected.get(4).setDone(true); // done d
        assertEquals(expected, getResults());
    }

    @Test
    public void invalidTest() {
        assertEquals(preview("due tonight"), "Please specify the description");
        assertEquals(preview("edit 0"), "Invalid line number: 0");
        assertEquals(preview("edit 0 abc"), "Invalid line number: 0");
        assertEquals(preview("edit 0 due 10/15/17 0"), "Invalid line number: 0");
        assertEquals(preview("edit 0 eventually"), "Invalid line number: 0");
        assertEquals(preview("done 0"), "Invalid line number: 0");
        assertEquals(preview("search from 10/15/17 to 10/14/17 0"), "Invalid dates: 14/10/17 is before 15/10/17");
    }

    @Test
    public void helpTest() {
        assertEquals(execute("help"), "Showing help screen (use left/right keys to navigate)");
    }

    @Test
    public void setPathTest() {
        assertEquals(preview("set /x"), "Set save location to " + File.listRoots()[0].getAbsolutePath() + "x" + File.separator + "storage.json");
        assertEquals(preview("set /x abc"), "Set save location to " + File.listRoots()[0].getAbsolutePath() + "x" + File.separator + "abc");
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

        assertEquals(expected, getResults());
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
        assertEquals(expected, getResults());
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

        assertEquals(expected, getResults());
    }
    //@@author

    @Test
    public void searchDue_ByDateDescription_ShouldShowTasksTillDateWithDescription() throws ParseException {
        List<Task> expected = new ArrayList<Task>();
        execute("a due 1/2/14 12:00am");
        execute("b due 1/3/14 12:00am");
        execute("c due 10/1/14 12:00am");
        execute("d due 10/1/15 12:00am");

        execute("search a due 10/1/2014");

        expected.add(new Deadline("a", sdf.parse("1/2/14")));

        assertEquals(expected, getResults());
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
        assertEquals(expected, getResults());

        execute("search due 1/3/14 12:00am");
        expected.add(new Event("a", sdf.parse("1/5/14"), sdf.parse("1/6/14")));
        assertEquals(expected, getResults());
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

        assertEquals(expected, getResults());
    }
    //@@author

    @Test
    public void searchOn_ByDateDescription_ShouldShowTasksOnDateWithDescription() throws ParseException {
        List<Task> expected = new ArrayList<Task>();
        execute("a due 1/1/14 12:00am");
        execute("b due 2/1/14 12:00am");
        execute("c due 2/1/14 12:00am");
        execute("d due 1/1/15 12:00am");

        execute("search c on 2/1/2014");

        expected.add(new Deadline("c", sdf.parse("2/1/14")));

        assertEquals(expected, getResults());
    }

    @Test
    public void searchRange_ByDates_ShouldShowTasksWithinDates() throws ParseException {
        List<Task> expected = new ArrayList<Task>();
        execute("a due 1/1/14 12:00am");
        execute("b due 2/1/14 12:00am");
        execute("c due 3/1/14 12:00am");
        execute("d due 4/1/14 12:00am");
        execute("e due 5/1/14 12:00am");

        execute("search from 2/1/2014 to 4/1/2014");

        expected.add(new Deadline("b", sdf.parse("2/1/14")));
        expected.add(new Deadline("c", sdf.parse("3/1/14")));
        expected.add(new Deadline("d", sdf.parse("4/1/14")));

        assertEquals(expected, getResults());
    }

    @Test
    public void searchRange_ByDatesDescription_ShouldShowTasksWithinDatesWithDescription() throws ParseException {
        List<Task> expected = new ArrayList<Task>();
        execute("a due 1/1/14 12:00am");
        execute("b due 2/1/14 12:00am");
        execute("b due 3/1/14 12:00am");
        execute("d due 4/1/14 12:00am");
        execute("e due 5/1/14 12:00am");

        execute("search b from 2/1/2014 to 4/1/2014");

        expected.add(new Deadline("b", sdf.parse("2/1/14")));
        expected.add(new Deadline("b", sdf.parse("3/1/14")));

        assertEquals(expected, getResults());
    }

    private String execute(String userCommand) {
        logic.previewCommand(userCommand);
        return logic.executeLastPreviewedCommand();
    }

    private String preview(String userCommand) {
        return logic.previewCommand(userCommand);
    }

    private List<Task> getResults() {
        return uiStub.getTaskList();
    }

}
