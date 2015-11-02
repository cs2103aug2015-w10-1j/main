package procrastinate.ui;

import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import procrastinate.task.Deadline;
import procrastinate.task.Event;
import procrastinate.task.Task;

public abstract class MultiCategoryScreen extends CenterScreen {

    // ================================================================================
    // Message strings
    // ================================================================================

    private static final String CATEGORY_OVERDUE = "Overdue";
    private static final String CATEGORY_UPCOMING = "Upcoming";
    private static final String CATEGORY_FUTURE = "Future";
    private static final String CATEGORY_DREAMS = "Dreams";
    private static final String CATEGORY_DONE = "Done";

    private static final String SUBCATEGORY_TODAY = "Today";
    private static final String SUBCATEGORY_TOMORROW = "Tomorrow";

    // Time values used are in milliseconds
    private static final int TIME_TRANSITION_CATEGORY_FADE_IN = 250;
    private static final int TIME_TRANSITION_CATEGORY_FADE_OUT = 200;
    private static final int TIME_TRANSITION_SUBCATEGORY_FADE_IN = 200;
    private static final int TIME_TRANSITION_SUBCATEGORY_FADE_OUT = 150;

    // ================================================================================
    // Class variables
    // ================================================================================

    // Nodes are used to add them onto the screen
    protected Node overdueNode;
    protected Node upcomingNode;
    protected Node futureNode;
    protected Node dreamsNode;
    protected Node doneNode;

    protected ArrayList<Node> nodeList = new ArrayList<>();
    protected ArrayList<VBox> upcomingSubcategories = new ArrayList<>();

    protected int[] subcategoryVisibilityTracker; // used to determine if the subcategory is to be faded in or out.

    // The main variables to call when adding tasks since they act as a task list for a TaskEntry to be displayed
    protected VBox overdueTaskList;
    protected VBox upcomingTaskList;
    protected VBox futureTaskList;
    protected VBox dreamsTaskList;
    protected VBox doneTaskList;

    protected Date today;
    protected Date currentDate;
    protected Date endOfWeek;

    protected VBox mainVBox;

    // ================================================================================
    // MultiCategoryScreen Constructor
    // ================================================================================

    protected MultiCategoryScreen(String filePath) {
        super(filePath);
        createCategories();
        retrieveFxmlElements();
    }

    // ================================================================================
    // Init methods
    // ================================================================================

    /**
     * Setup the various categories that tasks can fall under
     */
    @Override
    protected void createCategories() {
        // Create all the different categories(by time frame) for entries to go into
        CategoryBox overdueBox = new CategoryBox(CATEGORY_OVERDUE);
        CategoryBox upcomingBox = new CategoryBox(CATEGORY_UPCOMING);
        CategoryBox futureBox = new CategoryBox(CATEGORY_FUTURE);
        CategoryBox dreamsBox = new CategoryBox(CATEGORY_DREAMS);
        CategoryBox doneBox = new CategoryBox(CATEGORY_DONE);

        this.overdueNode = overdueBox.getCategoryBox();
        this.overdueTaskList = overdueBox.getTaskListVBox();
        nodeList.add(overdueNode);

        this.upcomingNode = upcomingBox.getCategoryBox();
        this.upcomingTaskList = upcomingBox.getTaskListVBox();
        nodeList.add(upcomingNode);

        this.futureNode = futureBox.getCategoryBox();
        this.futureTaskList = futureBox.getTaskListVBox();
        nodeList.add(futureNode);

        this.dreamsNode = dreamsBox.getCategoryBox();
        this.dreamsTaskList = dreamsBox.getTaskListVBox();
        nodeList.add(dreamsNode);

        this.doneNode = doneBox.getCategoryBox();
        this.doneTaskList = doneBox.getTaskListVBox();
        nodeList.add(doneNode);
    }

    private void retrieveFxmlElements() {
        this.mainVBox = getMainVBox();
    }

    // ================================================================================
    // Methods to be overridden by Child
    // ================================================================================

