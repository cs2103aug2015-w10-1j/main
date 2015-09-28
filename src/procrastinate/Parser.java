package procrastinate;

public class Parser {

    private static final String DEBUG_PARSING_COMMAND = "Parsing command: ";

    public static Command parse(String userCommand) {
        Utilities.printDebug(DEBUG_PARSING_COMMAND + userCommand);

        if (userCommand.equalsIgnoreCase("exit")) {
            return new Command(Command.Type.EXIT, null);
        } else {
            String params[] = {userCommand};
            return new Command(Command.Type.ADD_DREAM, params);
        }
    }

}
