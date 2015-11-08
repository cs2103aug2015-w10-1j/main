//@@author A0080485B
package procrastinate;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.time.DateUtils;

import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import procrastinate.Command.CommandType;
import procrastinate.task.Deadline;
import procrastinate.task.Dream;
import procrastinate.task.Event;
import procrastinate.task.Task;
import procrastinate.task.TaskEngine;
import procrastinate.ui.UI;
import procrastinate.ui.UI.ScreenView;

public class Logic {

    // ================================================================================
    // Class variables
    // ================================================================================

    private static final Logger logger = Logger.getLogger(Logic.class.getName());

    private static enum ViewType {
        SHOW_OUTSTANDING, SHOW_DONE, SHOW_ALL, SHOW_SUMMARY, SHOW_SEARCH_RESULTS
    }

    // ================================================================================
    // Message strings
    // ================================================================================

    private static final String DEBUG_LOGIC_INIT = "Logic initialised.";

    private static final String NATTY_WARMUP_STRING = "Natty starts up slowly due tomorrow";

    private static final String FEEDBACK_READY = "Ready!";
    private static final String FEEDBACK_ADD = "New %1$s: ";
    private static final String FEEDBACK_EDIT = "Edited #%1$s: ";
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
    private static final String FEEDBACK_HELP = "Showing help screen (use left/right keys to navigate)";
    private static final String FEEDBACK_SHOW_ALL = "Showing all tasks";
    private static final String FEEDBACK_SHOW_DONE = "Showing completed tasks";
    private static final String FEEDBACK_SHOW_OUTSTANDING = "Showing outstanding tasks";
    private static final String FEEDBACK_SHOW_SUMMARY = "Showing summary of outstanding tasks";
    private static final String FEEDBACK_TRY_AGAIN = "Please set a different save location and try again";
    private static final String FEEDBACK_EXIT = "Goodbye!";

    private static final String ERROR_STARTUP_HEADER = "There was a problem accessing the directory";
    private static final String ERROR_STARTUP_MESSAGE = "Please startup Procrastinate from a different working directory";
    private static final String ERROR_SAVE_HEADER = "Could not save changes to file!";
    private static final String ERROR_SAVE_MESSAGE = FEEDBACK_TRY_AGAIN;
    private static final String ERROR_EXIT_HEADER = "Could not save changes! Your data will be LOST!";
    private static final String ERROR_EXIT_MESSAGE = "Discard unsaved changes and exit?";
    private static final String ERROR_EXIT_BUTTON_LABEL = "Discard and exit";
    private static final String ERROR_SET_LOCATION_HEADER = "Could not set save location:";
    private static final String ERROR_SET_LOCATION_MESSAGE = "%1$s%2$s\n\n" + FEEDBACK_TRY_AGAIN;

    private static final String SEARCH_STRING_DESCRIPTION = "'%1$s'";
    private static final String SEARCH_STRING_NO_DESCRIPTION = "all tasks";
    private static final String SEARCH_STRING_ON = " on ";
    private static final String SEARCH_STRING_DUE = " due ";
    private static final String SEARCH_STRING_FROM_TO = " from %1$s to %2$s";

    private static final DateFormat dateTimeFormatter = new SimpleDateFormat("d/MM/yy h:mma");
    private static final DateFormat dateFormatter = new SimpleDateFormat("d/MM/yy");

    // ================================================================================
    // Instance variables
    // ================================================================================

    protected TaskEngine taskEngine;
    protected UI ui;

    private boolean hasStartupError = false;

    private Command lastPreviewedCommand = null;

    private ViewType currentView;

    private String searchString = null;
    private String searchTerm = null;
    private Date searchStartDate = null;
    private Date searchEndDate = null;
    private boolean searchShowDone = true;

    // ================================================================================
    // Singleton pattern
    // ================================================================================

    private static Logic logic;

