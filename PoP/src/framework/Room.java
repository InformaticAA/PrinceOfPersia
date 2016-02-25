package framework;

public class Room {

	private int row;
	private int col;
	private Square[][] grid;
	
	public Room(int row, int col) {
		this.row = row;
		this.col = col;
		this.grid = new Square[row][col];
	}
	
	/**
	 * 
	 * @param row
	 * @param col
	 * @return the square located at grid[row][col]
	 */
	public Square getSquare(int row, int col) {
		return grid[row][col];
	}
	
	/**
	 * 
	 * @param row
	 * @param col
	 * @param square
	 * Sets the value of square located at grid[row][col]
	 */
	public void setSquare(int row, int col, Square square) {
		grid[row][col] = square;
	}

	/**
	 * @return the row
	 */
	public int getRow() {
		return row;
	}

	/**
	 * @param row the row to set
	 */
	public void setRow(int row) {
		this.row = row;
	}

	/**
	 * @return the col
	 */
	public int getCol() {
		return col;
	}

	/**
	 * @param col the col to set
	 */
	public void setCol(int col) {
		this.col = col;
	}

	/**
	 * @return the entities
	 */
	public Square[][] getGrid() {
		return grid;
	}

	/**
	 * @param entities the entities to set
	 */
	public void setEntities(Square[][] grid) {
		this.grid = grid;
	}
	
}
