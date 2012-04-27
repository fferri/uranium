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

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;


public class Main {
	
	private static final int L = 0;
	private static final int U = 1;
	private static final int R = 2;
	private static final int D = 3;
	
	private static final int dx[] = {-1,0,1,0};
	private static final int dy[] = {0,-1,0,1};
	
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
			{0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0},
			{0,2,1,1,1,0,0,0,2,0},
			{0,0,0,0,1,0,0,0,0,0},
			{0,0,2,0,0,0,0,2,0,2},
			{0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0},
	};	
	
	static double[][] createRandomMap(int w, int h) {
		double map[][] = new double[h][w];
		for(int i = 0; i < h; i++) {
			for(int j = 0; j < w; j++) {
				map[i][j] = Math.random();
			}
		}
		return map;
	}
	
	private boolean checkBound(int i, int j){
		if(i < 0 || j < 0 || i > mapp.length || j > mapp[0].length)
			return false;
		return true;
	}
	
	private boolean isOcc(int i,int j,int dir){
		if (checkBound(i+dx[dir],j+dy[dir]))
			return mapp[i+dx[dir]][j+dy[dir]]==1;
		else return true;
	}
	
	private boolean isOil(int i,int j){
		return mapp[i][j]==2;
	}
	
	/*
	 * p(Xkl | Xij , dir)
	 */	
	private void update(int dir){
		for(int i = 0; i < mapp.length; i++)
			for(int j = 0; j < mapp.length; j++)
				distOld[i][j]= dist[i][j];
		
		for(int k = 0; k < mapp.length; k++)
			for(int l = 0; l < mapp.length; l++){
				dist[k][l] = filter(k,l,dir) ;
			}
	}
	
	private double filter(int k, int l,int dir) {
		double summ = 0;
		for(int i = 0; i < mapp.length; i++)
			for(int j = 0; j< mapp[0].length; i++){
				summ += p(i,j,k,l,dir)*distOld[i][j];
			}
		return summ;
	}
	private double p(int i,int j, int k,int l,int dir){
		if (!checkBound(i,j) || !checkBound(k,l)) return 0;
		int indi = k-i+2;
		int indj = l-j+2;
		
		if(indi < 0 || indj < 0 || indi >= 5 || indj >= 5)
			return 0;
		
		return trans[dir][isOcc(i,j,dir)?1:0][isOil(i,j)?1:0][indi][indj];
	}
	
	public Main() {
		trans = new double[4][2][2][][];
		
		trans[L][0][0]=matrix;
		trans[L][0][1]=matrixOil;
		trans[L][1][1]=matrixOilOcc;
		trans[L][1][0]=matrixOcc;
		for(int i=1; i < 4; i++){
			trans[i][0][0]=rotateMatrixRight(trans[i-1][0][0]);
			trans[i][0][1]=rotateMatrixRight(trans[i-1][0][1]);
			trans[i][1][1]=rotateMatrixRight(trans[i-1][1][1]);
			trans[i][1][0]=rotateMatrixRight(trans[i-1][1][0]);
		}
		new Win(createRandomMap(80, 60));
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
		new Main();
	}

}
