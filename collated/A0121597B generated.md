# A0121597B generated
###### Procrastinate/src/procrastinate/ui/CategoryBox.java
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
###### Procrastinate/src/procrastinate/ui/CenterPaneController.java
``` java
    protected Node getMainScreen() {
        return mainScreenNode;
    }

    protected Node getHelpOverlay() {
        return helpOverlayNode;
    }
}
```
###### Procrastinate/src/procrastinate/ui/CenterScreen.java
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
###### Procrastinate/src/procrastinate/ui/DateBox.java
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
###### Procrastinate/src/procrastinate/ui/ImageOverlay.java
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
###### Procrastinate/src/procrastinate/ui/TaskEntry.java
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
###### Procrastinate/src/procrastinate/ui/WindowHandler.java
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
