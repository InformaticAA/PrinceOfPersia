package entities;

import java.awt.Graphics;

import framework.Loader;
import game.Game;

public class MPEnemy extends MultiPlayer {
	
	private final int SAFE_DISTANCE = 150;
	private final int NO_COMBAT_ATTACK_DISTANCE = 190;
	private final int ATTACK_DISTANCE = 110;
	private final int AGRESIVE_DISTANCE = 80;
	private final int TURN_DISTANCE = 50;
	private final long MOVE_COOLDOWN = 200;
	private final long ATTACK_COOLDOWN = 400;
	private String colour;
	
	private MPPrince player;
	
	private boolean canBeHit;
	
	public MPEnemy(int x, int y, Loader loader, int hp, String orientation, String colour, int playerNumber, MPPrince player) {
		super(x, y - 20, loader, hp, orientation, playerNumber);
		
		animations = loader.getAnimations("Guard_" + colour);
		currentAnimation = animations.get("sword idle_" + this.orientation);
		currentState = MultiState.COMBAT;
		this.colour = colour;
		
		this.sword = new SwordFighting(this.x,this.y,0,0,this.loader,"sword idle_" + orientation, "guard");
		manageSword("sword idle",0,false);
		this.splash = new Splash(0,0,0,0,loader,"guard_" + colour);
		this.player = player;
		canBeHit = true;
		this.life = new Life[this.maxHp];
		for(int i = 0; i < this.maxHp; i++){
			if(i < this.hp){
				this.life[i] = new Life(20 + i*16, Game.HEIGHT - 5, 0, 0, loader, "guard_" + colour + "_full");
				this.life[i].setVisible(true);
			} else{
				this.life[i] = new Life(20 + i*16, Game.HEIGHT - 5, 0, 0, loader, "guard_" + colour + "_empty");
				this.life[i].setVisible(true);
			}
		}
		this.isPrince = false;
		
	}
	
	@Override
	public void update(long elapsedTime){
		super.update(elapsedTime);
		if(this.xDistanceChar(player) <= ATTACK_DISTANCE && player.isHitting() && canBeHit){
			setCanShowSplash(true);
			beenHit();
		}
		for(int i = 0; i < this.maxHp; i++){
			if(i < this.hp){
				this.life[i] = new Life(Game.WIDTH - (10 + i*16), Game.HEIGHT - 5, 0, 0, loader, "guard_" + colour + "_full");
				this.life[i].setVisible(true);
			} else{
				this.life[i] = new Life(Game.WIDTH - (10 + i*16), Game.HEIGHT - 5, 0, 0, loader, "guard_" + colour + "_empty");
				this.life[i].setVisible(true);
			}
		}
	}
	
	@Override
	public void drawSelf(Graphics g){
		super.drawSelf(g);
		for (int i = 0; i < life.length; i++) {
			life[i].drawSelf(g);
		}	
	}
	
	public void beenHit(){
		this.hp = this.hp - 1;
		if(this.hp == 0){
			this.currentState = MultiState.DIED;
			this.sword = null;
			this.setCurrentAnimation("dying_" + orientation, FRAME_DURATION);
		} else{
			this.setCurrentAnimation("hit_" + orientation, FRAME_DURATION);
		}
		this.canBeHit = false;
		this.beenBlocked = false;
		this.hasBlocked = false;
	}
	
	public boolean isHitting(){
		return (this.getCurrentAnimation().getCurrentFrame() == 3);
	}
	
