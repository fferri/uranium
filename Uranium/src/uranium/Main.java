package uranium;

public class Main {
	public static final int L = 0;
	public static final int U = 1;
	public static final int R = 2;
	public static final int D = 3;
	
	private static final int dx[] = {-1, 0, 1, 0};
	private static final int dy[] = {0, -1, 0, 1};
	
	private double trans[][][][][];

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


	private double dist[][];
	private double distOld[][];
	
	/* 0 free
	 * 1 occ
	 * 2 oil
	 */
	private int mapp[][] = {
			{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
			{1,0,0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
			{1,0,0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
			{1,0,0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
			{1,0,0,0,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,2,0,0,0,0,0,0,0,1},
			{1,0,0,0,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
			{1,0,0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
			{1,0,0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
			{1,0,0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
			{1,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,1,1,1,1},
			{1,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,1,1,1,1},
			{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,2,2,0,0,0,0,1,1,0,0,0,0,0,0,0,1},
			{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,0,0,0,0,0,1,1,0,0,0,0,0,0,0,1},
			{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,1},
			{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,1},
			{1,0,0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,1},
			{1,0,0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,1},
			{1,0,0,0,0,2,0,0,0,2,1,1,1,1,1,1,1,1,0,0,0,0,1,1,0,0,0,0,0,0,0,1},
			{1,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,0,0,0,0,1,1,0,0,0,0,0,0,0,1},
			{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
			{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
			{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
			{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
			{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
	};
	
	private boolean checkBounds(int i, int j) {
		if(i < 0 || j < 0 || i >= mapp.length || j >= mapp[0].length)
			return false;
		return true;
	}
	
	private boolean isOcc(int i, int j, int dir) {
		if(checkBounds(i + dy[dir], j + dx[dir]))
			return mapp[i + dy[dir]][j + dx[dir]] == 1;
		else
			return true;
	}
	
	private boolean isOil(int i, int j) {
		return mapp[i][j] == 2;
	}
	
	private void normalize() {
		double norm_factor = 0;
		for(int k = 0; k < mapp.length; k++)
			for(int l = 0; l < mapp[0].length; l++)
				norm_factor += dist[k][l];

		for(int k = 0; k < mapp.length; k++)
			for(int l = 0; l < mapp[0].length; l++)
				dist[k][l] /= norm_factor;
	}
	
	private void updateDistrib(int dir) {
		// clone dist[i][j]
		for(int i = 0; i < mapp.length; i++)
			for(int j = 0; j < mapp[0].length; j++)
				distOld[i][j] = dist[i][j];
		
		// filter every cell
		for(int k = 0; k < mapp.length; k++)
			for(int l = 0; l < mapp[0].length; l++)
				dist[k][l] = filter(k, l, dir);
		
		// the TRICK
		for(int i = 0; i < mapp.length; i++)
			for(int j = 0; j < mapp[0].length; j++)
				if(mapp[i][j] == 1)
					dist[i][j] = 0;
		
		normalize();
	}
	
	private double filter(int k, int l, int dir) {
		double summ = 0;
		for(int i = 0; i < mapp.length; i++)
			for(int j = 0; j < mapp[0].length; j++)
				summ += p(i, j, k, l, dir) * distOld[i][j];
		return summ;
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
		
		return trans[dir][isOcc(i,j,dir) ? 1 : 0][isOil(i,j) ? 1 : 0][indi][indj];
	}
	
	/*
	 * observation model:
	 * 
	 * p(Z[dir] | Xij, dir)
	 */
	private double p(int bumper, int i, int j, int dir) {
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
		trans = generateRotatedTransitionMatrices(matrix, matrixOil, matrixOcc, matrixOilOcc);
		
		dist = new double[mapp.length][mapp[0].length];
		distOld = new double[mapp.length][mapp[0].length];
		dist[1][1] = 1.0;
		dist[1][2] = 0;
		dist[1][3] = 0;
		
		new Window(dist, mapp) {			
			@Override public void move(int direction) {
				updateDistrib(direction);
			}
		};
	}
	
	public static void main(String[] args) {
		new Main();
	}
	
	class Simulator {
		
	}
}
