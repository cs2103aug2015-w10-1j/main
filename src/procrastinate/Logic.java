package procrastinate;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import procrastinate.task.*;
import procrastinate.test.UIStub;
import procrastinate.ui.UI;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Logic {

    private static final Logger logger = Logger.getLogger(Logic.class.getName());

    private static enum ViewType {
        SHOW_OUTSTANDING, SHOW_DONE, SHOW_ALL, SHOW_SEARCH_RESULTS
    }

    private static DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);

    // ================================================================================
    // Message strings
    // ================================================================================

    private static final String DEBUG_LOGIC_INIT = "Logic initialised.";

    private static final String FEEDBACK_ADD_DREAM = "Added dream: ";
    private static final String FEEDBACK_ADD_DEADLINE = "Added deadline: %1$s due %2$s";
    private static final String FEEDBACK_ADD_EVENT = "Added event: %1$s from %2$s to %3$s";
    private static final String FEEDBACK_EDIT_DREAM = "Edited #%1$s: %2$s";
    private static final String FEEDBACK_EDIT_DEADLINE = "Edited #%1$s: %2$s due %3$s";
    private static final String FEEDBACK_EDIT_EVENT = "Edited #%1$s: %2$s from %3$s to %4$s";
    private static final String FEEDBACK_DELETED = "Deleted %1$s: %2$s";
    private static final String FEEDBACK_DONE = "Done %1$s: %2$s";
    private static final String FEEDBACK_UNDONE = "Undone %1$s: %2$s";
    private static final String FEEDBACK_SEARCH = "Searching for tasks";
    private static final String FEEDBACK_SEARCH_CONTAINING = " containing '%1$s'";
    private static final String FEEDBACK_SEARCH_DUE = " due %1$s";
    private static final String FEEDBACK_INVALID_LINE_NUMBER = "Invalid line number: ";
    private static final String FEEDBACK_UNDO = "Undid last operation";
    private static final String FEEDBACK_NOTHING_TO_UNDO = "Nothing to undo";
    private static final String FEEDBACK_HELP = "Showing help screen";
    private static final String FEEDBACK_SHOW_ALL = "Showing all tasks";
    private static final String FEEDBACK_SHOW_DONE = "Showing completed tasks";
    private static final String FEEDBACK_SHOW_OUTSTANDING = "Showing outstanding tasks";

    private static final String PREVIEW_EXIT = "Goodbye!";

    private static final String DEBUG_FILE_WRITE_FAILURE = "Could not write to file";

    private static final String ERROR_UNIMPLEMENTED_COMMAND = "Error: command not implemented yet";

    private static final String STATUS_READY = "Ready!";
    private static final String STATUS_PREVIEW_COMMAND = "Preview: ";

    // ================================================================================
    // Class variables
    // ================================================================================

    private TaskEngine taskEngine;
    private UI ui;

    private Command lastPreviewedCommand = null;
    private ViewType currentView = ViewType.SHOW_OUTSTANDING; // default view
    private String lastSearchTerm = null;

    private StringProperty userInput = new SimpleStringProperty();
    private StringProperty statusLabelText = new SimpleStringProperty();

    // ================================================================================
    // Singleton pattern
    // ================================================================================

    private static Logic logic;

    private Logic() {
        this(false, null);
    }

    private Logic(boolean isUnderTest, UIStub uiStub) {
        if (isUnderTest) {
            ui = uiStub;
            try {
                taskEngine = new TaskEngine(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            initUi();
            try {
                initTaskEngine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        initParser();
        logger.log(Level.INFO, DEBUG_LOGIC_INIT);
    }

    public static Logic getInstance() {
        if (logic == null) {
            logic = new Logic();
        }
        return logic;
    }

    public static Logic getTestInstance(UIStub uiStub) {
        return new Logic(true, uiStub);
    }

    // Main handle
    public void initialiseWindow(Stage primaryStage) {
        assert(ui != null);
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
                    boolean success = taskEngine.add(new Dream(description));
                    if (!success) {
                        logger.log(Level.SEVERE, DEBUG_FILE_WRITE_FAILURE);
                    }
                    currentView = ViewType.SHOW_OUTSTANDING;
                    updateUiTaskList();
                }

                return FEEDBACK_ADD_DREAM + description;
            }

            case ADD_DEADLINE: {
                String description = command.getDescription();
                Date date = command.getDate();

                if (execute) {
                    boolean success = taskEngine.add(new Deadline(description, date));
                    if (!success) {
                        logger.log(Level.SEVERE, DEBUG_FILE_WRITE_FAILURE);
                    }
                    currentView = ViewType.SHOW_OUTSTANDING;
                    updateUiTaskList();
                }

                return String.format(FEEDBACK_ADD_DEADLINE, description, formatDate(date));
            }

            case ADD_EVENT: {
                String description = command.getDescription();
                Date startDate = command.getStartDate();
                Date endDate = command.getEndDate();

                if (execute) {
                    boolean success = taskEngine.add(new Event(description, startDate, endDate));
                    if (!success) {
                        logger.log(Level.SEVERE, DEBUG_FILE_WRITE_FAILURE);
                    }
                    currentView = ViewType.SHOW_OUTSTANDING;
                    updateUiTaskList();
                }

                return String.format(FEEDBACK_ADD_EVENT, description, formatDate(startDate), formatDate(endDate));
            }

            case EDIT: {
                int lineNumber = command.getLineNumber();

                if (lineNumber < 1 || lineNumber > getCurrentTaskList().size()) {
                    return FEEDBACK_INVALID_LINE_NUMBER + lineNumber;
                }

                Task oldTask = getTaskFromLineNumber(lineNumber);
                String oldDescription = oldTask.getDescription();

                String newDescription = command.getDescription();
                Date newDate = command.getDate();
                Date newStartDate = command.getStartDate();
                Date newEndDate = command.getEndDate();

                Task newTask;

                if (newDate != null) {
                    newTask = new Deadline(oldDescription, newDate);
                } else if (newStartDate != null) {
                    newTask = new Event(oldDescription, newStartDate, newEndDate);
                } else {
                    newTask = Task.copy(oldTask);
                }

                if (newDescription != null) {
                    newTask.setDescription(newDescription);
                }

                if (execute) {
                    boolean success = taskEngine.edit(oldTask.getId(), newTask);
                    if (!success) {
                        logger.log(Level.SEVERE, DEBUG_FILE_WRITE_FAILURE);
                    }
                    updateUiTaskList();
                }

                switch (newTask.getType()) {
                    case DREAM:
                        return String.format(FEEDBACK_EDIT_DREAM, lineNumber, newTask.getDescription());
                    case DEADLINE:
                        return String.format(FEEDBACK_EDIT_DEADLINE, lineNumber, newTask.getDescription(),
                                formatDate(((Deadline) newTask).getDate()));
                    case EVENT:
                        return String.format(FEEDBACK_EDIT_EVENT, lineNumber, newTask.getDescription(),
                                formatDate(((Event) newTask).getStartDate()),
                                formatDate(((Event) newTask).getEndDate()));
                }

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
                    boolean success = taskEngine.delete(task.getId());
                    if (!success) {
                        logger.log(Level.SEVERE, DEBUG_FILE_WRITE_FAILURE);
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
                String feedback;

                if (!task.isDone()) {
                    feedback = FEEDBACK_DONE;
                } else {
                    feedback = FEEDBACK_UNDONE;
                }

                if (execute) {
                    boolean success;
                    if (!task.isDone()) {
                        success = taskEngine.done(task.getId());
                    } else {
                        success = taskEngine.undone(task.getId());
                    }
                    if (!success) {
                        logger.log(Level.SEVERE, DEBUG_FILE_WRITE_FAILURE);
                    }
                    updateUiTaskList();
                }

                return String.format(feedback, type, description);
            }

            case UNDO: {
                if (!taskEngine.hasPreviousOperation()) {
                    return FEEDBACK_NOTHING_TO_UNDO;
                }

                if (execute) {
                    boolean success = taskEngine.undo();
                    if (!success) {
                        logger.log(Level.SEVERE, DEBUG_FILE_WRITE_FAILURE);
                    }
                    updateUiTaskList();
                }

                return FEEDBACK_UNDO;
            }

            case SEARCH: {
                String description = command.getDescription();
                Date date = command.getDate();

                String feedback = FEEDBACK_SEARCH;
                if (description != null) {
                    feedback += String.format(FEEDBACK_SEARCH_CONTAINING, description);
                    if (execute) {
                        lastSearchTerm = description;
                        currentView = ViewType.SHOW_SEARCH_RESULTS;
                        updateUiTaskList();
                    }
                }
                if (date != null) {
                    feedback += String.format(FEEDBACK_SEARCH_DUE, date);
                    //TODO: implement searching by date
                }
                return feedback;
            }

            case SHOW_OUTSTANDING: {
                if (execute) {
                    currentView = ViewType.SHOW_OUTSTANDING;
                    updateUiTaskList();
                }

                return FEEDBACK_SHOW_OUTSTANDING;
            }

            case SHOW_DONE: {
                if (execute) {
                    currentView = ViewType.SHOW_DONE;
                    updateUiTaskList();
                }

                return FEEDBACK_SHOW_DONE;
            }

            case SHOW_ALL: {
                if (execute) {
                    currentView = ViewType.SHOW_ALL;
                    updateUiTaskList();
                }

                return FEEDBACK_SHOW_ALL;
            }

            case HELP: {
                if (execute) {
                    ui.showHelp();
                }

                return FEEDBACK_HELP;
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

    private void initTaskEngine() throws IOException {
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
        return taskEngine.getCurrentTaskList();
    }

    private static String formatDate(Date date) {
        return dateFormat.format(date);
    }

    // ================================================================================
    // UI Interaction methods
    // ================================================================================

    private void updateUiTaskList() {
        switch (currentView) {
            case SHOW_OUTSTANDING:
                updateUiTaskList(taskEngine.getOutstandingTasks());
                break;
            case SHOW_DONE:
                updateUiTaskList(taskEngine.getCompletedTasks());
                break;
            case SHOW_ALL:
                updateUiTaskList(taskEngine.getAllTasks());
                break;
            case SHOW_SEARCH_RESULTS:
                assert(lastSearchTerm != null);
                updateUiTaskList(taskEngine.getTasksContaining(lastSearchTerm));
                break;
        }
    }

    private void updateUiTaskList(List<Task> taskList) {
        ui.updateTaskList(taskList);
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
            // To remove the help overlay once the user starts typing
            ui.checkForScreenOverlay();

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

            if (keyEvent.getCode().equals(KeyCode.F1)) {
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
