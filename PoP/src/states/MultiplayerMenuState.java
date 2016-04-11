package states;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.imageio.ImageIO;

import entities.Character;
import entities.Enemy;
import entities.Player;
import entities.Torch;
import framework.Loader;
import game.Game;
import input.Key;
import kuusisto.tinysound.Music;
import kuusisto.tinysound.Sound;
import kuusisto.tinysound.TinySound;
import map.Background;

public class MultiplayerMenuState extends State{
	
	private Background bg;
	private BufferedImage title;
	private Character[] options;
	
	private int currentChoiceP1 = 0;
	private int currentChoiceP2 = 1;
	
	private int prince = -1;
	private int guard = -1;
	private boolean selectedAll;
	
	private Sound moving;
	private Sound choosing;
	private Music menu;
	
	private Torch t1,t2;
	
	public MultiplayerMenuState(GameStateManager gsm, ConcurrentLinkedQueue<Key> keys, Hashtable<String,Integer> keys_mapped, Loader loader){
		super(gsm, keys, keys_mapped, loader);
		
		try{
			bg = new Background("/Sprites_400/Menu/room_won.png");
			title = ImageIO.read(new File("resources/Sprites_400/Title/main titles/game name.png"));
			options = new Character[2];
			options[0] = new Player(294,330,loader,3,"right");
			options[1] = new Enemy(404,350,loader,"left", "red", 3, 3);
			options[0].setCurrentAnimation("idle_right", 7);
			options[0].update(0);
			options[1].setCurrentAnimation("idle_left", 100);
			options[1].manageSword("idle", 0, true);
			
			moving = loader.getSound("sword moving");
			choosing = loader.getSound("sword vs sword");
			menu = TinySound.loadMusic(new File("resources/Music/intro_theme.ogg"));
			t1 = new Torch(232,265,loader,true);
			t2 = new Torch(468,265,loader,true);
			selectedAll = false;
			
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
		manageKeys();
		t1.update(elapsedTime);
		t2.update(elapsedTime);
		options[0].update(elapsedTime);
		// TODO Auto-generated method stub
		
	}

	@Override
	public void draw(Graphics2D g) {
		bg.draw(g);
		g.drawImage(title, Game.WIDTH/2 - title.getWidth()/2,
				Game.HEIGHT/4 - title.getHeight()/2, null);
		
		for (int i = 0; i < options.length; i++) {
			
			options[i].drawSelf(g);
//			g.drawImage(options[i], Game.WIDTH/2 - options[i].getWidth()/2,
//					Game.HEIGHT/2 - 55*Game.SCALE + i*20*Game.SCALE + options[0].getHeight(),
//					null);
		}
		
		
		t1.drawSelf(g);
		t2.drawSelf(g);
		
//		g.drawImage(sword, 
//				Game.WIDTH/2 - options[currentChoice].getWidth()/2 - sword.getWidth() - 10*Game.SCALE,
//				Game.HEIGHT/2 - 47*Game.SCALE + currentChoice*20*Game.SCALE,null);
	}
	
	public void selectP1(){
		if(currentChoiceP1 == 0){
			if(prince == -1){
				choosing.play();
				prince = 0;
				Player p = (Player)options[0];
				p.isEnemySaw(true);
			}
		} else if(currentChoiceP1 == 1){
			if(guard == -1){
				choosing.play();
				guard = 0;
				options[1].setCurrentAnimation("sword idle_left", 7);
				options[1].manageSword("sword idle",0,true);
			}
		} else if(currentChoiceP1 == 2){
			if(prince != -1 && guard != -1){
				choosing.play();
				gsm.setState(gsm.VERSUS);
			}
		} else if(currentChoiceP1 == 3){
			System.out.println("jjejejeje");
			choosing.play();
			gsm.setState(gsm.MENUSTATE);
		}
	}
	
	public void selectP2(){
		if(currentChoiceP2 == 0){
			if(prince == -1){
				choosing.play();
				prince = 1;
				Player p = (Player)options[0];
				p.isEnemySaw(true);
			}
		} else if(currentChoiceP2 == 1){
			if(guard == -1){
				choosing.play();
				guard = 1;
				options[1].setCurrentAnimation("sword idle_left", 7);
				options[1].manageSword("sword idle",0,true);
			}
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
					System.out.println(key_pressed);
					
					if(key_pressed == keys_mapped.get(Key.W) || key_pressed == keys_mapped.get(Key.A)){
						moving.play();
						currentChoiceP1 = (currentChoiceP1 + 3)%4;
						if(currentChoiceP1 == 0 && prince == 1){
							currentChoiceP1 = 3;
						}
						if(currentChoiceP1 == 2 && (prince == -1 || guard == -1)){
							currentChoiceP1 = 1;
						}
						System.out.println(currentChoiceP1);
					} else if(key_pressed == keys_mapped.get(Key.S) || key_pressed == keys_mapped.get(Key.D)){
						moving.play();
						currentChoiceP1 = (currentChoiceP1 + 1)%4;
						if(currentChoiceP1 == 0 && prince == 1){
							currentChoiceP1 = 2;
						}
						if(currentChoiceP1 == 2 && (prince == -1 || guard == -1)){
							currentChoiceP1 = 3;
						}
						System.out.println(currentChoiceP1);
					} else if(key_pressed == keys_mapped.get(Key.UP) ||
							key_pressed == keys_mapped.get(Key.LEFT) ||
							key_pressed == keys_mapped.get(Key.RIGHT)||
							key_pressed == keys_mapped.get(Key.DOWN)){
						moving.play();
						currentChoiceP2 = (currentChoiceP2 + 1)%2;
						if(prince == 0 && currentChoiceP2 == 0){
							currentChoiceP2 = 1;
						} else if(guard == 0 && currentChoiceP2 == 1){
							currentChoiceP2 = 0;
						}
						System.out.println(currentChoiceP2);
					} else if(key_pressed == keys_mapped.get(Key.C)){
						selectP1();
					} else if(key_pressed == keys_mapped.get(Key.M)){
						selectP2();
					}
				}
			}
		}
	}
}
