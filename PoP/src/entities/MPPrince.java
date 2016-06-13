package entities;

import java.awt.Graphics;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import entities.MultiPlayer.MultiState;
import framework.Loader;
import game.Game;

public class MPPrince extends MultiPlayer {
	
	public MPPrince(int x, int y, Loader loader, int hp, String orientation, int playerNumber) {
		super(x, y, loader, hp, orientation, playerNumber);
		
		animations = loader.getAnimations("Dastan");
		currentAnimation = animations.get("sword idle_" + this.orientation);
		manageSword("idle",0,true);
		currentState = MultiState.COMBAT;
		this.splash = new Splash(0,0,0,0,loader,"red");
		this.life = new Life[this.maxHp];
		for(int i = 0; i < this.maxHp; i++){
			if(i < this.hp){
				this.life[i] = new Life(20 + i*16, Game.HEIGHT - 5, 0, 0, loader, "prince_full");
				this.life[i].setVisible(true);
			} else{
				this.life[i] = new Life(20 + i*16, Game.HEIGHT - 5, 0, 0, loader, "prince_empty");
				this.life[i].setVisible(true);
			}
		}
		
		this.isPrince = true;
	}
	
	@Override
	public void update(long elapsedTime){
		super.update(elapsedTime);
		for(int i = 0; i < this.maxHp; i++){
			if(i < this.hp){
				this.life[i] = new Life(20 + i*16, Game.HEIGHT - 5, 0, 0, loader, "prince_full");
				this.life[i].setVisible(true);
			} else{
				this.life[i] = new Life(20 + i*16, Game.HEIGHT - 5, 0, 0, loader, "prince_empty");
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
			this.setCurrentAnimation("dieing_" + orientation, FRAME_DURATION);
		} else{
			this.setCurrentAnimation("sword hit_" + orientation, FRAME_DURATION);
		}
		this.beenBlocked = false;
		this.hasBlocked = false;
	}
	
	@Override
	public void manageAnimations(){
		
		switch(currentAnimation.getId()){
		
		case "dieing_left":
		case "dieing_right":

			switch(currentState){
				
			case COMBAT:
				break;
				
			case DIED:
				//TODO: ELIMINAR
				if(this.getCurrentAnimation().getCurrentFrame() == 0){
					setSplashVisible(true);
				} else if(this.getCurrentAnimation().getCurrentFrame() == 1){
					setSplashVisible(false);
					setCanShowSplash(false);
				}
				if(this.currentAnimation.isOver(false)){
					this.setCurrentAnimation("normal dead_" + orientation, FRAME_DURATION);
					//TODO: CHANGE POSITION WHEN DEAD
				}
				break;
				
				
			default:
				
				break;
			}
			break;
			
		case "falling_left":
		case "falling_right":

			switch(currentState){
				
			case COMBAT:

				break;
				
				
			default:
				
				break;
			}
			break;
			
		case "normal dead_left":
		case "normal dead_right":

			switch(currentState){
			case COMBAT:
				
				break;
				
			case DIED:
				
				break;
				
			default:
				
				break;
			}
			break;
			
		case "sword attack start_left":
		case "sword attack start_right":

			switch(currentState){
				
			case COMBAT:
				manageSword("start attacking", this.currentAnimation.getCurrentFrame(), false);
				if(combatCanDefense && combatDefense){
					this.goingToCounter = true;
					this.combatCanDefense = false;
				}
				if(this.currentAnimation.isOver(false)){
					//TODO: ELIMINAR
					if(!beenBlocked){
						this.goingToCounter = false;
						this.setCurrentAnimation("sword attack end_" + orientation, FRAME_DURATION);
						manageSword("end attacking",0,false);
					} else{
						beenBlocked = false;
						if(goingToCounter){
							this.goingToCounter = false;
							this.setCurrentAnimation("sword blocked and block_" + orientation, FRAME_DURATION);
							manageSword("defending after block",0,false);
						} else{
							System.out.println("He sido bloquiado");
							this.setCurrentAnimation("sword attack end blocked_" + orientation, FRAME_DURATION);
							manageSword("end attacking blocked",0,false);
						}
					}
				}
				
			default:
				
				break;
			}
			break;
			
		case "sword attack end blocked_left":
		case "sword attack end blocked_right":

			switch(currentState){
				
			case COMBAT:
				manageSword("end attacking blocked", this.getCurrentAnimation().getCurrentFrame(), false);
				if(this.currentAnimation.isOver(false)){
					//TODO: ELIMINAR
					this.setCurrentAnimation("sword idle_" + orientation, FRAME_DURATION);
					manageSword("idle", 0, false);
				}
				break;
				
			default:
				
				break;
			}
			break;
			
		case "sword attack end_left":
		case "sword attack end_right":

			switch(currentState){
				
			case COMBAT:
				manageSword("end attacking", this.getCurrentAnimation().getCurrentFrame(), false);
				if(this.currentAnimation.isOver(false)){
					//TODO: ELIMINAR
					this.setCurrentAnimation("sword idle_" + orientation, FRAME_DURATION);
					manageSword("idle", 0, false);
				}
				break;
				
			default:
				
				break;
			}
			break;
			
		case "sword attack up start_left":
		case "sword attack up start_right":

			switch(currentState){
				
			case COMBAT:
				manageSword("start attacking up",this.getCurrentAnimation().getCurrentFrame(),false);
				if(this.currentAnimation.isOver(false)){
					//TODO: ELIMINAR
					if(!beenBlocked){
						this.setCurrentAnimation("sword attack end_" + orientation, FRAME_DURATION);
						manageSword("end attacking",0,false);
					} else{
						beenBlocked = false;
						this.setCurrentAnimation("sword blocked_" + this.orientation, FRAME_DURATION);
						manageSword("blocked",0,false);
					}
				}
				break;
				
			default:
				
				break;
			}
			break;
			
		case "sword blocked_left":
		case "sword blocked_right":

			switch(currentState){
				
			case COMBAT:
				manageSword("blocked", this.getCurrentAnimation().getCurrentFrame(), false);
				if(this.combatCanDefense && combatDefense){
					this.goingToBlock = true;
					this.combatCanDefense = false;
				}
				if(this.currentAnimation.isOver(false)){
					//TODO: ELIMINAR
					if(this.goingToBlock){
						this.goingToBlock = false;
						this.setCurrentAnimation("sword blocked and block_" + orientation, FRAME_DURATION);
						manageSword("defending after block",0,false);
					} else{
						this.setCurrentAnimation("sword attack end blocked_" + orientation, FRAME_DURATION);
						manageSword("end attacking blocked",0,false);
					}
				}
				break;
				
			default:
				
				break;
			}
			break;
			
		case "sword blocked and block_left":
		case "sword blocked and block_right":

			switch(currentState){
				
			case COMBAT:
				manageSword("defending after block",this.getCurrentAnimation().getCurrentFrame(),false);
				if(this.currentAnimation.isOver(false)){
					//TODO: ELIMINAR
					this.setCurrentAnimation("sword defense start_" + orientation, FRAME_DURATION);
					manageSword("defending start",0,false);
				}
				break;
				
			default:
				
				break;
			}
			break;
			
		case "sword defense start_left":
		case "sword defense start_right":

			switch(currentState){

			case COMBAT:
				manageSword("defending start",this.getCurrentAnimation().getCurrentFrame(),false);
				if(this.currentAnimation.isOver(false)){
					//TODO: ELIMINAR
					this.setCurrentAnimation("sword defense end_" + orientation, FRAME_DURATION);
					manageSword("defending end",0,false);
				}
				break;
				
			default:
				
				break;
			}
			break;
			
		case "sword defense end_left":
		case "sword defense end_right":

			switch(currentState){
				
			case COMBAT:
				manageSword("defending end",this.getCurrentAnimation().getCurrentFrame(),false);
				if(this.combatAttack){
					this.goingToAttack = true;
					this.combatCanAttack = false;
				}
				if(this.currentAnimation.isOver(false)){
					//TODO: ELIMINAR
					if(this.goingToAttack){
						this.goingToAttack = false;
						this.setCurrentAnimation("sword attack up start_" + orientation, FRAME_DURATION);
						manageSword("start attacking up",0,false);
					} else if(hasBlocked){
						this.hasBlocked = false;
						this.setCurrentAnimation("sword walking backwards_" + orientation, FRAME_DURATION);
						this.manageSword("moving backwards", 0, false);
					} else{
						this.setCurrentAnimation("sword idle_" + orientation, FRAME_DURATION);
						manageSword("idle",0,false);
					}
				}
				break;
				
			default:
				
				break;
			}
			break;
			
		case "sword walking backwards_left":
		case "sword walking backwards_right":

			switch(currentState){
			case COMBAT:
				manageSword("moving backwards",this.getCurrentAnimation().getCurrentFrame(),false);
				if(this.currentAnimation.isOver(false)){
					////TODO: ELIMINAR
					this.setCurrentAnimation("sword idle_" + orientation, FRAME_DURATION);
					manageSword("idle",0,false);
				}
				break;
				
			default:
				
				break;
			}
			break;
		
		case "sword hit_left":
		case "sword hit_right":

			switch(currentState){
			case COMBAT:
				if(this.getOrientation().equals("left")){
					//TODO: ELIMINAR (MOVE_SPEED);
				} else{
					//TODO: ELIMINAR (-MOVE_SPEED);
				}
				manageSword("hit",this.getCurrentAnimation().getCurrentFrame(),false);
				if(this.currentAnimation.isOver(false)){
					//TODO: ELIMINAR
					this.setCurrentAnimation("sword idle_" + orientation, FRAME_DURATION);
					manageSword("idle",0,false);
					setCanShowSplash(true);
				}
				break;
				
			default:
				
				break;
			}
			break;
			
		
			
		case "sword idle_left":
		case "sword idle_right":

			switch(currentState){
			case COMBAT:
				manageSword("idle",0,false);
				//TODO: ELIMINAR
				if(this.combatCanMove && combatStepRight){
					this.combatCanMove = false;
					//TODO: ELIMINAR (MOVE_SPEED);
					if(this.getOrientation().equals("right")){
						this.setCurrentAnimation("sword walking_" + orientation, FRAME_DURATION);
						manageSword("moving forward",0,false);
					} else{
						this.setCurrentAnimation("sword walking backwards_" + orientation, FRAME_DURATION);
						manageSword("moving backwards",0,false);
					}
				} else if(this.combatCanMove && combatStepLeft){
					this.combatCanMove = false;
					//TODO: ELIMINAR (-MOVE_SPEED);
					if(this.getOrientation().equals("left")){
						this.setCurrentAnimation("sword walking_" + orientation, FRAME_DURATION);
						manageSword("moving forward",0,false);
					} else{
						this.setCurrentAnimation("sword walking backwards_" + orientation, FRAME_DURATION);
						manageSword("moving backwards",0,false);
					}
				} else if(this.combatCanDefense && combatDefense){
					this.combatCanDefense = false;
					this.setCurrentAnimation("sword defense start_" + orientation, FRAME_DURATION);
					manageSword("defending start",this.getCurrentAnimation().getCurrentFrame(),false);
				} else if(this.combatCanAttack && this.combatAttack){
					this.combatCanAttack = false;
					this.setCurrentAnimation("sword attack start_" + orientation, FRAME_DURATION);
					manageSword("start attacking", 0, false);
				} 
				
				break;
				
			default:
				
				break;
			}
			break;
			
		case "sword walking_left":
		case "sword walking_right":

			switch(currentState){
			case COMBAT:
				manageSword("moving forward", this.currentAnimation.getCurrentFrame(), false);
				if(this.currentAnimation.isOver(false)){
					//TODO: ELIMINAR
					this.setCurrentAnimation("sword idle_" + orientation, FRAME_DURATION);
					manageSword("idle", 0, false);
				}
				break;
				
			default:
				
				break;
			}
			break;
		
		default:
			System.out.println("ANIMATION NOT RECOGNIZED");
			break;
		}
	}
	
	@Override
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
				this.sword = new SwordFighting(this.x,this.y,x_offset,y_offset,this.loader,"idle_" + orientation, "prince");
			} else{
				this.sword.setCurrentAnimation("idle_" + orientation, FRAME_DURATION, 0, this.x + x_offset,this.y + y_offset);
			}
			break;
			
