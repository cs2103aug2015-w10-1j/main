package procrastinate.test;

import java.io.IOException;

import javafx.stage.Stage;
import procrastinate.Logic;

public class LogicUnit extends Logic {
    private UIStub uiStub;
    public LogicUnit(UIStub uiStub) {
        this.uiStub = uiStub;
    }
    @Override
    public void initUi(Stage stage) {
        ui = uiStub;
    }
    @Override
    protected void initTaskEngine() throws IOException {
        taskEngine = new TaskEngineUnit();
    }
}
