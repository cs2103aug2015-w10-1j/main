package procrastinate.ui;

import java.util.List;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import procrastinate.task.Task;

public class SearchScreen extends MultiCategoryScreen {

    // ================================================================================
    // Message strings
    // ================================================================================

    private static final String FX_BACKGROUND_IMAGE_NO_SEARCH_RESULTS = "-fx-background-image: url('/procrastinate/ui/images/no-search-results.png')";

    private static final String SEARCH_HEADER = "Search results for ";

    private static final String STYLE_SEARCH_HEADER_FONT_FAMILY = "-fx-font-family: 'Helvetica Neue';";
    private static final String STYLE_SEARCH_HEADER_FONT_WEIGHT = "-fx-font-weight: bold;";
    private static final String STYLE_SEARCH_HEADER_FONT_SIZE = "-fx-font-size: 18px;";

    // ================================================================================
    // Class variables
    // ================================================================================

    Label searchHeader = new Label();

    // ================================================================================
    // SearchScreen Constructor
    // ================================================================================

    protected SearchScreen(String filePath) {
        super(filePath);
        adjustStyles();
    }

    // ================================================================================
    // SearchScreen methods
    // ================================================================================

    @Override
    protected void updateTaskList(List<Task> taskList) {
        removeSearchHeaderLabel();
        getUpdatedDates();
        clearTaskList();

        for (Task task : taskList) {
            taskCount.set(taskCount.get() + 1);

            addTaskByType(task);
        }
        updateDisplay();
        addSearchHeaderLabel();
    }

    @Override
    protected void setBackgroundImageIfMainVBoxIsEmpty(VBox mainVBox) {
        if (mainVBox.getChildren().isEmpty()) {
            mainVBox.setStyle(FX_BACKGROUND_IMAGE_NO_SEARCH_RESULTS);
        }
    }

    protected void updateSearchStringLabel(String searchString) {
        searchHeader.setText(SEARCH_HEADER + searchString.trim());
    }

    private void removeSearchHeaderLabel() {
        if (mainVBox.getChildren().contains(searchHeader)) {
            mainVBox.getChildren().remove(searchHeader);
        }
    }

    private void addSearchHeaderLabel() {
        mainVBox.getChildren().add(0, searchHeader);
    }

    private void adjustStyles() {
        searchHeader.setWrapText(true);
        searchHeader.setFocusTraversable(false);
        searchHeader.setStyle(STYLE_SEARCH_HEADER_FONT_FAMILY
                            + STYLE_SEARCH_HEADER_FONT_WEIGHT
                            + STYLE_SEARCH_HEADER_FONT_SIZE);
    }
}
