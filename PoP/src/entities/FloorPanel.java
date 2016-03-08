package entities;

import framework.Loader;

public class FloorPanel extends Entity{

	public FloorPanel(int x, int y, int x_offset, int y_offset, Loader loader, String floor_type) {
		super(x + x_offset, y + y_offset, loader);
		animations = loader.getAnimations("floor_panels");
		currentAnimation = animations.get(floor_type);
	}

}