    protected Logic() {
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

            case ADD_DREAM :
            case ADD_DEADLINE :
            case ADD_EVENT :
                return runAdd(command, execute);

            case EDIT :
            case EDIT_TO_DREAM :
                return runEdit(command, execute);

            case EDIT_PARTIAL :
                return runEditPartial(command);

            case DELETE :
                return runDelete(command, execute);

            case DONE :
                return runDone(command, execute);

            case UNDO :
                return runUndo(execute);

            case SEARCH :
            case SEARCH_ON :
                return runSearch(command, execute);

            case SET_PATH :
                return runSetPath(command, execute);

            case SHOW_OUTSTANDING :
                return runShowOutstanding(execute);

            case SHOW_DONE :
                return runShowDone(execute);

            case SHOW_ALL :
                return runShowAll(execute);

            case SHOW_SUMMARY :
                return runShowSummary(execute);

            case HELP :
                return runHelp(execute);

            case INVALID :
                return runInvalid(command);

            case EXIT :
                return runExit(execute);

            default :
                return null;

        }

    }

    // ================================================================================
    // Command handling methods
    // ================================================================================

    private String runAdd(Command command, boolean execute) {
        String description = command.getDescription();
        assert(description != null);

        Task newTask = null;
        Date date = null;
        Date startDate = null;
        Date endDate = null;

        switch(command.getType()) {
            case ADD_DREAM :
                newTask = new Dream(description);
                break;

            case ADD_DEADLINE :
                date = command.getDate();
                assert(date != null);

                newTask = new Deadline(description, date);
                break;

            case ADD_EVENT :
                startDate = command.getStartDate();
                endDate = command.getEndDate();
                assert(startDate != null && endDate != null);

                if (endDate.before(startDate)) {
                    return String.format(FEEDBACK_INVALID_RANGE, formatDateTime(startDate), formatDateTime(endDate));
                }

                newTask = new Event(description, startDate, endDate);
                break;

            default :
                break;
        }

        if (execute) {
            boolean success = taskEngine.add(newTask);
            updateView(ViewType.SHOW_OUTSTANDING);
            if (!success) {
                ui.createErrorDialog(ERROR_SAVE_HEADER, ERROR_SAVE_MESSAGE);
                return FEEDBACK_TRY_AGAIN;
            }
        }

        return ui.fitToStatus(String.format(FEEDBACK_ADD, newTask.getTypeString()), description, newTask.getDateString());
    }

    private String runEdit(Command command, boolean execute) {
        int lineNumber = command.getLineNumber();

        if (!isValidLineNumber(lineNumber)) {
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
            if (newEndDate.before(newStartDate)) {
                return String.format(FEEDBACK_INVALID_RANGE,
                        formatDateTime(newStartDate), formatDateTime(newEndDate));
            }

            newTask = new Event(oldDescription, newStartDate, newEndDate);

        } else if (command.getType() == CommandType.EDIT_TO_DREAM) {
            newTask = new Dream(oldDescription);

        } else {
            newTask = Task.copyWithNewId(oldTask);

        }

        if (newDescription != null) {
            newTask.setDescription(newDescription);
        }

        if (execute) {
            boolean success = taskEngine.edit(oldTask.getId(), newTask);
            updateView();
            if (!success) {
                ui.createErrorDialog(ERROR_SAVE_HEADER, ERROR_SAVE_MESSAGE);
                return FEEDBACK_TRY_AGAIN;
            }
        }

        return ui.fitToStatus(String.format(FEEDBACK_EDIT, lineNumber), newTask.getDescription(), newTask.getDateString());
    }

    private String runEditPartial(Command command) {
        int lineNumber = command.getLineNumber();

        if (!isValidLineNumber(lineNumber)) {
            return FEEDBACK_INVALID_LINE_NUMBER + lineNumber;
        }

        return FEEDBACK_EDIT_PARTIAL;
    }

    private String runDelete(Command command, boolean execute) {
        int lineNumber = command.getLineNumber();

        if (!isValidLineNumber(lineNumber)) {
            return FEEDBACK_INVALID_LINE_NUMBER + lineNumber;
        }

        Task task = getTaskFromLineNumber(lineNumber);
        String type = task.getTypeString();
        String description = task.getDescription();

        if (execute) {
            boolean success = taskEngine.delete(task.getId());
            updateView();
            if (!success) {
                ui.createErrorDialog(ERROR_SAVE_HEADER, ERROR_SAVE_MESSAGE);
                return FEEDBACK_TRY_AGAIN;
            }
        }

        return String.format(FEEDBACK_DELETED, type, description);
    }

    private String runDone(Command command, boolean execute) {
        int lineNumber = command.getLineNumber();

        if (!isValidLineNumber(lineNumber)) {
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
            boolean success = taskEngine.done(task.getId());
            updateView();
            if (!success) {
                ui.createErrorDialog(ERROR_SAVE_HEADER, ERROR_SAVE_MESSAGE);
                return FEEDBACK_TRY_AGAIN;
            }
        }

        return String.format(feedback, type, description);
    }

    private String runUndo(boolean execute) {
        if (!taskEngine.hasPreviousOperation()) {
            return FEEDBACK_NOTHING_TO_UNDO;
        }

        if (execute) {
            boolean success = taskEngine.undo();
            updateView();
            if (!success) {
                ui.createErrorDialog(ERROR_SAVE_HEADER, ERROR_SAVE_MESSAGE);
                return FEEDBACK_TRY_AGAIN;
            }
        }

        return FEEDBACK_UNDO;
    }

    private String runSearch(Command command, boolean execute) {
        String description = command.getDescription();
        Date date = command.getDate();
        Date startDate = command.getStartDate();
        Date endDate = command.getEndDate();

        if (execute) {
            searchString = "";
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
                searchString += String.format(SEARCH_STRING_DESCRIPTION, description);
            }
        } else {
            if (execute) {
                searchString += SEARCH_STRING_NO_DESCRIPTION;
            }
        }

        if (date != null) {
            searchShowDone = false;

            // set time to 0000 hrs of the specified day
            date = DateUtils.truncate(date, Calendar.DATE);

            if (command.getType() == CommandType.SEARCH_ON) {
                feedback += String.format(FEEDBACK_SEARCH_ON, formatDate(date));
                if (execute) {
                    searchStartDate = date;
                    searchEndDate = DateUtils.addDays(date, 3);
                    searchString += SEARCH_STRING_ON + formatDate(date);
                }

            } else {
                feedback += String.format(FEEDBACK_SEARCH_DUE, formatDate(date));
                if (execute) {
                    searchStartDate = new Date(0); // beginning of time
                    searchEndDate = DateUtils.addDays(date, 3);
                    searchString += SEARCH_STRING_DUE + formatDate(date);
                }

            }

        } else if (startDate != null) {
            assert(endDate != null);

            // set time to 0000 hrs of the specified day
            startDate = DateUtils.truncate(startDate, Calendar.DATE);
            endDate = DateUtils.truncate(endDate, Calendar.DATE);

            if (endDate.before(startDate)) {
                return String.format(FEEDBACK_INVALID_RANGE, formatDate(startDate), formatDate(endDate));
            }

            feedback += String.format(FEEDBACK_SEARCH_FROM_TO, formatDate(startDate), formatDate(endDate));
            if (execute) {
                searchStartDate = startDate;
                searchEndDate = DateUtils.addDays(endDate, 1);;
                searchString += String.format(SEARCH_STRING_FROM_TO, formatDate(startDate), formatDate(endDate));
            }
        }

        if (execute) {
            updateView(ViewType.SHOW_SEARCH_RESULTS);
        }

        return feedback;
    }

    private String runSetPath(Command command, boolean execute) {
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
                ui.createErrorDialog(ERROR_SET_LOCATION_HEADER,
                                     String.format(ERROR_SET_LOCATION_MESSAGE,
                                                   parsedPathDirectory, pathFilename));
                return FEEDBACK_TRY_AGAIN;
            }
        }

        return FEEDBACK_SET_LOCATION + parsedPathDirectory + pathFilename;
    }

    private String runShowOutstanding(boolean execute) {
        if (execute) {
            updateView(ViewType.SHOW_OUTSTANDING);
        }
        return FEEDBACK_SHOW_OUTSTANDING;
    }

    private String runShowDone(boolean execute) {
        if (execute) {
            updateView(ViewType.SHOW_DONE);
        }
        return FEEDBACK_SHOW_DONE;
    }

    private String runShowAll(boolean execute) {
        if (execute) {
            updateView(ViewType.SHOW_ALL);
        }
        return FEEDBACK_SHOW_ALL;
    }

    private String runShowSummary(boolean execute) {
        if (execute) {
            updateView(ViewType.SHOW_SUMMARY);
        }
        return FEEDBACK_SHOW_SUMMARY;
    }

    private String runHelp(boolean execute) {
        if (execute) {
            ui.showHelpOverlay();
        }
        return FEEDBACK_HELP;
    }

    private String runInvalid(Command command) {
        return command.getDescription();
    }

    private String runExit(boolean execute) {
        if (execute) {
            if (!exit()) {
                return FEEDBACK_TRY_AGAIN;
            }
        }
        return FEEDBACK_EXIT;
    }

    // ================================================================================
    // Init methods
    // ================================================================================

    // Main handle
    public void initUi(Stage stage) {
        ui = new UI(stage);
        ui.attachHandlersAndListeners(createKeyPressHandler(), createUserInputListener(), createIsExitListener());
        if (hasStartupError) {
            ui.createErrorDialog(ERROR_STARTUP_HEADER, ERROR_STARTUP_MESSAGE);
            exit();
        }
        initUiTaskList();
        ui.setPreviewStatus(FEEDBACK_READY);
    }

    protected void initTaskEngine() {
		try {
            taskEngine = new TaskEngine();
        } catch (IOException e) {
            hasStartupError = true;
        }
    }

    private void initParser() {
        Parser.parse(NATTY_WARMUP_STRING);
    }

    // ================================================================================
    // UI Interaction methods
    // ================================================================================

    private void updateView() {
        if (currentView == ViewType.SHOW_SUMMARY) {
            currentView = ViewType.SHOW_OUTSTANDING;
        }
        updateUiTaskList();
    }

    private void updateView(ViewType newView) {
        currentView = newView;
        updateUiTaskList();
    }

    private void updateUiTaskList() {
        switch (currentView) {
            case SHOW_OUTSTANDING :
                ui.updateTaskList(taskEngine.getOutstandingTasks(), ScreenView.SCREEN_MAIN);
                break;

            case SHOW_DONE :
                ui.updateTaskList(taskEngine.getCompletedTasks(), ScreenView.SCREEN_DONE);
                break;

            case SHOW_ALL :
                ui.updateTaskList(taskEngine.getAllTasks(), ScreenView.SCREEN_MAIN);
                break;

            case SHOW_SUMMARY :
                ui.updateTaskList(taskEngine.getOutstandingTasks(), ScreenView.SCREEN_SUMMARY);
                break;

            case SHOW_SEARCH_RESULTS :
                ui.passSearchStringToSearchScreen(searchString);
                ui.updateTaskList(taskEngine.search(searchTerm, searchStartDate, searchEndDate, searchShowDone),
                                  ScreenView.SCREEN_SEARCH);
                break;
        }
    }

    private void initUiTaskList() {
        ui.initialUpdateTaskList(taskEngine.getOutstandingTasks());
        updateView(ViewType.SHOW_SUMMARY);
    }

    // Process key press events
    private EventHandler<KeyEvent> createKeyPressHandler() {
        return (keyEvent) -> {

            // Remove initial splash overlay
            ui.hideSplashOverlay();

            switch (keyEvent.getCode()) {

                // Main command execution flow
                case ENTER : {

                    // Whitespace command
                    if (ui.getInput().trim().isEmpty()) {
                        ui.clearInput();
                        ui.hideHelpOverlay();
                        return;
                    }

                    // Hide help unless it's a help or exit command
                    if (!lastPreviewedCommand.getType().equals(CommandType.HELP)
                            && !lastPreviewedCommand.getType().equals(CommandType.EXIT)) {
                        ui.hideHelpOverlay();
                    }

                    // All the work happens here!
                    String feedback = executeLastPreviewedCommand();

                    // Clear input box and display feedback
                    // ClearInput must come before setStatus as user input listener
                    // resets status when input is cleared
                    ui.clearInput();
                    ui.setExecuteStatus(feedback);

                    return;
                }

                // Edit description autocompletion
                case TAB : {
                    if (!hasLastPreviewedCommand()) {
                        return;
                    }

                    Command command = lastPreviewedCommand;
                    if (command.getType() != CommandType.EDIT_PARTIAL) {
                        return;
                    }

                    int lineNumber = lastPreviewedCommand.getLineNumber();

                    if (!isValidLineNumber(lineNumber)) {
                        return;
                    }

                    ui.setInput(ui.getInput().trim() + " " + getTaskFromLineNumber(lineNumber).getDescription());
                    return;
                }

                // Scrolling
                case UP : {
                    ui.scrollUpScreen();
                    return;
                }
                case DOWN : {
                    ui.scrollDownScreen();
                    return;
                }

                // Show help
                case F1 : {
                    ui.showHelpOverlay();
                    if (ui.getInput().isEmpty()) {
                        ui.setPreviewStatus(FEEDBACK_HELP);
                    }
                    return;
                }

                // Activate next help page using left/right keys
                // (but only when the input box is empty)
                case LEFT :
                case RIGHT : {
                    if (ui.getInput().isEmpty()) {
                        ui.nextHelpPage();
                    }
                    return;
                }

                // Hide help
                case ESCAPE : {
                    ui.hideHelpOverlay();
                    if (ui.getInput().trim().isEmpty()) {
                        ui.setPreviewStatus(FEEDBACK_READY);
                    }
                    return;
                }

                default :
                    break;
            }
        };
    }

    // Main command preview flow
    private ChangeListener<String> createUserInputListener() {
        return (observable, oldValue, newValue) -> {
            if (newValue.trim().isEmpty()) {
                ui.setPreviewStatus(FEEDBACK_READY);
            } else {
                ui.setPreviewStatus(previewCommand(newValue));
            }
        };
    }

    // Listen for exit invoked by close button or system tray
    private ChangeListener<Boolean> createIsExitListener() {
        return (observable, oldValue, newValue) -> {
            if (newValue.booleanValue()) {
                if (!exit()) {
                    ui.resetIsExit();
                    ui.setPreviewStatus(FEEDBACK_TRY_AGAIN);
                }
            }
        };
    }

    // ================================================================================
    // Utility methods
    // ================================================================================

    // Exit routine used by exit command, close button and system tray
    private boolean exit() {
        if (hasStartupError) {
            hideAndTerminate();
        }

        if (!taskEngine.hasPreviousOperation()) {
            hideAndTerminate(); // No write operations; safe to exit
        }

        boolean success = taskEngine.save(); // Try to write state to file
        if (success) {
            hideAndTerminate(); // Write success; safe to exit
        }

        // Write failure; create confirmation dialog to warn user
        boolean exitAnyway = ui.createErrorDialogWithConfirmation(ERROR_EXIT_HEADER,
                ERROR_EXIT_MESSAGE, ERROR_EXIT_BUTTON_LABEL);
        if (exitAnyway) {
            hideAndTerminate(); // User chose to exit anyway despite save failure
        }

        return false; // User pressed cancel
    }

    // Simulate faster exit by hiding window first
    private void hideAndTerminate() {
        ui.hide();
        System.exit(0);
    }

    private boolean isValidLineNumber(int lineNumber) {
        return (lineNumber >= 1 && lineNumber <= getCurrentTaskList().size());
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

}
