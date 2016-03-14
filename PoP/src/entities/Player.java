package entities;

import java.awt.Rectangle;
import java.util.Hashtable;

import framework.Loader;
import input.Key;

public class Player extends Character {

	private enum PlayerState {IDLE, MOVE, JUMP, COMBAT};
	
	/* Constants */
	private final String RUNNING_START = "running start";
	private final String RUNNING = "running";
	private final int FRAME_DURATION = 6;
	private final int MOVE_SPEED = 2;
	
	private boolean up_pressed;
	private boolean down_pressed;
	private boolean right_pressed;
	private boolean left_pressed;
	private boolean shift_pressed;
	
	private PlayerState currentState;
	
	private boolean changed_position;
	private String newOrientation;
	
	private boolean canMakeStep;
	private boolean canWalkCrouched;
	
	public Player(int x, int y, Loader loader, String orientation) {
		super(x, y, loader, orientation);
		animations = loader.getAnimations("Dastan");
		
		currentAnimation = animations.get("idle_" + this.orientation);
		currentState = PlayerState.IDLE;
		
		boundingBox = new Rectangle(x,y,currentAnimation.getImage().getWidth(),
				currentAnimation.getImage().getHeight());
		
		this.changed_position = false;
		this.newOrientation = orientation;
		
		this.right_pressed = false;
		this.left_pressed = false;
		this.shift_pressed = false;
		this.up_pressed = false;
		this.down_pressed = false;
		
		this.canMakeStep = true;
		this.canWalkCrouched = true;
	}
	
