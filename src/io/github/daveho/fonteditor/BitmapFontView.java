package io.github.daveho.fonteditor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class BitmapFontView extends JPanel implements MyObserver {
	private static final long serialVersionUID = 1L;

//	public static final int model.getWidth() = 8;
//	public static final int model.getHeight() = 16;
	public static final int SCALE = 2;
	
	private BitmapFont model;
	
	public BitmapFontView() {
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
	
	protected void handleMouseClicked(MouseEvent e) {
		int c = e.getX() / (model.getWidth()*SCALE);
		int r = e.getY() / (model.getHeight()*SCALE);
		if (c >= 0 && c < 16 && r >= 0 && r < 16) {
			model.setSelected(r*16 + c);
			//repaint();
		}
	}

	public void setModel(BitmapFont model) {
		this.model = model;
		this.model.addObserver(this);
		setPreferredSize(new Dimension(16 * model.getWidth() * SCALE, 16 * model.getHeight() * SCALE));
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
		int x = selCol * model.getWidth() * SCALE;
		int y = selRow * model.getHeight() * SCALE;
		g.fillRect(x, y, model.getWidth() * SCALE, model.getHeight() * SCALE);
	}

	private void paintGlyph(Glyph glyph, int r, int c, Graphics g) {
		g.setColor(Color.BLACK);
		for (int i = 0; i < model.getHeight(); i++) {
			for (int j = 0; j < model.getWidth(); j++) {
				int bitidx = model.getWidth() - j - 1;
				if (glyph.getData().get(i).get(bitidx)) {
					int x = c * model.getWidth() * SCALE;
					int y = r * model.getHeight() * SCALE;
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
//		try (Reader in = new FileReader(fileName)) {
//			font = BitmapFont.read(in);
//			System.out.println("Read successfully?");
//		}
		
		// Try to read binary 8x16 VGA font data
		File inputFile = new File(fileName);
		font = BitmapFont.readBinary(inputFile, 8, 16);
		
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
