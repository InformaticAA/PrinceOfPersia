package entities;

import framework.Loader;

public class Wall extends Entity{

	public Wall(int x, int y, int x_offset, int y_offset, Loader loader, String wall_type) {
		super("Wall_" + wall_type, x + x_offset, y + y_offset, loader);
		animations = loader.getAnimations("wall");
		currentAnimation = animations.get(wall_type);
		
		/* Sets the bounding box */
		if (wall_type.equals("left_stack_main") || wall_type.equals("single_stack_main")) {
			enableBoundingBox(this.x + currentAnimation.getImage().getWidth()/2,
						this.y,
						currentAnimation.getImage().getWidth()/2,
						currentAnimation.getImage().getHeight());
		} else if (wall_type.equals("face_stack_main")) {
			enableBoundingBox(this.x, this.y,
						currentAnimation.getImage().getWidth()/2,
						currentAnimation.getImage().getHeight());
		} else {
			boundingBox = null;
		}
		
	}

	@Override
	public Entity copy() {
		return null;
	}

}
