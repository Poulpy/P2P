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
	public void testSearchByKeyword() throws Exception {
		//central.handleHostnameCommand("dinfo");
		File fileToSearch = new File(central.descriptionsFolder + "dinfo/yojinbo");
		Assert.assertTrue(central.searchByKeyword("Le", fileToSearch));
		Assert.assertTrue(central.searchByKeyword("garde", fileToSearch));
		Assert.assertTrue(central.searchByKeyword("du", fileToSearch));
		Assert.assertFalse(central.searchByKeyword("Truc", fileToSearch));
	}

	@Test
	public void testSearchFolderByKeyword() throws Exception {
		String folderToSearch = central.descriptionsFolder + "dinfo/";
		ArrayList<String> emp = new ArrayList<String>();
		Map<String, Boolean> keywords = new HashMap<String, Boolean>();
		ArrayList<String> matches = new ArrayList<String>();

		keywords.put("Le", true);
		keywords.put("garde", true);
		keywords.put("du", true);
		keywords.put("film", true);
		keywords.put("Truc", false);

		for (Map.Entry<String, Boolean> keyword : keywords.entrySet()) {

			matches = central.searchFolderByKeyword(keyword.getKey(), folderToSearch);
			/*
			if (matches.size() > 0)
				System.out.println(matches.get(0).toString());
				*/

			if (keyword.getValue()) {
				//System.out.println(matches);
				Assert.assertTrue(matches.size() > 0);
			} else {
				Assert.assertEquals(matches.size(), 0);
			}
		}
	}

	@Test
	public void testSearchUsersFoldersByKeyword() throws Exception {
		ArrayList<String> connected = new ArrayList<String>();
		ArrayList<String> files = new ArrayList<String>();

		files = central.searchUsersFoldersByKeyword("Le");
		// Pas d'utilisateurs : size() == 0
		Assert.assertEquals(files.size(), 0);
		central.usersConnected.add("dinfo");
		// size() == 1
		files = central.searchUsersFoldersByKeyword("étoiles");
		Assert.assertEquals(files.size(), 1);

		files = central.searchUsersFoldersByKeyword("film");
		//System.out.println(files);
		Assert.assertEquals(files.size(), 2);
	}
}

