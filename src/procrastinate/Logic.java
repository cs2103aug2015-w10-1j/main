package procrastinate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Logic {

    private static final Logger logger = Logger.getLogger(Logic.class.getName());

    // ================================================================================
    // Message strings
    // ================================================================================

    private static final String FEEDBACK_ADD_DREAM = "Adding dream: ";
    private static final String FEEDBACK_DELETED = "Deleted task: ";
    private static final String FEEDBACK_INVALID_LINE_NUMBER = "Invalid line number: ";

    private static final String PREVIEW_EXIT = "Goodbye!";

    private static final String DEBUG_LOGIC_INIT = "Logic initialised.";

    private static final String ERROR_PARSER_UNKNOWN_COMMAND = "Error with parser: unknown command type returned";

    // ================================================================================
    // Class variables
    // ================================================================================

    private TaskEngine taskEngine;
    private UI ui;
    private List<Task> currentTaskList;

    // ================================================================================
    // Singleton pattern
    // ================================================================================

    private static Logic logic;

    private Logic() {
        initTaskEngine();
        initCurrentTaskList();
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
                updateUiTaskList();

                return FEEDBACK_ADD_DREAM + description;

            case DELETE:
                int lineNumber = command.getLineNumber();

                if (lineNumber < 1 || lineNumber > currentTaskList.size()) {
                    return FEEDBACK_INVALID_LINE_NUMBER + lineNumber;
                }

                Task task = getTaskFromLineNumber(lineNumber);
                UUID taskId = task.getId();

                taskEngine.delete(taskId);
                updateUiTaskList();

                return FEEDBACK_DELETED + task.getDescription();

            case INVALID:
                return command.getDescription();

            case EXIT:
                System.exit(0);

            default:
                throw new Error(ERROR_PARSER_UNKNOWN_COMMAND);

        }

    }

    public String previewCommand(String userCommand) {

        Command command = Parser.parse(userCommand);

        switch (command.getType()) {

            case ADD_DREAM:
                String description = command.getDescription();
                return FEEDBACK_ADD_DREAM + description;

            case DELETE:
                int lineNumber = command.getLineNumber();

                if (lineNumber < 1 || lineNumber > currentTaskList.size()) {
                    return FEEDBACK_INVALID_LINE_NUMBER + lineNumber;
                }

                Task task = getTaskFromLineNumber(lineNumber);

                return FEEDBACK_DELETED + task.getDescription();

            case INVALID:
                return command.getDescription();

            case EXIT:
                return PREVIEW_EXIT;

            default:
                throw new Error(ERROR_PARSER_UNKNOWN_COMMAND);

        }

    }

    private void updateUiTaskList() {
        currentTaskList = taskEngine.getOutstandingTasks();
        ui.updateTaskList(currentTaskList);
    }

    // ================================================================================
    // Init methods
    // ================================================================================

    private void initTaskEngine() {
        taskEngine = new TaskEngine();
    }

    private void initCurrentTaskList() {
        currentTaskList = new ArrayList<Task>();
    }

    // ================================================================================
    // Utility methods
    // ================================================================================

    private Task getTaskFromLineNumber(int lineNumber) {
        return currentTaskList.get(lineNumber - 1);
    }

}
