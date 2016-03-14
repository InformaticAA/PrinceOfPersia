package data;

import java.awt.Graphics2D;
import java.util.ArrayList;

import entities.Entity;
import entities.Character;

public class Square {

	private ArrayList<Entity> background;
	private ArrayList<Character> characters;
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
		
		for (Character c : characters) {
			c.update(elapsedTime);
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
		
		for (Character c : characters) {
			c.drawSelf(g);
		}
		
		for (Entity e : foreground) {
			e.drawSelf(g);
		}
		
	}
	
	/**
	 * @return the characters
	 */
	public ArrayList<Character> getCharacters() {
		return characters;
	}

	/**
	 * @param characters the characters to set
	 */
	public void setCharacters(ArrayList<Character> characters) {
		this.characters = characters;
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
