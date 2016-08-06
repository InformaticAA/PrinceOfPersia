package entities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Hashtable;

import framework.Animation;
import framework.Loader;

public class Character extends Entity {

	private int framesDebug = 0;
	private boolean firstTime = true;
	
	/* Attributes */
	protected int hp;
	protected int maxHp;
	
	/* Movement constants */
	protected final int gravity = 1;
	protected final int maxxSpeed = 10;
	protected final int maxySpeed = 30;
	protected final int maxfightSpeed = 3;
	protected final int jumpSpeed = 5;
	protected final int fallSpeed = 3;
	protected final int FRAME_DURATION = 7; //6
	protected final int MOVE_SPEED = 2;
	
	/* Splash */
	protected Splash splash;
	protected boolean canShowSplash;
	
	/* Sword */
	protected SwordFighting sword;
	
	/* Life */
	protected Life[] life;

	/* Movement variables */
	protected int moveSpeed;
	protected int xSpeed;
	protected int ySpeed;
	protected String sound;
	protected boolean leftBlocked;
	protected boolean rightBlocked;
	protected boolean falling;
	protected boolean freeFall;
	protected boolean grounded;
	protected boolean jumping;
	
	/* Animations */
	protected String orientation;
	protected BufferedImage prevImage;
	protected int xFrameOffset;
	protected int yFrameOffset;
	
	public Character(int x, int y, Loader loader, String orientation) {
		super("Character", x,y,loader);
		this.orientation = orientation;
		this.falling = false;
		this.grounded = true;
		this.freeFall = false;
		this.jumping = false;
		this.canShowSplash = true;
	}
	
	protected void updateSpeed() {
		int currentFrame = currentAnimation.getCurrentFrame();
		String newSound = currentAnimation.getFrame(currentFrame).getSound();
		
		if (prevImage != null) {
			
			/* Sets charactes speed as current animation describes */
			int newxSpeed = currentAnimation.getFrameXSpeed(currentFrame, prevImage);
			int newySpeed = currentAnimation.getFrameYSpeed(currentFrame, prevImage);
			int newxFrameOffset = currentAnimation.getFrameXOffset(currentFrame, prevImage);
			int newyFrameOffset = currentAnimation.getFrameYOffset(currentFrame, prevImage);
			
			if (newxSpeed != 0 || newySpeed != 0 ||
					xFrameOffset != 0 || yFrameOffset != 0) {
				
				if (currentAnimation.isLastFrame()) {
					xSpeed = newxSpeed;
					ySpeed = newySpeed;
					
					xFrameOffset = newxFrameOffset;
					yFrameOffset = newyFrameOffset;
					
					sound = newSound;
				}
				else {
					xSpeed = 0;
					ySpeed = 0;
					xFrameOffset = 0;
					yFrameOffset = 0;
					
					sound = "";
				}
			}
			else {
				xSpeed = 0;
				ySpeed = 0;
				xFrameOffset = 0;
				yFrameOffset = 0;
				
				sound = "";
			}
		}
		else {
			xSpeed = 0;
			ySpeed = 0;
			xFrameOffset = 0;
			yFrameOffset = 0;
			
			sound = "";
		}
		prevImage = currentAnimation.getImage();
	}
	
	@Override
	public void update(long elapsedTime) {
		super.update(elapsedTime);
		
		if (currentAnimation.getId().contains("scaling") ||
				currentAnimation.getId().contains("clipping") ||
				currentAnimation.getId().contains("hanging") ||
				currentAnimation.getId().contains("jump") ||
				!firstTime) {
			
			if (framesDebug == 0) {
				System.out.println(currentAnimation.getId() + ": "
									+ currentAnimation.getCurrentFrame()
									+ " -> (" + getX() + ", " + getY() + ")");
			}
			
			if (framesDebug == 4) {
				framesDebug = -1;
			}
			
			framesDebug++;
			firstTime = false;
		}
		
		/* Updates the position of the bounding box */
		enableBoundingBox();
	}
	
