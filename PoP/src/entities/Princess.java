package entities;

import framework.Loader;

public class Princess extends Entity {

	public Princess(int x, int y, Loader loader) {
		super("Princess", x, y, loader);
		animations = loader.getAnimations("Princess");
		this.setCurrentAnimation("waiting", FRAME_DURATION);
	}

	@Override
	public Entity copy() {
		return null;
	}
	
}
