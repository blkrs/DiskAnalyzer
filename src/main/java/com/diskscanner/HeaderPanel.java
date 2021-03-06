package com.diskscanner;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.LayoutManager;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class HeaderPanel extends JPanel {
	private Image img = null;

	public HeaderPanel(LayoutManager l) {
		this(ImageUtils.createImageIcon("gradient.png", "Gradient").getImage(), l);
	}

	public HeaderPanel(String img, LayoutManager l) {
		this(new ImageIcon(img).getImage(), l);
	}

	public HeaderPanel(Image img, LayoutManager l) {
		super(l);
		this.img = img;

		System.out.println("loading image :" + img);
	}

	public void rescale() {
		img = img.getScaledInstance(this.getWidth(), this.getHeight(), Image.SCALE_SMOOTH);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		//

		g.drawImage(img, 0, 0, null);
		g.drawLine(0, 0, 200, 200);
	}
}
