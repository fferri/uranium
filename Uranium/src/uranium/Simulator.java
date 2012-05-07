package uranium;

public class Simulator {
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
	
	private static final double simtrans[][][][][] = Util.generateRotatedTransitionMatrices(sim, simOil, simOcc, simOilOcc);
	
	/* use simulator transition tables to compute the new position of the robot */
	public void move(int direction) {
		int isOcc = 0;
		int isOil = 0;
		double[][] t = simtrans[direction][isOcc][isOil];
	}
}
