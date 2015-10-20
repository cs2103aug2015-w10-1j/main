package procrastinate.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.VBox;

import java.io.IOException;

public abstract class CenterScreen extends VBox {

    // ================================================================================
    // Class variables
    // ================================================================================

    @SuppressWarnings("unused")
    private CenterPaneController parentController;
    private Node node;

    // ================================================================================
    // CenterScreen methods
    // ================================================================================

    protected CenterScreen(String filePath) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(filePath));
        loader.setController(this); // Required due to different package declaration from Main
        try {
            node = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // ================================================================================
    // Getters and Setters
    // ================================================================================

    protected void setParentController(CenterPaneController centerScreenController) {
        this.parentController = centerScreenController;
    }

    protected Node getNode() {
        return this.node;
    }
}
