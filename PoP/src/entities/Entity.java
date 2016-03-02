package entities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Hashtable;

import framework.Animation;
import framework.Loader;

public abstract class Entity {
	
	protected int x, y;
	protected boolean back;
	protected Hashtable<String,Animation> animations;
	protected Animation currentAnimation;
	protected Loader loader;
	
	public Entity(int x, int y, boolean back, Loader loader) {
		this.x = x;
		this.y = y;
		this.back = back;
		this.loader = loader;
		currentAnimation = null;
	}
	
	public void update(long elapsedTime) {
		currentAnimation.update(elapsedTime);
	}
	
	/**
	 * Draws the entity's actual frame of current animation
	 * @param g
	 */
	public void drawSelf(Graphics g) {
		BufferedImage img = currentAnimation.getImage();
		g.drawImage(img, x - img.getWidth(), y - img.getHeight(), null);
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
	
}
