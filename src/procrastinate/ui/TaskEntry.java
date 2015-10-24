package procrastinate.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class TaskEntry extends HBox{

    // ================================================================================
    // Message strings
    // ================================================================================

    private static final String LOCATION_TASK_ENTRY_FXML = "views/TaskEntry.fxml";
    private static final String EMPTY_STRING = "";

    // ================================================================================
    // Class variables
    // ================================================================================

    private Node taskEntry;

    // ================================================================================
    // FXML field variables
    // ================================================================================

    @FXML Label lineNum;
    @FXML Label description;
    @FXML Label time;

    // ================================================================================
    // TaskEntry methods
    // ================================================================================

    /**
     * Constructor to be used for displaying "DREAMS"
     * @param lineNum
     * @param description
     */
    protected TaskEntry(String lineNum, String description) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(LOCATION_TASK_ENTRY_FXML));
        loader.setController(this); // Required due to different package declaration from Main
        try {
            this.taskEntry = loader.load();
            this.lineNum.setText(lineNum);
            this.description.setText(description);
            this.time.setMinWidth(0);
            this.time.setStyle("-fx-padding: 0;");
            this.time.setText(EMPTY_STRING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Constructor to be used for displaying all other TaskTypes
     * @param lineNum
     * @param description
     */
    protected TaskEntry(String lineNum, String description, String time) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(LOCATION_TASK_ENTRY_FXML));
        loader.setController(this); // Required due to different package declaration from Main
        try {
            this.taskEntry = loader.load();
            this.lineNum.setText(lineNum);
            this.description.setText(description);
            this.time.setText(time);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ================================================================================
    // Getter methods
    // ================================================================================

    protected Node getEntryDisplay() {
        return this.taskEntry;
    }
}

