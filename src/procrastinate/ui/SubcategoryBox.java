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

    private Node subcategoryBox_;

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
     * Creates a SubcategoryBox that encloses a Label as the header text and a VBox
     * to be used to contain the list of tasks.
     *
     * @param subcategoryHeader    to be used as the header text of the subcategory
     */
    protected SubcategoryBox(String subcategoryHeader) {
        loadLayout();
        setLabelText(subcategoryHeader);
    }

    // ================================================================================
    // Init Methods
    // ================================================================================

    private void loadLayout() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(LOCATION_DATEBOX_FXML));
        loader.setController(this); // Required due to different package
                                    // declaration from Main
        try {
            this.subcategoryBox_ = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void setLabelText(String subcategoryHeader) {
        this.subcategoryLabel.setText(subcategoryHeader);
    }

    // ================================================================================
    // Getter methods
    // ================================================================================

    // @@author A0121597B generated
    protected Node getDateBox() {
        return this.subcategoryBox_;
    }

    /**
     * Retrieves the VBox that acts as a task list for a TaskEntry to go into
     */
    protected VBox getTaskListVBox() {
        return this.subcategoryVBox;
    }
}
