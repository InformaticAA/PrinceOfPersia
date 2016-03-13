package entities;

import java.awt.Rectangle;
import java.util.Hashtable;

import framework.Loader;
import input.Key;

public class Player extends Character {

	private enum PlayerState {IDLE, STARTING_TO_MOVE, MOVE, JUMP, COMBAT};
	
	/* Constants */
	private final String RUNNING_START = "running start";
	private final String RUNNING = "running";
	private final int FRAME_DURATION = 1;
	private final int MOVE_SPEED = 2;
	
	private PlayerState currentState;
	private boolean shift_pressed;
	private boolean changed_position;
	private String newOrientation;
	
	public Player(int x, int y, Loader loader, String orientation) {
		super(x, y, loader, orientation);
		animations = loader.getAnimations("Dastan");
		
		currentAnimation = animations.get("idle_" + this.orientation);
		currentState = PlayerState.IDLE;
		
		boundingBox = new Rectangle(x,y,currentAnimation.getImage().getWidth(),
				currentAnimation.getImage().getHeight());
		
		this.changed_position = false;
		this.newOrientation = orientation;
	}
	
	@Override
	public void update(long elapsedTime) {
//		super.update(elapsedTime);

		switch (currentState) {
		case IDLE:
			
			switch(currentAnimation.getId()){
			
			case "turning_left":
			case "turning_right":
				if(currentAnimation.isOver(false)){
					this.setMoveSpeed(0);
					currentAnimation.reset();
					currentAnimation = animations.get("idle_" + orientation);
				}
				break;
				
			case "running start_left":
			case "running start_right":
				if(currentAnimation.isOver(false)){
					System.out.println();
					System.out.printf("stops running: ");
					currentAnimation.reset();
					currentAnimation = animations.get("running stop_" + orientation);
					currentAnimation.setFrameDuration(FRAME_DURATION);
				}
				break;
				
			case "running_left":
			case "running_right":	
				System.out.printf("stops running: ");
				currentAnimation.reset();
				currentAnimation = animations.get("running stop_" + orientation);
				currentAnimation.setFrameDuration(FRAME_DURATION);
				break;
				
			case "turn running_left":
			case "turn running_right":
				if(currentAnimation.isOver(false)){
					currentAnimation.reset();
					currentAnimation = animations.get("running stop_" + orientation);
					currentAnimation.setFrameDuration(FRAME_DURATION);
				}
				break;
				
			case "running stop_left":
			case "running stop_right":
				System.out.printf(currentAnimation.getCurrentFrame() + ", ");
				if(currentAnimation.isOver(false)){
					System.out.println();
					currentAnimation.reset();
					this.setMoveSpeed(0);
					currentAnimation = animations.get("idle_" + orientation);
				}
				break;
			}
			
			
			
			
			
//			currentAnimation = animations.get("running");
//			System.out.println("x: " + x + ", y: " + y);
//			System.out.println("x_draw: " + x_draw + ", y_draw: " + y_draw);
			
			break;
		case MOVE:
			
			switch (currentAnimation.getId()){
			case "idle_left":
			case "idle_right":
				if(changed_position){
					changed_position = false;
					this.setOrientation(newOrientation);
					currentAnimation.reset();
					currentAnimation = animations.get("turning_" + orientation);
					currentAnimation.setFrameDuration(FRAME_DURATION);
				}
				else{
					System.out.printf("starts running: ");
					currentAnimation.reset();
					currentAnimation = animations.get("running start_" + orientation);
					currentAnimation.setFrameDuration(FRAME_DURATION);
				}
				break;
				
			case "turning_left":
			case "turning_right":
				if(currentAnimation.isOver(false)){
					currentAnimation.reset();
					currentAnimation = animations.get("running start_" + orientation);
				}
				break;
				
			case "running start_left":
			case "running start_right":
				System.out.printf(currentAnimation.getCurrentFrame() + ", ");
				if(currentAnimation.isOver(false)){
					if(changed_position){
						changed_position = false;
						this.setOrientation(newOrientation);
						currentAnimation.reset();
						currentAnimation = animations.get("turn running_" + orientation);
						currentAnimation.setFrameDuration(FRAME_DURATION);
					} else{
						System.out.println();
						currentAnimation.reset();
						currentAnimation = animations.get("running_" + orientation);
						currentAnimation.setFrameDuration(FRAME_DURATION);
					}
				} 
				break;
				
			case "running_left":
			case "running_right":
				
				if(changed_position){
					changed_position = false;
					this.setOrientation(newOrientation);
					currentAnimation.reset();
					currentAnimation = animations.get("turn running_" + orientation);
					currentAnimation.setFrameDuration(FRAME_DURATION);
				}
//				System.out.println("running");
				break;
			case "running stop_left":
			case "running stop_right":
				if(currentAnimation.isOver(false)){
					currentAnimation.reset();
					currentAnimation = animations.get("running start_" + orientation);
					currentAnimation.setFrameDuration(FRAME_DURATION);
				}
				break;
				
			case "turn running_left":
			case "turn running_right":
				if(currentAnimation.isOver(false)){
					currentAnimation.reset();
					currentAnimation = animations.get("running start_" + orientation);
					currentAnimation.setFrameDuration(FRAME_DURATION);
				}
				break;
//			default:
//				System.out.println(currentAnimation.getId());
			}
			this.moveCharacter();
		default:
			break;
		}
		currentAnimation.update(elapsedTime);
	}
	