		case "moving forward":
			x_offsets = new int[]{38,-58,42,-60};
			if(this.getOrientation().equals("right")){
				x_offset = x_offsets[2 * currentFrame];
			} else{
				x_offset = x_offsets[2 * currentFrame + 1];
			}
			this.sword.setCurrentAnimation("idle_" + orientation, FRAME_DURATION, 0, this.x + x_offset,this.y + y_offset);
			break;
			
		case "start attacking":
			x_offsets = new int[]{14,-40,0,0};
			y_offsets = new int[]{-54,0};
			
			if(this.getOrientation().equals("right")){
				x_offset = x_offsets[2 * currentFrame];
			} else{
				x_offset = x_offsets[2 * currentFrame + 1];
			}
			y_offset = y_offsets[currentFrame];
			
			this.sword.setCurrentAnimation("start attacking_" + orientation, FRAME_DURATION, currentFrame, this.x + x_offset,this.y + y_offset);
			break;
			
		case "start attacking up":
			x_offsets = new int[]{2,-30,0,0};
			y_offsets = new int[]{-78,0};
			
			if(this.getOrientation().equals("right")){
				x_offset = x_offsets[2 * currentFrame];
			} else{
				x_offset = x_offsets[2 * currentFrame + 1];
			}
			y_offset = y_offsets[currentFrame];
			
