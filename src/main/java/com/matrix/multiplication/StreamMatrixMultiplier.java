package com.matrix.multiplication;

import java.util.stream.IntStream;
import com.matrix.Matrix;

public class StreamMatrixMultiplier implements MatrixMultiplier<Double> {

	@Override
	public Matrix<Double> multiply(Matrix<Double> m1, Matrix<Double> m2) {
		Matrix<Double> matrix = new Matrix<>(Double.class, m1.getRowsSize(), m2.getColumnsSize());
		
		IntStream.range(0, m1.getRowsSize()).forEach(i ->
			IntStream.range(0, m2.getColumnsSize())
				.forEach(j -> matrix.setElement(i, j, 
						IntStream.range(0, m2.getRowsSize())
							.mapToDouble(k -> m1.getElement(i, k).doubleValue() * m2.getElement(k, j).doubleValue())
							.sum()
						))
		);
		
		return matrix;
	}
}