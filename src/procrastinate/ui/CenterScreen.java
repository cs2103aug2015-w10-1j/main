//@@author A0121597B
package procrastinate.ui;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.SequentialTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.util.converter.NumberStringConverter;
import procrastinate.task.Task;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public abstract class CenterScreen extends VBox {

    // ================================================================================
    // Message Strings
    // ================================================================================

    protected static final String MESSAGE_UNABLE_TO_DETERMINE_TYPE = "Unable to determine TaskType for adding.";
    protected static final String MESSAGE_UNABLE_TO_RECOGNISE_NODE = "Unable to recognise Node.";

    protected static final String EVENT_DATE_SEPARATOR_GENERAL = "\nto ";
    protected static final String EVENT_DATE_SEPARATOR_SAME_DAY = " to ";
    protected static final String FRIENDLY_DATE_OR_TIME_SEPARATOR = " ";

    protected static final String SELECTOR_CATEGORY_VBOX = "#categoryVBox";

    protected static final String FX_BACKGROUND_IMAGE_NULL = "-fx-background-image: null;";

    private static final String UI_NUMBER_SEPARATOR = ". ";

    private static final String DATE_TODAY = "Today";
    private static final String DATE_TOMORROW = "Tomorrow";

    // ================================================================================
    // Constants
    // ================================================================================

    protected static final double OPACITY_ZERO = 0;
    protected static final double OPACITY_FULL = 1;

    private static final int NUMBER_OF_DAYS_IN_A_WEEK = 7;

    // ================================================================================
    // Class Variables
    // ================================================================================

    protected IntegerProperty taskCount = new SimpleIntegerProperty(1);
    protected StringProperty taskCountFormatted = new SimpleStringProperty();
    protected StringProperty taskCountString = new SimpleStringProperty();

    protected SimpleDateFormat dateFormatter = new SimpleDateFormat("d MMM");
    protected SimpleDateFormat timeFormatter = new SimpleDateFormat("h:mma");

    private SimpleDateFormat dateFormatterWithFriendlyDayAndYear_ = new SimpleDateFormat("EEE d MMM''yy h:mma");
    private SimpleDateFormat friendlyDayFormatter_ = new SimpleDateFormat("EEE");
    private SimpleDateFormat yearFormatter_ = new SimpleDateFormat("yyyy");

    private Node node_;

    private Date today_;
    private Date currentDate_;
    private Date endOfWeek_;

    // ================================================================================
    // FXML Field Variables
    // ================================================================================

    @FXML
    private VBox mainVBox;

    // ================================================================================
    // CenterScreen Constructor
    // ================================================================================

    protected CenterScreen(String filePath) {
        loadLayout(filePath);
        setupBinding();
    }

    // ================================================================================
    // CenterScreen Methods
    // ================================================================================

    /**
     * Setup the various categories that tasks can fall under
     */
    protected abstract void createCategories();

    /**
     * The list of tasks displayed is updated by removing all previously added
     * tasks and re-adding them back to allow the line number to be sorted by
     * category and not insertion time.
     *
     * Dreams are directly added via this method but Deadlines and Events are
     * passed to two different addTask methods depending on their (start) dates.
     *
     * @param taskList
     *            to be added onto the screen
     */
    protected abstract void updateTaskList(List<Task> taskList);

    protected abstract SequentialTransition getScreenSwitchOutSequence();

    protected abstract SequentialTransition getScreenSwitchInSequence();

    protected void setMainVBoxBackgroundImage(VBox mainVBox, String value) {
        mainVBox.setStyle(value);
    }

    /**
     * Updates the class variable Dates that are used to compare the event dates
     * and generate subcategories for 'Upcoming'
     */
    protected void updateDates() {
        today_ = Date.from(getInstantFromLocalDateTime(getDateTimeStartOfToday()));
        currentDate_ = new Date();
        endOfWeek_ = getEndOfWeekDate(today_);
    }

    // ================================================================================
    // Animation Methods
    // ================================================================================

    protected FadeTransition generateFadeInTransition(Node nodeToFade, int fadeInTime) {
        FadeTransition transition = new FadeTransition(Duration.millis(fadeInTime), nodeToFade);
        transition.setFromValue(OPACITY_ZERO);
        transition.setToValue(OPACITY_FULL);
        transition.setInterpolator(Interpolator.EASE_IN);
        return transition;
    }

    protected FadeTransition generateFadeOutTransition(Node nodeToFade, int fadeOutTime) {
        FadeTransition transition = new FadeTransition(Duration.millis(fadeOutTime), nodeToFade);
        transition.setFromValue(OPACITY_FULL);
        transition.setToValue(OPACITY_ZERO);
        transition.setInterpolator(Interpolator.EASE_IN);
        return transition;
    }

    // ================================================================================
    // Date Format Methods
    // ================================================================================

    protected String getDateFormatForDeadlineWithDifferentYear(Date date) {
        return dateFormatterWithFriendlyDayAndYear_.format(date);
    }

    protected String getDateFormatForEventWithDifferentYearButInOneDay(Date date, Date endDate) {
        return dateFormatterWithFriendlyDayAndYear_.format(date) + EVENT_DATE_SEPARATOR_SAME_DAY +
               timeFormatter.format(endDate);
    }

    protected String getDateFormatForEventWithDifferentYearAndDifferentDays(Date date, Date endDate) {
        return dateFormatterWithFriendlyDayAndYear_.format(date) + EVENT_DATE_SEPARATOR_GENERAL +
               dateFormatterWithFriendlyDayAndYear_.format(endDate);
    }

    protected String getDateFormatForDeadlineWithSameYear(Date date) {
        return getFriendlyDayFormat(date) + FRIENDLY_DATE_OR_TIME_SEPARATOR +
               dateFormatter.format(date) + FRIENDLY_DATE_OR_TIME_SEPARATOR +
               timeFormatter.format(date);
    }

    protected String getDateFormatForEventWithSameYearAndInOneDay(Date date, Date endDate) {
        return getFriendlyDayFormat(date) + FRIENDLY_DATE_OR_TIME_SEPARATOR +
               dateFormatter.format(date) + FRIENDLY_DATE_OR_TIME_SEPARATOR +
               timeFormatter.format(date) + EVENT_DATE_SEPARATOR_SAME_DAY +
               timeFormatter.format(endDate);
    }

    protected String getDateFormatForEventWithSameYearAndDifferentDays(Date date, Date endDate) {
        return getFriendlyDayFormat(date) + FRIENDLY_DATE_OR_TIME_SEPARATOR +
               dateFormatter.format(date) + FRIENDLY_DATE_OR_TIME_SEPARATOR +
               timeFormatter.format(date) + EVENT_DATE_SEPARATOR_GENERAL +
               getFriendlyDayFormat(endDate) + FRIENDLY_DATE_OR_TIME_SEPARATOR +
               dateFormatter.format(endDate) + FRIENDLY_DATE_OR_TIME_SEPARATOR +
               timeFormatter.format(endDate);
    }

    protected String getDateFormatForUpcomingEventAndInOneDay(Date startDate, Date endDate) {
        return timeFormatter.format(startDate) + EVENT_DATE_SEPARATOR_SAME_DAY +
               timeFormatter.format(endDate);
    }

    protected String getDateFormatForUpcomingEventButDifferentDays(Date startDate, Date endDate) {
        return timeFormatter.format(startDate) + EVENT_DATE_SEPARATOR_GENERAL +
               getFriendlyDayFormatForUpcomingCategory(endDate) + FRIENDLY_DATE_OR_TIME_SEPARATOR +
               timeFormatter.format(endDate);
    }

    protected String getDateFormatForUpcomingEventButDifferentWeek(Date startDate, Date endDate) {
        return timeFormatter.format(startDate) + EVENT_DATE_SEPARATOR_GENERAL +
               getFriendlyDayFormat(endDate) + FRIENDLY_DATE_OR_TIME_SEPARATOR +
               dateFormatter.format(endDate) + FRIENDLY_DATE_OR_TIME_SEPARATOR +
               timeFormatter.format(endDate);
    }

    protected String getDateFormatForUpcomingEventButDifferentYear(Date startDate, Date endDate) {
        return timeFormatter.format(startDate) + EVENT_DATE_SEPARATOR_GENERAL +
               dateFormatterWithFriendlyDayAndYear_.format(endDate);
    }

    // ================================================================================
    // Utility methods
    // ================================================================================

    protected boolean isSameYear(Date firstDate, Date secondDate) {
        return yearFormatter_.format(firstDate).equals(yearFormatter_.format(secondDate));
    }

    protected boolean isSameDay(Date firstDate, Date secondDate) {
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(firstDate);
        int firstDay = calendar.get(Calendar.DAY_OF_YEAR);

        calendar.setTime(secondDate);
        int secondDay = calendar.get(Calendar.DAY_OF_YEAR);

        return firstDay == secondDay;
    }

    /**
     * Converts a LocalDateTime to an Instant
     *
     * @param localDateTime
     *            to be converted
     * @return Instant generated from the given LocalDateTime
     */
    protected Instant getInstantFromLocalDateTime(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant();
    }

    /**
     * Creates a LocalDateTime that reflects today's date at 0000hrs, to be used
     * for more accurate comparison of the date of tasks.
     *
     * @return LocalDateTime of today at 0000hrs
     */
    protected LocalDateTime getDateTimeStartOfToday() {
        return LocalDate.now().atStartOfDay();
    }

    private String getFriendlyDayFormat(Date date) {
        return friendlyDayFormatter_.format(date);
    }

    /**
     * Generates the date of the end of the week for task date comparisons
     *
     * @param today
     *            Current date at 0000hrs
     * @return Date that is a week from now at 0000hrs for comparing tasks due
     *         this week
     */
    private Date getEndOfWeekDate(Date today) {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setTime(today);
        calendar.add(Calendar.DAY_OF_WEEK, NUMBER_OF_DAYS_IN_A_WEEK);
        return calendar.getTime();
    }

    private String getFriendlyDayFormatForUpcomingCategory(Date date) {
        LocalDateTime startingDateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        if (startingDateTime.getDayOfMonth() == getDateTimeStartOfToday().getDayOfMonth()) {
            return DATE_TODAY;
        } else if (startingDateTime.getDayOfMonth() == getDateTimeStartOfToday().plusDays(1).getDayOfMonth()) {
            return DATE_TOMORROW;
        } else {
            return getFriendlyDayFormat(date);
        }
    }

    // ================================================================================
    // Init Methods
    // ================================================================================

    private void loadLayout(String filePath) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(filePath));
        loader.setController(this); // Required due to different package
                                    // declaration from Main
        try {
            node_ = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a formatted task counter for use when adding tasks onto screen.
     */
    private void setupBinding() {
        taskCountString.bindBidirectional(taskCount, new NumberStringConverter());
        taskCountFormatted.bind(Bindings.concat(taskCountString).concat(UI_NUMBER_SEPARATOR));
    }

    // ================================================================================
    // Getter Methods
    // ================================================================================

    // @@author A0121597B generated
    protected Node getNode() {
        return this.node_;
    }

    protected VBox getMainVBox() {
        return this.mainVBox;
    }

    protected Date getToday() {
        return today_;
    }

    protected Date getCurrentDate() {
        return currentDate_;
    }

    protected Date getEndOfWeek() {
        return endOfWeek_;
    }
}
