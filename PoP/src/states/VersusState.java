package states;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.concurrent.ConcurrentLinkedQueue;

import data.Level;
import data.Room;
import data.Text;
import entities.Character;
import entities.Entity;
import entities.MPEnemy;
import entities.MPPrince;
import framework.Loader;
import framework.Writter;
import game.Game;
import input.Key;

public class VersusState extends State{
	
	/* Constants */
	private final int INITIAL_HEALTH = 3;
	private final int INITIAL_LEVEL = 14;
	
	/* Variables */
	private boolean start;
	private Level currentLevel;
	private Room currentRoom;
	
	private MPPrince prince;
	private MPEnemy enemy;
	
	private int player1;
	private int player2;
	private int room;
	
	private boolean over;
	private boolean paused;
	
	private ArrayList<Text> texts;
	
	public VersusState(GameStateManager gsm, ConcurrentLinkedQueue<Key> keys, Hashtable<String,Integer> keys_mapped, Loader loader, Writter writter) {
		super(gsm, keys, keys_mapped, loader, writter);
	}
	
	public void setInitialParams(int player1, int player2, int room){
		
		/* 0: prince
		 * 1: red guard */
		
		this.player1 = player1;
		this.player2 = player2;
		this.room = room;
	}

	@Override
	public void init() {
		/* Start game */
		currentLevel = loader.loadLevel(INITIAL_LEVEL);
		currentRoom = currentLevel.getRoom(1, room);
			
		//RIGHT
		prince = new MPPrince(200,240,loader,3,"right",player1);
		enemy = new MPEnemy(460,260,loader,3,"left","red",player2,prince);
		
		
		//LEFT
		//prince = new MPPrince(460,240,loader,3,"left",player1);
		//enemy = new MPEnemy(200,260,loader,3,"right","red",player2,prince);
			
		currentRoom.addCharacter(prince);
		currentRoom.addCharacter(enemy);
		over = false;
		paused = false;
//		enemy.setPlayer(true,prince);
		
		texts = new ArrayList<Text>();
	}

	@Override
	public void update(long elapsedTime) {

		manageKeys();
		if(!paused){
			currentLevel.update(elapsedTime);
		}
		if(!over){
			if(prince.getHp() == 0 || enemy.getHp() == 0){
				over = true;
				String message;
				if(prince.getHp() == 0 && player1 == 0){
					message = "P2 WINS (ESPACIO PARA SALIR)"; 
				} else{
					message = "P1 WINS (ESPACIO PARA SALIR)";
				}
				texts.add(Writter.createText(message, (Game.WIDTH/2) - (16* message.length()/2) , Game.HEIGHT - 16));
				
			}
		} else{
		}
		//checkCollisions(elapsedTime);
	}

	@Override
	public void draw(Graphics2D g) {
		currentRoom.draw(g);
		for(int i = 0; i < texts.size(); i++){
			texts.get(i).drawSelf(g);
		}
	}

	@Override
	public void manageKeys() {
		Object[] keys_used = keys.toArray();
		keys.clear();
		Key e;
		if(keys_used.length!=0){
			for (int i = 0; i < keys_used.length; i++) {
				e = (Key)keys_used[i];
				if(e.isPressed()){
					
					/* key pressed */
					int key_pressed = e.getKeycode();
					
					if(key_pressed == keys_mapped.get(Key.ESCAPE)){
						
					} else if(key_pressed == keys_mapped.get(Key.SPACE)){
						if(over){
							gsm.setState(GameStateManager.MENUSTATE);
						} else{
							paused = !paused;
						}
					}else if(key_pressed == keys_mapped.get(Key.CONTROL)){
						
					} else if(key_pressed == keys_mapped.get(Key.W)||
							key_pressed == keys_mapped.get(Key.A)||
							key_pressed == keys_mapped.get(Key.S)||
							key_pressed == keys_mapped.get(Key.D)||
							key_pressed == keys_mapped.get(Key.C)){
						
						if(!paused){
						
							/* Player1 key map */
							if(player1 == 0){
								prince.manageKeyPressed(key_pressed, keys_mapped);
							} else{
								enemy.manageKeyPressed(key_pressed, keys_mapped);
							}
						}
					} else if(key_pressed == keys_mapped.get(Key.UP)||
							key_pressed == keys_mapped.get(Key.DOWN)||
							key_pressed == keys_mapped.get(Key.LEFT)||
							key_pressed == keys_mapped.get(Key.RIGHT)||
							key_pressed == keys_mapped.get(Key.M)){
						
						if(!paused){
							if(player2 == 0){
								prince.manageKeyPressed(key_pressed, keys_mapped);
							} else{
								enemy.manageKeyPressed(key_pressed, keys_mapped);
							}
						}
					}
				} else{
					
					/* Key released */
					int key_released = e.getKeycode();
					
					if(key_released == keys_mapped.get(Key.ESCAPE)){
						
					} else if(key_released == keys_mapped.get(Key.CONTROL)){
						
					} else if(key_released == keys_mapped.get(Key.W)||
							key_released == keys_mapped.get(Key.A)||
							key_released == keys_mapped.get(Key.S)||
							key_released == keys_mapped.get(Key.D)||
							key_released == keys_mapped.get(Key.C)){
						
						/* Player1 key map */
						if(player1 == 0){
							prince.manageKeyReleased(key_released, keys_mapped);
						} else{
							enemy.manageKeyReleased(key_released, keys_mapped);
						}
					} else if(key_released == keys_mapped.get(Key.UP)||
							key_released == keys_mapped.get(Key.DOWN)||
							key_released == keys_mapped.get(Key.LEFT)||
							key_released == keys_mapped.get(Key.RIGHT)||
							key_released == keys_mapped.get(Key.M)){
						
						/* Player1 key map */
						if(player2 == 0){
							prince.manageKeyReleased(key_released, keys_mapped);
						} else{
							enemy.manageKeyReleased(key_released, keys_mapped);
						}
					}
				}
			}
		}
	}


