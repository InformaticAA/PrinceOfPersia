package states;

import java.awt.Graphics2D;
import java.util.ArrayList;

import tests.Test;

public class GameStateManager {
	
	private ArrayList<State> gameStates;
	private int currentState;
	
	public static final int TESTSTATE = -1;
	public static final int MENUSTATE = 0;
	public static final int MAINGAMESTATE = 1;
	
	public GameStateManager(){
		
		gameStates = new ArrayList<State>();
		
		currentState = TESTSTATE;
		gameStates.add(new Test(this));
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
