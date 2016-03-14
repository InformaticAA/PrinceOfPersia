package entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Hashtable;

import framework.Animation;
import framework.Loader;

public abstract class Entity {
	
	public static final int FRAME_DURATION = 5;
	
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
//		x_draw = x - currentAnimation.getImage().getWidth();
//		y_draw = y - currentAnimation.getImage().getHeight();
//		enableBoundingBox();
	}
	
	/**
	 * Draws the entity's actual frame of current animation
	 * @param g
	 */
	public void drawSelf(Graphics g) {
		
		/* Draws the entity */
		BufferedImage img = currentAnimation.getImage();
		g.drawImage(img, x - currentAnimation.getImage().getWidth(),
				y - currentAnimation.getImage().getHeight(), null);
		
		/* Draws the entity's bounding box */
		if (boundingBox != null) {
			int width = currentAnimation.getImage().getWidth();
			int height = currentAnimation.getImage().getHeight();

			g.setColor(Color.RED);
			g.drawRect((int) boundingBox.getX() - width,
					(int) boundingBox.getY() - height,
					width, height);
			g.setColor(Color.BLACK);
		}
	}
	
	/**
	 * Creates a bounding box that covers the entity
	 */
	public void enableBoundingBox() {
		
		if (boundingBox != null) {
			boundingBox = new Rectangle(x,y,currentAnimation.getImage().getWidth(),
					currentAnimation.getImage().getHeight());
		}
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
	public void setCurrentAnimation(String animationName, int frameDuration) {
		this.currentAnimation = animations.get(animationName);
		currentAnimation.reset();
		currentAnimation.setFrameDuration(frameDuration);
	}
	
	public String getTypeOfEntity() {
		return typeOfEntity;
	}

}