	public void manageKeyPressed(int key_pressed, Hashtable<String,Integer> keys_mapped){
		if(key_pressed == keys_mapped.get(Key.UP)){
			
		} else if(key_pressed == keys_mapped.get(Key.RIGHT)){

			currentState = PlayerState.MOVE;
			if(this.getOrientation().equals("left")){
				this.changed_position = true;
				this.newOrientation = "right";
			}
//			if(currentState != PlayerState.MOVE){
//				this.currentAnimation = animations.get("running");
//				this.currentAnimation.setFrameDuration(4);
//				this.currentState = PlayerState.MOVE;
//				this.setMoveSpeed(15);
//			}
		} else if(key_pressed == keys_mapped.get(Key.LEFT)){
			
			currentState = PlayerState.MOVE;
			if(this.getOrientation().equals("right")){
				this.changed_position = true;
				this.newOrientation = "left";
			}
			
//			if(currentState != PlayerState.MOVE){
//				this.currentAnimation = animations.get("running");
//				this.currentAnimation.setFrameDuration(4);
//				this.currentState = PlayerState.MOVE;
//				this.setMoveSpeed(-15);
//			}
		} else if(key_pressed == keys_mapped.get(Key.DOWN)){
			
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
			if(currentState != PlayerState.IDLE && this.getOrientation().equals("right")){
				System.out.println("Seteamos el idle de la derecha");
				this.currentState = PlayerState.IDLE;
			}
			
//			if(this.currentState == PlayerState.MOVE){
//				this.currentAnimation = animations.get("idle");
//				this.setMoveSpeed(0);
//				this.currentState = PlayerState.IDLE;
//			}
			
		} else if(key_released == keys_mapped.get(Key.LEFT)){
			if(currentState != PlayerState.IDLE && this.getOrientation().equals("left")){
				System.out.println("Seteamos el idle de la izquierda");
				this.currentState = PlayerState.IDLE;
			}
//			if(this.currentState == PlayerState.MOVE){
//				this.currentAnimation = animations.get("idle");
//				this.setMoveSpeed(0);
//				this.currentState = PlayerState.IDLE;
//			}
			
		} else if(key_released == keys_mapped.get(Key.DOWN)){
			
		} else if(key_released == keys_mapped.get(Key.SHIFT)){
			shift_pressed = false;
		}
	}

}
