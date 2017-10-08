package com.matrix;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.RandomUtils;
import org.apache.log4j.Logger;

import com.matrix.multiplication.MatrixMultiplier;
import com.matrix.multiplication.MultiThreadedMatrixMultiplier;
import com.matrix.multiplication.MultiThreadedMatrixMultiplier2;
import com.matrix.multiplication.SimpleMatrixMultiplier;
import com.matrix.multiplication.StreamMatrixMultiplier;
import com.matrix.multiplication.hadoop.HadoopMatrixMultiplier;

public class App {

	private final static Logger logger = Logger.getLogger(App.class);

	private static final String INPUT_FOLDER = "./input";
	private static final String OUTPUT_FOLDER = "./output";
	
	public void run() {
		int size = 100;
		
		Matrix<Double> m1 = new Matrix<>(Double.class, generateRandomData(size, size));
		Matrix<Double> m2 = new Matrix<>(Double.class, generateRandomData(size, size));
		
		List<MatrixMultiplier<Double>> multipliers = Arrays.asList(
				new SimpleMatrixMultiplier(),
				new MultiThreadedMatrixMultiplier(),
				new MultiThreadedMatrixMultiplier2(),
				new StreamMatrixMultiplier());
		
		multipliers.forEach(multiplier -> multiplyMatrixes(multiplier, m1, m2));
		
		hadoopMatrixMultiplication();
	}
	
	private Matrix<Double> multiplyMatrixes(MatrixMultiplier<Double> multiplier, Matrix<Double> m1, Matrix<Double> m2) {
		long startTime = System.currentTimeMillis();
		Matrix<Double> matrix = (Matrix<Double>) multiplier.multiply(m1, m2);
		
		long duration = System.currentTimeMillis() - startTime;
		logger.info(String.format("%s consumes: %d ms", multiplier.getClass().getSimpleName(), duration));
		
		return matrix;
	}
	
	private void hadoopMatrixMultiplication() {
		HadoopMatrixMultiplier hadoopMatrixMultiplier = new HadoopMatrixMultiplier(INPUT_FOLDER, OUTPUT_FOLDER, 1000, 100, 100, 1000);
		try {
			hadoopMatrixMultiplier.calculate();
		} catch (Exception e) {
			logger.error("hadoop execution failed", e);
		}
	}

	private static Double[][] generateRandomData(int rows, int columns) {
		Double[][] data = new Double[rows][columns];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				data[i][j] = RandomUtils.nextDouble(0, 100);
			}
		}
		return data;
	}
	
	public static void main(String[] args) {
		new App().run();
	}
}