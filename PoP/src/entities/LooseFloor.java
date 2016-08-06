package entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import framework.Loader;

public class LooseFloor extends Entity {
	
	private boolean activated;
	private boolean falling;
	private Rectangle baseBoundingBox;
	private int row;
	private int col;
	private int room1;
	private int room2;
	private boolean broken;

	public LooseFloor(int x, int y, int x_offset, int y_offset, Loader loader, String loose_type) {
		super("LooseFloor" + loose_type, x+x_offset, y+y_offset, loader);
		activated = false;
		falling = false;
		animations = loader.getAnimations("loose_floor");
		currentAnimation = animations.get(loose_type);
		currentAnimation.setFrameDuration(4);
		broken = false;
		
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
		this.setCurrentAnimation("shaking", FRAME_DURATION*3);
	}
	
	public boolean isFalling() {
		return falling;
	}

	public void setFalling(boolean falling) {
		this.falling = falling;
	}

	@Override
	public void update(long elapsedTime){
		
	}
	
	@Override
	public void setCurrentAnimation(String newAnimation, int frameDuration){
		super.setCurrentAnimation(newAnimation, frameDuration);
	}
	
	public void makeSound(){
		int sound = (int)(Math.random()*3 + 1);
		loader.getSound("tile moving " + sound).play();
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
	
	public int getRow(){
		return this.row;
	}
	
	public void decreaseRow(){
		this.row--;
	}
	
	public void setRow(int row){
		this.row = row;
	}
	
	public int getCol(){
		return this.col;
	}
	
	public void setCol(int col){
		this.col = col;
	}

	public boolean isBroken() {
		return broken;
	}

	public void setBroken() {
		this.broken = true;
		loader.getSound("tile crashing").play();;
	}

	public int getRoom1() {
		return room1;
	}

	public void setRoom1(int room1) {
		this.room1 = room1;
	}

	public int getRoom2() {
		return room2;
	}

	public void setRoom2(int room2) {
		this.room2 = room2;
	}
	
	public void increaseRoom1(){
		this.room1++;
	}
	
	public void updateReal(long elapsedTime){
		/* Entity */
		currentAnimation.update(elapsedTime);
		if(this.currentAnimation.isLastFrame()){
			int currentFrame = currentAnimation.getCurrentFrame();
			String newSound = currentAnimation.getFrame(currentFrame).getSound();
			if(!newSound.equals("")){
				makeSound();
			}
		}
		
		/* LooseFloor */
		if(this.getCurrentAnimation().getId().equals("shaking")){
			if(this.getCurrentAnimation().isOver(false)){
				this.setCurrentAnimation("falling", FRAME_DURATION);
			}
		}
		if(this.getCurrentAnimation().getId().equals("falling")){
			if(this.currentAnimation.isLastFrame()){
				this.setY(this.getY() + 20);
			}
		}
	}
	
	public boolean isLastFrameMoving(){
		return (this.getCurrentAnimation().getId().equals("shaking") && 
				this.getCurrentAnimation().getCurrentFrame() == 3 &&
				this.getCurrentAnimation().isLastFrame());
	}
}
