package entities;

import framework.Loader;

public class SwordFighting extends Entity{

	public SwordFighting(int x, int y, int x_offset, int y_offset, Loader loader, String animation, String type) {
		super("Sword Fighting", x + x_offset, y + y_offset, loader);
		animations = loader.getAnimations("sword_" + type);
		currentAnimation = animations.get(animation);
		
		/* Sets the bounding box */
		enableBoundingBox();
	}
	
	public void setCurrentAnimation(String animationName, int frameDuration,int frame, Integer x_position, Integer y_position){
		super.setCurrentAnimation(animationName, frameDuration);
		if(x_position != null){
			this.x = x_position;
		}
		if(y_position != null){
			this.y = y_position;
		}
		this.getCurrentAnimation().setCurrentFrame(frame);
	}

	@Override
	public Entity copy() {
		return null;
	}
}
