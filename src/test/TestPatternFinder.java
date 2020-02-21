package test;

import fr.uvsq.fsp.util.PatternFinder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestPatternFinder {

    @Test
    public void testGrepFile() throws Exception {
        Assert.assertTrue(PatternFinder.grepFile("Akira", "build/client/descriptions/yojinbo"));
    }

    @Test
    public void testGrepFolder() throws Exception {
        ArrayList<String> files;

        files = new ArrayList<String>();
        files = PatternFinder.grepFolder("Akira", "build/test/central/descriptions/", true);
        System.out.println(files);
        Assert.assertEquals(files.size(), 1);
    }
}

