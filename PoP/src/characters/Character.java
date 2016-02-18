package characters;

import java.awt.Rectangle;
import java.util.Set;

import framework.Animation;

public abstract class Character {

	/* Attributes */
	private int hp;
	private int maxHp;
	
//	/* TileMap */
//	private TileMap tileMap;
//	private int tileSize;
//	private double xmap;
//	private double ymap;
	
	/* Position */
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
}
