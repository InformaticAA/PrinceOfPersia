package states;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;

import data.Level;
import data.Room;
import entities.Character;
import entities.Entity;
import entities.Player;
import framework.Loader;
import input.Key;

public class LevelState extends State{
	
	/* Constants */
	private final float INIT_TIME = 3600000;
	private final int INITIAL_HEALTH = 3;
	private final int INITIAL_LEVEL = 1;
	
	/* Variables */
	private boolean start;
	private float remainingTime;
	private Level currentLevel;
	private Room currentRoom;
	
	private Player player;
	
	
	public LevelState(GameStateManager gsm, ConcurrentLinkedQueue<Key> keys, Hashtable<String,Integer> keys_mapped, Loader loader, boolean start) {
		super(gsm, keys, keys_mapped, loader);

		this.start = start;
	}

	@Override
	public void init() {
		if(start){
			
			/* Start game */
			remainingTime = INIT_TIME;
			currentLevel = loader.loadLevel(INITIAL_LEVEL);
			currentRoom = currentLevel.getRoom(1, 7);
			
//			for(String key : loader.getAnimations("wall").keySet()){
//				System.out.println("key "+ key + " - Animation " + loader.getAnimations("wall").get(key).getId() );
//			}
			player = new Player(400,120,loader, "left");
			player.setySpeed(4);
			
			currentRoom.addCharacter(player);
		}
		
		else{
			
			/* Load game */
			File savegame = new File("savegame/save");
			if(savegame.exists() && !savegame.isDirectory()){
				
				/* There is actually a savegame -> resume game */
				Scanner save;
				try {
					save = new Scanner(new FileReader("savegame/save"));
					int line = 0;
					while(save.hasNextLine()){
						if(line == 0){
							currentLevel = loader.loadLevel(save.nextInt());
						} else if(line == 1){
							remainingTime = save.nextFloat();
						} else if(line == 2){
//							player = new Player(save.nextInt());
						}
						line++;
						save.nextLine();
					}
					save.close();
				} catch (FileNotFoundException e) {
					
					/* Exception */
					e.printStackTrace();

					/* There was not any savegame -> Start game */
					remainingTime = INIT_TIME;
					currentLevel = loader.loadLevel(INITIAL_LEVEL);
					currentRoom = currentLevel.getRoom(1, 7);
//					player = new Player(INITIAL_HEALTH);
				}
			}
			else{
				
				/* There was not any savegame -> Start game */
				remainingTime = INIT_TIME;
				currentLevel = loader.loadLevel(INITIAL_LEVEL);
				currentRoom = currentLevel.getRoom(1, 7);
//				player = new Player(100,300,loader);
			}
			
		}
		
	}

	@Override
	public void update(long elapsedTime) {
		remainingTime = remainingTime - elapsedTime;

		manageKeys();
		currentLevel.update(elapsedTime);
		checkCollisions(elapsedTime);
	}

	@Override
	public void draw(Graphics2D g) {
		currentRoom.draw(g);
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
						
					} else if(key_pressed == keys_mapped.get(Key.CONTROL)){
						
					} else{
						player.manageKeyPressed(key_pressed, keys_mapped);
					}
					
