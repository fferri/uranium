package uranium;

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
	
	private static final double trans[][][][][] = generateRotatedTransitionMatrices(matrix, matrixOil, matrixOcc, matrixOilOcc);

	private double dist[][];
	private double distOld[][];
	
	private Map map = new Map();
	
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
	private double filter2(int k, int l, int dir, int bumpers) {
		// TODO:
		return 0.0;
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
	private double p(int bumper, int i, int j, int dir) {
		// TODO:
		return 0.0;
	}
	
	public static double[][][][][] generateRotatedTransitionMatrices(double m[][], double mOil[][], double mOcc[][], double mOilOcc[][]) {
		double[][][][][] ret = new double[4][2][2][][];
		
		ret[L][0][0]=m;
		ret[L][0][1]=mOil;
		ret[L][1][0]=mOcc;
		ret[L][1][1]=mOilOcc;
		for(int dir = 1; dir < 4; dir++)
			for(int occ = 0; occ < 2; occ++)
				for(int oil = 0; oil < 2; oil++)
					ret[dir][occ][oil] = Util.rotateMatrixRight(ret[dir - 1][occ][oil]);
		
		return ret;
	}
	
	@SuppressWarnings("serial")
	public Main() {
		dist = new double[map.getNumRows()][map.getNumColumns()];
		distOld = new double[map.getNumRows()][map.getNumColumns()];
		dist[1][1] = 1.0;
		dist[1][2] = 0;
		dist[1][3] = 0;
		
		new Window(dist, map) {			
			@Override public void move(int direction) {
				updateDistrib(direction);
			}
		};
	}
	
	public static void main(String[] args) {
		new Main();
	}
	
	static class Simulator {
		private static final double sim[][] = {
			{0.00, 0.00, 0.00, 0.00, 0.00},
			{0.01, 0.03, 0.01, 0.00, 0.00},
			{0.03, 0.84, 0.03, 0.00, 0.00},
			{0.01, 0.03, 0.01, 0.00, 0.00},
			{0.00, 0.00, 0.00, 0.00, 0.00}
		}; 
		
		private static final double simOil[][] = {
			{0.00, 0.00, 0.00, 0.00, 0.00},
			{0.15, 0.10, 0.00, 0.00, 0.00},
			{0.20, 0.30, 0.00, 0.00, 0.00},
			{0.15, 0.10, 0.00, 0.00, 0.00},
			{0.00, 0.00, 0.00, 0.00, 0.00}
		}; 

		private static final double simOcc[][] = {
			{0.00, 0.00, 0.00, 0.00, 0.00},
			{0.00, 0.00, 0.10, 0.00, 0.00},
			{0.00, 0.00, 0.80, 0.00, 0.00},
			{0.00, 0.00, 0.10, 0.00, 0.00},
			{0.00, 0.00, 0.00, 0.00, 0.00}
		}; 
		
		private static final double simOilOcc[][] = {
			{0.00, 0.00, 0.00, 0.00, 0.00},
			{0.00, 0.00, 0.30, 0.00, 0.00},
			{0.00, 0.00, 0.40, 0.00, 0.00},
			{0.00, 0.00, 0.30, 0.00, 0.00},
			{0.00, 0.00, 0.00, 0.00, 0.00}
		};
		
		private static final double simtrans[][][][][] = generateRotatedTransitionMatrices(sim, simOil, simOcc, simOilOcc);
		
		/* use simulator transition tables to compute the new position of the robot */
		public void move(int direction) {
			int isOcc = 0;
			int isOil = 0;
			double[][] t = simtrans[direction][isOcc][isOil];
		}
	}
}
