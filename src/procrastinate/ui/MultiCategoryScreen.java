//@@author A0121597B
package procrastinate.ui;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import procrastinate.task.Deadline;
import procrastinate.task.Event;
import procrastinate.task.Task;

/**
 * <h1>A subclass of CenterScreen and contains multiple categories in their
 * respective CategoryBox.</h1>
 *
 * There are 5 different categories - Overdue, Upcoming, Future, Dreams and Done.
 *
 * <p>It is important to note that the 'Upcoming' CategoryBox contains
 * SubcategoryBox as its children and the different TaskEntry should be
 * added into the SubcategoryBox instead.
 */
public abstract class MultiCategoryScreen extends CenterScreen {

    // ================================================================================
    // Message Strings
    // ================================================================================

    private static final String CATEGORY_OVERDUE = "Overdue";
    private static final String CATEGORY_UPCOMING = "Upcoming";
    private static final String CATEGORY_FUTURE = "Future";
    private static final String CATEGORY_DREAMS = "Dreams";
    private static final String CATEGORY_DONE = "Done";

    private static final String SUBCATEGORY_TODAY = "Today";
    private static final String SUBCATEGORY_TOMORROW = "Tomorrow";

    // ================================================================================
    // Animation Values
    // ================================================================================
    // Time values used are in milliseconds
    private static final int TIME_TRANSITION_CATEGORY_FADE_IN = 250;
    private static final int TIME_TRANSITION_CATEGORY_FADE_OUT = 200;
    private static final int TIME_TRANSITION_SUBCATEGORY_FADE_IN = 200;
    private static final int TIME_TRANSITION_SUBCATEGORY_FADE_OUT = 150;

    private static final int TIME_TRANSITION_TASK_ENTRY_FADE_OUT = 100;

    private static final int TIME_TRANSITION_SCREEN_SWITCH_IN = 500;
    private static final int TIME_TRANSITION_SCREEN_SWITCH_OUT = 150;

    private static final int STYLE_BACKGROUND_HIGHLIGHT_FRAME_TIME = 10;
    private static final int STYLE_BACKGROUND_HIGHLIGHT_FULL_OPACITY = 100;

    private static final double STYLE_BACKGROUND_HIGHLIGHT_RATE = 0.8;

    private static final String STYLE_BACKGROUND_HIGHLIGHT_FORMAT = "-fx-background-color: rgb(250,221,177, %.2f);";

    // ================================================================================
    // Class Variables
    // ================================================================================
    // Nodes are used to add them onto the screen
    protected Node overdueNode;
    protected Node upcomingNode;
    protected Node futureNode;
    protected Node dreamsNode;
    protected Node doneNode;

    protected ArrayList<Node> nodeList = new ArrayList<>();
    protected ArrayList<VBox> upcomingSubcategories = new ArrayList<>();

    // Used to determine if the subcategory is to be faded in or out.
    // Each element of subcategoryVisibilityTracker corresponds to the subcategory at a particular index,
    // '0' indicates visible/faded in and '1' indicates it has been faded out previously.
    protected int[] subcategoryVisibilityTracker;

    // The main variables to call when adding tasks since they act as a task
    // list for a TaskEntry to be displayed
    protected VBox overdueTaskList;
    protected VBox upcomingTaskList;
    protected VBox futureTaskList;
    protected VBox dreamsTaskList;
    protected VBox doneTaskList;

    protected Date today;
    protected Date currentDate;
    protected Date endOfWeek;

    protected VBox mainVBox;

    // Used for tracking changes and animating add/edit/deletes
    protected ArrayList<Task> prevTaskList;

    // ================================================================================
    // MultiCategoryScreen Constructor
    // ================================================================================

    protected MultiCategoryScreen() {
        super();
        createCategories();
        retrieveFxmlElements();
    }

    // ================================================================================
    // MultiCategoryScreen Methods
    // ================================================================================

    protected abstract void setBackgroundImageIfMainVBoxIsEmpty(VBox mainVBox);

