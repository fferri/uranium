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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;


public class Main {

		
	static double[][] createRandomMap(int w, int h) {
		double map[][] = new double[h][w];
		for(int i = 0; i < h; i++) {
			for(int j = 0; j < w; j++) {
				map[i][j] = Math.random();
			}
		}
		return map;
	}
	


	static class Win extends JFrame implements KeyListener {
		private static final long serialVersionUID = -5125368081992354692L;
		
		class Pnl extends JPanel {
			private static final long serialVersionUID = 1173319384063742620L;
			private static final int cellSize = 5;
			
			double map[][];
			
			public Pnl(double map[][]) {
				this.map = map;
				
				setPreferredSize(new Dimension(cellSize * map[0].length, cellSize * map.length));
				
				setFocusable(true);
				requestFocus();
				
				addKeyListener(Win.this);
			}
			
			@Override
			public void paint(Graphics g) {
				super.paint(g);
				
				BufferedImage bi = createImageFromMap(map);
				Image i = Toolkit.getDefaultToolkit().createImage(bi.getSource());
				
				g.drawImage(i, 0, 0, null);
			}
			
			private BufferedImage createImageFromMap(double map[][]) {
				GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
				GraphicsDevice gs = ge.getDefaultScreenDevice();
				GraphicsConfiguration gc = gs.getDefaultConfiguration();
				
				//BufferedImage bimage = new BufferedImage(map.length, map[0].length, BufferedImage.TYPE_INT_RGB);
				BufferedImage bimage = gc.createCompatibleImage(cellSize * map[0].length, cellSize * map.length, Transparency.OPAQUE);
				
				Graphics2D g2d = bimage.createGraphics();

				// Draw on the image
				
				for(int i = 0; i < map.length; i++) {
					for(int j = 0; j < map[0].length; j++) {
						g2d.setColor(Color.getHSBColor(0, 0, (float) map[i][j]));
						g2d.fill(new Rectangle2D.Double(j*cellSize, i*cellSize, (j+1)*cellSize, (i+1)*cellSize));
					}
				}
				
				g2d.dispose();

				return bimage;
			}
		}
		
		double map[][];
		
		public Win(double map[][]) {
			super("ciccio");
			add(new Pnl(map));
			pack();
			setVisible(true);
			setDefaultCloseOperation(EXIT_ON_CLOSE);
		}

		@Override
		public void keyPressed(KeyEvent e) {
		    int keyCode = e.getKeyCode();
		    switch(keyCode) { 
		        case KeyEvent.VK_UP:
				    JOptionPane.showMessageDialog(this, "UP");
		            break;
		        case KeyEvent.VK_DOWN:
				    JOptionPane.showMessageDialog(this, "DOWN");
		            break;
		        case KeyEvent.VK_LEFT:
				    JOptionPane.showMessageDialog(this, "LEFT");
		            break;
		        case KeyEvent.VK_RIGHT :
				    JOptionPane.showMessageDialog(this, "RIGHT");
		            break;
		     }
		}

		@Override
		public void keyReleased(KeyEvent e) {}

		@Override
		public void keyTyped(KeyEvent e) {}
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new Win(createRandomMap(80, 60));
	}

}
