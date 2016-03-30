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
	private boolean isOver;
	
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
		this.isOver = false;
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
		animTime += elapsedTime;
		if (frames.size() > 1) {
			if (animTime >= totalDuration) {
				animTime = animTime % totalDuration;
				currentFrame = initialFrame;
				animTime = 0;
				currentUpdates = 0;
				isOver = true;
			}
			else {
				
				if (currentUpdates == frameDuration - 1) {
					currentFrame = (currentFrame + 1) % frames.size();
					currentUpdates = 0;
					isOver = false;
				}
				else {
					currentUpdates++;
					isOver = false;
				}
			}
		} else{
			if(animTime >= totalDuration){
				isOver = true;
				animTime = 0;
			}
		}
	}
	
//	public void update(long elapsedTime, boolean reverse) {
//		System.out.println("hola");
//		if (frames.size() > 1) {
//			animTime += elapsedTime;
//			
//			if (!reverse) {
//				if (animTime >= totalDuration) {
//					animTime = animTime % totalDuration;
//					currentFrame = initialFrame;
//				}
//	
//				if (currentFrame < frames.size() - 1) {
//					currentFrame= (currentFrame + 1) % frames.size();
//				}
//			}
//			else {
//				
//				/* Inverse animation */
//				if (animTime >= totalDuration) {
//					animTime = animTime % totalDuration;
//					currentFrame = frames.size() - 1;
//				}
//	
//				if (currentFrame > 0) {
//					currentFrame--;
//				}
//			}
//			
//		}
//	}

	public boolean isOver(boolean reverse) {
		return isOver;
		
		
//		boolean over = false;
//		
//		if (!reverse) {
//			System.out.println("Anim time " +  animTime + " -Total duration- " + totalDuration +  " -Frame duration- " + this.frameDuration );
////			over = this.isOver && this.currentFrame == 0;
//			return isOver;
//		}
//		else {
//			over = (currentFrame == 0);
//		}
//		
//		return over;
	}
	
	public String getId() {
		return id;
	}
	
	public int getCurrentFrame() {
		return currentFrame;
	}
	
	public void setCurrentFrame(int numFrame) {
		animTime = animTime + numFrame * frameDuration;
		this.currentFrame = numFrame;
	}
	
	public void setRandomCurrentFrame() {
		int randomFrame = (int) (Math.random() * frames.size());
		currentFrame = randomFrame;
		initialFrame = currentFrame;
	}
	
	public void setFrameDuration(int frameDuration) {
		this.totalDuration = (long) (frameDuration * totalDuration);
		this.frameDuration = frameDuration;
	}
	
	public void reset() {
		currentUpdates = 0;
		currentFrame = initialFrame;
		frameDuration = 1;
		animTime = 0;
		totalDuration = 0;
		this.isOver = false;
		for (int i = 0; i < frames.size(); i++) {
			totalDuration = totalDuration + frames.get(i).getEndtime();
		}
	}
	
	public long getAnimTime() {
		return animTime;
	}
	
	public long getTotalDuration() {
		return totalDuration;
	}
	
	public int getFrameDuration() {
		return frameDuration;
	}

}