    /**
     * The list of tasks displayed is updated by removing all previously added tasks and re-adding them back to allow
     * the line number to be sorted by category and not insertion time.
     *
     * Dreams are directly added via this method but Deadlines and Events are passed to two different
     * addTask methods depending on their (start) dates.
     * @param taskList List of Tasks to be added onto the screen
     */
    @Override
    protected abstract void updateTaskList(List<Task> taskList);

    protected abstract void checkIfMainVBoxIsEmpty(VBox mainVBox);

    // ================================================================================
    // Display methods
    // ================================================================================

    @Override
    protected SequentialTransition getScreenSwitchOutSequence() {
        SequentialTransition sequentialTransition = new SequentialTransition();
        for (Node node : nodeList) {
            if (mainVBox.getChildren().contains(node)) {
                sequentialTransition.getChildren().add(0, generateFadeOutTransition(node, TIME_TRANSITION_CATEGORY_FADE_OUT));
            }
        }
        return sequentialTransition;
    }

    @Override
    protected SequentialTransition getScreenSwitchInSequence() {
        SequentialTransition sequentialTransition = new SequentialTransition();
        for (Node node : nodeList) {
            node.setOpacity(OPACITY_FULL);
        }
        return sequentialTransition;
    }

    // ================================================================================
    // TaskDisplay methods
    // ================================================================================

    protected void addTaskByType(Task task) {
        Date taskDate;
        boolean isSameYear;
        switch (task.getType()) {

            case DEADLINE: {
                taskDate = ((Deadline) task).getDate();
                isSameYear = checkIfTwoDatesOfSameYear(today, taskDate);
                if (isSameYear) {
                    addTaskWithSameYear(task, taskDate);
                } else {
                    addTaskWithDifferentYear(task, taskDate);
                }
                break;
            }

            case EVENT: {
                taskDate = ((Event) task).getStartDate();
                isSameYear = checkIfTwoDatesOfSameYear(today, taskDate);
                if (isSameYear) {
                    addTaskWithSameYear(task, taskDate);
                } else {
                    addTaskWithDifferentYear(task, taskDate);
                }
                break;
            }

            case DREAM: {
                TaskEntry taskEntry = new TaskEntry(taskCountFormatted.get(), task.getDescription(), task.isDone());
                if (task.isDone()) {
                    doneTaskList.getChildren().add(taskEntry.getEntryDisplay());
                } else {
                    dreamsTaskList.getChildren().add(taskEntry.getEntryDisplay());
                }
                break;
            }

            default: {
                System.out.println(MESSAGE_UNABLE_TO_DETERMINE_TYPE);
                break;
            }
        }
    }

    /**
     * Updates the display using fade transitions.
     * When the program is first initialised, all categories are faded in and shown.
     * After the user executes a command, empty categories are faded out and
     * non-empty categories are faded in.
     */
    protected void updateDisplay() {
        setMainVBoxBackgroundImage(mainVBox, FX_BACKGROUND_IMAGE_NULL);

        SequentialTransition sequentialTransition = new SequentialTransition();
        for (Node node : nodeList) {
            // Remove empty nodes if it is on screen, else add non-empty nodes back into screen.
            if (node.equals(upcomingNode)) {
                // Need to take care of special case with 'This Week' category
                ParallelTransition parallelTransition = new ParallelTransition();
                int totalTasksThisWeek = 0;
                for (int i=0; i<upcomingSubcategories.size(); i++) {
                    totalTasksThisWeek += upcomingSubcategories.get(i).getChildren().size();
                    addOrRemoveThisWeekSubcategories(parallelTransition, i);
                }
                // Next, to settle the main parent node for all the subcategories
                addOrRemoveThisWeekNode(sequentialTransition, parallelTransition, totalTasksThisWeek);
            // Next, settle all the other nodes
            } else if (((VBox) node.lookup(SELECTOR_CATEGORY_VBOX)).getChildren().isEmpty()) {
                removeNodeIfEmptyAndInDisplay(sequentialTransition, node);
            } else {
                addNodeIfNotEmptyAndNotInDisplay(sequentialTransition, node);
            }
        }
        sequentialTransition.setOnFinished(checkEmpty -> checkIfMainVBoxIsEmpty(mainVBox));
        sequentialTransition.play();
    }

