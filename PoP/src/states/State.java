package states;

import java.awt.Graphics2D;
import java.util.concurrent.ConcurrentLinkedQueue;

import types.Key;

public abstract class State {
	
	protected GameStateManager gsm;
	protected ConcurrentLinkedQueue<Key> keys;
	
	public State(GameStateManager gsm, ConcurrentLinkedQueue<Key> keys){
		this.gsm = gsm;
		this.keys = keys;
	}
	
	public abstract void init();
	public abstract void update(long elapsedTime);
	public abstract void draw(Graphics2D g);
	public abstract void manageKeys();
}
