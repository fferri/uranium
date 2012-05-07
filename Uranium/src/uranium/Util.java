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
}
