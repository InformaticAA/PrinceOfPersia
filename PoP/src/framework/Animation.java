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
	private long animTime;
	private long totalDuration;
	private boolean isOver;
	private boolean lastFrame;
	private boolean infinite;
	
	public Animation(String id, ArrayList<Frame> frames) {
		this.id = id;
		this.frames = frames;
		
		this.currentUpdates = 0;
		this.currentFrame = 0;
		this.frameDuration = 1;
		this.initialFrame = 0;
		this.animTime = 0;
		this.totalDuration = 0;
		this.isOver = false;
		this.lastFrame = true;
		
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
		
		if (frames.size() == 1 && frames.get(0).isInfinite()) {
			lastFrame = false;
			isOver = true;
		}
		else if (frames.size() > 1) {
			animTime += elapsedTime;
			
			if (animTime >= totalDuration) {
				animTime = animTime % totalDuration;
				currentFrame = initialFrame;
				animTime = 0;
				currentUpdates = 0;
				isOver = true;
				lastFrame = true;
			}
			else {
				
				if (currentUpdates == frameDuration - 1) {
					currentFrame = (currentFrame + 1) % frames.size();
					currentUpdates = 0;
					isOver = false;
					lastFrame = true;
				}
				else {
					currentUpdates++;
					isOver = false;
					lastFrame = false;
				}
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
	
	public int getFrameXSpeed(int idFrame, BufferedImage prevImage) {
		int currWidth2 = getFrame(idFrame).getImage().getWidth()/2;
		int prevWidth2 = prevImage.getWidth()/2;
		int newxSpeed = frames.get(currentFrame).getxSpeed();
		int res;
		
		res = newxSpeed - prevWidth2 + currWidth2;
		
//		System.out.println("xs: " + res + " --> nxS: " + newxSpeed +
//				", cW2: " + currWidth2 + ", pW2: " + prevWidth2);
		
		return res;
	}
	
	public int getFrameYSpeed(int idFrame, BufferedImage prevImage) {
		int currHeight2 = getFrame(idFrame).getImage().getHeight()/2;
		int prevHeight2 = prevImage.getHeight()/2;
		int newySpeed = frames.get(currentFrame).getySpeed();
		
		return newySpeed - prevHeight2 + currHeight2;
	}
	
	public int getFrameXOffset(int idFrame, BufferedImage prevImage) {
		int currWidth2 = getFrame(idFrame).getImage().getWidth()/2;
		int prevWidth2 = prevImage.getWidth()/2;
		int newxOffset = frames.get(currentFrame).getxOffset();
		int res = newxOffset - prevWidth2 + currWidth2;
		
		return res;
	}
	
	public int getFrameYOffset(int idFrame, BufferedImage prevImage) {
		int currHeight2 = getFrame(idFrame).getImage().getHeight()/2;
		int prevHeight2 = prevImage.getHeight()/2;
		int newyOffset = frames.get(currentFrame).getyOffset();
		
		return newyOffset - prevHeight2 + currHeight2;
	}

	/**
	 * @return true if the animation is currently at the last frame
	 * of one sprite
	 */
	public boolean isLastFrame() {
		return lastFrame;
	}

	/**
	 * @return the infinite
	 */
	public boolean isInfinite() {
		return infinite;
	}

	/**
	 * @param infinite the infinite to set
	 */
	public void setInfinite(boolean infinite) {
		this.infinite = infinite;
	}

}
