package entities;

import framework.Loader;

public class Wall extends Entity{

	public Wall(int x, int y, Loader loader, String wall_type) {
		super(x, y - 4, loader);
		animations = loader.getAnimations("wall");
		currentAnimation = animations.get(wall_type);
		
	}

}
