package com.matrix.multiplication;

import com.matrix.Matrix;

public interface MatrixMultiplier<N extends Number> {
	Matrix<N> multiply(Matrix<N> m1, Matrix<N> m2);
}