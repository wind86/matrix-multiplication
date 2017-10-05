package com.matrix.multiplication;

import java.util.Arrays;
import java.util.stream.IntStream;

import com.matrix.Matrix;

public class StreamMatrixMultiplier implements MatrixMultiplier {

	@Override
	public Matrix<? extends Number> multiply(Matrix<? extends Number> m1, Matrix<? extends Number> m2) {
//		Double[][] result = Arrays.stream(m1.getData()).map(rw ->
//			IntStream.range(0, m2.getColumnsSize()).mapToDouble(j ->
//				IntStream.range(0, m1.getRowsSize()).mapToDouble(k -> {
//					return rw[k].doubleValue() * m2.getElement(k, j).doubleValue();
//				}).sum()
//			).toArray()
//		).toArray(Double[][]::new);
//		
//		return new Matrix<Double>(Double.class, result);
		return null;
	}

}