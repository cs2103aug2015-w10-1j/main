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
    private static final String MESSAGE_INVALID_EDIT_NO_DESCRIPTION = "Please specify the new description";

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
    private static final String COMMAND_SHORT_EXIT = "exit";

    // ================================================================================
    // Parser methods
    // ================================================================================

    public static Command parse(String userInput) {
        logger.log(Level.FINE, DEBUG_PARSING_COMMAND + userInput);

        String userCommand = userInput.trim(); // Trim leading and trailing whitespace
        Date inputDate = getDate(userCommand);
        userCommand = splitDatesFromUserCommand(userCommand, inputDate);

        if(isCommandEmpty(userCommand)) {
            return new Command(CommandType.INVALID).addDescription(MESSAGE_INVALID_NO_DESCRIPTION);
        }

        String firstWord = getFirstWord(userCommand).toLowerCase(); // Case insensitive

        switch (firstWord) {
            case COMMAND_ADD:
                if(!userCommand.equalsIgnoreCase(firstWord)) {
                    String[] argument = userCommand.split(" ", 2);
                    String description = argument[1];
                    if (inputDate != null) {
                        return new Command(CommandType.ADD_DEADLINE).addDescription(description).addDate(inputDate);
                    }
                    return new Command(CommandType.ADD_DREAM).addDescription(description);
                } else {
                    return new Command(CommandType.INVALID).addDescription(MESSAGE_INVALID_NO_DESCRIPTION);
                }

            case COMMAND_EDIT:
            case COMMAND_SHORT_EDIT:
                if (!userCommand.equalsIgnoreCase(firstWord)) {
                    int lineNumber = 0;
                    try {
                        String[] argument = userCommand.split(" ", 3);
                        lineNumber = Integer.parseInt(argument[1]);
                        String description = argument[2];
                        System.out.println('"' + description + '"');
                        Command command = new Command(CommandType.EDIT).addLineNumber(lineNumber);
                        if (inputDate != null) {
                            command.addDate(inputDate);
                        }
                        if (!description.isEmpty()) {
                            command.addDescription(description);
                        }
                        System.out.println("normal edit");
                        return command;

                    } catch (NumberFormatException e) { // So "edit something" is an add command, inject add to the front of command and recurse
                        String newUserCommand = putAddInCommand(userInput);
                        return Parser.parse(newUserCommand);
                    } catch (Exception e) { // So "edit 1" is invalid (no description given)
                        if (inputDate != null) {
                            System.out.println("? edit");
                            return new Command(CommandType.EDIT).addLineNumber(lineNumber).addDate(inputDate);
                        } else {
                            return new Command(CommandType.INVALID).addDescription(MESSAGE_INVALID_EDIT_NO_DESCRIPTION);
                        }
                    }
                } else { // So "edit" is invalid (no line number given)
                    return new Command(CommandType.INVALID).addDescription(MESSAGE_INVALID_LINE_NUMBER);
                }

            case COMMAND_DELETE:
            case COMMAND_SHORT_DELETE:
                try {
                    String argument = userCommand.substring(firstWord.length() + 1);
                    int lineNumber = Integer.parseInt(argument);

                    return new Command(CommandType.DELETE).addLineNumber(lineNumber);

                } catch (NumberFormatException e) { // So "delete something" is an add command, inject add to the front of command and recurse
                    String newUserCommand = putAddInCommand(userInput);
                    return Parser.parse(newUserCommand);
                } catch (Exception e) { // So "delete" is invalid (no line number given)
                    return new Command(CommandType.INVALID).addDescription(MESSAGE_INVALID_LINE_NUMBER);
                }

            case COMMAND_UNDO:
            case COMMAND_SHORT_UNDO:
                if (userCommand.equalsIgnoreCase(firstWord)) {
                    return new Command(CommandType.UNDO);
                } else { // So "undo something" is an add command, inject add to the front of command and recurse
                    String newUserCommand = putAddInCommand(userInput);
                    return Parser.parse(newUserCommand);
                }

            case COMMAND_DONE:
            case COMMAND_SHORT_DONE:
                if (!userCommand.equalsIgnoreCase(firstWord)) {
                    try {
                        String[] argument = userCommand.split(" ", 2);
                        int lineNumber = Integer.parseInt(argument[1]);
                        return new Command(CommandType.DONE).addLineNumber(lineNumber);

                    } catch (NumberFormatException e) { // So "done something" is an add command, inject add to the front of command and recurse
                        String newUserCommand = putAddInCommand(userInput);
                        return Parser.parse(newUserCommand);
                    }
                } else { // So "done" is invalid (no line number given)
                    return new Command(CommandType.INVALID).addDescription(MESSAGE_INVALID_LINE_NUMBER);
                }

            case COMMAND_SEARCH:
                if(!userCommand.equalsIgnoreCase(firstWord)) {
                    String[] argument = userCommand.split(" ", 2);
                    String searchDescription = argument[1];
                    return new Command(CommandType.SEARCH).addDescription(searchDescription).addDate(inputDate);
                } else{
                    if(inputDate == null) { // So "search" is invalid
                        return new Command(CommandType.INVALID).addDescription(MESSAGE_INVALID_NO_DESCRIPTION);
                    } else {
                        return new Command(CommandType.SEARCH).addDate(inputDate);
                    }
                }

            case COMMAND_EXIT:
            case COMMAND_SHORT_EXIT:
                if (userCommand.equalsIgnoreCase(firstWord)) { // So "procrastinate something" is an add command, inject add to the front of command and recurse
                    return new Command(CommandType.EXIT);
                } else {
                    String newUserCommand = putAddInCommand(userInput);
                    return Parser.parse(newUserCommand);
                }

            default:
                if(inputDate != null) {
                    return new Command(CommandType.ADD_DEADLINE).addDescription(userCommand).addDate(inputDate);
                } else {
                    return new Command(CommandType.ADD_DREAM).addDescription(userCommand);
                }
        }

    }

    // ================================================================================
    // Utility methods
    // ================================================================================

    private static Date getDate(String userCommand) {
        String[] arguments = userCommand.split("due");
        if(arguments.length <= 1) {
            return null;
        }

        List<DateGroup> dateGroups = dateParser.parse(arguments[arguments.length - 1]);
        if(hasDates(dateGroups)) {
            return dateGroups.get(0).getDates().get(0);
        } else {
            return null;
        }
    }

    private static String splitDatesFromUserCommand(String userCommand, Date inputDate) {
        if(inputDate == null) {
            if(userCommand.equals("due")) {
                return null;
            } else {
                return userCommand;
            }
        } else {
            String[] arguments = userCommand.split("due");
            StringBuilder stringBuilder = new StringBuilder();
            for(int i = 0; i < arguments.length - 1; i ++) {
                stringBuilder.append(arguments[i]);
                if(i != arguments.length - 2) {
                    stringBuilder.append("due");
                }
            }
            return stringBuilder.toString();
        }
    }

    private static boolean isCommandEmpty(String userCommand) {
        return userCommand == null || userCommand.equals("");
    }

    private static String getFirstWord(String userCommand) {
        return userCommand.split(" ")[0];
    }

    private static String putAddInCommand(String description) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(COMMAND_ADD);
        stringBuilder.append(" ");
        stringBuilder.append(description);
        return stringBuilder.toString();
    }

    private static boolean hasDates(List<DateGroup> groups) {
        return !groups.isEmpty();
    }
}
