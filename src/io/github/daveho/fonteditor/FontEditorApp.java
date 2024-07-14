package io.github.daveho.fonteditor;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class FontEditorApp extends JPanel implements MyObserver {
	private static final long serialVersionUID = 1L;
	
	private static final int BORDER = 8;
	private static final int GAP = 8;

	private BitmapFont model;
	private BitmapFontView fontView;
	private GlyphView glyphView;
	
	public FontEditorApp() {
		setLayout(null);
		
		fontView = new BitmapFontView();
		add(fontView);
		glyphView = new GlyphView();
		add(glyphView);

//		// Create default empty 8x16 bitmap font
//		model = new BitmapFont(16, 8);
	}
	
	public void setModel(BitmapFont model) {
		this.model = model;
		
		fontView.setModel(model);
		Dimension fontViewDim = fontView.getPreferredSize();
		fontView.setBounds(BORDER, BORDER, (int) fontViewDim.getWidth(), (int) fontViewDim.getHeight());
		
		glyphView.setModel(model);
		Dimension glyphViewDim = glyphView.getPreferredSize();
		glyphView.setBounds(
				BORDER + (int) fontViewDim.getWidth() + GAP,
				BORDER,
				(int) glyphViewDim.getWidth(),
				(int) glyphViewDim.getHeight());
		
		setPreferredSize(
				new Dimension(
						(int) (BORDER*2 + GAP + fontViewDim.getWidth() + glyphViewDim.getWidth()),
						(int) (BORDER*2 + Math.max(fontViewDim.getHeight(), glyphViewDim.getHeight()))));
	}

	@Override
	public void update(MyObservable o, Object arg) {
		// font changed: do nothing for now, but we could keep track of whether
		// the font has changed since the last save
	}
}
