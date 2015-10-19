package procrastinate;

import com.joestelmach.natty.DateGroup;
import procrastinate.Command.CommandType;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Parser {

    private static final Logger logger = Logger.getLogger(Parser.class.getName());
    private static final com.joestelmach.natty.Parser dateParser = new com.joestelmach.natty.Parser();

    // ================================================================================
    // Message strings
    // ================================================================================

    private static final String DEBUG_PARSING_COMMAND = "Parsing command: ";

    private static final String MESSAGE_INVALID_NO_DESCRIPTION = "Please specify the description";
    private static final String MESSAGE_INVALID_LINE_NUMBER = "Please specify a valid line number";
    private static final String MESSAGE_INVALID_EDIT_NO_NEW_DATA = "Please specify the new description or date(s)";

    private static final String COMMAND_ADD = "add";
    private static final String COMMAND_EDIT = "edit";
    private static final String COMMAND_DELETE = "delete";
    private static final String COMMAND_DONE = "done";
    private static final String COMMAND_UNDO = "undo";
    private static final String COMMAND_SEARCH = "search";
    private static final String COMMAND_EXIT = "procrastinate";

    private static final String COMMAND_SHORT_EDIT = "ed";
    private static final String COMMAND_SHORT_DELETE = "del";
    private static final String COMMAND_SHORT_DONE = "do";
    private static final String COMMAND_SHORT_UNDO = "un";
    private static final String COMMAND_SHORT_SEARCH = "se";
    private static final String COMMAND_SHORT_EXIT = "exit";

    private static final String KEYWORD_DEADLINE = "due";

    // ================================================================================
    // Parser methods
    // ================================================================================

    public static Command parse(String userInput) {
        logger.log(Level.FINE, DEBUG_PARSING_COMMAND + userInput);

        String userCommand = userInput.trim(); // Trim leading and trailing whitespace
        Date inputDate = getDate(userCommand);
        userCommand = removeDatesFromUserCommand(userCommand, inputDate);
        // If there was a date, userCommand now comes with a trailing space.
        // This helps identify commands with no arguments: the expression
        // userCommand.equalsIgnoreCase(firstWord) will only be true if
        // no arguments were ever specified (if a date argument was specified and
        // subsequently removed, the expression will be false due to the trailing space).

        if (isCommandEmpty(userCommand)) {
            return new Command(CommandType.INVALID).addDescription(MESSAGE_INVALID_NO_DESCRIPTION);
        }

        String firstWord = getFirstWord(userCommand).toLowerCase(); // Case insensitive

        switch (firstWord) {
            case COMMAND_ADD: {
                if (userCommand.equalsIgnoreCase(firstWord)) { // No arguments
                    // Treat "add" as an invalid command
                    // Display a helpful message (no description)
                    return new Command(CommandType.INVALID).addDescription(MESSAGE_INVALID_NO_DESCRIPTION);
                }

                String[] argument = userCommand.split(" ", 2);
                String description = argument[1];

                if (description.isEmpty()) {
                    // Display a helpful message (no description)
                    return new Command(CommandType.INVALID).addDescription(MESSAGE_INVALID_NO_DESCRIPTION);
                }

                Command command;
                if (inputDate != null) {
                    command = new Command(CommandType.ADD_DEADLINE).addDate(inputDate);
                } else {
                    command = new Command(CommandType.ADD_DREAM);
                }
                command.addDescription(description);

                return command;
            }

            case COMMAND_EDIT:
            case COMMAND_SHORT_EDIT: {
                if (userCommand.equalsIgnoreCase(firstWord)) { // No arguments
                    // Treat "edit" as an invalid command
                    // Display a helpful message (no line number given)
                    return new Command(CommandType.INVALID).addDescription(MESSAGE_INVALID_LINE_NUMBER);
                }

                int lineNumber = 0;
                try {
                    String[] argument = userCommand.split(" ", 3);
                    lineNumber = Integer.parseInt(argument[1]);
                    String description = argument[2];

                    Command command = new Command(CommandType.EDIT).addLineNumber(lineNumber);
                    if (inputDate != null) {
                        command.addDate(inputDate);
                    }
                    if (!description.isEmpty()) {
                        command.addDescription(description);
                    }

                    return command;

                } catch (NumberFormatException e) { // Not a line number
                    // Treat "edit something" as an add command
                    // Inject add to the front of command and recurse
                    return Parser.parse(putAddInFront(userInput));

                } catch (Exception e) { // Display a helpful message for "edit 1" (no description or date(s) given)
                    return new Command(CommandType.INVALID).addDescription(MESSAGE_INVALID_EDIT_NO_NEW_DATA);
                }
            }

            case COMMAND_DELETE:
            case COMMAND_SHORT_DELETE:
                try {
                    String argument = userCommand.substring(firstWord.length() + 1);
                    int lineNumber = Integer.parseInt(argument);

                    return new Command(CommandType.DELETE).addLineNumber(lineNumber);

                } catch (NumberFormatException e) { // Not a line number
                    // Treat "delete something" is an add command
                    // Inject add to the front of command and recurse
                    return Parser.parse(putAddInFront(userInput));

                } catch (Exception e) { // Display a helpful message for "delete" (no line number given)
                    return new Command(CommandType.INVALID).addDescription(MESSAGE_INVALID_LINE_NUMBER);
                }

            case COMMAND_UNDO:
            case COMMAND_SHORT_UNDO: {
                if (!userCommand.equalsIgnoreCase(firstWord)) { // Extra arguments
                    // Treat "undo something" as an add command
                    // Inject add to the front of command and recurse
                    return Parser.parse(putAddInFront(userInput));
                }

                return new Command(CommandType.UNDO);
            }

            case COMMAND_DONE:
            case COMMAND_SHORT_DONE: {
                if (userCommand.equalsIgnoreCase(firstWord)) { // No arguments
                    // Treat "done" as an invalid command
                    // Display a helpful message (no line number given)
                    return new Command(CommandType.INVALID).addDescription(MESSAGE_INVALID_LINE_NUMBER);
                }

                try {
                    String[] argument = userCommand.split(" ", 2);
                    int lineNumber = Integer.parseInt(argument[1]);

                    return new Command(CommandType.DONE).addLineNumber(lineNumber);

                } catch (NumberFormatException e) { // Not a line number
                    // Treat "done something" as an add command
                    // Inject add to the front of command and recurse
                    return Parser.parse(putAddInFront(userInput));
                }
            }

            case COMMAND_SEARCH:
            case COMMAND_SHORT_SEARCH: {
                if (userCommand.equalsIgnoreCase(firstWord)) { // No arguments
                    // Treat "search" as an invalid command
                    // Display a helpful message (no description)
                    return new Command(CommandType.INVALID).addDescription(MESSAGE_INVALID_NO_DESCRIPTION);
                }

                String[] argument = userCommand.split(" ", 2);
                String searchDescription = argument[1];

                Command command = new Command(CommandType.SEARCH);
                if (inputDate != null) {
                    command.addDate(inputDate);
                }
                if (!searchDescription.isEmpty()) {
                    command.addDescription(searchDescription);
                }

                return command;
            }

            case COMMAND_EXIT:
            case COMMAND_SHORT_EXIT: {
                if (!userCommand.equalsIgnoreCase(firstWord)) { // Extra arguments
                    // Treat "procrastinate something" as an add command
                    // Inject add to the front of command and recurse
                    return Parser.parse(putAddInFront(userInput));
                }

                return new Command(CommandType.EXIT);
            }

            default: {
                // Inject add to the front of command and recurse
                return Parser.parse(putAddInFront(userInput));
            }
        }

    }

    // ================================================================================
    // Utility methods
    // ================================================================================

    private static Date getDate(String userCommand) {
        String[] arguments = userCommand.split("due");
        if (arguments.length <= 1) {
            return null;
        }

        List<DateGroup> dateGroups = dateParser.parse(arguments[arguments.length - 1]);
        if (hasDates(dateGroups)) {
            return dateGroups.get(0).getDates().get(0);
        } else {
            return null;
        }
    }

    private static String removeDatesFromUserCommand(String userCommand, Date inputDate) {
        if (inputDate == null) {
            if (userCommand.equals(KEYWORD_DEADLINE)) {
                return null;
            } else {
                return userCommand;
            }
        } else {
            String[] arguments = userCommand.split(KEYWORD_DEADLINE);
            StringBuilder stringBuilder = new StringBuilder();
            for(int i = 0; i < arguments.length - 1; i ++) {
                stringBuilder.append(arguments[i]);
                if (i != arguments.length - 2) {
                    stringBuilder.append(KEYWORD_DEADLINE);
                }
            }
            return stringBuilder.toString(); // Do NOT trim
        }
    }

    private static boolean isCommandEmpty(String userCommand) {
        return userCommand == null || userCommand.isEmpty();
    }

    private static String getFirstWord(String userCommand) {
        return userCommand.split(" ")[0];
    }

    private static String putAddInFront(String userInput) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(COMMAND_ADD);
        stringBuilder.append(" ");
        stringBuilder.append(userInput);
        return stringBuilder.toString();
    }

    private static boolean hasDates(List<DateGroup> groups) {
        return !groups.isEmpty();
    }
}
