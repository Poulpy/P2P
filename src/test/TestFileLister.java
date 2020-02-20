package test;

import fr.uvsq.fsp.util.FileLister;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import java.util.List;
import java.util.ArrayList;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestFileLister {

	@Test
	public void testList() throws Exception {
		ArrayList<String> a;
		Path p;
/*
		p = Paths.get("build/client/downloads/");

		a = new ArrayList<String>();
		a = FileLister.getFileNames(a, p);
		for (String file : a)
			System.out.println("File : " + a);
		System.out.println("+++++++");

		a = new ArrayList<String>();
		a = FileLister.getFileNames(a, p);
		p = Paths.get("build/client/shared/");
		for (String file : a)
			System.out.println("File : " + a);
		System.out.println("+++++++");
		*/

		a = FileLister.list("build/client/downloads/", 1);
		System.out.println(a);
		System.out.println("+++++++");
		a = FileLister.list("build/client/shared/", 0);
		System.out.println(a);
	}
}


