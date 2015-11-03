package procrastinate;

import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.apache.commons.lang.time.DateUtils;

import procrastinate.Command.CommandType;
import procrastinate.task.*;
import procrastinate.ui.UI;
import procrastinate.ui.UI.ScreenView;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Logic {

    private static final Logger logger = Logger.getLogger(Logic.class.getName());

    private static enum ViewType {
        SHOW_OUTSTANDING, SHOW_DONE, SHOW_ALL, SHOW_SEARCH_RESULTS
    }

    // ================================================================================
    // Message strings
    // ================================================================================

    private static final String DEBUG_LOGIC_INIT = "Logic initialised.";

    private static final String STATUS_READY = "Ready!";
    private static final String STATUS_PREVIEW_COMMAND = ">>";

    private static final String FEEDBACK_ADD_DREAM = "New dream: ";
    private static final String FEEDBACK_ADD_DEADLINE = "New deadline: %1$s due %2$s";
    private static final String FEEDBACK_ADD_EVENT = "New event: %1$s %2$s to %3$s";
    private static final String FEEDBACK_EDIT_DREAM = "Edited #%1$s: %2$s";
    private static final String FEEDBACK_EDIT_DEADLINE = "Edited #%1$s: %2$s due %3$s";
    private static final String FEEDBACK_EDIT_EVENT = "Edited #%1$s: %2$s %3$s to %4$s";
    private static final String FEEDBACK_EDIT_PARTIAL = "Please specify the new description/date(s) or press tab";
    private static final String FEEDBACK_DELETED = "Deleted %1$s: %2$s";
    private static final String FEEDBACK_DONE = "Done %1$s: %2$s";
    private static final String FEEDBACK_UNDONE = "Undone %1$s: %2$s";
    private static final String FEEDBACK_SEARCH = "Searching for tasks";
    private static final String FEEDBACK_SEARCH_CONTAINING = " containing '%1$s'";
    private static final String FEEDBACK_SEARCH_ON = " on %1$s";
    private static final String FEEDBACK_SEARCH_DUE = " due by %1$s";
    private static final String FEEDBACK_SEARCH_FROM_TO = " from %1$s to %2$s";
    private static final String FEEDBACK_INVALID_LINE_NUMBER = "Invalid line number: ";
    private static final String FEEDBACK_INVALID_RANGE = "Invalid dates: %2$s is before %1$s";
    private static final String FEEDBACK_UNDO = "Undid last operation";
    private static final String FEEDBACK_NOTHING_TO_UNDO = "Nothing to undo";
    private static final String FEEDBACK_SET_LOCATION = "Set save location to ";
    private static final String FEEDBACK_HELP = "Showing help screen";
    private static final String FEEDBACK_SHOW_ALL = "Showing all tasks";
    private static final String FEEDBACK_SHOW_DONE = "Showing completed tasks";
    private static final String FEEDBACK_SHOW_OUTSTANDING = "Showing outstanding tasks";
    private static final String FEEDBACK_TRY_AGAIN = "Please try setting a different save location and try again";

    private static final String FEEDBACK_ERROR_SAVE = "Could not save changes to file!";
    private static final String FEEDBACK_ERROR_SAVE_EXIT = "Could not save changes! Your data will be lost! Continue?";
    private static final String FEEDBACK_ERROR_SET_LOCATION = "Could not set save location to ";

    private static final String PREVIEW_EXIT = "Goodbye!";

    private static final String ERROR_UNIMPLEMENTED_COMMAND = "Error: command not implemented yet";

    private static final int MAX_LENGTH_DESCRIPTION = 20;
    private static final int MAX_LENGTH_DESCRIPTION_SHORT = 10;
    private static final int MAX_LENGTH_DESCRIPTION_TINY = 7;

    private static final DateFormat dateTimeFormatter = new SimpleDateFormat("d/MM/yy h:mma");
    private static final DateFormat dateFormatter = new SimpleDateFormat("d/MM/yy");

    // ================================================================================
    // Class variables
    // ================================================================================

    protected TaskEngine taskEngine;
    protected UI ui;

    private Command lastPreviewedCommand = null;

    private ViewType currentView = ViewType.SHOW_OUTSTANDING; // initial view

    private String searchTerm = null;
    private Date searchStartDate = null;
    private Date searchEndDate = null;
    private boolean searchShowDone = true;

    // ================================================================================
    // Singleton pattern
    // ================================================================================

    private static Logic logic;

    protected Logic() {
        try {
            initTaskEngine();
        } catch (IOException e) {
            e.printStackTrace();
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

        CommandType commandType = command.getType();

        switch (commandType) {

            case ADD_DREAM:
            case ADD_DEADLINE:
            case ADD_EVENT: {
                String description = command.getDescription();
                assert(description != null);

                Task newTask = null;
                Date date = null;
                Date startDate = null;
                Date endDate = null;

                if (commandType == CommandType.ADD_DREAM) {
                    newTask = new Dream(description);

                } else if (commandType == CommandType.ADD_DEADLINE) {
                    date = command.getDate();
                    assert(date != null);

                    newTask = new Deadline(description, date);

                } else { // CommandType.ADD_EVENT
                    startDate = command.getStartDate();
                    endDate = command.getEndDate();
                    assert(startDate != null && endDate != null);

                    if (endDate.compareTo(startDate) < 0) {
                        return String.format(FEEDBACK_INVALID_RANGE, formatDateTime(startDate), formatDateTime(endDate));
                    }

                    newTask = new Event(description, startDate, endDate);

                }

                if (execute) {
                    boolean success = taskEngine.add(newTask);
                    if (!success) {
                        ui.createErrorDialog(FEEDBACK_ERROR_SAVE);
                    }
                    currentView = ViewType.SHOW_OUTSTANDING;
                    updateUiTaskList();
                }

                if (commandType == CommandType.ADD_DREAM) {
                    return FEEDBACK_ADD_DREAM + description;

                } else if (commandType == CommandType.ADD_DEADLINE) {
                    return String.format(FEEDBACK_ADD_DEADLINE, shorten(description, MAX_LENGTH_DESCRIPTION),
                            formatDateTime(date));

                } else { // CommandType.ADD_DEADLINE
                    return String.format(FEEDBACK_ADD_EVENT, shorten(description, MAX_LENGTH_DESCRIPTION_SHORT),
                            formatDateTime(startDate), formatDateTime(endDate));

                }

            }

            case EDIT:
            case EDIT_TO_DREAM: {
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
                    assert(newEndDate != null);
                    if (newEndDate.compareTo(newStartDate) < 0) {
                        return String.format(FEEDBACK_INVALID_RANGE,
                                formatDateTime(newStartDate), formatDateTime(newEndDate));
                    }

                    newTask = new Event(oldDescription, newStartDate, newEndDate);

                } else if (commandType == CommandType.EDIT_TO_DREAM) {
                    newTask = new Dream(oldDescription);

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

                String description = newTask.getDescription();

                switch (newTask.getType()) {
                    case DREAM:
                        return String.format(FEEDBACK_EDIT_DREAM, lineNumber, description);
                    case DEADLINE:
                        return String.format(FEEDBACK_EDIT_DEADLINE, lineNumber,
                                shorten(description, MAX_LENGTH_DESCRIPTION),
                                formatDateTime(((Deadline) newTask).getDate()));
                    case EVENT:
                        return String.format(FEEDBACK_EDIT_EVENT, lineNumber,
                                shorten(description, MAX_LENGTH_DESCRIPTION_TINY),
                                formatDateTime(((Event) newTask).getStartDate()),
                                formatDateTime(((Event) newTask).getEndDate()));
                }

            }

            case EDIT_PARTIAL: {
                int lineNumber = command.getLineNumber();

                if (lineNumber < 1 || lineNumber > getCurrentTaskList().size()) {
                    return FEEDBACK_INVALID_LINE_NUMBER + lineNumber;
                }

                return FEEDBACK_EDIT_PARTIAL;
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
                    searchTerm = null;
                    searchStartDate = null;
                    searchEndDate = null;
                    searchShowDone = true;
                }

                String feedback = FEEDBACK_SEARCH;

                if (description != null) {
                    feedback += String.format(FEEDBACK_SEARCH_CONTAINING, description);
                    if (execute) {
                        searchTerm = description;
                    }
                }

                if (date != null) {
                    searchShowDone = false;

                    // set time to 0000 hrs of the specified day
                    date = DateUtils.truncate(date, Calendar.DATE);

                    if (commandType == CommandType.SEARCH_ON) {
                        feedback += String.format(FEEDBACK_SEARCH_ON, formatDate(date));
                        if (execute) {
                            searchStartDate = date;
                            searchEndDate = DateUtils.addDays(date, 3);
                        }

                    } else {
                        feedback += String.format(FEEDBACK_SEARCH_DUE, formatDate(date));
                        if (execute) {
                            searchStartDate = new Date(0); // beginning of time
                            searchEndDate = DateUtils.addDays(date, 3);
                        }

                    }

                } else if (startDate != null) {
                    assert(endDate != null);

                    // set time to 0000 hrs of the specified day
                    startDate = DateUtils.truncate(startDate, Calendar.DATE);
                    endDate = DateUtils.truncate(endDate, Calendar.DATE);

                    if (endDate.compareTo(startDate) < 0) {
                        return String.format(FEEDBACK_INVALID_RANGE, formatDate(startDate), formatDate(endDate));
                    }

                    feedback += String.format(FEEDBACK_SEARCH_FROM_TO, formatDate(startDate), formatDate(endDate));
                    if (execute) {
                        searchStartDate = startDate;
                        searchEndDate = DateUtils.addDays(endDate, 1);;
                    }
                }

                if (execute) {
                    currentView = ViewType.SHOW_SEARCH_RESULTS;
                    updateUiTaskList();
                }

                return feedback;
            }

            case SET_PATH: {
                String pathDirectory = command.getPathDirectory();
                String pathFilename = command.getPathFilename();

                String parsedPathDirectory = null;
                File targetDirectory = new File(pathDirectory);

                try {
                    parsedPathDirectory = targetDirectory.getCanonicalPath();
                } catch (IOException e) {
                    parsedPathDirectory = targetDirectory.getAbsolutePath();
                }

                if (!parsedPathDirectory.endsWith(File.separator)) {
                    parsedPathDirectory += File.separator;
                }

                if (pathFilename == null) {
                    pathFilename = FileHandler.DEFAULT_FULL_FILENAME;
                }

                if (execute) {
                    boolean success = taskEngine.set(parsedPathDirectory, pathFilename);
                    if (!success) {
                        ui.createErrorDialog(FEEDBACK_ERROR_SET_LOCATION + parsedPathDirectory + pathFilename);
                    }
                }

                return FEEDBACK_SET_LOCATION + parsedPathDirectory + pathFilename;
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
                    ui.showHelpOverlay();
                }

                return FEEDBACK_HELP;
            }

            case INVALID: {
                return command.getDescription();
            }

            case EXIT: {
                if (execute) {
                    if (!exit()) {
                        return FEEDBACK_TRY_AGAIN;
                    }
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

    // Main handle
    public void initUi(Stage stage) {
        ui = new UI(stage);
        ui.attachHandlersAndListeners(createKeyReleaseHandler(), createKeyPressHandler(),
                createUserInputListener(), createIsExitListener());
        ui.setStatus(STATUS_READY);
        updateUiTaskList();
    }

    protected void initTaskEngine() throws IOException {
		taskEngine = new TaskEngine();
    }

    private void initParser() {
        Parser.parse("Natty starts up slowly due tomorrow");
    }

    // ================================================================================
    // UI Interaction methods
    // ================================================================================

    private void updateUiTaskList() {
        switch (currentView) {
            case SHOW_OUTSTANDING:
                updateUiTaskList(taskEngine.getOutstandingTasks(), ScreenView.SCREEN_MAIN);
                break;
            case SHOW_DONE:
                updateUiTaskList(taskEngine.getCompletedTasks(), ScreenView.SCREEN_DONE);
                break;
            case SHOW_ALL:
                updateUiTaskList(taskEngine.getAllTasks(), ScreenView.SCREEN_MAIN);
                break;
            case SHOW_SEARCH_RESULTS:
                ui.passSearchTermToSearchScreen(searchTerm);
                updateUiTaskList(taskEngine.search(searchTerm, searchStartDate, searchEndDate, searchShowDone),
                        ScreenView.SCREEN_SEARCH);
                break;
        }
    }

    private void updateUiTaskList(List<Task> taskList, ScreenView screenView) {
        ui.updateTaskList(taskList, screenView);
    }

    // Handles KeyEvents upon key release by the user.
    // Key release is used to enable user to see the response first before the event executes.
    private EventHandler<KeyEvent> createKeyReleaseHandler() {
        return (keyEvent) -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                String input = ui.getInput();
                ui.clearInput(); // Must come before setStatus as key release handler resets status.
                if (!input.trim().isEmpty()) {
                    if (!hasLastPreviewedCommand()) {
                        previewCommand(input);
                    }
                    String feedback = executeLastPreviewedCommand();
                    ui.setStatus(feedback);
                } else {
                    ui.setStatus(STATUS_READY);
                }
            }
            if (keyEvent.getCode().equals(KeyCode.F1)) {
                ui.showHelpOverlay();
            }
            if (keyEvent.getCode().equals(KeyCode.TAB)) {
                if (!hasLastPreviewedCommand()) {
                    return;
                }

                Command command = lastPreviewedCommand;
                if (command.getType() != CommandType.EDIT_PARTIAL) {
                    return;
                }

                int lineNumber = lastPreviewedCommand.getLineNumber();

                if (lineNumber < 1 || lineNumber > getCurrentTaskList().size()) {
                    return;
                }

                ui.setInput(ui.getInput().trim() + " " + getTaskFromLineNumber(lineNumber).getDescription());
            }
        };
    }

    private EventHandler<KeyEvent> createKeyPressHandler() {
        return (keyEvent) -> {
            // To remove the help overlay only when the user presses 'Enter' or 'Esc'
            // And checks also if the user command is 'help' only (follows case-insensitivity)
            if (keyEvent.getCode().equals(KeyCode.ESCAPE)
                    || keyEvent.getCode().equals(KeyCode.ENTER)
                       && hasLastPreviewedCommand()
                       && !lastPreviewedCommand.getType().equals(CommandType.HELP)) {
                ui.hideHelpOverlay();
            }
            ui.hideSplashOverlay();
        };
    }

    private ChangeListener<String> createUserInputListener() {
        return (observable, oldValue, newValue) -> {
            // A ChangeListener is added and the arguments are sent to its 'changed' method,
            // which is overwritten below:
            if (newValue.trim().isEmpty()) {
                ui.setStatus(STATUS_READY);
            } else {
                ui.setStatus(STATUS_PREVIEW_COMMAND + previewCommand(newValue));
            }
        };
    }

    private ChangeListener<Boolean> createIsExitListener() {
        return (observable, oldValue, newValue) -> {
            if (newValue.booleanValue()) {
                if (!exit()) {
                    ui.getIsExit().set(false);
                    ui.setStatus(FEEDBACK_TRY_AGAIN);
                }
            }
        };
    }

    // ================================================================================
    // Utility methods
    // ================================================================================

    private boolean exit() {
        if (!taskEngine.hasPreviousOperation()) {
            System.exit(0);
        }

        boolean success = taskEngine.save();
        if (success) {
            System.exit(0);
        }

        boolean exitAnyway = ui.createErrorDialogWithConfirmation(FEEDBACK_ERROR_SAVE_EXIT);
        if (exitAnyway) {
            System.exit(0);
        }

        return false;
    }

    private Task getTaskFromLineNumber(int lineNumber) {
        return getCurrentTaskList().get(lineNumber - 1);
    }

    private List<Task> getCurrentTaskList() {
        return taskEngine.getCurrentTaskList();
    }

    private static String formatDateTime(Date date) {
        return dateTimeFormatter.format(date);
    }

    private static String formatDate(Date date) {
        return dateFormatter.format(date);
    }

    private static String shorten(String description, int maxLength) {
        if (description.length() <= maxLength) {
            return description;
        } else {
            return description.substring(0, maxLength - 1) + "...";
        }
    }

}
