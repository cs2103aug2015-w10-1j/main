package procrastinate.test;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import org.junit.Test;
import org.junit.Before;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import procrastinate.FileHandler;
import procrastinate.task.DateAdapter;
import procrastinate.task.Task;
import procrastinate.task.TaskDeserializer;
import procrastinate.task.TaskState;

public class FileHandlerTest {
    String defaultName;
    Path myRoot = Paths.get("");

    @Before
    public void setup() {
        defaultName = "storage.json";
    }

    @Test
    public void fileHandler_DirNotGiven_ShouldMakeFileAtCurrLoc() throws IOException {
        new FileHandler();
        Path p = Paths.get(defaultName);

        assertTrue(Files.exists(p));
        assertTrue(Files.isRegularFile(p));
        assertTrue(Files.isWritable(p));

        Files.deleteIfExists(p);
    }

    @Test
    public void fileHandler_HasProperFilename_ShouldMakeNewFile() throws IOException {
        String filename = "foo.json";
        new FileHandler(filename);
        Path p = Paths.get(filename);

        assertTrue(Files.exists(p));
        assertTrue(Files.isRegularFile(p));
        assertTrue(Files.isWritable(p));

        Files.deleteIfExists(p);
    }

    @Test
    public void fileHandler_HasRelativeDirAndFilename_ShouldMakeNewFile() throws IOException {
        String relativePath = "../" + defaultName;
        new FileHandler(relativePath);
        Path p = Paths.get(relativePath);

        assertTrue(Files.exists(p));
        assertTrue(Files.isRegularFile(p));
        assertTrue(Files.isWritable(p));

        Files.deleteIfExists(p);
    }

    @Test
    public void fileHandler_HasMultiLevelDir_ShouldMakeFileAtDir() throws IOException {
        String relativePath = "./baz/boo/bou/";
        new FileHandler(relativePath);
        Path p = Paths.get(relativePath + defaultName);

        assertTrue(Files.exists(p));
        assertTrue(Files.isRegularFile(p));
        assertTrue(Files.isWritable(p));

        for (int i = 0; i < 4; ++i) {
            Files.deleteIfExists(p);
            p = p.getParent();
        }
    }

    @Test
    public void loadTaskState_HasStorageFile_ShouldLoadState() throws IOException {
        FileHandler handler;
        BufferedReader br;
        TaskState loadedState;
        Path p = Paths.get(defaultName);

        handler = new FileHandler(defaultName);
        // save stubstate to file
        handler.saveTaskState(new TaskStateStub());

        // laod state from file
        loadedState = handler.loadTaskState();

        // mock a json file and load from it
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Task.class, new TaskDeserializer())
                .registerTypeAdapter(Date.class, new DateAdapter())
                .create();

        Type type = new TypeToken<TaskState>() {}.getType();
        br = new BufferedReader(new FileReader(new File(defaultName)));
        TaskState stub = gson.fromJson(br, type);

        assertEquals(stub, loadedState);

        Files.deleteIfExists(p);
    }

    @Test
    public void loadTaskState_NoStorageFile_ShouldInitAndLoad() throws IOException {
        FileHandler handler;
        TaskState loadedState;
        Path p = Paths.get(defaultName);

        handler = new FileHandler();

        // load state from file
        loadedState = handler.loadTaskState();

        assertEquals(0, loadedState.getTasks().size());

        Files.deleteIfExists(p);
    }

    @Test
    public void makeConfig_NoConfigFile_ShouldMakeFile() throws IOException {
        new FileHandler();
        BufferedReader reader = new BufferedReader(new FileReader(Paths.get("settings.config").toFile()));
        String line;
        if ((line = reader.readLine()) != null) {
            assertEquals(defaultName, line);
        } else {
            fail();
        }
        reader.close();
    }

}