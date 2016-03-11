package entities;

import framework.Loader;

public class Wall extends Entity{

	public Wall(int x, int y, int x_offset, int y_offset, Loader loader, String wall_type) {
		super("Wall", x + x_offset, y + y_offset, loader);
		animations = loader.getAnimations("wall");
		currentAnimation = animations.get(wall_type);
		
		/* Sets the bounding box */
		if (wall_type.equals("left_stack_main")) {
			enableBoundingBox(x + currentAnimation.getImage().getWidth()/2,
						y,
						currentAnimation.getImage().getWidth()/2,
						currentAnimation.getImage().getHeight());
		} else if (wall_type.equals("face_stack_main")) {
			enableBoundingBox(x, y,
						currentAnimation.getImage().getWidth()/2,
						currentAnimation.getImage().getHeight());
		} else if (wall_type.equals("centre_stack_main")) {
			enableBoundingBox(x, y,
						currentAnimation.getImage().getWidth(),
						currentAnimation.getImage().getHeight());
		} else {
			boundingBox = null;
		}
		
	}

}