    @Override
    protected SequentialTransition getScreenSwitchOutSequence() {
        SequentialTransition switchOutTransition = new SequentialTransition();

        for (Node node : nodeList) {
            if (mainVBox.getChildren().contains(node)) {
                switchOutTransition.getChildren().add(0, generateFadeOutTransition(node, TIME_TRANSITION_SCREEN_SWITCH_OUT));
            }
        }

        return switchOutTransition;
    }

    @Override
    protected ParallelTransition getScreenSwitchInSequence() {
        ParallelTransition switchInTransition = new ParallelTransition();

        for (Node node : nodeList) {
            if (mainVBox.getChildren().contains(node)) {
                switchInTransition.getChildren().add(generateFadeInTransition(node, TIME_TRANSITION_SCREEN_SWITCH_IN));
            }
        }

        return switchInTransition;
    }

    /**
     * Used when updating the task list, removes all tasks and resets the task counter
     */
    protected void clearTaskList() {
        resetTaskCount();
        resetTaskList();

        generateThisWeekSubcategories();
    }

    protected void getUpdatedDates() {
        updateDates();

        today = getToday();
        endOfWeek = getEndOfWeek();
        currentDate = getCurrentDate();
    }

    // ================================================================================
    // Task Adding Methods
    // ================================================================================

    protected void addTaskByType(Task task) {
        Date taskDate;
        switch (task.getType()) {

            case DEADLINE : {
                taskDate = ((Deadline) task).getDate();
                addDeadlineOrEvent(task, taskDate);
                break;
            }

            case EVENT : {
                taskDate = ((Event) task).getStartDate();
                addDeadlineOrEvent(task, taskDate);
                break;
            }

            case DREAM : {
                TaskEntry taskEntry = new TaskEntry(taskCountFormatted.get(), task.getDescription(), task.isDone());
                addDream(task, taskEntry);
                break;
            }

            default: {
                System.out.println(MESSAGE_UNABLE_TO_DETERMINE_TYPE);
                break;
            }
        }
    }

    // After tasks are filtered by type, it is filtered by the year of the (start) date
    private void addDeadlineOrEvent(Task task, Date taskDate) {
        boolean isSameStartYear = isSameYear(today, taskDate);

        if (isSameStartYear) {
            addSameStartYearTask(task, taskDate);
        } else {
            addDifferentStartYearTask(task, taskDate);
        }
    }

    private void addDream(Task task, TaskEntry taskEntry) {
        if (task.isDone()) {
            doneTaskList.getChildren().add(taskEntry.getEntryDisplay());
        } else {
            dreamsTaskList.getChildren().add(taskEntry.getEntryDisplay());
        }
    }

    private void addSameStartYearTask(Task task, Date date) {
        String dateString;

        switch (task.getType()) {

            case DEADLINE : {
                dateString = getDateFormatForDeadlineWithSameYear(date);

                String taskCount = taskCountFormatted.get();
                TaskEntry taskEntry = new TaskEntry(taskCount, task.getDescription(), dateString, task.isDone());

                addSameStartYearTaskToTaskList(task, date, taskCount, taskEntry);
                break;
            }

            case EVENT : {
                Date endDate = ((Event) task).getEndDate();
                boolean isSameEndYear = isSameYear(today, endDate);
                dateString = getDateFormatForEventWithSameStartYear(date, endDate, isSameEndYear);

                String taskCount = taskCountFormatted.get();
                TaskEntry taskEntry = new TaskEntry(taskCount, task.getDescription(), dateString, task.isDone());

                addSameStartYearTaskToTaskList(task, date, taskCount, taskEntry);
                break;
            }

            default: {
                System.out.println(MESSAGE_UNABLE_TO_DETERMINE_TYPE);
                break;
            }
        }
    }

