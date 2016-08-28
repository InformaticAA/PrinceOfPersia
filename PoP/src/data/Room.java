package data;

import java.awt.Graphics2D;
import java.util.LinkedList;
import java.util.List;

import entities.Character;
import entities.Door;
import entities.Entity;

public class Room {

	private final int rows = 4;
	private final int cols = 10;
	private int row;
	private int col;
	private Square[][] grid;
	private List<Entity> background;
	private List<Entity> foreground;
	private List<Character> characters;
	
	public Room(int row, int col) {
		this.row = row;
		this.col = col;
		this.grid = new Square[rows][cols];
		this.background = new LinkedList<Entity>();
		this.foreground = new LinkedList<Entity>();
		this.characters = new LinkedList<Character>();
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
	
	public void addBackground(List<Entity> newEntities){
		this.background.addAll(newEntities);
	}
	
	public void addForeground(List<Entity> newEntities){
		this.foreground.addAll(newEntities);
	}
	
	public void addCharacters(List<Character> newCharacters){
		this.characters.addAll(newCharacters);
	}
	
	public void addCharacter(Character character){
		this.characters.add(character);
	}

	/**
	 * @return the background
	 */
	public List<Entity> getBackground() {
		return background;
	}

	/**
	 * @param background the background to set
	 */
	public void setBackground(LinkedList<Entity> background) {
		this.background = background;
	}

	/**
	 * @return the foreground
	 */
	public List<Entity> getForeground() {
		return foreground;
	}

	/**
	 * @param foreground the foreground to set
	 */
	public void setForeground(LinkedList<Entity> foreground) {
		this.foreground = foreground;
	}

	/**
	 * @return the characters
	 */
	public List<Character> getCharacters() {
		return characters;
	}

	/**
	 * @param characters
	 */
	public void setCharacters(List<Character> characters) {
		this.characters = characters;
	}
	
	public void deleteEntityBackground(Entity entity, Square square){
		background.remove(entity);
		square.getBackground().remove(entity);
	}
	
	public void deleteEntityForeground(Entity entity, Square square){
		foreground.remove(entity);
		square.getForeground().remove(entity);
	}
	
	public Entity returnNamedEntityBackground(String name, Square square){
		Entity toBeDeleted = null;
		for (Entity e : square.getBackground()) {
			if(e.getTypeOfEntity().startsWith(name)){
				toBeDeleted = e;
			}
		}
		return toBeDeleted;
	}
	
	public Entity returnNamedEntityForeground(String name, Square square){
		Entity toBeDeleted = null;
		for (Entity e : square.getForeground()) {
			if(e.getTypeOfEntity().startsWith(name)){
				toBeDeleted = e;
			}
		}
		return toBeDeleted;
	}
	
	public void deleteEntityBackground(Entity entity){
		background.remove(entity);
	}
	
	public void insertAfterEntity(Entity toBeInserted, Entity after){
		int index = background.indexOf(after);
		System.out.println("Amoh a imprimir");
	}
	
	public void addToBackground(Entity toBeAdded){
		background.add(toBeAdded);
	}
	
	public void addToBackground(Entity toBeAdded, Square square){
		background.add(0,toBeAdded);
		square.getBackground().add(0,toBeAdded);
	}
	
	public List<Door> getDoors(){
		List<Door> doors = new LinkedList<Door>();
		List<Entity> bEntities = this.getBackground();
		
		for(Entity bgE : bEntities){
			if(bgE.getTypeOfEntity().equals("Door_normal") || bgE.getTypeOfEntity().equals("Door_final")){
				((Door)bgE).setRoom(this.row, this.col);
				doors.add((Door) bgE);
			}
		}
		
		return doors;
	}
	
	public void deleteCharacter(Entity character){
		this.characters.remove(character);
	}
	
	public Entity getGuard(){
		Entity guard = null;
		for(Character e : this.getCharacters()){
			if(e.getTypeOfEntity().startsWith("Enemy")){
				guard = e;
			}
		}
		return guard;
	}
	
}
