package game;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

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
	private int FPS = 60;
	private long targetTime = 1000/FPS;
	
	/* Image */
	private BufferedImage image;
	private Graphics2D g;
	
	/* Game State Manager */
	private GameStateManager gsm;
	
	
	public Game(){
		super();
		setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		setFocusable(true);
		requestFocus();
	}
	
	public void addNotify(){
		super.addNotify();
		if(gameLoop == null){
			gameLoop = new Thread(this);
			addKeyListener(new Listener());
			//Listener
			gameLoop.start();
		}
	}
	
	private void init(){
		
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_BYTE_INDEXED);
		g = (Graphics2D) image.getGraphics();
		
		running = true;
		
		gsm = new GameStateManager();
	}
	
	public void run(){
		
		init();
		
		long start;
		long elapsed;
		long wait;
		
		//Game Loop Starting
		while(running){
			
			start =  System.nanoTime();
			
			update();
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
				System.out.println("Too slow");
			}
		}
	}
	
	private void update(){
		gsm.update();
	}
	
	private void draw(){
		gsm.draw(g);
	}
	
	private void drawToScreen(){
		Graphics g2 = getGraphics();
		g2.drawImage(image, 0, 0, null);
		g2.dispose();
		
	}

}