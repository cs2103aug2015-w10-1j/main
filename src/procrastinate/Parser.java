package procrastinate;

import java.util.logging.Level;
import java.util.logging.Logger;

import procrastinate.Command.CommandType;

public class Parser {

    private static final Logger logger = Logger.getLogger(Parser.class.getName());

    private static final String DEBUG_PARSING_COMMAND = "Parsing command: ";
    private static final String COMMAND_EXIT = "exit";
    private static final String COMMAND_PROCRASTINATE = "procrastinate";

    public static Command parse(String userCommand) {
        logger.log(Level.INFO, DEBUG_PARSING_COMMAND + userCommand);

        userCommand = userCommand.trim(); // Trim leading and trailing whitespace
        String firstWord = getFirstWord(userCommand).toLowerCase(); // Case insensitive

        switch (firstWord) {

            case COMMAND_EXIT:
            case COMMAND_PROCRASTINATE:
                return new Command(CommandType.EXIT);

            default:
                return new Command(CommandType.ADD_DREAM).addDescription(userCommand);

        }

    }

    private static String getFirstWord(String userCommand) {
        return userCommand.split(" ")[0];
    }

}
