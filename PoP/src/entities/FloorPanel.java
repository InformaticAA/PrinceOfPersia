package entities;

import framework.Loader;

public class FloorPanel extends Entity{

	public FloorPanel(int x, int y, int x_offset, int y_offset, Loader loader, String floor_type) {
		super("FloorPanel_" + floor_type, x + x_offset, y + y_offset, loader);
		animations = loader.getAnimations("floor_panels");
		currentAnimation = animations.get(floor_type);
		
		/* Sets the bounding box */
		if(floor_type.equals("normal_left")){
			enableBoundingBox(this.x + 25, this.y + currentAnimation.getImage().getHeight()/2 - 1,
						currentAnimation.getImage().getWidth() - 25,
						currentAnimation.getImage().getHeight()/2);
		} else if(floor_type.equals("broken_left")){
			enableBoundingBox(this.x + 25, this.y + currentAnimation.getImage().getHeight()/2 - 1,
					currentAnimation.getImage().getWidth() - 25,
					currentAnimation.getImage().getHeight()/2);
			
		} else if(floor_type.equals("normal_right")){
			enableBoundingBox(this.x, this.y + currentAnimation.getImage().getHeight()/2 - 3,
					currentAnimation.getImage().getWidth() - 27,
					currentAnimation.getImage().getHeight()/2);
		} else if(floor_type.equals("broken_left")){
			enableBoundingBox(this.x, this.y + currentAnimation.getImage().getHeight()/2 - 2,
					currentAnimation.getImage().getWidth() - 27,
					currentAnimation.getImage().getHeight()/2);
		}
	}

	@Override
	public Entity copy() {
		return null;
	}

}
