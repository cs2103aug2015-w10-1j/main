package procrastinate;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import procrastinate.task.Dream;
import procrastinate.task.Task;
import procrastinate.task.TaskEngine;
import procrastinate.ui.UI;

public class Logic {

    private static final Logger logger = Logger.getLogger(Logic.class.getName());

    // ================================================================================
    // Message strings
    // ================================================================================

    private static final String DEBUG_LOGIC_INIT = "Logic initialised.";

    private static final String FEEDBACK_ADD_DREAM = "Added dream: ";
    private static final String FEEDBACK_EDIT_DREAM = "Edited #%1$s: %2$s";
    private static final String FEEDBACK_DELETED = "Deleted %1$s: %2$s";
    private static final String FEEDBACK_INVALID_LINE_NUMBER = "Invalid line number: ";
    private static final String FEEDBACK_UNDONE = "Undid last operation";
    private static final String FEEDBACK_NOTHING_TO_UNDO = "Nothing to undo";

    private static final String PREVIEW_EXIT = "Goodbye!";

    private static final String ERROR_UNIMPLEMENTED_COMMAND = "Error: command not implemented yet";

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
        ui.updateTaskList(currentTaskList);
    }

    // ================================================================================
    // Logic methods
    // ================================================================================

    public String executeCommand(Command command) {
        return executeCommand(command, true);
    }

    public String previewCommand(Command command) {
        return executeCommand(command, false);
    }

    private String executeCommand(Command command, boolean execute) {

        switch (command.getType()) {

            case ADD_DREAM: {
                String description = command.getDescription();

                if (execute) {
                    taskEngine.add(new Dream(description));
                    updateUiTaskList();
                }

                return FEEDBACK_ADD_DREAM + description;
            }

            case EDIT: {
                int lineNumber = command.getLineNumber();

                if (lineNumber < 1 || lineNumber > currentTaskList.size()) {
                    return FEEDBACK_INVALID_LINE_NUMBER + lineNumber;
                }

                Task task = getTaskFromLineNumber(lineNumber);

                String newDescription = command.getDescription();
                Task newTask = new Dream(newDescription);

                if (execute) {
                    taskEngine.edit(task.getId(), newTask);
                    updateUiTaskList();
                }

                return String.format(FEEDBACK_EDIT_DREAM, lineNumber, newDescription);
            }

            case DELETE: {
                int lineNumber = command.getLineNumber();

                if (lineNumber < 1 || lineNumber > currentTaskList.size()) {
                    return FEEDBACK_INVALID_LINE_NUMBER + lineNumber;
                }

                Task task = getTaskFromLineNumber(lineNumber);
                String type = task.getTypeString();
                String description = task.getDescription();

                if (execute) {
                    taskEngine.delete(task.getId());
                    updateUiTaskList();
                }

                return String.format(FEEDBACK_DELETED, type, description);
            }

            case UNDO: {
                if (taskEngine.hasPreviousOperation()) {
                    if (execute) {
                        taskEngine.undo();
                        updateUiTaskList();
                    }
                    return FEEDBACK_UNDONE;
                } else {
                    return FEEDBACK_NOTHING_TO_UNDO;
                }
            }

            case INVALID: {
                return command.getDescription();
            }

            case EXIT: {
                if (execute) {
                    System.exit(0);
                }

                return PREVIEW_EXIT;
            }

            default: {
                throw new Error(ERROR_UNIMPLEMENTED_COMMAND);
            }

        }

    }

    // ================================================================================
    // Init methods
    // ================================================================================

    private void initTaskEngine() {
        taskEngine = new TaskEngine();
    }

    private void initCurrentTaskList() {
        currentTaskList = taskEngine.getOutstandingTasks();
    }

    // ================================================================================
    // Utility methods
    // ================================================================================

    private Task getTaskFromLineNumber(int lineNumber) {
        return currentTaskList.get(lineNumber - 1);
    }

    private void updateUiTaskList() {
        currentTaskList = taskEngine.getOutstandingTasks();
        ui.updateTaskList(currentTaskList);
    }

}
