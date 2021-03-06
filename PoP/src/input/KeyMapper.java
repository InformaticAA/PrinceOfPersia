package input;

import java.awt.event.KeyEvent;
import java.util.Hashtable;

import input.Key;

public class KeyMapper {

	private Hashtable<String,Integer> key_map;
	
	public KeyMapper(Hashtable<String, Integer> key_map){
		this.key_map = key_map;
	}
	
	public void addKey(String action, int key){
		key_map.put(action, key);
	}
	
	public void initDefaultKeys(){
		key_map.put(Key.UP, KeyEvent.VK_UP);
		key_map.put(Key.DOWN, KeyEvent.VK_DOWN);
		key_map.put(Key.LEFT, KeyEvent.VK_LEFT);
		key_map.put(Key.RIGHT, KeyEvent.VK_RIGHT);
		key_map.put(Key.ENTER, KeyEvent.VK_ENTER);
		key_map.put(Key.SHIFT, KeyEvent.VK_SHIFT);
		key_map.put(Key.ESCAPE, KeyEvent.VK_ESCAPE);
		key_map.put(Key.CONTROL, KeyEvent.VK_CONTROL);
		key_map.put(Key.W, KeyEvent.VK_W);
		key_map.put(Key.A ,KeyEvent.VK_A);
		key_map.put(Key.S, KeyEvent.VK_S);
		key_map.put(Key.D, KeyEvent.VK_D);
		key_map.put(Key.M, KeyEvent.VK_M);
		key_map.put(Key.C, KeyEvent.VK_C);
		key_map.put(Key.R, KeyEvent.VK_R);
		key_map.put(Key.SPACE, KeyEvent.VK_SPACE);
	}
}
