package io.github.daveho.fonteditor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Observable;
import java.util.Scanner;

public class BitmapFont extends MyObservable {
	private int rows, cols;
	private List<Glyph> glyphs;
	private int selected;
	
	public BitmapFont(int rows, int cols) {
		this.rows = rows;
		this.cols = cols;
		this.glyphs = new ArrayList<>();
		for (int i = 0; i < 256; i++) {
			glyphs.add(new Glyph(rows, cols));
		}
		this.selected = 0;
	}
	
	public int getWidth() {
		return cols;
	}
	
	public int getHeight() {
		return rows;
	}
	
	public Glyph getGlyph(int n) {
		return glyphs.get(n);
	}
	
	public int getSelected() {
		return selected;
	}
	
	public void setSelected(int selected) {
		this.selected = selected;
		notifyObservers();
	}
	
	// Read font using the text-based format, see "font2.txt"
	public static BitmapFont read(Reader rdr) throws IOException {
		BufferedReader br = new BufferedReader(rdr);
		String header = br.readLine();
		@SuppressWarnings("resource")
		Scanner hs = new Scanner(header);
		int rows = hs.nextInt();
		int cols = hs.nextInt();
		BitmapFont font = new BitmapFont(rows, cols);
		for (int i = 0; i < 256; i++) {
			for (int r = 0; r < rows; r++) {
				String line = br.readLine();
				convertRowData(line, cols, font.getGlyph(i).getData().get(r));
			}
		}
		return font;
	}
	
	// Read font using raw binary format, i.e., what would be encoded
	// in a font ROM. Note that the font width must be 8.
	public static BitmapFont readBinary(File inputFile, int fontWidth, int fontHeight) throws IOException {
		if (fontWidth != 8)
			throw new IOException("font width must be 8 for reading binary font data");
		
		if (!inputFile.exists() || !inputFile.isFile())
			throw new IOException("Input file '" + inputFile.getPath() + "' does not exist or isn't a file");
		
		long size = inputFile.length();
		
		if (size % 256 != 0)
			throw new IOException("File size (" + size + ") isn't a multiple of 256");
		
		int height = (int) (size / 256);
		if (fontHeight != height)
			throw new IOException("font height inferred from file size (" +
					height + ") inconsistent with expected height + (" +
					fontHeight + ")");
		
		BitmapFont font = new BitmapFont(fontHeight, fontWidth);

		try (InputStream is = new FileInputStream(inputFile)) {
			for (int i = 0; i < 256; ++i) {
				Glyph g = font.getGlyph(i);
				for (int r = 0; r < fontHeight; ++r) {
					int val = is.read();
					if (val < 0) throw new IOException("Unexpected EOF");
					for (int j = 0; j < 8; ++j) {
						g.set(r, j, (val & (1 << j)) != 0);
					}
				}
			}
		}
		
		return font;
	}

	private static void convertRowData(String line, int cols, BitSet bitSet) {
		// Line data uses '.' for foreground, ' ' for background.
		// Order of dots is most significant to least significant.
		for (int i = 0; i < cols; i++) {
			int idx = cols - i - 1;
			boolean dotVal = line.charAt(i) == '.';
			bitSet.set(idx, dotVal);
		}
	}
	
	// Just for testing
	public static void main(String[] args) throws IOException {
		@SuppressWarnings("resource")
		Scanner keyboard = new Scanner(System.in);
		String fileName = keyboard.nextLine();
		try (Reader in = new FileReader(fileName)) {
			@SuppressWarnings("unused")
			BitmapFont font = BitmapFont.read(in);
			System.out.println("Read successfully?");
		}
	}
}
