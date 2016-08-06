package entities;

import java.awt.Graphics;
import java.awt.Rectangle;

import framework.Loader;
import kuusisto.tinysound.Sound;

public class Opener extends Entity {
	
	private Rectangle baseBoundingBox;
	private Door door;
	private Sound pressed;
	private boolean isPressed;
	private int id;
	private Player p;

	public Opener(int x, int y, int x_offset, int y_offset, Loader loader, int id) {
		super("Opener", x+x_offset, y+y_offset, loader);
		animations = loader.getAnimations("opener");
		currentAnimation = animations.get("unpressed");
		pressed = loader.getSound("button pressed");
		this.id = id;
		
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
		super.update(elapsedTime);
		
		/* Check if the player is pressing the opener */
		if(p != null){
			if((p.getBoundingBox().getMinX() > this.getBoundingBox().getMaxX() ) || 
					(p.getBoundingBox().getMaxX() < this.getBoundingBox().getMinX())){
				this.unpress();
				p = null;
			} else{
				door.openDoor();
			}
		}
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
	
	public void openDoor(Player p){
		door.openDoor();
		if(!isPressed){
			this.p = p;
			this.setCurrentAnimation("pressed", FRAME_DURATION);
			isPressed = true;
			pressed.play();
		}
	}
	
	public void unpress(){
		if(isPressed){
			this.setCurrentAnimation("unpressed", FRAME_DURATION);
			isPressed = false;
		}
	}
	
	public void setDoor(Door door){
		this.door = door;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public Door getDoor(){
		return this.door;
	}
}
