package entities;

import framework.Loader;

public class Pillar extends Entity{

	public Pillar(int x, int y, int x_offset, int y_offset, Loader loader, String type) {
		super(x + x_offset, y + y_offset, loader);
		animations = loader.getAnimations("pillar");
		currentAnimation = animations.get(type);
	}

}
