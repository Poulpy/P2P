package test;

import fr.uvsq.fsp.client.FSPClient;
import fr.uvsq.fsp.server.FSPCentral;
import java.io.File;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestServer {

	public FSPCentral central;
	public Socket sock;

	@Before
	public void setUp() throws Exception {
		central = new FSPCentral(sock, "build/test/central/");
	}

	@Test
	public void testIsClientLogged() throws Exception {
		central.hostname = "dinfo";
		Assert.assertFalse(central.isClientLogged());
	}
}

