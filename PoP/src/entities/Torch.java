package entities;

import java.util.Hashtable;

import framework.Animation;

public class Torch extends Entity {

	public Torch(int x, int y, boolean back,
			Hashtable<String,Animation> animations) {
		super(x, y, back, animations);
		
		currentAnimation = animations.get("fire");
		currentAnimation.setRandomCurrentFrame();
	}
	
	@Override
	public void update(long elapsedTime) {
		currentAnimation.update(elapsedTime);
		
		System.out.print(currentAnimation.getCurrentFrame() + " ");
	}
	
}
