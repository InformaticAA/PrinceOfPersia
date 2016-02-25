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
	}
}
