//@@author A0121597B
package procrastinate.ui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * <h1>Another VBox wrapper structure that is similar to CategoryBox.</h1>
 * It is mainly used as a child of CategoryBox to contain the TaskEntry instead.
 *
 * <p><b>Note:</b>
 * <br>It is used in the 'Upcoming' category of the MultiCategoryScreen to provide
 * subheaders.
 *
 * <br>The CSS style class for SubcategoryBox is 'subcategoryBox' and the styling
 * applied differentiates it from CategoryBox.
 */
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

    protected VBox getTaskListVBox() {
        return this.subcategoryVBox;
    }
}
