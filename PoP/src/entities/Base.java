package entities;

import framework.Loader;

public class Base extends Entity{
	
	public Base(int x, int y, Loader loader, String base_type) {
		super(x, y, loader);
		animations = loader.getAnimations("bases");
		currentAnimation = animations.get(base_type);
	}
}
