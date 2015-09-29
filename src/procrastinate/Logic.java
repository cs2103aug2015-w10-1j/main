package procrastinate;

public class Logic {

    // ================================================================================
    // Message strings
    // ================================================================================

    private static final String FEEDBACK_ADD_DREAM = "Adding dream: ";

    private static final String ERROR_PARSER_UNKNOWN_COMMAND = "Error with parser: unknown command type returned";

    // ================================================================================
    // Class variables
    // ================================================================================

    private TaskEngine taskEngine;
    private UI ui;

    // ================================================================================
    // Singleton pattern
    // ================================================================================

    private static Logic logic;

    private Logic() {
        initTaskEngine();
    }

    public static Logic getInstance() {
        if (logic == null) {
            logic = new Logic();
        }
        return logic;
    }

    // UI handle
    public void setUi(UI ui) {
        this.ui = ui;
    }

    // ================================================================================
    // Public logic methods
    // ================================================================================

    public String executeCommand(String userCommand) {

        Command command = Parser.parse(userCommand);

        switch (command.getType()) {

            case ADD_DREAM:
                String description = command.getDescription();
                Task newDream = new Task(description);
                taskEngine.add(newDream);
                ui.addDreamToTaskList(newDream);
                return FEEDBACK_ADD_DREAM + description;

            case EXIT:
                System.exit(0);

            default:
                throw new Error(ERROR_PARSER_UNKNOWN_COMMAND);

        }

    }

    // ================================================================================
    // Init methods
    // ================================================================================

    private void initTaskEngine() {
        taskEngine = new TaskEngine();
    }

}
