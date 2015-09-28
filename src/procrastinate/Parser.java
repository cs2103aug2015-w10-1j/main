package procrastinate;

public class Parser {

    private static final String DEBUG_PARSING_COMMAND = "Parsing command: ";
    private static final String COMMAND_EXIT = "exit";
    private static final String COMMAND_PROCRASTINATE = "procrastinate";

    public static Command parse(String userCommand) {
        Utilities.printDebug(DEBUG_PARSING_COMMAND + userCommand);

        userCommand = userCommand.trim(); // Trim leading and trailing whitespace
        String firstWord = getFirstWord(userCommand).toLowerCase(); // Case insensitive

        switch (firstWord) {

            case COMMAND_EXIT:
            case COMMAND_PROCRASTINATE:
                return new Command(Command.Type.EXIT);

            default:
                return new Command(Command.Type.ADD_DREAM).addDescription(userCommand);

        }

    }

    private static String getFirstWord(String userCommand) {
        return userCommand.split(" ")[0];
    }

}
