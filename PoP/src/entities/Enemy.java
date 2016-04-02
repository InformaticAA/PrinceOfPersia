package entities;

import java.awt.Graphics;
import java.awt.Rectangle;

import framework.Loader;

public class Enemy extends Character {
	
	private enum EnemyState {IDLE, COMBAT, DIED};
	
	private EnemyState currentState;
	private int health;
	private int maxHealth;
	private boolean playerSaw;
	
	private Player player;
	
	/* Aux */
	private double counter;

	public Enemy(int x, int y, boolean back, Loader loader, String orientation, String colour, int health) {
		super(x, y, loader, orientation);
		this.health = health;
		this.maxHealth = health;
		
		animations = loader.getAnimations("Guard_" + colour);
		currentAnimation = animations.get("idle_" + this.orientation);
		currentState = EnemyState.IDLE;
		
		boundingBox = new Rectangle(x,y,currentAnimation.getImage().getWidth(),
				currentAnimation.getImage().getHeight());
		
		this.splash = new Splash(0,0,0,0,loader,colour);
		this.playerSaw = false;
	}
	
	@Override
	public void update(long elapsedTime) {
		super.update(elapsedTime);
		
		if(playerSaw){
			manageIA();
		}
	}
	
	public void setPlayer(boolean saw, Player p){
		this.playerSaw = saw;
		if(saw){
			this.player = p;
			this.currentState = EnemyState.COMBAT;
		} else{
			this.player = null;
			this.currentState = EnemyState.IDLE;
		}
	}
	
	public void manageIA(){
		if(player.getX() < this.getX() && this.getOrientation().equals("right")){
			this.setOrientation("left");
		} else if(player.getX() > this.getX() && this.getOrientation().equals("left")){
			this.setOrientation("right");
		}
		switch(this.getCurrentAnimation().getId()){
			
		case "idle_left":
		case "idle_right":
			
			if(this.xDistance(player)>10){
				move();
			} else{
				
			}
			break;
			
		case "walking_left":
		case "walking_right":
			if(this.getCurrentAnimation().isOver(false)){
				idle();
			}
			break;
			
		default:
			
			break;
		}
		
	}
	
	public void move(){
		if(this.getOrientation().equals("left")){
			this.setMoveSpeed(-MOVE_SPEED/2);
		} else{
			this.setMoveSpeed(MOVE_SPEED/2);
		}
		
		this.setCurrentAnimation("walking_" + orientation, FRAME_DURATION);
		manageSword("walking", 0, false);
		
	}
	
	public void idle(){
		this.setMoveSpeed(0);
		this.setCurrentAnimation("idle_" + orientation, FRAME_DURATION);
		manageSword("idle", 0, false);
	}
	
	public void manageSword(String animation, int currentFrame, boolean newSword){
		int x_offset = 0;
		int y_offset = -46;
		int[] x_offsets;
		int[] y_offsets;
		
		switch(animation){
		
		case "idle":
			if(this.getOrientation().equals("right")){
				x_offset = 56;
			} else{
				x_offset = -56;
			}
			if(newSword){
				this.sword = new SwordFighting(this.x,this.y,x_offset,y_offset,this.loader,"idle_" + orientation);
			} else{
				this.sword.setCurrentAnimation("idle_" + orientation, FRAME_DURATION, 0, this.x + x_offset,this.y + y_offset);
			}
			break;
		
		default:
			break;
		
		}
	}

}