	@Override
	public void manageAnimations(){
		
		switch(currentAnimation.getId()){
		
		case "attack end blocked_left":
		case "attack end blocked_right":
			switch(currentState){
			
			case COMBAT:
				manageSword("attack end blocked", this.getCurrentAnimation().getCurrentFrame(),false);
				if(this.getCurrentAnimation().isOver(false)){
					this.setCurrentAnimation("sword idle_" + orientation, FRAME_DURATION);
					manageSword("sword idle", 0, false);
				}
				break;
				
			case DIED:
				break;
				
			default:
				break;
			}
			break;
			
		case "attack end success_left":
		case "attack end success_right":
			switch(currentState){
			
			case COMBAT:
				manageSword("attack end success", this.getCurrentAnimation().getCurrentFrame(),false);
				if(this.getCurrentAnimation().isOver(false)){
					this.setCurrentAnimation("sword idle_" + orientation, FRAME_DURATION);
					manageSword("sword idle", 0, false);
				}
				break;
				
			case DIED:
				break;
				
			default:
				break;
			}
			break;
			
		case "attack start_left":
		case "attack start_right":
			switch(currentState){
			
			case COMBAT:
				manageSword("attack start", this.getCurrentAnimation().getCurrentFrame(),false);
				if(this.isHitting() && player.isBlocking() && this.xDistanceChar(player) <= ATTACK_DISTANCE){
					System.out.println("Player ha bloquiao");
					player.hasBlocked();
					this.setCurrentAnimation("blocked_" + orientation, FRAME_DURATION);
					manageSword("blocked",0,false);
				}
				if(this.getCurrentAnimation().isOver(false)){
					if(player.isBlocking() && this.xDistanceChar(player) <= ATTACK_DISTANCE){
						//TODO: test
						System.out.println("Player ha bloquiao");
						player.hasBlocked();
						this.setCurrentAnimation("blocked_" + orientation, FRAME_DURATION);
						manageSword("blocked",0,false);
					}else{
						this.setCurrentAnimation("attack end success_" + orientation, FRAME_DURATION);
						manageSword("attack end success",0,false);
						if(this.xDistanceChar(player) <= ATTACK_DISTANCE){
							player.beenHit();
						}
					}
				}
				break;
				
			case DIED:
				break;
				
			default:
				break;
			}
			break;
			
		case "block and attack_left":
		case "block and attack_right":
			switch(currentState){
			
			case COMBAT:
				manageSword("block and attack",this.getCurrentAnimation().getCurrentFrame(),false);
				if(this.getCurrentAnimation().isOver(false)){
					if(player.isBlocking()){
						this.setCurrentAnimation("blocked_" + orientation, FRAME_DURATION);
						manageSword("blocked", 0, false);
					} else{
						this.setCurrentAnimation("attack end success_" + orientation, FRAME_DURATION);
						manageSword("attack end success", 0, false);
						if(this.xDistanceChar(player) <= ATTACK_DISTANCE){
							player.beenHit();
						}
					}
				}
				break;
				
			case DIED:
				break;
				
			default:
				break;
			}
			break;
			
		case "block only_left":
		case "block only_right":
			switch(currentState){
			
			case COMBAT:
				manageSword("block only",this.getCurrentAnimation().getCurrentFrame(),false);
				if(this.getCurrentAnimation().getCurrentFrame() == 0 && this.combatCanAttack && combatAttack){
					this.goingToAttack = true;
					this.combatCanAttack = false;
				}
				if(this.goingToAttack && this.getCurrentAnimation().getCurrentFrame() == 1){
					this.goingToAttack = false;
					this.setCurrentAnimation("block and attack_" + orientation, FRAME_DURATION);
					this.getCurrentAnimation().setCurrentFrame(1);
					manageSword("block and attack",1,false);
				}
				if(this.getCurrentAnimation().isOver(false)){
					this.setCurrentAnimation("sword idle_" + orientation, FRAME_DURATION);
					manageSword("sword idle", 0, false);
				}
				break;
				
			case DIED:
				break;
				
			default:
				break;
			}
			break;
			
		case "blocked and prepare block_left":
		case "blocked and prepare block_right":
			switch(currentState){
			
			case COMBAT:
				manageSword("blocked and prepare block",this.getCurrentAnimation().getCurrentFrame(),false);
				if(combatCanAttack && combatAttack){
					this.goingToAttack = true;
					this.combatCanAttack = false;
				}
				if(this.getCurrentAnimation().isOver(false)){
					if(this.goingToAttack){
						this.goingToAttack = false;
						this.setCurrentAnimation("block and attack_" + orientation, FRAME_DURATION);
						manageSword("block and attack",0,false);
					} else{
						this.setCurrentAnimation("block only_" + orientation, FRAME_DURATION);
						manageSword("block only",0,false);
					}
				}
				break;
				
			case DIED:
				break;
				
			default:
				break;
			}
			break;
			
		case "blocked_left":
		case "blocked_right":
			switch(currentState){
			
			case COMBAT:
				manageSword("blocked",this.getCurrentAnimation().getCurrentFrame(),false);
				if(this.combatDefense){
					this.goingToBlock = true;
					this.combatCanDefense = false;
				}
				if(this.currentAnimation.isOver(false)){
					if(this.goingToBlock){
						this.goingToBlock = false;
						this.setCurrentAnimation("blocked and prepare block_" + orientation, FRAME_DURATION);
						manageSword("blocked and prepare block",0,false);
						if(player.isAttacking()){
							System.out.println("Hemos bloquiado al player");
							player.hasBeenBlocked();
						}
					} else{
						if(this.getOrientation().equals("left")){
							this.setMoveSpeed(MOVE_SPEED/2);
						} else{
							this.setMoveSpeed(-MOVE_SPEED/2);
						}
						this.setCurrentAnimation("attack end blocked_" + orientation, FRAME_DURATION);
						manageSword("attack end blocked",0,false);
					}
				}
				break;
				
			case DIED:
				break;
				
			default:
				break;
			}
			break;
			
		case "chopped_left":
		case "chopped_right":
			switch(currentState){
			
			case COMBAT:
				break;
				
			case DIED:
				break;
				
			default:
				break;
			}
			break;
			
		case "died_left":
		case "died_right":
			switch(currentState){
			
			case COMBAT:
				break;
				
			case DIED:
				break;
				
			default:
				break;
			}
			break;
			
		case "dying_left":
		case "dying_right":
			switch(currentState){
			
			case COMBAT:
				break;
				
			case DIED:
				if(this.getCurrentAnimation().getCurrentFrame() == 0){
					this.setSplashVisible(true);
				} else if(this.getCurrentAnimation().getCurrentFrame() == 1){
					this.setSplashVisible(false);
					this.setCanShowSplash(false);
				}
				if(this.getCurrentAnimation().isOver(false)){
					this.setCurrentAnimation("died_" + this.orientation, FRAME_DURATION);
				}
				break;
				
			default:
				break;
			}
			break;
			
		case "hit_left":
		case "hit_right":
			switch(currentState){
			
			case COMBAT:
				manageSword("hit", this.getCurrentAnimation().getCurrentFrame(),false);
				if(this.getCurrentAnimation().getCurrentFrame() == 0){
					this.setSplashVisible(true);
				} else if(this.getCurrentAnimation().getCurrentFrame() == 1){
					this.setSplashVisible(false);
					this.setCanShowSplash(false);
				}
				if(this.getCurrentAnimation().isOver(false)){
					this.setCurrentAnimation("sword idle_" + orientation, FRAME_DURATION);
					manageSword("sword idle",0,false);
					canBeHit = true;
				}
				break;
				
			case DIED:
				break;
				
			default:
				break;
			}
			break;
			
		case "idle_left":
		case "idle_right":
			switch(currentState){
			
			case COMBAT:
				break;
				
			case DIED:
				break;
				
			default:
				break;
			}
			break;
			
		case "spiked_left":
		case "spiked_right":
			switch(currentState){
			
			case COMBAT:
				break;
				
			case DIED:
				break;
				
			default:
				break;
			}
			break;
			
		case "sword idle_left":
		case "sword idle_right":
			switch(currentState){
			
			case COMBAT:
				manageSword("sword idle",0,false);
				this.setMoveSpeed(0);
				if(!goingToBlock){
					if(this.combatCanMove && combatStepRight){
						this.combatCanMove = false;
						this.setMoveSpeed(MOVE_SPEED);
						this.setCurrentAnimation("walking_" + this.orientation, FRAME_DURATION);
						manageSword("walking",0,false);
					} else if(this.combatCanMove && combatStepLeft){
						this.combatCanMove = false;
						this.setMoveSpeed(-MOVE_SPEED);
						this.setCurrentAnimation("walking_" + orientation, FRAME_DURATION);
						manageSword("walking",0,false);
					} else if(this.combatCanDefense && this.combatDefense){
						//TODO: MIRAR SI EL PLAYER ESTA ATACANDO PARA ESPERAR
						this.combatCanDefense = false;
						if(player.checkAttack()){
							this.goingToBlock = true;
							player.hasBeenBlocked();
						}else{
							this.setCurrentAnimation("block only_" + orientation, FRAME_DURATION);
							manageSword("block only",0,false);
						}
					} else if(this.combatCanAttack && this.combatAttack){
						this.combatCanAttack = false;
						this.setCurrentAnimation("attack start_" + orientation, FRAME_DURATION);
						manageSword("attack start",0,false);
					}
				} else{
					if(player.isBeingBlocked()){
						this.goingToBlock = false;
						if(this.combatCanAttack && this.combatAttack){
							this.combatCanAttack = false;
							this.setCurrentAnimation("block and attack_" + orientation, FRAME_DURATION);
							manageSword("block and attack",0,false);
						} else{
							this.setCurrentAnimation("block only_" + orientation, FRAME_DURATION);
							manageSword("block only",0,false);
						}
					}
				}
				break;
				
			case DIED:
				break;
				
			default:
				break;
			}
			break;
			
		case "walking end_left":
		case "walking end_right":
			switch(currentState){
			
			case COMBAT:
				manageSword("walking end",this.getCurrentAnimation().getCurrentFrame(),false);
				this.setMoveSpeed(0);
				if(this.combatCanMove && combatStepRight){
					this.combatCanMove = false;
					this.setMoveSpeed(MOVE_SPEED);
					this.setCurrentAnimation("walking_" + this.orientation, FRAME_DURATION);
					manageSword("walking",0,false);
				} else if(this.combatCanMove && combatStepLeft){
					this.combatCanMove = false;
					this.setMoveSpeed(-MOVE_SPEED);
					this.setCurrentAnimation("walking_" + orientation, FRAME_DURATION);
					manageSword("walking",0,false);
				} else if(this.combatCanDefense && this.combatDefense){
					//TODO: MIRAR SI EL PLAYER ESTA ATACANDO PARA ESPERAR
					//TODO: HACER QUE PUEDA BLOQUEAR Y ATACAR
					this.combatCanDefense = false;
					this.setCurrentAnimation("block only_" + orientation, FRAME_DURATION);
					manageSword("block only",0,false);
				} else if(this.combatCanAttack && this.combatAttack){
					this.combatCanAttack = false;
					this.setCurrentAnimation("attack start_" + orientation, FRAME_DURATION);
					manageSword("attack start",0,false);
				}
				
				if(this.getCurrentAnimation().isOver(false)){
					this.setCurrentAnimation("sword idle_" + orientation, FRAME_DURATION);
					this.manageSword("sword idle", 0, false);
				}
				break;
				
			case DIED:
				break;
				
			default:
				break;
			}
			break;
			
		case "walking_left":
		case "walking_right":
			switch(currentState){
			
			case COMBAT:
				manageSword("walking",this.getCurrentAnimation().getCurrentFrame(),false);
				if(this.getCurrentAnimation().isOver(false)){
					this.setMoveSpeed(0);
					this.setCurrentAnimation("walking end_" + orientation, FRAME_DURATION);
					this.manageSword("walking end", 0, false);
				}
				break;
				
			case DIED:
				break;
				
			default:
				break;
			}
			break;
		
		
		default:
			System.out.println("ANIMATION NOT RECOGNIZED - " + this.getCurrentAnimation() );
			break;
		}
	}
	
	@Override
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
