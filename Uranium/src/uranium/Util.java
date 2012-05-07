package uranium;

public class Util {
	public static double[][] rotateMatrixRight(double[][] matrix) {
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
	
	public static double[][][][][] generateRotatedTransitionMatrices(double m[][], double mOil[][], double mOcc[][], double mOilOcc[][]) {
		double[][][][][] ret = new double[4][2][2][][];
		
		ret[Main.L][0][0]=m;
		ret[Main.L][0][1]=mOil;
		ret[Main.L][1][0]=mOcc;
		ret[Main.L][1][1]=mOilOcc;
		for(int dir = 1; dir < 4; dir++)
			for(int occ = 0; occ < 2; occ++)
				for(int oil = 0; oil < 2; oil++)
					ret[dir][occ][oil] = Util.rotateMatrixRight(ret[dir - 1][occ][oil]);
		
		return ret;
	}
}
