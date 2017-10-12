package com.matrix.multiplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import static java.util.stream.Collectors.*;
import java.util.stream.IntStream;

import org.apache.log4j.Logger;

import com.matrix.Matrix;

public class ForkJoinMatrixMultiplier implements MatrixMultiplier<Double> {

	private final static Logger logger = Logger.getLogger(ForkJoinMatrixMultiplier.class);
	
	@Override
	public Matrix<Double> multiply(Matrix<Double> m1, Matrix<Double> m2) {
		Matrix<Double> resultMatrix = new Matrix<>(Double.class, m1.getRowsSize(), m2.getColumnsSize());
		
		Map<Integer, Double[]> columnsCache = new HashMap<>();
		for (int i = 0; i < m2.getColumnsSize(); i++) {
			columnsCache.put(i, m2.getColumnElements(i));
		}
		
		ForkJoinPool pool = new ForkJoinPool(50);
		try {
			List<MatrixMultiplicationTask> tasks = new ArrayList<>();
			for (int i = 0; i < m1.getRowsSize(); i++) {
				Double[] row = m1.getRowElements(i);
				for (int j = 0; j < m2.getColumnsSize(); j++) {
					tasks.add((MatrixMultiplicationTask) pool.submit(new MatrixMultiplicationTask(i, j, row, columnsCache.get(j))));
				}
			}
			
			for (MatrixMultiplicationTask task : tasks) {
				try {
					resultMatrix.setElement(task.getRowIndex(), task.getColumnIndex(), task.get());
				} catch (InterruptedException | ExecutionException e) {
					logger.error(String.format("value (%d,%d) is not calculated", task.getRowIndex(), task.getColumnIndex()), e);
				}
			}
		} finally {
			pool.shutdown();
		}
		
		return resultMatrix;
	}
	
	private class MatrixMultiplicationTask extends RecursiveTask<Double> {

		private static final long serialVersionUID = 1812024328369430877L;

		private int i;
		private int j;
		private Double[] row;
		private Double[] column;
		private int index;

		public MatrixMultiplicationTask(int i, int j, Double[] row, Double[] column) {
			this(row, column, -1);
			this.i = i;
			this.j = j;
		}
		
		public MatrixMultiplicationTask(Double[] row, Double[] column, int index) {
			this.row = row;
			this.column = column;
			this.index = index;
		}
		
		public int getRowIndex() {
			return i;
		}
		
		public int getColumnIndex() {
			return j;
		}
		
		@Override
		protected Double compute() {
			if (index != -1) {
				return row[index] * column[index];
			}

			List<MatrixMultiplicationTask> subtasks = IntStream.range(0, row.length)
					.mapToObj(i -> new MatrixMultiplicationTask(row, column, i))
					.collect(toList());

			subtasks.forEach(task -> task.fork());

			return subtasks.stream().collect(summingDouble(task -> task.join()));
		}
	}
}