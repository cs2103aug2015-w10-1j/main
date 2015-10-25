package procrastinate.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
    FileHandlerTest.class,
    LogicTest.class,
    ParserTest.class,
    UITest.class
    })
public class AllTests {

}
