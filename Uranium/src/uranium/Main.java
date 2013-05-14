package uranium;

import uranium.gui.Window;

public class Main {
	public static final int L = 0;
	public static final int U = 1;
	public static final int R = 2;
	public static final int D = 3;
	
	private static final double matrix[][] = {
		{0.00, 0.00, 0.00, 0.00, 0.00},
		{0.01, 0.03, 0.01, 0.00, 0.00},
		{0.03, 0.84, 0.03, 0.00, 0.00},
		{0.01, 0.03, 0.01, 0.00, 0.00},
		{0.00, 0.00, 0.00, 0.00, 0.00}
	}; 
	
	private static final double matrixOil[][] = {
		{0.00, 0.00, 0.00, 0.00, 0.00},
		{0.15, 0.10, 0.00, 0.00, 0.00},
		{0.20, 0.30, 0.00, 0.00, 0.00},
		{0.15, 0.10, 0.00, 0.00, 0.00},
		{0.00, 0.00, 0.00, 0.00, 0.00}
	}; 

	private static final double matrixOcc[][] = {
		{0.00, 0.00, 0.00, 0.00, 0.00},
		{0.00, 0.00, 0.10, 0.00, 0.00},
		{0.00, 0.00, 0.80, 0.00, 0.00},
		{0.00, 0.00, 0.10, 0.00, 0.00},
		{0.00, 0.00, 0.00, 0.00, 0.00}
	}; 
	
	private static final double matrixOilOcc[][] = {
		{0.00, 0.00, 0.00, 0.00, 0.00},
		{0.00, 0.00, 0.30, 0.00, 0.00},
		{0.00, 0.00, 0.40, 0.00, 0.00},
		{0.00, 0.00, 0.30, 0.00, 0.00},
		{0.00, 0.00, 0.00, 0.00, 0.00}
	};
	
	private static final double trans[][][][][] = Util.generateRotatedTransitionMatrices(matrix, matrixOil, matrixOcc, matrixOilOcc);

	private static final double bump[][] = {
		/* when the cell is not occupied: */
		{0.8, 0.2},
		/* when the cell is occupied: */
		{0.1, 0.9}
	};
	
	private double dist[][];
	private double distOld[][];
	
	private Map map = new Map();
	private Simulator simulator = new Simulator(map);
	
	private void normalize() {
		// the TRICK
		for(int i = 0; i < map.getNumRows(); i++)
			for(int j = 0; j < map.getNumColumns(); j++)
				if(map.isOcc(i, j))
					dist[i][j] = 0;
		
		double norm_factor = 0;
		for(int k = 0; k < map.getNumRows(); k++)
			for(int l = 0; l < map.getNumColumns(); l++)
				norm_factor += dist[k][l];

		for(int k = 0; k < map.getNumRows(); k++)
			for(int l = 0; l < map.getNumColumns(); l++)
				dist[k][l] /= norm_factor;
	}
	
	private void cloneDist() {
		// clone dist[i][j]
		for(int i = 0; i < map.getNumRows(); i++)
			for(int j = 0; j < map.getNumColumns(); j++)
				distOld[i][j] = dist[i][j];
	}
	
	/* update whole distribution of p(x) for "blind" estimation */
	private void updateDistrib(int dir) {
		cloneDist();
		
		// filter every cell
		for(int k = 0; k < map.getNumRows(); k++)
			for(int l = 0; l < map.getNumColumns(); l++)
				dist[k][l] = filter(k, l, dir);
		
		normalize();
	}
	
	/* update whole distribution of p(x|z) */
	private void updateDistrib2(int dir, int[] bumpers) {
		cloneDist();
		
		// filter every cell
		for(int k = 0; k < map.getNumRows(); k++)
			for(int l = 0; l < map.getNumColumns(); l++)
				dist[k][l] = filter2(k, l, dir, bumpers);
		
		normalize();
	}
	
	/* get sum p(x[t]|x[t-1]) */
	/* used in "blind" state estimation */
	private double filter(int k, int l, int dir) {
		double summ = 0;
		for(int i = 0; i < map.getNumRows(); i++)
			for(int j = 0; j < map.getNumColumns(); j++)
				summ += p(i, j, k, l, dir) * distOld[i][j];
		return summ;
	}
	
	/* get sum p(x[t]|x[t-1])*p(x[t-1]|z[1:t-1]) */
	/* used in state estimation with sensors */
	private double filter2(int k, int l, int dir, int[] bumpers) {
		double summ = 0;
		for(int i = 0; i < map.getNumRows(); i++)
			for(int j = 0; j < map.getNumColumns(); j++)
				summ += p(i, j, k, l, dir) * distOld[i][j];
		return summ * p( bumpers, k, l);
	}
	
	/*
	 * probabilistic transition function:
	 * 
	 * p(Xkl | Xij , dir) == p(X[t] | X[t-1], u[t])
	 */	
	private double p(int i, int j, int k, int l, int dir) {
		//if(!checkBounds(i,j) || !checkBounds(k,l)) return 0;
		int indi = k - i + 2;
		int indj = l - j + 2;
		
		if(indi < 0 || indj < 0 || indi >= 5 || indj >= 5)
			return 0;
		
		return trans[dir][map.isOcc(i,j,dir) ? 1 : 0][map.isOil(i,j) ? 1 : 0][indi][indj];
	}
	
	/*
	 * observation model:
	 * 
	 * p(Z[dir] | Xij, dir)
	 */
	private double p(int[] bumper, int i, int j) {
		double result = 1;
		for(int n = 0; n < 4; n++){
			result *= bump[map.isOcc(i, j, n) ? 1 : 0][bumper[n]];
		}
		return result;
	}
	
	@SuppressWarnings("serial")
	public Main() {
		dist = new double[map.getNumRows()][map.getNumColumns()];
		distOld = new double[map.getNumRows()][map.getNumColumns()];
		for(int i = 0; i < map.getNumRows(); i++)
			for(int j = 0; j < map.getNumColumns(); j++)
				dist[i][j] = Math.random();
		normalize();
		simulator.setPosition(1, 1);
		
		new Window(dist, map, simulator) {			
			@Override public void move(int direction) {
				//updateDistrib(direction);
				updateDistrib2(direction,simulator.getBumperState());
				simulator.move(direction);
			}
			
			@Override public void cellClicked(int i, int j) {
				simulator.setPosition(i, j);
			}
		};
	}
	
	public static void main(String[] args) {
		new Main();
	}
}
