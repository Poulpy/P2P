package test;

import fr.uvsq.fsp.util.CSVParser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import java.io.IOException;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class TestCSVParser {

    @Test
    public void testRead() throws Exception {
        Map<String, String> users;

        users = new HashMap<String, String>();

        try {
            users = CSVParser.read("build/test/central/utilisateurs.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (Map.Entry<String, String> entry : users.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }

        Assert.assertEquals(users.size(), 5);
        Assert.assertTrue(users.containsKey("toto"));
        Assert.assertEquals(users.get("toto"), "21232f297a57a5a743894a0e4a801fc3");
        Assert.assertEquals(users.get("zoiehoziroa"), null);
    }

    @Test
    public void testWrite() throws Exception {
        Map<String, String> users;

        users = new HashMap<String, String>();

        try {
            users = CSVParser.read("build/test/central/utilisateurs.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }
        users.put("Sayous", "OwO");
        CSVParser.write(users, "build/test/central/test.csv");

        try {
            users = CSVParser.read("build/test/central/test.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }

        Assert.assertEquals(users.size(), 6);
        Assert.assertTrue(users.containsKey("Sayous"));
        Assert.assertEquals(users.get("Sayous"), "OwO");
    }
}

