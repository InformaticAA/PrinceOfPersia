package entities;

import java.awt.Color;

import framework.Loader;
import kuusisto.tinysound.Sound;

public class Door extends Entity{
	
	private int id;
	private Sound opening;
	private Sound closing;
	private Sound closing_fast;
	private Sound closed;
	private long remaining_time;
	
	public final int MAX_TIME = 10 * 1000;
	
	public Door(int x, int y, int x_offset, int y_offset, Loader loader, String door_state, int id, int frame, String type) {
		super("Door_" + type, x + x_offset, y + y_offset, loader);
		this.id = id;
		animations = loader.getAnimations("door");
		this.setCurrentAnimation(door_state, FRAME_DURATION*6);
		currentAnimation.setCurrentFrame(frame);
		opening = loader.getSound("gate opening");
		closing = loader.getSound("gate closing");
		closing_fast = loader.getSound("gate closing fast");
		closed = loader.getSound("gate stop");
		remaining_time = -1000;
		
		boundingBoxColor = Color.GREEN;
		
		/* Sets the bounding box */
		if(door_state.contains("closed")){
			enableBoundingBox(this.x, this.y,
						currentAnimation.getImage().getWidth() - currentAnimation.getImage().getWidth()/2,
						currentAnimation.getImage().getHeight());
		}
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
//		System.out.println("ANIMACION " + this.getCurrentAnimation().getId() + " (" + this.getCurrentAnimation().getCurrentFrame() + ")");
		switch(currentAnimation.getId()){
		
		case "door_opened":
			if(remaining_time != -1000){
				remaining_time = remaining_time - elapsedTime;
				if(remaining_time != -1000 && remaining_time < 0){
					remaining_time = -1000;
					this.setCurrentAnimation("door_closing", FRAME_DURATION * 6);
				}
			} 
			break;
		
		case "door_closed":
			
			break;
		
		case "door_half_opening":
			if(this.currentAnimation.isLastFrame()){
				opening.play();
			}
			if(this.getCurrentAnimation().isOver(false)){
				this.setCurrentAnimation("door_opening", FRAME_DURATION*2);
			}
			break;
			
		case "door_opening":
			if(this.currentAnimation.isLastFrame()){
				opening.play();
			}
			if(this.getCurrentAnimation().isOver(false)){
				this.setCurrentAnimation("door_opened", FRAME_DURATION*2);
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
				this.setCurrentAnimation("door_closed", FRAME_DURATION);
			}
			break;
			
		case "final_door_opening":
			if(this.currentAnimation.isLastFrame()){
				opening.play();
			}
			if(this.getCurrentAnimation().isOver(false)){
				this.setCurrentAnimation("final_door_opened", FRAME_DURATION*2);
			}
			break;
			
		default:
			
			break;
		}
		
		// updates door's bounding box
		if(currentAnimation.getId().contains("closed")){
			enableBoundingBox(this.x, this.y,
						currentAnimation.getImage().getWidth() - currentAnimation.getImage().getWidth()/2,
						currentAnimation.getImage().getHeight());
		}
		else if(currentAnimation.getId().contains("half")){
			enableBoundingBox(this.x, this.y,
					currentAnimation.getImage().getWidth() - currentAnimation.getImage().getWidth()/2,
					currentAnimation.getImage().getHeight()/2);
		}
		else if(currentAnimation.getId().contains("opened")){
			enableBoundingBox(this.x, this.y,
					currentAnimation.getImage().getWidth() - currentAnimation.getImage().getWidth()/2,
					0);
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
			this.setCurrentAnimation("door_half_opening", FRAME_DURATION * 2);
			
			break;
			
		case "door_closing":
			frame = this.getCurrentAnimation().getCurrentFrame();
			this.setCurrentAnimation("door_opening", FRAME_DURATION * 2);
			this.getCurrentAnimation().setCurrentFrame(8 - frame);
			break;
			
		case "door_half_closing":
			frame = this.getCurrentAnimation().getCurrentFrame();
			this.setCurrentAnimation("door_half_opening", FRAME_DURATION * 2);
			this.getCurrentAnimation().setCurrentFrame(10 - frame);
			break;
			
		case "final_door_closed":
			System.out.println("ENTRA AQUI?");
			this.setCurrentAnimation("final_door_opening", FRAME_DURATION * 2);
			break;
			
		default:
			
			break;
		}
	}
	
	public void closeDoor(){
		System.out.println("CERRRRRAMOS");
		if(!this.getCurrentAnimation().getId().equals("door_closed")){
			this.setCurrentAnimation("door_closed", FRAME_DURATION);
			closing_fast.play();
		}
	}
	
	public boolean isOpenedFinal(){
		System.out.println(this.getCurrentAnimation().getId());
		return this.getCurrentAnimation().getId().equals("final_door_opened");
	}
}
