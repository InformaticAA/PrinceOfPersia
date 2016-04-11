package entities;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Hashtable;

import framework.Animation;
import framework.Loader;

public class Character extends Entity {

	/* Attributes */
	protected int hp;
	protected int maxHp;
	
	/* Movement constants */
	protected final int gravity = 2;
	protected final int maxxSpeed = 10;
	protected final int maxySpeed = 7;
	protected final int maxfightSpeed = 3;
	protected final int jumpSpeed = 5;
	protected final int fallSpeed = 3;
	protected final int FRAME_DURATION = 7; //6
	protected final int MOVE_SPEED = 2;

	/* Movement variables */
	protected int xSpeed;
	protected int ySpeed;
	protected boolean leftBlocked;
	protected boolean rightBlocked;
	
	/* Animations */
	protected String orientation;
	
	/* Splash */
	protected Splash splash;
	protected boolean canShowSplash;
	
	/* Sword */
	protected SwordFighting sword;
	
	public Character(int x, int y, Loader loader, String orientation) {
		super("Character", x,y,loader);
		this.orientation = orientation;
		this.canShowSplash = true;
	}
	
	@Override
	public void update(long elapsedTime) {
		super.update(elapsedTime);
		
		/* Updates the position of the bounding box */
		enableBoundingBox();
	}
	
	@Override
	public void drawSelf(Graphics g){
		if(sword!=null){
			sword.drawSelf(g);
		}
		super.drawSelf(g);
		splash.drawSelf(g);
	}
	
	
	
	public void moveCharacter(){
		
		/* If character is blocked sideways, he cant move horizontally */
		if (leftBlocked || rightBlocked) {
			xSpeed = 0;
		}
		
		setX(x + xSpeed);
		setY(y + ySpeed);
		this.boundingBox.translate(xSpeed, ySpeed);
	}
	
	/**
	 * Checks collision with other characters
	 * @return true if both rectangle collide,
	 * false otherwise
	 */
	@Override
	public boolean intersects(Entity entity, long elapsedTime) {
		boolean intersection = false;
		
		/* Creates the bounding box for the next step */
		Rectangle nextStep = new Rectangle((int) boundingBox.getX(),
					(int) boundingBox.getY(),
					(int) boundingBox.getWidth(),
					(int) boundingBox.getHeight());
	
		nextStep.translate(xSpeed, ySpeed);
		
		Rectangle r2 = entity.getBoundingBox();
		
		/* Checks if collision will take place in the next step */
		if (nextStep != null && r2 != null) {
			intersection = nextStep.intersects(r2);
		}
		else {
			intersection = false;
		}
		return intersection;
	}
	
	public void setMoveSpeed(int moveSpeed) {
		
		/* Unlocks player's movement in one direction */
		if (moveSpeed > 0 && leftBlocked) {
			leftBlocked = false;
		} else if (moveSpeed < 0 && rightBlocked) {
			rightBlocked = false;
		}
		
		/* Sets player's horizontal speed */
		this.xSpeed = moveSpeed;
	}
	
	public void setMoveSpeed(int moveSpeed, String blockedSide) {
		
		/* Sets player's horizontal speed */
		this.xSpeed = moveSpeed;

		/* Blocks player's movement in one direction */
		this.leftBlocked = blockedSide.equals("left");
		this.rightBlocked = blockedSide.equals("right");
		this.xSpeed = moveSpeed;
	}

	/**
	 * @return the hp
	 */
	public int getHp() {
		return hp;
	}

	/**
	 * @param hp the hp to set
	 */
	public void setHp(int hp) {
		this.hp = hp;
	}

	/**
	 * @return the maxHp
	 */
	public int getMaxHp() {
		return maxHp;
	}

	/**
	 * @param maxHp the maxHp to set
	 */
	public void setMaxHp(int maxHp) {
		this.maxHp = maxHp;
	}

	/**
	 * @return the xSpeed
	 */
	public int getxSpeed() {
		return xSpeed;
	}

	/**
	 * @param xSpeed the xSpeed to set
	 */
	public void setxSpeed(int xSpeed) {
		this.xSpeed = xSpeed;
	}

	/**
	 * @return the ySpeed
	 */
	public int getySpeed() {
		return ySpeed;
	}

	/**
	 * @param ySpeed the ySpeed to set
	 */
	public void setySpeed(int ySpeed) {
		this.ySpeed = ySpeed;
	}

	/**
	 * @return the leftBlocked
	 */
	public boolean isLeftBlocked() {
		return leftBlocked;
	}

	/**
	 * @param leftBlocked the leftBlocked to set
	 */
	public void setLeftBlocked(boolean leftBlocked) {
		this.leftBlocked = leftBlocked;
	}

	/**
	 * @return the rightBlocked
	 */
	public boolean isRightBlocked() {
		return rightBlocked;
	}

	/**
	 * @param rightBlocked the rightBlocked to set
	 */
	public void setRightBlocked(boolean rightBlocked) {
		this.rightBlocked = rightBlocked;
	}

	/**
	 * @return the orientation
	 */
	public String getOrientation() {
		return orientation;
	}

	/**
	 * @param orientation the orientation to set
	 */
	public void setOrientation(String orientation) {
		this.orientation = orientation;
	}

	/**
	 * @return the gravity
	 */
	public int getGravity() {
		return gravity;
	}

	/**
	 * @return the maxxSpeed
	 */
	public int getMaxxSpeed() {
		return maxxSpeed;
	}

	/**
	 * @return the maxySpeed
	 */
	public int getMaxySpeed() {
		return maxySpeed;
	}

	/**
	 * @return the maxfightSpeed
	 */
	public int getMaxfightSpeed() {
		return maxfightSpeed;
	}

	/**
	 * @return the jumpSpeed
	 */
	public int getJumpSpeed() {
		return jumpSpeed;
	}

	/**
	 * @return the fallSpeed
	 */
	public int getFallSpeed() {
		return fallSpeed;
	}

	@Override
	public Entity copy() {
		Character newCharacter = new Character(x, y, loader, orientation);
		
		Hashtable<String, Animation> newAnimations = this.getAnimations();
		
		
		return newCharacter;
	}
	
	protected void setSplashVisible(boolean visible){
		if(visible && canShowSplash){
			this.splash.setX(this.getActualCentre() + this.splash.getCurrentAnimation().getImage().getWidth()/2);
			this.splash.setY(this.y - 16);
			this.splash.setVisible(true);
		} else{
			this.splash.setVisible(false);
		}
	}
	
	public void setCanShowSplash(boolean canShowSplash) {
		this.canShowSplash = canShowSplash;
	}
	
	public int getActualCentre(){
		return this.getX() - this.getCurrentAnimation().getImage().getWidth()/2;
	}
//	
	public int getCharCentre(){
		return this.getX() - this.getAnimations().get("sword idle_left").getFrame(0).getImage().getWidth()/2;
	}
	
	public int xDistanceChar(Character e){
		return Math.abs(this.getCharCentre() - e.getCharCentre());
	}
	
	public void manageSword(String animation, int currentFrame, boolean newSword){}
}
