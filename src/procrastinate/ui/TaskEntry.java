package procrastinate.ui;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class TaskEntry extends HBox{

    private static final String LOCATION_TASK_ENTRY_FXML = "TaskEntry.fxml";
    private static final String EMPTY_STRING = "";

    private Node taskEntry;

    @FXML Label lineNum;
    @FXML Label description;
    @FXML Label time;

    protected TaskEntry() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(LOCATION_TASK_ENTRY_FXML));
        loader.setController(this); // Required due to different package declaration from Main
        try {
            this.taskEntry = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected TaskEntry(String lineNum, String description) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(LOCATION_TASK_ENTRY_FXML));
        loader.setController(this); // Required due to different package declaration from Main
        try {
            this.taskEntry = loader.load();
            this.lineNum.setText(lineNum);
            this.description.setText(description);
            this.time.setText(EMPTY_STRING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected TaskEntry(String lineNum, String description, String time) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(LOCATION_TASK_ENTRY_FXML));
        loader.setController(this); // Required due to different package declaration from Main
        try {
            this.taskEntry = loader.load();
            this.lineNum.setText(lineNum);
            this.description.setText(description);
            this.time.setText(time);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected Node getTaskEntry() {
        return this.taskEntry;
    }
}
