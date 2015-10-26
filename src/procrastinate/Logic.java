package procrastinate;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import procrastinate.Command.CommandType;
import procrastinate.task.*;
import procrastinate.test.UIStub;
import procrastinate.ui.UI;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.time.DateUtils;

public class Logic {

    private static final Logger logger = Logger.getLogger(Logic.class.getName());

    private static enum ViewType {
        SHOW_OUTSTANDING, SHOW_DONE, SHOW_ALL, SHOW_SEARCH_RESULTS
    }

    private static DateFormat dateTimeFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
    private static DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);

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
    private static final String FEEDBACK_SEARCH_ON = " on %1$s";
    private static final String FEEDBACK_SEARCH_DUE = " due by %1$s";
    private static final String FEEDBACK_SEARCH_FROM_TO = " from %1$s to %2$s";
    private static final String FEEDBACK_INVALID_LINE_NUMBER = "Invalid line number: ";
    private static final String FEEDBACK_INVALID_FROM_TO = "Invalid date range: %2$s is before %1$s";
    private static final String FEEDBACK_UNDO = "Undid last operation";
    private static final String FEEDBACK_NOTHING_TO_UNDO = "Nothing to undo";
    private static final String FEEDBACK_SET_PATH = "Set save directory to ";
    private static final String FEEDBACK_HELP = "Showing help screen";
    private static final String FEEDBACK_SHOW_ALL = "Showing all tasks";
    private static final String FEEDBACK_SHOW_DONE = "Showing completed tasks";
    private static final String FEEDBACK_SHOW_OUTSTANDING = "Showing outstanding tasks";
    private static final String FEEDBACK_USE_DIFFERENT_PATH = "Please try setting a different save directory and try again";

    private static final String FEEDBACK_ERROR_SAVE = "Could not write to file! Your changes were not saved.";
    private static final String FEEDBACK_ERROR_SAVE_EXIT = "Could not write to file! Your changes will NOT be saved! Continue?";
    private static final String FEEDBACK_ERROR_SET_PATH = "Could set path to ";

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
    private ViewType currentView = ViewType.SHOW_OUTSTANDING; // default view
    private String lastSearchTerm = null;
    private Date lastSearchStartDate = null;
    private Date lastSearchEndDate = null;

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
                        ui.createErrorDialog(FEEDBACK_ERROR_SAVE);
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
                        ui.createErrorDialog(FEEDBACK_ERROR_SAVE);
                    }
                    currentView = ViewType.SHOW_OUTSTANDING;
                    updateUiTaskList();
                }

                return String.format(FEEDBACK_ADD_DEADLINE, description, formatDateTime(date));
            }

            case ADD_EVENT: {
                String description = command.getDescription();
                Date startDate = command.getStartDate();
                Date endDate = command.getEndDate();

                if (endDate.compareTo(startDate) < 0) {
                    return String.format(FEEDBACK_INVALID_FROM_TO, formatDateTime(startDate), formatDateTime(endDate));
                }

                if (execute) {
                    boolean success = taskEngine.add(new Event(description, startDate, endDate));
                    if (!success) {
                        ui.createErrorDialog(FEEDBACK_ERROR_SAVE);
                    }
                    currentView = ViewType.SHOW_OUTSTANDING;
                    updateUiTaskList();
                }

                return String.format(FEEDBACK_ADD_EVENT, description, formatDateTime(startDate), formatDateTime(endDate));
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
                    if (newEndDate.compareTo(newStartDate) < 0) {
                        return String.format(FEEDBACK_INVALID_FROM_TO, formatDateTime(newStartDate), formatDateTime(newEndDate));
                    }
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
                        ui.createErrorDialog(FEEDBACK_ERROR_SAVE);
                    }
                    updateUiTaskList();
                }

                switch (newTask.getType()) {
                    case DREAM:
                        return String.format(FEEDBACK_EDIT_DREAM, lineNumber, newTask.getDescription());
                    case DEADLINE:
                        return String.format(FEEDBACK_EDIT_DEADLINE, lineNumber, newTask.getDescription(),
                                formatDateTime(((Deadline) newTask).getDate()));
                    case EVENT:
                        return String.format(FEEDBACK_EDIT_EVENT, lineNumber, newTask.getDescription(),
                                formatDateTime(((Event) newTask).getStartDate()),
                                formatDateTime(((Event) newTask).getEndDate()));
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
                        ui.createErrorDialog(FEEDBACK_ERROR_SAVE);
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
                        ui.createErrorDialog(FEEDBACK_ERROR_SAVE);
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
                        ui.createErrorDialog(FEEDBACK_ERROR_SAVE);
                    }
                    updateUiTaskList();
                }

                return FEEDBACK_UNDO;
            }

            case SEARCH:
            case SEARCH_ON: {
                String description = command.getDescription();
                Date date = command.getDate();
                Date startDate = command.getStartDate();
                Date endDate = command.getEndDate();

                if (execute) {
                    lastSearchTerm = null;
                    lastSearchStartDate = null;
                    lastSearchEndDate = null;
                }

                String feedback = FEEDBACK_SEARCH;

                if (description != null) {
                    feedback += String.format(FEEDBACK_SEARCH_CONTAINING, description);
                    if (execute) {
                        lastSearchTerm = description;
                    }
                }

                if (date != null) {
                    // set time to 0000 hrs of the specified day
                    date = DateUtils.truncate(date, Calendar.DATE);

                    if (command.getType() == CommandType.SEARCH_ON) {
                        feedback += String.format(FEEDBACK_SEARCH_ON, formatDate(date));
                        if (execute) {
                            lastSearchStartDate = date;
                            lastSearchEndDate = DateUtils.addDays(date, 3);
                        }

                    } else {
                        feedback += String.format(FEEDBACK_SEARCH_DUE, formatDate(date));
                        if (execute) {
                            lastSearchStartDate = new Date(0); // beginning of time
                            lastSearchEndDate = DateUtils.addDays(date, 1);
                        }

                    }

                } else if (startDate != null) {
                    assert(endDate != null);

                    // set time to 0000 hrs of the specified day
                    startDate = DateUtils.truncate(startDate, Calendar.DATE);
                    endDate = DateUtils.truncate(endDate, Calendar.DATE);

                    if (endDate.compareTo(startDate) < 0) {
                        return String.format(FEEDBACK_INVALID_FROM_TO, formatDate(startDate), formatDate(endDate));
                    }

                    feedback += String.format(FEEDBACK_SEARCH_FROM_TO, formatDate(startDate), formatDate(endDate));
                    if (execute) {
                        lastSearchStartDate = startDate;
                        lastSearchEndDate = DateUtils.addDays(endDate, 1);;
                    }
                }

                if (execute) {
                    currentView = ViewType.SHOW_SEARCH_RESULTS;
                    updateUiTaskList();
                }

                return feedback;
            }

            case SET_PATH: {
                String path = command.getDescription();
                String parsedPath = null;
                try {
                    parsedPath = (new File(path)).getCanonicalPath();
                } catch (IOException e) {
                    parsedPath = (new File(path)).getAbsolutePath();
                }
                if (!parsedPath.endsWith(File.separator)) {
                    parsedPath += File.separator;
                }

                if (execute) {
                    boolean success = taskEngine.set(path);
                    if (!success) {
                        ui.createErrorDialog(FEEDBACK_ERROR_SET_PATH + parsedPath);
                    }
                }

                return FEEDBACK_SET_PATH + parsedPath;
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
                    boolean success = taskEngine.save();
                    if (!success) {
                        boolean exitAnyway = ui.createErrorDialogWithConfirmation(FEEDBACK_ERROR_SAVE_EXIT);
                        if (!exitAnyway) {
                            return FEEDBACK_USE_DIFFERENT_PATH;
                        }
                    }
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

    private static String formatDateTime(Date date) {
        return dateTimeFormat.format(date);
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
                updateUiTaskList(taskEngine.getTasksContaining(lastSearchTerm, lastSearchStartDate, lastSearchEndDate));
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

    private EventHandler<KeyEvent> createKeyPressHandler() {
        return (keyEvent) -> {
            // To remove the help overlay once the user starts typing
            if (!keyEvent.getCode().equals(KeyCode.F1)) {
                ui.checkForScreenOverlay();
            }
        };
    }

    // Attaches KeyHandler and Listener to the TextField to dynamically update the 'Status' Label upon input.
    private void attachHandlersAndListeners() {
        TextField userInputField = ui.getUserInputField();
        userInputField.setOnKeyReleased(createKeyReleaseHandler());
        userInputField.setOnKeyPressed(createKeyPressHandler());
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
