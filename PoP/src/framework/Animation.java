package framework;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Animation {

	private String id;
	private ArrayList<Frame> frames;
	private int currentFrame;
	private int initialFrame;
	private int currentUpdates;
	private int frameDuration;
	private boolean infinite;
	private long animTime;
	private long totalDuration;
	
	public Animation(String id, ArrayList<Frame> frames, boolean infinite) {
		this.id = id;
		this.frames = frames;
		this.infinite = infinite;
		currentUpdates = 0;
		currentFrame = 0;
		frameDuration = 1;
		initialFrame = 0;
		animTime = 0;
		totalDuration = 0;
		for (int i = 0; i < frames.size(); i++) {
			totalDuration = totalDuration + frames.get(i).getEndtime();
		}
	}
	
	public void addFrame(Frame frame, long duration) {
		totalDuration += duration;
		frames.add(frame);
		System.out.println("Frames added " + duration);
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
				currentFrame = initialFrame;
				currentUpdates = 0;
			}
			else {
				
				if (currentUpdates == frameDuration - 1) {
					currentFrame = (currentFrame + 1) % frames.size();
					currentUpdates = 0;
				}
				else {
					currentUpdates++;
				}
			}
			
		}
	}
	
	public void update(long elapsedTime, boolean reverse) {
		if (frames.size() > 1) {
			animTime += elapsedTime;
			
			if (!reverse) {
				
				if (animTime >= totalDuration) {
					animTime = animTime % totalDuration;
					currentFrame = initialFrame;
				}
	
				if (currentFrame < frames.size() - 1) {
					currentFrame= (currentFrame + 1) % frames.size();
				}
			}
			else {
				
				/* Inverse animation */
				if (animTime >= totalDuration) {
					animTime = animTime % totalDuration;
					currentFrame = frames.size() - 1;
				}
	
				if (currentFrame > 0) {
					currentFrame--;
				}
			}
			
		}
	}

	public boolean isOver(boolean reverse) {
		boolean over = false;
		
		if (!reverse) {
			over = (currentFrame == frames.size() - 1) && !infinite;
		}
		else {
			over = (currentFrame == 0) && !infinite;
		}
		
		return over;
	}
	
	public String getId() {
		return id;
	}
	
	public int getCurrentFrame() {
		return currentFrame;
	}
	
	public void setCurrentFrame(int numFrame) {
		this.currentFrame = numFrame;
	}
	
	public void setRandomCurrentFrame() {
		int randomFrame = (int) (Math.random() * frames.size());
		currentFrame = randomFrame;
		initialFrame = currentFrame;
	}
	
	public void setFrameDuration(int frameDuration) {
		this.totalDuration = frameDuration * totalDuration;
		this.frameDuration = frameDuration;
	}

}
