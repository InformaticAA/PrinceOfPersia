package entities;

import framework.Loader;
import kuusisto.tinysound.Sound;

public class Door extends Entity{
	
	private int id;
	private Sound opening;
	private Sound closing;
	private Sound closing_fast;
	private Sound closed;
	private long remaining_time;
	
	public final int MAX_TIME = 30;
	
	public Door(int x, int y, int x_offset, int y_offset, Loader loader, String door_state, int id, int frame) {
		super("Door", x + x_offset, y + y_offset, loader);
		this.id = id;
		animations = loader.getAnimations("door");
		this.setCurrentAnimation(door_state, FRAME_DURATION*6);
		currentAnimation.setCurrentFrame(frame);
		opening = loader.getSound("gate opening");
		closing = loader.getSound("gate closing");
		closing_fast = loader.getSound("gate closing_fast");
		closed = loader.getSound("gate stop");
		remaining_time = -1;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	@Override
	public void update(long elapsedTime){
		
	}
	
	public void updateReal(long elapsedTime){
		super.update(elapsedTime);
		switch(currentAnimation.getId()){
		
		case "door_opened":
			remaining_time = remaining_time - elapsedTime;
			if(remaining_time != -1 && remaining_time < 0){
				remaining_time = -1;
				this.setCurrentAnimation("door_closing", FRAME_DURATION * 6);
			}
			break;
		
		case "door_closed":
			
			break;
		
		case "door_half_opening":
			if(this.currentAnimation.isLastFrame()){
				opening.play();
			}
			if(this.getCurrentAnimation().isOver(false)){
				this.setCurrentAnimation("door_opening", FRAME_DURATION*3);
			}
			break;
			
		case "door_opening":
			if(this.currentAnimation.isLastFrame()){
				opening.play();
			}
			if(this.getCurrentAnimation().isOver(false)){
				this.setCurrentAnimation("door_opened", FRAME_DURATION*3);
				remaining_time = MAX_TIME;
			}
			break;
		
		case "door_closing":
			if(this.currentAnimation.isLastFrame()){
				closing.play();
			}
			if(this.getCurrentAnimation().isOver(false)){
				this.setCurrentAnimation("door_half_closing", FRAME_DURATION*6);
			}
			break;
			
		case "door_half_closing":
			if(this.currentAnimation.isLastFrame()){
				closing.play();
			}
			if(this.getCurrentAnimation().isOver(false)){
				closed.play();
				this.setCurrentAnimation("door_closed", FRAME_DURATION*3);
			}
			break;
			
		default:
			
			break;
		}
	}

	@Override
	public Entity copy() {
		return null;
	}
	
	public void openDoor(){
		int frame = 0;
		switch(currentAnimation.getId()){
		
		case "door_opened":
			this.remaining_time = MAX_TIME;
			break;
			
		case "door_closed":
			this.setCurrentAnimation("door_half_opening", FRAME_DURATION * 3);
			
			break;
			
		case "door_closing":
			frame = this.getCurrentAnimation().getCurrentFrame();
			this.setCurrentAnimation("door_opening", FRAME_DURATION * 3);
			this.getCurrentAnimation().setCurrentFrame(4 - frame/2);
			break;
			
		case "door_half_closing":
			frame = this.getCurrentAnimation().getCurrentFrame();
			this.setCurrentAnimation("door_half_opening", FRAME_DURATION * 3);
			this.getCurrentAnimation().setCurrentFrame(5 - frame/2);
			break;
			
		default:
			
			break;
		}
	}
	
	public void closeDoor(){
		if(!this.getCurrentAnimation().getId().equals("door_closed")){
			this.setCurrentAnimation("door_closed", FRAME_DURATION);
			closing_fast.play();
		}
	}
}