    private void addSameStartYearTaskToTaskList(Task task, Date date, String taskCount, TaskEntry taskEntry) {
        if        (task.isDone()) {
            doneTaskList.getChildren().add(taskEntry.getEntryDisplay());

        } else if (date.before(currentDate)) {
            overdueTaskList.getChildren().add(taskEntry.getEntryDisplay());

        } else if (date.before(endOfWeek)) {
            addUpcomingTask(task, date, taskCount);

        } else {
            futureTaskList.getChildren().add(taskEntry.getEntryDisplay());
        }
    }

    private void addDifferentStartYearTask(Task task, Date date) {
        String dateString;

        switch (task.getType()) {

            case DEADLINE : {
                dateString = getDateFormatForDeadlineWithDifferentYear(date);

                TaskEntry taskEntry = new TaskEntry(taskCountFormatted.get(), task.getDescription(), dateString, task.isDone());

                addDifferentStartYearTaskToTaskList(task, date, taskEntry);
                break;
            }

            case EVENT : {
                Date endDate = ((Event) task).getEndDate();
                boolean isSameEndYear = isSameYear(date, endDate);

                dateString = getDateFormatForEventWithDifferentStartYear(date, endDate, isSameEndYear);
                TaskEntry taskEntry = new TaskEntry(taskCountFormatted.get(), task.getDescription(), dateString, task.isDone());

                addDifferentStartYearTaskToTaskList(task, date, taskEntry);
                break;
            }

            default: {
                System.out.println(MESSAGE_UNABLE_TO_DETERMINE_TYPE);
                break;
            }
        }
    }

    private void addDifferentStartYearTaskToTaskList(Task task, Date date, TaskEntry taskEntry) {
        if        (task.isDone()) {
            doneTaskList.getChildren().add(taskEntry.getEntryDisplay());

        } else if (date.before(today)) {
            overdueTaskList.getChildren().add(taskEntry.getEntryDisplay());

        } else {
            futureTaskList.getChildren().add(taskEntry.getEntryDisplay());
        }
    }

    // Iterates through the list of subcategories and find the corresponding
    // date of the task to go into. If it is unable to find one, it will add the
    // task into the 'Future' category instead.
    private void addUpcomingTask(Task task, Date startDate, String taskCount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.add(Calendar.DATE, 1);

        Date deadline = calendar.getTime();

        switch (task.getType()) {

            case DEADLINE : {
                TaskEntry taskEntry = new TaskEntry(taskCount, task.getDescription(), timeFormatter.format(startDate), task.isDone());

                addThisUpcomingTaskToTaskList(startDate, calendar, deadline, taskEntry);
                break;
            }

            case EVENT : {
                Date endDate = ((Event) task).getEndDate();
                boolean isSameEndYear = isSameYear(endDate, today);

                String dateString = getDateFormatForEventThisWeek(startDate, endDate, isSameEndYear);
                TaskEntry taskEntry = new TaskEntry(taskCount, task.getDescription(), dateString, task.isDone());

                addThisUpcomingTaskToTaskList(startDate, calendar, deadline, taskEntry);
                break;
            }

            default:
                System.out.println(MESSAGE_UNABLE_TO_DETERMINE_TYPE);
                break;
        }
    }

    private void addThisUpcomingTaskToTaskList(Date startDate, Calendar calendar, Date deadline, TaskEntry taskEntry) {
        boolean isAdded = false;

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
    }

    private String getDateFormatForEventWithSameStartYear(Date date, Date endDate, boolean isSameEndYear) {
        String dateString;

        if (isSameEndYear) {

            if (isSameDay(date, endDate)) {
                dateString = getDateFormatForEventWithSameYearAndInOneDay(date, endDate);
            } else {
                dateString = getDateFormatForEventWithSameYearAndDifferentDays(date, endDate);
            }

        } else {
            dateString = getDateFormatForEventWithDifferentYearAndDifferentDays(date, endDate);
        }

        return dateString;
    }

    private String getDateFormatForEventWithDifferentStartYear(Date date, Date endDate, boolean isSameEndYear) {
        String dateString;

        if (isSameEndYear && isSameDay(date, endDate)) {
            dateString = getDateFormatForEventWithDifferentYearButInOneDay(date, endDate);
        } else {
            dateString = getDateFormatForEventWithDifferentYearAndDifferentDays(date, endDate);
        }

        return dateString;
    }

