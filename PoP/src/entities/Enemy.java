package entities;

import java.awt.Rectangle;

import framework.Loader;

public class Enemy extends Character {
	
	private enum EnemyState {IDLE, COMBAT, DIED};
	
	private final int SAFE_DISTANCE = 100;
	private final int ATTACK_DISTANCE = 80;
	
	private EnemyState currentState;
	private int health;
	private int maxHealth;
	private int difficulty;
	private boolean playerSaw;
	
	private Player player;
	
	private boolean success;
	
	/* Aux */
	private double counter;

	public Enemy(int x, int y, Loader loader, String orientation, String colour, int health, int difficulty) {
		super(x, y - 20, loader, orientation);
		this.health = health;
		this.maxHealth = health;
		
		System.out.println("Guard_" + colour);
		animations = loader.getAnimations("Guard_" + colour);
		currentAnimation = animations.get("idle_" + this.orientation);
		currentState = EnemyState.IDLE;
		
		this.difficulty = difficulty;
		
		boundingBox = new Rectangle(x,y,currentAnimation.getImage().getWidth(),
				currentAnimation.getImage().getHeight());
		
		this.splash = new Splash(0,0,0,0,loader,colour);
		this.playerSaw = false;
		
		this.success = true;
	}
	
	@Override
	public void update(long elapsedTime) {
		super.update(elapsedTime);
		
		if(playerSaw){
			manageIA();
			this.moveCharacter();
		}
	}
	
	public void setPlayer(boolean saw, Player p){
		this.playerSaw = saw;
		if(saw){
			this.player = p;
			this.currentState = EnemyState.COMBAT;
			this.setCurrentAnimation("idle combat_" + orientation, FRAME_DURATION);
		} else{
			this.player = null;
			this.currentState = EnemyState.IDLE;
			this.setCurrentAnimation("idle_" + orientation, FRAME_DURATION);
		}
	}
	
	public void manageIA(){
		if(player.getX() < this.getX() && this.getOrientation().equals("right")){
			this.setOrientation("left");
		} else if(player.getX() > this.getX() && this.getOrientation().equals("left")){
			this.setOrientation("right");
		}
		switch(this.getCurrentAnimation().getId()){
			
		case "idle combat_left":
		case "idle combat_right":
			
			if(this.xDistance(player)>SAFE_DISTANCE){
				startMove();
			} else{
				if(this.xDistance(player) > ATTACK_DISTANCE){
					if(!player.isAttacking()){
						startMove();
					}
				} else{
					startAttack();
				}
			}
			break;
			
		case "walking_left":
		case "walking_right":
			if(this.getCurrentAnimation().isOver(false)){
				if(this.xDistance(player)>SAFE_DISTANCE){
					idle();
				} else{
					endMove();
				}
			}
			break;
			
		case "walking end_left":
		case "walking end_right":
			if(this.getCurrentAnimation().isOver(false)){
				idle();
			}
			break;
			
		case "attack start_left":
		case "attack start_right":
			if(this.getCurrentAnimation().isOver(false)){
				endAttack(this.success);
			}
			break;
			
		case "attack end success_left":
		case "attack end success_right":
			if(this.getCurrentAnimation().isOver(false)){
				idle();
			}
			break;
			
		case "attack end blocked_left":
		case "attack end blocked_right":
			if(this.getCurrentAnimation().isOver(false)){
				idle();
			}
			break;
			
		default:
			
			break;
		}
	}
	
	public void startMove(){
		if(this.getOrientation().equals("left")){
			this.setMoveSpeed(-MOVE_SPEED/2);
		} else{
			this.setMoveSpeed(MOVE_SPEED/2);
		}
		
		this.setCurrentAnimation("walking_" + orientation, FRAME_DURATION);
		manageSword("walking", 0, false);
	}
	
	public void endMove(){
		this.setMoveSpeed(0);
		this.setCurrentAnimation("walking end_" + orientation, FRAME_DURATION);
		manageSword("end walking",0,false);
	}
	
	public void idle(){
		this.setMoveSpeed(0);
		this.setCurrentAnimation("idle combat_" + orientation, FRAME_DURATION);
		manageSword("idle combat", 0, false);
	}
	
	public void startAttack(){
		this.setMoveSpeed(0);
		this.setCurrentAnimation("attack start_" + orientation, FRAME_DURATION);
		manageSword("attack start", 0, false);
	}
	
	public void endAttack(boolean success){
		if(success){
			this.setMoveSpeed(0);
			success = false;
			this.setCurrentAnimation("attack end success_" + orientation, FRAME_DURATION);
			manageSword("attack end success", 0, false);
		} else{
			if(this.getOrientation().equals("left")){
				this.setMoveSpeed(MOVE_SPEED/2);
			} else{
				this.setMoveSpeed(-MOVE_SPEED/2);
			}
			this.setCurrentAnimation("attack end blocked_" + orientation, FRAME_DURATION);
			manageSword("attack end success", 0, false);
		}
	}
	
	public boolean random(double probability){
		return Math.random() > probability;
	}
	
	public void manageSword(String animation, int currentFrame, boolean newSword){
//		int x_offset = 0;
//		int y_offset = -46;
//		int[] x_offsets;
//		int[] y_offsets;
//		
//		switch(animation){
//		
//		case "idle":
//			if(this.getOrientation().equals("right")){
//				x_offset = 56;
//			} else{
//				x_offset = -56;
//			}
//			if(newSword){
//				this.sword = new SwordFighting(this.x,this.y,x_offset,y_offset,this.loader,"idle_" + orientation);
//			} else{
//				this.sword.setCurrentAnimation("idle_" + orientation, FRAME_DURATION, 0, this.x + x_offset,this.y + y_offset);
//			}
//			break;
//		
//		default:
//			break;
//		
//		}
	}

}
