package states;

import java.awt.Graphics2D;
import java.util.ArrayList;

public class GameStateManager {
	
	private ArrayList<State> gameStates;
	private int currentState;
	
	public static final int MENUSTATE = 0;
	public static final int MAINGAMESTATE = 1;
	
	public GameStateManager(){
		
		gameStates = new ArrayList<State>();
		
		currentState = MENUSTATE;
		gameStates.add(new MenuState(this));
	}
	
	public void setState(int state){
		currentState = state;
		gameStates.get(currentState).init();
	}
	
	public void update(){
		gameStates.get(currentState).update();
	}
	
	public void draw(Graphics2D g){
		gameStates.get(currentState).draw(g);
	}
}
