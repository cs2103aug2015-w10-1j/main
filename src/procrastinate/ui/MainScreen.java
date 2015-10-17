package procrastinate.ui;

public class MainScreen extends CenterScreen {

    protected MainScreen(String filePath) {
        super(filePath);
    }

    protected void say() {
        System.out.println("Say something");
    }
}
