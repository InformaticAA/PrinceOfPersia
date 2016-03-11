package entities;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Hashtable;

import framework.Animation;
import framework.Loader;

public abstract class Entity {
	
	protected String typeOfEntity;
	protected int x, y;
	protected int x_draw, y_draw;
	
	protected Hashtable<String,Animation> animations;
	protected Animation currentAnimation;
	protected Rectangle boundingBox;
	protected Loader loader;
	
	public Entity(String typeOfEntity, int x, int y, Loader loader) {
		this.typeOfEntity = typeOfEntity;
		this.x = x;
		this.y = y;
		this.loader = loader;
		currentAnimation = null;
		boundingBox = null;
	}
	
	public void update(long elapsedTime) {
		
		/* Updates animation */
		currentAnimation.update(elapsedTime);
		
		/* Updates draw position */
		x_draw = x - currentAnimation.getImage().getWidth();
		y_draw = y - currentAnimation.getImage().getHeight();
	}
	
	/**
	 * Draws the entity's actual frame of current animation
	 * @param g
	 */
	public void drawSelf(Graphics g) {
		BufferedImage img = currentAnimation.getImage();
		g.drawImage(img, x_draw, y_draw, null);
	}
	
	/**
	 * Creates a bounding box that covers the entity
	 */
	public void enableBoundingBox() {
		boundingBox = new Rectangle(x_draw,y_draw,currentAnimation.getImage().getWidth(),
				currentAnimation.getImage().getHeight());
	}
	
	/**
	 * Creates a bounding box with given measures
	 */
	public void enableBoundingBox(int x, int y, int width, int height) {
		boundingBox = new Rectangle(x,y,width,height);
	}
	
	/**
	 * @return the bounding box of the entity
	 */
	public Rectangle getBoundingBox() {
		return boundingBox;
	}
	
	/**
	 * Checks collision with other characters
	 * @return true if both rectangle collide,
	 * false otherwise
	 */
	public boolean intersects(Entity entity) {
		Rectangle r1 = boundingBox;
		Rectangle r2 = entity.getBoundingBox();
		return r1.intersects(r2);
	}
	
	/**
	 * @return the x
	 */
	public int getX() {
		return x;
	}
	/**
	 * @param x the x to set
	 */
	public void setX(int x) {
		this.x = x;
	}
	/**
	 * @return the y
	 */
	public int getY() {
		return y;
	}
	/**
	 * @param y the y to set
	 */
	public void setY(int y) {
		this.y = y;
	}
	/**
	 * @return the animations
	 */
	public Hashtable<String, Animation> getAnimations() {
		return animations;
	}
	/**
	 * @param animations the animations to set
	 */
	public void setAnimations(Hashtable<String, Animation> animations) {
		this.animations = animations;
	}
	/**
	 * @return the currentAnimation
	 */
	public Animation getCurrentAnimation() {
		return currentAnimation;
	}
	/**
	 * @param currentAnimation the currentAnimation to set
	 */
	public void setCurrentAnimation(String animationName) {
		this.currentAnimation = animations.get(animationName);
	}
	
	public String getTypeOfEntity() {
		return typeOfEntity;
	}

}
