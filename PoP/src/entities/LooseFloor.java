package entities;

import framework.Loader;

public class LooseFloor extends Entity {
	
	private boolean activated;

	public LooseFloor(int x, int y, int x_offset, int y_offset, Loader loader, String loose_type) {
		super(x+x_offset, y+y_offset, loader);
		activated = false;
		animations = loader.getAnimations("loose_floor");
		currentAnimation = animations.get(loose_type);
		currentAnimation.setFrameDuration(4);
	}
	
	/**
	 * @return the activated
	 */
	public boolean isActivated() {
		return activated;
	}
	
	/**
	 * @param activated the activated to set
	 */
	public void setActivated(boolean activated) {
		this.activated = activated;
	}
	
	@Override
	public void update(long elapsedTime){
		super.update(elapsedTime);
		if(currentAnimation.getId().equals("shaking")){
			int sound = (int)(Math.random()*3 + 1);
			loader.getSound("tile moving " + sound).play();
		}
	}
}
