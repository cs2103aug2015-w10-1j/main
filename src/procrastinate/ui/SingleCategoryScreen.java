//@@author A0121597B
package procrastinate.ui;

import java.util.Date;

import javafx.animation.ParallelTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.Node;
import javafx.scene.layout.VBox;

/**
 * <h1>A subclass of CenterScreen and contain only a single CategoryBox.</h1>
 *
 * The constructor takes in a string to be used as the text of the CategoryBox
 * header.
 */
public abstract class SingleCategoryScreen extends CenterScreen {

    // ================================================================================
    // Class Variables
    // ================================================================================

    protected Node thisCategoryNode;

    protected VBox thisCategoryTaskList;

    protected Date today;

    protected VBox mainVBox;

    private String headerName_;

    // ================================================================================
    // Animation Values
    // ================================================================================
    // Time values used are in milliseconds
    private static final int TIME_TRANSITION_FADE = 250;

    // ================================================================================
    // SingleCategoryScreen Constructor
    // ================================================================================

    protected SingleCategoryScreen(String headerName) {
        super();
        createCategories(headerName);
        retrieveFxmlElements();
    }

    // ================================================================================
    // Screen Transition Methods
    // ================================================================================

    @Override
    protected SequentialTransition getScreenSwitchOutSequence() {
        SequentialTransition switchOutTransition = new SequentialTransition();
        switchOutTransition.getChildren().add(generateFadeOutTransition(thisCategoryNode, TIME_TRANSITION_FADE));
        return switchOutTransition;
    }

    @Override
    protected ParallelTransition getScreenSwitchInSequence() {
        ParallelTransition switchInTransition = new ParallelTransition();
        switchInTransition.getChildren().add(generateFadeInTransition(thisCategoryNode, TIME_TRANSITION_FADE));
        return switchInTransition;
    }

    // ================================================================================
    // Init Methods
    // ================================================================================

    @Override
    protected void createCategories() {
        CategoryBox categoryBox = new CategoryBox(headerName_);

        this.thisCategoryNode = categoryBox.getCategoryBox();
        this.thisCategoryTaskList = categoryBox.getTaskListVBox();
    }

    private void createCategories(String headerName) {
        this.headerName_ = headerName;

        createCategories();
    }

    private void retrieveFxmlElements() {
        this.mainVBox = getMainVBox();
    }

    // ================================================================================
    // Utility Methods
    // ================================================================================

    /**
     * Used when updating the task list, removes all tasks and resets the task counter
     */
    protected void clearTaskList() {
        taskCount.set(0);

        mainVBox.getChildren().clear();
        thisCategoryTaskList.getChildren().clear();
    }

    protected void getUpdatedDates() {
        updateDates();

        today = getToday();
    }
}
