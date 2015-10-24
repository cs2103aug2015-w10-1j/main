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

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
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

    private static final String MESSAGE_UNABLE_TO_DETERMINE_TYPE = "Unable to determine TaskType for adding.";

    private static final String EVENT_DATE_SEPARATOR = " to ";
    private static final String TIME_SEPARATOR = " ";   // should we use 'on'?
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

    private SimpleDateFormat dateFormatWithYear = new SimpleDateFormat("d MMM y h:mma");
    private SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM");
    private SimpleDateFormat timeFormat = new SimpleDateFormat("h:mma");
    private SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
    private Date today = Date.from(getInstantFromLocalDateTime(getDateTimeStartOfToday()));    // To get today's Date at 0000hrs
    private Date endOfWeek = getEndOfWeekDate(today);
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
     * @param taskList List of Tasks to be added onto the screen
     */
    protected void updateTaskList(List<Task> taskList) {
        clearTaskList();

        Date taskDate;
        boolean isSameYear;

        for (Task task : taskList) {
            taskCount.set(taskCount.get() + 1);

            switch (task.getType()) {

                case DEADLINE: {
                    taskDate = ((Deadline) task).getDate();
                    isSameYear = yearFormat.format(today).equals(yearFormat.format(taskDate));
                    if (isSameYear) {
                        addTaskWithSameYear(task, taskDate);
                    } else {
                        addTaskWithDifferentYear(task, taskDate);
                    }
                    break;
                }

                case EVENT: {
                    taskDate = ((Event) task).getStartDate();
                    isSameYear = yearFormat.format(today).equals(yearFormat.format(taskDate));
                    if (isSameYear) {
                        addTaskWithSameYear(task, taskDate);
                    } else {
                        addTaskWithDifferentYear(task, taskDate);
                    }
                    break;
                }

                case DREAM: {
                    TaskEntry taskEntry = new TaskEntry(taskCountFormatted.get(), task.getDescription());
                    dreamsTaskList.getChildren().add(taskEntry.getEntryDisplay());
                    break;
                }

                default: {
                    System.out.println(MESSAGE_UNABLE_TO_DETERMINE_TYPE);
                }
            }
        }
    }

    private void addTaskWithSameYear(Task task, Date date) {
        String dateString;

        switch (task.getType()) {

            case DEADLINE: {
                dateString = dateFormat.format(date)
                            + TIME_SEPARATOR
                            + timeFormat.format(date);
                TaskEntry taskEntry = new TaskEntry(taskCountFormatted.get(), task.getDescription(), dateString);
                if (date.before(today)) {
                    overdueTaskList.getChildren().add(taskEntry.getEntryDisplay());
                } else if (date.before(endOfWeek)) {
                    thisWeekTaskList.getChildren().add(taskEntry.getEntryDisplay());
                } else {
                    futureTaskList.getChildren().add(taskEntry.getEntryDisplay());
                }
                break;
            }

            case EVENT: {
                Date endDate = ((Event) task).getEndDate();
                boolean isSameEndYear = checkEventEndDateYear(endDate);
                if (isSameEndYear) {
                    dateString = dateFormat.format(date)
                                + TIME_SEPARATOR
                                + timeFormat.format(date)
                                + EVENT_DATE_SEPARATOR
                                + dateFormat.format(endDate)
                                + TIME_SEPARATOR
                                + timeFormat.format(date);
                } else {
                    dateString = dateFormat.format(date)
                                + TIME_SEPARATOR
                                + timeFormat.format(date)
                                + EVENT_DATE_SEPARATOR
                                + dateFormatWithYear.format(endDate)
                                + TIME_SEPARATOR
                                + timeFormat.format(date);
                }
                TaskEntry taskEntry = new TaskEntry(taskCountFormatted.get(), task.getDescription(), dateString);
                if (date.before(today)) {
                    overdueTaskList.getChildren().add(taskEntry.getEntryDisplay());
                } else if (date.before(endOfWeek)) {
                    thisWeekTaskList.getChildren().add(taskEntry.getEntryDisplay());
                } else {
                    futureTaskList.getChildren().add(taskEntry.getEntryDisplay());
                }
                break;
            }

            default: {
                System.out.println(MESSAGE_UNABLE_TO_DETERMINE_TYPE);
            }
        }
    }

    private void addTaskWithDifferentYear(Task task, Date date) {
        String dateString;

        switch (task.getType()) {

            case DEADLINE: {
                dateString = dateFormatWithYear.format(date);
                TaskEntry taskEntry = new TaskEntry(taskCountFormatted.get(), task.getDescription(), dateString);
                if (date.before(today)) {
                    overdueTaskList.getChildren().add(taskEntry.getEntryDisplay());
                } else if (date.before(endOfWeek)) {
                    thisWeekTaskList.getChildren().add(taskEntry.getEntryDisplay());
                } else {
                    futureTaskList.getChildren().add(taskEntry.getEntryDisplay());
                }
                break;
            }

            case EVENT: {
                Date endDate = ((Event) task).getEndDate();
                dateString = dateFormatWithYear.format(date)
                            + EVENT_DATE_SEPARATOR
                            + dateFormatWithYear.format(endDate);
                TaskEntry taskEntry = new TaskEntry(taskCountFormatted.get(), task.getDescription(), dateString);
                if (date.before(today)) {
                    overdueTaskList.getChildren().add(taskEntry.getEntryDisplay());
                } else if (date.before(endOfWeek)) {
                    thisWeekTaskList.getChildren().add(taskEntry.getEntryDisplay());
                } else {
                    futureTaskList.getChildren().add(taskEntry.getEntryDisplay());
                }
                break;
            }

            default: {
                System.out.println(MESSAGE_UNABLE_TO_DETERMINE_TYPE);
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
        taskCount.set(0);
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

    private LocalDateTime getDateTimeStartOfToday() {
        return LocalDate.now().atStartOfDay();
    }

    /**
     * Converts a LocalDateTime to an Instant
     * @param localDateTime to be converted
     * @return Instant generated from the given LocalDateTime
     */
    private Instant getInstantFromLocalDateTime(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant();
    }

    /**
     * Generates the date of the end of the week for task date comparisons
     * @param today Current date at 0000hrs
     * @return Date of next Monday at 0000hrs for comparing tasks due this week
     */
    private Date getEndOfWeekDate(Date today) {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setTime(today);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        calendar.add(Calendar.DAY_OF_WEEK, 1);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return calendar.getTime();
    }

    private boolean checkEventEndDateYear(Date date) {
        return yearFormat.format(today).equals(yearFormat.format(date));
    }

    /**
     * Generates a list of 7 days starting from today
     * @return Arraylist of LocalDates of 7 consecutive days including today
     */
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
