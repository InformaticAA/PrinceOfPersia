package framework;

import java.awt.Image;
import java.util.ArrayList;

public class Animation {

	private ArrayList<Frame> frames;
	private int currentFrame;
	private long animTime;
	private long totalDuration;
	
	public Animation() {
		frames = new ArrayList<Frame>();
		currentFrame = 0;
		animTime = 0;
		totalDuration = 0;
	}
	
	public void addFrame(Image frame, long duration) {
		totalDuration += duration;
		frames.add(new Frame(frame, totalDuration));
	}
	
	public Image getImage() {
		return frames.get(currentFrame).getImage();
	}
	
	public Frame getFrame(int i) {
		return frames.get(i);
	}
	
	public void update(long elapsedTime) {
		if (frames.size() > 1) {
			animTime += elapsedTime;
			
			if (animTime >= totalDuration) {
				animTime = animTime % totalDuration;
				currentFrame = 0;
			}
			
			while (animTime > getFrame(currentFrame).getEndtime()) {
				currentFrame++;
			}
			
		}
	}
}
