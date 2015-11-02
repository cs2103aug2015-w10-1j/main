package procrastinate.ui;

import javafx.scene.Node;
import javafx.scene.layout.VBox;

public abstract class SingleCategoryScreen extends CenterScreen {

    // ================================================================================
    // Class variables
    // ================================================================================

    private Node thisCategoryNode;
    private VBox thisCategoryTaskList;

    private String headerName;

    // ================================================================================
    // SingleCategoryScreen Constructor
    // ================================================================================

    protected SingleCategoryScreen(String filePath, String headerName) {
        super(filePath);
        createCategories(headerName);
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

    protected Node getThisCategoryNode() {
        return thisCategoryNode;
    }

    protected VBox getThisCategoryTaskList() {
        return thisCategoryTaskList;
    }

}
