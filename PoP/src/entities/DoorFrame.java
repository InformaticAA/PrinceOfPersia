package entities;

import framework.Loader;

public class DoorFrame extends Entity{

	public DoorFrame(int x, int y, int x_offset, int y_offset, Loader loader, String frame_type) {
		super("DoorFrame_" + frame_type, x + x_offset, y + y_offset, loader);
		animations = loader.getAnimations("door_frame");
		currentAnimation = animations.get(frame_type);
	}

	@Override
	public Entity copy() {
		return null;
	}
}