package data;

import java.util.Hashtable;

import framework.Animation;

public class Animations {

	private Hashtable<String, Hashtable<String, Animation>> animations;
	
	public Animations() {
		this.animations = new Hashtable<String, Hashtable<String,Animation>>();
	}
	
	/**
	 * 
	 * @param entityName
	 * @param entityAnimations
	 */
	public void addEntityAnimations(String entityName, Hashtable<String, Animation> entityAnimations) {
		this.animations.put(entityName, entityAnimations);
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