    private void addTaskWithSameYear(Task task, Date date) {
        String dateString;

        switch (task.getType()) {

            case DEADLINE: {
                dateString = getSameYearDeadlineDateFormat(date);
                String taskCount = taskCountFormatted.get();
                TaskEntry taskEntry = new TaskEntry(taskCount, task.getDescription(), dateString, task.isDone());
                if (task.isDone()) {
                    doneTaskList.getChildren().add(taskEntry.getEntryDisplay());
                } else if (date.before(currentDate)) {
                    overdueTaskList.getChildren().add(taskEntry.getEntryDisplay());
                } else if (date.before(endOfWeek)) {
                    addThisWeekTask(task, date, taskCount);
                } else {
                    futureTaskList.getChildren().add(taskEntry.getEntryDisplay());
                }
                break;
            }

            case EVENT: {
                Date endDate = ((Event) task).getEndDate();
                boolean isSameEndYear = checkIfTwoDatesOfSameYear(endDate, today);
                if (isSameEndYear) {
                    if (checkIfStartAndEndSameDay(date, endDate)) {
                        dateString = getSameYearSameDayEventDateFormat(date, endDate);
                    } else {
                        dateString = getSameYearDifferentDayEventDateFormat(date, endDate);
                    }
                } else {
                    dateString = getDifferentYearEventDateFormat(date, endDate);
                }
                String taskCount = taskCountFormatted.get();
                TaskEntry taskEntry = new TaskEntry(taskCount, task.getDescription(), dateString, task.isDone());
                if (task.isDone()) {
                    doneTaskList.getChildren().add(taskEntry.getEntryDisplay());
                } else if (date.before(currentDate)) {
                    overdueTaskList.getChildren().add(taskEntry.getEntryDisplay());
                } else if (date.before(endOfWeek)) {
                    addThisWeekTask(task, date, taskCount);
                } else {
                    futureTaskList.getChildren().add(taskEntry.getEntryDisplay());
                }
                break;
            }

            default: {
                System.out.println(MESSAGE_UNABLE_TO_DETERMINE_TYPE);
                break;
            }
        }
    }

    private void addTaskWithDifferentYear(Task task, Date date) {
        String dateString;

        switch (task.getType()) {

            case DEADLINE: {
                dateString = getDifferentYearDeadlineDateFormat(date);
                TaskEntry taskEntry = new TaskEntry(taskCountFormatted.get(), task.getDescription(), dateString, task.isDone());
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
                // if same day also should be in same line.
                boolean isSameEndYear = checkIfTwoDatesOfSameYear(date, endDate);
                if (isSameEndYear && checkIfStartAndEndSameDay(date, endDate)) {
                    dateString = getDifferentYearSameDayEventDateFormat(date, endDate);
                } else {
                    dateString = getDifferentYearEventDateFormat(date, endDate);
                }
                TaskEntry taskEntry = new TaskEntry(taskCountFormatted.get(), task.getDescription(), dateString, task.isDone());
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
                break;
            }
        }
    }

