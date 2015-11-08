//@@author A0080485B
package procrastinate.task;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import procrastinate.FileHandler;

public class TaskEngine {

    private static final Logger logger = Logger.getLogger(TaskEngine.class.getName());

    // ================================================================================
    // Message strings
    // ================================================================================

    private static final String DEBUG_TASK_ENGINE_INIT = "TaskEngine initialised.";
    private static final String DEBUG_ADDED_TASK = "Added %1$s: %2$s";
    private static final String DEBUG_EDITED_TASK = "Edited #%1$s: %2$s";
    private static final String DEBUG_DELETED_TASK = "Deleted %1$s: %2$s";
    private static final String DEBUG_DONE_TASK = "Done %1$s: %2$s";
    private static final String DEBUG_UNDONE_TASK = "Undone %1$s: %2$s";
    private static final String DEBUG_UNDONE = "Last task operation undone";

    private static final String ERROR_TASK_NOT_FOUND = "Task not found!";

    // ================================================================================
    // Class variables
    // ================================================================================

    private TaskState previousState_ = null;
    private TaskState currentState_ = null;
    private TaskState currentView_ = null;

    private boolean isPreviousOperationSet_ = false;
    private String previousSaveDirectory_ = null;
    private String previousSaveFilename_ = null;

    protected FileHandler fileHandler;

    public TaskEngine() throws IOException {
        initFileHandler();
        initTasks();
        logger.log(Level.INFO, DEBUG_TASK_ENGINE_INIT);
    }

    // ================================================================================
    // TaskEngine methods
    // ================================================================================

    public boolean add(Task task) {
        backupOlderState();

        String description = task.getDescription();
        String type = task.getTypeString();

        getTasks().add(task);

        logger.log(Level.INFO, String.format(DEBUG_ADDED_TASK, type, description));

        return writeStateToFile();
    }

    public boolean edit(UUID taskId, Task newTask) {
        backupOlderState();

        int index = getIndexFromId(taskId);
        getTasks().remove(index);
        getTasks().add(index, newTask);

        logger.log(Level.INFO, String.format(DEBUG_EDITED_TASK, index + 1, newTask.getDescription()));

        return writeStateToFile();
    }

    public boolean delete(UUID taskId) {
        backupOlderState();

        int index = getIndexFromId(taskId);
        Task task = getTasks().get(index);
        getTasks().remove(index);

        String description = task.getDescription();
        String type = task.getTypeString();

        logger.log(Level.INFO, String.format(DEBUG_DELETED_TASK, type, description));

        return writeStateToFile();
    }

    public boolean done(UUID taskId) {
        backupOlderState();

        int index = getIndexFromId(taskId);
        Task task = Task.copy(getTasks().get(index));
        task.setDone(!task.isDone());
        getTasks().remove(index);
        getTasks().add(index, task);

        String description = task.getDescription();
        String type = task.getTypeString();
        String feedback;

        if (task.isDone()) {
            feedback = DEBUG_DONE_TASK;
        } else {
            feedback = DEBUG_UNDONE_TASK;
        }

        logger.log(Level.INFO, String.format(feedback, type, description));

        return writeStateToFile();
    }

    public boolean undo() {
        if (isPreviousOperationSet_) {
            return set(previousSaveDirectory_, previousSaveFilename_);
        }

        if (!hasPreviousOperation()) {
            return true;
        }

        TaskState backupNewerState = getBackupOfCurrentState();
        restoreOlderState();
        previousState_ = backupNewerState;

        logger.log(Level.INFO, String.format(DEBUG_UNDONE));

        return writeStateToFile();
    }

    public boolean save() {
        return writeStateToFile();
    }

    public boolean set(String directory, String filename) {
        isPreviousOperationSet_ = true;
        File previousSaveFile = fileHandler.getSaveFile();
        previousSaveDirectory_ = previousSaveFile.getAbsoluteFile().getParent() + File.separator;
        previousSaveFilename_ = previousSaveFile.getName();
        return fileHandler.setPath(directory, filename);
    }

    public boolean hasPreviousOperation() {
        return previousState_ != null || isPreviousOperationSet_;
    }

    public List<Task> search(String description, Date startDate, Date endDate, boolean showDone) {
        assert(description != null || startDate != null && endDate != null);
        List<Task> results = getTasks();
        if (description != null) {
            results = results.stream()
                    .filter(task -> task.contains(description))
                    .collect(Collectors.toList());
        }
        if (startDate != null) {
            results = results.stream()
                    .filter(task -> task.isWithin(startDate, endDate))
                    .collect(Collectors.toList());
        }
        if (!showDone) {
            results = results.stream()
                    .filter(task -> !task.isDone())
                    .collect(Collectors.toList());
        }
        currentView_ = new TaskState(results);
        return currentView_.getTasks();
    }

    public List<Task> getOutstandingTasks() {
        List<Task> outstandingTasks = getTasks().stream()
                .filter(task -> !task.isDone())
                .collect(Collectors.toList());
        currentView_ = new TaskState(outstandingTasks);
        return currentView_.getTasks();
    }

    public List<Task> getCompletedTasks() {
        List<Task> completedTasks = getTasks().stream()
                .filter(task -> task.isDone())
                .collect(Collectors.toList());
        currentView_ = new TaskState(completedTasks);
        return currentView_.getTasks();
    }

    public List<Task> getAllTasks() {
        currentView_ = currentState_;
        return currentView_.getTasks();
    }

    public List<Task> getCurrentTaskList() {
        return currentView_.getTasks();
    }

    // ================================================================================
    // Init methods
    // ================================================================================

    protected void initFileHandler() throws IOException {
        fileHandler = new FileHandler();
    }

    private void initTasks() {
        loadState(fileHandler.loadTaskState());
        currentView_ = currentState_;
        Collections.sort(getTasks());
    }

    // ================================================================================
    // State handling methods
    // ================================================================================

    private void backupOlderState() {
        previousState_ = getBackupOfCurrentState();
        isPreviousOperationSet_ = false;
    }

    private void restoreOlderState() {
        loadState(previousState_);
    }

    private void loadState(TaskState state) {
        currentState_ = state;
    }

    private boolean writeStateToFile() {
        return fileHandler.saveTaskState(getCurrentState());
    }

    private TaskState getBackupOfCurrentState() {
        return TaskState.copy(getCurrentState());
    }

    private TaskState getCurrentState() {
        Collections.sort(getTasks());
        return currentState_;
    }

    // ================================================================================
    // Utility methods
    // ================================================================================

    private int getIndexFromId(UUID id) {
        for (int i = 0; i < getTasks().size(); i++) {
            if (getTasks().get(i).getId().equals(id)) {
                return i;
            }
        }
        throw new Error(ERROR_TASK_NOT_FOUND);
    }

    private List<Task> getTasks() {
        return currentState_.getTasks();
    }

}