    private String getDateFormatForEventThisWeek(Date startDate, Date endDate, boolean isSameEndYear) {
        String dateString;

        if (isSameEndYear) {

            if        (isSameDay(startDate, endDate)) {
                dateString = getDateFormatForUpcomingEventAndInOneDay(startDate, endDate);

            } else if (endDate.before(endOfWeek)) {
                dateString = getDateFormatForUpcomingEventButDifferentDays(startDate, endDate);

            } else {
                dateString = getDateFormatForUpcomingEventButDifferentWeek(startDate, endDate);
            }

        } else {
            dateString = getDateFormatForUpcomingEventButDifferentYear(startDate, endDate);
        }

        return dateString;
    }

    // ================================================================================
    // Task Display methods
    // ================================================================================

    /**
     * Updates the display using fade transitions. When the screen is first
     * initialised, all categories are faded in and shown. After the user
     * executes a command, empty categories are faded out and non-empty
     * categories are faded in.
     */
    protected void updateDisplay() {
        setMainVBoxBackgroundImage(mainVBox, FX_BACKGROUND_IMAGE_NULL);

        SequentialTransition sequentialTransition = new SequentialTransition();
        for (Node node : nodeList) {
            // Need to take care of special case with 'Upcoming' category
            if (node.equals(upcomingNode)) {
                ParallelTransition parallelTransition = new ParallelTransition();

                int totalUpcomingTasks = 0;
                for (int i = 0; i < upcomingSubcategories.size(); i++) {
                    totalUpcomingTasks += upcomingSubcategories.get(i).getChildren().size();
                    addOrRemoveUpcomingSubcategories(parallelTransition, i);
                }

                // Next, to settle the main parent node for all the subcategories
                addOrRemoveUpcomingNode(sequentialTransition, parallelTransition, totalUpcomingTasks);

            } else if (((VBox) node.lookup(SELECTOR_CATEGORY_VBOX)).getChildren().isEmpty()) {
                removeNodeIfEmptyAndInDisplay(sequentialTransition, node);
            } else {
                addNodeIfNotEmptyAndNotInDisplay(sequentialTransition, node);
            }
        }
        sequentialTransition.setOnFinished(checkEmpty -> setBackgroundImageIfMainVBoxIsEmpty(mainVBox));
        sequentialTransition.play();
    }

