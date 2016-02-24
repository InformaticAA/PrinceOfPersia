package framework;

import java.util.ArrayList;

import entities.Entity;

public class Room {

	private int row;
	private int col;
	private ArrayList<Entity> entities;
	
	public Room(int row, int col) {
		this.row = row;
		this.col = col;
		this.entities = new ArrayList<Entity>();
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
	public ArrayList<Entity> getEntities() {
		return entities;
	}

	/**
	 * @param entities the entities to set
	 */
	public void setEntities(ArrayList<Entity> entities) {
		this.entities = entities;
	}
	
}
