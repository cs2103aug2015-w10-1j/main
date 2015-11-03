# A0121597B generated
###### procrastinate\ui\CategoryBox.java
``` java
    protected Node getCategoryBox() {
        return this.categoryBox;
    }

    /**
     * Retrieves the VBox that acts as a task list for a TaskEntry to go into
     */
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
    protected Node getMainScreen() {
        return mainScreenNode;
    }

    protected Node getHelpOverlay() {
        return helpOverlayNode;
    }
}
```
###### procrastinate\ui\CenterScreen.java
``` java
    protected Node getNode() {
        return this.node;
    }

    protected VBox getMainVBox() {
        return this.mainVBox;
    }

    protected Date getToday() {
        return today;
    }

    protected Date getCurrentDate() {
        return currentDate;
    }

    protected Date getEndOfWeek() {
        return endOfWeek;
    }
}
```
###### procrastinate\ui\DateBox.java
``` java
    protected Node getDateBox() {
        return this.dateBox;
    }

    /**
     * Retrieves the VBox that acts as a task list for a TaskEntry to go into
     */
    protected VBox getTaskListVBox() {
        return this.dateVBox;
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
###### procrastinate\ui\TaskEntry.java
``` java
    protected Node getEntryDisplay() {
        return this.taskEntry;
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
    private boolean isSysTraySupported() {
        return  SystemTray.isSupported();
    }

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
