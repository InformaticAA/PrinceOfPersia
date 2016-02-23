package framework;

import java.awt.image.BufferedImage;

public class Frame {

	private BufferedImage image;
	private long endtime;
	
	public Frame(BufferedImage image, long endtime) {
		super();
		this.image = image;
		this.endtime = endtime;
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
}
