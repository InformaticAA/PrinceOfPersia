package states;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.concurrent.ConcurrentLinkedQueue;

import framework.Loader;
import input.Key;
import input.KeyMapper;
import tests.Test;

public class GameStateManager {
	
	private ArrayList<State> gameStates;
	private int currentState;
	
	public static final int TESTSTATE = 0;
	public static final int MENUSTATE = 1;
	public static final int MAINGAMESTATE = 2;
	public static final int MULTIPLAYERMENUSTATE = 3;
	public static final int SCENARYMENUSTATE = 4;
	public static final int VERSUSSTATE = 5;
	
	public GameStateManager(ConcurrentLinkedQueue<Key> keys, Loader loader){
		
		gameStates = new ArrayList<State>();
		
		currentState = TESTSTATE;
		
		Hashtable<String, Integer> keys_mapped = new Hashtable<String, Integer>();
		KeyMapper key_mapper = new KeyMapper(keys_mapped);
		key_mapper.initDefaultKeys();
		gameStates.add(new Test(this, keys, keys_mapped, loader));
		gameStates.add(new MenuState(this,keys, keys_mapped, loader));
		gameStates.add(new LevelState(this, keys, keys_mapped, loader, true));
		gameStates.add(new MultiplayerMenuState(this,keys,keys_mapped,loader));
		gameStates.add(new ScenaryMenuState(this,keys,keys_mapped,loader));
		gameStates.add(new VersusState(this,keys,keys_mapped,loader));
	}
	
	public ArrayList<State> getGameStates(){
		return this.gameStates;
	}
	
	public State getState(int state){
		return this.gameStates.get(state);
	}
	
	public void setState(int state){
		currentState = state;
		gameStates.get(currentState).init();
	}
	
	public void update(long elapsedTime){
		gameStates.get(currentState).update(elapsedTime);
	}
	
	public void draw(Graphics2D g){
		gameStates.get(currentState).draw(g);
	}
}