    /**
     * Determines the correct position for each node and adds it back.
     *
     * @param node    to be added
     */
    private void addNodeBackToScreen(Node node) {
        String nodeName = determineNodeName(node);

        switch (nodeName) {

            // This node is always at the top.
            case CATEGORY_OVERDUE : {
                mainVBox.getChildren().add(0, node);
                break;
            }

            // Check if the 'Overdue' node is on screen or not and adds this node after it.
            // Else this node would take precedence at the top.
            case CATEGORY_UPCOMING : {
                if (mainVBox.getChildren().contains(overdueNode)) {
                    mainVBox.getChildren().add(mainVBox.getChildren().indexOf(overdueNode) + 1, node);
                } else {
                    mainVBox.getChildren().add(0, node);
                }
                break;
            }

            // Check if 'Overdue' and 'This Week' nodes are added before. This node takes position after them.
            // Then check if either one is available. Else it will go to the top.
            case CATEGORY_FUTURE : {
                if        (mainVBox.getChildren().contains(overdueNode) && mainVBox.getChildren().contains(upcomingNode)) {
                    mainVBox.getChildren().add(mainVBox.getChildren().indexOf(upcomingNode) + 1, node);

                } else if (mainVBox.getChildren().contains(overdueNode) && !mainVBox.getChildren().contains(upcomingNode)) {
                    mainVBox.getChildren().add(mainVBox.getChildren().indexOf(overdueNode) + 1, node);

                } else if (mainVBox.getChildren().contains(upcomingNode)) {
                    mainVBox.getChildren().add(mainVBox.getChildren().indexOf(upcomingNode) + 1, node);

                } else {
                    mainVBox.getChildren().add(0, node);
                }
                break;
            }

            // Only needs to check if the only Node that can be lower than it (doneNode) is on the screen.
            case CATEGORY_DREAMS : {
                if (mainVBox.getChildren().contains(doneNode)) {
                    mainVBox.getChildren().add(mainVBox.getChildren().indexOf(doneNode), node);
                } else {
                    mainVBox.getChildren().add(node);
                }
                break;
            }

            // Takes position at the bottom of the list
            case CATEGORY_DONE : {
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

    private void addOrRemoveUpcomingNode(SequentialTransition sequentialTransition,
                                         ParallelTransition parallelTransition, int totalUpcomingTasks) {

        if (totalUpcomingTasks == 0 && mainVBox.getChildren().contains(upcomingNode)) {
            FadeTransition fadeOut = generateFadeOutTransition(upcomingNode, TIME_TRANSITION_CATEGORY_FADE_OUT);

            fadeOut.setOnFinished(done -> mainVBox.getChildren().remove(upcomingNode));

            sequentialTransition.getChildren().add(parallelTransition);
            sequentialTransition.getChildren().add(fadeOut);

        } else if (totalUpcomingTasks != 0 && !mainVBox.getChildren().contains(upcomingNode)) {
            FadeTransition fadeIn = generateFadeInTransition(upcomingNode, TIME_TRANSITION_CATEGORY_FADE_IN);

            addNodeBackToScreen(upcomingNode);

            sequentialTransition.getChildren().add(fadeIn);

        } else {
            sequentialTransition.getChildren().add(parallelTransition);
        }
    }

    private void addOrRemoveUpcomingSubcategories(ParallelTransition parallelTransition, int currSubcategoryIndex) {
        // 2 cases, either it has been faded in or not faded in previously.
        if (upcomingSubcategories.get(currSubcategoryIndex).getChildren().isEmpty()) {

            // If faded out previously/not faded in yet, just remove away from the view
            if (subcategoryVisibilityTracker[currSubcategoryIndex] == 0) {
                upcomingTaskList.getChildren().remove(upcomingSubcategories.get(currSubcategoryIndex).getParent());

            // If faded in, set it up to fade out since it has been emptied.
            } else {
                Node parentNode = upcomingSubcategories.get(currSubcategoryIndex).getParent();

                FadeTransition fadeOut = generateFadeOutTransition(parentNode, TIME_TRANSITION_SUBCATEGORY_FADE_OUT);
                fadeOut.setOnFinished(done -> upcomingTaskList.getChildren().remove(parentNode));

                parallelTransition.getChildren().add(fadeOut);
                subcategoryVisibilityTracker[currSubcategoryIndex] = 0;
            }

        // All non-empty and faded out should be faded back in.
        } else if (!(upcomingSubcategories.get(currSubcategoryIndex).getChildren().isEmpty()) &&
                    (subcategoryVisibilityTracker[currSubcategoryIndex] == 0)) {
            FadeTransition fadeIn = generateFadeInTransition(upcomingSubcategories.get(currSubcategoryIndex).getParent(),
                                                             TIME_TRANSITION_SUBCATEGORY_FADE_IN);
            parallelTransition.getChildren().add(fadeIn);
            subcategoryVisibilityTracker[currSubcategoryIndex] = 1;

        // Other cases can just ignore.
        } else {
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
    // TaskList Change Animation Methods
    // ================================================================================

    protected FadeTransition fadeOutDeletedTaskEntry(List<Task> taskList) {
        boolean isInitialised = initialisePrevTaskList(taskList);
        boolean isDelete = isTaskChangeDelete(taskList);

        if (isInitialised && isDelete) {
            int index = findIndexOfDeletedTask(taskList);
            prevTaskList = (ArrayList<Task>) taskList;
            return fadeOutDeletedTask(index);
        }

        return new FadeTransition();
    }

    protected void highlightAddedOrEditedTaskEntry(List<Task> taskList) {
        boolean isInitialised = initialisePrevTaskList(taskList);
        boolean isDelete = isTaskChangeDelete(taskList);

        if (isInitialised && !isDelete) {
            int index = findIndexOfAddedOrEditedTask(taskList);
            highlightTask(index);
            prevTaskList = (ArrayList<Task>) taskList;
        }
    }

    private FadeTransition fadeOutDeletedTask(int index) {
        int prevCount = 0;
        int currCount = 0;
        int indexOfTaskEntry = -1;

        for (Node node : nodeList) {

            if (node == upcomingNode) {
                prevCount = currCount;
                currCount = currCount + findNumberOfTasksInUpcomingSubcategories();

                if (currCount > index) {
                    indexOfTaskEntry = index - prevCount;
                    for (VBox vBox : upcomingSubcategories) {
                        if (indexOfTaskEntry < vBox.getChildren().size()) {
                            GridPane newTaskEntry = (GridPane) vBox.getChildren().get(indexOfTaskEntry);

                            return generateFadeOutTransition(newTaskEntry, TIME_TRANSITION_TASK_ENTRY_FADE_OUT);
                        }

                        indexOfTaskEntry -= vBox.getChildren().size();
                    }
                    break;
                }

            } else {
                prevCount = currCount;
                VBox currTaskList = ((VBox) node.lookup(SELECTOR_CATEGORY_VBOX));
                currCount = currCount + currTaskList.getChildren().size();

                if (currCount > index) {
                    indexOfTaskEntry = index - prevCount;

                    GridPane newTaskEntry = (GridPane) currTaskList.getChildren().get(indexOfTaskEntry);

                    return generateFadeOutTransition(newTaskEntry, TIME_TRANSITION_TASK_ENTRY_FADE_OUT);
                }
            }
        }
        return new FadeTransition();
    }

    private void highlightTask(int index) {
        if (index == -1) {
            return;
        }

        int prevCount = 0;
        int currCount = 0;
        int indexOfTaskEntry = -1;

        for (Node node : nodeList) {
            if (node == doneNode) {
                break;
            }

            if (node == upcomingNode) {
                prevCount = currCount;
                currCount = currCount + findNumberOfTasksInUpcomingSubcategories();

                if (currCount > index) {
                    indexOfTaskEntry = index - prevCount;
                    for (VBox vBox : upcomingSubcategories) {
                        if (indexOfTaskEntry < vBox.getChildren().size()) {
                            GridPane newTaskEntry = (GridPane) vBox.getChildren().get(indexOfTaskEntry);

                            Timeline highlightTimeline = generateHighlightTimeline(newTaskEntry);
                            highlightTimeline.play();
                            break;
                        }

                        indexOfTaskEntry -= vBox.getChildren().size();
                    }
                    break;
                }

            } else {
                prevCount = currCount;
                VBox currTaskList = ((VBox) node.lookup(SELECTOR_CATEGORY_VBOX));
                currCount = currCount + currTaskList.getChildren().size();

                if (currCount > index) {
                    indexOfTaskEntry = index - prevCount;

                    GridPane newTaskEntry = (GridPane) currTaskList.getChildren().get(indexOfTaskEntry);

                    Timeline highlightTimeline = generateHighlightTimeline(newTaskEntry);
                    highlightTimeline.play();
                    break;
                }
            }
        }
    }

    //@@author A0121597B-reused
    private Timeline generateHighlightTimeline(GridPane newTaskEntry) {
        Timeline highlightTimeline = new Timeline();

        for (int i = STYLE_BACKGROUND_HIGHLIGHT_FULL_OPACITY; i >= 0; i--) {
            float backgroundColorOpacity = (float) i / STYLE_BACKGROUND_HIGHLIGHT_FULL_OPACITY;
            String styleBackgroundColor = String.format(STYLE_BACKGROUND_HIGHLIGHT_FORMAT, backgroundColorOpacity);

            Duration duration = highlightTimeline.getTotalDuration().add(Duration.millis(STYLE_BACKGROUND_HIGHLIGHT_FRAME_TIME));
            KeyValue keyValue = new KeyValue(newTaskEntry.styleProperty(), styleBackgroundColor, Interpolator.EASE_IN);
            KeyFrame keyFrame = new KeyFrame(duration, keyValue);

            highlightTimeline.getKeyFrames().add(keyFrame);
        }

        highlightTimeline.setRate(STYLE_BACKGROUND_HIGHLIGHT_RATE);
        return highlightTimeline;
    }

    //@@author A0121597B
    private int findIndexOfDeletedTask(List<Task> taskList) {
        Task prevTaskListTask;
        Task currTaskListTask;

        for (int i = 0; i < taskList.size(); i++) {
            currTaskListTask = taskList.get(i);
            prevTaskListTask = prevTaskList.get(i);

            // Check for the task at an index that differs between the
            // two task lists
            if (currTaskListTask.equals(prevTaskListTask) &&
                currTaskListTask.getId().equals(prevTaskListTask.getId())) {
                    continue;

            } else {
                return i;
            }
        }
        return taskList.size();
    }

    private int findIndexOfAddedOrEditedTask(List<Task> taskList) {
        List<Task> filteredTaskList = new ArrayList<>(taskList);

        // To retrieve the newly added/edited task, filter
        // the new task list to get the new task that has changed.
        for (Task task : prevTaskList) {
            filteredTaskList = filteredTaskList.stream()
                               .filter(filterTask -> (!filterTask.getId().equals(task.getId())))
                               .collect(Collectors.toList());
        }

        // To catch an edit that does not do any changes
        if (filteredTaskList.isEmpty()) {
            return -1;
        }

        Task newTask = filteredTaskList.get(0);
        for (int i = 0; i < taskList.size(); i++) {
            Task currTask = taskList.get(i);
            if (currTask.equals(newTask) && currTask.getId().equals(newTask.getId())) {
                return i;
            }
        }

        // Should be another 'show' command
        return -1;
    }

    private int findNumberOfTasksInUpcomingSubcategories() {
        int numTasks = 0;

        for (VBox vBox : upcomingSubcategories) {
            numTasks += vBox.getChildren().size();
        }

        return numTasks;
    }

    private boolean initialisePrevTaskList(List<Task> taskList) {
        if (prevTaskList == null) {
            prevTaskList = (ArrayList<Task>) taskList;
            return false;
        }
        return true;
    }

    private boolean isTaskChangeDelete(List<Task> taskList) {
        if (taskList.size() < prevTaskList.size()) {
            return true;
        } else {
            return false;
        }
    }

    // ================================================================================
    // Utility methods
    // ================================================================================

    private String determineNodeName(Node node) {
        if        (node.equals(overdueNode)) {
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
     * Generates the relative date sub-headers for the 'Upcoming' category
     * and places them in the upcomingTaskList.
     */
    private void generateThisWeekSubcategories() {
        ArrayList<Node> thisWeekDateBoxes = new ArrayList<>();
        int count = 1;

        LocalDateTime startingDateTime = getDateTimeStartOfToday();
        while (!(getInstantFromLocalDateTime(startingDateTime)).equals(endOfWeek.toInstant())) {
            SubcategoryBox newDateBox;
            String shortDate = ", " + startingDateTime.format(DateTimeFormatter.ofPattern(dateFormatter.toPattern()));

            if        (count == 1) {
                newDateBox = new SubcategoryBox(SUBCATEGORY_TODAY + shortDate);
            } else if (count == 2) {
                newDateBox = new SubcategoryBox(SUBCATEGORY_TOMORROW + shortDate);
            } else {
                newDateBox = new SubcategoryBox(startingDateTime.getDayOfWeek()
                                                .getDisplayName(TextStyle.FULL, Locale.getDefault()) +
                                                shortDate);
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

    // ================================================================================
    // Init Methods
    // ================================================================================

    @Override
    protected void createCategories() {
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

}
