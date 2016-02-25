package states;

import java.awt.Graphics2D;
import java.io.File;
import java.io.FileReader;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;

import framework.Loader;
import input.Key;

public class LevelState extends State{
	
	/* Constants */
	private final float INIT_TIME = 3600;
	private final int INITIAL_HEALTH = 3;
	private final int INITIAL_LEVEL = 1;
	
	/* Variables */
	private boolean start;
	private float remainingTime;
	
	private Loader loader;
	
	private Level currentLevel;
	
	private Player player;
	
	
	public LevelState(GameStateManager gsm, ConcurrentLinkedQueue<Key> keys, boolean start, Loader loader) {
		super(gsm, keys);

		this.start = start;
		this.loader = loader;
	}

	@Override
	public void init() {
		if(start){
			
			/* Start game */
			remainingTime = INIT_TIME;
			currentLevel = loader.loadLevel(INITIAL_LEVEL);
			player = new Player(INITIAL_HEALTH);
		}
		
		else{
			
			/* Load game */
			File savegame = new File("savegame/save");
			if(savegame.exists() && !savegame.isDirectory()){
				
				/* There is actually a savegame -> resume game */
				Scanner save = new Scanner(new FileReader("savegame/save"));
				int line = 0;
				while(save.hasNextLine()){
					if(line == 0){
						currentLevel = loader.loadLevel(save.nextInt());
					} else if(line == 1){
						remainingTime = save.nextFloat();
					} else if(line == 2){
						player = new Player(save.nextInt());
					}
					line++;
					save.nextLine();
				}
				save.close();
			}
			else{
				
				/* There was not any savegame -> Start game */
				remainingTime = INIT_TIME;
				currentLevel = loader.loadLevel(INITIAL_LEVEL);
				player = new Player(INITIAL_HEALTH);
			}
			
		}
		
	}

	@Override
	public void update(long elapsedTime) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void draw(Graphics2D g) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void manageKeys() {
		// TODO Auto-generated method stub
		
	}
	
	public void initPlayer(){
		
	}

}
