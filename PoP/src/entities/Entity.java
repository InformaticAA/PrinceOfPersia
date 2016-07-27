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
	protected Color boundingBoxColor;
	protected Loader loader;
	
	public Entity(String typeOfEntity, int x, int y, Loader loader) {
		this.typeOfEntity = typeOfEntity;
		this.x = x;
		this.y = y;
		this.loader = loader;
		currentAnimation = null;
		boundingBox = null;
		boundingBoxColor = Color.RED;
	}
	
	public void update(long elapsedTime) {
		
		/* Updates animation */
//		if(this.typeOfEntity.equals("MPEnemy") && (this.currentAnimation.getId().startsWith("sword idle") || this.currentAnimation.getId().startsWith("block") || this.currentAnimation.getId().startsWith("attack"))){
//			System.out.println(this.getCurrentAnimation().getId() + "(" + this.getCurrentAnimation().getCurrentFrame() + ")" + " - " + this.x + " - " + this.y);
//		}
		currentAnimation.update(elapsedTime);
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
			g.setColor(boundingBoxColor);
			g.drawRect((int) boundingBox.getX(),
					(int) boundingBox.getY(),
					(int) boundingBox.getWidth(),
					(int) boundingBox.getHeight());
			g.setColor(Color.BLACK);
		}
		
		/* Draws a red cross in the center of the entity */
//		if (typeOfEntity.startsWith("FloorPanel_")) {
//			
//			if (typeOfEntity.contains("left")) g.setColor(Color.RED);
//			else if (typeOfEntity.contains("right")) g.setColor(Color.YELLOW);
//			
//			int[] center = getCenter();
//			int cx = center[0];
//			int cy = center[1];
//			int lineLength = 20;
//			
//			g.drawLine(cx - lineLength/2, cy - lineLength/2, cx + lineLength/2, cy + lineLength/2);
//			g.drawLine(cx - lineLength/2, cy + lineLength/2, cx + lineLength/2, cy - lineLength/2);
//			
//			g.setColor(Color.BLACK);	
//		}
	}
	
	/**
	 * Creates a bounding box that covers the entity
	 */
	public void enableBoundingBox() {
		
		if (boundingBox != null) {
			boundingBox = new Rectangle(x - currentAnimation.getImage().getWidth(),
					y - currentAnimation.getImage().getHeight(),
					currentAnimation.getImage().getWidth(),
					currentAnimation.getImage().getHeight());
			
//			System.out.println("ebb: " + currentAnimation.getId() + ", f: " + currentAnimation.getCurrentFrame());
		}
	}
	
	/**
	 * Creates a bounding box with given measures
	 */
	public void enableBoundingBox(int x, int y, int width, int height) {
		boundingBox = new Rectangle(x - currentAnimation.getImage().getWidth(),
				y - currentAnimation.getImage().getHeight(), width, height);
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
	public boolean intersects(Entity entity, long elapsedTime) {
		boolean intersects = false;

		Rectangle r1 = boundingBox;
		Rectangle r2 = entity.getBoundingBox();
		
		if (r1 != null && r2 != null) {
			intersects = r1.intersects(r2);
			
//			/* Debug */
//			Rectangle intersection = r1.intersection(r2);
//			System.out.println(intersection.x + ", " + intersection.y + 
//					", " + intersection.width + ", " + intersection.height);
		}
		else {
			intersects = false;
		}
		return intersects;
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

	/**
	 * @return the boundingBoxColor
	 */
	public Color getBoundingBoxColor() {
		return boundingBoxColor;
	}

	/**
	 * @param boundingBoxColor the boundingBoxColor to set
	 */
	public void setBoundingBoxColor(Color boundingBoxColor) {
		this.boundingBoxColor = boundingBoxColor;
	}
	
	/**
	 * 
	 * @return square coords in the room corresponding to the
	 * current x,y coords of the entity
	 */
	public int[] getSquare() {
		int i = y - 6;
		
		if (i <= 0) i = 0;
		else i = (i / 126) + 1;
		
		int j = x / 64;
		return new int[]{i, j};
	}
	
	/**
	 * 
	 * @return square coords in the room corresponding to the
	 * current x,y coords of the entity
	 */
	public int[] getSquare(int xx, int yy) {
		int i = yy - 6;
		
		if (i <= 0) i = 0;
		else i = (i / 126) + 1;
		
		int j = xx / 64;
		return new int[]{i, j};
	}
	
	/**
	 * 
	 * @return the center coords of the entity
	 */
	public int[] getCenter() {
		int width2 = currentAnimation.getImage().getWidth()/2;
		int height2 = currentAnimation.getImage().getHeight()/2;
		int xx = x - width2;
		int yy = y - height2;
		
		return new int[]{xx,yy};
	}
	
	public int getXCentre(){
		return this.getX() - this.getCurrentAnimation().getImage().getWidth()/2;
	}

	public int xDistanceEntity(Entity e){
		return Math.abs(this.getXCentre() - e.getXCentre());
	}
	
	public abstract Entity copy();
}
