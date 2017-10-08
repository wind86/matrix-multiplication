package com.matrix.multiplication.hadoop;

import static org.apache.commons.io.FileUtils.deleteDirectory;
import static org.apache.commons.io.FileUtils.readLines;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.After;
import org.junit.Test;

public class HadoopMatrixMultiplierTest {

	private static final String INPUT_FOLDER = "./src/test/resources/hadoop/input";
	private static final String OUTPUT_FOLDER = "./src/test/resources/hadoop/output";

	private static final String SUCCESS_FILE_NAME = "_SUCCESS";
	private static final String RESULT_FILE_NAME = "part-r-00000";

	private static final String[] EXPECTED_MULTIPLICATION_RESULT = new String[] {
		"0,0,6.0",
		"1,0,1.0"
	};

	@Test
	public void testHadoopMatrixMultiplication() throws ClassNotFoundException, IOException, InterruptedException {
		File outputFolder = new File(OUTPUT_FOLDER);
		assertFalse(outputFolder.exists());

		HadoopMatrixMultiplier hadoopMatrixMultiplier = new HadoopMatrixMultiplier(INPUT_FOLDER, OUTPUT_FOLDER, 2, 4, 4, 1);
		hadoopMatrixMultiplier.calculate();

		assertTrue(outputFolder.exists());

		File[] outputFiles = outputFolder.listFiles();
		assertEquals(4, outputFiles.length);

		assertFileExists(outputFolder, SUCCESS_FILE_NAME);
		assertFileExists(outputFolder, RESULT_FILE_NAME);

		List<String> multiplecationResults = readLines(new File(outputFolder, RESULT_FILE_NAME));
		assertArrayEquals(EXPECTED_MULTIPLICATION_RESULT, multiplecationResults.toArray(new String[0]));
	}

	@After
	public void tearDown() throws IOException {
		deleteDirectory(new File(OUTPUT_FOLDER));
	}

	private static void assertFileExists(File folder, String fileName) {
		File[] files = folder.listFiles((dir, name) -> fileName.equals(name));
		assertEquals(1, files.length);
	}
}