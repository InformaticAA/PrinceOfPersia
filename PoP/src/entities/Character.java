package entities;

import java.awt.Rectangle;

import framework.Loader;

public class Character extends Entity {

	/* Attributes */
	protected int hp;
	protected int maxHp;
	
	/* Movement */
	protected int xSpeed;
	protected int ySpeed;
	protected double maxSpeed;
	protected double fightSpeed;
	protected double jumpSpeed;
	protected double fallSpeed;
	protected double maxFallSpeed;
	protected boolean leftBlocked;
	protected boolean rightBlocked;
	
	/* Animations */
	protected String orientation;
	
	public Character(int x, int y, Loader loader, String orientation) {
		super("Character", x,y,loader);
		this.orientation = orientation;
	}
	
	@Override
	public void update(long elapsedTime) {
		super.update(elapsedTime);
		
		/* Updates the position of the bounding box */
		enableBoundingBox();
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
	public boolean intersects(Entity entity) {
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
	
	public int getHp() {
		return hp;
	}

	public void setHp(int hp) {
		this.hp = hp;
	}

	public int getMaxHp() {
		return maxHp;
	}

	public void setMaxHp(int maxHp) {
		this.maxHp = maxHp;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getMoveSpeed() {
		return xSpeed;
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
		
		/* Unlocks player's movement in one direction */
//		if (moveSpeed > 0 && leftBlocked) {
//			leftBlocked = false;
//		} else if (moveSpeed < 0 && rightBlocked) {
//			rightBlocked = false;
//		}
		
		/* Sets player's horizontal speed */
		this.xSpeed = moveSpeed;

		/* Blocks player's movement in one direction */
		this.leftBlocked = blockedSide.equals("left");
		this.rightBlocked = blockedSide.equals("right");
		this.xSpeed = moveSpeed;
	}

	public double getMaxSpeed() {
		return maxSpeed;
	}

	public void setMaxSpeed(double maxSpeed) {
		this.maxSpeed = maxSpeed;
	}

	public double getFightSpeed() {
		return fightSpeed;
	}

	public void setFightSpeed(double fightSpeed) {
		this.fightSpeed = fightSpeed;
	}

	public double getJumpSpeed() {
		return ySpeed;
	}

	public void setJumpSpeed(int ySpeed) {
		this.ySpeed = ySpeed;
	}

	public double getFallSpeed() {
		return fallSpeed;
	}

	public void setFallSpeed(double fallSpeed) {
		this.fallSpeed = fallSpeed;
	}

	public double getMaxFallSpeed() {
		return maxFallSpeed;
	}

	public void setMaxFallSpeed(double maxFallSpeed) {
		this.maxFallSpeed = maxFallSpeed;
	}

	public String getOrientation() {
		return orientation;
	}

	public void setOrientation(String facingRight) {
		this.orientation = facingRight;
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
	public void setxSpeed(int xSpeed, String blockedSide) {
		
		/* Unlocks player's movement in one direction */
		if (xSpeed > 0 && leftBlocked) {
			leftBlocked = false;
		} else if (xSpeed < 0 && rightBlocked) {
			rightBlocked = false;
		}
		
		/* Sets player's horizontal speed */
		this.xSpeed = xSpeed;

		/* Blocks player's movement in one direction */
		this.leftBlocked = blockedSide.equals("left");
		this.rightBlocked = blockedSide.equals("right");
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
	 * @param jumpSpeed the jumpSpeed to set
	 */
	public void setJumpSpeed(double jumpSpeed) {
		this.jumpSpeed = jumpSpeed;
	}
	
}
