package entities;

import framework.Loader;
import kuusisto.tinysound.Sound;

public class Door extends Entity{
	
	private int id;
	private Sound opening;
	private Sound closing;
	
	public Door(int x, int y, int x_offset, int y_offset, Loader loader, String door_state, int id, int frame) {
		super("Door", x + x_offset, y + y_offset, loader);
		this.id = id;
		animations = loader.getAnimations("door");
		currentAnimation = animations.get(door_state);
		currentAnimation.setCurrentFrame(frame);
		this.opening = loader.getSound("gate opening");
		this.closing = loader.getSound("gate closing");
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	@Override
	public void update(long elapsedTime){
		super.update(elapsedTime);
		if(currentAnimation.getId().equals("door_opening")){
			opening.play();
		} else if(currentAnimation.getId().equals("door_closing")){
			closing.play();
		}
		
	}

}
