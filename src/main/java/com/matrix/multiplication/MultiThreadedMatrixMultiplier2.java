package com.matrix.multiplication;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.apache.log4j.Logger;

import com.matrix.Matrix;

public class MultiThreadedMatrixMultiplier2 implements MatrixMultiplier<Double> {
	
	private final static Logger logger = Logger.getLogger(MultiThreadedMatrixMultiplier2.class);
	
	private final int threads;
	
	public MultiThreadedMatrixMultiplier2() {
		this(50);
	}
	
	public MultiThreadedMatrixMultiplier2(int threads) {
		this.threads = threads;
	}

	@Override
	public Matrix<Double> multiply(Matrix<Double> m1, Matrix<Double> m2) {
		Matrix<Double> matrix = new Matrix<>(Double.class, m1.getRowsSize(), m2.getColumnsSize());
		
		ExecutorService multiplyExecutor = Executors.newFixedThreadPool(threads);
		try {
			for (int i = 0; i < m1.getRowsSize(); i++) {
				for (int j = 0; j < m2.getColumnsSize(); j++) {
					multiplyExecutor.submit(new DoubleMatrixMultiplicationTask(matrix, i, j, m1, m2));
				}
			}
		} finally {
			shutdownExecutor(multiplyExecutor);
		}
		
		return matrix;
	}

	private void shutdownExecutor(ExecutorService executor) {
		try {
			executor.shutdown();
			executor.awaitTermination(1, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			logger.error("unable to shutdown multiplication executor", e);
		} finally {
			if (!executor.isTerminated()) {
				logger.error("cancel non-finished tasks");
			}
			executor.shutdownNow();
		}
	}
	
	
	private abstract class MatrixMultiplicationTask<N extends Number> implements Runnable {

		protected final Matrix<N> matrix;
		protected final int rowIndex;
		protected final int columnIndex;
		
		public MatrixMultiplicationTask(Matrix<N> matrix, int rowIndex, int columnIndex) {
			this.matrix = matrix;
			this.rowIndex = rowIndex;
			this.columnIndex = columnIndex;
		}

		@Override
		public final void run() {
			matrix.setElement(rowIndex, columnIndex, calculateValue());
		}
		
		protected abstract N calculateValue();
	}
	
	private final class DoubleMatrixMultiplicationTask extends MatrixMultiplicationTask<Double> {
		
		private final Matrix<? extends Number> m1;
		private final Matrix<? extends Number> m2;

		public DoubleMatrixMultiplicationTask(Matrix<Double> matrix, int rowIndex, int columnIndex, Matrix<? extends Number> m1, Matrix<? extends Number> m2) {
			super(matrix, rowIndex, columnIndex);
			this.m1 = m1;
			this.m2 = m2;
		}
		
		@Override
		protected Double calculateValue() {
			Number[] rowValues = m1.getRowElements(rowIndex);
			Number[] columnValues = m2.getColumnElements(columnIndex);
			
			return IntStream.range(0, rowValues.length)//.parallel()
					.mapToDouble(i -> rowValues[i].doubleValue() * columnValues[i].doubleValue())
					.sum();
		}
	}
}