package entities;

import java.awt.Rectangle;
import java.util.Hashtable;

import framework.Loader;
import input.Key;

public class MultiPlayer extends Character {

	protected enum MultiState {COMBAT, DIED, WON};
	
	/* Constants */
	protected final String RUNNING_START = "running start";
	protected final String RUNNING = "running";
	
	protected boolean up_pressed;
	protected boolean right_pressed;
	protected boolean left_pressed;
	protected boolean shift_pressed;
	
	protected MultiState currentState;
	
	protected boolean combatStepRight;
	protected boolean combatStepLeft;
	protected boolean combatAttack;
	protected boolean combatDefense;
	protected boolean combatCanMove;
	protected boolean combatCanAttack;
	protected boolean combatCanDefense;
	protected boolean wantCombat;
	protected boolean beenBlocked;
	protected boolean hasBlocked;
	protected boolean goingToBlock;
	protected boolean goingToAttack;
	protected boolean goingToCounter;
	
	protected int playerNumber;
	protected boolean isPrince;
	
	public MultiPlayer(int x, int y, Loader loader, int hp, String orientation, int playerNumber) {
		super(x, y, loader, orientation);
		
		//PERSONALIZAR DEPENDIENDO DEL PERSONAJE
		animations = loader.getAnimations("Dastan");
		currentAnimation = animations.get("idle_" + this.orientation);
		currentState = MultiState.COMBAT;
		
		this.right_pressed = false;
		this.left_pressed = false;
		this.shift_pressed = false;
		this.up_pressed = false;
		
		this.hp = 3;
		this.maxHp = this.hp;
		
		boundingBox = new Rectangle(x,y,currentAnimation.getImage().getWidth(),
				currentAnimation.getImage().getHeight());
		
		this.combatStepRight = false;
		this.combatStepLeft = false;
		this.combatAttack = false;
		this.combatDefense = false;
		this.combatCanMove = true;
		this.combatCanAttack = true;
		this.combatCanDefense = true;
		this.wantCombat = true;
		this.beenBlocked = false;
		this.hasBlocked = false;
		this.goingToBlock = false;
		this.goingToAttack = false;
		this.goingToCounter = false;
		
		this.playerNumber = playerNumber;
		
	}
	
	@Override
	public void update(long elapsedTime) {
		super.update(elapsedTime);
//		if(currentAnimation.getId().startsWith("block and attack")){
//			System.out.println("Animation time -> " + currentAnimation.getAnimTime());
//		}
		
		if(sword!=null){
			sword.update(elapsedTime);
		}
		manageAnimations();
		updateSpeed();
		if(this.currentState.equals(MultiState.COMBAT)){
			cleanYSpeed();
		}
		this.moveCharacter();
		this.moveSword();
		this.enableBoundingBox();
	}
	
	public void hasBlocked(){
		this.hasBlocked = true;
	}
	
	public void hasBeenBlocked(){
		this.beenBlocked = true;
	}
	
	public boolean isAttacking(){
		return (this.getCurrentAnimation().getId().startsWith("sword attack"));
	}
	
	public boolean isBeingBlocked(){
		return ((this.getCurrentAnimation().getId().startsWith("sword blocked and block") ||
				this.getCurrentAnimation().getId().startsWith("sword attack end blocked")) &&
				this.getCurrentAnimation().getCurrentFrame() == 0);
	}
	
	public boolean isBlocking(){
		
		return this.getCurrentAnimation().getId().startsWith("sword defense");
	}
	
	public boolean isWalking(){
		return this.getCurrentAnimation().getId().startsWith("sword walking");
	}
	
	public boolean checkAttack(){
		boolean checkAttack = false;
		if(this.getCurrentAnimation().getId().startsWith("sword attack start")
				|| this.getCurrentAnimation().getId().startsWith("sword attack up start")){
			checkAttack = true;
		}
		
		return checkAttack;
	}
	
	public boolean isHitting(){
		return this.getCurrentAnimation().getId().startsWith("sword attack end_") &&
				this.getCurrentAnimation().getCurrentFrame() == 1;
	}
	
	public boolean isDead(){
		return this.currentState == MultiState.DIED;
	}
	
	public boolean isInCombat(){
		return this.currentState == MultiState.COMBAT;
	}
	
	public void manageKeyPressed(int key_pressed, Hashtable<String,Integer> keys_mapped){
		if(playerNumber == 1 && !isPrince && key_pressed == keys_mapped.get(Key.DOWN)||
				playerNumber == 1 && isPrince && key_pressed == keys_mapped.get(Key.UP) ||
				playerNumber == 0 && !isPrince && key_pressed == keys_mapped.get(Key.S) ||
				playerNumber == 0  && isPrince && key_pressed == keys_mapped.get(Key.W)){
			if(this.currentState != MultiState.DIED){
				if(!this.combatDefense){
					combatDefense = true;
					combatCanDefense = true;
				}
			}
			
		} else if(key_pressed == keys_mapped.get(Key.RIGHT)||
				key_pressed == keys_mapped.get(Key.D)){
			if(this.currentState != MultiState.DIED){
				if(!this.combatStepRight){
					this.combatStepRight = true;
					this.combatCanMove = true;
				}
			}
			
		} else if(key_pressed == keys_mapped.get(Key.LEFT)||
				key_pressed == keys_mapped.get(Key.A)){
			if(this.currentState != MultiState.DIED){
				if(!this.combatStepLeft){
					this.combatStepLeft = true;
					this.combatCanMove = true;
				}
			}
			
		} else if(key_pressed == keys_mapped.get(Key.C) ||
				key_pressed == keys_mapped.get(Key.M)){
			if(this.currentState != MultiState.DIED){
				
				if(!this.combatAttack){
					this.combatCanAttack = true;
					this.combatAttack = true;
				}
			}
			
		} 
	}
	
	public void manageKeyReleased(int key_released, Hashtable<String,Integer> keys_mapped){
		if(playerNumber == 1 && !isPrince && key_released == keys_mapped.get(Key.DOWN)||
				playerNumber == 1 && isPrince && key_released == keys_mapped.get(Key.UP) ||
				playerNumber == 0 && !isPrince && key_released == keys_mapped.get(Key.S) ||
				playerNumber == 0  && isPrince && key_released == keys_mapped.get(Key.W)){
			up_pressed = false;
			combatDefense = false;
			
		} else if(key_released == keys_mapped.get(Key.RIGHT)||
				key_released == keys_mapped.get(Key.D)){
			right_pressed = false;
			combatStepRight = false;
			
		} else if(key_released == keys_mapped.get(Key.LEFT)||
				key_released == keys_mapped.get(Key.A)){
			left_pressed = false;
			combatStepLeft = false;
			
		} else if(key_released == keys_mapped.get(Key.C)||
				key_released == keys_mapped.get(Key.M)){
			shift_pressed = false;
			combatAttack = false;
		}
	}
	
	public void manageAnimations(){}
	
	@Override
	public void manageSword(String animation, int currentFrame, boolean newSword){}
}
