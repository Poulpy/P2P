package test;

import client.FSPClient;
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

    @Test
    public void testChiffrement() throws Exception {
        Assert.assertEquals(client.chiffrage("admin"), client.chiffrage("admin"));
        Assert.assertNotEquals(client.chiffrage("admin"), client.chiffrage("ADMIN"));
    }
}

