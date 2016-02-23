package data;

import java.util.Hashtable;

import framework.Animation;

public class Animations {

	private Hashtable<String, Hashtable<String,Animation>> animations;
	
	public Animations() {
		animations = new Hashtable<String, Hashtable<String,Animation>>();
	}

	public Hashtable<String, Hashtable<String, Animation>> getAllAnimations() {
		return animations;
	}

	public void setAllAnimations(Hashtable<String, Hashtable<String, Animation>> animations) {
		this.animations = animations;
	}
	
	public Hashtable<String, Animation> getAnimations(String animationName) {
		return animations.get(animationName);				
	}
	
}
