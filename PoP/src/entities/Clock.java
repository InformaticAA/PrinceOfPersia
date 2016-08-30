package entities;

import framework.Loader;

public class Clock extends Entity {

	public Clock(int x, int y, Loader loader) {
		super("Clock", x, y, loader);
		animations = loader.getAnimations("clock");
		this.setCurrentAnimation("running", FRAME_DURATION);
	}

	@Override
	public Entity copy() {
		return null;
	}
	
}
