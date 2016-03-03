package framework;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Animation {

	private String id;
	private ArrayList<Frame> frames;
	private int currentFrame;
	private int initialFrame;
	private boolean infinite;
	private long animTime;
	private long totalDuration;
	
	public Animation(String id, ArrayList<Frame> frames, boolean infinite) {
		this.id = id;
		this.frames = frames;
		currentFrame = 0;
		initialFrame = 0;
		this.infinite = infinite;
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
			}
			currentFrame= (currentFrame + 1) % frames.size();
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

}
