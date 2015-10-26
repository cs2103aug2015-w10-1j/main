package procrastinate.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class DateBox extends VBox {

    // ================================================================================
    // Message strings
    // ================================================================================

    private static final String LOCATION_DATEBOX_FXML = "views/DateBox.fxml";

    // ================================================================================
    // Class variables
    // ================================================================================

    private Node dateBox;

    // ================================================================================
    // FXML field variables
    // ================================================================================

    @FXML private Label dateLabel;
    @FXML private VBox dateVBox;

    // ================================================================================
    // CategoryBox methods
    // ================================================================================

    /**
     * Creates a category with the given label for tasks to go into
     * @param label
     */
    protected DateBox(String label) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(LOCATION_DATEBOX_FXML));
        loader.setController(this); // Required due to different package declaration from Main
        try {
            this.dateBox = loader.load();
            this.dateLabel.setText(label);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // ================================================================================
    // Getter methods
    // ================================================================================

    protected Node getDateBox() {
        return this.dateBox;
    }

    /**
     * Retrieves the VBox that acts as a task list for a TaskEntry to go into
     */
    protected VBox getTaskListVBox() {
        return this.dateVBox;
    }
}

