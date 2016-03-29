package entities;

import framework.Loader;

public class Corner extends Entity{

	public Corner(int x, int y, int x_offset, int y_offset, Loader loader, String corner_type) {
		super("Corner", x + x_offset, y + y_offset, loader);
		animations = loader.getAnimations("floor_panels");
		currentAnimation = animations.get(corner_type);
		
		/* Sets the bounding box */
		if(corner_type.equals("left")){
			enableBoundingBox(this.x + 25, this.y + currentAnimation.getImage().getHeight()/2 - 1,
						currentAnimation.getImage().getWidth() - 25,
						currentAnimation.getImage().getHeight()/2);
		} else if(corner_type.equals("normal_right")){
			enableBoundingBox(this.x, this.y + currentAnimation.getImage().getHeight()/2 - 3,
					currentAnimation.getImage().getWidth() - 27,
					currentAnimation.getImage().getHeight()/2);
		}
	}

	@Override
	public Entity copy() {
		return null;
	}

}
