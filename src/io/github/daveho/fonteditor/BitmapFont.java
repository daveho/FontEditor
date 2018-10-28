package io.github.daveho.fonteditor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Scanner;

public class BitmapFont {
	private int rows, cols;
	private List<Glyph> glyphs;
	
	public BitmapFont(int rows, int cols) {
		this.rows = rows;
		this.cols = cols;
		this.glyphs = new ArrayList<>();
		for (int i = 0; i < 256; i++) {
			glyphs.add(new Glyph(rows, cols));
		}
	}
	
	public Glyph getGlyph(int n) {
		return glyphs.get(n);
	}
	
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
