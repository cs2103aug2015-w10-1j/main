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
    }

    @Test
    public void addDeadlineTest() {
        /* Add deadline with "do" as a keyword with only dates as argument */
        Command resultCommand = Parser.parse("do due tomorrow");
        assertEquals(CommandType.ADD_DEADLINE, resultCommand.getType());
        assertEquals("do", resultCommand.getDescription());
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
    }

    @Test
    public void helpTest() {
        /* Help in a standard format*/
        Command resultCommand = Parser.parse("help");
        assertEquals(resultCommand.getType(), CommandType.HELP);
    }
}
