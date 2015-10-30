package procrastinate.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class TaskEntry extends HBox{

    // ================================================================================
    // Message strings
    // ================================================================================

    private static final String EMPTY_STRING = "";

    private static final String LOCATION_TASK_ENTRY_FXML = "views/TaskEntry.fxml";

    private static final String STYLE_CHECKBOX_PADDING = "-fx-padding: 5 0 0 0;";
    private static final String STYLE_CHECKBOX_SIZE = "-fx-font-size: 7.5px;";
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
            addCheckBoxBeforeLineNumber();
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
            addCheckBoxBeforeLineNumber();
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

    private void addCheckBoxBeforeLineNumber() {
        HBox lineNumberWrapper = getLabelWrapper();
        HBox checkBoxWrapper = getCheckBoxWrapper();
        addCheckBoxAndLabelToTaskEntry(lineNumberWrapper, checkBoxWrapper);
    }

    private void addCheckBoxAndLabelToTaskEntry(HBox lineNumberWrapper, HBox checkBoxWrapper) {
        HBox combinedWrapper = new HBox(checkBoxWrapper, lineNumberWrapper);
        ((GridPane)taskEntry).add(combinedWrapper, 0, 0);
    }

    private CheckBox createNewCheckBox() {
        CheckBox checkBox = new CheckBox();
        checkBox.setDisable(true);
        checkBox.setSelected(true);
        checkBox.setStyle(STYLE_CHECKBOX_SIZE);
        return checkBox;
    }

    private HBox createCheckBoxWrapper(CheckBox checkBox) {
        HBox checkBoxWrapper = new HBox(checkBox);
        checkBoxWrapper.setSpacing(0);
        checkBoxWrapper.setStyle(STYLE_CHECKBOX_PADDING);
        return checkBoxWrapper;
    }

    private HBox createLabelWrapper(Label lineNumberLabel) {
        HBox lineNumberWrapper = new HBox(lineNumberLabel);
        lineNumberWrapper.setSpacing(0);
        return lineNumberWrapper;
    }

    private HBox getCheckBoxWrapper() {
        CheckBox checkBox = createNewCheckBox();
        return createCheckBoxWrapper(checkBox);
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

