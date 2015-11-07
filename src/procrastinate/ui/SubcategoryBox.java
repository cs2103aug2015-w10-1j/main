//@@author A0121597B
package procrastinate.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class SubcategoryBox extends VBox {

    // ================================================================================
    // Message strings
    // ================================================================================

    private static final String LOCATION_DATEBOX_FXML = "views/SubcategoryBox.fxml";

    // ================================================================================
    // Class variables
    // ================================================================================

    private Node subcategoryBox;

    // ================================================================================
    // FXML field variables
    // ================================================================================

    @FXML
    private Label subcategoryLabel;
    @FXML
    private VBox subcategoryVBox;

    // ================================================================================
    // CategoryBox methods
    // ================================================================================

    /**
     * Creates a category with the given label for tasks to go into
     *
     * @param labelString
     */
    protected SubcategoryBox(String labelString) {
        loadLayout();
        setLabel(labelString);
    }

    private void loadLayout() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(LOCATION_DATEBOX_FXML));
        loader.setController(this); // Required due to different package
                                    // declaration from Main
        try {
            this.subcategoryBox = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void setLabel(String labelString) {
        this.subcategoryLabel.setText(labelString);
    }

    // ================================================================================
    // Getter methods
    // ================================================================================

    // @@author A0121597B generated
    protected Node getDateBox() {
        return this.subcategoryBox;
    }

    /**
     * Retrieves the VBox that acts as a task list for a TaskEntry to go into
     */
    protected VBox getTaskListVBox() {
        return this.subcategoryVBox;
    }
}
