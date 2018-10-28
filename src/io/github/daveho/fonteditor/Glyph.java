package io.github.daveho.fonteditor;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class Glyph {
	private int rows, cols;
	private List<BitSet> data;
	
	public Glyph(int rows, int cols) {
		this.rows = rows;
		this.cols = cols;
		this.data = new ArrayList<>();
		for (int i = 0; i < rows; i++) {
			data.add(new BitSet(cols));
		}
	}
	
	public int getRows() {
		return rows;
	}
	
	public int getCols() {
		return cols;
	}
	
	public List<BitSet> getData() {
		return data;
	}
	
	public boolean get(int r, int c) {
		return data.get(r).get(c);
	}
	
	public void set(int r, int c, boolean val) {
		data.get(r).set(c, val);
	}
}
