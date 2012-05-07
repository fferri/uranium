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
	
	private final Map map;
	
	// the "REAL" position
	private int i,j;
	
	public Simulator(Map map) {
		if(map == null)
			throw new RuntimeException("simulator needs a valid map");
		this.map = map;
	}
	
	/**
	 * sets the position of robot to the specified value
	 * (only if it is a valid position and it is free)
	 * 
	 * @param i the row
	 * @param j the column
	 */
	public void setPosition(int i, int j) {
		if(map.checkBounds(i, j) && !map.isOcc(i, j)) {
			this.i = i;
			this.j = j;
		}
	}

	/**
	 * use simulator transition tables to compute the new position of the robot
	 * @param direction
	 */
	public void move(int direction) {
		double[][] t = simtrans[direction][map.isOcc(i, j, direction) ? 1 : 0][map.isOil(i, j) ? 1 : 0];
	}
}
