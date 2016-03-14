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
	 * @param jumpSpeed the jumpSpeed to set
	 */
	public void setJumpSpeed(double jumpSpeed) {
		this.jumpSpeed = jumpSpeed;
	}
	
}
