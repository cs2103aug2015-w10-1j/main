package procrastinate;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import procrastinate.task.*;
import procrastinate.ui.UI;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Logic {

    private static final Logger logger = Logger.getLogger(Logic.class.getName());

    // ================================================================================
    // Message strings
    // ================================================================================

    private static final String DEBUG_LOGIC_INIT = "Logic initialised.";

    private static final String FEEDBACK_ADD_DREAM = "Added dream: ";
    private static final String FEEDBACK_ADD_DEADLINE = "Added deadline: %1$s due: %2$s";
    private static final String FEEDBACK_ADD_EVENT = "Added event: %1$s from: %2$s to: %3$s";
    private static final String FEEDBACK_EDIT_DREAM = "Edited #%1$s: %2$s";
    private static final String FEEDBACK_DELETED = "Deleted %1$s: %2$s";
    private static final String FEEDBACK_DONE = "Done %1$s: %2$s";
    private static final String FEEDBACK_INVALID_LINE_NUMBER = "Invalid line number: ";
    private static final String FEEDBACK_UNDONE = "Undid last operation";
    private static final String FEEDBACK_NOTHING_TO_UNDO = "Nothing to undo";

    private static final String PREVIEW_EXIT = "Goodbye!";

    private static final String ERROR_UNIMPLEMENTED_COMMAND = "Error: command not implemented yet";

    private static final String STATUS_READY = "Ready!";
    private static final String STATUS_PREVIEW_COMMAND = "Preview: ";

    // ================================================================================
    // Class variables
    // ================================================================================

    private TaskEngine taskEngine;
    private UI ui;
    private Command lastPreviewedCommand = null;

    private StringProperty userInput = new SimpleStringProperty();
    private StringProperty statusLabelText = new SimpleStringProperty();

    // ================================================================================
    // Singleton pattern
    // ================================================================================

    private static Logic logic;

    private Logic() {
        initUi();
        initTaskEngine();
        initParser();
        logger.log(Level.INFO, DEBUG_LOGIC_INIT);
    }

    public static Logic getInstance() {
        if (logic == null) {
            logic = new Logic();
        }
        return logic;
    }

    // Main handle
    public void initialiseWindow(Stage primaryStage) {
        assert (ui != null);
        ui.setUpStage(primaryStage);
        ui.setUpBinding(userInput, statusLabelText);
        attachHandlersAndListeners();
        updateUiTaskList();
        setStatus(STATUS_READY);
    }

    // ================================================================================
    // Logic methods
    // ================================================================================

    public String previewCommand(String userCommand) {
    	lastPreviewedCommand = Parser.parse(userCommand);
        return runCommand(lastPreviewedCommand, false);
    }

    public String executeLastPreviewedCommand() {
    	assert(lastPreviewedCommand != null);
        return runCommand(lastPreviewedCommand, true);
    }

    public boolean hasLastPreviewedCommand() {
    	return lastPreviewedCommand != null;
    }

    private String runCommand(Command command, boolean execute) {

        switch (command.getType()) {

            case ADD_DREAM: {
                String description = command.getDescription();

                if (execute) {
                    try {
						taskEngine.add(new Dream(description));
					} catch (IOException e) {
						e.printStackTrace();
					}
                    updateUiTaskList();
                }

                return FEEDBACK_ADD_DREAM + description;
            }

            case ADD_DEADLINE: {
                String description = command.getDescription();
                Date date = command.getDate();

                if (execute) {
                    try {
                        taskEngine.add(new Deadline(description, date));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    updateUiTaskList();
                }

                return String.format(FEEDBACK_ADD_DEADLINE, description, date);
            }

            case ADD_EVENT: {
                String description = command.getDescription();
                Date startDate = command.getStartDate();
                Date endDate = command.getEndDate();

                if (execute) {
                    try {
                        taskEngine.add(new Event(description, startDate, endDate));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    updateUiTaskList();
                }

                return String.format(FEEDBACK_ADD_EVENT, description, startDate, endDate);
            }

            case EDIT: {
                int lineNumber = command.getLineNumber();

                if (lineNumber < 1 || lineNumber > getCurrentTaskList().size()) {
                    return FEEDBACK_INVALID_LINE_NUMBER + lineNumber;
                }

                Task task = getTaskFromLineNumber(lineNumber);

                String newDescription = command.getDescription();
                Task newTask = new Dream(newDescription);

                if (execute) {
                    try {
						taskEngine.edit(task.getId(), newTask);
					} catch (IOException e) {
						e.printStackTrace();
					}
                    updateUiTaskList();
                }

                return String.format(FEEDBACK_EDIT_DREAM, lineNumber, newDescription);
            }

            case DELETE: {
                int lineNumber = command.getLineNumber();

                if (lineNumber < 1 || lineNumber > getCurrentTaskList().size()) {
                    return FEEDBACK_INVALID_LINE_NUMBER + lineNumber;
                }

                Task task = getTaskFromLineNumber(lineNumber);
                String type = task.getTypeString();
                String description = task.getDescription();

                if (execute) {
                    try {
						taskEngine.delete(task.getId());
					} catch (IOException e) {
						e.printStackTrace();
					}
                    updateUiTaskList();
                }

                return String.format(FEEDBACK_DELETED, type, description);
            }

            case DONE: {
                int lineNumber = command.getLineNumber();

                if (lineNumber < 1 || lineNumber > getCurrentTaskList().size()) {
                    return FEEDBACK_INVALID_LINE_NUMBER + lineNumber;
                }

                Task task = getTaskFromLineNumber(lineNumber);
                String type = task.getTypeString();
                String description = task.getDescription();

                if (execute) {
                    try {
						taskEngine.done(task.getId());
					} catch (IOException e) {
						e.printStackTrace();
					}
                    updateUiTaskList();
                }

                return String.format(FEEDBACK_DONE, type, description);
            }

            case UNDO: {
                if (taskEngine.hasPreviousOperation()) {
                    if (execute) {
                        try {
							taskEngine.undo();
						} catch (IOException e) {
							e.printStackTrace();
						}
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

    private void initUi() {
        ui = new UI();
    }

    private void initTaskEngine() {
        taskEngine = new TaskEngine();
    }

    private void initParser() {
        Parser.parse("Natty starts up slowly due tomorrow");
    }

    // ================================================================================
    // Utility methods
    // ================================================================================

    private Task getTaskFromLineNumber(int lineNumber) {
        return getCurrentTaskList().get(lineNumber - 1);
    }

    private List<Task> getCurrentTaskList() {
        return taskEngine.getOutstandingTasks();
    }

    // ================================================================================
    // UI Interaction methods
    // ================================================================================

    private void updateUiTaskList() {
        ui.updateTaskList(getCurrentTaskList());
    }

    // Retrieves the current user input from the TextField.
    private String getInput() {
        return userInput.get();
    }

    private void clearInput() {
        ui.getUserInputField().clear();
    }

    // Sets the text of the 'Status' Label directly.
    private void setStatus(String status) {
        statusLabelText.set(status);
    }

    // Handles KeyEvents upon key release by the user.
    // Key release is used to enable user to see the response first before the event executes.
    private EventHandler<KeyEvent> createKeyReleaseHandler() {
        return (keyEvent) -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                String input = getInput();
                clearInput(); // Must come before setStatus as key release handler resets status.
                if (!input.trim().isEmpty()) {
                    if (!hasLastPreviewedCommand()) {
                        previewCommand(input);
                    }
                    String feedback = executeLastPreviewedCommand();
                    setStatus(feedback);
                } else {
                    setStatus(STATUS_READY);
                }
            }
            // For testing purposes
            if (keyEvent.getCode().equals(KeyCode.SHIFT)) {
                ui.getCSC();
            }
            if (keyEvent.getCode().equals(KeyCode.ALT)) {
                ui.showHelp();
            }
        };
    }

    // Attaches KeyHandler and Listener to the TextField to dynamically update the 'Status' Label upon input.
    private void attachHandlersAndListeners() {
        TextField userInputField = ui.getUserInputField();
        userInputField.setOnKeyReleased(createKeyReleaseHandler());
        userInputField.textProperty().addListener((observable, oldValue, newValue) -> {
            // A ChangeListener is added and the arguments are sent to its 'changed' method,
            // which is overwritten below:
            if (newValue.trim().isEmpty()) {
                setStatus(STATUS_READY);
            } else {
                setStatus(STATUS_PREVIEW_COMMAND + previewCommand(newValue));
            }
        });
    }
}
