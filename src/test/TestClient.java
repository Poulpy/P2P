package test;

import fr.uvsq.fsp.client.FSPClient;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import java.util.ArrayList;

public class TestClient {

    public FSPClient client;

    @Before
    public void setUp() throws Exception {
        client = new FSPClient("127.0.0.1", 50000);
    }

    @Test
    public void testParseFilesFound() throws Exception {
        String found;
        ArrayList<String> files;

        found = "file1.txt file2.txt yojinbo";

        files = client.parseFilesFound(found);

        Assert.assertEquals(files.size(), 3);
    }
}

