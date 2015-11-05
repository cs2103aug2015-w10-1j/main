//@@author A0126576X
package procrastinate.test;

import static org.junit.Assert.*;

import org.junit.Test;

import procrastinate.Command.CommandType;
import procrastinate.Parser;
import procrastinate.Command;

public class ParserTest {
    private static final String MESSAGE_INVALID_LINE_NUMBER = "Please specify a valid line number";
    private static final String MESSAGE_INVALID_NO_DESCRIPTION = "Please specify the description";

    @Test
    public void addDreamTest() {
        /* This is the normal format for adding a dream */
        Command resultCommand = Parser.parse("write test case for V0.2");
        assertEquals(CommandType.ADD_DREAM, resultCommand.getType());
        assertEquals("write test case for V0.2", resultCommand.getDescription());

        /* Add dream with "add" as a keyword */
        resultCommand = Parser.parse("add write test case for V0.2");
        assertEquals(CommandType.ADD_DREAM, resultCommand.getType());
        assertEquals("write test case for V0.2", resultCommand.getDescription());

        /* Add dream with "edit" as a keyword */
        resultCommand = Parser.parse("edit essay");
        assertEquals(CommandType.ADD_DREAM, resultCommand.getType());
        assertEquals("edit essay", resultCommand.getDescription());

        /* Add dream with "do" as a keyword */
        resultCommand = Parser.parse("do project manual for CS2103T project");
        assertEquals(CommandType.ADD_DREAM, resultCommand.getType());
        assertEquals("do project manual for CS2103T project", resultCommand.getDescription());

        /* Add dream with "delete" as a keyword */
        resultCommand = Parser.parse("do project manual for CS2103T project");
        assertEquals(CommandType.ADD_DREAM, resultCommand.getType());
        assertEquals("do project manual for CS2103T project", resultCommand.getDescription());

        /* Add dream with "procrastinate" as a keyword */
        resultCommand = Parser.parse("procrastinate because it is not in a hurry");
        assertEquals(CommandType.ADD_DREAM, resultCommand.getType());
        assertEquals("procrastinate because it is not in a hurry", resultCommand.getDescription());

        /* Add dream with "exit" as a keyword */
        resultCommand = Parser.parse("exit something");
        assertEquals(CommandType.ADD_DREAM, resultCommand.getType());
        assertEquals("exit something", resultCommand.getDescription());

        /* Add dream with "undo" as a keyword */
        resultCommand = Parser.parse("undo a change in project");
        assertEquals(CommandType.ADD_DREAM, resultCommand.getType());
        assertEquals("undo a change in project", resultCommand.getDescription());

        /* Add dream with "search" as a keyword */
        resultCommand = Parser.parse("add search for my stuff");
        assertEquals(CommandType.ADD_DREAM, resultCommand.getType());
        assertEquals("search for my stuff", resultCommand.getDescription());

        /* Add dream with "help" as a keyword */
        resultCommand = Parser.parse("help out a friend");
        assertEquals(CommandType.ADD_DREAM, resultCommand.getType());
        assertEquals("help out a friend", resultCommand.getDescription());

        /* Add dream with "show" as a keyword */
        resultCommand = Parser.parse("show something amazing to a friend");
        assertEquals(CommandType.ADD_DREAM, resultCommand.getType());
        assertEquals("show something amazing to a friend", resultCommand.getDescription());
    }

    @Test
    public void addDeadlineTest() {
        /* Add deadline with "do" as a keyword with only dates as argument */
        Command resultCommand = Parser.parse("do due tomorrow");
        assertEquals(CommandType.ADD_DEADLINE, resultCommand.getType());
        assertEquals("do", resultCommand.getDescription());

        /* Add deadline with using "due" as keyword  */
        resultCommand = Parser.parse("something important due tomorrow");
        assertEquals(CommandType.ADD_DEADLINE, resultCommand.getType());
        assertEquals("something important", resultCommand.getDescription());

        /* Add deadline with using "on" as keyword  */
        resultCommand = Parser.parse("something important on tomorrow");
        assertEquals(CommandType.ADD_DEADLINE, resultCommand.getType());
        assertEquals("something important", resultCommand.getDescription());
    }

    @Test
    public void editTest() {
        /* Edit in a standard format with no dates*/
        Command resultCommand = Parser.parse("edit 1 write user guide");
        assertEquals(CommandType.EDIT, resultCommand.getType());
        assertEquals(1, resultCommand.getLineNumber());
        assertEquals("write user guide", resultCommand.getDescription());

        /* Edit with no description*/
        resultCommand = Parser.parse("edit 1");
        assertEquals(CommandType.EDIT_PARTIAL, resultCommand.getType());
        assertEquals(1, resultCommand.getLineNumber());

        /* Edit with no line number*/
        resultCommand = Parser.parse("edit");
        assertEquals(CommandType.INVALID, resultCommand.getType());
        assertEquals(MESSAGE_INVALID_LINE_NUMBER, resultCommand.getDescription());

        /* Edit with eventually keyword*/
        resultCommand = Parser.parse("edit 1 eventually");
        assertEquals(CommandType.EDIT_TO_DREAM, resultCommand.getType());
    }

    @Test
    public void deleteTest() {
        /* Delete in a standard format*/
        Command resultCommand = Parser.parse("delete 1");
        assertEquals(CommandType.DELETE, resultCommand.getType());
        assertEquals(1, resultCommand.getLineNumber());

        /* Delete with no line number*/
        resultCommand = Parser.parse("delete");
        assertEquals(CommandType.INVALID, resultCommand.getType());
        assertEquals(MESSAGE_INVALID_LINE_NUMBER, resultCommand.getDescription());
    }

