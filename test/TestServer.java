package test;

import client.FSPClient;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.io.File;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import server.FSPCentral;

public class TestServer {

    public FSPCentral central;

    @Before
    public void setUp() throws Exception {
        central = new FSPCentral("127.0.0.1", 50000);
        central.cheminUtilisateurs = "test/utilisateurs.csv";
    }

    @Test
    public void testUtilisateurExiste() throws Exception {
        Assert.assertTrue(central.utilisateurExiste("toto"));
        Assert.assertTrue(central.utilisateurExiste("titi"));
        Assert.assertFalse(central.utilisateurExiste("totot"));
    }

    @Test
    public void testMdpCorrect() throws Exception {
        Assert.assertTrue(central.mdpCorrect("toto", "4cb9c8a8048fd02294477fcb1a41191a"));
        Assert.assertFalse(central.mdpCorrect("toto", "a"));
        Assert.assertTrue(central.mdpCorrect("titi", "21232f297a57a5a743894a0e4a801fc3"));
    }

    @Test
    public void testEnregistrerUtilisateur() throws Exception {
        Assert.assertFalse(central.utilisateurExiste("pouic"));
        central.EnregistrerUtilisateur("pouic", "hey");
        Assert.assertTrue(central.utilisateurExiste("pouic"));
        Assert.assertTrue(central.mdpCorrect("pouic", "hey"));
        Assert.assertTrue(central.enleverUtilisateur("pouic"));
        Assert.assertFalse(central.utilisateurExiste("pouic"));
    }

    @Test
    public void testSearchByKeyword() throws Exception {
        File fileToSearch = new File("quigon/yojinbo");
        Assert.assertTrue(central.searchByKeyword("Le", fileToSearch));
        Assert.assertTrue(central.searchByKeyword("garde", fileToSearch));
        Assert.assertTrue(central.searchByKeyword("du", fileToSearch));
        Assert.assertFalse(central.searchByKeyword("Truc", fileToSearch));
    }

    @Test
    public void testSearchFolderByKeyword() throws Exception {
        String folderToSearch = "quigon/";
        ArrayList<String> emp = new ArrayList<String>();
        Map<String, Boolean> keywords = new HashMap<String, Boolean>();
        ArrayList<String> matches = new ArrayList<String>();

        keywords.put("Le", true);
        keywords.put("garde", true);
        keywords.put("du", true);
        keywords.put("Truc", false);

        for (Map.Entry<String, Boolean> keyword : keywords.entrySet()) {

            matches = central.searchFolderByKeyword(keyword.getKey(), folderToSearch);
            /*
            if (matches.size() > 0)
                System.out.println(matches.get(0).toString());
                */

            if (keyword.getValue()) {
                Assert.assertNotEquals(matches.size(), 0);
            } else {
                Assert.assertEquals(matches.size(), 0);
            }
        }
    }
}

