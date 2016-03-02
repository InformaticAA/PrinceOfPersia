package framework;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

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
	
	public void update(long elapsedTime, boolean reverse) {
		if (frames.size() > 1) {
			animTime += elapsedTime;
			
			if (!reverse) {
				
				if (animTime >= totalDuration) {
					animTime = animTime % totalDuration;
					currentFrame = 0;
				}
	
				if (currentFrame < frames.size() - 1) {
					currentFrame++;
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
		
		long newTime = 0;
		for (int i = 0; i < frames.size(); i++) {
			newTime += frames.get(i).getEndtime();
		}
		animTime = newTime;
	}

}
