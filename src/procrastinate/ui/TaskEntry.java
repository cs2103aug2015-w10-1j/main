package procrastinate.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class TaskEntry extends GridPane {

    // ================================================================================
    // Message strings
    // ================================================================================

    private static final String EMPTY_STRING = "";

    private static final String LOCATION_TASK_ENTRY_FXML = "views/TaskEntry.fxml";
    private static final String LOCATION_TICK_IMAGE = "images/tick.png";

    private static final String STYLE_TICK_CENTERING_PADDING = "-fx-padding: 4 5 0 0;";
    private static final String STYLE_REMOVE_PADDING = "-fx-padding: 0;";

    // ================================================================================
    // Class variables
    // ================================================================================

    private Node taskEntry;

    // ================================================================================
    // FXML field variables
    // ================================================================================

    @FXML private Label lineNum;
    @FXML private Label description;
    @FXML private Label time;

    // ================================================================================
    // TaskEntry methods
    // ================================================================================

    /**
     * Constructor to be used for displaying "DREAMS"
     * @param lineNum
     * @param description
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
     * @param lineNum
     * @param description
     */
    protected TaskEntry(String lineNum, String description, String time, boolean isDone) {
        loadLayout();
        if (isDone) {
            addTickBeforeLineNumber();
        }
        setLabels(lineNum, description, time);
    }

    private void loadLayout() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(LOCATION_TASK_ENTRY_FXML));
        loader.setController(this); // Required due to different package declaration from Main
        try {
            this.taskEntry = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
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

    private void addTickBeforeLineNumber() {
        HBox lineNumberWrapper = getLabelWrapper();
        HBox tickWrapper = getTickWrapper();
        addTickAndLabelToTaskEntry(lineNumberWrapper, tickWrapper);
    }

    private void addTickAndLabelToTaskEntry(HBox lineNumberWrapper, HBox checkBoxWrapper) {
        HBox combinedWrapper = new HBox(checkBoxWrapper, lineNumberWrapper);
        ((GridPane)taskEntry).add(combinedWrapper, 0, 0);
    }

    private ImageView createNewTick() {
        ImageView tickImage = new ImageView();
        tickImage.setImage(new Image(TaskEntry.class.getResource(LOCATION_TICK_IMAGE).toExternalForm()));
        tickImage.setFocusTraversable(false);
        tickImage.setSmooth(true);
        tickImage.setFitHeight(10);
        tickImage.setFitWidth(10);
        return tickImage;
    }

    private HBox createTickWrapper(ImageView tick) {
        HBox tickWrapper = new HBox(tick);
        tickWrapper.setSpacing(0);
        tickWrapper.setStyle(STYLE_TICK_CENTERING_PADDING);
        return tickWrapper;
    }

    private HBox createLabelWrapper(Label lineNumberLabel) {
        HBox lineNumberWrapper = new HBox(lineNumberLabel);
        lineNumberWrapper.setSpacing(0);
        return lineNumberWrapper;
    }

    private HBox getTickWrapper() {
        ImageView tick = createNewTick();
        return createTickWrapper(tick);
    }

    private HBox getLabelWrapper() {
        Label lineNumberLabel = getLabelAndRemovePadding();
        return createLabelWrapper(lineNumberLabel);
    }

    private Label getLabelAndRemovePadding() {
        Label lineNumberLabel = (Label) ((GridPane)taskEntry).getChildren().get(0);
        lineNumberLabel.setStyle(STYLE_REMOVE_PADDING);
        return lineNumberLabel;
    }

    // ================================================================================
    // Getter methods
    // ================================================================================

    protected Node getEntryDisplay() {
        return this.taskEntry;
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

