package procrastinate;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Logic {

    private static final Logger logger = Logger.getLogger(Logic.class.getName());

    // ================================================================================
    // Message strings
    // ================================================================================

    private static final String FEEDBACK_ADD_DREAM = "Adding dream: ";

    private static final String DEBUG_LOGIC_INIT = "Logic initialised.";

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
        logger.log(Level.INFO, DEBUG_LOGIC_INIT);
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
                Task newDream = new Dream(description);
                taskEngine.add(newDream);
                ui.updateTaskList(taskEngine.getOutstandingTasks());
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
