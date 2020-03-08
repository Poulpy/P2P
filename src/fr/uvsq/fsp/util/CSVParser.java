package fr.uvsq.fsp.util;

import java.io.IOException;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

/**
 * Reads a writes to a csv file
 */
public class CSVParser {

    /**
     * Reads a CSV file and returns a map
     * @throws IOException
     */
    public static Map<String, String> read(String filePath) throws IOException {
        Map<String, String> map;
        String values[];
        String line;
        BufferedReader reader;

        map = new HashMap<String, String>();
        values = new String[2];

        reader = new BufferedReader(new FileReader(filePath));

        // Lecture de chaque ligne
        while ((line = reader.readLine()) != null) {
            values = line.split(",");

            map.put(values[0], values[1]);
        }

        reader.close();

        return map;
    }

    /**
     * Write the key-value pair of a map a csv file
     * key,value
     * @param map La table de hashage a écrire dans le fichier csv
     * @param filePath Le chemin du fichier csv
     * @throws IOException
     */
    public static void write(Map<String, String> map, String filePath) throws IOException {
        BufferedWriter writer;

        writer = new BufferedWriter(new FileWriter(filePath));

        for (Map.Entry<String, String> entry : map.entrySet()) {
            writer.write(entry.getKey() + "," + entry.getValue() + System.getProperty("line.separator"));
        }

        writer.close();
    }
}

