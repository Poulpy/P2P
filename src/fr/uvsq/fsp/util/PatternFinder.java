package fr.uvsq.fsp.util;

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

/**
 * Similaire à grep
 */
public class PatternFinder {

    /**
     * Find a pattern in a file
     */
    public static boolean grepFile(String pattern, String filePath) throws IOException {
        BufferedReader reader;
        String currentLine;
        File fileToSearch;
        boolean match;

        match = false;
        fileToSearch = new File(filePath);
        reader = new BufferedReader(new FileReader(fileToSearch));

        while ((currentLine = reader.readLine()) != null && !match) {
            if (currentLine.contains(pattern)) {
                match = true;
            }
        }

        reader.close();

        return match;
    }

    /**
     * Find a pattern in a folder. Recursive
     */
    public static ArrayList<String> grepFolder(String pattern, String folderPath) throws IOException {
        File fileToSearch;
        ArrayList<String> filesMatching = new ArrayList<String>();
        ArrayList<String> files = new ArrayList<String>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(folderPath))) {
            for (Path path : stream) {
                fileToSearch = new File(path.toString());
                if (fileToSearch.isFile()) {
                    if (PatternFinder.grepFile(pattern, path.toString())) {
                        filesMatching.add(path.toString());
                    }
                } else if (fileToSearch.isDirectory()) {
                    files = PatternFinder.grepFolder(pattern, path.toString());
                    filesMatching.addAll(files);
                }
            }
        }

        return filesMatching;
    }

    /**
     * build/central/descriptions
     * Gives :
     * dinfo/yojinbo
     * and not
     * build/central/descriptions/dinfo/yojinbo
     *
     */
    public static ArrayList<String> grepFolder(String pattern, String folderPath, boolean relative) throws IOException {
        ArrayList<String> results;
        int index;

        results = new ArrayList<String>();

        results = grepFolder(pattern, folderPath);

        if (relative) {
            ArrayList<String> withRelative = new ArrayList<String>();
            for (String path : results) {
                withRelative.add(path.substring(folderPath.length()));
            }
            return withRelative;
        } else {
            return results;
        }
    }
}

