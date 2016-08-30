package data;

import java.util.LinkedList;
import java.util.List;

import entities.Door;
import entities.SpikeFloor;

public class Level {

	private int numLevel;
	private int rows;
	private int cols;
	private Room[][] rooms;
	
	public Level(int numLevel, int rows, int cols) {
		this.numLevel = numLevel;
		this.rows = rows;
		this.cols = cols;
		this.rooms = new Room[rows][cols];
	}
	
	/**
	 * Updates every room in the level
	 */
	public void update(long elapsedTime) {
		
		for (int i = 0; i < rooms.length; i++) {
			for (int j = 0; j < rooms[0].length; j++) {
				if (rooms[i][j] != null) {
					rooms[i][j].update(elapsedTime);
				}
			}
		}
	}
	
	public void addRoom(Room room) {
		rooms[room.getRow()][room.getCol()] = room;
	}
	
	public Room getRoom(int row, int col) {
		return rooms[row - 1][col - 1];
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
	
	public List<Door> getDoors(){
		List<Door> doors = new LinkedList<Door>();
		for (int i = 0; i < rooms.length; i++) {
			for (int j = 0; j < rooms[0].length; j++) {
				if (rooms[i][j] != null) {
					doors.addAll(rooms[i][j].getDoors());
				}
			}
		}
		return doors;
	}
}