    /**
     * Iterates through the list of subcategories and find the corresponding date of the task to go into.
     * If it is unable to find one, it will add the task into the 'Future' category instead.
     * @param taskEntry to be added
     * @param date of the task due
     */
    private void addThisWeekTask(Task task, Date startDate, String taskCount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.add(Calendar.DATE, 1);
        Date deadline = calendar.getTime();

        boolean isAdded = false;
        switch (task.getType()) {

            case DEADLINE: {
                TaskEntry taskEntry = new TaskEntry(taskCount,
                        task.getDescription(),
                        timeFormat.format(startDate),
                        task.isDone());
                for (VBox vBox : upcomingSubcategories) {
                    if (startDate.before(deadline)) {
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
                break;
            }

            case EVENT: {
                Date endDate = ((Event) task).getEndDate();
                String dateString;
                boolean isSameEndYear = checkIfTwoDatesOfSameYear(endDate, today);
                if (isSameEndYear) {
                    if (checkIfStartAndEndSameDay(startDate, endDate)) {
                        dateString = getThisWeekSameDayEventDateFormat(startDate, endDate);
                    } else if (endDate.before(endOfWeek)) {
                    dateString = getThisWeekEndDifferentDayEventDateFormat(startDate, endDate);
                    } else {
                        dateString = getThisWeekEndDifferentWeekEventDateFormat(startDate, endDate);
                    }
                } else {
                    dateString = getThisWeekEndDifferentYearDateFormat(startDate, endDate);
                }
                TaskEntry taskEntry = new TaskEntry(taskCount, task.getDescription(), dateString, task.isDone());
                for (VBox vBox : upcomingSubcategories) {
                    if (startDate.before(deadline)) {
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
                break;
            }

            default:
                System.out.println(MESSAGE_UNABLE_TO_DETERMINE_TYPE);
                break;
        }
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

            case CATEGORY_UPCOMING: {
                if (mainVBox.getChildren().contains(overdueNode)) {
                    // Check if the 'Overdue' node is on screen or not and adds this node after it
                    mainVBox.getChildren().add(mainVBox.getChildren().indexOf(overdueNode) + 1, node);
                } else {
                    // Else this node would take precedence at the top.
                    mainVBox.getChildren().add(0, node);
                }
                break;
            }

            case CATEGORY_FUTURE: {
                if (mainVBox.getChildren().contains(overdueNode) && mainVBox.getChildren().contains(upcomingNode)) {
                    // Check if 'Overdue' and 'This Week' nodes are added before. This node takes position after them
                    mainVBox.getChildren().add(mainVBox.getChildren().indexOf(upcomingNode) + 1, node);
                } else if (mainVBox.getChildren().contains(overdueNode) && !mainVBox.getChildren().contains(upcomingNode)) {
                    // Then check if either one is available
                    mainVBox.getChildren().add(mainVBox.getChildren().indexOf(overdueNode) + 1, node);
                } else if (mainVBox.getChildren().contains(upcomingNode)) {
                    mainVBox.getChildren().add(mainVBox.getChildren().indexOf(upcomingNode) + 1, node);
                } else {
                    // Else it will go to the top
                    mainVBox.getChildren().add(0, node);
                }
                break;
            }

            case CATEGORY_DREAMS: {
                // Only needs to check if there is a lower node than it, where there is one 1 - doneNode.
                if (mainVBox.getChildren().contains(doneNode)) {
                    mainVBox.getChildren().add(mainVBox.getChildren().indexOf(doneNode), node);
                } else {
                    mainVBox.getChildren().add(node);
                }
                break;
            }

            case CATEGORY_DONE: {
                // Takes position at the bottom of the list
                mainVBox.getChildren().add(node);
                break;
            }

            default: {
                System.out.println(MESSAGE_UNABLE_TO_RECOGNISE_NODE);
                break;
            }
        }
    }

    private void addNodeIfNotEmptyAndNotInDisplay(SequentialTransition sequentialTransition, Node node) {
        if (!mainVBox.getChildren().contains(node)) {
            FadeTransition fadeIn = generateFadeInTransition(node, TIME_TRANSITION_CATEGORY_FADE_IN);
            addNodeBackToScreen(node);
            sequentialTransition.getChildren().add(fadeIn);
        }
    }

    private void addOrRemoveThisWeekNode(SequentialTransition sequentialTransition, ParallelTransition parallelTransition, int totalTasksThisWeek) {
        if (totalTasksThisWeek == 0 && mainVBox.getChildren().contains(upcomingNode)) {
            // If there are no tasks within all the subcategories, remove the node if it is contained in the mainVBox
            FadeTransition fadeOut = generateFadeOutTransition(upcomingNode, TIME_TRANSITION_CATEGORY_FADE_OUT);
            fadeOut.setOnFinished(done -> mainVBox.getChildren().remove(upcomingNode));
            sequentialTransition.getChildren().add(parallelTransition);
            sequentialTransition.getChildren().add(fadeOut);
        } else if (totalTasksThisWeek != 0 && !mainVBox.getChildren().contains(upcomingNode)){
            // Else if there are some tasks and yet it is not contained in the mainVBox, fade it in.
            FadeTransition fadeIn = generateFadeInTransition(upcomingNode, TIME_TRANSITION_CATEGORY_FADE_IN);
            addNodeBackToScreen(upcomingNode);
            sequentialTransition.getChildren().add(fadeIn);
        } else {
            // Else just fade the subcategories
            sequentialTransition.getChildren().add(parallelTransition);
        }
    }

    private void addOrRemoveThisWeekSubcategories(ParallelTransition parallelTransition, int i) {
        // Each element of subcategoryVisibilityTracker corresponds to the subcategory at a particular
        // index, '0' indicates visible/faded in and '1' indicates it has been faded out previously.
        if (upcomingSubcategories.get(i).getChildren().isEmpty()) {
            // 2 cases, either it has been faded in or not faded in previously.
            if (subcategoryVisibilityTracker[i] == 0) {
                // If faded out previously/not faded in yet, just remove away from the view
                upcomingTaskList.getChildren().remove(upcomingSubcategories.get(i).getParent());
            } else {
                // If faded in, set it up to fade out since it has been emptied.
                Node parentNode = upcomingSubcategories.get(i).getParent();
                FadeTransition fadeOut = generateFadeOutTransition(parentNode, TIME_TRANSITION_SUBCATEGORY_FADE_OUT);
                fadeOut.setOnFinished(done -> upcomingTaskList.getChildren().remove(parentNode));
                parallelTransition.getChildren().add(fadeOut);
                subcategoryVisibilityTracker[i] = 0;
            }
        } else if (!(upcomingSubcategories.get(i).getChildren().isEmpty()) && (subcategoryVisibilityTracker[i] == 0)) {
            // All non-empty and faded out should be faded back in.
            FadeTransition fadeIn = generateFadeInTransition(upcomingSubcategories.get(i).getParent(), TIME_TRANSITION_SUBCATEGORY_FADE_IN);
            parallelTransition.getChildren().add(fadeIn);
            subcategoryVisibilityTracker[i] = 1;
        } else {
            // Other cases can just ignore.
        }
    }

    private void removeNodeIfEmptyAndInDisplay(SequentialTransition sequentialTransition, Node node) {
        if (mainVBox.getChildren().contains(node)) {
            FadeTransition fadeOut = generateFadeOutTransition(node, TIME_TRANSITION_CATEGORY_FADE_OUT);
            fadeOut.setOnFinished(done -> mainVBox.getChildren().remove(node));
            sequentialTransition.getChildren().add(fadeOut);
        }
    }

    // ================================================================================
    // Utility methods
    // ================================================================================

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
                newDateBox = new DateBox(startingDateTime.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault()));
            }
            VBox newDateVBox = newDateBox.getTaskListVBox();
            upcomingSubcategories.add(newDateVBox);
            thisWeekDateBoxes.add(newDateBox.getDateBox());
            startingDateTime = startingDateTime.plusDays(1);
            count++;
        }
        if (subcategoryVisibilityTracker == null || (subcategoryVisibilityTracker.length != thisWeekDateBoxes.size())) {
            subcategoryVisibilityTracker = new int[thisWeekDateBoxes.size()];
        }
        upcomingTaskList.getChildren().addAll(thisWeekDateBoxes);
    }

    protected void getUpdatedDates() {
        updateDates();
        today = getToday();
        endOfWeek = getEndOfWeek();
        currentDate = getCurrentDate();
    }

    private String determineNodeName(Node node) {
        if (node.equals(overdueNode)) {
            return CATEGORY_OVERDUE;
        } else if (node.equals(upcomingNode)) {
            return CATEGORY_UPCOMING;
        } else if (node.equals(futureNode)) {
            return CATEGORY_FUTURE;
        } else {
            return CATEGORY_DREAMS;
        }
    }

    /**
     * Used when updating the task list, removes all tasks and resets the task counter
     */
    protected void clearTaskList() {
        resetTaskCount();
        resetTaskList();
        generateThisWeekSubcategories();
    }

    private void resetTaskCount() {
        taskCount.set(0);
    }

    private void resetTaskList() {
        overdueTaskList.getChildren().clear();
        upcomingTaskList.getChildren().clear();
        futureTaskList.getChildren().clear();
        dreamsTaskList.getChildren().clear();
        doneTaskList.getChildren().clear();

        upcomingSubcategories.clear();
    }
}
