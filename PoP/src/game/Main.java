package game;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Main extends Applet implements Runnable, KeyListener {

	private static final long serialVersionUID = 1L;

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

		while (true) {

			/* Frame work */
			repaint();

			try {
				Thread.sleep(17);
			} catch (InterruptedException e) {
				e.printStackTrace();
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
