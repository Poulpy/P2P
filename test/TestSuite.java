package test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    TestChecksum.class,
    TestClient.class,
    TestServer.class
})

public class TestSuite {
}

