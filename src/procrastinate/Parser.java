package procrastinate;

import java.util.logging.Level;
import java.util.logging.Logger;

import procrastinate.Command.CommandType;

public class Parser {

    private static final Logger logger = Logger.getLogger(Parser.class.getName());

    private static final String DEBUG_PARSING_COMMAND = "Parsing command: ";

    private static final String COMMAND_DELETE = "delete";
    private static final String COMMAND_EXIT = "exit";
    private static final String COMMAND_PROCRASTINATE = "procrastinate";

    private static final String MESSAGE_INVALID_DELETE = "Please specify a valid line number";

    public static Command parse(String userCommand) {
        logger.log(Level.INFO, DEBUG_PARSING_COMMAND + userCommand);

        userCommand = userCommand.trim(); // Trim leading and trailing whitespace
        String firstWord = getFirstWord(userCommand).toLowerCase(); // Case insensitive

        switch (firstWord) {

            case COMMAND_EXIT:
            case COMMAND_PROCRASTINATE:
                return new Command(CommandType.EXIT);

            case COMMAND_DELETE:
                try {
                    String argument = userCommand.substring(COMMAND_DELETE.length() + 1);
                    int lineNumber = Integer.parseInt(argument);

                    return new Command(CommandType.DELETE).addLineNumber(lineNumber);

                } catch (Exception e) {
                    return new Command(CommandType.INVALID).addDescription(MESSAGE_INVALID_DELETE);
                }

            default:
                return new Command(CommandType.ADD_DREAM).addDescription(userCommand);

        }

    }

    private static String getFirstWord(String userCommand) {
        return userCommand.split(" ")[0];
    }

}
