package procrastinate.ui;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.SequentialTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
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
    private static final String CATEGORY_DONE = "Done";

    private static final String SUBCATEGORY_TODAY = "TODAY";
    private static final String SUBCATEGORY_TOMORROW = "TOMORROW";

    private static final String LOCATION_EMPTY_VIEW = "images/no-tasks.png";

    private static final String MESSAGE_UNABLE_TO_DETERMINE_TYPE = "Unable to determine TaskType for adding.";
    private static final String MESSAGE_UNABLE_TO_RECOGNISE_NODE = "Unable to recognise Node.";

    private static final String EVENT_DATE_SEPARATOR = "\nto ";
    private static final String SELECTOR_CATEGORY_VBOX = "#categoryVBox";
    private static final String TIME_SEPARATOR = " ";   // should we use 'on'?
    private static final String UI_NUMBER_SEPARATOR = ". ";

    // Time values used are in milliseconds
    private static final int TIME_INITIALISE_FADE = 200;
    private static final int TIME_TRANSITION_FADE_IN = 300;
    private static final int TIME_TRANSITION_FADE_OUT = 300;
    private static final double OPACITY_ZERO = 0;
    private static final double OPACITY_FULL = 1;

    private static final String FX_BACKGROUND_IMAGE_NULL = "-fx-background-image: null;";
    private static String FX_BACKGROUND_IMAGE_NO_TASKS; // will be initialised later on.

    // ================================================================================
    // Class variables
    // ================================================================================

    // Nodes are used to add them onto the screen
    private Node overdueNode;
    private Node thisWeekNode;
    private Node futureNode;
    private Node dreamsNode;
    private Node doneNode;
    private ArrayList<Node> nodeList = new ArrayList<>();

    private int[] subcategoryVisibilityTracker; // used to determine if the subcategory is to be faded in or out.
    private ArrayList<VBox> thisWeekSubcategories = new ArrayList<>();

    // Used for determining insertion order when adding nodes back onto screen.
    private DoubleProperty overdueNodeOpacity = new SimpleDoubleProperty(0);
    private DoubleProperty thisWeekNodeOpacity = new SimpleDoubleProperty(0);
    private DoubleProperty futureNodeOpacity = new SimpleDoubleProperty(0);
    private DoubleProperty dreamsNodeOpacity = new SimpleDoubleProperty(0);

    // The main variables to call when adding tasks since they act as a task list for a TaskEntry to be displayed
    private VBox overdueTaskList;
    private VBox thisWeekTaskList;
    private VBox futureTaskList;
    private VBox dreamsTaskList;
    private VBox doneTaskList;

    private IntegerProperty taskCount = new SimpleIntegerProperty(1);
    private StringProperty taskCountFormatted = new SimpleStringProperty();
    private StringProperty taskCountString = new SimpleStringProperty();

    private SimpleDateFormat dateFormatWithYear = new SimpleDateFormat("d MMMyy h:mma");
    private SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM");
    private SimpleDateFormat timeFormat = new SimpleDateFormat("h:mma");
    private SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");

    private Date today;
    private Date currentDate;
    private Date endOfWeek;

    private boolean isInitialise = true;

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
        prepareForSubcategories();
        setupBinding();
    }

    /**
     * The list of tasks displayed is updated by removing all previously added tasks and re-adding them back to allow
     * the line number to be sorted by category and not insertion time.
     *
     * Dreams are directly added via this method but Deadlines and Events are passed to two different
     * addTask methods depending on their (start) dates.
     * @param taskList List of Tasks to be added onto the screen
     */
    protected void updateTaskList(List<Task> taskList) {
        updateDates();
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
                    if (task.isDone()) {
                        doneTaskList.getChildren().add(taskEntry.getEntryDisplay());
                    } else {
                        dreamsTaskList.getChildren().add(taskEntry.getEntryDisplay());
                    }
                    break;
                }

                default: {
                    System.out.println(MESSAGE_UNABLE_TO_DETERMINE_TYPE);
                }
            }
        }
        updateDisplay();
    }
    
    /**
     * Updates the display using fade transitions.
     * When the program is first initialised, all categories are faded in and shown.
     * After the user executes a command, empty categories are faded out and
     * non-empty categories are faded in.
     */
    private void updateDisplay() {
        setMainVBoxBackgroundImage(FX_BACKGROUND_IMAGE_NULL);
        if (isInitialise) {
            SequentialTransition sequentialTransition = generateInitialSequentialTransition();
            sequentialTransition.play();
            isInitialise = false;
        } else {
            SequentialTransition sequentialTransition = new SequentialTransition();
            // Update the 4 usual categories onto screen
            for (Node node : nodeList) {
                // Remove empty nodes if it is on screen, else add non-empty nodes back into screen.
                if (node.equals(thisWeekNode)) {
                    // Need to take care of special case with 'This Week' category
                    ParallelTransition parallelTransition = new ParallelTransition();
                    int totalTasksThisWeek = 0;
                    for (int i=0; i<thisWeekSubcategories.size(); i++) {
                        totalTasksThisWeek += thisWeekSubcategories.get(i).getChildren().size();
                        // Each element of subcategoryVisibilityTracker corresponds to the subcategory at a particular
                        // index, '0' indicates visible/faded in and '1' indicates it has been faded out previously.
                        if (thisWeekSubcategories.get(i).getChildren().isEmpty()) {
                            // 2 cases, either it has been faded in or not faded in previously.
                            if (subcategoryVisibilityTracker[i] == 1) {
                                // If faded out previously/not faded in yet, just remove away from the view
                                thisWeekTaskList.getChildren().remove(thisWeekSubcategories.get(i).getParent());
                            } else {
                                // If faded in, set it up to fade out since it has been emptied.
                                Node parentNode = thisWeekSubcategories.get(i).getParent();
                                FadeTransition fadeOut = generateFadeOutTransition(parentNode);
                                fadeOut.setOnFinished(done -> thisWeekTaskList.getChildren().remove(parentNode));
                                parallelTransition.getChildren().add(fadeOut);
                                subcategoryVisibilityTracker[i] = 1;
                            }
                        } else if (!(thisWeekSubcategories.get(i).getChildren().isEmpty()) && (subcategoryVisibilityTracker[i] == 1)) {
                            // All non-empty and faded out should be faded back in.
                            FadeTransition fadeIn = generateFadeInTransition(thisWeekSubcategories.get(i).getParent());
                            parallelTransition.getChildren().add(fadeIn);
                            subcategoryVisibilityTracker[i] = 0;
                        } else {
                            // Other cases can just ignore.
                            continue;
                        }
                    }
                    // Next, to settle the main parent node for all the subcategories
                    if (totalTasksThisWeek == 0 && mainVBox.getChildren().contains(thisWeekNode)) {
                        // If there are no tasks within all the subcategories, remove the node if it is contained in the mainVBox
                        FadeTransition fadeOut = generateFadeOutTransition(thisWeekNode);
                        fadeOut.setOnFinished(done -> mainVBox.getChildren().remove(thisWeekNode));
                        sequentialTransition.getChildren().add(parallelTransition);
                        sequentialTransition.getChildren().add(fadeOut);
                    } else if (totalTasksThisWeek != 0 && !mainVBox.getChildren().contains(thisWeekNode)){
                        // Else if there are some tasks and yet it is not contained in the mainVBox, fade it in.
                        FadeTransition fadeIn = generateFadeInTransition(thisWeekNode);
                        addNodeBackToScreen(thisWeekNode);
                        sequentialTransition.getChildren().add(fadeIn);
                    } else {
                        // Else just fade the subcategories
                        sequentialTransition.getChildren().add(parallelTransition);
                    }
                // The next two clauses deal with the 'Overdue' and 'Future' categories.
                } else if (((VBox) node.lookup(SELECTOR_CATEGORY_VBOX)).getChildren().isEmpty()) {
                    if (mainVBox.getChildren().contains(node)) {
                        FadeTransition fadeOut = generateFadeOutTransition(node);
                        fadeOut.setOnFinished(done -> mainVBox.getChildren().remove(node));
                        sequentialTransition.getChildren().add(fadeOut);
                    }
                } else {
                    if (!mainVBox.getChildren().contains(node)) {
                        FadeTransition fadeIn = generateFadeInTransition(node);
                        addNodeBackToScreen(node);
                        sequentialTransition.getChildren().add(fadeIn);
                    }
                }
            }
            // 'Done' category requires special handling since it is not always shown, only when its list is full
            if (doneTaskList.getChildren().isEmpty()) {
                if (mainVBox.getChildren().contains(doneNode)) {
                    FadeTransition fadeOut = generateFadeOutTransition(doneNode);
                    fadeOut.setOnFinished(done -> mainVBox.getChildren().remove(doneNode));
                    sequentialTransition.getChildren().add(fadeOut);
                }
            } else {
                if (!mainVBox.getChildren().contains(doneNode)) {
                    FadeTransition fadeIn = generateFadeInTransition(doneNode);
                    mainVBox.getChildren().add(doneNode);
                    sequentialTransition.getChildren().add(fadeIn);
                }
            }

            sequentialTransition.setOnFinished(checkEmpty -> checkIfMainVBoxIsEmpty());
            sequentialTransition.play();
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
                if (task.isDone()) {
                    doneTaskList.getChildren().add(taskEntry.getEntryDisplay());
                } else if (date.before(currentDate)) {
                    overdueTaskList.getChildren().add(taskEntry.getEntryDisplay());
                } else if (date.before(endOfWeek)) {
                    addThisWeekTask(taskEntry, date);
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
                                + timeFormat.format(endDate);
                } else {
                    dateString = dateFormatWithYear.format(date)
                                + EVENT_DATE_SEPARATOR
                                + dateFormatWithYear.format(endDate);
                }
                TaskEntry taskEntry = new TaskEntry(taskCountFormatted.get(), task.getDescription(), dateString);
                if (task.isDone()) {
                    doneTaskList.getChildren().add(taskEntry.getEntryDisplay());
                } else if (date.before(currentDate)) {
                    overdueTaskList.getChildren().add(taskEntry.getEntryDisplay());
                } else if (date.before(endOfWeek)) {
                    addThisWeekTask(taskEntry, date);
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
                if (task.isDone()) {
                    doneTaskList.getChildren().add(taskEntry.getEntryDisplay());
                } else if (date.before(today)) {
                    overdueTaskList.getChildren().add(taskEntry.getEntryDisplay());
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
                if (task.isDone()) {
                    doneTaskList.getChildren().add(taskEntry.getEntryDisplay());
                } else if (date.before(today)) {
                    overdueTaskList.getChildren().add(taskEntry.getEntryDisplay());
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

    private void addThisWeekTask(TaskEntry taskEntry, Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.add(Calendar.DATE, 1);
        Date deadline = calendar.getTime();
        boolean isAdded = false;
        for (VBox vBox : thisWeekSubcategories) {
            if (date.before(deadline)) {
                vBox.getChildren().add(taskEntry.getEntryDisplay());
                isAdded = true;
                break;
            } else {
                calendar.add(Calendar.DATE, 1);
                deadline = calendar.getTime();
            }
        }
        if (!isAdded) {
            futureTaskList.getChildren().add(taskEntry.getEntryDisplay());
        }
    }

    // ================================================================================
    // Animation methods
    // ================================================================================

    /**
     * Generates a SequentialTransition of the 4 main category boxes being faded in one by one
     * @return the transition generated
     */
    private SequentialTransition generateInitialSequentialTransition() {
        SequentialTransition sequentialTransition = new SequentialTransition();
        for (Node node : nodeList) {
            FadeTransition transition = new FadeTransition(Duration.millis(TIME_INITIALISE_FADE), node);
            transition.setFromValue(OPACITY_ZERO);
            transition.setToValue(OPACITY_FULL);
            transition.setInterpolator(Interpolator.EASE_IN);
            mainVBox.getChildren().add(node);
            sequentialTransition.getChildren().add(transition);
        }
        return sequentialTransition;
    }

    private FadeTransition generateFadeInTransition(Node node) {
        FadeTransition transition = new FadeTransition(Duration.millis(TIME_TRANSITION_FADE_IN), node);
        transition.setFromValue(OPACITY_ZERO);
        transition.setToValue(OPACITY_FULL);
        transition.setInterpolator(Interpolator.EASE_IN);
        return transition;
    }

    private FadeTransition generateFadeOutTransition(Node node) {
        FadeTransition transition = new FadeTransition(Duration.millis(TIME_TRANSITION_FADE_OUT), node);
        transition.setFromValue(OPACITY_FULL);
        transition.setToValue(OPACITY_ZERO);
        transition.setInterpolator(Interpolator.EASE_IN);
        return transition;
    }

    /**
     * Determines the correct position for each node to be added back to
     * @param node to be added
     */
    private void addNodeBackToScreen(Node node) {
        String nodeName = determineNodeName(node);
        switch (nodeName) {

            case CATEGORY_OVERDUE: {
                // Just need to add to the front
                mainVBox.getChildren().add(0, node);
                break;
            }

            case CATEGORY_THIS_WEEK: {
                // Needs to check visibility of 'Overdue'
                if (overdueNodeOpacity.get() == OPACITY_ZERO) {
                    // If not visible, add to the top
                    mainVBox.getChildren().add(0, node);
                } else {
                    // Else add it after
                    mainVBox.getChildren().add(mainVBox.getChildren().indexOf(overdueNode) + 1, node);
                }
                break;
            }

            case CATEGORY_FUTURE: {
                // Most complicated and requires checking of multiple cases;
                if (dreamsNodeOpacity.get() == OPACITY_ZERO) {
                    // If 'Dream' is not visible, add to end
                    mainVBox.getChildren().add(node);
                } else if (thisWeekNodeOpacity.get() == OPACITY_FULL) {
                    // 'This Week' is also visible, use its index to help in adding
                    mainVBox.getChildren().add(mainVBox.getChildren().indexOf(thisWeekNode) + 1, node);
                } else if (overdueNodeOpacity.get() == OPACITY_FULL) {
                    // 'Overdue' is visible but 'This Week' is not visible
                    mainVBox.getChildren().add(mainVBox.getChildren().indexOf(overdueNode) + 1, node);
                } else {
                    // Only 'Dream' is visible, add it to the front
                    mainVBox.getChildren().add(0, node);
                }
                break;
            }

            case CATEGORY_DREAMS: {
                // Just need to add to the end
                mainVBox.getChildren().add(node);
                break;
            }

            default: {
                System.out.println(MESSAGE_UNABLE_TO_RECOGNISE_NODE);
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
        generateThisWeekSubcategories();
    }

    /**
     * Generates the relative date sub-headers for the remaining days of the week and places them
     * in the task list for 'This Week'.
     */
    private void generateThisWeekSubcategories() {
        LocalDateTime startingDateTime = getDateTimeStartOfToday();
        ArrayList<Node> thisWeekDateBoxes = new ArrayList<>();
        int count = 1;
        while (!(getInstantFromLocalDateTime(startingDateTime)).equals(endOfWeek.toInstant())) {
            DateBox newDateBox;
            if (count == 1) {
                newDateBox = new DateBox(SUBCATEGORY_TODAY);
            } else if (count == 2) {
                newDateBox = new DateBox(SUBCATEGORY_TOMORROW);
            } else {
                newDateBox = new DateBox(startingDateTime.getDayOfWeek().toString());
            }
            VBox newDateVBox = newDateBox.getTaskListVBox();
            thisWeekSubcategories.add(newDateVBox);
            thisWeekDateBoxes.add(newDateBox.getDateBox());
            startingDateTime = startingDateTime.plusDays(1);
            count++;
        }
        if (subcategoryVisibilityTracker == null || (subcategoryVisibilityTracker.length != thisWeekDateBoxes.size())) {
            subcategoryVisibilityTracker = new int[thisWeekDateBoxes.size()];
        }
        thisWeekTaskList.getChildren().addAll(thisWeekDateBoxes);
    }

    private void resetTaskCount() {
        taskCount.set(0);
    }

    private void resetTaskList() {
        overdueTaskList.getChildren().clear();
        thisWeekTaskList.getChildren().clear();
        futureTaskList.getChildren().clear();
        dreamsTaskList.getChildren().clear();
        doneTaskList.getChildren().clear();

        thisWeekSubcategories.clear();
    }

    private String determineNodeName(Node node) {
        if (node.equals(overdueNode)) {
            return CATEGORY_OVERDUE;
        } else if (node.equals(thisWeekNode)) {
            return CATEGORY_THIS_WEEK;
        } else if (node.equals(futureNode)) {
            return CATEGORY_FUTURE;
        } else {
            return CATEGORY_DREAMS;
        }
    }

    private void updateDates() {
        today = Date.from(getInstantFromLocalDateTime(getDateTimeStartOfToday()));    // To get today's Date at 0000hrs
        currentDate = new Date();
        endOfWeek = getEndOfWeekDate(today);
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

    private void checkIfMainVBoxIsEmpty() {
        if (FX_BACKGROUND_IMAGE_NO_TASKS == null) {
            String image = MainScreen.class.getResource(LOCATION_EMPTY_VIEW).toExternalForm();
            FX_BACKGROUND_IMAGE_NO_TASKS = "-fx-background-image: url('" + image + "');";
        }
        if (mainVBox.getChildren().isEmpty()) {
            mainVBox.setStyle(FX_BACKGROUND_IMAGE_NO_TASKS);
        }
    }

    private void setMainVBoxBackgroundImage(String value) {
        mainVBox.setStyle(value);
    }

    // ================================================================================
    // Init methods
    // ================================================================================

    /**
     * Setup the various categories that tasks can fall under
     */
    private void createCategories() {
        // Create all the different categories(by time frame) for entries to go into
        CategoryBox overdueBox = new CategoryBox(CATEGORY_OVERDUE);
        CategoryBox thisWeekBox = new CategoryBox(CATEGORY_THIS_WEEK);
        CategoryBox futureBox = new CategoryBox(CATEGORY_FUTURE);
        CategoryBox dreamsBox = new CategoryBox(CATEGORY_DREAMS);
        CategoryBox doneBox = new CategoryBox(CATEGORY_DONE);

        this.overdueNode = overdueBox.getCategoryBox();
        this.overdueTaskList = overdueBox.getTaskListVBox();
        nodeList.add(overdueNode);

        this.thisWeekNode = thisWeekBox.getCategoryBox();
        this.thisWeekTaskList = thisWeekBox.getTaskListVBox();
        nodeList.add(thisWeekNode);

        this.futureNode = futureBox.getCategoryBox();
        this.futureTaskList = futureBox.getTaskListVBox();
        nodeList.add(futureNode);

        this.dreamsNode = dreamsBox.getCategoryBox();
        this.dreamsTaskList = dreamsBox.getTaskListVBox();
        nodeList.add(dreamsNode);

        // Since 'Done' tasks are not shown on start up, it is not added into the nodeList.
        this.doneNode = doneBox.getCategoryBox();
        this.doneTaskList = doneBox.getTaskListVBox();
    }

    private void prepareForSubcategories() {
//        subcategoryVisibilityTracker =
    }

    /**
     * Creates a formatted shared task counter for use when adding tasks onto the screen
     */
    private void setupBinding() {
        taskCountString.bindBidirectional(taskCount, new NumberStringConverter());
        taskCountFormatted.bind(Bindings.concat(taskCountString).concat(UI_NUMBER_SEPARATOR));

        overdueNodeOpacity.bind(overdueNode.opacityProperty());
        thisWeekNodeOpacity.bind(thisWeekNode.opacityProperty());
        futureNodeOpacity.bind(futureNode.opacityProperty());
        dreamsNodeOpacity.bind(dreamsNode.opacityProperty());
    }
}
