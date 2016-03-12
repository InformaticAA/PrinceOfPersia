package entities;

import framework.Loader;

public class Pillar extends Entity{

	public Pillar(int x, int y, int x_offset, int y_offset, Loader loader, String type) {
		super("Pillar", x + x_offset, y + y_offset, loader);
		animations = loader.getAnimations("pillar");
		currentAnimation = animations.get(type);
		
		/* Sets the bounding box */
		if(type.equals("pillar_left")){
			enableBoundingBox(this.x + 25, this.y + currentAnimation.getImage().getHeight() - 14,
						currentAnimation.getImage().getWidth() - 25,
						13);
		} else if(type.equals("pillar_right_main")){
			enableBoundingBox(this.x, this.y + currentAnimation.getImage().getHeight() - 18,
					currentAnimation.getImage().getWidth() -27,
					17);
		}
	}

}
