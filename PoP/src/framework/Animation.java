package framework;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Animation {

	private String id;
	private ArrayList<Frame> frames;
	private int currentFrame;
	private boolean infinite;
	private long animTime;
	private long totalDuration;
	
	public Animation(String id, boolean infinite) {
		this.id = id;
		frames = new ArrayList<Frame>();
		currentFrame = 0;
		this.infinite = infinite;
		animTime = 0;
		totalDuration = 0;
	}
	
	public void addFrame(Frame frame, long duration) {
		totalDuration += duration;
		frames.add(frame);
	}
	
	public BufferedImage getImage() {
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

			if (currentFrame < frames.size() - 1) {
				currentFrame++;
			}
		}
	}

	public boolean isOver() {
		return (currentFrame == frames.size() - 1) && !infinite;
	}
	
	public String getId() {
		return id;
	}
	
	public int getCurrentFrame() {
		return currentFrame;
	}

}
