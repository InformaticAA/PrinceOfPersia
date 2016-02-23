package framework;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;

public class Loader {

	private long frameTime;
	private String finalPath;
	private String firePath = "/Resources/Sprites_400/Objects/fire";
	
	public Loader(long frameTime) {
		this.frameTime = frameTime;
	}
	
	public Set<Animation> loadCharacterAnimations(String characterPath) {
		Set<Animation> animations = new HashSet<Animation>();
		
		characterPath = firePath;
		finalPath = characterPath;
		
		/* Searches for .png files for each folder of characterPath */
		File dir = new File(characterPath);
		if (dir.isDirectory()) {
			File[] files = dir.listFiles();
			
			if (files != null) {
				for(File f : files) {
					if (f.isDirectory()) {
						
						/* folder f contains .png files */
						Animation anim = loadAnimation(f);
						animations.add(anim);
					}
					else {
						
						/* f is a .png file */
						
					}
				}
			}
			
		}
		
		return animations;
	}
	
	/**
	 * 
	 * @param f directory containing all frames of
	 * one animation
	 * @return new animation loaded
	 */
	public Animation loadAnimation(File f) {
		Animation animation = new Animation(f.getName());
		
		System.out.println("Animation: " + animation.getId());
		
		File[] images = f.listFiles();
		for(File image : images) {
			
			/* Loads one image as a frame of the animation */
			String name = image.getName();
			if (name.substring(name.length() - 4, name.length()).equals(".png")) {
				
				System.out.println(name);
				
				Frame frame = loadFrame(image);
				animation.addFrame(frame, frameTime);
			}
		}
		
		return animation;
	}
	
	/**
	 * 
	 * @param image
	 * @return a new frame loaded from file image
	 */
	public Frame loadFrame(File image) {
		Frame frame = null;
		BufferedImage img = null;
		
		try{
			img = ImageIO.read(image);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		frame = new Frame(img, frameTime);
		return frame;
	}
	
}
