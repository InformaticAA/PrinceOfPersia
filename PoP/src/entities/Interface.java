package entities;

import framework.Loader;

public class Interface extends Entity{

	public Interface(int x, int y, int x_offset, int y_offset, Loader loader) {
		super("Interface", x + x_offset, y + y_offset, loader);
		animations = loader.getAnimations("interface");
		this.setCurrentAnimation("interfaz", FRAME_DURATION);
	}
	
	@Override
	public Entity copy() {
		return null;
	}

}
