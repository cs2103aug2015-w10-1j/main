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

    private static final String MESSAGE_INVALID_LINE_NUMBER = "Please specify a valid line number";
    private static final String MESSAGE_INVALID_EDIT_NO_DESCRIPTION = "Please specify the new description";

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
        List<DateGroup> groups = dateParser.parse(userCommand);
        String firstWord = getFirstWord(userCommand).toLowerCase(); // Case insensitive
        Date inputDate = null;
        if(hasDates(groups)){
            inputDate = getDate(groups);
            userCommand = splitDatesFromUserCommand(groups, userCommand);
        }

        switch (firstWord) {

            case COMMAND_EDIT:
            case COMMAND_SHORT_EDIT:
                if (!userCommand.equalsIgnoreCase(firstWord)){
                    try {
                        String[] argument = userCommand.split(" ", 3);
                        int lineNumber = Integer.parseInt(argument[1]);
                        String description = argument[2];
                        return new Command(CommandType.EDIT).addDescription(description).addLineNumber(lineNumber);

                    } catch (NumberFormatException e) { // So "edit something" is a dream
                        if(hasDates(groups)){
                            return new Command(CommandType.ADD_DEADLINE).addDescription(userCommand).addDate(inputDate);
                        } else {
                             return new Command(CommandType.ADD_DREAM).addDescription(userCommand);
                        }
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

                } catch (NumberFormatException e) { // So "delete something" is a dream
                    if(hasDates(groups)){
                        return new Command(CommandType.ADD_DEADLINE).addDescription(userCommand).addDate(inputDate);
                    } else {
                         return new Command(CommandType.ADD_DREAM).addDescription(userCommand);
                    }
                } catch (Exception e) { // So "delete" is invalid (no line number given)
                    return new Command(CommandType.INVALID).addDescription(MESSAGE_INVALID_LINE_NUMBER);
                }

            case COMMAND_UNDO:
            case COMMAND_SHORT_UNDO:
                if (userCommand.equalsIgnoreCase(firstWord)) { // So "undo something" is a dream
                    return new Command(CommandType.UNDO);
                } else {
                    if(hasDates(groups)){
                        return new Command(CommandType.ADD_DEADLINE).addDescription(userCommand).addDate(inputDate);
                    } else {
                         return new Command(CommandType.ADD_DREAM).addDescription(userCommand);
                    }
                }

            case COMMAND_DONE:
            case COMMAND_SHORT_DONE:
                if (!userCommand.equalsIgnoreCase(firstWord)){
                    try {
                        String[] argument = userCommand.split(" ", 2);
                        int lineNumber = Integer.parseInt(argument[1]);
                        return new Command(CommandType.DONE).addLineNumber(lineNumber);

                    } catch (NumberFormatException e) { // So "done something" is a dream
                        if(hasDates(groups)){
                            return new Command(CommandType.ADD_DEADLINE).addDescription(userCommand).addDate(inputDate);
                        } else {
                             return new Command(CommandType.ADD_DREAM).addDescription(userCommand);
                        }
                    }
                } else { // So "done" is invalid (no line number given)
                    return new Command(CommandType.INVALID).addDescription(MESSAGE_INVALID_LINE_NUMBER);
                }

            case COMMAND_EXIT:
            case COMMAND_SHORT_EXIT:
                if (userCommand.equalsIgnoreCase(firstWord)) { // So "procrastinate something" is a dream
                    return new Command(CommandType.EXIT);
                } else {
                    if(hasDates(groups)){
                        return new Command(CommandType.ADD_DEADLINE).addDescription(userCommand).addDate(inputDate);
                    } else {
                         return new Command(CommandType.ADD_DREAM).addDescription(userCommand);
                    }
                }

            default:
                if(hasDates(groups)){
                    return new Command(CommandType.ADD_DEADLINE).addDescription(userCommand).addDate(inputDate);
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

    private static boolean hasDates(List<DateGroup> groups){
        return !groups.isEmpty();
    }

    private static Date getDate(List<DateGroup> groups){
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
