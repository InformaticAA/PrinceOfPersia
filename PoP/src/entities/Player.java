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
	
	public Player(int x, int y, Loader loader) {
		super(x, y, loader);
		animations = loader.getAnimations("Dastan");
		
		currentAnimation = animations.get("idle");
		currentState = PlayerState.IDLE;
		
		boundingBox = new Rectangle(x,y,currentAnimation.getImage().getWidth(),
				currentAnimation.getImage().getHeight());
		
	}
	
	@Override
	public void update(long elapsedTime) {
//		super.update(elapsedTime);

		switch (currentState) {
		case IDLE:
			
			switch(currentAnimation.getId()){
			case "running start":
				if(currentAnimation.isOver(false)){
					currentAnimation.reset();
					System.out.println();
					System.out.printf("stops running: ");
					currentAnimation = animations.get("running stop");
					currentAnimation.setFrameDuration(FRAME_DURATION);
				}
				break;
			case "running":
				if(currentAnimation.isOver(false)){
					currentAnimation.reset();
					System.out.printf("stops running: ");
					currentAnimation = animations.get("running stop");
					currentAnimation.setFrameDuration(FRAME_DURATION);
				}
				break;
			case "running stop":
				System.out.printf(currentAnimation.getCurrentFrame() + ", ");
				if(currentAnimation.isOver(false)){
					System.out.println();
					currentAnimation.reset();
					this.setMoveSpeed(0);
					currentAnimation = animations.get("idle");
				}
				break;
			}
			
			
			
			
			
//			currentAnimation = animations.get("running");
//			System.out.println("x: " + x + ", y: " + y);
//			System.out.println("x_draw: " + x_draw + ", y_draw: " + y_draw);
			
			break;
		case MOVE:
			
			switch (currentAnimation.getId()){
			case "idle":
				System.out.printf("starts running: ");
				currentAnimation = animations.get("running start");
				currentAnimation.setFrameDuration(FRAME_DURATION);
				break;
			case "running start":
				System.out.printf(currentAnimation.getCurrentFrame() + ", ");
				if(currentAnimation.isOver(false)){
					System.out.println();
					currentAnimation.reset();
					currentAnimation = animations.get("running");
					currentAnimation.setFrameDuration(FRAME_DURATION);
				} 
				break;
			case "running":
//				System.out.println("running");
				break;
			case "running stop":
				if(currentAnimation.isOver(false)){
					currentAnimation.reset();
					currentAnimation = animations.get("running start");
					currentAnimation.setFrameDuration(FRAME_DURATION);
				}
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
			this.setMoveSpeed(0);
//			if(currentState != PlayerState.MOVE){
//				this.currentAnimation = animations.get("running");
//				this.currentAnimation.setFrameDuration(4);
//				this.currentState = PlayerState.MOVE;
//				this.setMoveSpeed(15);
//			}
		} else if(key_pressed == keys_mapped.get(Key.LEFT)){
			currentState = PlayerState.MOVE;
			this.setMoveSpeed(0);
//			if(currentState != PlayerState.MOVE){
//				this.currentAnimation = animations.get("running");
//				this.currentAnimation.setFrameDuration(4);
//				this.currentState = PlayerState.MOVE;
//				this.setMoveSpeed(-15);
//			}
		} else if(key_pressed == keys_mapped.get(Key.DOWN)){
			
		} else if(key_pressed == keys_mapped.get(Key.SHIFT)){
			shift_pressed = true;
		}
	}
	
	public void manageKeyReleased(int key_released, Hashtable<String,Integer> keys_mapped){
		if(key_released == keys_mapped.get(Key.UP)){
			
		} else if(key_released == keys_mapped.get(Key.RIGHT)){
			this.currentState = PlayerState.IDLE;
//			if(this.currentState == PlayerState.MOVE){
//				this.currentAnimation = animations.get("idle");
//				this.setMoveSpeed(0);
//				this.currentState = PlayerState.IDLE;
//			}
			
		} else if(key_released == keys_mapped.get(Key.LEFT)){
			this.currentState = PlayerState.IDLE;
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
