package entities;

import java.awt.Rectangle;
import java.util.Set;

import framework.Animation;

public class Character extends Entity {

	/* Attributes */
	private int hp;
	private int maxHp;
	
//	/* TileMap */
//	private TileMap tileMap;
//	private int tileSize;
//	private double xmap;
//	private double ymap;
	
	/* Position */
	private int xprev;
	private int yprev;
	private int x;
	private int y;
	
	/* Collision box */
	private int width;
	private int height;
	private int cwidth;
	private int cheight;
	
	/* Collision */
	private int currRow;
	private int currCol;
	private double xdest;
	private double ydest;
	private double xtemp;
	private double ytemp;
	private boolean topLeft;
	private boolean topRight;
	private boolean bottomLeft;
	private boolean bottomRight;
	
	/* States */
	public enum state {
		IDLE, LEFT_RUN, RIGHT_RUN, JUMP, FALLING, FIGHT
	};
	
	/* Movement */
	private double moveSpeed;
	private double maxSpeed;
	private double fightSpeed;
	private double jumpSpeed;
	private double fallSpeed;
	private double maxFallSpeed;
	
	/* Animations */
	private Set<Animation> animations;
	private Animation currentAnimation;
	private boolean facingRight;
	
	public Character(int x, int y) {
		super(x,y, null);
	}
	
	/**
	 * @return the bounding box of the character
	 */
	public Rectangle getRectangle() {
		return new Rectangle(x,y,cwidth,cheight);
	}
	
	/**
	 * Checks collision with other characters
	 * @return true if both rectangle collide,
	 * false otherwise
	 */
	public boolean intersects(Character character) {
		Rectangle r1 = this.getRectangle();
		Rectangle r2 = character.getRectangle();
		return r1.intersects(r2);
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

	public int getXprev() {
		return xprev;
	}

	public void setXprev(int xprev) {
		this.xprev = xprev;
	}

	public int getYprev() {
		return yprev;
	}

	public void setYprev(int yprev) {
		this.yprev = yprev;
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

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getCwidth() {
		return cwidth;
	}

	public void setCwidth(int cwidth) {
		this.cwidth = cwidth;
	}

	public int getCheight() {
		return cheight;
	}

	public void setCheight(int cheight) {
		this.cheight = cheight;
	}

	public int getCurrRow() {
		return currRow;
	}

	public void setCurrRow(int currRow) {
		this.currRow = currRow;
	}

	public int getCurrCol() {
		return currCol;
	}

	public void setCurrCol(int currCol) {
		this.currCol = currCol;
	}

	public double getXdest() {
		return xdest;
	}

	public void setXdest(double xdest) {
		this.xdest = xdest;
	}

	public double getYdest() {
		return ydest;
	}

	public void setYdest(double ydest) {
		this.ydest = ydest;
	}

	public double getXtemp() {
		return xtemp;
	}

	public void setXtemp(double xtemp) {
		this.xtemp = xtemp;
	}

	public double getYtemp() {
		return ytemp;
	}

	public void setYtemp(double ytemp) {
		this.ytemp = ytemp;
	}

	public boolean isTopLeft() {
		return topLeft;
	}

	public void setTopLeft(boolean topLeft) {
		this.topLeft = topLeft;
	}

	public boolean isTopRight() {
		return topRight;
	}

	public void setTopRight(boolean topRight) {
		this.topRight = topRight;
	}

	public boolean isBottomLeft() {
		return bottomLeft;
	}

	public void setBottomLeft(boolean bottomLeft) {
		this.bottomLeft = bottomLeft;
	}

	public boolean isBottomRight() {
		return bottomRight;
	}

	public void setBottomRight(boolean bottomRight) {
		this.bottomRight = bottomRight;
	}

	public double getMoveSpeed() {
		return moveSpeed;
	}

	public void setMoveSpeed(double moveSpeed) {
		this.moveSpeed = moveSpeed;
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
		return jumpSpeed;
	}

	public void setJumpSpeed(double jumpSpeed) {
		this.jumpSpeed = jumpSpeed;
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

	public Animation getCurrentAnimation() {
		return currentAnimation;
	}

	public void setCurrentAnimation(Animation currentAnimation) {
		this.currentAnimation = currentAnimation;
	}

	public boolean isFacingRight() {
		return facingRight;
	}

	public void setFacingRight(boolean facingRight) {
		this.facingRight = facingRight;
	}
}
