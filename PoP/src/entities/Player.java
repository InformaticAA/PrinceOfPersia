package entities;

import java.awt.Rectangle;
import java.util.Hashtable;

import framework.Loader;
import input.Key;

public class Player extends Character {

	private enum PlayerState {IDLE, MOVE, JUMP, COMBAT, COLLIDED, DIED};
	
	/* Constants */
	private final String RUNNING_START = "running start";
	private final String RUNNING = "running";
	private final int FRAME_DURATION = 6;
	private final int MOVE_SPEED = 2;
	
	private boolean up_pressed;
	private boolean down_pressed;
	private boolean right_pressed;
	private boolean left_pressed;
	private boolean shift_pressed;
	
	private PlayerState currentState;
	
	private boolean changed_position;
	private String newOrientation;
	
	private boolean canMakeStep;
	private boolean canWalkCrouched;
	
	private boolean enemySaw;
	
	private boolean combatStepRight;
	private boolean combatStepLeft;
	private boolean combatAttack;
	private boolean combatDefense;
	private boolean combatCanMove;
	private boolean combatCanAttack;
	private boolean combatCanDefense;
	
	public Player(int x, int y, Loader loader, String orientation) {
		super(x, y, loader, orientation);
		animations = loader.getAnimations("Dastan");
		
		currentAnimation = animations.get("idle_" + this.orientation);
		currentState = PlayerState.IDLE;
		
		boundingBox = new Rectangle(x,y,currentAnimation.getImage().getWidth(),
				currentAnimation.getImage().getHeight());
		
		this.changed_position = false;
		this.newOrientation = orientation;
		
		this.right_pressed = false;
		this.left_pressed = false;
		this.shift_pressed = false;
		this.up_pressed = false;
		this.down_pressed = false;
		
		this.canMakeStep = true;
		this.canWalkCrouched = true;
		
		this.enemySaw = false;
		
		this.combatStepRight = false;
		this.combatStepLeft = false;
		this.combatAttack = false;
		this.combatDefense = false;
		this.combatCanMove = true;
		this.combatCanAttack = true;
		this.combatCanDefense = true;
	}
	
	@Override
	public void update(long elapsedTime) {
		super.update(elapsedTime);
		
		if(!right_pressed && !left_pressed && !up_pressed && currentState != PlayerState.COLLIDED && currentState != PlayerState.COMBAT){
			currentState = PlayerState.IDLE;
		} 

		if(this.currentState != PlayerState.COLLIDED && this.currentState != PlayerState.COMBAT){
			if(right_pressed || left_pressed){
				if(right_pressed && !left_pressed){
					if(this.getOrientation().equals("left") && !changed_position){
						this.currentState = PlayerState.IDLE;
					} else{
						this.currentState = PlayerState.MOVE;
					}
				} else if(!right_pressed && left_pressed && !changed_position){
					if(this.getOrientation().equals("right")){
						this.currentState = PlayerState.IDLE;
					} else{
						this.currentState = PlayerState.MOVE;
					}
				} 
			}
		}
		
		if(up_pressed && currentState != PlayerState.COLLIDED && currentState != PlayerState.COMBAT){
			this.currentState = PlayerState.JUMP;
		}
		
		manageAnimations();
		this.moveCharacter();
	}
	
	public void setCollided(){
		if(this.currentState != PlayerState.COMBAT){
			this.currentState = PlayerState.COLLIDED;
		}
	} 
	
	public void isEnemySaw(boolean isSaw){
		this.enemySaw = isSaw;
	}
	
	public void manageKeyPressed(int key_pressed, Hashtable<String,Integer> keys_mapped){
		if(key_pressed == keys_mapped.get(Key.UP)){
			if(this.currentState != PlayerState.COMBAT){
				up_pressed = true;
				currentState = PlayerState.JUMP;
			} else{
				if(!this.combatDefense){
					combatDefense = true;
					combatCanDefense = true;
				}
			}
			
		} else if(key_pressed == keys_mapped.get(Key.RIGHT)){
			if(this.currentState != PlayerState.COMBAT){
				if(!right_pressed){
					canMakeStep = true;
					canWalkCrouched = true;
				}
				if(this.currentState != PlayerState.JUMP){
					currentState = PlayerState.MOVE;
					if(this.getOrientation().equals("left")){
						this.changed_position = true;
						this.newOrientation = "right";
					}
				}
				right_pressed = true;
			} else{
				if(!this.combatStepRight){
					this.combatStepRight = true;
					this.combatCanMove = true;
				}
			}
			
		} else if(key_pressed == keys_mapped.get(Key.LEFT)){
			if(this.currentState != PlayerState.COMBAT){
				if(!left_pressed){
					canMakeStep = true;
					canWalkCrouched = true;
				}
				if(this.currentState != PlayerState.JUMP){
					currentState = PlayerState.MOVE;
					if(this.getOrientation().equals("right")){
						this.changed_position = true;
						this.newOrientation = "left";
					}
				}
				left_pressed = true;
			} else{
				if(!this.combatStepLeft){
					this.combatStepLeft = true;
					this.combatCanMove = true;
				}
			}
			
		} else if(key_pressed == keys_mapped.get(Key.DOWN)){
			down_pressed = true;
			
		} else if(key_pressed == keys_mapped.get(Key.SHIFT)){
			if(this.currentState != PlayerState.COMBAT){
				shift_pressed = true;
			} else{
				if(!this.combatAttack){
					this.combatCanAttack = true;
					this.combatAttack = true;
				}
			}
			
		} else if(key_pressed == keys_mapped.get(Key.D)){
			System.out.println("State: " + this.currentState + "\n" + 
					"Animation: " + this.getCurrentAnimation().getId() + "\n" + 
					"Orientation: " + this.getOrientation() + "\n" +
					"left_pressed: " + left_pressed + "\n" + 
					"right_pressed: " + right_pressed + "\n" + 
					"up_pressed: " + up_pressed + "\n" + 
					"down_pressed: " + down_pressed + "\n");
		}
	}
	
