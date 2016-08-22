package entities;

import framework.Loader;

public class SwordFloor extends Entity{
	
	public SwordFloor(int x, int y, int x_offset, int y_offset, Loader loader) {
		super("SwordFloor", x + x_offset, y + y_offset, loader);
		animations = loader.getAnimations("sword");
		currentAnimation = animations.get("normal");
		
		/* Sets the bounding box */
		enableBoundingBox(this.x,this.y,currentAnimation.getImage().getWidth(),
				currentAnimation.getImage().getHeight());
	}

	@Override
	public Entity copy() {
		return null;
	}
}
