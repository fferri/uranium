package uranium;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

class ImagePanel extends JPanel {
	private static final long serialVersionUID = 1173319384063742620L;
	private static final int cellSize = 5;
	private BufferedImage bufferedImage;
	
	public ImagePanel() {
		setFocusable(true);
		requestFocus();
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		Image i = Toolkit.getDefaultToolkit().createImage(bufferedImage.getSource());
		
		g.drawImage(i, 0, 0, null);
	}
	
	private Color makeColor(double v) {
		float fv = (float)v;
		if(fv < 0) fv = 0;
		else if(fv > 1) fv = 1;
		
		float b = 8 * (float)v;
		if(b > 1) b = 1;
		
		return Color.getHSBColor(0.7f - fv * 0.2f, 1.0f, b);
	}
	
	public void updateImage(double dist[][], int mapp[][]) {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gs = ge.getDefaultScreenDevice();
		GraphicsConfiguration gc = gs.getDefaultConfiguration();
		
		bufferedImage = gc.createCompatibleImage(cellSize * dist[0].length, cellSize * dist.length, Transparency.OPAQUE);
		
		Graphics2D g2d = bufferedImage.createGraphics();

		// find max value of dist:
		double max = 0;
		for(int i = 0; i < dist.length; i++) {
			for(int j = 0; j < dist[0].length; j++) {
				if(dist[i][j] > max) max = dist[i][j];
			}
		}
		
		// Draw probability distribution of state				
		for(int i = 0; i < dist.length; i++) {
			for(int j = 0; j < dist[0].length; j++) {
				g2d.setColor(makeColor(dist[i][j]/max));
				g2d.fill(new Rectangle2D.Double(j * cellSize, i * cellSize, cellSize, cellSize));
			}
		}
		
		// Draw map overlay
		for(int i = 0; i < mapp.length; i++) {
			for(int j = 0; j < mapp[0].length; j++) {
				if(mapp[i][j] == 0) continue;
				if(mapp[i][j] == 1) g2d.setColor(Color.gray);
				if(mapp[i][j] == 2) g2d.setColor(Color.yellow);
				g2d.fill(new Rectangle2D.Double(j * cellSize, i * cellSize, cellSize, cellSize));
			}
		}
		
		g2d.dispose();
		
		setPreferredSize(new Dimension(cellSize * mapp[0].length, cellSize * mapp.length));
	}
}
