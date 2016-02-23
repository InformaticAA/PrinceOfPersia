package game;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.concurrent.ConcurrentLinkedQueue;

import types.Key;

public class Listener implements KeyListener{
	
	private ConcurrentLinkedQueue<Key> keys;
	
	public Listener(ConcurrentLinkedQueue<Key> keys){
		this.keys = keys;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		keys.add(new Key(true,e));
	}

	@Override
	public void keyReleased(KeyEvent e) {
		keys.add(new Key(false,e));
	}

	

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

}
