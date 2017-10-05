package com.matrix.multiplication;

import com.matrix.Matrix;

public class SimpleMatrixMultiplier implements MatrixMultiplier {

	@Override
	public Matrix<? extends Number> multiply(Matrix<? extends Number> m1, Matrix<? extends Number> m2) {
		Matrix<Double> matrix = new Matrix<>(Double.class, m1.getRowsSize(), m2.getColumnsSize());
		
		for (int i = 0; i < m1.getRowsSize(); i++) {
			for (int j = 0; j < m2.getColumnsSize(); j++) {
				double value = 0;
				for (int k = 0; k < m1.getColumnsSize(); k++) {
					value += m1.getElement(i, k).doubleValue() * m2.getElement(k, j).doubleValue();
				}
				matrix.setElement(i, j, new Double(value));
			}
		}
		
		return matrix;
	}
}
