package com.matrix.multiplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.apache.log4j.Logger;

import com.matrix.Matrix;

public class MultiThreadedMatrixMultiplier implements MatrixMultiplier<Double> {
	
	private final static Logger logger = Logger.getLogger(MultiThreadedMatrixMultiplier.class);
	
	private final int threads;
	
	public MultiThreadedMatrixMultiplier() {
		this(50);
	}
	
	public MultiThreadedMatrixMultiplier(int threads) {
		this.threads = threads;
	}

	@Override
	public Matrix<Double> multiply(Matrix<Double> m1, Matrix<Double> m2) {
		Matrix<Double> matrix = new Matrix<>(Double.class, m1.getRowsSize(), m2.getColumnsSize());
		
		ExecutorService multiplyExecutor = Executors.newFixedThreadPool(threads);
		try {
			calculate(multiplyExecutor, m1, m2).forEach(future -> processResult(matrix, future));
		} finally {
			shutdownExecutor(multiplyExecutor);
		}
		
		return matrix;
	}
	
	private List<Future<MultipliedValue>> calculate(ExecutorService executor, Matrix<Double> m1, Matrix<Double> m2) {
		List<Future<MultipliedValue>> futures = new ArrayList<>();
		
		Map<Integer, Double[]> columns = new HashMap<>(); 
		for (int i = 0; i < m2.getColumnsSize(); i++) {
			columns.put(i, m2.getColumnElements(i));
		}

		for (int i = 0; i < m1.getRowsSize(); i++) {
			Double[] rowElements = m1.getRowElements(i);
			for (int j = 0; j < m2.getColumnsSize(); j++) {
				futures.add(executor.submit(new MatrixMultiplicationTask(i, j, rowElements, columns.get(j))));
			}
		}

		return futures;
	}
	
	private void processResult(Matrix<Double> matrix, Future<MultipliedValue> future) {
		try {
			MultipliedValue mv = future.get();
			matrix.setElement(mv.getRowIndex(), mv.getColumnIndex(), (Double) mv.getValue());
		} catch (InterruptedException | ExecutionException e) {
			logger.error("unable to process multiplication result", e);
		}
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
	
	private final class MatrixMultiplicationTask implements Callable<MultipliedValue> {

		private final int rowIndex;
		private final int columnIndex;
		private final Double[] rowValues;
		private final Double[] columnValues;
		
		public MatrixMultiplicationTask(int rowIndex, int columnIndex, Double[] rowValues, Double[] columnValues) {
			this.rowIndex = rowIndex;
			this.columnIndex = columnIndex;
			this.rowValues = rowValues;
			this.columnValues = columnValues;
		}

		@Override
		public MultipliedValue call() {
			logger.debug(String.format("calculating %d,%d", rowIndex, columnIndex));
			return new MultipliedValue(rowIndex, columnIndex, new Double(calculateValue()));
		}
		
		private double calculateValue() {
			return IntStream.range(0, rowValues.length)//.parallel()
					.mapToDouble(i -> rowValues[i].doubleValue() * columnValues[i].doubleValue())
					.sum();
		}
	}
	
	private final class MultipliedValue {
		
		private final int rowIndex;
		private final int columnIndex;
		private final Number value;
		
		public MultipliedValue(int row, int column, Number value) {
			this.rowIndex = row;
			this.columnIndex = column;
			this.value = value;
		}
		
		public int getRowIndex() {
			return rowIndex;
		}
		
		public int getColumnIndex() {
			return columnIndex;
		}
		
		public Number getValue() {
			return value;
		}
	}
}