package data;

public class Level {

	private int numLevel;
	private int rows;
	private int cols;
	private Room[][] rooms;
	
	public Level(int numLevel) {
		this.numLevel = numLevel;
		this.rows = 0;
		this.cols = 0;
		this.rooms = new Room[rows][cols];
	}
	
	public void addRoom(Room room) {
		rooms[room.getRow()][room.getCol()] = room;
	}
	
	public Room getRoom(int row, int col) {
		return rooms[row][col];
	}

	/**
	 * @return the numLevel
	 */
	public int getNumLevel() {
		return numLevel;
	}

	/**
	 * @param numLevel the numLevel to set
	 */
	public void setNumLevel(int numLevel) {
		this.numLevel = numLevel;
	}

	/**
	 * @return the rows
	 */
	public int getRows() {
		return rows;
	}

	/**
	 * @param rows the rows to set
	 */
	public void setRows(int rows) {
		this.rows = rows;
	}

	/**
	 * @return the cols
	 */
	public int getCols() {
		return cols;
	}

	/**
	 * @param cols the cols to set
	 */
	public void setCols(int cols) {
		this.cols = cols;
	}

	/**
	 * @return the rooms
	 */
	public Room[][] getRooms() {
		return rooms;
	}

	/**
	 * @param rooms the rooms to set
	 */
	public void setRooms(Room[][] rooms) {
		this.rooms = rooms;
	}
	
}