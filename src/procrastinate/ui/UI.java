package procrastinate.ui;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.converter.NumberStringConverter;

import procrastinate.task.Task;

public class UI {

    private static final Logger logger = Logger.getLogger(UI.class.getName());

    // ================================================================================
    // Message strings
    // ================================================================================

    private static final String DEBUG_UI_INIT = "UI initialised. View is now loaded!";

    private static final String MESSAGE_WELCOME = "What would you like to Procrastinate today?";

    private static final String UI_NUMBER_SEPARATOR = ". ";

    // ================================================================================
    // Class variables
    // ================================================================================

    private IntegerProperty taskCount = new SimpleIntegerProperty(1);

    private ObservableList<String> taskList = FXCollections.observableArrayList();

    private StringProperty taskCountFormatted = new SimpleStringProperty();
    private StringProperty taskCountString = new SimpleStringProperty();

    // ================================================================================
    // FXML field variables
    // ================================================================================

    @FXML private BorderPane mainBorderPane;
    @FXML private Label statusLabel;
    @FXML private ListView<String> taskListView;
    @FXML private TextField userInputField;

    // ================================================================================
    // UI methods
    // ================================================================================

    private Parent root;

    public UI() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainWindowLayout.fxml"));
        loader.setController(this); // Required due to different package declaration from Main
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // This auto gets called from the UI constructor when load is executed.
    public void initialize() {
        initTaskDisplay();

        logger.log(Level.INFO, DEBUG_UI_INIT);
    }

    public void updateTaskList(List<Task> tasks) {
        taskList.clear();
        taskCount.set(1);
        for (Task task : tasks) {
            taskList.add(taskCountFormatted.get() + task.getDescription());
            taskCount.set(taskCount.get() + 1);
        }
        updateListView();
    }

    // ================================================================================
    // Logic call methods
    // ================================================================================

    public void setUpBinding(StringProperty userInput, StringProperty statusLabelText) {
        initBinding(userInput, statusLabelText);
    }

    public void setUpStage(Stage primaryStage) {
        primaryStage.setScene(new Scene(root, 500, 500));
        primaryStage.show();
    }

    // ================================================================================
    // Init methods
    // ================================================================================

    private void initTaskDisplay() {
        taskListView.setPlaceholder(new Label(MESSAGE_WELCOME));
    }

    private void initBinding(StringProperty userInput, StringProperty statusLabelText) {
        userInput.bindBidirectional(userInputField.textProperty());
        statusLabelText.bindBidirectional(statusLabel.textProperty());
        taskCountString.bindBidirectional(taskCount, new NumberStringConverter());
        taskCountFormatted.bind(Bindings.concat(taskCountString).concat(UI_NUMBER_SEPARATOR));
    }

    // ================================================================================
    // Utility methods
    // ================================================================================

    private void updateListView() {
        taskListView.setItems(taskList);
    }

    public void clearInput() {
        userInputField.clear();
    }

    // ================================================================================
    // Getter methods
    // ================================================================================

    public TextField getUserInputField() {
        return userInputField;
    }
}
