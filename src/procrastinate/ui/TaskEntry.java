//@@author A0121597B
package procrastinate.ui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

/**
 * <h1>A GridPane wrapper structure used to display the different Tasks.</h1>
 *
 * It is laid out in 3 columns and displays the information of the Task
 * in the order of:
 * <li>Line Number,
 * <li>Task Description
 * <li>Date/Time String
 * <br>
 * Text wrapping also is enabled for long task descriptions while the date/time strings
 * are to be properly formatted before being passed in.
 *
 * <p><b>Note:</b>
 * A tick will be displayed for Tasks that are marked as done and will be placed beside
 * the line number of the Task displayed.
 */
public class TaskEntry extends GridPane {

    // ================================================================================
    // Message Strings
    // ================================================================================

    private static final String EMPTY_STRING = "";

    private static final String LOCATION_TASK_ENTRY_FXML = "views/TaskEntry.fxml";
    private static final String LOCATION_TICK_IMAGE = "images/tick.png";

    private static final String STYLE_TICK_CENTERING_PADDING = "-fx-padding: 4 5 0 0;";
    private static final String STYLE_REMOVE_PADDING = "-fx-padding: 0;";

    // ================================================================================
    // Constants
    // ================================================================================

    private static final int GRIDPANE_LINE_NUMBER_ROW_INDEX = 0;
    private static final int GRIDPANE_LINE_NUMBER_COLUMN_INDEX = 0;
    private static final int GRIDPANE_LINE_NUMBER_POSITION = 0;

    private static final int STYLE_TICK_WIDTH = 10;
    private static final int STYLE_TICK_HEIGHT = 10;
    private static final int STYLE_NO_SPACING = 0;

    // ================================================================================
    // Class Variables
    // ================================================================================

    private Node taskEntry_;

    // ================================================================================
    // FXML Field Variables
    // ================================================================================

    @FXML
    private Label lineNum;
    @FXML
    private Label description;
    @FXML
    private Label time;

    // ================================================================================
    // TaskEntry Constructor
    // ================================================================================

    /**
     * Constructor to be used for displaying "DREAMS"
     *
     * @param lineNum        formatted line numbering of the corresponding task
     * @param description    the entire task description
     * @param isDone         if true, a tick will be displayed beside the line number
     */
    protected TaskEntry(String lineNum, String description, boolean isDone) {
        loadLayout();
        if (isDone) {
            addTickBeforeLineNumber();
        }
        setLabels(lineNum, description, EMPTY_STRING);
    }

    /**
     * Constructor to be used for displaying all other TaskTypes
     *
     * @param lineNum        formatted line numbering of the corresponding task
     * @param description    the entire task description
     * @param time           formatted string to be displayed in the 'time' Label
     * @param isDone         if true, a tick will be displayed beside the line number
     */
    protected TaskEntry(String lineNum, String description, String time, boolean isDone) {
        loadLayout();
        if (isDone) {
            addTickBeforeLineNumber();
        }
        setLabels(lineNum, description, time);
    }

    // ================================================================================
    // Init Methods
    // ================================================================================

    private void loadLayout() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(LOCATION_TASK_ENTRY_FXML));
        loader.setController(this); // Required due to different package
                                    // declaration from Main
        try {
            this.taskEntry_ = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void setLabels(String lineNum, String description, String time) {
        this.lineNum.setText(lineNum);
        this.description.setText(description);
        this.time.setText(time);

        if (time.equals(EMPTY_STRING)) {
            this.time.setMinWidth(0);
            this.time.setStyle(STYLE_REMOVE_PADDING);
        }
    }

    /**
     * Two HBoxes are created as wrapper for the line number and tick image individually,
     * before they are wrapped again in another HBox to replace the current line number Label
     * in the taskEntry.
     */
    private void addTickBeforeLineNumber() {
        HBox lineNumberWrapper = getLabelWrapper();
        HBox tickWrapper = getTickWrapper();

        addTickAndLabelToTaskEntry(lineNumberWrapper, tickWrapper);
    }

    private HBox getLabelWrapper() {
        Label lineNumberLabel = getLabelAndRemovePadding();
        return wrapLabel(lineNumberLabel);
    }

    private HBox getTickWrapper() {
        ImageView tick = getTick();
        return wrapTick(tick);
    }

    private Label getLabelAndRemovePadding() {
        Label lineNumberLabel = (Label) ((GridPane) taskEntry_).getChildren().get(GRIDPANE_LINE_NUMBER_POSITION);
        lineNumberLabel.setStyle(STYLE_REMOVE_PADDING);

        return lineNumberLabel;
    }

    private void addTickAndLabelToTaskEntry(HBox lineNumberWrapper, HBox checkBoxWrapper) {
        HBox combinedWrapper = new HBox(checkBoxWrapper, lineNumberWrapper);
        ((GridPane) taskEntry_).add(combinedWrapper, GRIDPANE_LINE_NUMBER_COLUMN_INDEX, GRIDPANE_LINE_NUMBER_ROW_INDEX);
    }

    private HBox wrapTick(ImageView tick) {
        HBox tickWrapper = new HBox(tick);

        tickWrapper.setSpacing(STYLE_NO_SPACING);
        tickWrapper.setStyle(STYLE_TICK_CENTERING_PADDING);

        return tickWrapper;
    }

    private ImageView getTick() {
        ImageView tickImage = new ImageView();

        tickImage.setImage(new Image(TaskEntry.class.getResource(LOCATION_TICK_IMAGE).toExternalForm()));
        tickImage.setFocusTraversable(false);
        tickImage.setSmooth(true);
        tickImage.setFitHeight(STYLE_TICK_HEIGHT);
        tickImage.setFitWidth(STYLE_TICK_WIDTH);

        return tickImage;
    }


    private HBox wrapLabel(Label lineNumberLabel) {
        HBox lineNumberWrapper = new HBox(lineNumberLabel);
        lineNumberWrapper.setSpacing(STYLE_NO_SPACING);

        return lineNumberWrapper;
    }

    // ================================================================================
    // Getter Methods
    // ================================================================================
    // @@author A0121597B generated
    protected Node getEntryDisplay() {
        return this.taskEntry_;
    }

    protected Label getLineNum() {
        return lineNum;
    }

    protected Label getDescription() {
        return description;
    }

    protected Label getTime() {
        return time;
    }
}