	@Override
	public void update(long elapsedTime) {
		super.update(elapsedTime);
		
		if(!right_pressed && !left_pressed){
			currentState = PlayerState.IDLE;
		}
		
		switch (currentState) {
		case IDLE:
			
			switch(currentAnimation.getId()){
			
			case "turning_left":
			case "turning_right":
				if(currentAnimation.isOver(false)){
					this.setMoveSpeed(0);
					this.setCurrentAnimation("idle_" + orientation, FRAME_DURATION);
				}
				break;
				
			case "running start_left":
			case "running start_right":
				if(currentAnimation.isOver(false)){
					if(this.getOrientation().equals("left")){
						this.setMoveSpeed(-MOVE_SPEED);
					} else{
						this.setMoveSpeed(MOVE_SPEED);
					}
					this.setCurrentAnimation("running stop start_" + orientation, FRAME_DURATION);
				}
				break;
				
			case "running_left":
			case "running_right":	
				if(this.getOrientation().equals("left")){
					this.setMoveSpeed(-MOVE_SPEED);
				} else{
					this.setMoveSpeed(MOVE_SPEED);
				}
				this.setCurrentAnimation("running stop start_" + orientation, FRAME_DURATION);
				break;
				
			case "turn running_left":
			case "turn running_right":
			case "turn running started_left":
			case "turn running started_right":
				if(currentAnimation.isOver(false)){
					if(this.getOrientation().equals("left")){
						this.setMoveSpeed(-MOVE_SPEED);
					} else{
						this.setMoveSpeed(MOVE_SPEED);
					}
					this.setCurrentAnimation("running stop start_" + orientation, FRAME_DURATION);
				}
				break;
				
			case "running stop start_left":
			case "running stop start_right":
				if(currentAnimation.isOver(false)){
					if(this.getOrientation().equals("left")){
						this.setMoveSpeed(-MOVE_SPEED);
					} else{
						this.setMoveSpeed(MOVE_SPEED);
					}
					this.setCurrentAnimation("running stop_" + orientation, FRAME_DURATION);
					this.setMoveSpeed(0);
				}
				break;
				
			case "running stop_left":
			case "running stop_right":
				if(currentAnimation.isOver(false)){
					this.setMoveSpeed(0);
					this.setCurrentAnimation("idle_" + orientation, FRAME_DURATION);
				}
				break;
				
			case "idle_left":
			case "idle_right":
				if(changed_position){
					changed_position = false;
				} else if(down_pressed){
					this.setCurrentAnimation("crouching down_" + orientation, FRAME_DURATION);
				}
				this.setMoveSpeed(0);
				
				break;
			
			case "walking a step_right":
			case "walking a step_left":
				if(currentAnimation.isOver(false)){
					this.setMoveSpeed(0);
					this.setCurrentAnimation("idle_" + orientation, FRAME_DURATION);
				}
				break;
				
			case "crouching down_left":
			case "crouching down_right":
				if(currentAnimation.isOver(false)){
					this.setMoveSpeed(0);
					this.setCurrentAnimation("crouching idle_" + orientation, FRAME_DURATION);
				}
				break;
				
			case "crouching idle_left":
			case "crouching idle_right":
				if(!down_pressed){
					this.setCurrentAnimation("crouching up_" + orientation, FRAME_DURATION);
				} 
				this.setMoveSpeed(0);
				break;
				
			case "crouching up_left":
			case "crouching up_right":
				this.setMoveSpeed(0);
				if(currentAnimation.isOver(false)){
					canWalkCrouched = true;
					this.setCurrentAnimation("idle_" + orientation, FRAME_DURATION);
				}
				break;
				
			case "crouching walk_left":
			case "crouching walk_right":
				if(currentAnimation.isOver(false)){
					if(!down_pressed){
						this.setMoveSpeed(0);
						this.setCurrentAnimation("crouching up_" + orientation, FRAME_DURATION);
					} else{
						this.setMoveSpeed(0);
						this.setCurrentAnimation("crouching idle_" + orientation, FRAME_DURATION);
					}
				}
				break;
				
			default:
				System.out.println("Unexpected animation");
				break;
			}
			this.moveCharacter();
			break;
			
		case MOVE:
			
			switch (currentAnimation.getId()){
			case "idle_left":
			case "idle_right":
				if(changed_position){
					this.setMoveSpeed(0);
					changed_position = false;
					this.setOrientation(newOrientation);
					this.setCurrentAnimation("turning_" + orientation, FRAME_DURATION);
				} else if(shift_pressed){
					if(canMakeStep){
						if(this.getOrientation().equals("left")){
							this.setMoveSpeed(-MOVE_SPEED/2);
						} else{
							this.setMoveSpeed(MOVE_SPEED/2);
						}
						this.setCurrentAnimation("walking a step_" + orientation, FRAME_DURATION);
						canMakeStep = false;
					}
				} else if(down_pressed){
					if(this.getOrientation().equals("left")){
						this.setMoveSpeed(-MOVE_SPEED);
					} else{
						this.setMoveSpeed(MOVE_SPEED);
					}
					this.setCurrentAnimation("crouching down_" + orientation, FRAME_DURATION);
				}
				else{
					if(this.getOrientation().equals("left")){
						this.setMoveSpeed(-MOVE_SPEED);
					} else{
						this.setMoveSpeed(MOVE_SPEED);
					}
//					System.out.printf("starts running: ");
					this.setCurrentAnimation("running start_" + orientation, FRAME_DURATION);
				}
				break;
				
			case "turning_left":
			case "turning_right":
				if(currentAnimation.isOver(false)){
					if(changed_position){
						this.setMoveSpeed(0);
						changed_position = false;
						this.setOrientation(newOrientation);
						this.setCurrentAnimation("turning_" + orientation, FRAME_DURATION);
					} else if(shift_pressed){
						if(canMakeStep){
							if(this.getOrientation().equals("left")){
								this.setMoveSpeed(-MOVE_SPEED/2);
							} else{
								this.setMoveSpeed(MOVE_SPEED/2);
							}
							this.setCurrentAnimation("walking a step_" + orientation, FRAME_DURATION);
							canMakeStep = false;
						}
					} else{
						if(this.getOrientation().equals("left")){
							this.setMoveSpeed(-MOVE_SPEED);
						} else{
							this.setMoveSpeed(MOVE_SPEED);
						}
						this.setCurrentAnimation("running start_" + orientation, FRAME_DURATION);
					}
				}
				break;
				
			case "running start_left":
			case "running start_right":
				if(this.getOrientation().equals("left")){
					this.setMoveSpeed(-MOVE_SPEED);
				} else{
					this.setMoveSpeed(MOVE_SPEED);
				}
//				System.out.printf(currentAnimation.getCurrentFrame() + ", ");
				if(currentAnimation.isOver(false)){
					if(changed_position){
						changed_position = false;
						this.setOrientation(newOrientation);
						this.setCurrentAnimation("turn running_" + orientation, FRAME_DURATION);
					} else{
//						System.out.println();
						this.setCurrentAnimation("running_" + orientation, FRAME_DURATION);
					}
				} 
				break;
				
			case "running_left":
			case "running_right":
				if(this.getOrientation().equals("left")){
					this.setMoveSpeed(-MOVE_SPEED);
				} else{
					this.setMoveSpeed(MOVE_SPEED);
				}
				if(changed_position){
					changed_position = false;
					this.setOrientation(newOrientation);
					this.setCurrentAnimation("turn running_" + orientation, FRAME_DURATION);
				} else if(down_pressed){
					this.setCurrentAnimation("crouching down_" + orientation, FRAME_DURATION);
				}
				
				System.out.println("running");
				break;
				
			case "running stop start_left":
			case "running stop start_right":
				if(this.getOrientation().equals("left")){
					this.setMoveSpeed(-MOVE_SPEED);
				} else{
					this.setMoveSpeed(MOVE_SPEED);
				}
				if(currentAnimation.isOver(false)){
					if(changed_position){
						changed_position = false;
						this.setOrientation(newOrientation);
						this.setCurrentAnimation("turn running started_" + orientation, FRAME_DURATION);
					} else{
						this.currentState = PlayerState.IDLE;
						this.setCurrentAnimation("running stop_" + orientation, FRAME_DURATION);
					}
				}
				break;
				
			case "running stop_left":
			case "running stop_right":
				if(currentAnimation.isOver(false)){
					if(changed_position){
						changed_position = false;
						this.setOrientation(newOrientation);
						this.setCurrentAnimation("turning_" + orientation, FRAME_DURATION);
						
					} else{
						if(this.getOrientation().equals("left")){
							this.setMoveSpeed(-MOVE_SPEED);
						} else{
							this.setMoveSpeed(MOVE_SPEED);
						}
						this.setCurrentAnimation("running start_" + orientation, FRAME_DURATION);
					}
				}
				break;
				
			case "turn running_left":
			case "turn running_right":
			case "turn running started_left":
			case "turn running started_right":
				if(currentAnimation.isOver(false)){
					if(this.getOrientation().equals("left")){
						this.setMoveSpeed(-MOVE_SPEED);
					} else{
						this.setMoveSpeed(MOVE_SPEED);
					}
					this.setCurrentAnimation("running_" + orientation, FRAME_DURATION);
					
				}
				break;
				
			case "walking a step_right":
			case "walking a step_left":
				if(currentAnimation.isOver(false)){
					this.setMoveSpeed(0);
					this.setCurrentAnimation("idle_" + orientation, FRAME_DURATION);
				}
				break;
				
			case "crouching down_left":
			case "crouching down_right":
				if(currentAnimation.isOver(false)){
					if(changed_position){
						changed_position = false;
						this.currentState = PlayerState.IDLE;
						this.setCurrentAnimation("crouching idle_" + orientation, FRAME_DURATION);
					}
					if(canWalkCrouched){
						if(this.getOrientation().equals("left")){
							this.setMoveSpeed(-MOVE_SPEED/2);
						} else{
							this.setMoveSpeed(MOVE_SPEED/2);
						}
						canWalkCrouched = false;
						this.setCurrentAnimation("crouching walk_" + orientation, FRAME_DURATION);
					} else{
						this.setMoveSpeed(0);
						this.setCurrentAnimation("crouching idle_" + orientation, FRAME_DURATION);
					}
				}
				break;
				
			case "crouching walk_left":
			case "crouching walk_right":
				if(currentAnimation.isOver(false)){
					this.setMoveSpeed(0);
					this.setCurrentAnimation("crouching idle_" + orientation, FRAME_DURATION);
				}
				break;
				
			case "crouching idle_left":
			case "crouching idle_right":
				if(canWalkCrouched){
					canWalkCrouched = false;
					if(changed_position){
						changed_position = false;
					} else{
						if(this.getOrientation().equals("left")){
							this.setMoveSpeed(-MOVE_SPEED);
						} else{
							this.setMoveSpeed(MOVE_SPEED);
						}
						this.setCurrentAnimation("crouching walk_" + orientation, FRAME_DURATION);
					}
				}
				if(!down_pressed){
					this.setCurrentAnimation("crouching up_" + orientation, FRAME_DURATION);
				}
				break;
				
			case "crouching up_left":
			case "crouching up_right":
				if(currentAnimation.isOver(false)){
					canWalkCrouched = true;
					this.setCurrentAnimation("idle_" + orientation, FRAME_DURATION);
				}
				break;
				
			default:
				System.out.println("Unexpected animation");
			}
			this.moveCharacter();
		default:
			
			break;
		}
	}
	