	private void checkCollisions(long elapsedTime) {
		
		/* Checks for collisions in the currentRoom */
		ArrayList<Entity> bgEntities = currentRoom.getBackground();
		ArrayList<Character> characters = currentRoom.getCharacters();
		ArrayList<Entity> fgEntities = currentRoom.getForeground();
		
		for (Character c : characters) {
			
			/* Collisions with background */
			for (Entity bgE : bgEntities) {
				
				if (c.intersects(bgE, elapsedTime)) {
					
					/* x axis */
					if (c.getBoundingBox().getMinX() >= bgE.getBoundingBox().getMaxX()) {
						
						/* Collision with wall in the character's left side */
						System.out.println("Face stack collision detected (background)");
						c.setMoveSpeed(0, "left");
						
						/* Sets new animation for player */
						c.setCurrentAnimation("running collided_left", 4);
						c.enableBoundingBox();
						
					} else if (c.getBoundingBox().getMaxX() <= bgE.getBoundingBox().getMinX()) {
						
						/* Collision with wall in the character's right side */
						System.out.println("Left stack collision detected (background)");
						c.setMoveSpeed(0, "right");
						
						/* Sets new animation for player */
						c.setCurrentAnimation("running collided_left", 4);
						c.enableBoundingBox();
						
					}
					
					/* y axis */
					if (c.getBoundingBox().getMinY() >= bgE.getBoundingBox().getMaxY()) {
						
						/* Collision with wall in the character's up side */
						
						
					} else if (c.getBoundingBox().getMaxY() <= bgE.getBoundingBox().getMinY()) {
						
						/* Collision with wall in the character's down side */
						System.out.println("Floor collision detected (background)");
						c.setySpeed(0);
					} else {
						
						/* Player is not colliding with anything, thus he must fall */
						c.setySpeed(c.getGravity());
					}
					
					bgE.setBoundingBoxColor(Color.YELLOW);
					c.setBoundingBoxColor(Color.YELLOW);
				}
				else {
					bgE.setBoundingBoxColor(Color.RED);
					c.setBoundingBoxColor(Color.RED);
				}
			}
			
			/* Collisions with foreground */
			for (Entity fgE : fgEntities) {
				if (c.intersects(fgE, elapsedTime)) {
					
					/* x axis */
					if (c.getBoundingBox().getMinX() >= fgE.getBoundingBox().getMaxX()) {
						
						/* Collision with wall in the character's left side */
						System.out.println("Face stack collision detected (foreground)");
						c.setMoveSpeed(0, "left");
						
						
					} else if (c.getBoundingBox().getMaxX() <= fgE.getBoundingBox().getMinX()) {
						
						/* Collision with wall in the character's right side */
						System.out.println("Left stack collision detected (foreground)");
						c.setMoveSpeed(0, "right");
					}
					
					/* y axis */
					if (c.getBoundingBox().getMinY() >= fgE.getBoundingBox().getMaxY()) {
						
						/* Collision with wall in the character's up side */
						
						
					} else if (c.getBoundingBox().getMaxY() <= fgE.getBoundingBox().getMinY()) {
						
						/* Collision with wall in the character's down side */
//						System.out.println("Floor collision detected (foreground)");
						c.setySpeed(0);
					}
					
					fgE.setBoundingBoxColor(Color.YELLOW);
					c.setBoundingBoxColor(Color.YELLOW);
				}
				else {
					fgE.setBoundingBoxColor(Color.RED);
					c.setBoundingBoxColor(Color.RED);
				}
			}
		}
	}
}
