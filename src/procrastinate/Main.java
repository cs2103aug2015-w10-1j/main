package procrastinate;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    private Logic logic;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        logic = Logic.getInstance();
        assert (logic != null);
        logic.initialiseWindow(primaryStage);
    }
}
