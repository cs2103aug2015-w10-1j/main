//@@author A0121597B
package procrastinate.ui;

import java.util.List;

import javafx.animation.FadeTransition;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import procrastinate.task.Task;

public class SearchScreen extends MultiCategoryScreen {

    // ================================================================================
    // Message Strings
    // ================================================================================

    private static final String FX_BACKGROUND_IMAGE_NO_SEARCH_RESULTS = "-fx-background-image: url('/procrastinate/ui/images/no-search-results.png')";

    private static final String SEARCH_HEADER = "Search results for ";

    private static final String SELECTOR_PARENT_OF_MAIN_VBOX = "#scrollPane";

    private static final String STYLE_SEARCH_HEADER_FONT_FAMILY = "-fx-font-family: 'Helvetica Neue';";
    private static final String STYLE_SEARCH_HEADER_FONT_WEIGHT = "-fx-font-weight: bold;";
    private static final String STYLE_SEARCH_HEADER_FONT_SIZE = "-fx-font-size: 18px;";
    private static final String STYLE_SEARCH_HEADER_PADDING = "-fx-padding: 10 0 0 10;";

    private static final String STYLE_WRAPPER_BACKGROUND_COLOR = "-fx-background-color: white;";

    // ================================================================================
    // Constants
    // ================================================================================

    private static final int MAIN_VBOX_PREF_HEIGHT = 450;
    private static final int MAIN_VBOX_PREF_WIDTH = 450;

    // ================================================================================
    // Class Variables
    // ================================================================================

    private Label searchHeader_ = new Label();

    // ================================================================================
    // SearchScreen Constructor
    // ================================================================================

    protected SearchScreen() {
        super();
        adjustLabelStyle();
        wrapSearchHeaderLabelWithMainVBox();
    }

    // ================================================================================
    // SearchScreen Methods
    // ================================================================================

    @Override
    protected void updateTaskList(List<Task> taskList) {
        FadeTransition fadeOutDeletedTaskEntry = fadeOutDeletedTaskEntry(taskList);

        fadeOutDeletedTaskEntry.setOnFinished(finish -> {
            getUpdatedDates();
            clearTaskList();

            for (Task task : taskList) {
                taskCount.set(taskCount.get() + 1);

                addTaskByType(task);
            }
            updateDisplay();
        });

        fadeOutDeletedTaskEntry.play();
    }

    @Override
    protected void setBackgroundImageIfMainVBoxIsEmpty(VBox mainVBox) {
        if (mainVBox.getChildren().isEmpty()) {
            mainVBox.setStyle(FX_BACKGROUND_IMAGE_NO_SEARCH_RESULTS);
        }
    }

    protected void updateSearchStringLabel(String searchString) {
        searchHeader_.setText(SEARCH_HEADER + searchString.trim());
    }

    // ================================================================================
    // Init Methods
    // ================================================================================

    private void adjustLabelStyle() {
        searchHeader_.setWrapText(true);
        searchHeader_.setFocusTraversable(false);
        searchHeader_.setStyle(STYLE_SEARCH_HEADER_FONT_FAMILY +
                              STYLE_SEARCH_HEADER_FONT_WEIGHT +
                              STYLE_SEARCH_HEADER_FONT_SIZE +
                              STYLE_SEARCH_HEADER_PADDING);
    }

    private void wrapSearchHeaderLabelWithMainVBox() {
        ScrollPane parentOfMainVBox = (ScrollPane) this.getNode().lookup(SELECTOR_PARENT_OF_MAIN_VBOX);

        adjustMainVBoxForWrapping();

        VBox wrapper = buildWrapper();
        parentOfMainVBox.setContent(wrapper);
    }

    private void adjustMainVBoxForWrapping() {
        mainVBox.setPrefSize(MAIN_VBOX_PREF_WIDTH, MAIN_VBOX_PREF_HEIGHT);
    }

    private VBox buildWrapper() {
        VBox wrapper = new VBox(searchHeader_, mainVBox);

        wrapper.setStyle(STYLE_WRAPPER_BACKGROUND_COLOR);

        return wrapper;
    }

}
