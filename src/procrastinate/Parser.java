package procrastinate;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.joestelmach.natty.DateGroup;

import procrastinate.Command.CommandType;

public class Parser {

    private static final Logger logger = Logger.getLogger(Parser.class.getName());
    private static final com.joestelmach.natty.Parser dateParser = new com.joestelmach.natty.Parser();

    // ================================================================================
    // Message strings
    // ================================================================================

    private static final String DEBUG_PARSING_COMMAND = "Parsing command: ";

    private static final String MESSAGE_INVALID_ADD_NO_DESCRIPTION = "Please specify the description";
    private static final String MESSAGE_INVALID_LINE_NUMBER = "Please specify a valid line number";
    private static final String MESSAGE_INVALID_EDIT_NO_DESCRIPTION = "Please specify the new description";

    private static final String COMMAND_ADD = "add";
    private static final String COMMAND_EDIT = "edit";
    private static final String COMMAND_DELETE = "delete";
    private static final String COMMAND_DONE = "done";
    private static final String COMMAND_UNDO = "undo";
    private static final String COMMAND_EXIT = "procrastinate";

    private static final String COMMAND_SHORT_EDIT = "ed";
    private static final String COMMAND_SHORT_DELETE = "del";
    private static final String COMMAND_SHORT_DONE = "do";
    private static final String COMMAND_SHORT_UNDO = "un";
    private static final String COMMAND_SHORT_EXIT = "exit";

    // ================================================================================
    // Parser methods
    // ================================================================================

    public static Command parse(String userCommand) {
        logger.log(Level.FINE, DEBUG_PARSING_COMMAND + userCommand);

        userCommand = userCommand.trim(); // Trim leading and trailing whitespace
        String userCommandLowerCase = userCommand.toLowerCase(); // Case insensitive

        String firstWord = getFirstWord(userCommand).toLowerCase(); // Case insensitive

        switch (firstWord) {
            case COMMAND_ADD:
                if(!userCommand.equalsIgnoreCase(firstWord)){
                    String[] argument = userCommand.split(" ", 2);
                    String description = argument[1];
                    List<DateGroup> dateGroups = null;
                    Date inputDate = null;
                    if (userCommandLowerCase.contains("due")) {
                        dateGroups = dateParser.parse(description);
                        inputDate = null;
                        if(hasDates(dateGroups)){
                            inputDate = getFirstDate(dateGroups);
                            description = splitDatesFromUserCommand(dateGroups, description);
                            return new Command(CommandType.ADD_DEADLINE).addDescription(description).addDate(inputDate);
                        }
                    }
                    return new Command(CommandType.ADD_DREAM).addDescription(description);
                } else {
                    return new Command(CommandType.INVALID).addDescription(MESSAGE_INVALID_ADD_NO_DESCRIPTION);
                }

            case COMMAND_EDIT:
            case COMMAND_SHORT_EDIT:
                if (!userCommand.equalsIgnoreCase(firstWord)){
                    try {
                        String[] argument = userCommand.split(" ", 3);
                        int lineNumber = Integer.parseInt(argument[1]);
                        String description = argument[2];
                        return new Command(CommandType.EDIT).addDescription(description).addLineNumber(lineNumber);

                    } catch (NumberFormatException e) { // So "edit something" is an add command, inject add to the front of command and recurse
                        String newUserCommand = putAddInCommand(userCommand);
                        return Parser.parse(newUserCommand);
                    } catch (Exception e) { // So "edit 1" is invalid (no description given)
                        return new Command(CommandType.INVALID).addDescription(MESSAGE_INVALID_EDIT_NO_DESCRIPTION);
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
                    String newUserCommand = putAddInCommand(userCommand);
                    return Parser.parse(newUserCommand);
                } catch (Exception e) { // So "delete" is invalid (no line number given)
                    return new Command(CommandType.INVALID).addDescription(MESSAGE_INVALID_LINE_NUMBER);
                }

            case COMMAND_UNDO:
            case COMMAND_SHORT_UNDO:
                if (userCommand.equalsIgnoreCase(firstWord)) { // So "delete something" is an add command, inject add to the front of command and recurse
                    String newUserCommand = putAddInCommand(userCommand);
                    return Parser.parse(newUserCommand);
                } else {
                    return new Command(CommandType.ADD_DREAM).addDescription(userCommand);
                }

            case COMMAND_DONE:
            case COMMAND_SHORT_DONE:
                if (!userCommand.equalsIgnoreCase(firstWord)){
                    try {
                        String[] argument = userCommand.split(" ", 2);
                        int lineNumber = Integer.parseInt(argument[1]);
                        return new Command(CommandType.DONE).addLineNumber(lineNumber);

                    } catch (NumberFormatException e) { // So "done something" is an add command, inject add to the front of command and recurse
                        String newUserCommand = putAddInCommand(userCommand);
                        return Parser.parse(newUserCommand);
                    }
                } else { // So "done" is invalid (no line number given)
                    return new Command(CommandType.INVALID).addDescription(MESSAGE_INVALID_LINE_NUMBER);
                }

            case COMMAND_EXIT:
            case COMMAND_SHORT_EXIT:
                if (userCommand.equalsIgnoreCase(firstWord)) { // So "procrastinate something" is an add command, inject add to the front of command and recurse
                    return new Command(CommandType.EXIT);
                } else {
                    String newUserCommand = putAddInCommand(userCommand);
                    return Parser.parse(newUserCommand);
                }

            default:
                List<DateGroup> dateGroups = null;
                Date inputDate = null;
                if (userCommandLowerCase.contains("due")) {
                    dateGroups = dateParser.parse(userCommand);
                    inputDate = null;
                    if(hasDates(dateGroups)){
                        inputDate = getFirstDate(dateGroups);
                        String description = splitDatesFromUserCommand(dateGroups, userCommand);
                        return new Command(CommandType.ADD_DEADLINE).addDescription(description).addDate(inputDate);
                    } else {
                        return new Command(CommandType.ADD_DREAM).addDescription(userCommand);
                    }
                } else {
                    return new Command(CommandType.ADD_DREAM).addDescription(userCommand);
                }
        }

    }

    // ================================================================================
    // Utility methods
    // ================================================================================

    private static String getFirstWord(String userCommand) {
        return userCommand.split(" ")[0];
    }

    private static String putAddInCommand(String description){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(COMMAND_ADD);
        stringBuilder.append(" ");
        stringBuilder.append(description);
        return stringBuilder.toString();
    }

    private static boolean hasDates(List<DateGroup> groups){
        return !groups.isEmpty();
    }

    private static Date getFirstDate(List<DateGroup> groups){
        Date date = groups.get(0).getDates().get(0);
        return date;
    }

    private static String splitDatesFromUserCommand(List<DateGroup> groups, String userCommand){
        int endIndex = groups.get(0).getPosition();
        String removedDateCommand = userCommand.substring(0, endIndex - 1);
        String[] temp = removedDateCommand.split("due");
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < temp.length; i ++){
            stringBuilder.append(temp[i]);
        }
        return stringBuilder.toString();
    }

}
