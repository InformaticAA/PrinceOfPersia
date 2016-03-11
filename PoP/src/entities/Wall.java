package entities;

import framework.Loader;

public class Wall extends Entity{

	public Wall(int x, int y, int x_offset, int y_offset, Loader loader, String wall_type) {
		super("Wall", x + x_offset, y + y_offset, loader);
		animations = loader.getAnimations("wall");
		currentAnimation = animations.get(wall_type);
		
	}

}
