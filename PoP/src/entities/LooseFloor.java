package entities;

import framework.Loader;

public class LooseFloor extends Entity {

	public LooseFloor(int x, int y, int x_offset, int y_offset, Loader loader, boolean activated, String loose_type) {
		super(x+x_offset, y+y_offset, loader);
	}
	
}
