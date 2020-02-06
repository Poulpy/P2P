package fr.uvsq.fsp.test;

import fr.uvsq.fsp.client.FSPClient;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestClient {

    public FSPClient client;

    @Before
    public void setUp() throws Exception {
        client = new FSPClient("127.0.0.1", 50000);
    }
}