					/* this has to be sent to the player */
					if(key_pressed == keys_mapped.get(Key.UP)){
						
					} else if(key_pressed == keys_mapped.get(Key.DOWN)){
						
					} else if(key_pressed == keys_mapped.get(Key.LEFT)){
						
					} else if(key_pressed == keys_mapped.get(Key.RIGHT)){
						
					} else if(key_pressed == keys_mapped.get(Key.ENTER)){
						
					} else if(key_pressed == keys_mapped.get(Key.SHIFT)){
						
					}
				} else{
					
					/* Key released */
					int key_released = e.getKeycode();
					
					if(key_released == keys_mapped.get(Key.ESCAPE)){
						
					} else if(key_released == keys_mapped.get(Key.CONTROL)){
						
					} else{
						player.manageKeyReleased(key_released, keys_mapped);
					}
				}
			}
		}
	}

	/**
	 * Checks every collision in the current room between a
	 * character and the background
	 * @param elapsedTime
	 */
	private void checkCollisions(long elapsedTime) {
		
		boolean collidingB = false;
		boolean collidingF = false;
		player.setGrounded(false);
		
		/* Checks for collisions in the currentRoom */
		ArrayList<Entity> bgEntities = currentRoom.getBackground();
		ArrayList<Character> characters = currentRoom.getCharacters();
		ArrayList<Entity> fgEntities = currentRoom.getForeground();
		
		for (Character c : characters) {
			
			/* Collisions with background */
			for (Entity bgE : bgEntities) {
				collidingB = handleCollision(c, bgE, elapsedTime);
			}
			
			/* Collisions with foreground */
			for (Entity fgE : fgEntities) {
				collidingF = handleCollision(c, fgE, elapsedTime);
			}
			
			if (!collidingB && !collidingF) {
				c.setBoundingBoxColor(Color.RED);
			}
			
		}
	}
	
	/**
	 * Handles a single collision between a character and
	 * a background/foreground entity
	 * @param c character colliding
	 * @param e background/foreground entity colliding
	 * @param elapsedTime
	 */
	private boolean handleCollision(Character c, Entity e, long elapsedTime) {
		
		boolean colliding = false;

		if (e.getBoundingBox() != null) {
			
			/* Character's bounding box */
			int cLeft = (int) c.getBoundingBox().getMinX();
			int cRight = (int) c.getBoundingBox().getMaxX();
			int cTop = (int) c.getBoundingBox().getMinY();
			int cBottom = (int) c.getBoundingBox().getMaxY();
			
			/* Background entity's bounding box */
			int bgLeft = (int) e.getBoundingBox().getMinX();
			int bgRight = (int) e.getBoundingBox().getMaxX();
			int bgTop = (int) e.getBoundingBox().getMinY();
			int bgBottom = (int) e.getBoundingBox().getMaxY();
			
			if (c.intersects(e, elapsedTime)) {
				
				int vSpeed = c.getySpeed();
				int hSpeed = c.getxSpeed();
				
				Rectangle intersection = c.getBoundingBox().intersection(e.getBoundingBox());
				
				boolean vertical = intersection.width > intersection.height;
				boolean horizontal = intersection.height >= intersection.width;
				
//				boolean vertical = Math.abs(vSpeed) >= Math.abs(hSpeed);
//				boolean horizontal = Math.abs(vSpeed) < Math.abs(hSpeed);
				
				/* Checks vertical collisions */
				if ( vertical && (vSpeed < 0) && (cTop < bgBottom)  ) {
					
					/* Player was jumping */
					while( c.intersects(e, elapsedTime) && (cTop <= bgBottom) ) {
						c.move(0, 1);
						cTop = (int) c.getBoundingBox().getMinY();
						bgBottom = (int) e.getBoundingBox().getMaxY();
					}
					
				}
				else if ( vertical && (vSpeed > 0) && (cBottom > bgTop) ) {
					
					/* Player was falling */
					while ( c.intersects(e, elapsedTime) || (cBottom >= bgTop) ) {
						c.move(0, -1);
						cBottom = (int) c.getBoundingBox().getMaxY();
						bgTop = (int) e.getBoundingBox().getMinY();
					}
					c.setGrounded(true);
					
				}
				
				/* Checks horizontal collisions */
				if ( horizontal && (hSpeed > 0) && (cRight > bgLeft)  ) {
					
					/* Character was heading right */
					while( c.intersects(e, elapsedTime) && (cRight >= bgLeft) ) {
						c.move(-1, 0);
						cRight = (int) c.getBoundingBox().getMaxX();
						bgLeft = (int) e.getBoundingBox().getMinX();
					}
					player.setCollided();
				}
				else if ( horizontal && (hSpeed < 0) && (cLeft < bgRight) ) {
					
					/* Character was heading left */
					while ( c.intersects(e, elapsedTime) && (cLeft <= bgRight) ) {
						c.move(1, 0);
						cLeft = (int) c.getBoundingBox().getMinX();
						bgRight = (int) e.getBoundingBox().getMaxX();
					}
					player.setCollided();
				}
				
				/* Debug */
				e.setBoundingBoxColor(Color.YELLOW);
				c.setBoundingBoxColor(Color.YELLOW);
				colliding = true;
			}
			else {
				
				/* Objects dont collide (floor) */
				if ( (cBottom + 1) == bgTop ) {
					
					/* Character is walking over the floor */
					if ( (cLeft > bgLeft && cLeft < bgRight) ||
							(cRight > bgLeft && cRight < bgRight) ||
							(cLeft > bgLeft && cRight < bgRight) ||
							(cLeft < bgLeft && cRight > bgRight) ){
						
						/* Character walking over one particular floor panel,
						 * thus it is grounded */
						c.setGrounded(true);
					}
				}
				
				/* Debug */
				e.setBoundingBoxColor(Color.RED);
			}
		}
		return colliding;
	}
	
}
