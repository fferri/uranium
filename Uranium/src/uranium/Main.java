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
import javax.swing.JPanel;

public class Main {
	// constants
	private static final int L = 0;
	private static final int U = 1;
	private static final int R = 2;
	private static final int D = 3;
	
	private static final int dx[] = {-1,0,1,0};
	private static final int dy[] = {0,-1,0,1};
	
	private static final String dirString[] = {"Left", "Up", "Right", "Down"};
	
	private double trans[][][][][];

	private static final double matrix[][] = 
	{
		{0   ,0   ,0   ,0   ,0   },
		{0   ,0.10,0   ,0   ,0   },
		{0   ,0.80,0   ,0   ,0   },
		{0   ,0.10,0   ,0   ,0   },
		{0   ,0   ,0   ,0   ,0   }
	}; 
	
	private static final double matrixOil[][] = 
	{
		{0   ,0   ,0   ,0   ,0   },
		{0.15,0.10,0   ,0   ,0   },
		{0.20,0.30,0   ,0   ,0   },
		{0.15,0.10,0   ,0   ,0   },
		{0   ,0   ,0   ,0   ,0   }
	}; 
	
	private static final double matrixOilOcc[][] = {
		{0   ,0   ,0   ,0   ,0   },
		{0   ,0   ,0.3 ,0   ,0   },
		{0   ,0   ,0.4 ,0   ,0   },
		{0   ,0   ,0.3 ,0   ,0   },
		{0   ,0   ,0   ,0   ,0   }
	}; 

	private static final double matrixOcc[][] = {
		{0   ,0   ,0   ,0   ,0   },
		{0   ,0   ,0.1 ,0   ,0   },
		{0   ,0   ,0.8 ,0   ,0   },
		{0   ,0   ,0.1 ,0   ,0   },
		{0   ,0   ,0   ,0   ,0   }
	}; 

	private double dist[][];
	private double distOld[][];
	
