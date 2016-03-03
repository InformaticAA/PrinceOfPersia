package entities;

import framework.Loader;

public class Torch extends Entity {

	public Torch(int x, int y, Loader loader) {
		super(x, y, loader);
		
		animations = loader.getAnimations("torch");
		currentAnimation = animations.get("fire");
		currentAnimation.setRandomCurrentFrame();
	}
}
