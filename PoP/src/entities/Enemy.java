package entities;

import java.awt.Rectangle;
import java.util.Random;

import framework.Loader;

public class Enemy extends Character {
	
	private enum EnemyState {IDLE, COMBAT, DIED};
	
	private final int SAFE_DISTANCE = 150;
	private final int NO_COMBAT_ATTACK_DISTANCE = 190;
	private final int ATTACK_DISTANCE = 110;
	private final int AGRESIVE_DISTANCE = 80;
	private final int TURN_DISTANCE = 50;
	private final long MOVE_COOLDOWN = 200;
	private final long ATTACK_COOLDOWN = 400;
	
	private final double BASE_BLOCK_PERCENTAJE = 0.3;
	private final double BASE_COUNTER_PERCENTAJE = 0.2;
	
	
	private EnemyState currentState;
	private int difficulty;
	private boolean playerSaw;
	
	private Player player;
	
	private boolean success;
	private boolean goingToAttack;
	private boolean decidedToBlock;
	private boolean blockDecission;
	
	private boolean canBeHit;
	
	/* Cooldowns */
	private long counterMove;
	private long counterAttack;

	public Enemy(int x, int y, Loader loader, String orientation, String colour, int health, int difficulty) {
		super(x, y - 20, loader, orientation);
		this.hp = health;
		this.maxHp = health;
		
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
		this.decidedToBlock = false;
		this.blockDecission = false;
		
		this.splash = new Splash(0,0,0,0,loader,"guard_" + colour);
		this.sword = new SwordFighting(this.x,this.y,0,0,this.loader,"idle_" + orientation, "guard");
		this.canBeHit = true;
	}
	
	@Override
	public void update(long elapsedTime) {
		super.update(elapsedTime);
		
		if(this.currentState == EnemyState.DIED){
			if(this.currentAnimation.getId().startsWith("dying_")){
				if(this.getCurrentAnimation().getCurrentFrame() == 0){
					this.setSplashVisible(true);
				} else if(this.getCurrentAnimation().getCurrentFrame() == 1){
					this.setSplashVisible(false);
					this.setCanShowSplash(false);
				}
				if(this.getCurrentAnimation().isOver(false)){
					dead();
				}
			} else if(this.currentAnimation.getId().startsWith("died_")){
				
			}
		} else if(playerSaw){
			manageIA(elapsedTime);
			this.moveCharacter();
			if(this.xDistanceChar(player) <= ATTACK_DISTANCE && player.isHitting() && canBeHit){
				setCanShowSplash(true);
				beenHit();
			}
			if(player.isDead()){
				playerSaw = false;
				player = null;
			}
		} else{
			normalIdle();
		}
	}
	
	public void setPlayer(boolean saw, Player p){
		this.playerSaw = saw;
		if(saw){
			this.player = p;
			this.currentState = EnemyState.COMBAT;
			this.setCurrentAnimation("sword idle_" + orientation, FRAME_DURATION);
		} else{
			this.player = null;
			this.currentState = EnemyState.IDLE;
			this.setCurrentAnimation("idle_" + orientation, FRAME_DURATION);
		}
	}
	
