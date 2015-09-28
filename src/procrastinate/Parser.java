package procrastinate;

public class Parser {

    private static final String DEBUG_PARSING_COMMAND = "Parsing command: ";
    private static final String COMMAND_EXIT = "exit";

    public static Command parse(String userCommand) {
        Utilities.printDebug(DEBUG_PARSING_COMMAND + userCommand);

        if (userCommand.equalsIgnoreCase(COMMAND_EXIT)) {
            return new Command(Command.Type.EXIT);
        } else {
            return new Command(Command.Type.ADD_DREAM).addDescription(userCommand);
        }
    }

}