	public void manageKeyPressed(int key_pressed, Hashtable<String,Integer> keys_mapped){
		if(key_pressed == keys_mapped.get(Key.UP)){
			
		} else if(key_pressed == keys_mapped.get(Key.RIGHT)){
			if(!right_pressed){
				canMakeStep = true;
				canWalkCrouched = true;
			}
			currentState = PlayerState.MOVE;
			if(this.getOrientation().equals("left")){
				this.changed_position = true;
				this.newOrientation = "right";
			}
			right_pressed = true;
			
		} else if(key_pressed == keys_mapped.get(Key.LEFT)){
			if(!left_pressed){
				canMakeStep = true;
				canWalkCrouched = true;
			}
			currentState = PlayerState.MOVE;
			if(this.getOrientation().equals("right")){
				this.changed_position = true;
				this.newOrientation = "left";
			}
			left_pressed = true;
			
		} else if(key_pressed == keys_mapped.get(Key.DOWN)){
			down_pressed = true;
			
		} else if(key_pressed == keys_mapped.get(Key.SHIFT)){
			shift_pressed = true;
		} else if(key_pressed == keys_mapped.get(Key.D)){
			System.out.println("State: " + this.currentState + "\n" + 
					"Animation: " + this.getCurrentAnimation().getId() + "\n" + 
					"Orientation: " + this.getOrientation());
		}
	}
	
	public void manageKeyReleased(int key_released, Hashtable<String,Integer> keys_mapped){
		if(key_released == keys_mapped.get(Key.UP)){
			
		} else if(key_released == keys_mapped.get(Key.RIGHT)){
			if(/*currentState != PlayerState.IDLE && */this.getOrientation().equals("right")){
				this.currentState = PlayerState.IDLE;
			}
			right_pressed = false;
			
		} else if(key_released == keys_mapped.get(Key.LEFT)){
			if(/*currentState != PlayerState.IDLE && */this.getOrientation().equals("left")){
				this.currentState = PlayerState.IDLE;
			}
			left_pressed = false;
			
		} else if(key_released == keys_mapped.get(Key.DOWN)){
			down_pressed = false;
			
		} else if(key_released == keys_mapped.get(Key.SHIFT)){
			shift_pressed = false;
		}
	}

}
