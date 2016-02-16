package framework;

import java.applet.Applet;
import java.awt.Image;
import java.io.File;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class Loader {

	private long frameTime;
	private Applet applet;
	private URL base;
	
	public Loader(Applet applet, long frameTime) {
		this.applet = applet;
		this.frameTime = frameTime;
		this.base = applet.getDocumentBase();
	}
	
	public Set<Animation> loadCharacterAnimations(String characterPath) {
		Set<Animation> animations = new HashSet<Animation>();
		
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
	
	private Animation loadAnimation(File f) {
		Animation animation = new Animation(f.getName());
		
		File[] images = f.listFiles();
		for(File image : images) {
			
			String name = image.getName();
			if (name.substring(name.length() - 4, name.length()).equals(".png")) {
				
				String path = image.getAbsolutePath();
				String[] paths = path.split("data");
				String finalPath = "../data" + paths[1];
				
				Image img = applet.getImage(base, finalPath);
				animation.addFrame(img, frameTime);
			}
		}
		
		return animation;
	}
	
}
