package states;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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
import map.MobileBackground;

public class MultiplayerMenuState extends State{
	
	
	private final int YVEL = -5;
	
	private MobileBackground bg;
	private BufferedImage title;
	private Character[] options;
	
	private int currentChoiceP1, currentChoiceP2;
	
	private int prince;
	private int guard;
	
	private Sound moving;
	private Sound choosing;
	private Music menu;
	
	private Torch t1,t2;
	BufferedImage p1,p2,sword;
	BufferedImage[] menuOptions;
	private int offset;
	private boolean initialAnimation;
	private int yvel;
	
	public MultiplayerMenuState(GameStateManager gsm, ConcurrentLinkedQueue<Key> keys, Hashtable<String,Integer> keys_mapped, Loader loader){
		super(gsm, keys, keys_mapped, loader);
		
		try{
			bg = new MobileBackground("resources/Sprites_400/Menu/room_won.png");
			bg.addImage("resources/Sprites_400/Menu/walls.png");
			bg.addImage("resources/Sprites_400/Menu/walls.png");
			bg.addImage("resources/Sprites_400/Menu/walls.png");
			bg.addImage("resources/Sprites_400/Menu/walls.png");
			bg.addImage("resources/Sprites_400/Menu/walls.png");
			bg.addImage("resources/Sprites_400/Menu/Scenaries/test1.png");
			title = ImageIO.read(new File("resources/Sprites_400/Title/main titles/game name.png"));
			menu = TinySound.loadMusic(new File("resources/Music/intro_theme.ogg"));
			t1 = new Torch(232,265,loader,true);
			t2 = new Torch(468,265,loader,true);
			
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void init() {
		bg.setDrawArrows(false);
		yvel = YVEL;
		bg.setVel(0, yvel);
		
		initialAnimation = true;
		options = new Character[2];
		options[0] = new Player(200,6*400 + 240,loader,3,"right");
		options[1] = new Enemy(460,6*400 + 260,loader,"left", "red", 3, 3);
		options[0].setCurrentAnimation("idle_right", 7);
		options[0].update(0);
		options[1].setCurrentAnimation("idle_left", 100);
		options[1].manageSword("idle", 0, true);
		
		moving = loader.getSound("sword moving");
		choosing = loader.getSound("sword vs sword");
		menu.play(true);
		currentChoiceP1 = 0;
		currentChoiceP2 = 1;
		prince = -1;
		guard = -1;
	}

	@Override
	public void update(long elapsedTime) {
		if(initialAnimation){
			if(bg.getCurrentBackground()==1){
				this.yvel = 4*YVEL;
				bg.setVel(0, yvel);
			} else if(bg.getCurrentBackground()==5){
				this.yvel = YVEL;
				bg.setVel(0, yvel);
			}
			offset = offset + yvel;
			t1.update(elapsedTime);
			t1.setY(t1.getY() + yvel);
			t2.update(elapsedTime);
			t2.setY(t2.getY() + yvel);
			options[0].setY(options[0].getY() + yvel);
			options[1].setY(options[1].getY() + yvel);
			options[0].update(elapsedTime);
			bg.update(elapsedTime);
			if(bg.getCurrentBackground()==6){
				initialAnimation = false;
				bg.setVel(0, 0);
				menuOptions = new BufferedImage[2];
				try {
					menuOptions[0] = ImageIO.read(new File("resources/Sprites_400/Menu/no_next.png"));
					menuOptions[1] = ImageIO.read(new File("resources/Sprites_400/Menu/back.png"));
					p1 = ImageIO.read(new File("resources/Sprites_400/Menu/p1.png")); 
					p2 = ImageIO.read(new File("resources/Sprites_400/Menu/p2.png")); 
					sword = ImageIO.read(new File("resources/Sprites_400/Menu/sword.png"));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else{
			options[0].update(elapsedTime);
		}
		
		manageKeys();
		
		// TODO Auto-generated method stub
		
	}

	@Override
	public void draw(Graphics2D g) {
		bg.draw(g);
		g.drawImage(title, Game.WIDTH/2 - title.getWidth()/2,
				Game.HEIGHT/4 - title.getHeight()/2 + offset, null);
		
		for (int i = 0; i < options.length; i++) {
			options[i].drawSelf(g);
		}
		if(!initialAnimation){
			for(int i = 0; i < menuOptions.length; i++){
				g.drawImage(menuOptions[i], Game.WIDTH/2 - menuOptions[i].getWidth()/2,
						Game.HEIGHT/2 + 128*Game.SCALE + i*30*Game.SCALE + menuOptions[0].getHeight(),
						null);
			}
			
			if(currentChoiceP1 == 0 && currentChoiceP2 == 0){
				g.drawImage(p1,Game.WIDTH/2-150-p1.getWidth()/2,Game.HEIGHT/2 - 70,null);
				g.drawImage(p2,Game.WIDTH/2-150+p2.getWidth()/2,Game.HEIGHT/2 - 70,null);
			} else if(currentChoiceP1 == 1 && currentChoiceP2 == 1){
				g.drawImage(p1,Game.WIDTH/2+106-p1.getWidth()/2,Game.HEIGHT/2 - 70,null);
				g.drawImage(p2,Game.WIDTH/2+106+p2.getWidth()/2,Game.HEIGHT/2 - 70,null);
			} 
			
			if(currentChoiceP1 == 0 && currentChoiceP2 != 0){
				g.drawImage(p1,Game.WIDTH/2-146,Game.HEIGHT/2 - 70,null);
			} else if(currentChoiceP1 == 1 && currentChoiceP2 != 1){
				g.drawImage(p1,Game.WIDTH/2+110,Game.HEIGHT/2 - 70,null);
			} else if(currentChoiceP1 >= 2 && currentChoiceP1 <=3){
				g.drawImage(sword, 
						Game.WIDTH/2 - menuOptions[currentChoiceP1 - 2].getWidth()/2 - sword.getWidth() - 10*Game.SCALE,
						Game.HEIGHT/2 + 136*Game.SCALE + (currentChoiceP1-2)*30*Game.SCALE,null);
			}
			
			if(currentChoiceP2 == 0 && currentChoiceP1 != 0){
				g.drawImage(p2,Game.WIDTH/2-148,Game.HEIGHT/2 - 70,null);
			} else if(currentChoiceP2 == 1 && currentChoiceP1 != 1){
				g.drawImage(p2,Game.WIDTH/2+108,Game.HEIGHT/2 - 70,null);
			}
			
			if(prince == 0){
				g.drawImage(p1,Game.WIDTH/2-146,Game.HEIGHT/2 - 70,null);
			} else if(prince == 1){
				g.drawImage(p2,Game.WIDTH/2-112,Game.HEIGHT/2 - 70,null);
			}
			
			if(guard == 0){
				g.drawImage(p1,Game.WIDTH/2+136,Game.HEIGHT/2 - 70,null);
			} else if(guard == 1){
				g.drawImage(p2,Game.WIDTH/2+108,Game.HEIGHT/2 - 70,null);
			}
		}
		
		
		t1.drawSelf(g);
		t2.drawSelf(g);
	}
	
	public void selectP1(){
		if(currentChoiceP1 == 0){
			if(prince == -1){
				choosing.play();
				prince = 0;
				if(currentChoiceP2 == 0){
					currentChoiceP2 = 1;
				}
				Player p = (Player)options[0];
				p.isEnemySaw(true);
				if(guard == 1){
					currentChoiceP1 = 2;
					try {
						menuOptions[0] = ImageIO.read(new File("resources/Sprites_400/Menu/next.png"));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		} else if(currentChoiceP1 == 1){
			if(guard == -1){
				choosing.play();
				guard = 0;
				if(currentChoiceP2 == 1){
					currentChoiceP2 = 0;
				}
				options[1].setCurrentAnimation("sword idle_left", 7);
				options[1].manageSword("sword idle",0,true);
				if(prince == 1){
					currentChoiceP1 = 2;
					try {
						menuOptions[0] = ImageIO.read(new File("resources/Sprites_400/Menu/next.png"));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		} else if(currentChoiceP1 == 2){
			if(prince != -1 && guard != -1){
				choosing.play();
				menu.stop();
				gsm.setState(GameStateManager.VERSUS);
			}
		} else if(currentChoiceP1 == 3){
			choosing.play();
			menu.stop();
			prince = -1;
			guard = -1;
			gsm.setState(GameStateManager.MENUSTATE);
		}
	}
	
	public void selectP2(){
		if(currentChoiceP2 == 0){
			if(prince == -1){
				choosing.play();
				prince = 1;
				if(currentChoiceP1 == 0){
					currentChoiceP1 = 1;
				}
				Player p = (Player)options[0];
				p.isEnemySaw(true);
				if(guard == 0){
					currentChoiceP1 = 2;
					try {
						menuOptions[0] = ImageIO.read(new File("resources/Sprites_400/Menu/next.png"));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		} else if(currentChoiceP2 == 1){
			if(guard == -1){
				choosing.play();
				guard = 1;
				if(currentChoiceP1 == 1){
					currentChoiceP1 = 0;
				}
				options[1].setCurrentAnimation("sword idle_left", 7);
				options[1].manageSword("sword idle",0,true);
				if(prince == 0){
					currentChoiceP1 = 2;
					try {
						menuOptions[0] = ImageIO.read(new File("resources/Sprites_400/Menu/next.png"));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		} 
	}

	@Override
	public void manageKeys() {
		Object[] keys_used = keys.toArray();
		keys.clear();
		Key e;
		if(!initialAnimation){
			if(keys_used.length!=0){
				for (int i = 0; i < keys_used.length; i++) {
					e = (Key)keys_used[i];
					if(e.isPressed()){
						/* key pressed */
						int key_pressed = e.getKeycode();
						System.out.println(key_pressed);
						if(key_pressed == keys_mapped.get(Key.A) || 
								key_pressed == keys_mapped.get(Key.D)){
							if(prince != 1 && guard != 1){
								if(currentChoiceP1 < 2){
									moving.play();
									currentChoiceP1 = (currentChoiceP1 + 1)%2;
									if(prince == 0 && currentChoiceP1 == 0){
										currentChoiceP1 = 1;
									} else if(guard == 0 && currentChoiceP1 == 1){
										currentChoiceP1 = 0;
									}
								}
							}
						}
						
						else if(key_pressed == keys_mapped.get(Key.W)){
							moving.play();
							if(currentChoiceP1 < 2){
								currentChoiceP1 = 3;
							} else if(currentChoiceP1 == 3){
								if(prince == -1 || guard == -1){
									if(prince == 0 || guard == 1){
										currentChoiceP1 = 0;
									} else{
										currentChoiceP1 = 1;
									}
								} else{
									currentChoiceP1 = 2;
								}
							} else {
								if(prince == -1 || guard == -1){
									if(prince == 0 || guard == 1){
										currentChoiceP1 = 0;
									} else{
										currentChoiceP1 = 1;
									}
								} else{
									currentChoiceP1 = 3;
								}
							}
							
						} else if(key_pressed == keys_mapped.get(Key.S)){
							moving.play();
							if(currentChoiceP1 < 2){
								if(prince == -1 || guard == -1){
									currentChoiceP1 = 3;
								} else{
									currentChoiceP1 = 2;
								}
							} else if(currentChoiceP1 == 2){
								currentChoiceP1 = 3;
							} else{
								if(prince == -1 || guard == -1){
									if(prince == 0 || guard == 1){
										currentChoiceP1 = 0;
									} else{
										currentChoiceP1 = 1;
									}
								} else{
									currentChoiceP1 = 2;
								}
							}
						} else if(key_pressed == keys_mapped.get(Key.S) || key_pressed == keys_mapped.get(Key.D)){
							moving.play();
							currentChoiceP1 = (currentChoiceP1 + 1)%4;
							if(currentChoiceP1 == 0 && prince == 1){
								currentChoiceP1 = 1;
							} else if(currentChoiceP1 == 0 && (prince == 0 || guard == 0)){
								currentChoiceP1 = 2;
							}
							if(currentChoiceP1 == 1 && guard == 1){
								currentChoiceP1 = 2;
							} else if(currentChoiceP1 == 1 && (prince == 0 || guard == 0)){
								currentChoiceP1 = 2;
							}
							if(currentChoiceP1 == 2 && (prince == -1 || guard == -1)){
								currentChoiceP1 = 3;
							}
							System.out.println(currentChoiceP1);
						} else if(key_pressed == keys_mapped.get(Key.LEFT) ||
								key_pressed == keys_mapped.get(Key.RIGHT)){
							if(prince != 1 && guard != 1){
								moving.play();
								currentChoiceP2 = (currentChoiceP2 + 1)%2;
								if(prince == 0 && currentChoiceP2 == 0){
									currentChoiceP2 = 1;
								} else if(guard == 0 && currentChoiceP2 == 1){
									currentChoiceP2 = 0;
								}
							}
							System.out.println(currentChoiceP2);
						} else if(key_pressed == keys_mapped.get(Key.C) ||
								key_pressed == keys_mapped.get(Key.ENTER)){
							selectP1();
						} else if(key_pressed == keys_mapped.get(Key.M)){
							selectP2();
						}
					}
				}
			}
		}
	}
}
