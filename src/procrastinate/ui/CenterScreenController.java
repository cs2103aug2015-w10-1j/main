package procrastinate.ui;

import javafx.scene.Node;
import javafx.scene.layout.StackPane;

import java.util.HashMap;

public class CenterScreenController {

    private HashMap<Integer, Node> controlledScreens;
    private StackPane centerStackPane;

    protected static final int SCREEN_MAIN = 1;
    protected static final int SCREEN_SEARCH = 2;
    protected static final int SCREEN_HELP = 3;  // Maybe should use arraylist of these integers/string to reference the integers?

    public CenterScreenController() {
        this.controlledScreens = new HashMap<Integer, Node>();
        this.centerStackPane = new StackPane();
    }
    
    public Node changeScreen(int screenKey) {
        return controlledScreens.get(screenKey);
    }

    private void addScreen(int screenKey, Node newScreen) {
        controlledScreens.put(screenKey, newScreen);
    }

    protected StackPane getCenterStackPane() {
        return centerStackPane;
    }
}
