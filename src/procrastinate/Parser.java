package procrastinate;

import java.util.logging.Level;
import java.util.logging.Logger;

import procrastinate.Command.CommandType;

public class Parser {

    private static final Logger logger = Logger.getLogger(Parser.class.getName());

    private static final String DEBUG_PARSING_COMMAND = "Parsing command: ";

    private static final String COMMAND_EDIT = "edit";
    private static final String COMMAND_DELETE = "delete";
    private static final String COMMAND_UNDO = "undo";
    private static final String COMMAND_EXIT = "procrastinate";

    private static final String COMMAND_SHORT_EDIT = "ed";
    private static final String COMMAND_SHORT_DELETE = "del";
    private static final String COMMAND_SHORT_UNDO = "un";
    private static final String COMMAND_SHORT_EXIT = "exit";

    private static final String MESSAGE_INVALID_LINE_NUMBER = "Please specify a valid line number";
    private static final String MESSAGE_INVALID_EDIT_NO_DESCRIPTION = "Please specify the new description";

    public static Command parse(String userCommand) {
        logger.log(Level.INFO, DEBUG_PARSING_COMMAND + userCommand);

        userCommand = userCommand.trim(); // Trim leading and trailing whitespace
        String firstWord = getFirstWord(userCommand).toLowerCase(); // Case insensitive

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
                        return new Command(CommandType.ADD_DREAM).addDescription(userCommand);
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
                    return new Command(CommandType.ADD_DREAM).addDescription(userCommand);
                } catch (Exception e) { // So "delete" is invalid (no line number given)
                    return new Command(CommandType.INVALID).addDescription(MESSAGE_INVALID_LINE_NUMBER);
                }

            case COMMAND_UNDO:
            case COMMAND_SHORT_UNDO:
                if (userCommand.equalsIgnoreCase(firstWord)) { // So "undo something" is a dream
                    return new Command(CommandType.UNDO);
                } else {
                    return new Command(CommandType.ADD_DREAM).addDescription(userCommand);
                }

            case COMMAND_SHORT_EXIT:
            case COMMAND_EXIT:
                if (userCommand.equalsIgnoreCase(firstWord)) { // So "procrastinate something" is a dream
                    return new Command(CommandType.EXIT);
                } else {
                    return new Command(CommandType.ADD_DREAM).addDescription(userCommand);
                }

            default:
                return new Command(CommandType.ADD_DREAM).addDescription(userCommand);

        }

    }

    private static String getFirstWord(String userCommand) {
        return userCommand.split(" ")[0];
    }

}
