package entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import framework.Loader;

public class LooseFloor extends Entity {
	
	private boolean activated;
	private Rectangle baseBoundingBox;

	public LooseFloor(int x, int y, int x_offset, int y_offset, Loader loader, String loose_type) {
		super("LooseFloor" + loose_type, x+x_offset, y+y_offset, loader);
		activated = false;
		animations = loader.getAnimations("loose_floor");
		currentAnimation = animations.get(loose_type);
		currentAnimation.setFrameDuration(4);
		
		/* Sets the bounding box */
		enableBoundingBox(this.x + 25, 
					this.y + currentAnimation.getImage().getHeight()/2 - 4,
					currentAnimation.getImage().getWidth() - 25,
					currentAnimation.getImage().getHeight()/2 + 4);
		
		baseBoundingBox = new Rectangle(this.x, 
				this.y + 2*currentAnimation.getImage().getHeight()/3 + 5,
				currentAnimation.getImage().getWidth(),
				currentAnimation.getImage().getHeight()/3 - 4);
		
	}
	
	/**
	 * @return the activated
	 */
	public boolean isActivated() {
		return activated;
	}
	
	/**
	 * @param activated the activated to set
	 */
	public void setActivated(boolean activated) {
		this.activated = activated;
	}
	
	@Override
	public void update(long elapsedTime){
		super.update(elapsedTime);
		if(currentAnimation.getId().equals("shaking") && currentAnimation.isOver(false)){
			System.out.println("Finalizamos");
			currentAnimation = animations.get("idle");
		}
	}
	
	@Override
	public void setCurrentAnimation(String newAnimation, int frameDuration){
		super.setCurrentAnimation(newAnimation, frameDuration);
		System.out.println("hola");
		if(currentAnimation.getId().equals("shaking")){
			int sound = (int)(Math.random()*3 + 1);
			loader.getSound("tile moving " + sound).play();
		}
	}
	
	@Override
	public void drawSelf(Graphics g){
		super.drawSelf(g);
		
		g.setColor(Color.RED);
		g.drawRect((int) baseBoundingBox.getX() - currentAnimation.getImage().getWidth(),
				(int) baseBoundingBox.getY() - currentAnimation.getImage().getHeight(),
				(int) baseBoundingBox.getWidth(),
				(int) baseBoundingBox.getHeight());
		g.setColor(Color.BLACK);
		
	}

	@Override
	public Entity copy() {
		return null;
	}
}
