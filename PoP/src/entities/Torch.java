package entities;

import framework.Loader;

public class Torch extends Entity {

	public Torch(int x, int y, boolean back,
			Loader loader) {
		super(x, y, back, loader);
		
		animations = loader.getAnimations("torch");
		currentAnimation = animations.get("fire");
		currentAnimation.setRandomCurrentFrame();
	}
}
