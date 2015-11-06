//@@author A0121597B
package procrastinate.ui;

import java.time.LocalDateTime;
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

    // ================================================================================
    // Animation Values
    // ================================================================================
    // Time values used are in milliseconds
    private static final int TIME_TRANSITION_CATEGORY_FADE_IN = 250;
    private static final int TIME_TRANSITION_CATEGORY_FADE_OUT = 200;
    private static final int TIME_TRANSITION_SUBCATEGORY_FADE_IN = 200;
    private static final int TIME_TRANSITION_SUBCATEGORY_FADE_OUT = 150;

    private static final int TIME_TRANSITION_TASK_ENTRY_FADE_OUT = 100;

    private static final int STYLE_BACKGROUND_HIGHLIGHT_FRAME_TIME = 10;
    private static final int STYLE_BACKGROUND_HIGHLIGHT_FULL_OPACITY = 100;

    private static final String STYLE_BACKGROUND_HIGHLIGHT_FORMAT = "-fx-background-color: rgb(250,221,177, %.2f);";

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

    protected int[] subcategoryVisibilityTracker; // used to determine if the
                                                  // subcategory is to be faded
                                                  // in or out.

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
        // Create all the different categories(by time frame) for entries to go
        // into
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

    protected abstract void setBackgroundImageIfMainVBoxIsEmpty(VBox mainVBox);

    // ================================================================================
    // Screen Transition methods
    // ================================================================================

    @Override
    protected SequentialTransition getScreenSwitchOutSequence() {
        SequentialTransition sequentialTransition = new SequentialTransition();
        for (Node node : nodeList) {
            if (mainVBox.getChildren().contains(node)) {
                sequentialTransition.getChildren().add(0,
                        generateFadeOutTransition(node, TIME_TRANSITION_CATEGORY_FADE_OUT));
            }
        }
        return sequentialTransition;
    }

    @Override
    protected SequentialTransition getScreenSwitchInSequence() {
        SequentialTransition sequentialTransition = new SequentialTransition();
        for (Node node : nodeList) {
            sequentialTransition.getChildren().add(generateFadeInTransition(node, TIME_TRANSITION_CATEGORY_FADE_OUT));
        }
        return sequentialTransition;
    }

    // ================================================================================
    // TaskDisplay methods
    // ================================================================================

    /**
     * Updates the display using fade transitions. When the program is first
     * initialised, all categories are faded in and shown. After the user
     * executes a command, empty categories are faded out and non-empty
     * categories are faded in.
     */
    protected void updateDisplay() {
        // Background image will be reset back to null after each update and
        // changed to the
        // corresponding image set in the Child class after checking that there
        // are no tasks left on screen.
        setMainVBoxBackgroundImage(mainVBox, FX_BACKGROUND_IMAGE_NULL);

        SequentialTransition sequentialTransition = new SequentialTransition();
        for (Node node : nodeList) {
            // Remove empty nodes if it is on screen, else add non-empty nodes
            // back into screen.
            if (node.equals(upcomingNode)) {
                // Need to take care of special case with 'This Week' category
                ParallelTransition parallelTransition = new ParallelTransition();
                int totalTasksThisWeek = 0;
                for (int i = 0; i < upcomingSubcategories.size(); i++) {
                    totalTasksThisWeek += upcomingSubcategories.get(i).getChildren().size();
                    addOrRemoveThisWeekSubcategories(parallelTransition, i);
                }
                // Next, to settle the main parent node for all the
                // subcategories
                addOrRemoveThisWeekNode(sequentialTransition, parallelTransition, totalTasksThisWeek);
                // Next, settle all the other nodes
            } else if (((VBox) node.lookup(SELECTOR_CATEGORY_VBOX)).getChildren().isEmpty()) {
                removeNodeIfEmptyAndInDisplay(sequentialTransition, node);
            } else {
                addNodeIfNotEmptyAndNotInDisplay(sequentialTransition, node);
            }
        }
        sequentialTransition.setOnFinished(checkEmpty -> setBackgroundImageIfMainVBoxIsEmpty(mainVBox));
        sequentialTransition.play();
    }

    protected void addTaskByType(Task task) {
        Date taskDate;
        switch (task.getType()) {

            case DEADLINE: {
                taskDate = ((Deadline) task).getDate();
                addDeadlineOrEvent(task, taskDate);
                break;
            }

            case EVENT: {
                taskDate = ((Event) task).getStartDate();
                addDeadlineOrEvent(task, taskDate);
                break;
            }

            case DREAM: {
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

    private void addDeadlineOrEvent(Task task, Date taskDate) {
        boolean isSameYear;
        isSameYear = checkIfTwoDatesOfSameYear(today, taskDate);
        if (isSameYear) {
            addTaskWithSameYear(task, taskDate);
        } else {
            addTaskWithDifferentYear(task, taskDate);
        }
    }

    private void addDream(Task task, TaskEntry taskEntry) {
        if (task.isDone()) {
            doneTaskList.getChildren().add(taskEntry.getEntryDisplay());
        } else {
            dreamsTaskList.getChildren().add(taskEntry.getEntryDisplay());
        }
    }

    private void addTaskWithSameYear(Task task, Date date) {
        String dateString;

        switch (task.getType()) {

            case DEADLINE: {
                dateString = getSameYearDeadlineDateFormat(date);
                String taskCount = taskCountFormatted.get();
                TaskEntry taskEntry = new TaskEntry(taskCount, task.getDescription(), dateString, task.isDone());
                addTaskWithSameYearToTaskList(task, date, taskCount, taskEntry);
                break;
            }

            case EVENT: {
                Date endDate = ((Event) task).getEndDate();
                boolean isSameEndYear = checkIfTwoDatesOfSameYear(endDate, today);
                dateString = getDateFormatForEventWithSameStartYear(date, endDate, isSameEndYear);
                String taskCount = taskCountFormatted.get();
                TaskEntry taskEntry = new TaskEntry(taskCount, task.getDescription(), dateString, task.isDone());
                addTaskWithSameYearToTaskList(task, date, taskCount, taskEntry);
                break;
            }

            default: {
                System.out.println(MESSAGE_UNABLE_TO_DETERMINE_TYPE);
                break;
            }
        }
    }

    private void addTaskWithSameYearToTaskList(Task task, Date date, String taskCount, TaskEntry taskEntry) {
        if (task.isDone()) {
            doneTaskList.getChildren().add(taskEntry.getEntryDisplay());
        } else if (date.before(currentDate)) {
            overdueTaskList.getChildren().add(taskEntry.getEntryDisplay());
        } else if (date.before(endOfWeek)) {
            addThisWeekTask(task, date, taskCount);
        } else {
            futureTaskList.getChildren().add(taskEntry.getEntryDisplay());
        }
    }

    private void addTaskWithDifferentYear(Task task, Date date) {
        String dateString;

        switch (task.getType()) {

            case DEADLINE: {
                dateString = getDifferentYearDeadlineDateFormat(date);
                TaskEntry taskEntry = new TaskEntry(taskCountFormatted.get(), task.getDescription(), dateString,
                        task.isDone());
                addTaskWithDifferentYearToTaskList(task, date, taskEntry);
                break;
            }

            case EVENT: {
                Date endDate = ((Event) task).getEndDate();
                // if same day also should be in same line.
                boolean isSameEndYear = checkIfTwoDatesOfSameYear(date, endDate);
                dateString = getDateFormatForEventWithDifferentStartYear(date, endDate, isSameEndYear);
                TaskEntry taskEntry = new TaskEntry(taskCountFormatted.get(), task.getDescription(), dateString,
                        task.isDone());
                addTaskWithDifferentYearToTaskList(task, date, taskEntry);
                break;
            }

            default: {
                System.out.println(MESSAGE_UNABLE_TO_DETERMINE_TYPE);
                break;
            }
        }
    }

    private String getDateFormatForEventWithSameStartYear(Date date, Date endDate, boolean isSameEndYear) {
        String dateString;
        if (isSameEndYear) {
            if (checkIfStartAndEndSameDay(date, endDate)) {
                dateString = getSameYearSameDayEventDateFormat(date, endDate);
            } else {
                dateString = getSameYearDifferentDayEventDateFormat(date, endDate);
            }
        } else {
            dateString = getDifferentYearEventDateFormat(date, endDate);
        }
        return dateString;
    }

    private String getDateFormatForEventWithDifferentStartYear(Date date, Date endDate, boolean isSameEndYear) {
        String dateString;
        if (isSameEndYear && checkIfStartAndEndSameDay(date, endDate)) {
            dateString = getDifferentYearSameDayEventDateFormat(date, endDate);
        } else {
            dateString = getDifferentYearEventDateFormat(date, endDate);
        }
        return dateString;
    }

    private void addTaskWithDifferentYearToTaskList(Task task, Date date, TaskEntry taskEntry) {
        if (task.isDone()) {
            doneTaskList.getChildren().add(taskEntry.getEntryDisplay());
        } else if (date.before(today)) {
            overdueTaskList.getChildren().add(taskEntry.getEntryDisplay());
        } else {
            futureTaskList.getChildren().add(taskEntry.getEntryDisplay());
        }
    }

    /**
     * Iterates through the list of subcategories and find the corresponding
     * date of the task to go into. If it is unable to find one, it will add the
     * task into the 'Future' category instead.
     *
     * @param taskEntry
     *            to be added
     * @param date
     *            of the task due
     */
    private void addThisWeekTask(Task task, Date startDate, String taskCount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.add(Calendar.DATE, 1);
        Date deadline = calendar.getTime();

        boolean isAdded = false;
        switch (task.getType()) {

            case DEADLINE: {
                TaskEntry taskEntry = new TaskEntry(taskCount, task.getDescription(), timeFormat.format(startDate),
                        task.isDone());
                addThisWeekTaskToTaskList(startDate, calendar, deadline, isAdded, taskEntry);
                break;
            }

            case EVENT: {
                Date endDate = ((Event) task).getEndDate();
                String dateString;
                boolean isSameEndYear = checkIfTwoDatesOfSameYear(endDate, today);
                dateString = getDateFormatForEventThisWeek(startDate, endDate, isSameEndYear);
                TaskEntry taskEntry = new TaskEntry(taskCount, task.getDescription(), dateString, task.isDone());
                addThisWeekTaskToTaskList(startDate, calendar, deadline, isAdded, taskEntry);
                break;
            }

            default:
                System.out.println(MESSAGE_UNABLE_TO_DETERMINE_TYPE);
                break;
        }
    }

    private String getDateFormatForEventThisWeek(Date startDate, Date endDate, boolean isSameEndYear) {
        String dateString;
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
        return dateString;
    }

    private void addThisWeekTaskToTaskList(Date startDate, Calendar calendar, Date deadline, boolean isAdded,
            TaskEntry taskEntry) {
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

    /**
     * Determines the correct position for each node to be added back to
     *
     * @param node
     *            to be added
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
                    // Check if the 'Overdue' node is on screen or not and adds
                    // this node after it
                    mainVBox.getChildren().add(mainVBox.getChildren().indexOf(overdueNode) + 1, node);
                } else {
                    // Else this node would take precedence at the top.
                    mainVBox.getChildren().add(0, node);
                }
                break;
            }

            case CATEGORY_FUTURE: {
                if (mainVBox.getChildren().contains(overdueNode) && mainVBox.getChildren().contains(upcomingNode)) {
                    // Check if 'Overdue' and 'This Week' nodes are added
                    // before. This node takes position after them
                    mainVBox.getChildren().add(mainVBox.getChildren().indexOf(upcomingNode) + 1, node);
                } else if (mainVBox.getChildren().contains(overdueNode)
                        && !mainVBox.getChildren().contains(upcomingNode)) {
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
                // Only needs to check if there is a lower node than it, where
                // there is one 1 - doneNode.
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

    private void addOrRemoveThisWeekNode(SequentialTransition sequentialTransition,
            ParallelTransition parallelTransition, int totalTasksThisWeek) {
        if (totalTasksThisWeek == 0 && mainVBox.getChildren().contains(upcomingNode)) {
            // If there are no tasks within all the subcategories, remove the
            // node if it is contained in the mainVBox
            FadeTransition fadeOut = generateFadeOutTransition(upcomingNode, TIME_TRANSITION_CATEGORY_FADE_OUT);
            fadeOut.setOnFinished(done -> mainVBox.getChildren().remove(upcomingNode));
            sequentialTransition.getChildren().add(parallelTransition);
            sequentialTransition.getChildren().add(fadeOut);
        } else if (totalTasksThisWeek != 0 && !mainVBox.getChildren().contains(upcomingNode)) {
            // Else if there are some tasks and yet it is not contained in the
            // mainVBox, fade it in.
            FadeTransition fadeIn = generateFadeInTransition(upcomingNode, TIME_TRANSITION_CATEGORY_FADE_IN);
            addNodeBackToScreen(upcomingNode);
            sequentialTransition.getChildren().add(fadeIn);
        } else {
            // Else just fade the subcategories
            sequentialTransition.getChildren().add(parallelTransition);
        }
    }

    private void addOrRemoveThisWeekSubcategories(ParallelTransition parallelTransition, int i) {
        // Each element of subcategoryVisibilityTracker corresponds to the
        // subcategory at a particular
        // index, '0' indicates visible/faded in and '1' indicates it has been
        // faded out previously.
        if (upcomingSubcategories.get(i).getChildren().isEmpty()) {
            // 2 cases, either it has been faded in or not faded in previously.
            if (subcategoryVisibilityTracker[i] == 0) {
                // If faded out previously/not faded in yet, just remove away
                // from the view
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
            FadeTransition fadeIn = generateFadeInTransition(upcomingSubcategories.get(i).getParent(),
                    TIME_TRANSITION_SUBCATEGORY_FADE_IN);
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
    // TaskList change animation methods
    // ================================================================================

    protected FadeTransition fadeOutDeletedTaskEntry(List<Task> taskList) {
        boolean isInitialised = initialisePrevTaskList(taskList);
        boolean isDelete = isTaskChangeDelete(taskList);
        if (isInitialised && isDelete) {
            int index = findIndexOfDeletedTask(taskList);
            prevTaskList = (ArrayList<Task>) taskList;  // Update the prevTaskList
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
            prevTaskList = (ArrayList<Task>) taskList;  // Update the prevTaskList
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

    private Timeline generateHighlightTimeline(GridPane newTaskEntry) {
        Timeline highlightTimeline = new Timeline();

        for (int i = STYLE_BACKGROUND_HIGHLIGHT_FULL_OPACITY; i >= 0; i--) {
            float backgroundColorOpacity = (float) i / STYLE_BACKGROUND_HIGHLIGHT_FULL_OPACITY;
            String styleBackgroundColor = String.format(STYLE_BACKGROUND_HIGHLIGHT_FORMAT, backgroundColorOpacity);
            highlightTimeline.getKeyFrames().add(
                                    new KeyFrame(highlightTimeline.getTotalDuration().add(Duration.millis(STYLE_BACKGROUND_HIGHLIGHT_FRAME_TIME)),
                                    new KeyValue(newTaskEntry.styleProperty(), styleBackgroundColor, Interpolator.EASE_BOTH)));
        }
        return highlightTimeline;
    }

    private int findIndexOfDeletedTask(List<Task> taskList) {
        Task prevTaskListTask;
        Task currTaskListTask;
        for (int i = 0; i<taskList.size(); i++) {
            currTaskListTask = taskList.get(i);
            prevTaskListTask = prevTaskList.get(i);
            if (currTaskListTask.equals(prevTaskListTask) && currTaskListTask.getId().equals(prevTaskListTask.getId())) {
                continue;
            } else {
                return i;
            }
        }
        return taskList.size();
    }

    private int findIndexOfAddedOrEditedTask(List<Task> taskList) {
            List<Task> filteredTaskList = new ArrayList<>(taskList);
            // To retrieve the newly added/edited task
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

    /**
     * Used when updating the task list, removes all tasks and resets the task
     * counter
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
     * Generates the relative date sub-headers for the remaining days of the
     * week and places them in the task list for 'This Week'.
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
                newDateBox = new DateBox(
                        startingDateTime.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault()));
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
}
