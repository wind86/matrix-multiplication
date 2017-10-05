package com.matrix.multiplication;

import static org.junit.Assert.*;
import org.junit.Test;

import com.matrix.Matrix;

public class MatrixMultiplierTest {

	private Matrix<Double> m1;
	private Matrix<Double> m2;
	private Matrix<Double> expectedMatrix;
	
	public MatrixMultiplierTest() {
		this.m1 = new Matrix<>(Double.class, new Double[][]{
			{2.0,  0.0, 4.0, -1.0},
			{1.0, -1.0, 1.0,  0.0}
		});
		
		this.m2 = new Matrix<>(Double.class, new Double[][] {
			{ 2.0 },
			{ 1.0 },
			{ 0.0 },
			{-2.0 }
		});
		
		this.expectedMatrix = new Matrix<>(Double.class, new Double[][]{
			{ 6.0 },
			{ 1.0 }
		});
	}
	
	@Test
	public void testSimpleMatrixMultiplier() {
		testMatrixMultiplier(new SimpleMatrixMultiplier());
	}
	
	@Test
	public void testMultiThreadedMatrixMultiplier() {
		testMatrixMultiplier(new MultiThreadedMatrixMultiplier());
	}
	
	@Test
	public void testMultiThreadedMatrixMultiplier2() {
		testMatrixMultiplier(new MultiThreadedMatrixMultiplier2());
	}
	
	@Test
	public void testStreamMatrixMultiplier() {
		testMatrixMultiplier(new StreamMatrixMultiplier());
	}
	
	private void testMatrixMultiplier(MatrixMultiplier multiplier) {
		@SuppressWarnings("unchecked")
		Matrix<Double> calculatedMatrix = (Matrix<Double>) multiplier.multiply(m1, m2);
		
		assertNotNull(calculatedMatrix);
		assertEquals(expectedMatrix, calculatedMatrix);
	}
}