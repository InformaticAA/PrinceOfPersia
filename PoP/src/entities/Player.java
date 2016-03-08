package entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import framework.Loader;

public class Player extends Character {

	private enum PlayerState {IDLE, MOVE, JUMP, COMBAT};
	private PlayerState currentState;
	
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
		super.update(elapsedTime);
		
		currentAnimation.update(elapsedTime);

		switch (currentState) {
		case IDLE:
			
			currentAnimation = animations.get("idle");
			
			break;
		default:
			break;
		}
	}
	
	@Override
	public void drawSelf(Graphics g) {
		super.drawSelf(g);
		
		/* Draws player's bounding box */
		g.setColor(Color.RED);
		int width = currentAnimation.getImage().getWidth();
		int height = currentAnimation.getImage().getHeight();
		g.drawRect(x - width, y - height, boundingBox.width, boundingBox.height);
		g.setColor(Color.BLACK);
	}
}
