# A0080485Bunused
###### procrastinate\LegacyCommand.java
``` java
// Was refactored into individual procrastinate.command classes by A0124321Y
// Skeleton code for the Command class
package procrastinate;

import java.util.Date;

@Deprecated
public class LegacyCommand {

    public static enum CommandType {
        ADD_DEADLINE, ADD_EVENT, ADD_DREAM, EDIT, EDIT_PARTIAL, EDIT_TO_DREAM, DELETE, UNDO, DONE,
        SEARCH, SEARCH_ON, SHOW_OUTSTANDING, SHOW_DONE, SHOW_ALL, SHOW_SUMMARY,
        SET_PATH, EXIT, HELP, INVALID;
    }

    // Required field for all command types
    private CommandType type_;

    // Optional fields; availability depends on command type
    private String description_;
    private Date date_;
    private Date startDate_;
    private Date endDate_;
    private int lineNumber_;
    private String pathDirectory_;
    private String pathFilename_;

    public LegacyCommand(CommandType type) {
        this.type_ = type;
    }

    // ================================================================================
    // Setter methods (using chaining)
    // ================================================================================

    public LegacyCommand addDescription(String description) {
        description_ = description.trim();
        return this;
    }

    public LegacyCommand addDate(Date date) {
        date_ = date;
        return this;
    }

    public LegacyCommand addStartDate(Date startDate) {
        startDate_ = startDate;
        return this;
    }

    public LegacyCommand addEndDate(Date endDate) {
        endDate_ = endDate;
        return this;
    }

    public LegacyCommand addLineNumber(int lineNumber) {
        lineNumber_ = lineNumber;
        return this;
    }

    public LegacyCommand addPathDirectory(String pathDirectory) {
        pathDirectory_ = pathDirectory;
        return this;
    }

    public LegacyCommand addPathFilename(String fileName) {
        pathFilename_ = fileName;
        return this;
    }

    // ================================================================================
    // Getter methods
    // ================================================================================

    public CommandType getType() {
        return type_;
    }

    public String getDescription() {
        return description_;
    }

    public Date getDate() {
        return date_;
    }

    public Date getStartDate() {
        return startDate_;
    }

    public Date getEndDate() {
        return endDate_;
    }

    public int getLineNumber() {
        return lineNumber_;
    }

    public String getPathDirectory() {
        return pathDirectory_;
    }

    public String getPathFilename() {
        return pathFilename_;
    }

}
```
###### procrastinate\Logic.java
``` java
    // Was refactored into individual procrastinate.command classes by A0124321Y
    // Provided the structure for Command class
    // ================================================================================
    // Command handling methods
    // ================================================================================
/*
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
//*/

```