	public void manageKeyReleased(int key_released, Hashtable<String,Integer> keys_mapped){
		if(key_released == keys_mapped.get(Key.UP)){
			if(currentState != PlayerState.COMBAT){
				this.currentState = PlayerState.IDLE;
			}
			up_pressed = false;
			combatDefense = false;
			
		} else if(key_released == keys_mapped.get(Key.RIGHT)){
			if(currentState != PlayerState.JUMP && currentState != PlayerState.COMBAT && this.getOrientation().equals("right")){
				this.currentState = PlayerState.IDLE;
			}
			right_pressed = false;
			combatStepRight = false;
			
		} else if(key_released == keys_mapped.get(Key.LEFT)){
			if(currentState != PlayerState.JUMP && currentState != PlayerState.COMBAT && this.getOrientation().equals("left")){
				this.currentState = PlayerState.IDLE;
			}
			left_pressed = false;
			combatStepLeft = false;
			
		} else if(key_released == keys_mapped.get(Key.DOWN)){
			down_pressed = false;
			
		} else if(key_released == keys_mapped.get(Key.SHIFT)){
			shift_pressed = false;
			combatAttack = false;
		}
	}
	
	private void manageAnimations(){
		
		switch(currentAnimation.getId()){
		
		case "clipping_left":
		case "clipping_right":
			
			switch(currentState){
			case IDLE:
				
				break;
				
			case JUMP:
				
				break;
				
			case MOVE:
				
				break;
				
			case COLLIDED:
				System.out.println("COLLIDED EN POS TO RARA");
				break;
				
			case COMBAT:
				
				break;
				
			default:
				
				break;
			}
			break;
			
		case "crouching down_left":
		case "crouching down_right":
			
			switch(currentState){
			case IDLE:
				this.setMoveSpeed(0);
				if(currentAnimation.isOver(false)){
					this.setCurrentAnimation("crouching idle_" + orientation, FRAME_DURATION);
				}
				break;
				
			case JUMP:
				this.setMoveSpeed(0);
				if(currentAnimation.isOver(false)){
					this.setCurrentAnimation("crouching idle_" + orientation, FRAME_DURATION);
				}
				break;
				
			case MOVE:
				if(this.getOrientation().equals("left")){
					this.setMoveSpeed(-MOVE_SPEED);
				} else{
					this.setMoveSpeed(MOVE_SPEED);
				}
				if(currentAnimation.isOver(false)){
					if(changed_position){
						changed_position = false;
						this.currentState = PlayerState.IDLE;
						this.setMoveSpeed(0);
						this.setCurrentAnimation("crouching idle_" + orientation, FRAME_DURATION);
					}
					if(canWalkCrouched){
						if(this.getOrientation().equals("left")){
							this.setMoveSpeed(-MOVE_SPEED/2);
						} else{
							this.setMoveSpeed(MOVE_SPEED/2);
						}
						canWalkCrouched = false;
						this.setCurrentAnimation("crouching walk_" + orientation, FRAME_DURATION);
					} else{
						this.setMoveSpeed(0);
						this.setCurrentAnimation("crouching idle_" + orientation, FRAME_DURATION);
					}
				}
				break;
				
			case COLLIDED:
				if(this.getOrientation().equals("left")){
					this.setMoveSpeed(-MOVE_SPEED/2);
				} else{
					this.setMoveSpeed(MOVE_SPEED/2);
				}
				this.setCurrentAnimation("running collided_" + orientation, FRAME_DURATION);
				break;
				
			case COMBAT:
				
				break;
				
			default:
				
				break;
			}
			break;
			
		case "crouching idle_left":
		case "crouching idle_right":

			switch(currentState){
			case IDLE:
				if(!down_pressed){
					this.setCurrentAnimation("crouching up_" + orientation, FRAME_DURATION);
				} 
				this.setMoveSpeed(0);
				break;
				
			case JUMP:
				this.setCurrentAnimation("crouching up_" + orientation, FRAME_DURATION);
				this.setMoveSpeed(0);
				break;
				
			case MOVE:
				this.setMoveSpeed(0);
				if(canWalkCrouched){
					canWalkCrouched = false;
					if(changed_position){
						changed_position = false;
					} else{
						if(this.getOrientation().equals("left")){
							this.setMoveSpeed(-MOVE_SPEED);
						} else{
							this.setMoveSpeed(MOVE_SPEED);
						}
						this.setCurrentAnimation("crouching walk_" + orientation, FRAME_DURATION);
					}
				}
				if(!down_pressed){
					this.setCurrentAnimation("crouching up_" + orientation, FRAME_DURATION);
				}
				break;
				
			case COLLIDED:
				System.out.println("COLLIDED EN POS TO RARA");
				break;
				
			case COMBAT:
				
				break;
				
			default:
				
				break;
			}
			break;
			
		case "crouching up_left":
		case "crouching up_right":

			switch(currentState){
			case IDLE:
				this.setMoveSpeed(0);
				if(currentAnimation.isOver(false)){
					canWalkCrouched = true;
					this.setCurrentAnimation("idle_" + orientation, FRAME_DURATION);
				}
				break;
				
			case JUMP:
				this.setMoveSpeed(0);
				if(currentAnimation.isOver(false)){
					canWalkCrouched = true;
					this.setCurrentAnimation("idle_" + orientation, FRAME_DURATION);
				}
				break;
				
			case MOVE:
				this.setMoveSpeed(0);
				if(currentAnimation.isOver(false)){
					canWalkCrouched = true;
					this.setCurrentAnimation("idle_" + orientation, FRAME_DURATION);
				}
				break;
				
			case COLLIDED:
				if(this.getOrientation().equals("left")){
					this.setMoveSpeed(-MOVE_SPEED/2);
				} else{
					this.setMoveSpeed(MOVE_SPEED/2);
				}
				this.setCurrentAnimation("running collided_" + orientation, FRAME_DURATION);
				break;
				
			case COMBAT:
				
				break;
				
			default:
				
				break;
			}
			break;
			
		case "crouching walk_left":
		case "crouching walk_right":

			switch(currentState){
			case IDLE:
				if(this.getOrientation().equals("left")){
					this.setMoveSpeed(-MOVE_SPEED);
				} else{
					this.setMoveSpeed(MOVE_SPEED);
				}
				if(currentAnimation.isOver(false)){
					if(!down_pressed){
						this.setMoveSpeed(0);
						this.setCurrentAnimation("crouching up_" + orientation, FRAME_DURATION);
					} else{
						this.setMoveSpeed(0);
						this.setCurrentAnimation("crouching idle_" + orientation, FRAME_DURATION);
					}
				}
				break;
				
			case JUMP:
				if(this.getOrientation().equals("left")){
					this.setMoveSpeed(-MOVE_SPEED);
				} else{
					this.setMoveSpeed(MOVE_SPEED);
				}
				if(currentAnimation.isOver(false)){
					this.setMoveSpeed(0);
					this.setCurrentAnimation("crouching up_" + orientation, FRAME_DURATION);
				}
				break;
				
			case MOVE:
				if(this.getOrientation().equals("left")){
					this.setMoveSpeed(-MOVE_SPEED/2);
				} else{
					this.setMoveSpeed(MOVE_SPEED/2);
				}
				if(currentAnimation.isOver(false)){
					this.setMoveSpeed(0);
					this.setCurrentAnimation("crouching idle_" + orientation, FRAME_DURATION);
				}
				break;
				
			case COLLIDED:
				if(this.getOrientation().equals("left")){
					this.setMoveSpeed(-MOVE_SPEED/2);
				} else{
					this.setMoveSpeed(MOVE_SPEED/2);
				}
				this.setCurrentAnimation("running collided_" + orientation, FRAME_DURATION);
				break;
				
			case COMBAT:
				
				break;
				
			default:
				
				break;
			}
			break;
			
		case "dieing_left":
		case "dieing_right":

			switch(currentState){
			case IDLE:
				
				break;
				
			case JUMP:
				
				break;
				
			case MOVE:
				
				break;
				
			case COLLIDED:
				System.out.println("COLLIDED EN ANIMATION TO RARA");
				break;
				
				
			case COMBAT:
				break;
				
				
			default:
				
				break;
			}
			break;
	
		case "drinking_left":
		case "drinking_right":

			switch(currentState){
			case IDLE:
				
				break;
				
			case JUMP:
				
				break;
				
			case MOVE:
				
				break;
				
			case COLLIDED:
				System.out.println("COLLIDED EN ANIMATION TO RARA");
				break;
				
			default:
				
				break;
			}
			break;
			
		case "falling_left":
		case "falling_right":

			switch(currentState){
			case IDLE:
				
				break;
				
			case JUMP:
				
				break;
				
			case MOVE:
				
				break;
				
			case COLLIDED:
				
				break;
				
			case COMBAT:

				break;
				
				
			default:
				
				break;
			}
			break;
			
		case "got sword_left":
		case "got sword_right":

			switch(currentState){
			case IDLE:
				
				break;
				
			case JUMP:
				
				break;
				
			case MOVE:
				
				break;
				
			case COLLIDED:
				System.out.println("COLLIDED EN POS TO RARA");
				break;
				
			case COMBAT:

				break;
				
			default:
				
				break;
			}
			break;
			
		case "hanging backwards_left":
		case "hanging backwards_right":

			switch(currentState){
			case IDLE:
				
				break;
				
			case JUMP:
				
				break;
				
			case MOVE:
				
				break;
				
			case COLLIDED:
				
				break;
				
			case COMBAT:

				break;
				
			default:
				
				break;
			}
			break;
			
		case "hanging forward_left":
		case "hanging forward_right":

			switch(currentState){
			case IDLE:
				
				break;
				
			case JUMP:
				
				break;
				
			case MOVE:
				
				break;
				
			case COLLIDED:
				
				break;
				
			case COMBAT:

				break;
				
				
			default:
				
				break;
			}
			break;
			
		case "hanging idle_left":
		case "hanging idle_right":

			switch(currentState){
			case IDLE:
				
				break;
				
			case JUMP:
				
				break;
				
			case MOVE:
				
				break;
				
			case COLLIDED:
				
				break;
				
			case COMBAT:

				break;
				
				
			default:
				
				break;
			}
			break;
			
		case "idle_left":
		case "idle_right":

			switch(currentState){
			case IDLE:
				this.setMoveSpeed(0);
				if(!enemySaw){
					if(changed_position){
						changed_position = false;
					} else if(down_pressed){
						this.setCurrentAnimation("crouching down_" + orientation, FRAME_DURATION);
					}
				} else{
					this.currentState = PlayerState.COMBAT;
				}
				break;
				
			case JUMP:
				this.setMoveSpeed(0);
				if(!enemySaw){
					if(right_pressed || left_pressed){
						if(right_pressed && this.currentAnimation.equals("right")){
							this.setCurrentAnimation("simple jump_" + orientation, FRAME_DURATION);
						} else if(left_pressed && this.currentAnimation.equals("left")){
							this.setCurrentAnimation("simple jump_" + orientation, FRAME_DURATION);
						} else{
							this.setCurrentAnimation("scaling up start_" + orientation, FRAME_DURATION);
						}
					} else{
						this.setCurrentAnimation("scaling up start_" + orientation, FRAME_DURATION);
					}
				} else{
					this.currentState = PlayerState.COMBAT;
				}
				break;
				
			case MOVE:
				this.setMoveSpeed(0);
				if(!enemySaw){
					if(changed_position){
						this.setMoveSpeed(0);
						changed_position = false;
						this.setOrientation(newOrientation);
						this.setCurrentAnimation("turning_" + orientation, FRAME_DURATION);
					} else if(shift_pressed){
						if(canMakeStep){
							if(this.getOrientation().equals("left")){
								this.setMoveSpeed(-MOVE_SPEED/2);
							} else{
								this.setMoveSpeed(MOVE_SPEED/2);
							}
							this.setCurrentAnimation("walking a step_" + orientation, FRAME_DURATION);
							canMakeStep = false;
						} else{
							this.setMoveSpeed(0);
						}
					} else if(down_pressed){
						if(this.getOrientation().equals("left")){
							this.setMoveSpeed(-MOVE_SPEED);
						} else{
							this.setMoveSpeed(MOVE_SPEED);
						}
						this.setCurrentAnimation("crouching down_" + orientation, FRAME_DURATION);
					}
					else{
						if(this.getOrientation().equals("left")){
							this.setMoveSpeed(-MOVE_SPEED);
						} else{
							this.setMoveSpeed(MOVE_SPEED);
						}
	//					System.out.printf("starts running: ");
						this.setCurrentAnimation("running start_" + orientation, FRAME_DURATION);
					}
				} else{
					this.currentState = PlayerState.COMBAT;
				}
				break;
				
			case COLLIDED:

				break;
				
			case COMBAT:
				this.setMoveSpeed(0);
				this.setCurrentAnimation("taking sword out_" + orientation, FRAME_DURATION);
				break;
				
			default:
				
				break;
			}
			break;
			
		case "putting down sword_left":
		case "putting down sword_right":

			switch(currentState){
			case IDLE:
				
				break;
				
			case JUMP:
				
				break;
				
			case MOVE:
				
				break;
				
			case COLLIDED:

				break;
				
			case COMBAT:
				this.setMoveSpeed(0);
				if(currentAnimation.isOver(false)){
					this.setCurrentAnimation("idle_" + orientation, FRAME_DURATION);
					this.currentState = PlayerState.IDLE;
				}
				break;
				
			default:
				
				break;
			}
			break;
			
		case "running collided_left":
		case "running collided_right":

			switch(currentState){
			case IDLE:
				if(this.currentAnimation.isOver(false)){
					this.setMoveSpeed(0);
					this.setCurrentAnimation("idle_" + orientation, FRAME_DURATION);
				}
				break;
				
			case JUMP:
				if(this.currentAnimation.isOver(false)){
					this.setMoveSpeed(0);
					this.setCurrentAnimation("idle_" + orientation, FRAME_DURATION);
				}
				break;
				
			case MOVE:
				if(this.currentAnimation.isOver(false)){
					this.setMoveSpeed(0);
					this.setCurrentAnimation("idle_" + orientation, FRAME_DURATION);
				}
				break;
				
			case COLLIDED:
				if(this.currentAnimation.isOver(false)){
					this.setMoveSpeed(0);
					this.setCurrentAnimation("idle_" + orientation, FRAME_DURATION);
					this.currentState = PlayerState.IDLE;
				}
				break;
				
			case COMBAT:
				
				break;
				
			default:
				
				break;
			}
			break;
			
		case "running jump collided_left":
		case "running jump collided_right":

			switch(currentState){
			case IDLE:
				if(this.currentAnimation.isOver(false)){
					this.setMoveSpeed(0);
					this.setCurrentAnimation("idle_" + orientation, FRAME_DURATION);
				}
				break;
				
			case JUMP:
				if(this.currentAnimation.isOver(false)){
					this.setMoveSpeed(0);
					this.setCurrentAnimation("idle_" + orientation, FRAME_DURATION);
				}
				break;
				
			case MOVE:
				if(this.currentAnimation.isOver(false)){
					this.setMoveSpeed(0);
					this.setCurrentAnimation("idle_" + orientation, FRAME_DURATION);
				}
				break;
				
			case COLLIDED:
				if(this.currentAnimation.isOver(false)){
					this.setMoveSpeed(0);
					this.setCurrentAnimation("idle_" + orientation, FRAME_DURATION);
					this.currentState = PlayerState.IDLE;
				}
				break;
				
			case COMBAT:
			
				break;
				
				
			default:
				
				break;
			}
			break;
		
		case "running jump_left":
		case "running jump_right":

			switch(currentState){
			case IDLE:
				if(this.getOrientation().equals("left")){
					this.setMoveSpeed(-MOVE_SPEED);
				} else{
					this.setMoveSpeed(MOVE_SPEED);
				}
				
				if(this.currentAnimation.isOver(false)){
					if(this.getOrientation().equals("left")){
						this.setMoveSpeed(-MOVE_SPEED);
					} else{
						this.setMoveSpeed(MOVE_SPEED);
					}
					this.setCurrentAnimation("running stop start_" + orientation, FRAME_DURATION);
				}
				break;
				
			case JUMP:
				if(this.getOrientation().equals("left")){
					this.setMoveSpeed(-MOVE_SPEED);
				} else{
					this.setMoveSpeed(MOVE_SPEED);
				}
				
				if(this.currentAnimation.isOver(false)){
					if(this.getOrientation().equals("left")){
						this.setMoveSpeed(-MOVE_SPEED);
					} else{
						this.setMoveSpeed(MOVE_SPEED);
					}
					this.setCurrentAnimation("running stop start_" + orientation, FRAME_DURATION);
				}
				break;
				
			case MOVE:
				if(this.getOrientation().equals("left")){
					this.setMoveSpeed(-MOVE_SPEED);
				} else{
					this.setMoveSpeed(MOVE_SPEED);
				}
				
				if(this.currentAnimation.isOver(false)){
					if(this.getOrientation().equals("left")){
						this.setMoveSpeed(-MOVE_SPEED);
					} else{
						this.setMoveSpeed(MOVE_SPEED);
					}
					this.setCurrentAnimation("running_" + orientation, FRAME_DURATION);
				}
				break;
				
			case COLLIDED:
				this.setMoveSpeed(0);
				this.setCurrentAnimation("running jump collided_" + orientation, FRAME_DURATION);
				break;
				
			case COMBAT:
				
				break;
				
			default:
				
				break;
			}
			break;
			
		case "running start_left":
		case "running start_right":

			switch(currentState){
			case IDLE:
				if(this.getOrientation().equals("left")){
					this.setMoveSpeed(-MOVE_SPEED);
				} else{
					this.setMoveSpeed(MOVE_SPEED);
				}
				if(currentAnimation.isOver(false)){
					if(this.getOrientation().equals("left")){
						this.setMoveSpeed(-MOVE_SPEED);
					} else{
						this.setMoveSpeed(MOVE_SPEED);
					}
					this.setCurrentAnimation("running stop start_" + orientation, FRAME_DURATION);
				}
				break;
				
			case JUMP:
				if(this.getOrientation().equals("left")){
					this.setMoveSpeed(-MOVE_SPEED);
				} else{
					this.setMoveSpeed(MOVE_SPEED);
				}
				if(currentAnimation.isOver(false)){
					if(this.getOrientation().equals("left")){
						if(left_pressed){
							this.setCurrentAnimation("running jump_" + orientation, FRAME_DURATION);
							this.setMoveSpeed(-MOVE_SPEED);
						} else{
							this.setMoveSpeed(0);
							this.setCurrentAnimation("running stop_" + orientation, FRAME_DURATION);
						}
					} else{
						if(right_pressed){
							this.setCurrentAnimation("running jump_" + orientation, FRAME_DURATION);
							this.setMoveSpeed(MOVE_SPEED);
						} else{
							this.setMoveSpeed(0);
							this.setCurrentAnimation("running stop start_" + orientation, FRAME_DURATION);
						}
					}
				}
				break;
				
			case MOVE:
				if(this.getOrientation().equals("left")){
					this.setMoveSpeed(-MOVE_SPEED);
				} else{
					this.setMoveSpeed(MOVE_SPEED);
				}
//				System.out.printf(currentAnimation.getCurrentFrame() + ", ");
				if(currentAnimation.isOver(false)){
					if(changed_position){
						changed_position = false;
						this.setOrientation(newOrientation);
						this.setCurrentAnimation("turn running_" + orientation, FRAME_DURATION);
					} else{
//						System.out.println();
						this.setCurrentAnimation("running_" + orientation, FRAME_DURATION);
					}
				} 
				break;
				
			case COLLIDED:
				if(this.getOrientation().equals("left")){
					this.setMoveSpeed(-MOVE_SPEED/2);
				} else{
					this.setMoveSpeed(MOVE_SPEED/2);
				}
				this.setCurrentAnimation("running collided_" + orientation, FRAME_DURATION);
				break;
				
			case COMBAT:
				
				break;
				
			default:
				
				break;
			}
			break;
			
		case "running stop start_left":
		case "running stop start_right":

			switch(currentState){
			case IDLE:
				if(this.getOrientation().equals("left")){
					this.setMoveSpeed(-MOVE_SPEED);
				} else{
					this.setMoveSpeed(MOVE_SPEED);
				}
				if(currentAnimation.isOver(false)){
					this.setCurrentAnimation("running stop_" + orientation, FRAME_DURATION);
					this.setMoveSpeed(0);
				}
				break;
				
			case JUMP:
				if(this.getOrientation().equals("left")){
					this.setMoveSpeed(-MOVE_SPEED);
				} else{
					this.setMoveSpeed(MOVE_SPEED);
				}
				if(currentAnimation.isOver(false)){
					this.setCurrentAnimation("running stop_" + orientation, FRAME_DURATION);
					this.setMoveSpeed(0);
				}
				break;
				
			case MOVE:
				if(this.getOrientation().equals("left")){
					this.setMoveSpeed(-MOVE_SPEED);
				} else{
					this.setMoveSpeed(MOVE_SPEED);
				}
				if(currentAnimation.isOver(false)){
					if(changed_position){
						changed_position = false;
						this.setOrientation(newOrientation);
						this.setCurrentAnimation("turn running started_" + orientation, FRAME_DURATION);
					} else{
						this.currentState = PlayerState.IDLE;
						this.setCurrentAnimation("running stop_" + orientation, FRAME_DURATION);
					}
				}
				break;
				
			case COLLIDED:
				if(this.getOrientation().equals("left")){
					this.setMoveSpeed(-MOVE_SPEED/2);
				} else{
					this.setMoveSpeed(MOVE_SPEED/2);
				}
				this.setCurrentAnimation("running collided_" + orientation, FRAME_DURATION);
				break;
				
			case COMBAT:
				
				break;
				
			default:
				
				break;
			}
			break;
			
		case "running stop_left":
		case "running stop_right":

			switch(currentState){
			case IDLE:
				if(this.getOrientation().equals("left")){
					this.setMoveSpeed(-MOVE_SPEED);
				} else{
					this.setMoveSpeed(MOVE_SPEED);
				}
				if(currentAnimation.isOver(false)){
					this.setMoveSpeed(0);
					this.setCurrentAnimation("idle_" + orientation, FRAME_DURATION);
				}
				break;
				
			case JUMP:
				if(this.getOrientation().equals("left")){
					this.setMoveSpeed(-MOVE_SPEED);
				} else{
					this.setMoveSpeed(MOVE_SPEED);
				}
				if(currentAnimation.isOver(false)){
					this.setMoveSpeed(0);
					this.setCurrentAnimation("idle_" + orientation, FRAME_DURATION);
				}
				break;
				
			case MOVE:
				if(currentAnimation.isOver(false)){
					if(changed_position){
						this.setMoveSpeed(0);
						changed_position = false;
						this.setOrientation(newOrientation);
						this.setCurrentAnimation("turning_" + orientation, FRAME_DURATION);
						
					} else{
						if(this.getOrientation().equals("left")){
							this.setMoveSpeed(-MOVE_SPEED);
						} else{
							this.setMoveSpeed(MOVE_SPEED);
						}
						this.setCurrentAnimation("running start_" + orientation, FRAME_DURATION);
					}
				}
				break;
				
			case COLLIDED:
				
				break;
				
			case COMBAT:
				
				break;
				
			default:
				
				break;
			}
			break;
			
		case "running_left":
		case "running_right":

			switch(currentState){
			case IDLE:
				if(this.getOrientation().equals("left")){
					this.setMoveSpeed(-MOVE_SPEED);
				} else{
					this.setMoveSpeed(MOVE_SPEED);
				}
				this.setCurrentAnimation("running stop start_" + orientation, FRAME_DURATION);
				break;
				
			case JUMP:
				if(this.getOrientation().equals("left")){
					if(left_pressed){
						this.setCurrentAnimation("running jump_" + orientation, FRAME_DURATION);
					} else{
						this.setCurrentAnimation("running stop start_" + orientation, FRAME_DURATION);
					}
					this.setMoveSpeed(-MOVE_SPEED);
				} else{
					if(right_pressed){
						this.setCurrentAnimation("running jump_" + orientation, FRAME_DURATION);
					} else{
						this.setCurrentAnimation("running stop start_" + orientation, FRAME_DURATION);
					}
					this.setMoveSpeed(MOVE_SPEED);
				}
				
				break;
				
			case MOVE:
				if(this.getOrientation().equals("left")){
					this.setMoveSpeed(-MOVE_SPEED);
				} else{
					this.setMoveSpeed(MOVE_SPEED);
				}
				if(changed_position){
					changed_position = false;
					this.setOrientation(newOrientation);
					this.setCurrentAnimation("turn running_" + orientation, FRAME_DURATION);
				} else if(down_pressed){
					this.setCurrentAnimation("crouching down_" + orientation, FRAME_DURATION);
				}
				break;
				
			case COLLIDED:
				if(this.getOrientation().equals("left")){
					this.setMoveSpeed(-MOVE_SPEED/2);
				} else{
					this.setMoveSpeed(MOVE_SPEED/2);
				}
				this.setCurrentAnimation("running collided_" + orientation, FRAME_DURATION);
				break;
				
			case COMBAT:
				
				break;
				
			default:
				
				break;
			}
			break;
			
		case "scaling down_left":
		case "scaling down_right":

			switch(currentState){
			case IDLE:
				if(this.currentAnimation.isOver(false)){
					this.setMoveSpeed(0);
					this.setCurrentAnimation("idle_" + orientation, FRAME_DURATION);
				}
				break;
				
			case JUMP:
				if(this.currentAnimation.isOver(false)){
					this.setCurrentAnimation("idle_" + orientation, FRAME_DURATION);
				}
				break;
				
			case MOVE:
				if(this.currentAnimation.isOver(false)){
					this.setMoveSpeed(0);
					this.setCurrentAnimation("idle_" + orientation, FRAME_DURATION);
				}
				break;
				
			case COLLIDED:
				System.out.println("COLISIONO EN POS TO RARA");
				break;
				
			case COMBAT:
				
				break;
				
				
			default:
				
				break;
				
			
			}
			break;
			
		case "scaling fall_left":
		case "scaling fall_right":

			switch(currentState){
			case IDLE:
				
				break;
				
			case JUMP:
				
				break;
				
			case MOVE:
				
				break;
				
			case COLLIDED:
				
				break;
				
			case COMBAT:
				
				break;
				
			default:
				
				break;
			}
			break;
			
		case "scaling up start_left":
		case "scaling up start_right":

			switch(currentState){
			case IDLE:
				if(this.currentAnimation.isOver(false)){
					this.setMoveSpeed(0);
					this.setCurrentAnimation("scaling up_" + orientation, FRAME_DURATION);
				}
				break;
				
			case JUMP:
				if(right_pressed || left_pressed){
					if(right_pressed && orientation.equals("right")){
						this.setCurrentAnimation("simple jump_" + orientation, FRAME_DURATION);
					} else if(left_pressed && orientation.equals("left")){
						this.setCurrentAnimation("simple jump_" + orientation, FRAME_DURATION);
					} else{
						if(this.currentAnimation.isOver(false)){
							this.setCurrentAnimation("scaling up_" + orientation, FRAME_DURATION);
						}
					}
				} else{
					if(this.currentAnimation.isOver(false)){
						this.setCurrentAnimation("scaling up_" + orientation, FRAME_DURATION);
					}
				}
				break;
				
			case MOVE:
				if(right_pressed || left_pressed){
					if(right_pressed && orientation.equals("right")){
						this.setCurrentAnimation("simple jump_" + orientation, FRAME_DURATION);
					} else if(left_pressed && orientation.equals("left")){
						this.setCurrentAnimation("simple jump_" + orientation, FRAME_DURATION);
					} else{
						if(this.currentAnimation.isOver(false)){
							this.setCurrentAnimation("scaling up_" + orientation, FRAME_DURATION);
						}
					}
				} else{
					if(this.currentAnimation.isOver(false)){
						this.setCurrentAnimation("scaling up_" + orientation, FRAME_DURATION);
					}
				}
				break;
				
			case COLLIDED:
				System.out.println("COLISIONO EN POS TO RARA");
				break;
				
			case COMBAT:
				
				break;
					
				
			default:
				
				break;
			}
			break;
			
		case "scaling up_left":
		case "scaling up_right":

			switch(currentState){
			case IDLE:
				if(this.currentAnimation.isOver(false)){
					this.setMoveSpeed(0);
					this.setCurrentAnimation("scaling down_" + orientation, FRAME_DURATION);
				}
				break;
				
			case JUMP:
				if(this.currentAnimation.isOver(false)){
					this.setCurrentAnimation("scaling down_" + orientation, FRAME_DURATION);
				}
				break;
				
			case MOVE:
				if(this.currentAnimation.isOver(false)){
					this.setCurrentAnimation("scaling down_" + orientation, FRAME_DURATION);
				}
				break;
				
			case COLLIDED:
				System.out.println("COLISIONO EN UNA ANIMACION TO RARA");
				break;
				
			case COMBAT:
				
				break;
				
			default:
				
				break;
			}
			break;
			
		case "simple jump_left":
		case "simple jump_right":

			switch(currentState){
			case IDLE:
				if(this.getOrientation().equals("left")){
					this.setMoveSpeed(-MOVE_SPEED);
				} else{
					this.setMoveSpeed(MOVE_SPEED);
				}
				
				if(this.currentAnimation.isOver(false)){
					this.setMoveSpeed(0);
					this.setCurrentAnimation("idle_" + orientation, FRAME_DURATION);
				}
				break;
				
			case JUMP:
				if(this.getOrientation().equals("left")){
					this.setMoveSpeed(-MOVE_SPEED);
				} else{
					this.setMoveSpeed(MOVE_SPEED);
				}
				
				if(this.currentAnimation.isOver(false)){
					this.setMoveSpeed(0);
					this.setCurrentAnimation("idle_" + orientation, FRAME_DURATION);
				}
				break;
				
			case MOVE:
				if(this.getOrientation().equals("left")){
					this.setMoveSpeed(-MOVE_SPEED);
				} else{
					this.setMoveSpeed(MOVE_SPEED);
				}
				
				if(this.currentAnimation.isOver(false)){
					this.setMoveSpeed(0);
					this.setCurrentAnimation("idle_" + orientation, FRAME_DURATION);
				}
				break;
				
			case COLLIDED:
				this.setMoveSpeed(0);
				this.setCurrentAnimation("running jump collided_" + orientation, FRAME_DURATION);
				break;
				
			default:
				
				break;
			}
			break;
			
		case "sword attack start_left":
		case "sword attack start_right":

			switch(currentState){
			case IDLE:
				
				break;
				
			case JUMP:
				
				break;
				
			case MOVE:
				
				break;
				
			case COLLIDED:

				break;
				
			case COMBAT:
				if(this.currentAnimation.isOver(false)){
					this.setMoveSpeed(0);
					this.setCurrentAnimation("sword attack end_" + orientation, FRAME_DURATION);
				}
				break;
				
			default:
				
				break;
			}
			break;
			
		case "sword attack end_left":
		case "sword attack end_right":

			switch(currentState){
			case IDLE:
				
				break;
				
			case JUMP:
				
				break;
				
			case MOVE:
				
				break;
				
			case COLLIDED:

				break;
				
			case COMBAT:
				if(this.currentAnimation.isOver(false)){
					this.setMoveSpeed(0);
					this.setCurrentAnimation("sword idle_" + orientation, FRAME_DURATION);
				}
				break;
				
			default:
				
				break;
			}
			break;
			
		case "sword attack up start_left":
		case "sword attack up start_right":

			switch(currentState){
			case IDLE:
				
				break;
				
			case JUMP:
				
				break;
				
			case MOVE:
				
				break;
				
			case COLLIDED:

				break;
				
			case COMBAT:
				if(this.currentAnimation.isOver(false)){
					this.setMoveSpeed(0);
					this.setCurrentAnimation("sword attack end_" + orientation, FRAME_DURATION);
				}
				break;
				
			default:
				
				break;
			}
			break;
			
		case "sword attacking_left":
		case "sword attacking_right":

			switch(currentState){
			case IDLE:
				
				break;
				
			case JUMP:
				
				break;
				
			case MOVE:
				
				break;
				
			case COLLIDED:

				break;
				
			case COMBAT:
				
				break;
				
			default:
				
				break;
			}
			break;
			
		case "sword defense start_left":
		case "sword defense start_right":

			switch(currentState){
			case IDLE:
				
				break;
				
			case JUMP:
				
				break;
				
			case MOVE:
				
				break;
				
			case COLLIDED:

				break;
				
			case COMBAT:
				if(this.currentAnimation.isOver(false)){
					this.setMoveSpeed(0);
					if(this.combatCanAttack && this.combatAttack){
						this.combatCanAttack = false;
						this.setCurrentAnimation("sword attack up start_" + orientation, FRAME_DURATION);
					} else{
						this.setCurrentAnimation("sword idle_" + orientation, FRAME_DURATION);
					}
				}
				break;
				
			default:
				
				break;
			}
			break;
			
		case "sword idle_left":
		case "sword idle_right":

			switch(currentState){
			case IDLE:
				
				break;
				
			case JUMP:
				
				break;
				
			case MOVE:
				
				break;
				
			case COLLIDED:

				break;
				
			case COMBAT:
				this.setMoveSpeed(0);
				if(this.combatCanMove && combatStepRight){
					this.combatCanMove = false;
					this.setMoveSpeed(MOVE_SPEED);
					this.setCurrentAnimation("sword walking_" + orientation, FRAME_DURATION);
				} else if(this.combatCanMove && combatStepLeft){
					this.combatCanMove = false;
					this.setMoveSpeed(-MOVE_SPEED);
					this.setCurrentAnimation("sword walking_" + orientation, FRAME_DURATION);
				} else if(this.combatCanDefense && combatDefense){
					this.combatCanDefense = false;
					this.setCurrentAnimation("sword defense start_" + orientation, FRAME_DURATION);
				} else if(this.combatCanAttack && this.combatAttack){
					this.combatCanAttack = false;
					this.setCurrentAnimation("sword attack start_" + orientation, FRAME_DURATION);
				}
				
				break;
				
			default:
				
				break;
			}
			break;
			
		case "sword walking_left":
		case "sword walking_right":

			switch(currentState){
			case IDLE:
				
				break;
				
			case JUMP:
				
				break;
				
			case MOVE:
				
				break;
				
			case COLLIDED:

				break;
				
			case COMBAT:
				if(this.currentAnimation.isOver(false)){
					this.setMoveSpeed(0);
					this.setCurrentAnimation("sword idle_" + orientation, FRAME_DURATION);
				}
				break;
				
			default:
				
				break;
			}
			break;
			
		case "taking sword out_left":
		case "taking sword out_right":

			switch(currentState){
			case IDLE:
				
				break;
				
			case JUMP:
				
				break;
				
			case MOVE:
				
				break;
				
			case COLLIDED:
				
				break;
				
			case COMBAT:
				this.setMoveSpeed(0);
				if(this.currentAnimation.isOver(false)){
					this.setCurrentAnimation("sword idle_" + orientation, FRAME_DURATION);
				}
				break;
				
			default:
				
				break;
			}
			break;
			
		case "turn running_left":
		case "turn running_right":
		case "turn running started_left":
		case "turn running started_right":

			switch(currentState){
			case IDLE:
				if(this.getOrientation().equals("left")){
					this.setMoveSpeed(MOVE_SPEED);
				} else{
					this.setMoveSpeed(-MOVE_SPEED);
				}
				if(currentAnimation.isOver(false)){
					if(this.getOrientation().equals("left")){
						this.setMoveSpeed(-MOVE_SPEED);
					} else{
						this.setMoveSpeed(MOVE_SPEED);
					}
					this.setCurrentAnimation("running stop start_" + orientation, FRAME_DURATION);
				}
				break;
				
			case JUMP:
				if(this.getOrientation().equals("left")){
					this.setMoveSpeed(MOVE_SPEED);
				} else{
					this.setMoveSpeed(-MOVE_SPEED);
				}
				if(currentAnimation.isOver(false)){
					if(this.getOrientation().equals("left")){
						this.setCurrentAnimation("running jump_" + orientation, FRAME_DURATION);
						this.setMoveSpeed(-MOVE_SPEED);
					} else{
						this.setCurrentAnimation("running jump_" + orientation, FRAME_DURATION);
						this.setMoveSpeed(MOVE_SPEED);
					}
				}
				break;
				
			case MOVE:
				if(this.getOrientation().equals("left")){
					this.setMoveSpeed(MOVE_SPEED);
				} else{
					this.setMoveSpeed(-MOVE_SPEED);
				}
				if(currentAnimation.isOver(false)){
					if(this.getOrientation().equals("left")){
						this.setMoveSpeed(-MOVE_SPEED);
					} else{
						this.setMoveSpeed(MOVE_SPEED);
					}
					this.setCurrentAnimation("running_" + orientation, FRAME_DURATION);
					
				}
				break;
				
			case COLLIDED:
				if(this.getOrientation().equals("left")){
					this.setMoveSpeed(-MOVE_SPEED/2);
				} else{
					this.setMoveSpeed(MOVE_SPEED/2);
				}
				this.setCurrentAnimation("running collided_" + orientation, FRAME_DURATION);
				break;
				
			case COMBAT:
				
				break;
				
			default:
				
				break;
			}
			break;
			
		case "turning_left":
		case "turning_right":

			switch(currentState){
			case IDLE:
				this.setMoveSpeed(0);
				if(currentAnimation.isOver(false)){
					this.setCurrentAnimation("idle_" + orientation, FRAME_DURATION);
				}
				break;
				
			case JUMP:
				this.setMoveSpeed(0);
				if(currentAnimation.isOver(false)){
					this.setCurrentAnimation("scaling up start_" + orientation, FRAME_DURATION);
				}
				break;
				
			case MOVE:
				if(currentAnimation.isOver(false)){
					if(changed_position){
						this.setMoveSpeed(0);
						changed_position = false;
						this.setOrientation(newOrientation);
						this.setCurrentAnimation("turning_" + orientation, FRAME_DURATION);
					} else if(shift_pressed){
						if(canMakeStep){
							if(this.getOrientation().equals("left")){
								this.setMoveSpeed(-MOVE_SPEED/2);
							} else{
								this.setMoveSpeed(MOVE_SPEED/2);
							}
							this.setCurrentAnimation("walking a step_" + orientation, FRAME_DURATION);
							canMakeStep = false;
						} else{
							this.setMoveSpeed(0);
						}
					} else{
						if(this.getOrientation().equals("left")){
							this.setMoveSpeed(-MOVE_SPEED);
						} else{
							this.setMoveSpeed(MOVE_SPEED);
						}
						this.setCurrentAnimation("running start_" + orientation, FRAME_DURATION);
					}
				}
				break;
				
			case COLLIDED:
				System.out.println("COLISIONO EN UNA POS TO RARA");
				break;
				
			case COMBAT:
				
				break;
					
				
			default:
				
				break;
			}
			break;
			
		case "walking a step_left":
		case "walking a step_right":

			switch(currentState){
			case IDLE:
				if(this.getOrientation().equals("left")){
					this.setMoveSpeed(-MOVE_SPEED/2);
				} else{
					this.setMoveSpeed(MOVE_SPEED/2);
				}
				if(currentAnimation.isOver(false)){
					this.setMoveSpeed(0);
					this.setCurrentAnimation("idle_" + orientation, FRAME_DURATION);
				}
				break;
				
			case JUMP:
				if(this.getOrientation().equals("left")){
					this.setMoveSpeed(-MOVE_SPEED/2);
				} else{
					this.setMoveSpeed(MOVE_SPEED/2);
				}
				if(currentAnimation.isOver(false)){
					this.setMoveSpeed(0);
					this.setCurrentAnimation("idle_" + orientation, FRAME_DURATION);
				}
				break;
				
			case MOVE:
				if(this.getOrientation().equals("left")){
					this.setMoveSpeed(-MOVE_SPEED/2);
				} else{
					this.setMoveSpeed(MOVE_SPEED/2);
				}
				if(currentAnimation.isOver(false)){
					this.setMoveSpeed(0);
					this.setCurrentAnimation("idle_" + orientation, FRAME_DURATION);
				}
				break;
				
			case COLLIDED:
				if(this.getOrientation().equals("left")){
					this.setMoveSpeed(-MOVE_SPEED/2);
				} else{
					this.setMoveSpeed(MOVE_SPEED/2);
				}
				this.setCurrentAnimation("running collided_" + orientation, FRAME_DURATION);
				break;
				
			case COMBAT:
				
				break;
				
			default:
				
				break;
			}
			break;
		
		default:
			System.out.println("ANIMATION NOT RECOGNIZED");
			break;
		}
	}
}
