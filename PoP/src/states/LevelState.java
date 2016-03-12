package states;

import java.awt.Graphics2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;

import data.Level;
import data.Room;
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
			player = new Player(450,365,loader);
			currentRoom.addEntity(player);
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
		manageKeys();
		remainingTime = remainingTime - elapsedTime;
		currentRoom.update(elapsedTime);
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
					
				}
			}
		}
	}
	
	public void initPlayer(){
		
	}

}
