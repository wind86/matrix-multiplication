package com.matrix;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Matrix<N extends Number> {

	private final int rows;
	private final int columns;
	private final Class<N> clazz;
	private final N[][] array;
	
	@SuppressWarnings("unchecked")
	public Matrix(Class<N> clazz, int rows, int columns) {
		this.rows = rows;
		this.columns = columns;
		this.clazz = clazz;
		this.array =  (N[][]) Array.newInstance(clazz, rows, columns);
	}
	
	public Matrix(Class<N> clazz, N[][] data) {
		this(clazz, data.length, data[0].length);

		for (int i = 0; i < data.length; i++) {
			for (int j = 0; j < data[0].length; j++) {
				this.array[i][j] = data[i][j];
			}
		}
	}
	
	public Class<N> getElementClass() {
		return this.clazz;
	}
	
	public int getRowsSize() {
		return this.rows;
	}
	
	public int getColumnsSize() {
		return this.columns;
	}
	
	public N[][] getData() {
		return array;
	}
	
	public N[] getRowElements(int rowIndex) {
		checkRow(rowIndex);
		
		return this.array[rowIndex];
	}
	
	@SuppressWarnings("unchecked")
	public N[] getColumnElements(int columnIndex) {
		checkColumn(columnIndex);
		
//		@SuppressWarnings("unchecked")
//		N[] elements = (N[]) Array.newInstance(clazz, rows);
//		for (int i = 0; i < this.rows; i++) {
//			elements[i] = this.array[i][columnIndex];
//		}
//		return elements;
		
		return IntStream.range(0, this.rows).parallel()
				.mapToObj(i -> this.array[i][columnIndex])
				.collect(Collectors.toList())
				.toArray((N[]) Array.newInstance(clazz, rows));
	}
	
	public N getElement(int rowIndex, int columnIndex) {
		checkRow(rowIndex);
		checkColumn(columnIndex);

		return this.array[rowIndex][columnIndex];
	}
	
	public void setElement(int rowIndex, int columnIndex, N value) {
		checkRow(rowIndex);
		checkColumn(columnIndex);

		this.array[rowIndex][columnIndex] = value;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.deepHashCode(array);
		result = prime * result + ((clazz == null) ? 0 : clazz.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		@SuppressWarnings("rawtypes")
		Matrix other = (Matrix) obj;
		if (!Arrays.deepEquals(array, other.array))
			return false;
		if (clazz == null) {
			if (other.clazz != null)
				return false;
		} else if (!clazz.equals(other.clazz))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < this.rows; i++)
			for (int j = 0; j < this.columns; j++)
				sb.append(String.valueOf(this.array[i][j])).append(j == this.columns - 1 ? System.lineSeparator() : ";");
		
		return sb.toString();
	}
	
	private void checkRow(int rowIndex) {
		checkMatrixIndex("row", rowIndex, rows);
	}

	private void checkColumn(int columnIndex) {
		checkMatrixIndex("column", columnIndex, columns);
	}
	
	private void checkMatrixIndex(String type, int index, int max) {
		if (index < 0 || index >= max) {
			throw new ArrayIndexOutOfBoundsException(String.format("%s (%d) does not exist", type, index));
		}
	}
}