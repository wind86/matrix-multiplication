package com.matrix;

import org.apache.commons.lang3.RandomUtils;
import org.apache.log4j.Logger;

import com.matrix.multiplication.MatrixMultiplier;
import com.matrix.multiplication.MultiThreadedMatrixMultiplier;
import com.matrix.multiplication.MultiThreadedMatrixMultiplier2;
import com.matrix.multiplication.SimpleMatrixMultiplier;

public class App {

	private final static Logger logger = Logger.getLogger(App.class);

	public void run() {
		int size = 100;
		
		Matrix<Double> m1 = new Matrix<>(Double.class, generateRandomData(size, size));
		Matrix<Double> m2 = new Matrix<>(Double.class, generateRandomData(size, size));
		
		multiplyMatrixes(new SimpleMatrixMultiplier(), m1, m2);
		multiplyMatrixes(new MultiThreadedMatrixMultiplier(), m1, m2);
		multiplyMatrixes(new MultiThreadedMatrixMultiplier2(), m1, m2);
//		multiplyMatrixes(new StreamMatrixMultiplier(), m1, m2);
	}
	
	@SuppressWarnings("unchecked")
	private Matrix<? extends Number> multiplyMatrixes(MatrixMultiplier multiplier, Matrix<Double> m1, Matrix<Double> m2) {
		long startTime = System.currentTimeMillis();
		Matrix<Double> matrix = (Matrix<Double>) multiplier.multiply(m1, m2);
		
		long duration = System.currentTimeMillis() - startTime;
		logger.info(String.format("%s consumes: %d ms", multiplier.getClass().getSimpleName(), duration));
		
		return matrix;
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