package data;

import java.util.ArrayList;

import framework.Frame;

public class FrameList {

	private String id;
	private ArrayList<Frame> frames;
	
	public FrameList(String id) {
		this.id = id;
		frames = new ArrayList<Frame>();
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the frames
	 */
	public ArrayList<Frame> getFrames() {
		return frames;
	}

	/**
	 * @param frames the frames to set
	 */
	public void setFrames(ArrayList<Frame> frames) {
		this.frames = frames;
	}
	
	/**
	 * @param f the frame to add to the frame list
	 */
	public void addFrame(Frame f){
		this.frames.add(f);
	}
	

}