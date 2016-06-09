package game;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.JPanel;

import framework.Loader;
import input.Key;
import input.Listener;
import kuusisto.tinysound.TinySound;
import states.GameStateManager;

public class Game extends JPanel implements Runnable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8980406176996977071L;
	
	/* Game Dimensions */
	public static final int WIDTH = 640;
	public static final int HEIGHT = 400;
	public static final int SCALE = 1;
	
	/* Game Thread */
	private Thread gameLoop;
	private boolean running;
	private int FPS;
	private long targetTime;
	
	/* Image */
	private BufferedImage image;
	private Graphics2D g;
	
	/* Game State Manager */
	private GameStateManager gsm;
	
	/* Key Queue */
	private ConcurrentLinkedQueue<Key> keys;
	
	/* Game Data */
	private Loader loader;
	
	public Game(Loader loader){
		super();
		this.FPS = loader.getFPS();
		this.targetTime = 1000/this.FPS;
		setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		setFocusable(true);
		requestFocus();
		this.loader = loader;
	}
	
	public void addNotify(){
		super.addNotify();
		if(gameLoop == null){
			gameLoop = new Thread(this);
			keys = new ConcurrentLinkedQueue<Key>();
			addKeyListener(new Listener(keys));
			//Listener
			gameLoop.start();
		}
	}
	
	private void init(){
		
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
		g = (Graphics2D) image.getGraphics();
		
		running = true;
		
		gsm = new GameStateManager(keys,loader);
		gsm.setState(1);
	}
	
	public void run(){
		
		init();
		
		long start;
		long elapsed;
		long wait;
		
		//Game Loop Starting
		while(running){
			
			start =  System.nanoTime();
			
			update(targetTime);
			draw();
			drawToScreen();
			
			elapsed = System.nanoTime() - start;
			
			wait = targetTime - elapsed / 1000000;
			
			if(wait>0){
				try{
					Thread.sleep(wait);
				} catch(Exception e){
					e.printStackTrace();
				}
			} else{
//				System.out.println("Too slow");
			}
		}
	}
	
	private void update(long elapsedTime){
		gsm.update(elapsedTime);
	}
	
	private void draw(){
		g.clearRect(0, 0, getWidth(), getHeight());
		gsm.draw(g);
	}
	
	private void drawToScreen(){
		Graphics g2 = getGraphics();
		g2.drawImage(image, 0, 0, null);
		g2.dispose();
		
	}
	
	public boolean isRunning(){
		return this.running;
	}

}
