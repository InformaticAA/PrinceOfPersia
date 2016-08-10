package entities;

import java.awt.Graphics;
import java.awt.Rectangle;

import framework.Loader;
import kuusisto.tinysound.Sound;

public class SpikeFloor extends Entity {
	
	private boolean activated;
	private Rectangle baseBoundingBox;
	private Spike spike_background;
	private Spike spike_foreground;
	private Player p;
	private Sound spikes_sound;

	public SpikeFloor(int x, int y, int x_offset, int y_offset, Loader loader, Spike back, Spike fore) {
		super("SpikeFloor", x+x_offset, y+y_offset, loader);
		activated = false;
		animations = loader.getAnimations("spike_floor");
		currentAnimation = animations.get("spike_floor");
		currentAnimation.setFrameDuration(4);
		this.spike_background = back;
		this.spike_foreground = fore;
		this.spikes_sound = loader.getSound("spikes");
		
		
		/* Sets the bounding box */
		enableBoundingBox(this.x + 25, 
					this.y + currentAnimation.getImage().getHeight()/2 - 4,
					currentAnimation.getImage().getWidth() - 25,
					currentAnimation.getImage().getHeight()/2 + 4);
		
		baseBoundingBox = new Rectangle(this.x, 
				this.y + 2*currentAnimation.getImage().getHeight()/3 + 5,
				currentAnimation.getImage().getWidth(),
				currentAnimation.getImage().getHeight()/3 - 4);
		
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
	public void activate(Player p) {
		this.activated = true;
		this.p = p;
		spikes_sound.play();
		this.spike_background.setCurrentAnimation("opening", 2);
		this.spike_foreground.setCurrentAnimation("opening", 2);
	}
	
	public void deactivate(){
		this.activated = false;
		this.p = null;
		this.spike_background.setCurrentAnimation("closing", 2);
		this.spike_foreground.setCurrentAnimation("closing", 2);
	}
	
	@Override
	public void update(long elapsedTime){
		super.update(elapsedTime);
		this.spike_background.updateReal(elapsedTime);
		this.spike_foreground.updateReal(elapsedTime);
		if(p != null){
			if(p.getBoundingBox().getMinX() > (this.getBoundingBox().getMaxX()) || 
					p.getBoundingBox().getMaxX() < (this.getBoundingBox().getMinX())){
				this.deactivate();
			}
		}
	}
	
	@Override
	public void setCurrentAnimation(String newAnimation, int frameDuration){
		super.setCurrentAnimation(newAnimation, frameDuration);
	}
	
	@Override
	public void drawSelf(Graphics g){
		super.drawSelf(g);
		
//		g.setColor(Color.RED);
//		g.drawRect((int) baseBoundingBox.getX() - currentAnimation.getImage().getWidth(),
//				(int) baseBoundingBox.getY() - currentAnimation.getImage().getHeight(),
//				(int) baseBoundingBox.getWidth(),
//				(int) baseBoundingBox.getHeight());
//		g.setColor(Color.BLACK);
		
	}

	@Override
	public Entity copy() {
		return null;
	}

	public Spike getSpike_background() {
		return spike_background;
	}

	public void setSpike_background(Spike spike_background) {
		this.spike_background = spike_background;
	}

	public Spike getSpike_foreground() {
		return spike_foreground;
	}

	public void setSpike_foreground(Spike spike_foreground) {
		this.spike_foreground = spike_foreground;
	}
	
	
}
