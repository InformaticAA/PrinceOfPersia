package entities;

import java.awt.Rectangle;

import framework.Loader;

public class Player extends Character {

	private enum PlayerState {IDLE, MOVE, JUMP, COMBAT};
	private PlayerState currentState;
	
	public Player(int x, int y, Loader loader) {
		super(x, y, loader);
		animations = loader.getAnimations("Dastan");
		
		currentAnimation = animations.get("running");
		currentAnimation.setFrameDuration(3);
		
		currentState = PlayerState.IDLE;
		
		boundingBox = new Rectangle(x,y,currentAnimation.getImage().getWidth(),
				currentAnimation.getImage().getHeight());
		
	}
	
	@Override
	public void update(long elapsedTime) {
		super.update(elapsedTime);
		
		currentAnimation.update(elapsedTime);

		switch (currentState) {
		case IDLE:
			
//			currentAnimation = animations.get("running");
//			System.out.println("x: " + x + ", y: " + y);
//			System.out.println("x_draw: " + x_draw + ", y_draw: " + y_draw);
			
			break;
		default:
			break;
		}
	}

}
