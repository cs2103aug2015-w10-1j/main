package procrastinate.ui;

import javafx.scene.Node;
import javafx.scene.layout.VBox;

public abstract class SingleCategoryScreen extends CenterScreen {

    // ================================================================================
    // Class variables
    // ================================================================================

    protected Node thisCategoryNode;
    protected VBox thisCategoryTaskList;

    protected VBox mainVBox;

    private String headerName;

    // ================================================================================
    // SingleCategoryScreen Constructor
    // ================================================================================

    protected SingleCategoryScreen(String filePath, String headerName) {
        super(filePath);
        createCategories(headerName);
        retrieveFxmlElements();
    }

    // ================================================================================
    // Init methods
    // ================================================================================

    @Override
    protected void createCategories() {
        CategoryBox categoryBox = new CategoryBox(headerName);
        this.thisCategoryNode = categoryBox.getCategoryBox();
        this.thisCategoryTaskList = categoryBox.getTaskListVBox();
    }

    private void createCategories(String headerName) {
        this.headerName = headerName;
        createCategories();
    }

    private void retrieveFxmlElements() {
        this.mainVBox = getMainVBox();
    }
}
