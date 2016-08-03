package data;

import java.awt.Graphics2D;
import java.util.ArrayList;

import entities.Entity;
import entities.Character;

public class Room {

	private final int rows = 4;
	private final int cols = 10;
	private int row;
	private int col;
	private Square[][] grid;
	private ArrayList<Entity> background;
	private ArrayList<Entity> foreground;
	private ArrayList<Character> characters;
	
	public Room(int row, int col) {
		this.row = row;
		this.col = col;
		this.grid = new Square[rows][cols];
		this.background = new ArrayList<Entity>();
		this.foreground = new ArrayList<Entity>();
		this.characters = new ArrayList<Character>();
	}
	
	/**
	 * Updates all squares in the room
	 */
	public void update(long elapsedTime) {
		
		for (Entity entity : background) {
			entity.update(elapsedTime);
		}
		
		for (Character character : characters) {
			character.update(elapsedTime);
		}
		
		for (Entity entity : foreground) {
			entity.update(elapsedTime);
		}
	}
	
	/**
	 * Draws room's content
	 */
	public void draw(Graphics2D g) {
		for (Entity entity : background) {
			entity.drawSelf(g);
		}
		
		for (Character character : characters) {
			character.drawSelf(g);
		}

		for (Entity entity : foreground) {
			entity.drawSelf(g);
		}
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
	
	public void addBackground(ArrayList<Entity> newEntities){
		this.background.addAll(newEntities);
	}
	
	public void addForeground(ArrayList<Entity> newEntities){
		this.foreground.addAll(newEntities);
	}
	
	public void addCharacters(ArrayList<Character> newCharacters){
		this.characters.addAll(newCharacters);
	}
	
	public void addCharacter(Character character){
		this.characters.add(character);
	}

	/**
	 * @return the background
	 */
	public ArrayList<Entity> getBackground() {
		return background;
	}

	/**
	 * @param background the background to set
	 */
	public void setBackground(ArrayList<Entity> background) {
		this.background = background;
	}

	/**
	 * @return the foreground
	 */
	public ArrayList<Entity> getForeground() {
		return foreground;
	}

	/**
	 * @param foreground the foreground to set
	 */
	public void setForeground(ArrayList<Entity> foreground) {
		this.foreground = foreground;
	}

	/**
	 * @return the characters
	 */
	public ArrayList<Character> getCharacters() {
		return characters;
	}

	/**
	 * @param characters
	 */
	public void setCharacters(ArrayList<Character> characters) {
		this.characters = characters;
	}
	
	public void deleteEntityBackground(Entity entity){
		System.out.println(background.remove(entity));
	}
	
	public void insertAfterEntity(Entity toBeInserted, Entity after){
		int index = background.indexOf(after);
		System.out.println("Amoh a imprimir");
	}
	
	public void addToBackground(Entity toBeAdded){
		background.add(toBeAdded);
	}
	
}
