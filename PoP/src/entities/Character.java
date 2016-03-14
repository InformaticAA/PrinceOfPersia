package entities;

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
	
	/**
	 * TODO: checks collision with map objects
	 * @return
	 */
	public boolean checkTileMapCollision() {
		return false;
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
	
	public void moveCharacter(){
		setX(x + xSpeed);
		setY(y + ySpeed);
		this.boundingBox.translate(xSpeed, ySpeed);
	}
}
