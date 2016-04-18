package states;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.imageio.ImageIO;

import entities.Torch;
import framework.Loader;
import game.Game;
import input.Key;
import kuusisto.tinysound.Music;
import kuusisto.tinysound.Sound;
import kuusisto.tinysound.TinySound;
import map.Background;
import map.MobileBackground;

public class ScenaryMenuState extends State{
	
	private MobileBackground bg;
	private BufferedImage sword;
	private BufferedImage[] options;
	private BufferedImage[] fights;
	
	private int currentChoice;
	
	private Sound moving;
	private Sound choosing;
	private Music menu;
	
	public ScenaryMenuState(GameStateManager gsm, ConcurrentLinkedQueue<Key> keys, Hashtable<String,Integer> keys_mapped, Loader loader){
		super(gsm, keys, keys_mapped, loader);
		
			
		moving = loader.getSound("sword moving");
		choosing = loader.getSound("sword vs sword");
		menu = TinySound.loadMusic(new File("resources/Music/cutscene_before_8_9.ogg"));
	}

	@Override
	public void init() {
		menu.play(true);
		bg = new MobileBackground("resources/Sprites_400/Menu/Scenaries/test1.png",0,0);
		bg.setDrawArrows(true);
		bg.addImage("resources/Sprites_400/Menu/Scenaries/test2.png");
		bg.addImage("resources/Sprites_400/Menu/Scenaries/test3.png");
		bg.addImage("resources/Sprites_400/Menu/Scenaries/test4.png");
		options = new BufferedImage[2];
		fights = new BufferedImage[2];
		try {
			options[0] = ImageIO.read(new File("resources/Sprites_400/Menu/fight.png"));
			options[1] = ImageIO.read(new File("resources/Sprites_400/Menu/back.png"));
			fights[0] = ImageIO.read(new File("resources/Sprites_400/Menu/fight.png"));
			fights[1] = ImageIO.read(new File("resources/Sprites_400/Menu/no_fight.png"));
			sword = ImageIO.read(new File("resources/Sprites_400/Menu/sword.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		currentChoice = 0;
	}

	@Override
	public void update(long elapsedTime) {
		manageKeys();
		bg.update(elapsedTime);
		if(bg.getVelx()!=0){
			options[0] = fights[1];
		} else{
			options[0] = fights[0];
		}
		// TODO Auto-generated method stub
		
	}

	@Override
	public void draw(Graphics2D g) {
		bg.draw(g);
		
		for (int i = 0; i < options.length; i++) {
			g.drawImage(options[i], Game.WIDTH/2 - options[i].getWidth()/2,
					Game.HEIGHT/2 + 120*Game.SCALE + i*30*Game.SCALE + options[i].getHeight(),
					null);
		}

		g.drawImage(sword, 
				Game.WIDTH/2 - options[currentChoice].getWidth()/2 - sword.getWidth() - 10*Game.SCALE,
				Game.HEIGHT/2 + 128*Game.SCALE + currentChoice*30*Game.SCALE,null);
	}
	
	public void select(){
		if(currentChoice == 0){
			if(bg.getVelx()==0){
				menu.stop();
				choosing.play();
			}
		} else{
			menu.stop();
			choosing.play();
			gsm.setState(3);
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
					
					if(key_pressed == keys_mapped.get(Key.RIGHT)||key_pressed == keys_mapped.get(Key.D)){
						if(bg.getVelx()==0){
							moving.play();
							bg.setVel(-20, 0);
						}
					} else if(key_pressed == keys_mapped.get(Key.LEFT) || key_pressed == keys_mapped.get(Key.A)){
						if(bg.getVelx()==0){
							moving.play();
							bg.setVel(20, 0);
						}
					} else if(key_pressed == keys_mapped.get(Key.ENTER) ||
							key_pressed == keys_mapped.get(Key.C) || 
							key_pressed == keys_mapped.get(Key.M)){
						select();
					} else if(key_pressed == keys_mapped.get(Key.S) ||
							key_pressed == keys_mapped.get(Key.W) ||
							key_pressed == keys_mapped.get(Key.DOWN) ||
							key_pressed == keys_mapped.get(Key.UP)){
						moving.play();
						currentChoice = (currentChoice + 1)%2;
					}
				}
			}
		}
	}
}
