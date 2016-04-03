package entities;

import java.awt.Rectangle;
import java.util.Random;

import framework.Loader;

public class Enemy extends Character {
	
	private enum EnemyState {IDLE, COMBAT, DIED};
	
	private final int SAFE_DISTANCE = 100;
	private final int ATTACK_DISTANCE = 80;
	private final int AGRESIVE_DISTANCE = 60;
	private final long MOVE_COOLDOWN = 200;
	private final long ATTACK_COOLDOWN = 300;
	
	private final double BASE_BLOCK_PERCENTAJE = 0.5;
	private final double BASE_COUNTER_PERCENTAJE = 0.2;
	
	
	private EnemyState currentState;
	private int health;
	private int maxHealth;
	private int difficulty;
	private boolean playerSaw;
	
	private Player player;
	
	private boolean success;
	private boolean goingToAttack;
	private boolean decidedToBlock;
	
	/* Cooldowns */
	private long counterMove;
	private long counterAttack;

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
		this.goingToAttack = true;
		
		this.counterMove = restartCounterMove();
		this.counterAttack = restartCounterAttack();
		this.decidedToBlock = true;
	}
	
	@Override
	public void update(long elapsedTime) {
		super.update(elapsedTime);
		
		if(playerSaw){
			manageIA(elapsedTime);
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
	
	public void manageIA(long elapsedTime){
		if(player.getX() < this.getX() && this.getOrientation().equals("right")){
			this.setOrientation("left");
		} else if(player.getX() > this.getX() && this.getOrientation().equals("left")){
			this.setOrientation("right");
		}
		switch(this.getCurrentAnimation().getId()){
			
		case "idle combat_left":
		case "idle combat_right":
			
			if(this.xDistance(player)>SAFE_DISTANCE){
				
				/* Move towards the player */
				startMove(false);
			} else{
				
				if(this.xDistance(player) > ATTACK_DISTANCE){
					if(!player.isAttacking()){
						
						/* Player not attacking -> we go towards him to attack */
						if(counterMove < MOVE_COOLDOWN){
							counterMove = counterMove + elapsedTime;
						} else{
							counterMove = restartCounterMove();
							if(random(0.5)){
								
								/* 50% probability to move and attack player */
								startMove(true);
							}
						}
					} else{
						counterMove = 0;
					}
				} else{
					if(this.xDistance(player) < AGRESIVE_DISTANCE){
						
						/* Player very close to the enemy -> Attack */
						startAttack();
					} else{
						
						/* Check first if player is attacking */
						if(player.isAttacking()){
							
							/* TODO: gestionar que avise al level state de que quiere bloquear, y que tome esta decision solo una vez
							 * Cambiar la animacion de bloqueo del player tambien */
							
							
							/* Can block, so check if we have to block */
							if(random(BASE_COUNTER_PERCENTAJE + ((double)difficulty)/10) || decidedToBlock){
								if(player.getCurrentAnimation().getCurrentFrame() != 3){
									decidedToBlock = true;
								} else{
									
								}
							}
						}
						if(counterAttack < ATTACK_COOLDOWN){
							counterAttack = counterAttack + elapsedTime;
						} else{
							counterAttack = restartCounterAttack();
							if(random(0.5)){
								startAttack();
							}
						}
						
					}
				}
			}
			break;
			
		case "walking_left":
		case "walking_right":
			if(this.getCurrentAnimation().isOver(false)){
				if(goingToAttack){
					goingToAttack = false;
					startAttack();
				} else{
					if(this.xDistance(player)>SAFE_DISTANCE){
						idle();
					} else{
						endMove();
					}
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
	
	public void startMove(boolean goingToAttack){
		if(this.getOrientation().equals("left")){
			this.setMoveSpeed(-MOVE_SPEED/2);
		} else{
			this.setMoveSpeed(MOVE_SPEED/2);
		}
		
		this.goingToAttack = goingToAttack;
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
		return Math.random() < probability;
	}
	
	public int random(int min, int max){
		Random rn = new Random();
		int range = max - min + 1;
		return rn.nextInt(range) + min;
	}
	
	public int restartCounterAttack(){
		return random(0,(int)ATTACK_COOLDOWN);
	}
	
	public int restartCounterMove(){
		return random(0,(int)(MOVE_COOLDOWN/2));
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
