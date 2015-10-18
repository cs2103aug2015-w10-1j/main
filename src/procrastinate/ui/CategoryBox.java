package procrastinate.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class CategoryBox extends VBox {

    // ================================================================================
    // Message strings
    // ================================================================================

    private static final String LOCATION_CATEGORYBOX_FXML = "CategoryBox.fxml";

    // ================================================================================
    // Class variables
    // ================================================================================

    private Node categoryBox;

    // ================================================================================
    // FXML field variables
    // ================================================================================

    @FXML Label categoryLabel;
    @FXML VBox categoryVBox;

    // ================================================================================
    // CategoryBox methods
    // ================================================================================

    protected CategoryBox(String label) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(LOCATION_CATEGORYBOX_FXML));
        loader.setController(this); // Required due to different package declaration from Main
        try {
            this.categoryBox = loader.load();
            this.categoryLabel.setText(label);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ================================================================================
    // Getter methods
    // ================================================================================

    protected Node getCategoryBox() {
        return this.categoryBox;
    }

    protected VBox getTaskListVBox() {
        return this.categoryVBox;
    }
}
