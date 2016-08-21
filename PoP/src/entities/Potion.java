package entities;

import framework.Loader;

public class Potion extends Entity {

	public Potion(int x, int y, int x_offset, int y_offset, Loader loader, String potion_type) {
		super("Potion_" + potion_type,  x + x_offset, y + y_offset, loader);
		animations = loader.getAnimations("good potion");
		this.setCurrentAnimation("good potion", FRAME_DURATION);
		currentAnimation.setRandomCurrentFrame();
	}

	@Override
	public Entity copy() {
		return null;
	}
	
}
