package fr.uvsq.fsp.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileLister {


	public static ArrayList<String> getFileNames(ArrayList<String> fileNames, Path dir) {
		try(DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
			for (Path path : stream) {
				if(path.toFile().isDirectory()) {
					getFileNames(fileNames, path);
				} else {
					//fileNames.add(path.toAbsolutePath().toString());
					fileNames.add(path.toString());
					//System.out.println(path.getFileName());
				}
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
		return fileNames;
	}

	public static ArrayList<String> list(String folder, int level) {
		if (level < 0) throw new NumberFormatException("level must not be < 0");

		ArrayList<String> fileNames;
		ArrayList<String> subFolderFiles;
		String files[];

		fileNames = new ArrayList<String>();
		subFolderFiles = new ArrayList<String>();
		files = (new File(folder)).list();

		for (int i = 0; i != files.length; i++) {
			if (level != 0 && (new File(folder + files[i])).isDirectory()) {
				subFolderFiles.addAll(FileLister.list(folder + files[i], level - 1));
				fileNames.addAll(FileLister.prefix(subFolderFiles, files[i]+"/"));
				subFolderFiles.clear();
			} else {
				fileNames.add(files[i]);
			}
		}

		return fileNames;
	}

	private static ArrayList<String> prefix(ArrayList<String> toPrefix, String prefix) {
		ArrayList<String> withPrefix;

		withPrefix = new ArrayList<String>();

		for (String str : toPrefix) {
			withPrefix.add(prefix + str);
		}

		return withPrefix;
	}
}
