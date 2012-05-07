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

	private double dist[][];
	private double distOld[][];
	
	private Map map = new Map();
	private Simulator simulator = new Simulator(map);
	
	private void normalize() {
		double norm_factor = 0;
		for(int k = 0; k < map.getNumRows(); k++)
			for(int l = 0; l < map.getNumColumns(); l++)
				norm_factor += dist[k][l];

		for(int k = 0; k < map.getNumRows(); k++)
			for(int l = 0; l < map.getNumColumns(); l++)
				dist[k][l] /= norm_factor;
	}
	
	/* update whole distribution pf p(x) for "blind" estimation */
	private void updateDistrib(int dir) {
		// clone dist[i][j]
		for(int i = 0; i < map.getNumRows(); i++)
			for(int j = 0; j < map.getNumColumns(); j++)
				distOld[i][j] = dist[i][j];
		
		// filter every cell
		for(int k = 0; k < map.getNumRows(); k++)
			for(int l = 0; l < map.getNumColumns(); l++)
				dist[k][l] = filter(k, l, dir);
		
		// the TRICK
		for(int i = 0; i < map.getNumRows(); i++)
			for(int j = 0; j < map.getNumColumns(); j++)
				if(map.isOcc(i, j))
					dist[i][j] = 0;
		
		normalize();
	}
	
	/* update whole distribution of p(x|z) */
	private void updateDistrib2(int dir, int bumpers) {
		// TODO:
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
		return summ * p( bumpers, k, l, dir);
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
	private double p(int[] bumper, int i, int j, int dir) {
		
		
		return 0.0;
	}
	
	@SuppressWarnings("serial")
	public Main() {
		dist = new double[map.getNumRows()][map.getNumColumns()];
		distOld = new double[map.getNumRows()][map.getNumColumns()];
		dist[1][1] = 1.0;
		simulator.setPosition(1, 1);
		
		new Window(dist, map, simulator) {			
			@Override public void move(int direction) {
				updateDistrib(direction);
				simulator.move(direction);
			}
		};
	}
	
	public static void main(String[] args) {
		new Main();
	}
}
