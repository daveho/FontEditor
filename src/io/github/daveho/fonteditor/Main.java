package io.github.daveho.fonteditor;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Main {
	public static void main(String[] args) {
		FontEditorApp app = new FontEditorApp();
		BitmapFont font = new BitmapFont(16, 8);
		app.setModel(font);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame frame = new JFrame("Bitmap font editor");
				frame.add(app);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.pack();
				frame.setVisible(true);
			}
		});
	}
}
