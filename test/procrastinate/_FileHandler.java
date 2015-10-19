package procrastinate;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;

public class _FileHandler {
	String defaultName;
	Path path;
	Path myRoot = Paths.get("");

	@Before
	public void setup() {
		defaultName = "storage.json";
	}

	@Test
	public void fileHandler_DirNotGiven_ShouldMakeFileAtCurrLoc() throws IOException {
	    FileHandler handler = new FileHandler();
	    Path p = Paths.get(defaultName);
	    path = p;

	    assertTrue(Files.exists(p));
	    assertTrue(Files.isRegularFile(p));
	    assertTrue(Files.isWritable(p));

	    Files.deleteIfExists(p);
	}

	@Test
	public void fileHandler_HasProperFilename_ShouldMakeNewFile() throws IOException {
		String filename = "foo.json";
		FileHandler handler = new FileHandler(filename);
		Path p = Paths.get(filename);
		path = p;

		assertTrue(Files.exists(p));
		assertTrue(Files.isRegularFile(p));
		assertTrue(Files.isWritable(p));

		Files.deleteIfExists(p);
	}

	@Test
	public void fileHandler_HasRelativeDirAndFilename_ShouldMakeNewFile() throws IOException {
		String relativePath = "../" + defaultName;
		FileHandler handler = new FileHandler(relativePath);
		Path p = Paths.get(relativePath);
		path = p;

		assertTrue(Files.exists(p));
		assertTrue(Files.isRegularFile(p));
		assertTrue(Files.isWritable(p));

		Files.deleteIfExists(p);
	}

    @Test
    public void fileHandler_HasMultiLevelDir_ShouldMakeFileAtDir() throws IOException {
        String relativePath = "./baz/boo/bou/";
        FileHandler handler = new FileHandler(relativePath);
        Path p = Paths.get(relativePath + defaultName);
        path = p;

        assertTrue(Files.exists(p));
        assertTrue(Files.isRegularFile(p));
        assertTrue(Files.isWritable(p));

        for (int i = 0; i < 4; ++i) {
        	Files.deleteIfExists(p);
        	p = p.getParent();
        }
    }
}
