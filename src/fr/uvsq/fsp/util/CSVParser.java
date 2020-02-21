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

public class CSVParser {

	public static Map<String, String> read(String filePath) {
		Map<String, String> map;
		String values[];
		String line;
		BufferedReader reader;

		map = new HashMap<String, String>();
		values = new String[2];

		try {
			reader = new BufferedReader(new FileReader(filePath));

			// Lecture de chaque ligne
			while ((line = reader.readLine()) != null) {
				values = line.split(",");

				map.put(values[0], values[1]);
			}

			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return map;
	}
}

