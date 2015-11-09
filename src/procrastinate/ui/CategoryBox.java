//@@author A0121597B
package procrastinate.ui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 * <h1>A VBox wrapper structure that models a category list.</h1>
 * It contains a Label that acts as the header and another VBox, to contain all the TaskEntry added.
 *
 * <p><b>Note:</b>
 * <br>It should be the main element to be added into the mainVBox of any screen before
 * any SubcategoryBox or TaskEntry.
 *
 * <br>The CSS style class for CategoryBox is 'categoryBox' and the styling applied
 * differentiates CategoryBox from SubcategoryBox.
 */
public class CategoryBox extends VBox {

    // ================================================================================
    // Message Strings
    // ================================================================================

    private static final String LOCATION_CATEGORYBOX_FXML = "views/CategoryBox.fxml";

    // ================================================================================
    // Class Variables
    // ================================================================================

    private Node categoryBox_;

    // ================================================================================
    // FXML Field Variables
    // ================================================================================

    @FXML
    private Label categoryLabel;
    @FXML
    private VBox categoryVBox;

    // ================================================================================
    // CategoryBox Constructor
    // ================================================================================

    /**
     * Creates a CategoryBox that encloses a Label as the header text and a VBox
     * to be used to contain the list of tasks.
     *
     * @param categoryHeader    string to be used as the header text of the category
     */
    protected CategoryBox(String categoryHeader) {
        loadLayout();
        setLabelTextWithDropShadowEffect(categoryHeader);
    }

    // ================================================================================
    // Init Methods
    // ================================================================================

    private void loadLayout() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(LOCATION_CATEGORYBOX_FXML));
        loader.setController(this); // Required due to different package
                                    // declaration from Main
        try {
            this.categoryBox_ = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sets the categoryLabel which acts as the header for a CategoryBox. This
     * method also adds a DropShadow effect to the label after the text is set.
     *
     * @param categoryHeader    to be set as the category header text
     */
    private void setLabelTextWithDropShadowEffect(String categoryHeader) {
        this.categoryLabel.setText(categoryHeader);
        DropShadow dropShadow = new DropShadow(BlurType.GAUSSIAN, Color.GRAY, 6, 0, 0, 2.0f);
        categoryLabel.setEffect(dropShadow);
    }

    // ================================================================================
    // Getter Methods
    // ================================================================================

    //@@author generated
    protected Node getCategoryBox() {
        return this.categoryBox_;
    }

    protected VBox getTaskListVBox() {
        return this.categoryVBox;
    }

    protected Label getCategoryLabel() {
        return this.categoryLabel;
    }
}
