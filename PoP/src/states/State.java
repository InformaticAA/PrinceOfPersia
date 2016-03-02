package states;

import java.awt.Graphics2D;
import java.util.Hashtable;
import java.util.concurrent.ConcurrentLinkedQueue;

import framework.Loader;
import input.Key;

public abstract class State {
	
	protected GameStateManager gsm;
	protected ConcurrentLinkedQueue<Key> keys;
	protected Hashtable<String,Integer> keys_mapped;
	protected Loader loader;
	
	public State(GameStateManager gsm, ConcurrentLinkedQueue<Key> keys, Hashtable<String,Integer> keys_mapped, Loader loader){
		this.gsm = gsm;
		this.keys = keys;
		this.keys_mapped = keys_mapped;
		this.loader = loader;
	}
	
	public abstract void init();
	public abstract void update(long elapsedTime);
	public abstract void draw(Graphics2D g);
	public abstract void manageKeys();
}