	public void moveCharacter(){
		
		/* If character is blocked sideways, it cant move horizontally */
		if (leftBlocked || rightBlocked) {
			xSpeed = 0;
		}
		
		/* Applies gravity if falling */
		if (!grounded && falling) {
			int newySpeed = ySpeed + gravity;
			
			if (newySpeed > maxySpeed) {
				newySpeed = maxySpeed;
			}
			
			ySpeed = newySpeed;
		}
		else if (grounded) {

			/* Character is on the ground */
			ySpeed = 0;
		}
		
		/* Moves the character and its bounding box */
		setX(x + xSpeed + xFrameOffset);
		setY(y + ySpeed + yFrameOffset);
		boundingBox.translate(xSpeed + xFrameOffset, ySpeed + yFrameOffset);
		
		/* Play music */
		if(!sound.equals("")){
			loader.getSound(sound).play();;
		}
	}
	
	public void moveSword(){
		if(sword != null){
			sword.x = sword.x + xSpeed + xFrameOffset;
			sword.y = sword.y + ySpeed + yFrameOffset;
		}
	}
	
	public void cleanYSpeed(){
		ySpeed = 0;
		yFrameOffset = 0;
	}
	
	public void move(int x, int y) {
		
		// moves the character
		this.x += x;
		this.y += y;
		
		// moves the bounding box
		this.boundingBox.translate(x, y);
	}
	
//	/**
//	 * Checks collision with other characters
//	 * @return true if both rectangle collide,
//	 * false otherwise
//	 */
//	@Override
//	public boolean intersects(Entity entity, long elapsedTime) {
//		boolean intersection = false;
//		
//		/* Creates the bounding box for the next step */
//		Rectangle nextStep = new Rectangle((int) boundingBox.getX(),
//					(int) boundingBox.getY(),
//					(int) boundingBox.getWidth(),
//					(int) boundingBox.getHeight());
//	
//		nextStep.translate(xSpeed, ySpeed);
//		
//		Rectangle r2 = entity.getBoundingBox();
//		
//		/* Checks if collision will take place in the next step */
//		if (nextStep != null && r2 != null) {
//			intersection = nextStep.intersects(r2);
//		}
//		else {
//			intersection = false;
//		}
//		return intersection;
//	}
	
	public void setMoveSpeed(int moveSpeed) {
		
		/* Unlocks player's movement in one direction */
		if (moveSpeed > 0 && leftBlocked) {
			leftBlocked = false;
		} else if (moveSpeed < 0 && rightBlocked) {
			rightBlocked = false;
		}
		
		/* Sets player's horizontal speed */
		this.moveSpeed = moveSpeed;
	}
	
	public void setMoveSpeed(int moveSpeed, String blockedSide) {
		
		/* Sets player's horizontal speed */
		this.moveSpeed = moveSpeed;

		/* Blocks player's movement in one direction */
		this.leftBlocked = blockedSide.equals("left");
		this.rightBlocked = blockedSide.equals("right");
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

	/**
	 * @return the isFalling
	 */
	public boolean isFalling() {
		return falling;
	}

	/**
	 * @param isFalling the isFalling to set
	 */
	public void setFalling(boolean falling) {
		this.falling = falling;
	}

	/**
	 * @return the grounded
	 */
	public boolean isGrounded() {
		return grounded;
	}

	/**
	 * @param grounded the grounded to set
	 */
	public void setGrounded(boolean grounded) {
		this.grounded = grounded;
	}

	/**
	 * @return the freeFall
	 */
	public boolean isFreeFall() {
		return freeFall;
	}

	/**
	 * @param freeFall the freeFall to set
	 */
	public void setFreeFall(boolean freeFall) {
		this.freeFall = freeFall;
	}

	@Override
	public Entity copy() {
		Character newCharacter = new Character(x, y, loader, orientation);
		
		Hashtable<String, Animation> newAnimations = this.getAnimations();
		
		
		return newCharacter;
	}
	
	@Override
	public void drawSelf(Graphics g){
		if(sword!=null){
			sword.drawSelf(g);
		}
		super.drawSelf(g);
		splash.drawSelf(g);
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

	public int getCharCentre(){
		return this.getX() - this.getAnimations().get("sword idle_left").getFrame(0).getImage().getWidth()/2;
	}
	
	public int xDistanceChar(Character e){
		return Math.abs(this.getCharCentre() - e.getCharCentre());
	}
	
	public void manageSword(String animation, int currentFrame, boolean newSword){}
}
