package testing;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * @author Rob
 *
 */
@RunWith(Suite.class)
@SuiteClasses({ StudentTests.class, SupervisorTests.class, ConfigTests.class, MatchingTests.class, UtilsTests.class })

public class AllTests {

}
