package entities;

import java.awt.Graphics;

import framework.Loader;

public class Life extends Entity{
	
	private boolean visible;

	public Life(int x, int y, int x_offset, int y_offset, Loader loader, String type) {
		super("Life " + type, x + x_offset, y + y_offset, loader);
		animations = loader.getAnimations("hit points");
		currentAnimation = animations.get(type);
		this.visible = true;
	}

	@Override
	public void drawSelf(Graphics g) {
		if(visible){
			super.drawSelf(g);
		}
	}
	
	public void setVisible(boolean visible){
		this.visible = visible;
	}
	
	public boolean isVisible(){
		return this.visible;
	}
	
	@Override
	public Entity copy() {
		return null;
	}

}
