package io.github.daveho.fonteditor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class BitmapFontView extends JPanel {
	private static final long serialVersionUID = 1L;

	public static final int FONT_W = 8;
	public static final int FONT_H = 8;
	public static final int SCALE = 4;
	
	private BitmapFont model;
	
	public BitmapFontView() {
		setPreferredSize(new Dimension(16 * FONT_W * SCALE, 16 * FONT_H * SCALE));
		setBackground(Color.LIGHT_GRAY);
		
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				handleMouseClicked(e);
			}
		});
	}
	
	protected void handleMouseClicked(MouseEvent e) {
		int c = e.getX() / (FONT_W*SCALE);
		int r = e.getY() / (FONT_H*SCALE);
		if (c >= 0 && c < 16 && r >= 0 && r < 16) {
			model.setSelected(r*16 + c);
			repaint();
		}
	}

	public void setModel(BitmapFont model) {
		this.model = model;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		if (model == null) {
			return;
		}
		
		for (int r = 0; r < 16; r++) {
			for (int c = 0; c < 16; c++) {
				int n = r*16 + c;
				Glyph glyph = model.getGlyph(n);
				paintGlyph(glyph, r, c, g);
			}
		}
		
		int sel = model.getSelected();
		int selRow = sel/16;
		int selCol = sel%16;
		Color highlight = new Color(128, 255, 128, 80);
		g.setColor(highlight);
		int x = selCol * FONT_W * SCALE;
		int y = selRow * FONT_H * SCALE;
		g.fillRect(x, y, FONT_W * SCALE, FONT_H * SCALE);
	}

	private void paintGlyph(Glyph glyph, int r, int c, Graphics g) {
		g.setColor(Color.BLACK);
		for (int i = 0; i < FONT_H; i++) {
			for (int j = 0; j < FONT_W; j++) {
				int bitidx = FONT_W - j - 1;
				if (glyph.getData().get(i).get(bitidx)) {
					int x = c * FONT_W * SCALE;
					int y = r * FONT_H * SCALE;
					g.fillRect(x + j*SCALE, y + i*SCALE, SCALE, SCALE);
				}
			}
		}
	}
	
	public static void main(String[] args) throws IOException {
		@SuppressWarnings("resource")
		Scanner keyboard = new Scanner(System.in);
		System.out.print("Enter font filename: ");
		String fileName = keyboard.nextLine();
		BitmapFont font;
		try (Reader in = new FileReader(fileName)) {
			font = BitmapFont.read(in);
			System.out.println("Read successfully?");
		}
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				BitmapFontView view = new BitmapFontView();
				view.setModel(font);
				JFrame frame = new JFrame("Font view test");
				frame.setContentPane(view);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.pack();
				frame.setVisible(true);
			}
		});
	}
}
