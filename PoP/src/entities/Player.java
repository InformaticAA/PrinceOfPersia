package entities;

import java.awt.Rectangle;
import java.util.Hashtable;

import framework.Loader;
import input.Key;

public class Player extends Character {

	public enum PlayerState {IDLE, MOVE, JUMP, COMBAT, COLLIDED, DIED};
	
	/* Constants */
	private final String RUNNING_START = "running start";
	private final String RUNNING = "running";
	private final int FRAME_DURATION = 5;
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
	private boolean canClimb;
	
	private int fallDistance;
	
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
		
		this.canClimb = false;
		
		this.fallDistance = 0;
	}
	
	@Override
	public void update(long elapsedTime) {
		super.update(elapsedTime);
		
		if(!right_pressed && !left_pressed && !up_pressed && currentState != PlayerState.COLLIDED){
			currentState = PlayerState.IDLE;
		} 

		if(this.currentState != PlayerState.COLLIDED){
			if(right_pressed || left_pressed){
				if(right_pressed && !left_pressed){
					if(this.getOrientation().equals("left") && !changed_position){
						this.currentState = PlayerState.IDLE;
					} else{
						this.currentState = PlayerState.MOVE;
					}
				} else if(!right_pressed && left_pressed && !changed_position){
					if(this.getOrientation().equals("right")){
						this.currentState = PlayerState.IDLE;
					} else{
						this.currentState = PlayerState.MOVE;
					}
				} 
			}
		}
		
		if(up_pressed && currentState != PlayerState.COLLIDED){
			this.currentState = PlayerState.JUMP;
		}
		
		manageAnimations();
		updateSpeed();
		this.moveCharacter();
		this.enableBoundingBox();
	}
	
	public void setCollided(){
		if(this.currentState != PlayerState.COMBAT){
			this.currentState = PlayerState.COLLIDED;
		}
	}
	
	public void manageKeyPressed(int key_pressed, Hashtable<String,Integer> keys_mapped){
		if(key_pressed == keys_mapped.get(Key.UP)){
			up_pressed = true;
			currentState = PlayerState.JUMP;
			
		} else if(key_pressed == keys_mapped.get(Key.RIGHT)){
			if(!right_pressed){
				canMakeStep = true;
				canWalkCrouched = true;
			}
			if(this.currentState != PlayerState.JUMP){
				currentState = PlayerState.MOVE;
				if(this.getOrientation().equals("left")){
					this.changed_position = true;
					this.newOrientation = "right";
				}
			}
			right_pressed = true;
			
		} else if(key_pressed == keys_mapped.get(Key.LEFT)){
			if(!left_pressed){
				canMakeStep = true;
				canWalkCrouched = true;
			}
			if(this.currentState != PlayerState.JUMP){
				currentState = PlayerState.MOVE;
				if(this.getOrientation().equals("right")){
					this.changed_position = true;
					this.newOrientation = "left";
				}
			}
			left_pressed = true;
			
		} else if(key_pressed == keys_mapped.get(Key.DOWN)){
			down_pressed = true;
			
		} else if(key_pressed == keys_mapped.get(Key.SHIFT)){
			shift_pressed = true;
		} else if(key_pressed == keys_mapped.get(Key.D)){
			System.out.println("State: " + this.currentState + "\n" + 
					"Animation: " + this.getCurrentAnimation().getId() + "\n" + 
					"Orientation: " + this.getOrientation() + "\n" +
					"left_pressed: " + left_pressed + "\n" + 
					"right_pressed: " + right_pressed + "\n" + 
					"up_pressed: " + up_pressed + "\n" + 
					"down_pressed: " + down_pressed + "\n");
		}
	}
	
	public void manageKeyReleased(int key_released, Hashtable<String,Integer> keys_mapped){
		if(key_released == keys_mapped.get(Key.UP)){
			this.currentState = PlayerState.IDLE;
			up_pressed = false;
			
		} else if(key_released == keys_mapped.get(Key.RIGHT)){
			if(currentState != PlayerState.JUMP && this.getOrientation().equals("right")){
				this.currentState = PlayerState.IDLE;
			}
			right_pressed = false;
			
		} else if(key_released == keys_mapped.get(Key.LEFT)){
			if(currentState != PlayerState.JUMP && this.getOrientation().equals("left")){
				this.currentState = PlayerState.IDLE;
			}
			left_pressed = false;
			
		} else if(key_released == keys_mapped.get(Key.DOWN)){
			down_pressed = false;
			
		} else if(key_released == keys_mapped.get(Key.SHIFT)){
			shift_pressed = false;
		}
	}
	
	private void manageAnimations(){
		
		switch(currentAnimation.getId()){
		
		case "clipping_left":
		case "clipping_right":
			
			switch(currentState){
			case IDLE:
				if(currentAnimation.isOver(false)){
					this.setCurrentAnimation("idle_" + orientation, FRAME_DURATION);
					canClimb = false;
				}
				break;
				
			case JUMP:
				if(currentAnimation.isOver(false)){
					this.setCurrentAnimation("idle_" + orientation, FRAME_DURATION);
					canClimb = false;
				}
				break;
				
			case MOVE:
				if(currentAnimation.isOver(false)){
					this.setCurrentAnimation("idle_" + orientation, FRAME_DURATION);
					canClimb = false;
				}
				break;
				
			case COLLIDED:
				System.out.println("COLLIDED EN POS TO RARA");
				break;
				
			default:
				
				break;
			}
			break;
			
		case "crouching down_left":
		case "crouching down_right":
			
			switch(currentState){
			case IDLE:
				if(currentAnimation.isOver(false)){
					this.setCurrentAnimation("crouching idle_" + orientation, FRAME_DURATION);
				}
				break;
				
			case JUMP:
				if(currentAnimation.isOver(false)){
					this.setCurrentAnimation("crouching idle_" + orientation, FRAME_DURATION);
				}
				break;
				
			case MOVE:
				if(this.getOrientation().equals("left")){

				} else{

				}
				if(currentAnimation.isOver(false)){
					if(changed_position){
						changed_position = false;
						this.currentState = PlayerState.IDLE;
						this.setCurrentAnimation("crouching idle_" + orientation, FRAME_DURATION);
					}
					if(canWalkCrouched){
						if(this.getOrientation().equals("left")){

						} else{

						}
						canWalkCrouched = false;
						this.setCurrentAnimation("crouching walk_" + orientation, FRAME_DURATION);
					} else{
						this.setCurrentAnimation("crouching idle_" + orientation, FRAME_DURATION);
					}
				}
				break;
				
			case COLLIDED:
				if(this.getOrientation().equals("left")){

				} else{

				}
				this.setCurrentAnimation("running collided_" + orientation, FRAME_DURATION);
				break;
				
			default:
				
				break;
			}
			break;
			
		case "crouching idle_left":
		case "crouching idle_right":

			switch(currentState){
			case IDLE:
				if(!down_pressed){
					this.setCurrentAnimation("crouching up_" + orientation, FRAME_DURATION);
				} 
				break;
				
			case JUMP:
				this.setCurrentAnimation("crouching up_" + orientation, FRAME_DURATION);
				break;
				
			case MOVE:
				if(canWalkCrouched){
					canWalkCrouched = false;
					if(changed_position){
						changed_position = false;
					} else{
						if(this.getOrientation().equals("left")){

						} else{

						}
						this.setCurrentAnimation("crouching walk_" + orientation, FRAME_DURATION);
					}
				}
				if(!down_pressed){
					this.setCurrentAnimation("crouching up_" + orientation, FRAME_DURATION);
				}
				break;
				
			case COLLIDED:
				System.out.println("COLLIDED EN POS TO RARA");
				break;
				
			default:
				
				break;
			}
			break;
			
		case "crouching up_left":
		case "crouching up_right":

			switch(currentState){
			case IDLE:
				if(currentAnimation.isOver(false)){
					canWalkCrouched = true;
					this.setCurrentAnimation("idle_" + orientation, FRAME_DURATION);
				}
				break;
				
			case JUMP:
				if(currentAnimation.isOver(false)){
					canWalkCrouched = true;
					this.setCurrentAnimation("idle_" + orientation, FRAME_DURATION);
				}
				break;
				
			case MOVE:
				if(currentAnimation.isOver(false)){
					canWalkCrouched = true;
					this.setCurrentAnimation("idle_" + orientation, FRAME_DURATION);
				}
				break;
				
			case COLLIDED:
				if(this.getOrientation().equals("left")){

				} else{

				}
				this.setCurrentAnimation("running collided_" + orientation, FRAME_DURATION);
				break;
				
			default:
				
				break;
			}
			break;
			
		case "crouching walk_left":
		case "crouching walk_right":

			switch(currentState){
			case IDLE:
				if(this.getOrientation().equals("left")){
				
				} else{

				}
				if(currentAnimation.isOver(false)){
					if(!down_pressed){
						this.setCurrentAnimation("crouching up_" + orientation, FRAME_DURATION);
					} else{
						this.setCurrentAnimation("crouching idle_" + orientation, FRAME_DURATION);
					}
				}
				break;
				
			case JUMP:
				if(this.getOrientation().equals("left")){

				} else{

				}
				if(currentAnimation.isOver(false)){
					this.setCurrentAnimation("crouching up_" + orientation, FRAME_DURATION);
				}
				break;
				
			case MOVE:
				if(this.getOrientation().equals("left")){

				} else{

				}
				if(currentAnimation.isOver(false)){
					this.setCurrentAnimation("crouching idle_" + orientation, FRAME_DURATION);
				}
				break;
				
			case COLLIDED:
				if(this.getOrientation().equals("left")){

				} else{

				}
				this.setCurrentAnimation("running collided_" + orientation, FRAME_DURATION);
				break;
				
			default:
				
				break;
			}
			break;
			
		case "dieing_left":
		case "dieing_right":

			switch(currentState){
			case IDLE:
				
				break;
				
			case JUMP:
				
				break;
				
			case MOVE:
				
				break;
				
			case COLLIDED:
				System.out.println("COLLIDED EN ANIMATION TO RARA");
				break;
				
			default:
				
				break;
			}
			break;
	
		case "drinking_left":
		case "drinking_right":

			switch(currentState){
			case IDLE:
				
				break;
				
			case JUMP:
				
				break;
				
			case MOVE:
				
				break;
				
			case COLLIDED:
				System.out.println("COLLIDED EN ANIMATION TO RARA");
				break;
				
			default:
				
				break;
			}
			break;
			
		case "falling_left":
		case "falling_right":

			if (this.getCurrentAnimation().isOver(true)) {
				this.setCurrentAnimation("falling idle_" + orientation, FRAME_DURATION);
				
//				System.out.println("Starts free_fall");
				this.setFreeFall(true);
			}
			
//			switch(currentState){
//			case IDLE:
//				
//				break;
//				
//			case JUMP:
//				
//				break;
//				
//			case MOVE:
//				
//				break;
//				
//			case COLLIDED:
//				
//				break;
//				
//			default:
//				
//				break;
//			}
			break;
			
		case "falling idle_left":
		case "falling idle_right":
			
			break;
			
		case "got sword_left":
		case "got sword_right":

			switch(currentState){
			case IDLE:
				
				break;
				
			case JUMP:
				
				break;
				
			case MOVE:
				
				break;
				
			case COLLIDED:
				System.out.println("COLLIDED EN POS TO RARA");
				break;
				
			default:
				
				break;
			}
			break;
			
		case "hanging backwards_left":
		case "hanging backwards_right":

			switch(currentState){
			case IDLE:
				
				break;
				
			case JUMP:
				
				break;
				
			case MOVE:
				
				break;
				
			case COLLIDED:
				
				break;
				
			default:
				
				break;
			}
			break;
			
		case "hanging forward_left":
		case "hanging forward_right":

			switch(currentState){
			case IDLE:
				
				break;
				
			case JUMP:
				
				break;
				
			case MOVE:
				
				break;
				
			case COLLIDED:
				
				break;
				
			default:
				
				break;
			}
			break;
			
		case "hanging idle_left":
		case "hanging idle_right":

			switch(currentState){
			case IDLE:
				this.setCurrentAnimation("scaling down_" + orientation, FRAME_DURATION);
				break;
				
			case JUMP:
				this.setCurrentAnimation("clipping_" + orientation, FRAME_DURATION);
				break;
				
			case MOVE:
				this.setCurrentAnimation("scaling down_" + orientation, FRAME_DURATION);
				break;
				
			case COLLIDED:
				
				break;
				
			default:
				
				break;
			}
			break;
			
		case "idle_left":
		case "idle_right":

			switch(currentState){
			case IDLE:
				if(changed_position){
					changed_position = false;
				} else if(down_pressed){
					this.setCurrentAnimation("crouching down_" + orientation, FRAME_DURATION);
				}
				break;
				
			case JUMP:
				if(right_pressed || left_pressed){
					if(right_pressed && this.currentAnimation.equals("right")){
						this.setCurrentAnimation("simple jump_" + orientation, FRAME_DURATION);
					} else if(left_pressed && this.currentAnimation.equals("left")){
						this.setCurrentAnimation("simple jump_" + orientation, FRAME_DURATION);
					} else{
						this.setCurrentAnimation("scaling up start_" + orientation, FRAME_DURATION);
					}
				} else{
					this.setCurrentAnimation("scaling up start_" + orientation, FRAME_DURATION);
				}
				break;
				
			case MOVE:
				if(changed_position){
					changed_position = false;
					this.setOrientation(newOrientation);
					this.setCurrentAnimation("turning_" + orientation, FRAME_DURATION);
				} else if(shift_pressed){
					if(canMakeStep){
						if(this.getOrientation().equals("left")){

						} else{

						}
						this.setCurrentAnimation("walking a step_" + orientation, FRAME_DURATION);
						canMakeStep = false;
					} else{
					
					}
				} else if(down_pressed){
					if(this.getOrientation().equals("left")){

					} else{

					}
					this.setCurrentAnimation("crouching down_" + orientation, FRAME_DURATION);
				}
				else{
					if(this.getOrientation().equals("left")){

					} else{

					}
//					System.out.printf("starts running: ");
					this.setCurrentAnimation("running start_" + orientation, FRAME_DURATION);
				}
				break;
				
			case COLLIDED:

				break;
				
			default:
				
				break;
			}
			break;
			
		case "putting down sword_left":
		case "putting down sword_right":

			switch(currentState){
			case IDLE:
				
				break;
				
			case JUMP:
				
				break;
				
			case MOVE:
				
				break;
				
			case COLLIDED:

				break;
				
			default:
				
				break;
			}
			break;
			
		case "running collided_left":
		case "running collided_right":

			switch(currentState){
			case IDLE:
				if(this.currentAnimation.isOver(false)){
					this.setCurrentAnimation("idle_" + orientation, FRAME_DURATION);
				}
				break;
				
			case JUMP:
				if(this.currentAnimation.isOver(false)){
					this.setCurrentAnimation("idle_" + orientation, FRAME_DURATION);
				}
				break;
				
			case MOVE:
				if(this.currentAnimation.isOver(false)){
					this.setCurrentAnimation("idle_" + orientation, FRAME_DURATION);
				}
				break;
				
			case COLLIDED:
				if(this.currentAnimation.isOver(false)){
					this.setCurrentAnimation("idle_" + orientation, FRAME_DURATION);
					this.currentState = PlayerState.IDLE;
				}
				break;
				
			default:
				
				break;
			}
			break;
			
		case "running jump collided_left":
		case "running jump collided_right":

			switch(currentState){
			case IDLE:
				if(this.currentAnimation.isOver(false)){
					this.setCurrentAnimation("idle_" + orientation, FRAME_DURATION);
					this.fall();
				}
				break;
				
			case JUMP:
				if(this.currentAnimation.isOver(false)){
					this.setCurrentAnimation("idle_" + orientation, FRAME_DURATION);
					this.fall();
				}
				break;
				
			case MOVE:
				if(this.currentAnimation.isOver(false)){
					this.setCurrentAnimation("idle_" + orientation, FRAME_DURATION);
					this.fall();
				}
				break;
				
			case COLLIDED:
				if(this.currentAnimation.isOver(false)){
					this.setCurrentAnimation("idle_" + orientation, FRAME_DURATION);
					this.currentState = PlayerState.IDLE;
					this.fall();
				}
				break;
				
			default:
				
				break;
			}
			break;
		
		case "running jump_left":
		case "running jump_right":
			
			this.jumping = true;

			switch(currentState){
			case IDLE:
				if(this.getOrientation().equals("left")){

				} else{

				}
				
				if(this.currentAnimation.isOver(false)){
					
					if(this.getOrientation().equals("left")){

					} else{

					}
					this.setCurrentAnimation("running stop start_" + orientation, FRAME_DURATION);
					
				}
				break;
				
			case JUMP:
				if(this.getOrientation().equals("left")){

				} else{

				}
				
				if(this.currentAnimation.isOver(false)){
					
					this.fall();
					
					if(this.getOrientation().equals("left")){

					} else{

					}
					this.setCurrentAnimation("running stop start_" + orientation, FRAME_DURATION);
				}
				break;
				
			case MOVE:
				if(this.getOrientation().equals("left")){

				} else{
				
				}
				
				if(this.currentAnimation.isOver(false)){
					
					this.fall();
					
					if(this.getOrientation().equals("left")){

					} else{

					}
					this.setCurrentAnimation("running_" + orientation, FRAME_DURATION);
				}
				break;
				
			case COLLIDED:
				this.setCurrentAnimation("running jump collided_" + orientation, FRAME_DURATION);
				break;
				
			default:
				
				break;
			}
			break;
			
		case "running start_left":
		case "running start_right":

			switch(currentState){
			case IDLE:
				if(this.getOrientation().equals("left")){

				} else{

				}
				if(currentAnimation.isOver(false)){
					if(this.getOrientation().equals("left")){

					} else{

					}
					this.setCurrentAnimation("running stop start_" + orientation, FRAME_DURATION);
				}
				break;
				
			case JUMP:
				if(this.getOrientation().equals("left")){

				} else{

				}
				if(currentAnimation.isOver(false)){
					if(this.getOrientation().equals("left")){
						if(left_pressed){
							this.setCurrentAnimation("running jump_" + orientation, FRAME_DURATION);
						} else{
							this.setCurrentAnimation("running stop_" + orientation, FRAME_DURATION);
						}
					} else{
						if(right_pressed){
							this.setCurrentAnimation("running jump_" + orientation, FRAME_DURATION);
						} else{
							this.setCurrentAnimation("running stop start_" + orientation, FRAME_DURATION);
						}
					}
				}
				break;
				
			case MOVE:
				if(this.getOrientation().equals("left")){

				} else{

				}
				if(currentAnimation.isOver(false)){
					if(changed_position){
						changed_position = false;
						this.setOrientation(newOrientation);
						this.setCurrentAnimation("turn running_" + orientation, FRAME_DURATION);
					} else{
						this.setCurrentAnimation("running_" + orientation, FRAME_DURATION);
					}
				} 
				break;
				
			case COLLIDED:
				if(this.getOrientation().equals("left")){

				} else{

				}
				this.setCurrentAnimation("running collided_" + orientation, FRAME_DURATION);
				break;
				
			default:
				
				break;
			}
			break;
			
		case "running stop start_left":
		case "running stop start_right":

			switch(currentState){
			case IDLE:
				if(this.getOrientation().equals("left")){

				} else{

				}
				if(currentAnimation.isOver(false)){
					this.setCurrentAnimation("running stop_" + orientation, FRAME_DURATION);
				}
				break;
				
			case JUMP:
				if(this.getOrientation().equals("left")){

				} else{

				}
				if(currentAnimation.isOver(false)){
					this.setCurrentAnimation("running stop_" + orientation, FRAME_DURATION);
				}
				break;
				
			case MOVE:
				if(this.getOrientation().equals("left")){

				} else{

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
				
			case COLLIDED:
				if(this.getOrientation().equals("left")){

				} else{

				}
				this.setCurrentAnimation("running collided_" + orientation, FRAME_DURATION);
				break;
				
			default:
				
				break;
			}
			break;
			
		case "running stop_left":
		case "running stop_right":

			switch(currentState){
			case IDLE:
				if(this.getOrientation().equals("left")){

				} else{

				}
				if(currentAnimation.isOver(false)){
					this.setCurrentAnimation("idle_" + orientation, FRAME_DURATION);
				}
				break;
				
			case JUMP:
				if(this.getOrientation().equals("left")){

				} else{

				}
				if(currentAnimation.isOver(false)){
					this.setCurrentAnimation("idle_" + orientation, FRAME_DURATION);
				}
				break;
				
			case MOVE:
				if(currentAnimation.isOver(false)){
					if(changed_position){
						changed_position = false;
						this.setOrientation(newOrientation);
						this.setCurrentAnimation("turning_" + orientation, FRAME_DURATION);
						
					} else{
						if(this.getOrientation().equals("left")){

						} else{

						}
						this.setCurrentAnimation("running start_" + orientation, FRAME_DURATION);
					}
				}
				break;
				
			case COLLIDED:
				if(this.getOrientation().equals("left")){

				} else{

				}
				
				this.setCurrentAnimation("running collided_" + orientation, FRAME_DURATION);
				break;
				
			default:
				
				break;
			}
			break;
			
		case "running_left":
		case "running_right":

			switch(currentState){
			case IDLE:
				this.setCurrentAnimation("running stop start_" + orientation, FRAME_DURATION);
				break;
				
			case JUMP:
				if(this.getOrientation().equals("left")){
					if(left_pressed){
						this.setCurrentAnimation("running jump_" + orientation, FRAME_DURATION);
					} else{
						this.setCurrentAnimation("running stop start_" + orientation, FRAME_DURATION);
					}
				} else{
					if(right_pressed){
						this.setCurrentAnimation("running jump_" + orientation, FRAME_DURATION);
					} else{
						this.setCurrentAnimation("running stop start_" + orientation, FRAME_DURATION);
					}
				}
				
				break;
				
			case MOVE:
				if(this.getOrientation().equals("left")){
					
				} else{
					
				}
				if(changed_position){
					changed_position = false;
					this.setOrientation(newOrientation);
					this.setCurrentAnimation("turn running_" + orientation, FRAME_DURATION);
				} else if(down_pressed){
					this.setCurrentAnimation("crouching down_" + orientation, FRAME_DURATION);
				}
				break;
				
			case COLLIDED:
				if(this.getOrientation().equals("left")){
			
				} else{
					
				}
				this.setCurrentAnimation("running collided_" + orientation, FRAME_DURATION);
				break;
				
			default:
				
				break;
			}
			break;
			
		case "scaling down_left":
		case "scaling down_right":
			
			canClimb = false;

			switch(currentState){
			case IDLE:
				if(this.currentAnimation.isOver(false)){
					this.setCurrentAnimation("idle_" + orientation, FRAME_DURATION);
				}
				break;
				
			case JUMP:
				if(this.currentAnimation.isOver(false)){
					this.setCurrentAnimation("idle_" + orientation, FRAME_DURATION);
				}
				break;
				
			case MOVE:
				if(this.currentAnimation.isOver(false)){
					this.setCurrentAnimation("idle_" + orientation, FRAME_DURATION);
				}
				break;
				
			default:
				
				break;
				
			case COLLIDED:
				System.out.println("COLISIONO EN POS TO RARA");
				break;
			}
			break;
			
		case "scaling fall_left":
		case "scaling fall_right":

			switch(currentState){
			case IDLE:
				
				break;
				
			case JUMP:
				
				break;
				
			case MOVE:
				
				break;
				
			case COLLIDED:
				
				break;
				
			default:
				
				break;
			}
			break;
			
		case "scaling up start_left":
		case "scaling up start_right":

			switch(currentState){
			case IDLE:
				if(this.currentAnimation.isOver(false)){
					this.setCurrentAnimation("scaling up_" + orientation, FRAME_DURATION);
				}
				break;
				
			case JUMP:
				if(right_pressed || left_pressed){
					if(right_pressed && orientation.equals("right")){
						this.setCurrentAnimation("simple jump_" + orientation, FRAME_DURATION);
					} else if(left_pressed && orientation.equals("left")){
						this.setCurrentAnimation("simple jump_" + orientation, FRAME_DURATION);
					} else{
						if(this.currentAnimation.isOver(false)){
							this.setCurrentAnimation("scaling up_" + orientation, FRAME_DURATION);
						}
					}
				} else{
					if(this.currentAnimation.isOver(false)){
						this.setCurrentAnimation("scaling up_" + orientation, FRAME_DURATION);
					}
				}
				break;
				
			case MOVE:
				if(right_pressed || left_pressed){
					if(right_pressed && orientation.equals("right")){
						this.setCurrentAnimation("simple jump_" + orientation, FRAME_DURATION);
					} else if(left_pressed && orientation.equals("left")){
						this.setCurrentAnimation("simple jump_" + orientation, FRAME_DURATION);
					} else{
						if(this.currentAnimation.isOver(false)){
							this.setCurrentAnimation("scaling up_" + orientation, FRAME_DURATION);
						}
					}
				} else{
					if(this.currentAnimation.isOver(false)){
						this.setCurrentAnimation("scaling up_" + orientation, FRAME_DURATION);
					}
				}
				break;
				
			case COLLIDED:
				System.out.println("COLISIONO EN POS TO RARA");
				break;
				
			default:
				
				break;
			}
			break;
			
		case "scaling up_left":
		case "scaling up_right":

			switch(currentState){
			case IDLE:
				if(this.currentAnimation.isOver(false) && canClimb){
					this.setCurrentAnimation("hanging idle_" + orientation, FRAME_DURATION);
				}
				else if (this.currentAnimation.isOver(false) && !canClimb) {
					this.setCurrentAnimation("scaling down_" + orientation, FRAME_DURATION);
				}
				break;
				
			case JUMP:
				if(this.currentAnimation.isOver(false) && canClimb){
					this.setCurrentAnimation("hanging idle_" + orientation, FRAME_DURATION);
				}
				break;
				
			case MOVE:
				if(this.currentAnimation.isOver(false) && canClimb){
					this.setCurrentAnimation("hanging idle_" + orientation, FRAME_DURATION);
				}
				else if (this.currentAnimation.isOver(false) && !canClimb) {
					this.setCurrentAnimation("scaling down_" + orientation, FRAME_DURATION);
				}
				break;
				
			case COLLIDED:
				System.out.println("COLISIONO EN UNA ANIMACION TO RARA");
				break;
				
			default:
				
				break;
			}
			break;
			
		case "simple jump_left":
		case "simple jump_right":

			switch(currentState){
			case IDLE:
				if(this.getOrientation().equals("left")){

				} else{

				}
				
				if(this.currentAnimation.isOver(false)){
					
					this.setCurrentAnimation("idle_" + orientation, FRAME_DURATION);
				}
				break;
				
			case JUMP:
				if(this.getOrientation().equals("left")){
					
				} else{

				}
				
				if(this.currentAnimation.isOver(false)){
					this.setCurrentAnimation("idle_" + orientation, FRAME_DURATION);
				}
				break;
				
			case MOVE:
				if(this.getOrientation().equals("left")){

				} else{

				}
				
				if(this.currentAnimation.isOver(false)){
					this.setCurrentAnimation("idle_" + orientation, FRAME_DURATION);
				}
				break;
				
			case COLLIDED:
				this.setCurrentAnimation("running jump collided_" + orientation, FRAME_DURATION);
				break;
				
			default:
				
				break;
			}
			break;
			
		case "sword attacking_left":
		case "sword attacking_right":

			switch(currentState){
			case IDLE:
				
				break;
				
			case JUMP:
				
				break;
				
			case MOVE:
				
				break;
				
			case COLLIDED:

				break;
				
			default:
				
				break;
			}
			break;
			
		case "taking sword out_left":
		case "taking sword out_right":

			switch(currentState){
			case IDLE:
				
				break;
				
			case JUMP:
				
				break;
				
			case MOVE:
				
				break;
				
			case COLLIDED:
				
				break;
				
			default:
				
				break;
			}
			break;
			
		case "turn running_left":
		case "turn running_right":
		case "turn running started_left":
		case "turn running started_right":

			switch(currentState){
			case IDLE:
				if(this.getOrientation().equals("left")){

				} else{

				}
				if(currentAnimation.isOver(false)){
					if(this.getOrientation().equals("left")){

					} else{

					}
					this.setCurrentAnimation("running stop start_" + orientation, FRAME_DURATION);
				}
				break;
				
			case JUMP:
				if(this.getOrientation().equals("left")){

				} else{

				}
				if(currentAnimation.isOver(false)){
					if(this.getOrientation().equals("left")){
						this.setCurrentAnimation("running jump_" + orientation, FRAME_DURATION);
					} else{
						this.setCurrentAnimation("running jump_" + orientation, FRAME_DURATION);
					}
				}
				break;
				
			case MOVE:
				if(this.getOrientation().equals("left")){
					
				} else{
					
				}
				if(currentAnimation.isOver(false)){
					if(this.getOrientation().equals("left")){
						
					} else{
					
					}
					this.setCurrentAnimation("running_" + orientation, FRAME_DURATION);
					
				}
				break;
				
			case COLLIDED:
				if(this.getOrientation().equals("left")){

				} else{

				}
				this.setCurrentAnimation("running collided_" + orientation, FRAME_DURATION);
				break;
				
			default:
				
				break;
			}
			break;
			
		case "turning_left":
		case "turning_right":

			switch(currentState){
			case IDLE:
				if(currentAnimation.isOver(false)){
					this.setCurrentAnimation("idle_" + orientation, FRAME_DURATION);
				}
				break;
				
			case JUMP:
				if(currentAnimation.isOver(false)){
					this.setCurrentAnimation("scaling up start_" + orientation, FRAME_DURATION);
				}
				break;
				
			case MOVE:
				if(currentAnimation.isOver(false)){
					if(changed_position){
						changed_position = false;
						this.setOrientation(newOrientation);
						this.setCurrentAnimation("turning_" + orientation, FRAME_DURATION);
					} else if(shift_pressed){
						if(canMakeStep){
							if(this.getOrientation().equals("left")){

							} else{

							}
							this.setCurrentAnimation("walking a step_" + orientation, FRAME_DURATION);
							canMakeStep = false;
						} else{

						}
					} else{
						if(this.getOrientation().equals("left")){

						} else{

						}
						this.setCurrentAnimation("running start_" + orientation, FRAME_DURATION);
					}
				}
				break;
				
			case COLLIDED:
				System.out.println("COLISIONO EN UNA POS TO RARA");
				break;
				
			default:
				
				break;
			}
			break;
			
		case "walking a step_left":
		case "walking a step_right":

			switch(currentState){
			case IDLE:
				if(this.getOrientation().equals("left")){

				} else{

				}
				if(currentAnimation.isOver(false)){
					this.setCurrentAnimation("idle_" + orientation, FRAME_DURATION);
				}
				break;
				
			case JUMP:
				if(this.getOrientation().equals("left")){

				} else{

				}
				if(currentAnimation.isOver(false)){
					this.setCurrentAnimation("idle_" + orientation, FRAME_DURATION);
				}
				break;
				
			case MOVE:
				if(this.getOrientation().equals("left")){

				} else{

				}
				if(currentAnimation.isOver(false)){
					this.setCurrentAnimation("idle_" + orientation, FRAME_DURATION);
				}
				break;
				
			case COLLIDED:
				if(this.getOrientation().equals("left")){

				} else{

				}
				this.setCurrentAnimation("running collided_" + orientation, FRAME_DURATION);
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

	/**
	 * @return the currentState
	 */
	public PlayerState getCurrentState() {
		return currentState;
	}
	
	/**
	 * @return the fallDistance
	 */
	public int getFallDistance() {
		return fallDistance;
	}

	/**
	 * @param fallDistance the fallDistance to set
	 */
	public void setFallDistance(int fallDistance) {
		this.fallDistance = fallDistance;
	}
	
	public void fall() {
		this.setCurrentAnimation("falling_" + orientation, FRAME_DURATION);
		
		if (orientation.equals("left")) {
			this.move(-10, 0);
		}
		else if (orientation.equals("right")) {
			this.move(10, 0);
		}
		this.falling = true;
	}
	
	public void safeLand() {
		this.setCurrentAnimation("crouching down_" + orientation, FRAME_DURATION);
		
		// Only corrects player position if it is a short fall
//		if (!isFreeFall()) {
//			this.move(0, -20);
//		}
//		
//		else {
//			this.move(0, -1);
//		}
		
		this.enableBoundingBox();
	}
	
	public void riskyLand() {
		this.setCurrentAnimation("crouching down_" + orientation, FRAME_DURATION);
		this.enableBoundingBox();
	}
	
	public void die() {
		
		// TODO
	}
	
	/**
	 * Sets the player's current animation to collided
	 */
	public void collide(Entity wall) {
		this.setCurrentAnimation("running collided_" + orientation, FRAME_DURATION);
		this.enableBoundingBox();
		
		// player corrects its distance from wall
//		int bgLeft = (int) wall.getBoundingBox().getMinX();
//		int bgRight = (int) wall.getBoundingBox().getMaxX();
//		int pC = this.getCenter()[0];
//		int pW2 = this.getCurrentAnimation().getImage().getWidth()/2;
//		int gap = 10;
//		
//		if (this.getOrientation().equals("left")) {
//			
//			while ( ( pC - pW2 ) < ( bgRight + gap ) ) {
//				this.move(1, 0);
//				
//				pC = this.getCenter()[0];
//				pW2 = this.getCurrentAnimation().getImage().getWidth()/2;
//			}
//		}
//		else if (this.getOrientation().equals("right")) {
//			
//			while ( ( pC + pW2 ) > (bgLeft - gap ) ) {
//				this.move(-1, 0);
//				
//				pC = this.getCenter()[0];
//				pW2 = this.getCurrentAnimation().getImage().getWidth()/2;
//			}
//		}
	}
	
	public void collide_jump() {
		this.setCurrentAnimation("running jump collided_" + orientation, FRAME_DURATION);
		this.enableBoundingBox();
		
		int gap = 20;
		
		if (this.getOrientation().equals("left")) {
			int newX = this.getX() + gap;
			this.setX(newX);
		}
		else if (this.getOrientation().equals("right")) {
			int newX = this.getX() - gap;
			this.setX(newX);
		}
	}
	
	public boolean isColliding() {
		return currentAnimation.getId().contains("collided");
	}
	
	/**
	 * @return the canClimb
	 */
	public boolean isClimbing() {
		return canClimb;
	}

	/**
	 * @param canClimb the canClimb to set
	 */
	public void setClimbing(boolean canClimb) {
		this.canClimb = canClimb;
	}
	
	/**
	 * 
	 * @return true if the player is executing one of its
	 * jump animations
	 */
	public boolean isJumping() {
		return currentAnimation.getId().contains("jump");
	}
	
	/**
	 * 
	 * @return true if the player has just executed a jump
	 */
	public boolean wasJumping() {
		return jumping;
	}
	
	/**
	 * 
	 * sets the jumping value to false
	 */
	public boolean notJumping() {
		return jumping = false;
	}
	
	/**
	 * 
	 * @return true if the player is executing one of its
	 * fall animations
	 */
	public boolean isFalling() {
		return currentAnimation.getId().contains("fall");
	}
	
	/**
	 * 
	 * @return true if the player is not neither
	 * jumping nor falling
	 */
	public boolean isGrounded() {
		return !isJumping() && !isFalling();
	}
	
	@Override
	public void moveCharacter(){
		
		/* If character is blocked sideways, it cant move horizontally */
		if (leftBlocked || rightBlocked) {
			xSpeed = 0;
		}
		
		/* Applies gravity if falling */
		if ( isFalling() ) {
			int newySpeed = fallSpeed + gravity;
			
			
			if (newySpeed > maxySpeed) {
				newySpeed = maxySpeed;
			}
			
			ySpeed = newySpeed;
		}
		else if ( isGrounded() ) {

			/* Character is on the ground */
			ySpeed = 0;
			yFrameOffset = 0;
		}
		
		/* Moves the character and its bounding box */
		setX(x + xSpeed + xFrameOffset);
		setY(y + ySpeed + yFrameOffset);
		boundingBox.translate(xSpeed + xFrameOffset, ySpeed + yFrameOffset);
	}

}
