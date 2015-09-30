package procrastinate;

import java.util.logging.Level;
import java.util.logging.Logger;

import procrastinate.Command.CommandType;

public class Parser {

    private static final Logger logger = Logger.getLogger(Parser.class.getName());

    private static final String DEBUG_PARSING_COMMAND = "Parsing command: ";

    private static final String COMMAND_DELETE = "delete";
    private static final String COMMAND_UNDO = "undo";
    private static final String COMMAND_EXIT = "exit";
    private static final String COMMAND_PROCRASTINATE = "procrastinate";

    private static final String COMMAND_SHORT_DELETE = "del";

    private static final String MESSAGE_INVALID_DELETE = "Please specify a valid line number";

    public static Command parse(String userCommand) {
        logger.log(Level.INFO, DEBUG_PARSING_COMMAND + userCommand);

        userCommand = userCommand.trim(); // Trim leading and trailing whitespace
        String firstWord = getFirstWord(userCommand).toLowerCase(); // Case insensitive

        switch (firstWord) {

            case COMMAND_EXIT:
            case COMMAND_PROCRASTINATE:
                if (userCommand.equalsIgnoreCase(firstWord)) { // So "procrastinate something" is a dream
                    return new Command(CommandType.EXIT);
                } else {
                    return new Command(CommandType.ADD_DREAM).addDescription(userCommand);
                }

            case COMMAND_DELETE:
            case COMMAND_SHORT_DELETE:
                try {
                    String argument = userCommand.substring(firstWord.length() + 1);
                    int lineNumber = Integer.parseInt(argument);

                    return new Command(CommandType.DELETE).addLineNumber(lineNumber);

                } catch (Exception e) {
                    return new Command(CommandType.INVALID).addDescription(MESSAGE_INVALID_DELETE);
                }

            case COMMAND_UNDO:
                if (userCommand.equalsIgnoreCase(firstWord)) { // So "undo something" is a dream
                    return new Command(CommandType.UNDO);
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
