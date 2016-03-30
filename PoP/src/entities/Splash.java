package entities;

import java.awt.Graphics;

import framework.Loader;

public class Splash extends Entity{
	
	private boolean visible;

	public Splash(int x, int y, int x_offset, int y_offset, Loader loader, String colour) {
		super("Splash", x + x_offset, y + y_offset, loader);
		animations = loader.getAnimations("splash");
		currentAnimation = animations.get(colour);
		this.visible = false;
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
