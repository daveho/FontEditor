package io.github.daveho.fonteditor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class GlyphView extends JPanel implements MyObserver {
	private static final long serialVersionUID = 1L;

	public static final int SCALE = 24;
	
	private BitmapFont model;
	
	public GlyphView() {
		setPreferredSize(new Dimension(BitmapFontView.FONT_W*SCALE, BitmapFontView.FONT_H*SCALE));
		setBackground(Color.LIGHT_GRAY);
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				handleMouseClicked(e);
			}
		});
	}
	
	@Override
	public void update(MyObservable o, Object arg) {
		repaint();
	}
	
	public void setModel(BitmapFont model) {
		this.model = model;
		this.model.addObserver(this);
	}

	protected void handleMouseClicked(MouseEvent e) {
		if (model == null) {
			return;
		}
		Glyph glyph = model.getGlyph(model.getSelected());
		int c = e.getX() / SCALE;
		int r = e.getY() / SCALE;
		if (c >= 0 && c < BitmapFontView.FONT_W && r >= 0 && r < BitmapFontView.FONT_H) {
			int bitidx = BitmapFontView.FONT_W - c - 1;
			glyph.getData().get(r).flip(bitidx);
			//repaint();
			model.notifyObservers();
		}
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (model == null) {
			return;
		}
		Glyph glyph = model.getGlyph(model.getSelected());
		g.setColor(Color.BLACK);
		for (int r = 0; r < BitmapFontView.FONT_H; r++) {
			for (int c = 0; c < BitmapFontView.FONT_W; c++) {
				int bitidx = BitmapFontView.FONT_W - c - 1;
				if (glyph.getData().get(r).get(bitidx)) {
					int x = c * SCALE;
					int y = r * SCALE;
					g.fillRect(x, y, SCALE, SCALE);
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
				GlyphView view = new GlyphView();
				view.setModel(font);
				JFrame frame = new JFrame("Glyph view test");
				frame.setContentPane(view);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.pack();
				frame.setVisible(true);
			}
		});

	}
}