			this.sword.setCurrentAnimation("start attacking up_" + orientation, FRAME_DURATION, currentFrame, this.x + x_offset,this.y + y_offset);
			break;
			
		case "end attacking":
			x_offsets = new int[]{58,-86,56,-98,16,-58,0,-42,32,-44};
			y_offsets = new int[]{-44,-42,-26,-16,-36};
			
			if(this.getOrientation().equals("right")){
				x_offset = x_offsets[2 * currentFrame];
			} else{
				x_offset = x_offsets[2 * currentFrame + 1];
			}
			y_offset = y_offsets[currentFrame];
				
			this.sword.setCurrentAnimation("end attacking_" + orientation, FRAME_DURATION, currentFrame, this.x + x_offset,this.y + y_offset);
			break;
			
		case "end attacking blocked":
			x_offsets = new int[]{16,-58,0,-42,32,-44};
			y_offsets = new int[]{-26,-16,-36};
			
			if(this.getOrientation().equals("right")){
				x_offset = x_offsets[2 * currentFrame];
			} else{
				x_offset = x_offsets[2 * currentFrame + 1];
			}
			y_offset = y_offsets[currentFrame];
				
			this.sword.setCurrentAnimation("end attacking blocked_" + orientation, FRAME_DURATION, currentFrame, this.x + x_offset,this.y + y_offset);
			break;
			
