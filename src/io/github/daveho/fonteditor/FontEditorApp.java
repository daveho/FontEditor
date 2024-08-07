package io.github.daveho.fonteditor;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class FontEditorApp extends JPanel implements MyObserver {
	private static final long serialVersionUID = 1L;
	
	private static final int BORDER = 8;
	private static final int GAP = 8;

	private BitmapFont model;
	private BitmapFontView fontView;
	private GlyphView glyphView;
	private JPanel buttonPanel;
	
	public FontEditorApp() {
		setLayout(null);
		
		fontView = new BitmapFontView();
		add(fontView);
		glyphView = new GlyphView();
		add(glyphView);

//		// Create default empty 8x16 bitmap font
//		model = new BitmapFont(16, 8);
		
		this.buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		add(buttonPanel);
		
		addButton(buttonPanel, "Load", () -> onLoad());
		addButton(buttonPanel, "Save", () -> onSave());
	}
	
	private void addButton(JPanel buttonPanel, String name, Runnable fn) {
		JButton button = new JButton(name);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				fn.run();
			}
		});
		buttonPanel.add(button);
	}

	public void setModel(BitmapFont model) {
		this.model = model;
		
		fontView.setModel(model);
		Dimension fontViewDim = fontView.getPreferredSize();
		
		glyphView.setModel(model);
		Dimension glyphViewDim = glyphView.getPreferredSize();

		int topPartHeight = (int) (Math.max(fontViewDim.getHeight(), glyphViewDim.getHeight()));
		int topPartWidth = (int) (BORDER*2 + GAP + fontViewDim.getWidth() + glyphViewDim.getWidth());
		
		fontView.setBounds(BORDER, BORDER, (int) fontViewDim.getWidth(), (int) fontViewDim.getHeight());
		glyphView.setBounds(
				BORDER + (int) fontViewDim.getWidth() + GAP,
				BORDER,
				(int) glyphViewDim.getWidth(),
				(int) glyphViewDim.getHeight());
		
		Dimension buttonPanelDim = buttonPanel.getPreferredSize();
		buttonPanel.setBounds(BORDER, BORDER + topPartHeight, topPartWidth, (int) buttonPanelDim.getHeight());

		setPreferredSize(new Dimension(topPartWidth, topPartHeight + (int) (buttonPanelDim.getHeight() + BORDER)));
	}

	@Override
	public void update(MyObservable o, Object arg) {
		// font changed: do nothing for now, but we could keep track of whether
		// the font has changed since the last save
	}
	
	private void onLoad() {
		//System.out.println("TODO: dialog to load font");
		JFileChooser fc = new JFileChooser();
		if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			File inputFile = fc.getSelectedFile();
			
			try {
				// Attempt to infer the font height from the file size
				long size = inputFile.length();
				if (size == 0)
					throw new IOException("Empty file?");
				if ((size % 256) != 0)
					throw new IOException("Chosen file doesn't seem to be binary font data");
				int fontHeight = (int) (size / 256);
				
				BitmapFont font = BitmapFont.readBinary(inputFile, 8, fontHeight);
				this.setModel(font);
				font.notifyObservers();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(this, "Error opening font file: " + e.getMessage());
			}
		}
		
	}
	
	private void onSave() {
		JFileChooser fc = new JFileChooser();
		if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			File outputFile = fc.getSelectedFile();
			try {
				BitmapFont.writeBinary(outputFile, model);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(this, "Error saving font file: " + e.getMessage());
			}
		}
	}
}