	public void manageIA(long elapsedTime){
		if(this.getCurrentAnimation().isOver(false)){
			if(player.getX() < this.getX() && this.getOrientation().equals("right")){
				this.setOrientation("left");
			} else if(player.getX() > this.getX() && this.getOrientation().equals("left")){
				this.setOrientation("right");
			}
		}
		switch(this.getCurrentAnimation().getId()){
			
		case "sword idle_left":
		case "sword idle_right":
			
			idle();
			if(this.xDistanceChar(player)>=SAFE_DISTANCE){
				if(player.isRunning(this.getOrientation())){
					
				} else{
					/* Move towards the player */
					startMove(false);
				}
			} else{
				
				if(!player.isInCombat() || (player.isWalking() && random(0.05))){
					startAttack();
				} else{
					if(this.xDistanceChar(player) >= ATTACK_DISTANCE){
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
						if(this.xDistanceChar(player) < AGRESIVE_DISTANCE){
							
							if(this.xDistanceChar(player) < TURN_DISTANCE){
								if(this.getOrientation().equals("right")){
									this.setOrientation("left");
									this.setX(this.getX() + 40);
									player.setOrientation("right");
									player.setX(player.getX() - 40);
								} else{
									this.setOrientation("right");
									this.setX(this.getX() - 40);
									player.setOrientation("left");
									player.setX(player.getX() + 40);
								}
							} else{
								
								/* Player very close to the enemy -> Attack */
								startAttack();
							}
						} else{
							
							if(decidedToBlock && blockDecission && 
									player.isBeingBlocked()){
								System.out.println("Player being blocked");
								decidedToBlock = false;
								block();
							} 
							
							if(!player.checkAttack() || 
									(player.checkAttack() && decidedToBlock && !blockDecission)){
								/* Player is not attacking */
								/* or Player is attacking and we decided not to block it */
								/* -> We decide if attacking or not*/
								
								
								
								if(counterAttack < ATTACK_COOLDOWN){
									counterAttack = counterAttack + elapsedTime;
								} else{
									counterAttack = restartCounterAttack();
									if(random(0.5)){
										startAttack();
									}
								} 
								
							} else if(player.checkAttack() && !decidedToBlock){
								
								System.out.println("Player atacando");
								
								/* Player is attacking, and we have not decided if we
								 * are going to block it */
								decidedToBlock = true;
								blockDecission = random(BASE_BLOCK_PERCENTAJE + ((double)difficulty)/10);
								if(blockDecission){
									player.hasBeenBlocked();
								}
							} 
						}
					}
				}
			}
			break;
			
		case "walking_left":
		case "walking_right":
			manageSword("walking",this.getCurrentAnimation().getCurrentFrame(),false);
			if(this.getCurrentAnimation().isOver(false)){
				if(goingToAttack){
					goingToAttack = false;
					startAttack();
				} else{
					if(this.xDistanceChar(player)>SAFE_DISTANCE){
						idle();
					} else{
						endMove();
					}
				}
			}
			break;
			
		case "walking end_left":
		case "walking end_right":
			manageSword("walking end",this.getCurrentAnimation().getCurrentFrame(),false);
			if(this.getCurrentAnimation().isOver(false)){
				idle();
			}
			break;
			
		case "attack start_left":
		case "attack start_right":
			manageSword("attack start",this.getCurrentAnimation().getCurrentFrame(),false);
			if(this.isHitting() && player.isBlocking()){
				endAttack(false);
			}
			if(this.getCurrentAnimation().isOver(false)){
				endAttack(!player.isBlocking());
			}
			break;
			
		case "attack end success_left":
		case "attack end success_right":
			manageSword("attack end success",this.getCurrentAnimation().getCurrentFrame(),false);
			if(this.getCurrentAnimation().isOver(false)){
				idle();
			}
			break;
			
		case "attack end blocked_left":
		case "attack end blocked_right":
			manageSword("attack end blocked",this.getCurrentAnimation().getCurrentFrame(),false);
			if(this.getCurrentAnimation().isOver(false)){
				idle();
			}
			break;
			
		case "block and attack_left":
		case "block and attack_right":
			manageSword("block and attack",this.getCurrentAnimation().getCurrentFrame(),false);
			if(this.getCurrentAnimation().isOver(false)){
				endAttack(!player.isBlocking());
			}
			
			break;
			
		case "block only_left":
		case "block only_right":
			manageSword("block only",this.getCurrentAnimation().getCurrentFrame(),false);
			if(this.getCurrentAnimation().isOver(false)){
				idle();
			}
			
			break;
			
		case "blocked_left":
		case "blocked_right":
			manageSword("blocked",this.getCurrentAnimation().getCurrentFrame(),false);
			if(this.getCurrentAnimation().isOver(false)){
				blockedAndBlock();
			}
			break;
			
		case "blocked and prepare block_left":
		case "blocked and prepare block_right":
			manageSword("blocked and prepare block",this.getCurrentAnimation().getCurrentFrame(),false);
			if(this.getCurrentAnimation().isOver(false)){
				block();
			}
			
			break;
			
		case "chopped_left":
		case "chopped_right":
			
			break;
			
		case "died_left":
		case "died_right":
			
			break;
			
		case "dying_left":
		case "dying_right":
			if(this.getCurrentAnimation().isOver(false)){
				//died();
			}
			
			break;
			
		case "hit_left":
		case "hit_right":
			manageSword("hit",this.getCurrentAnimation().getCurrentFrame(),false);
			if(this.getCurrentAnimation().getCurrentFrame() == 0){
				this.setSplashVisible(true);
			} else if(this.getCurrentAnimation().getCurrentFrame() == 1){
				this.setSplashVisible(false);
				this.setCanShowSplash(false);
			}
			if(this.getCurrentAnimation().isOver(false)){
				idle();
				canBeHit = true;
			}
			
			break;
			
		case "spiked_left":
		case "spiked_right":
			
			break;
			
		case "idle_left":
		case "idle_right":
			manageSword("idle", 0, false);
			
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
		manageSword("walking end",0,false);
	}
	
	public void normalIdle(){
		this.setMoveSpeed(0);
		this.setCurrentAnimation("idle_" + orientation, FRAME_DURATION);
		manageSword("idle",0,false);
	}
	
	public void idle(){
		this.setMoveSpeed(0);
		this.setCurrentAnimation("sword idle_" + orientation, FRAME_DURATION);
		manageSword("sword idle", 0, false);
	}
	
	public void startAttack(){
		this.setMoveSpeed(0);
		this.setCurrentAnimation("attack start_" + orientation, FRAME_DURATION);
		manageSword("attack start", 0, false);
		this.decidedToBlock = false;
	}
	
	public void blockedAndBlock(){
		if(player.isAttacking() && random(BASE_BLOCK_PERCENTAJE + ((double)difficulty)/10)){
			player.hasBeenBlocked();
			this.setCurrentAnimation("blocked and prepare block_" + orientation, FRAME_DURATION);
			manageSword("blocked and prepare block", 0, false);
		} else{
			if(!player.isAttacking()){
				if(this.getOrientation().equals("left")){
					this.setMoveSpeed(MOVE_SPEED/2);
				} else{
					this.setMoveSpeed(-MOVE_SPEED/2);
				}
			}
			this.setCurrentAnimation("attack end blocked_" + orientation, FRAME_DURATION);
			manageSword("attack end blocked", 0, false);
		}
	}
	
	public void endAttack(boolean success){
		this.setMoveSpeed(0);
		if(success){
			success = false;
			this.setCurrentAnimation("attack end success_" + orientation, FRAME_DURATION);
			manageSword("attack end success", 0, false);
			if(this.xDistanceChar(player) <= ATTACK_DISTANCE){
				player.beenHit();
			}
		} else{
			this.setCurrentAnimation("blocked_" + orientation, FRAME_DURATION);
			manageSword("blocked", 0, false);
			
		}
	}
	
	public void block(){
		this.setMoveSpeed(0);
		decidedToBlock = false;
		if(random(this.BASE_COUNTER_PERCENTAJE + (this.difficulty - 1) * 0.15)){
			
			/* Block and counter */
			this.setCurrentAnimation("block and attack_" + orientation, FRAME_DURATION);
			manageSword("block and attack", 0, false);
		}
		else{
			
			/* Only block */
			this.setCurrentAnimation("block only_" + orientation, FRAME_DURATION);
			manageSword("block only", 0, false);
		}
	}
	
	public void hasBlocked(){
		if(this.getCurrentAnimation().isOver(false)){
			this.setMoveSpeed(0);
			block();
		}
	}
	
	public void beenHit(){
		canBeHit = false;
		this.hp = hp - 1;
		if(this.hp == 0){
			dying();
		} else{
			if(this.getOrientation().equals("left")){
				this.setMoveSpeed(MOVE_SPEED);
			} else{
				this.setMoveSpeed(-MOVE_SPEED);
			}
			this.setCurrentAnimation("hit_" + this.orientation, FRAME_DURATION);
			manageSword("hit", 0, false);
		}
	}
	
	public void dying(){
		this.setMoveSpeed(0);
		this.currentState = EnemyState.DIED;
		this.setCurrentAnimation("dying_" + orientation, FRAME_DURATION);
		this.sword = null;
	}
	
	public void dead(){
		this.setMoveSpeed(0);
		player.putSwordDown();
		this.setCurrentAnimation("died_" + orientation, FRAME_DURATION);
		this.sword = null;
	}
	
	public boolean random(double probability){
		double p = Math.random();
		return p < probability;
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
	
	public boolean isHitting(){
		return /*(this.getCurrentAnimation().getCurrentFrame() == 2)||*/
				(this.getCurrentAnimation().getCurrentFrame() == 3);
	}
	
	public void manageSword(String animation, int currentFrame, boolean newSword){
		int x_offset = 0;
		int y_offset = -18;
		int[] x_offsets;
		int[] y_offsets;
		
		switch(animation){
		
		case "idle":
			if(this.getOrientation().equals("right")){
				x_offset = 24;
			} else{
				x_offset = -36;
			}
			this.sword.setCurrentAnimation("idle_" + orientation, FRAME_DURATION, 0, this.x + x_offset,this.y + y_offset);
			break;
			
		case "sword idle":
			if(this.getOrientation().equals("right")){
				x_offset = 32;
			} else{
				x_offset = -46;
			}
			y_offset = -38;
			this.sword.setCurrentAnimation("sword idle_" + orientation, FRAME_DURATION, 0, this.x + x_offset,this.y + y_offset);
			break;
			
		case "walking":
			x_offsets = new int[]{32,-46,12,-42,12,-54,18,-60};
			y_offsets = new int[]{-38,-54,-46,-42};
			
			if(this.getOrientation().equals("right")){
				x_offset = x_offsets[2 * currentFrame];
			} else{
				x_offset = x_offsets[2 * currentFrame + 1];
			}
			y_offset = y_offsets[currentFrame];
			
			this.sword.setCurrentAnimation("walking_" + orientation, FRAME_DURATION, currentFrame, this.x + x_offset,this.y + y_offset);
			break;
			
		case "walking end":
			x_offsets = new int[]{32,-46,32,-44};
			y_offsets = new int[]{-38,-38};
			
			if(this.getOrientation().equals("right")){
				x_offset = x_offsets[2 * currentFrame];
			} else{
				x_offset = x_offsets[2 * currentFrame + 1];
			}
			y_offset = y_offsets[currentFrame];
			
			this.sword.setCurrentAnimation("walking end_" + orientation, FRAME_DURATION, currentFrame, this.x + x_offset,this.y + y_offset);
			break;
			
		case "attack start":
			x_offsets = new int[]{44,-56,0,-56,-18,-44,16,-72};
			y_offsets = new int[]{-42,-18,-58,-50};
			
			if(this.getOrientation().equals("right")){
				x_offset = x_offsets[2 * currentFrame];
			} else{
				x_offset = x_offsets[2 * currentFrame + 1];
			}
			y_offset = y_offsets[currentFrame];
			
			this.sword.setCurrentAnimation("attack start_" + orientation, FRAME_DURATION, currentFrame, this.x + x_offset,this.y + y_offset);
			break;
			
		case "attack end success":
			x_offsets = new int[]{34,-90,14,-56,0,-42,36,-50};
			y_offsets = new int[]{-52,-28,-10,-32};
			
			if(this.getOrientation().equals("right")){
				x_offset = x_offsets[2 * currentFrame];
			} else{
				x_offset = x_offsets[2 * currentFrame + 1];
			}
			y_offset = y_offsets[currentFrame];
			
			this.sword.setCurrentAnimation("attack end success_" + orientation, FRAME_DURATION, currentFrame, this.x + x_offset,this.y + y_offset);
			break;
			
		case "attack end blocked":
			x_offsets = new int[]{14,-56,0,-42,36,-50};
			y_offsets = new int[]{-28,-10,-32};
			
			if(this.getOrientation().equals("right")){
				x_offset = x_offsets[2 * currentFrame];
			} else{
				x_offset = x_offsets[2 * currentFrame + 1];
			}
			y_offset = y_offsets[currentFrame];
			
			this.sword.setCurrentAnimation("attack end blocked_" + orientation, FRAME_DURATION, currentFrame, this.x + x_offset,this.y + y_offset);
			break;
			
		case "block and attack":
			x_offsets = new int[]{20,-50,0,0,-18,-44};
			y_offsets = new int[]{-24,0,-58};
			
			if(this.getOrientation().equals("right")){
				x_offset = x_offsets[2 * currentFrame];
			} else{
				x_offset = x_offsets[2 * currentFrame + 1];
			}
			y_offset = y_offsets[currentFrame];
			
			this.sword.setCurrentAnimation("block and attack_" + orientation, FRAME_DURATION, currentFrame, this.x + x_offset,this.y + y_offset);
			break;
			
		case "block only":
			x_offsets = new int[]{20,-50,20,-48,36,-50};
			y_offsets = new int[]{-24,-32,-32};
			
			if(this.getOrientation().equals("right")){
				x_offset = x_offsets[2 * currentFrame];
			} else{
				x_offset = x_offsets[2 * currentFrame + 1];
			}
			y_offset = y_offsets[currentFrame];
			
			this.sword.setCurrentAnimation("block only_" + orientation, FRAME_DURATION, currentFrame, this.x + x_offset,this.y + y_offset);
			break;
			
		case "blocked":
			x_offsets = new int[]{26,-68};
			y_offsets = new int[]{-68};
			
			if(this.getOrientation().equals("right")){
				x_offset = x_offsets[2 * currentFrame];
			} else{
				x_offset = x_offsets[2 * currentFrame + 1];
			}
			y_offset = y_offsets[currentFrame];
			
			this.sword.setCurrentAnimation("blocked_" + orientation, FRAME_DURATION, currentFrame, this.x + x_offset,this.y + y_offset);
			break;
			
		case "blocked and prepare block":
			x_offsets = new int[]{14,-66,20,-48};
			y_offsets = new int[]{-50,-32};
			
			if(this.getOrientation().equals("right")){
				x_offset = x_offsets[2 * currentFrame];
			} else{
				x_offset = x_offsets[2 * currentFrame + 1];
			}
			y_offset = y_offsets[currentFrame];
			
			this.sword.setCurrentAnimation("blocked and prepare block_" + orientation, FRAME_DURATION, currentFrame, this.x + x_offset,this.y + y_offset);
			break;
			
		case "hit":
			x_offsets = new int[]{34,-56,26,-54,0,0,0,-42,36,-50};
			y_offsets = new int[]{-50,-46,0,-10,-32};
			
			if(this.getOrientation().equals("right")){
				x_offset = x_offsets[2 * currentFrame];
			} else{
				x_offset = x_offsets[2 * currentFrame + 1];
			}
			y_offset = y_offsets[currentFrame];
			
			this.sword.setCurrentAnimation("hit_" + orientation, FRAME_DURATION, currentFrame, this.x + x_offset,this.y + y_offset);
			break;
			
		default:
			break;
		
		}
	}

}