		case "defending start":
			x_offsets = new int[]{28,-56};
			y_offsets = new int[]{-56};
			
			if(this.getOrientation().equals("right")){
				x_offset = x_offsets[2 * currentFrame];
			} else{
				x_offset = x_offsets[2 * currentFrame + 1];
			}
			y_offset = y_offsets[currentFrame];
				
			this.sword.setCurrentAnimation("defending start_" + orientation, FRAME_DURATION, currentFrame, this.x + x_offset,this.y + y_offset);
			break;
			
		case "defending after block":
			x_offsets = new int[]{28,-70,22,-40};
			y_offsets = new int[]{-46,-36};
			
			if(this.getOrientation().equals("right")){
				x_offset = x_offsets[2 * currentFrame];
			} else{
				x_offset = x_offsets[2 * currentFrame + 1];
			}
			y_offset = y_offsets[currentFrame];
				
			this.sword.setCurrentAnimation("defending after block_" + orientation, FRAME_DURATION, currentFrame, this.x + x_offset,this.y + y_offset);
			break;
			
		case "blocked":
			x_offsets = new int[]{42,-84};
			y_offsets = new int[]{-38};
			
			if(this.getOrientation().equals("right")){
				x_offset = x_offsets[2 * currentFrame];
			} else{
				x_offset = x_offsets[2 * currentFrame + 1];
			}
			y_offset = y_offsets[currentFrame];
				
			this.sword.setCurrentAnimation("blocked_" + orientation, FRAME_DURATION, currentFrame, this.x + x_offset,this.y + y_offset);
			break;
			
		case "defending end":
			x_offsets = new int[]{26,-40,26,-40,26,-40};
			y_offsets = new int[]{-70,-70,-70};
			
			if(this.getOrientation().equals("right")){
				x_offset = x_offsets[2 * currentFrame];
			} else{
				x_offset = x_offsets[2 * currentFrame + 1];
			}
			y_offset = y_offsets[currentFrame];
				
			this.sword.setCurrentAnimation("defending end_" + orientation, FRAME_DURATION, currentFrame, this.x + x_offset,this.y + y_offset);
			break;
			
		case "hit":
			x_offsets = new int[]{0,0,0,0,0,-16,0,-42,32,-44};
			y_offsets = new int[]{0,0,-32,-16,-36};
			
			if(this.getOrientation().equals("right")){
				x_offset = x_offsets[2 * currentFrame];
			} else{
				x_offset = x_offsets[2 * currentFrame + 1];
			}
			y_offset = y_offsets[currentFrame];
			
			if(currentFrame == 0){
				setSplashVisible(true);
			} else if(currentFrame == 1){
				setSplashVisible(false);
				setCanShowSplash(false);
			}
				
			this.sword.setCurrentAnimation("hit_" + orientation, FRAME_DURATION, currentFrame, this.x + x_offset,this.y + y_offset);
			break;
			
		case "moving backwards":
			x_offsets = new int[]{22,-40,32,-44};
			y_offsets = new int[]{-36,-36};
			
			if(this.getOrientation().equals("right")){
				x_offset = x_offsets[2 * currentFrame];
			} else{
				x_offset = x_offsets[2 * currentFrame + 1];
			}
			y_offset = y_offsets[currentFrame];
			
			this.sword.setCurrentAnimation("moving backwards_" + orientation, FRAME_DURATION, currentFrame, this.x + x_offset,this.y + y_offset);
			break;
		
		default:
			break;
		
		}
	}
}