	/* 0 free
	 * 1 occ
	 * 2 oil
	 */
	private int mapp[][] = {
			{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
			{1,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
			{1,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
			{1,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
			{1,0,0,1,1,1,1,1,1,1,1,2,0,0,0,0,0,0,0,0,0,0,0,2,0,0,0,0,0,0,0,1},
			{1,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
			{1,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
			{1,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
			{1,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
			{1,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
			{1,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,1,1,1,1},
			{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,2,2,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
			{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
			{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
			{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
			{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
			{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
			{1,0,0,0,0,2,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
			{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
			{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
			{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
			{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
			{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
			{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
	};
	
	private boolean checkBounds(int i, int j){
		if(i < 0 || j < 0 || i >= mapp.length || j >= mapp[0].length)
			return false;
		return true;
	}
	
	private boolean isOcc(int i, int j, int dir){
		if(checkBounds(i + dx[dir], j + dy[dir]))
			return mapp[i + dx[dir]][j + dy[dir]] == 1;
		else
			return true;
	}
	
	private boolean isOil(int i, int j){
		return mapp[i][j] == 2;
	}
	
	private void updateDistrib(int dir){
		for(int i = 0; i < mapp.length; i++)
			for(int j = 0; j < mapp.length; j++)
				distOld[i][j] = dist[i][j];
		
		for(int k = 0; k < mapp.length; k++)
			for(int l = 0; l < mapp.length; l++)
				dist[k][l] = filter(k, l, dir);
	}
	
	private double filter(int k, int l, int dir) {
		double summ = 0;
		for(int i = 0; i < mapp.length; i++)
			for(int j = 0; j < mapp[0].length; j++)
				summ += p(i, j, k, l, dir) * distOld[i][j];
		return summ;
	}
	
	/*
	 * p(Xkl | Xij , dir)
	 */	
	private double p(int i, int j, int k, int l, int dir) {
		if(!checkBounds(i,j) || !checkBounds(k,l)) return 0;
		int indi = k - i + 2;
		int indj = l - j + 2;
		
		if(indi < 0 || indj < 0 || indi >= 5 || indj >= 5)
			return 0;
		
		return trans[dir][isOcc(i,j,dir)?1:0][isOil(i,j)?1:0][indi][indj];
	}
	
	static void printArray2D(int x[][]) {
		for(int i = 0; i < x.length; i++)
			System.out.println(Arrays.toString(x[i]));
	}
	
	static void printArray2D(double x[][]) {
		for(int i = 0; i < x.length; i++)
			System.out.println(Arrays.toString(x[i]));
	}
	
	public Main() {
		trans = new double[4][2][2][][];
		
		trans[L][0][0]=matrix;
		trans[L][0][1]=matrixOil;
		trans[L][1][1]=matrixOilOcc;
		trans[L][1][0]=matrixOcc;
		for(int dir = 1; dir < 4; dir++) {
			for(int occ = 0; occ < 2; occ++) {
				for(int oil = 0; oil < 2; oil++) {
					trans[dir][occ][oil]=rotateMatrixRight(trans[dir - 1][occ][oil]);
				}
			}
		}
		
		dist = new double[mapp.length][mapp[0].length];
		distOld = new double[mapp.length][mapp[0].length];
		dist[1][1] = 0.2;
		dist[1][2] = 0.6;
		dist[1][3] = 0.2;
		
		System.out.println("\n MAP:");
		printArray2D(mapp);
		
		for(int dir = 0; dir < 3; dir++) {
			for(int occ = 0; occ < 2; occ++) {
				for(int oil = 0; oil < 2; oil++) {
					System.out.println("\n TRANS[dir=" + dirString[dir] + "][occ=" + occ + "][oil=" + oil + "]:");
					printArray2D(trans[dir][occ][oil]);
				}
			}
		}
		
		new Win();
	}
	
	public double[][] rotateMatrixRight(double[][] matrix)
	{
	    /* W and H are already swapped */
	    int w = matrix.length;
	    int h = matrix[0].length;
	    double[][] ret = new double[h][w];
	    for (int i = 0; i < h; ++i) {
	        for (int j = 0; j < w; ++j) {
	            ret[i][j] = matrix[w - j - 1][i];
	        }
	    }
	    return ret;
	}

	class Win extends JFrame implements KeyListener {
		private static final long serialVersionUID = -5125368081992354692L;
		private Pnl panel;
		
		class Pnl extends JPanel {
			private static final long serialVersionUID = 1173319384063742620L;
			private static final int cellSize = 5;
			private BufferedImage bufferedImage;
			
			public Pnl() {
				updateImage(); // create bufferedImage for the first time
				
				setPreferredSize(new Dimension(cellSize * mapp[0].length, cellSize * mapp.length));
				
				setFocusable(true);
				requestFocus();
				
				addKeyListener(Win.this);
			}
			
			@Override
			public void paint(Graphics g) {
				super.paint(g);
				
				Image i = Toolkit.getDefaultToolkit().createImage(bufferedImage.getSource());
				
				g.drawImage(i, 0, 0, null);
			}
			
			public void updateImage() {
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
						g2d.setColor(Color.getHSBColor(0, 0, (float) (dist[i][j]/max)));
						g2d.fill(new Rectangle2D.Double(j*cellSize, i*cellSize, cellSize, cellSize));
					}
				}
				
				// Draw map overlay
				for(int i = 0; i < mapp.length; i++) {
					for(int j = 0; j < mapp[0].length; j++) {
						if(mapp[i][j] == 0) continue;
						if(mapp[i][j] == 1) g2d.setColor(Color.red);
						if(mapp[i][j] == 2) g2d.setColor(Color.green);
						g2d.fill(new Rectangle2D.Double(j*cellSize, i*cellSize, cellSize, cellSize));
					}
				}
				
				g2d.dispose();
			}
		}
		
		public Win() {
			super("ciccio");
			add(panel = new Pnl());
			pack();
			setVisible(true);
			setDefaultCloseOperation(EXIT_ON_CLOSE);
		}
		
		private void move(int dir) {
			updateDistrib(dir);
			panel.updateImage();
			panel.repaint();
		}

		@Override
		public void keyPressed(KeyEvent e) {
		    int keyCode = e.getKeyCode();
		    switch(keyCode) { 
		        case KeyEvent.VK_UP:
		        	move(U);
		            break;
		        case KeyEvent.VK_DOWN:
		        	move(D);
		            break;
		        case KeyEvent.VK_LEFT:
		        	move(L);
		            break;
		        case KeyEvent.VK_RIGHT :
		        	move(R);
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
		new Main();
	}

}
