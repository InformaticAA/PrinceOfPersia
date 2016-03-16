package entities;

import framework.Loader;

public class Torch extends Entity {

	public Torch(int x, int y, Loader loader, boolean menu) {
		super("Torch", x, y, loader);
		if(!menu){
			this.y = y-53;
		}
		animations = loader.getAnimations("torch");
		this.setCurrentAnimation("fire", FRAME_DURATION);
		currentAnimation.setRandomCurrentFrame();
	}

	@Override
	public Entity copy() {
		return null;
	}
	
}
