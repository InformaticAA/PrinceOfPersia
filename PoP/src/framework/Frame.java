package framework;

import java.awt.Image;

public class Frame {

	private Image image;
	private long endtime;
	
	public Frame(Image image, long endtime) {
		super();
		this.image = image;
		this.endtime = endtime;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public long getEndtime() {
		return endtime;
	}

	public void setEndtime(long endtime) {
		this.endtime = endtime;
	}
}
