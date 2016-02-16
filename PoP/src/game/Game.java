package game;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Game extends Applet implements Runnable, KeyListener {

	private static final long serialVersionUID = 1L;
	
	/* Game loop atributes */
	final static int FRAMES_PER_SECOND = 30;
	final static int SKIP_TICKS = 1000 / FRAMES_PER_SECOND;
	public static boolean game_is_running;

	@Override
	public void init() {
		super.init();
		System.out.println("init");

		/* Applet params */
		setSize(800, 480);
		setBackground(Color.BLACK);
		setFocusable(true);
		addKeyListener(this);
		Frame frame = (Frame) this.getParent().getParent();
		frame.setTitle("Prince of Persia");
	}

	@Override
	public void start() {
		super.start();
		Thread thread = new Thread(this);
		thread.start();
		System.out.println("start");
	}

	@Override
	public void stop() {
		super.stop();
		System.out.println("stop");
	}

	@Override
	public void destroy() {
		super.destroy();
		System.out.println("destroy");
	}

	@Override
	public void run() {
		
		long next_game_tick = System.currentTimeMillis();
	    // GetTickCount() returns the current number of milliseconds
	    // that have elapsed since the system was started

	    long sleep_time = 0;

	    game_is_running = true;
	    
	    while( game_is_running ) {
	        
	    	repaint();
	    	
	    	//update_game();
	        
	    	//display_game();

	        next_game_tick += SKIP_TICKS;
	        sleep_time = next_game_tick - System.currentTimeMillis();
	        if( sleep_time >= 0 ) {
	        	
	        	try{
	        		Thread.sleep(sleep_time);
	        	} catch (InterruptedException e){
	        		e.printStackTrace();
	        	}
	        }
	        else {
	        	System.out.println("Running slow");
	            // Shit, we are running behind!
	        }
	    }
	}

	@Override
	public void keyPressed(KeyEvent e) {

		switch (e.getKeyCode()) {

		case KeyEvent.VK_UP:
			break;

		case KeyEvent.VK_DOWN:
			break;

		case KeyEvent.VK_LEFT:
			break;

		case KeyEvent.VK_RIGHT:
			break;

		case KeyEvent.VK_SHIFT:
			break;

		}
	}

	@Override
	public void keyReleased(KeyEvent e) {

		switch (e.getKeyCode()) {

		case KeyEvent.VK_UP:
			break;

		case KeyEvent.VK_DOWN:
			break;

		case KeyEvent.VK_LEFT:
			break;

		case KeyEvent.VK_RIGHT:
			break;

		case KeyEvent.VK_SHIFT:
			break;

		}
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

}
