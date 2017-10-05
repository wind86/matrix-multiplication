package com.matrix.multiplication;

import com.matrix.Matrix;

public interface MatrixMultiplier {
	Matrix<? extends Number> multiply(Matrix<? extends Number> m1, Matrix<? extends Number> m2);
}