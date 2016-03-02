package data;

import java.awt.Graphics2D;
import java.util.ArrayList;

import entities.Entity;

public class Square {

	private ArrayList<Entity> background;
	private ArrayList<Entity> entities;
	private ArrayList<Entity> foreground;
	
	public Square() {
		
	}
	
	/**
	 * Updates each entity included in the square
	 * @param elapsedTime
	 */
	public void update(long elapsedTime) {
		
		for (Entity e : background) {
			e.update(elapsedTime);
		}
		
		for (Entity e : entities) {
			e.update(elapsedTime);
		}
		
		for (Entity e : foreground) {
			e.update(elapsedTime);
		}
	}
	
	/**
	 * Draws all square's content
	 * @param g
	 */
	public void draw(Graphics2D g) {
		
		for (Entity e : background) {
			e.drawSelf(g);
		}
		
		for (Entity e : entities) {
			e.drawSelf(g);
		}
		
		for (Entity e : foreground) {
			e.drawSelf(g);
		}
		
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

}
