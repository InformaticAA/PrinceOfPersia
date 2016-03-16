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
			player = new Player(100,130,loader, "left");
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
		checkCollisions();
		currentLevel.update(elapsedTime);
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
	
	
	public void initPlayer(){
		
	}

	private void checkCollisions() {
		
		/* Checks for collisions in the currentRoom */
		ArrayList<Entity> bgEntities = currentRoom.getBackground();
		ArrayList<Character> characters = currentRoom.getCharacters();
		ArrayList<Entity> fgEntities = currentRoom.getForeground();
		
		for (Character c : characters) {
			
			/* Collisions with background */
			for (Entity bgE : bgEntities) {
				
				if (c.intersects(bgE)) {
					
//					double minY = bgE.getBoundingBox().getMinY();
//					double maxY = bgE.getBoundingBox().getMaxY();
//					double playerMinY = c.getBoundingBox().getMinY();
//					double playerMaxY = c.getBoundingBox().getMaxY();
//					
//					System.out.println("minY: " + minY + " - maxY: " + maxY);
//					System.out.println("playerMinY: " + playerMinY + " - playerMaxY: " + playerMaxY);
					
					/* x axis */
					if (c.getBoundingBox().getMinX() >= bgE.getBoundingBox().getMaxX()) {
						
						/* Collision with wall in the character's left side */
						System.out.println("Face stack collision detected (background)");
						c.setMoveSpeed(0, "left");
						
					} else if (c.getBoundingBox().getMaxX() <= bgE.getBoundingBox().getMinX()) {
						
						/* Collision with wall in the character's right side */
						System.out.println("Left stack collision detected (background)");
						c.setMoveSpeed(0, "right");
						
					}
					
					/* y axis */
					if (c.getBoundingBox().getMinY() >= bgE.getBoundingBox().getMaxY()) {
						
						/* Collision with wall in the character's up side */
						
						
					} else if (c.getBoundingBox().getMaxY() <= bgE.getBoundingBox().getMinY()) {
						
						/* Collision with wall in the character's down side */
						System.out.println("Floor collision detected (background)");
						c.setySpeed(0);
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
				if (fgE.intersects(c)) {
					
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
