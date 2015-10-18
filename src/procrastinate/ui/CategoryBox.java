package procrastinate.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class CategoryBox extends VBox {

    private static final String LOCATION_CATEGORYBOX_FXML = "CategoryBox.fxml";

    private Node categoryBox;

    @FXML Label categoryLabel;
    @FXML VBox entriesBox;

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

    protected Node getCategoryBox() {
        return this.categoryBox;
    }

    protected VBox getEntriesBox() {
        return this.entriesBox;
    }
}
