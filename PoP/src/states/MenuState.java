package states;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import map.Background;

public class MenuState extends State{
	
	private Background bg;
	
	private int currentChoice = 0;
	private String[] options = {"Campaign", "Multiplayer", 
			"Settings", "Exit Game"};
	private Color titleColor;
	private Font titleFont;
	
	private Font font;
	
	public MenuState(GameStateManager gsm){
		this.gsm = gsm;
		
		try{
			bg = new Background("/Levels/test.PNG");
			
			titleColor = new Color(128,0,0);
			titleFont = new Font("Century Gothic",
					Font.PLAIN,28);
			
			font = new Font("Arial", Font.PLAIN,12);
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void draw(Graphics2D g) {
		bg.draw(g);
		// TODO Auto-generated method stub
		
	}

}
