package procrastinate;

import com.joestelmach.natty.DateGroup;

import procrastinate.Command.CommandType;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.time.DateUtils;

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
    private static final String MESSAGE_INVALID_NO_PATH = "Please specify the save directory path";

    private static final String COMMAND_ADD = "add";
    private static final String COMMAND_EDIT = "edit";
    private static final String COMMAND_DELETE = "delete";
    private static final String COMMAND_DONE = "done";
    private static final String COMMAND_UNDO = "undo";
    private static final String COMMAND_SEARCH = "search";
    private static final String COMMAND_SHOW = "show";
    private static final String COMMAND_SET_PATH = "set";
    private static final String COMMAND_HELP = "help";
    private static final String COMMAND_EXIT = "procrastinate";

    private static final String COMMAND_SHORT_EDIT = "ed";
    private static final String COMMAND_SHORT_DELETE = "del";
    private static final String COMMAND_SHORT_DONE = "do";
    private static final String COMMAND_SHORT_UNDO = "un";
    private static final String COMMAND_SHORT_SEARCH = "se";
    private static final String COMMAND_SHORT_SHOW = "sh";
    private static final String COMMAND_SHORT_EXIT = "exit";

    private static final String KEYWORD_DEADLINE = "due";
    private static final String KEYWORD_EVENT = "from";

    private static final String WHITESPACE = " ";

    // ================================================================================
    // CommandStringType
    // ================================================================================

    private static enum CommandStringType {
        NO_DATE, DEADLINE_DATE, EVENT_DATE
    }

    // ================================================================================
    // Parser methods
    // ================================================================================

    public static Command parse(String userInput) {
        logger.log(Level.FINE, DEBUG_PARSING_COMMAND + userInput);

        String userCommand = userInput.trim().replaceAll("\\s+", " "); // Trim whitespace
        CommandStringType commandInputType = getCommandStringType(userCommand);
        List<Date> dateArray = getDates(userCommand, commandInputType);
        userCommand = removeDatesFromUserCommand(userCommand, commandInputType);
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

                String[] argument = userCommand.split(WHITESPACE, 2);
                String description = argument[1];

                if (description.isEmpty()) {
                    // Display a helpful message (no description)
                    return new Command(CommandType.INVALID).addDescription(MESSAGE_INVALID_NO_DESCRIPTION);
                }

                Command command;

                if (commandInputType.equals(CommandStringType.DEADLINE_DATE)) {
                    command = new Command(CommandType.ADD_DEADLINE).addDate(getStartDate(dateArray));
                } else if (commandInputType.equals(CommandStringType.NO_DATE)) {
                    command = new Command(CommandType.ADD_DREAM);
                } else {
                    command = new Command(CommandType.ADD_EVENT).addStartDate(getStartDate(dateArray))
                            .addEndDate(getEndDate(dateArray));
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

                String[] argument = userCommand.split(WHITESPACE, 3);
                int lineNumber = 0;

                try {
                    lineNumber = Integer.parseInt(argument[1]);
                } catch (NumberFormatException e) { // Not a line number
                    // Treat "edit something" as an add command
                    // Inject add to the front of command and recurse
                    return Parser.parse(putAddInFront(userInput));
                }

                if (argument.length <= 2 && commandInputType == CommandStringType.NO_DATE) { // Too few arguments
                    // Treat "edit 1" as an invalid command
                    // Display a helpful message (no description or date(s) given)
                    return new Command(CommandType.INVALID).addDescription(MESSAGE_INVALID_EDIT_NO_NEW_DATA);
                }

                String description = "";
                if (argument.length > 2) {
                    description = argument[2];
                }

                Command command = new Command(CommandType.EDIT).addLineNumber(lineNumber);

                if (commandInputType.equals(CommandStringType.DEADLINE_DATE)) {
                    command.addDate(getStartDate(dateArray));
                } else if (commandInputType.equals(CommandStringType.EVENT_DATE)) {
                    command.addStartDate(getStartDate(dateArray)).addEndDate(getEndDate(dateArray));
                }

                if (!description.isEmpty()) {
                    command.addDescription(description);
                }

                return command;
            }

            case COMMAND_DELETE:
            case COMMAND_SHORT_DELETE: {
                if (userCommand.equalsIgnoreCase(firstWord)) { // No arguments
                    // Treat "delete" as an invalid command
                    // Display a helpful message (no line number given)
                    return new Command(CommandType.INVALID).addDescription(MESSAGE_INVALID_LINE_NUMBER);
                }

                String argument = userCommand.substring(firstWord.length() + 1);
                int lineNumber = 0;

                try {
                    lineNumber = Integer.parseInt(argument);
                } catch (NumberFormatException e) { // Not a line number
                    // Treat "delete something" is an add command
                    // Inject add to the front of command and recurse
                    return Parser.parse(putAddInFront(userInput));
                }

                return new Command(CommandType.DELETE).addLineNumber(lineNumber);
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

                String[] argument = userCommand.split(WHITESPACE, 2);
                int lineNumber = 0;

                try {
                    lineNumber = Integer.parseInt(argument[1]);
                } catch (NumberFormatException e) { // Not a line number
                    // Treat "done something" as an add command
                    // Inject add to the front of command and recurse
                    return Parser.parse(putAddInFront(userInput));
                }

                return new Command(CommandType.DONE).addLineNumber(lineNumber);
            }

            case COMMAND_SEARCH:
            case COMMAND_SHORT_SEARCH: {
                if (userCommand.equalsIgnoreCase(firstWord)) { // No arguments
                    // Treat "search" as an invalid command
                    // Display a helpful message (no description)
                    return new Command(CommandType.INVALID).addDescription(MESSAGE_INVALID_NO_DESCRIPTION);
                }

                String[] argument = userCommand.split(WHITESPACE, 2);
                String searchDescription = argument[1];

                Command command = new Command(CommandType.SEARCH);
                if (commandInputType.equals(CommandStringType.DEADLINE_DATE)) {
                    command.addDate(DateUtils.truncate(getStartDate(dateArray), Calendar.DATE));
                } else if (commandInputType.equals(CommandStringType.EVENT_DATE)) {
                    Date startDate = DateUtils.truncate(getStartDate(dateArray), Calendar.DATE);
                    Date endDate = DateUtils.truncate(getEndDate(dateArray), Calendar.DATE);
                    command.addStartDate(startDate).addEndDate(endDate);
                }

                if (!searchDescription.isEmpty()) {
                    command.addDescription(searchDescription);
                }

                return command;
            }

            case COMMAND_SHOW:
            case COMMAND_SHORT_SHOW: {
                if (userCommand.equalsIgnoreCase(firstWord)) {
                    return new Command(CommandType.SHOW_OUTSTANDING);
                }

                String argument = userCommand.substring(firstWord.length() + 1);

                if (argument.equals("done")) {
                    return new Command(CommandType.SHOW_DONE);

                } else if (argument.equals("all")) {
                    return new Command(CommandType.SHOW_ALL);

                } else {
                    // Treat "show something" as an add command
                    // Inject add to the front of command and recurse
                    return Parser.parse(putAddInFront(userInput));
                }
            }

            case COMMAND_HELP: {
                if (!userCommand.equalsIgnoreCase(firstWord)) { // Extra arguments
                    // Treat "help something" as an add command
                    // Inject add to the front of command and recurse
                    return Parser.parse(putAddInFront(userInput));
                }

                return new Command(CommandType.HELP);
            }

            case COMMAND_SET_PATH: {
                if (commandInputType.equals(CommandStringType.DEADLINE_DATE)
                        || commandInputType.equals(CommandStringType.EVENT_DATE)
                        || userCommand.split(WHITESPACE).length > 2) {
                    // Have dates or have more than three words
                    // Inject add to the front of the command and recurse
                    return Parser.parse(putAddInFront(userInput));
                }

                if (userCommand.equalsIgnoreCase(firstWord)) { // No arguments
                    // Treat "set" as an invalid command
                    // Display a helpful message (no path)
                    return new Command(CommandType.INVALID).addDescription(MESSAGE_INVALID_NO_PATH);
                }

                String[] argument = userCommand.split(WHITESPACE, 2);
                String pathDescription = argument[1];

                return new Command(CommandType.SET_PATH).addDescription(pathDescription);
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

    private static CommandStringType getCommandStringType(String userCommand) {
        int indexDue = userCommand.lastIndexOf(KEYWORD_DEADLINE);
        int indexFrom = userCommand.lastIndexOf(KEYWORD_EVENT);
        if (indexDue > indexFrom) {
            String dueSubString = userCommand.substring(indexDue, userCommand.length());
            Date dueDate = getDate(dueSubString);
            if (dueDate == null) {
                return CommandStringType.NO_DATE;
            } else {
                return CommandStringType.DEADLINE_DATE;
            }
        } else if (indexFrom > indexDue) {
            String fromSubString = userCommand.substring(indexFrom, userCommand.length());
            List<DateGroup> dateGroups = dateParser.parse(fromSubString);
            if (hasDates(dateGroups) && isEventDate(dateGroups)) {
                return CommandStringType.EVENT_DATE;
            } else {
                return CommandStringType.NO_DATE;
            }
        } else {
            return CommandStringType.NO_DATE;
        }
    }

    private static List<Date> getDates(String userCommand, CommandStringType commandInputType) {
        List<Date> dateList = new ArrayList<Date>();

        if (commandInputType.equals(CommandStringType.NO_DATE)) {
            return null;
        } else if (commandInputType.equals(CommandStringType.DEADLINE_DATE)) {
            String[] arguments = userCommand.split(KEYWORD_DEADLINE);
            List<DateGroup> dateGroups = dateParser.parse(arguments[arguments.length - 1]);
            dateList.add(dateGroups.get(0).getDates().get(0));
        } else {
            String[] arguments = userCommand.split(KEYWORD_EVENT);
            String date = arguments[arguments.length - 1];
            String[] dateArguments = date.split(" to ", 2);

            List<DateGroup> dateGroups = dateParser.parse(dateArguments[0]);
            dateList.add(dateGroups.get(0).getDates().get(0));
            dateGroups = dateParser.parse(dateArguments[1]);
            dateList.add(dateGroups.get(0).getDates().get(0));
        }
        return dateList;
    }

    private static String removeDatesFromUserCommand(String userCommand, CommandStringType commandInputType) {
        String keyword = null;
        if (commandInputType.equals(CommandStringType.NO_DATE)) {
            return userCommand;
        } else if (commandInputType.equals(CommandStringType.DEADLINE_DATE)) {
            keyword = KEYWORD_DEADLINE;
        } else {
            keyword = KEYWORD_EVENT;
        }

        int endIndex = userCommand.lastIndexOf(keyword);
        if (endIndex == 0) {
            return null;
        } else {
            return userCommand.substring(0, endIndex);
            // NOT endIndex - 1; we need the trailing space! See long comment above
        }
    }

    private static Date getDate(String userCommand) {
        String[] arguments = userCommand.split(KEYWORD_DEADLINE);
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

    private static boolean isCommandEmpty(String userCommand) {
        return userCommand == null || userCommand.isEmpty();
    }

    private static String getFirstWord(String userCommand) {
        return userCommand.split(WHITESPACE)[0];
    }

    private static String putAddInFront(String userInput) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(COMMAND_ADD);
        stringBuilder.append(WHITESPACE);
        stringBuilder.append(userInput);
        return stringBuilder.toString();
    }

    private static boolean hasDates(List<DateGroup> groups) {
        return !groups.isEmpty();
    }

    private static boolean isEventDate(List<DateGroup> groups) {
        return groups.get(0).getDates().size() == 2;
    }

    private static Date getStartDate(List<Date> dateArray) {
        return dateArray.get(0);
    }

    private static Date getEndDate(List<Date> dateArray) {
        return dateArray.get(1);
    }
}
