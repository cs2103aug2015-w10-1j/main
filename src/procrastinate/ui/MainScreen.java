package procrastinate.ui;

import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.util.converter.NumberStringConverter;
import procrastinate.task.Deadline;
import procrastinate.task.Event;
import procrastinate.task.Task;

import java.text.DateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainScreen extends CenterScreen {

    // ================================================================================
    // Message strings
    // ================================================================================

    private static final String CATEGORY_OVERDUE = "Overdue";
    private static final String CATEGORY_THIS_WEEK = "This Week";
    private static final String CATEGORY_FUTURE = "Future";
    private static final String CATEGORY_DREAMS = "Dreams";

    private static final String TASKTYPE_DEADLINE = "deadline";
    //private static final String TASKTYPE_DREAMS = "dream";
    private static final String TASKTYPE_EVENT = "event";

    private static final String UI_NUMBER_SEPARATOR = ". ";

    private static final int NUMBER_DAYS_OF_WEEK = 7;

    // ================================================================================
    // Class variables
    // ================================================================================

    // Nodes are used to add them onto the screen
    private Node overdueNode;
    private Node thisWeekNode;
    private Node futureNode;
    private Node dreamsNode;

    // Each category box is saved here for easy referencing in case the box is extended next time
    private CategoryBox overdueBox;
    private CategoryBox thisWeekBox;
    private CategoryBox futureBox;
    private CategoryBox dreamsBox;

    // The main variables to call when adding tasks since they act as a task list for a TaskEntry to be displayed
    private VBox overdueTaskList;
    private VBox thisWeekTaskList;
    private VBox futureTaskList;
    private VBox dreamsTaskList;

    private IntegerProperty taskCount = new SimpleIntegerProperty(1);
    private StringProperty taskCountFormatted = new SimpleStringProperty();
    private StringProperty taskCountString = new SimpleStringProperty();

    // ================================================================================
    // FXML field variables
    // ================================================================================

    @FXML VBox mainVBox;

    // ================================================================================
    // MainScreen methods
    // ================================================================================

    protected MainScreen(String filePath) {
        super(filePath);
        createCategories();
        setupBinding();
        getEntriesBoxes();
    }

    /**
     * The list of tasks displayed is updated by removing all previously added tasks and re-adding them back to allow
     * the line number to be sorted by category and not insertion time. **NOT FULLY IMPLEMENTED**
     * @param taskList
     */
    protected void updateTaskList(List<Task> taskList) {
        clearTaskList();

        // TO-DO: CLEANUP/REFACTORING
        for (Task task : taskList) {
            String taskType = task.getTypeString();
            Date today = Date.from(getDateTimeToday().atZone(ZoneId.systemDefault()).toInstant());
            DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
            String dateString;
            ArrayList<LocalDate> daysOfWeek = getWeek();
            if (taskType.equals(TASKTYPE_DEADLINE)) {
                dateString = dateFormat.format(((Deadline) task).getDate());
                if (((Deadline) task).getDate().before(today)) {
                    TaskEntry taskEntry = new TaskEntry(taskCountFormatted.get(), task.getDescription(), dateString);
                    taskCount.set(taskCount.get() + 1);
                    overdueTaskList.getChildren().add(taskEntry.getEntryDisplay());
                } else if (((Deadline) task).getDate().before(Date.from(daysOfWeek.get(6).atTime(LocalTime.now()).atZone(ZoneId.systemDefault()).toInstant()))) {
                    TaskEntry taskEntry = new TaskEntry(taskCountFormatted.get(), task.getDescription(), dateString);
                    taskCount.set(taskCount.get() + 1);
                    thisWeekTaskList.getChildren().add(taskEntry.getEntryDisplay());
                } else {
                    TaskEntry taskEntry = new TaskEntry(taskCountFormatted.get(), task.getDescription(), dateString);
                    taskCount.set(taskCount.get() + 1);
                    futureTaskList.getChildren().add(taskEntry.getEntryDisplay());
                }
            } else if (taskType.equals(TASKTYPE_EVENT)) {
                dateString = dateFormat.format(((Event) task).getStartDate())
                             + " to "
                             + dateFormat.format(((Event) task).getEndDate());
                if (((Event) task).getStartDate().before(today)) {
                    TaskEntry taskEntry = new TaskEntry(taskCountFormatted.get(), task.getDescription(), dateString);
                    taskCount.set(taskCount.get() + 1);
                    overdueTaskList.getChildren().add(taskEntry.getEntryDisplay());
                } else if (((Event) task).getStartDate().before(Date.from(daysOfWeek.get(6).atTime(LocalTime.now()).atZone(ZoneId.systemDefault()).toInstant()))) {
                    TaskEntry taskEntry = new TaskEntry(taskCountFormatted.get(), task.getDescription(), dateString);
                    taskCount.set(taskCount.get() + 1);
                    thisWeekTaskList.getChildren().add(taskEntry.getEntryDisplay());
                } else {
                    TaskEntry taskEntry = new TaskEntry(taskCountFormatted.get(), task.getDescription(), dateString);
                    taskCount.set(taskCount.get() + 1);
                    futureTaskList.getChildren().add(taskEntry.getEntryDisplay());
                }
            } else {
                TaskEntry taskEntry = new TaskEntry(taskCountFormatted.get(), task.getDescription());
                taskCount.set(taskCount.get() + 1);
                dreamsTaskList.getChildren().add(taskEntry.getEntryDisplay());
            }
        }
    }

    // ================================================================================
    // Utility methods
    // ================================================================================

    /**
     * Used when updating the task list, removes all tasks and resets the task counter
     */
    private void clearTaskList() {
        resetTaskCount();
        resetTaskList();
    }

    private void resetTaskCount() {
        taskCount.set(1);
    }

    private void resetTaskList() {
        overdueTaskList.getChildren().clear();
        thisWeekTaskList.getChildren().clear();
        futureTaskList.getChildren().clear();
        dreamsTaskList.getChildren().clear();
    }

    private LocalDate getDateToday() {
        return LocalDate.now();
    }

    private LocalDateTime getDateTimeToday() {
        return LocalDate.now().atStartOfDay();
    }

    private ArrayList<LocalDate> getWeek() {
        LocalDate today = getDateToday();
        ArrayList<LocalDate> daysOfWeek = new ArrayList<>();
        for (int i=0; i<NUMBER_DAYS_OF_WEEK; i++) {
            daysOfWeek.add(today.plusDays(i));
        }
        return daysOfWeek;
    }

    // ================================================================================
    // Init methods
    // ================================================================================

    /**
     * Setup the various categories that tasks can fall under
     */
    private void createCategories() {
        // Create all the different categories(by time frame) for entries to go into
        this.overdueBox = new CategoryBox(CATEGORY_OVERDUE);
        this.thisWeekBox = new CategoryBox(CATEGORY_THIS_WEEK);
        this.futureBox = new CategoryBox(CATEGORY_FUTURE);
        this.dreamsBox = new CategoryBox(CATEGORY_DREAMS);

        this.overdueNode = overdueBox.getCategoryBox();
        this.thisWeekNode = thisWeekBox.getCategoryBox();
        this.futureNode = futureBox.getCategoryBox();
        this.dreamsNode = dreamsBox.getCategoryBox();

        // Set up for placement of boxes
        mainVBox.setPadding(new Insets(10));    // Looks a bit weird if the boxes stick right next to the border
        mainVBox.setSpacing(10);                // Spacing between boxes
        mainVBox.getChildren().addAll(overdueNode, thisWeekNode, futureNode, dreamsNode);
    }

    /**
     * Retrieves all the VBoxes for each TaskEntry to go into
     */
    private void getEntriesBoxes() {
        this.overdueTaskList = overdueBox.getTaskListVBox();
        this.thisWeekTaskList = thisWeekBox.getTaskListVBox();
        this.futureTaskList = futureBox.getTaskListVBox();
        this.dreamsTaskList = dreamsBox.getTaskListVBox();
    }

    /**
     * Creates a formatted shared task counter for use when adding tasks onto the screen
     */
    private void setupBinding() {
        taskCountString.bindBidirectional(taskCount, new NumberStringConverter());
        taskCountFormatted.bind(Bindings.concat(taskCountString).concat(UI_NUMBER_SEPARATOR));
    }
}
