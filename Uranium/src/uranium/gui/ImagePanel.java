package uranium.gui;

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

import uranium.Main;
import uranium.Map;
import uranium.Simulator;

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
	
	public void updateImage(double dist[][], Map map, Simulator simulator) {
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
		for(int i = 0; i < map.getNumRows(); i++) {
			for(int j = 0; j < map.getNumColumns(); j++) {
				if(map.isOcc(i, j))
					g2d.setColor(Color.gray);
				else if(map.isOil(i, j))
					g2d.setColor(Color.yellow);
				else
					continue;
				
				g2d.fill(new Rectangle2D.Double(j * cellSize, i * cellSize, cellSize, cellSize));
			}
		}
		
		// Draw real robot position:
		int pos[] = simulator.getPosition();
		g2d.setColor(Color.white);
		g2d.draw(new Rectangle2D.Double(pos[1] * cellSize, pos[0] * cellSize, cellSize, cellSize));
		
		// Draw bumpers state:
		g2d.setColor(Color.red);
		int bumpers[] = simulator.getBumperState();
		if(bumpers[Main.L] != 0)
			g2d.drawLine(pos[1] * cellSize, pos[0] * cellSize, pos[1] * cellSize, (pos[0] + 1) * cellSize);
		if(bumpers[Main.U] != 0)
			g2d.drawLine(pos[1] * cellSize, pos[0] * cellSize, (pos[1] + 1) * cellSize, pos[0] * cellSize);
		if(bumpers[Main.R] != 0)
			g2d.drawLine((pos[1] + 1) * cellSize, pos[0] * cellSize, (pos[1] + 1) * cellSize, (pos[0] + 1) * cellSize);
		if(bumpers[Main.D] != 0)
			g2d.drawLine(pos[1] * cellSize, (pos[0] + 1) * cellSize, (pos[1] + 1) * cellSize, (pos[0] + 1) * cellSize);
		
		g2d.dispose();
		
		setPreferredSize(new Dimension(cellSize * map.getNumColumns(), cellSize * map.getNumRows()));
	}
}

