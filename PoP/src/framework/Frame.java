package framework;

import java.awt.image.BufferedImage;

public class Frame {

	private BufferedImage image;
	private long endtime;
	
	private int xSpeed;
	private int ySpeed;
	private int xOffset;
	private int yOffset;
	private boolean infinite;
	
	public Frame(BufferedImage image, long endtime) {
		super();
		this.image = image;
		this.endtime = endtime;
		
		this.xSpeed = 0;
		this.ySpeed = 0;
		this.xOffset = 0;
		this.xOffset = 0;
	}

	public BufferedImage getImage() {
		return image;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	public long getEndtime() {
		return endtime;
	}

	public void setEndtime(long endtime) {
		this.endtime = endtime;
	}

	/**
	 * @return the xSpeed
	 */
	public int getxSpeed() {
		return xSpeed;
	}

	/**
	 * @param xSpeed the xSpeed to set
	 */
	public void setxSpeed(int xSpeed) {
		this.xSpeed = xSpeed;
	}

	/**
	 * @return the ySpeed
	 */
	public int getySpeed() {
		return ySpeed;
	}

	/**
	 * @param ySpeed the ySpeed to set
	 */
	public void setySpeed(int ySpeed) {
		this.ySpeed = ySpeed;
	}

	/**
	 * @return the xOffset
	 */
	public int getxOffset() {
		return xOffset;
	}

	/**
	 * @param xOffset the xOffset to set
	 */
	public void setxOffset(int xOffset) {
		this.xOffset = xOffset;
	}

	/**
	 * @return the yOffset
	 */
	public int getyOffset() {
		return yOffset;
	}

	/**
	 * @param yOffset the yOffset to set
	 */
	public void setyOffset(int yOffset) {
		this.yOffset = yOffset;
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
