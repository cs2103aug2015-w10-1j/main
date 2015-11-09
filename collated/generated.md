# generated
###### procrastinate\ui\CategoryBox.java
``` java
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
```
###### procrastinate\ui\CenterPaneController.java
``` java
    protected Node getDoneScreenNode() {
        return doneScreenNode_;
    }

    protected Node getMainScreenNode() {
        return mainScreenNode_;
    }

    protected Node getSearchScreenNode() {
        return searchScreenNode_;
    }

    protected Node getSummaryScreenNode() {
        return summaryScreenNode_;
    }

    protected Node getHelpOverlayNode() {
        return helpOverlayNode_;
    }

    protected Node getSplashOverlayNode() {
        return splashOverlayNode_;
    }

    protected CenterScreen getCurrentScreen() {
        return currentScreen_;
    }

    protected ImageOverlay getCurrentOverlay() {
        return currentOverlay_;
    }

}
```
###### procrastinate\ui\CenterScreen.java
``` java
    protected Node getNode() {
        return this.node_;
    }

    protected VBox getMainVBox() {
        return this.mainVBox;
    }

    protected Date getToday() {
        return today_;
    }

    protected Date getCurrentDate() {
        return currentDate_;
    }

    protected Date getEndOfWeek() {
        return endOfWeek_;
    }
}
```
###### procrastinate\ui\DoubleNodePair.java
``` java
    protected double getHeight() {
        return height_;
    }

    protected Node getNode() {
        return node_;
    }

}
```
###### procrastinate\ui\ImageOverlay.java
``` java
    protected Node getNode() {
        return this.node;
    }

    protected ImageView getImageView() {
        return imageView;
    }

    protected VBox getContainer() {
        return container;
    }
}
```
###### procrastinate\ui\MultiCategoryScreen.java
``` java
    protected Node getOverdueNode() {
        return overdueNode;
    }

    protected Node getUpcomingNode() {
        return upcomingNode;
    }

    protected Node getFutureNode() {
        return futureNode;
    }

    protected Node getDreamsNode() {
        return dreamsNode;
    }

    protected Node getDoneNode() {
        return doneNode;
    }

    protected VBox getOverdueTaskList() {
        return overdueTaskList;
    }

    protected VBox getUpcomingTaskList() {
        return upcomingTaskList;
    }

    protected VBox getFutureTaskList() {
        return futureTaskList;
    }

    protected VBox getDreamsTaskList() {
        return dreamsTaskList;
    }

    protected VBox getDoneTaskList() {
        return doneTaskList;
    }

}
```
###### procrastinate\ui\SingleCategoryScreen.java
``` java
    protected Node getCategoryNode() {
        return thisCategoryNode;
    }

    protected VBox getCategoryTaskList() {
        return thisCategoryTaskList;
    }
}
```
###### procrastinate\ui\SubcategoryBox.java
``` java
    protected Node getSubcategoryBox() {
        return this.subcategoryBox_;
    }

    protected VBox getTaskListVBox() {
        return this.subcategoryVBox;
    }

    protected Label getSubcategoryLabel() {
        return this.subcategoryLabel;
    }
}
```
###### procrastinate\ui\TaskEntry.java
``` java
    protected Node getEntryDisplay() {
        return this.taskEntry_;
    }

    protected Label getLineNum() {
        return lineNum;
    }

    protected Label getDescription() {
        return description;
    }

    protected Label getTime() {
        return time;
    }
}
```
###### procrastinate\ui\WindowHandler.java
``` java
    protected Label getStatusLabel() {
        return statusLabel;
    }

    protected StackPane getCenterScreen() {
        return centerScreen;
    }

    protected TextField getUserInputField() {
        return userInputField;
    }

}
```
