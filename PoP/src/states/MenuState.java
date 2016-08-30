package states;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Hashtable;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.imageio.ImageIO;

import entities.Clock;
import entities.Princess;
import entities.Torch;
import framework.Loader;
import framework.RunningFromJar;
import framework.Writter;
import game.Game;
import game.Game3D;
import input.Key;
import kuusisto.tinysound.Music;
import kuusisto.tinysound.Sound;
import kuusisto.tinysound.TinySound;
import map.Background;

public class MenuState extends State{
	
	private Background bg;
	private BufferedImage title;
	private BufferedImage sword;
	private BufferedImage[] options;
	
	private int currentChoice = 0;
	
	private Sound moving;
	private Sound choosing;
	private Music menu;
	
	private Torch t1,t2;
	private Princess princess;
	private Clock clock;
	
	private boolean ddd;
	
	public MenuState(GameStateManager gsm, ConcurrentLinkedQueue<Key> keys, Hashtable<String,Integer> keys_mapped, Loader loader, Writter writter){
		super(gsm, keys, keys_mapped, loader, writter);
		
		try{
			
			if (RunningFromJar.isRunningFromJar()) {
				
				bg = new Background("/Sprites_400/Menu/room_won.png");
				title = ImageIO.read(getClass().getResourceAsStream(("/Sprites_400/Title/main titles/game name.png")));
				options = new BufferedImage[4];
				options[0] = ImageIO.read(getClass().getResourceAsStream("/Sprites_400/Menu/campaign.png"));
				options[1] = ImageIO.read(getClass().getResourceAsStream("/Sprites_400/Menu/versus.png"));
				options[2] = ImageIO.read(getClass().getResourceAsStream("/Sprites_400/Menu/settings.png"));
				options[3] = ImageIO.read(getClass().getResourceAsStream("/Sprites_400/Menu/exit.png"));
				sword = ImageIO.read(getClass().getResourceAsStream("/Sprites_400/Menu/sword.png"));
				moving = loader.getSound("sword moving");
				choosing = loader.getSound("sword vs sword");
				menu = TinySound.loadMusic(loader.getFile("Music/intro_theme.ogg"));
				t1 = new Torch(232,265,loader,true);
				t2 = new Torch(468,265,loader,true);
				princess = new Princess(260,350,loader);
				clock = new Clock(350,350,loader);
				ddd = false;
			}
			else {
				bg = new Background("resources/Sprites_400/Menu/room_won.png");
				title = ImageIO.read(new File("resources/Sprites_400/Title/main titles/game name.png"));
				options = new BufferedImage[4];
				options[0] = ImageIO.read(new File("resources/Sprites_400/Menu/campaign.png"));
				options[1] = ImageIO.read(new File("resources/Sprites_400/Menu/versus.png"));
				options[2] = ImageIO.read(new File("resources/Sprites_400/Menu/settings.png"));
				options[3] = ImageIO.read(new File("resources/Sprites_400/Menu/exit.png"));
				sword = ImageIO.read(new File("resources/Sprites_400/Menu/sword.png"));
				moving = loader.getSound("sword moving");
				choosing = loader.getSound("sword vs sword");
				menu = TinySound.loadMusic(new File("resources/Music/intro_theme.ogg"));
				t1 = new Torch(232,265,loader,true);
				t2 = new Torch(468,265,loader,true);
				princess = new Princess(280,334,loader);
				clock = new Clock(390,334,loader);
				ddd = false;
			}
			
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void init() {
		menu.play(true);
	}

	@Override
	public void update(long elapsedTime) {
		if(!ddd){
			manageKeys();
			t1.update(elapsedTime);
			t2.update(elapsedTime);
			clock.update(elapsedTime);
		}
	}

	@Override
	public void draw(Graphics2D g) {
		bg.draw(g);
		g.drawImage(title, Game.WIDTH/2 - title.getWidth()/2,
				Game.HEIGHT/4 - title.getHeight()/2, null);
		
		for (int i = 0; i < options.length; i++) {
			g.drawImage(options[i], Game.WIDTH/2 - options[i].getWidth()/2,
					Game.HEIGHT/2 - 55*Game.SCALE + i*20*Game.SCALE + options[0].getHeight(),
					null);
		}
		
		
		t1.drawSelf(g);
		t2.drawSelf(g);
		
		princess.drawSelf(g);
		clock.drawSelf(g);
		
		g.drawImage(sword, 
				Game.WIDTH/2 - options[currentChoice].getWidth()/2 - sword.getWidth() - 10*Game.SCALE,
				Game.HEIGHT/2 - 47*Game.SCALE + currentChoice*20*Game.SCALE,null);
	}
	
	public void select(){
		if(currentChoice == 0){
			choosing.play();
			menu.stop();
			gsm.setState(GameStateManager.MAINGAMESTATE);
			//campaign
		}
		else if(currentChoice == 1){
			menu.stop();
			gsm.setState(GameStateManager.MULTIPLAYERMENUSTATE);
		}
		else if(currentChoice == 2){
			choosing.play();
			this.set3d(true);
			Game3D.main(this, (LevelState)(gsm.getGameStates().get(gsm.MAINGAMESTATE)));
			//start settings
		}
		else{
			choosing.play();
			System.exit(0);
		}
	}
	
	public void set3d(boolean state){
		if(!state){
			menu.play(true);
		} else{
			menu.stop();
		}
		this.ddd = state;
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
					
					if(key_pressed == keys_mapped.get(Key.UP)||
							key_pressed == keys_mapped.get(Key.W)){
						moving.play();
						currentChoice = (currentChoice + 3)%4;
					} else if(key_pressed == keys_mapped.get(Key.DOWN) ||
							key_pressed == keys_mapped.get(Key.S)){
						moving.play();
						currentChoice = (currentChoice + 1)%4;
					} else if(key_pressed == keys_mapped.get(Key.LEFT)){
						
					} else if(key_pressed == keys_mapped.get(Key.RIGHT)){
						
					} else if(key_pressed == keys_mapped.get(Key.ENTER)|| 
							key_pressed == keys_mapped.get(Key.C)){
						select();
					}
				}
			}
		}
	}
}
