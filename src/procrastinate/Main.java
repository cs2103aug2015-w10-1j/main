package procrastinate;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private static final String WINDOW_TITLE = "Procrastinate";
    private static final double WINDOW_WIDTH = 500;
    private static final double WINDOW_HEIGHT = 600;

    public static void main(String[] args) {
        launch(args);
    }

	@Override
	public void start(Stage primaryStage) {
        Parent root;
        try {
            root = FXMLLoader.load(getClass().getResource("RootLayout.fxml"));
            primaryStage.setTitle(WINDOW_TITLE);
            primaryStage.setScene(new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT));
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

}
