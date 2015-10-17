package procrastinate.ui;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.VBox;

public class MainScreen extends CenterScreen {

    protected Node overdue;
    protected Node thisWeek;
    protected Node future;
    protected Node dreams;

    protected CategoryBox overdueBox;
    protected CategoryBox thisWeekBox;
    protected CategoryBox futureBox;
    protected CategoryBox dreamsBox;

    @FXML VBox mainVBox;

    protected MainScreen(String filePath) {
        super(filePath);
        createCategories();
    }

    private void createCategories() {
        // Create all the different categories(by time frame) for entries to go into
        overdueBox = new CategoryBox("Overdue");
        thisWeekBox = new CategoryBox("This Week");
        futureBox = new CategoryBox("Future");
        dreamsBox = new CategoryBox("Dreams");

        this.overdue = overdueBox.getCategoryBox();
        this.thisWeek = thisWeekBox.getCategoryBox();
        this.future = futureBox.getCategoryBox();
        this.dreams = dreamsBox.getCategoryBox();

        // Set up for placement of boxes
        mainVBox.setPadding(new Insets(10));    // Looks a bit weird if the boxes stick right next to the border
        mainVBox.setSpacing(10);                // Spacing between boxes
        mainVBox.getChildren().addAll(overdue,thisWeek,future,dreams);
    }
}