    @Test
    public void undoTest() {
        /* Undo in a standard format*/
        Command resultCommand = Parser.parse("undo");
        assertEquals(resultCommand.getType(), CommandType.UNDO);
    }

    @Test
    public void searchTest() {
        /* Search in a standard format*/
        Command resultCommand = Parser.parse("search keyword");
        assertEquals(resultCommand.getType(), CommandType.SEARCH);
        assertEquals("keyword", resultCommand.getDescription());

        /* Search with no keywords*/
        resultCommand = Parser.parse("search");
        assertEquals(CommandType.INVALID, resultCommand.getType());
        assertEquals(MESSAGE_INVALID_NO_DESCRIPTION, resultCommand.getDescription());

        /* Search with on keyword with a date*/
        resultCommand = Parser.parse("search on tomorrow");
        assertEquals(CommandType.SEARCH_ON, resultCommand.getType());

        /* Search with on keyword with no dates*/
        resultCommand = Parser.parse("search on something important");
        assertEquals(CommandType.SEARCH, resultCommand.getType());
        assertEquals("on something important", resultCommand.getDescription());

        /* Search with due keyword with a date*/
        resultCommand = Parser.parse("search due tomorrow");
        assertEquals(CommandType.SEARCH, resultCommand.getType());

        /* Search with on keyword with no dates*/
        resultCommand = Parser.parse("search due to some reason");
        assertEquals(CommandType.SEARCH, resultCommand.getType());
        assertEquals("due to some reason", resultCommand.getDescription());

    }

    @Test
    public void helpTest() {
        /* Help in a standard format*/
        Command resultCommand = Parser.parse("help");
        assertEquals(CommandType.HELP, resultCommand.getType());
    }

    @Test
    public void showTest() {
        /* Show in a standard format*/
        Command resultCommand = Parser.parse("show");
        assertEquals(CommandType.SHOW_OUTSTANDING, resultCommand.getType());

        /* Show with "all" keyword*/
        resultCommand = Parser.parse("show all");
        assertEquals(CommandType.SHOW_ALL, resultCommand.getType());

        /* Show with "done" keyword*/
        resultCommand = Parser.parse("show done");
        assertEquals(CommandType.SHOW_DONE, resultCommand.getType());
    }

    @Test
    public void setPathTest() {
        /* Set Path with no quotes*/
        Command resultCommand = Parser.parse("set something else");
        assertEquals(CommandType.SET_PATH, resultCommand.getType());
        assertEquals("something", resultCommand.getPathDirectory());
        assertEquals("else", resultCommand.getPathFilename());

        /* Set Path with no filename*/
        resultCommand = Parser.parse("set something");
        assertEquals(CommandType.SET_PATH, resultCommand.getType());
        assertEquals("something", resultCommand.getPathDirectory());

        /* Set Path with more than two arguments*/
        resultCommand = Parser.parse("set something else too");
        assertEquals(CommandType.ADD_DREAM, resultCommand.getType());

        /* Set Path with quotes in directory*/
        resultCommand = Parser.parse("set \"something else\" too");
        assertEquals(CommandType.SET_PATH, resultCommand.getType());
        assertEquals("something else", resultCommand.getPathDirectory());
        assertEquals("too", resultCommand.getPathFilename());

        /* Set Path with quotes in directory with no filename*/
        resultCommand = Parser.parse("set \"something else\"");
        assertEquals(CommandType.SET_PATH, resultCommand.getType());
        assertEquals("something else", resultCommand.getPathDirectory());

        /* Set Path with quotes in filename only*/
        resultCommand = Parser.parse("set too \"something else\"");
        assertEquals(CommandType.SET_PATH, resultCommand.getType());
        assertEquals("too", resultCommand.getPathDirectory());
        assertEquals("something else", resultCommand.getPathFilename());

        /* Set Path with quotes in directory with quotes in filename*/
        resultCommand = Parser.parse("set \"something else\" \"too\"");
        assertEquals(CommandType.SET_PATH, resultCommand.getType());
        assertEquals("something else", resultCommand.getPathDirectory());
        assertEquals("too", resultCommand.getPathFilename());

        /* Illegal set path formats*/
        resultCommand = Parser.parse("set\"something else\" \"too\"");
        assertEquals(CommandType.ADD_DREAM, resultCommand.getType());
        assertEquals("set\"something else\" \"too\"", resultCommand.getDescription());

        resultCommand = Parser.parse("set\"something else\" \"too\" more words");
        assertEquals(CommandType.ADD_DREAM, resultCommand.getType());
        assertEquals("set\"something else\" \"too\" more words", resultCommand.getDescription());
    }

    @Test
    public void escapeCharacterTest() {
        /* Escape on keywords*/
        Command resultCommand = Parser.parse("\\do 1");
        assertEquals(CommandType.ADD_DREAM, resultCommand.getType());
        assertEquals("do 1", resultCommand.getDescription());

        resultCommand = Parser.parse("something \\due tomorrow");
        assertEquals(CommandType.ADD_DREAM, resultCommand.getType());
        assertEquals("something due tomorrow", resultCommand.getDescription());

        resultCommand = Parser.parse("something \\on tomorrow");
        assertEquals(CommandType.ADD_DREAM, resultCommand.getType());
        assertEquals("something on tomorrow", resultCommand.getDescription());

        resultCommand = Parser.parse("something \\from tomorrow to next week");
        assertEquals(CommandType.ADD_DREAM, resultCommand.getType());
        assertEquals("something from tomorrow to next week", resultCommand.getDescription());

        /* Escape on non-keywords*/
        resultCommand = Parser.parse("this should \\be done");
        assertEquals(CommandType.ADD_DREAM, resultCommand.getType());
        assertEquals("this should be done", resultCommand.getDescription());

    }
}
