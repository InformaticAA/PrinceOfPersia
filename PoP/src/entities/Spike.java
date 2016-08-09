package entities;

import java.awt.Graphics;
import java.awt.Rectangle;

import framework.Loader;

public class Spike extends Entity {
	
	private Rectangle baseBoundingBox;

	public Spike(int x, int y, int x_offset, int y_offset, Loader loader, String type) {
		super("Spike_" + type, x+x_offset, y+y_offset, loader);
		animations = loader.getAnimations(type);
		currentAnimation = animations.get("closed");
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
	@Override
	public void update(long elapsedTime){
	}
	
	public void updateReal(long elapsedTime){
		super.update(elapsedTime);
		if(this.getCurrentAnimation().getId().equals("opening")){
			if(this.getCurrentAnimation().isOver(false)){
				this.setCurrentAnimation("opened", FRAME_DURATION);
			}
		} else if(this.getCurrentAnimation().getId().equals("closing")){
			if(this.getCurrentAnimation().isOver(false)){
				this.setCurrentAnimation("closed", FRAME_DURATION);
			}
		}
	}
	
	@Override
	public void setCurrentAnimation(String newAnimation, int frameDuration){
		super.setCurrentAnimation(newAnimation, frameDuration);
	}
	
	@Override
	public void drawSelf(Graphics g){
		super.drawSelf(g);
		
//		g.setColor(Color.RED);
//		g.drawRect((int) baseBoundingBox.getX() - currentAnimation.getImage().getWidth(),
//				(int) baseBoundingBox.getY() - currentAnimation.getImage().getHeight(),
//				(int) baseBoundingBox.getWidth(),
//				(int) baseBoundingBox.getHeight());
//		g.setColor(Color.BLACK);
		
	}

	@Override
	public Entity copy() {
		return null;
	}
}
