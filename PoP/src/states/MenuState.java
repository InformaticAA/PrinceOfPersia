package states;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import game.Game;
import map.Background;

public class MenuState extends State{
	
	private Background bg;
	private BufferedImage title;
	private BufferedImage princess;
	private BufferedImage selected;
	private BufferedImage noSelected;
	
	private int currentChoice = 0;
	private String[] options = {"Campaign", "Multiplayer", 
			"Settings", "Exit Game"};
	private Color titleColor;
	private Font titleFont;
	
	private Font font;
	
	public MenuState(GameStateManager gsm){
		this.gsm = gsm;
		
		try{
			bg = new Background("/Sprites_400/Menu/room_won.png");
			
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
		try{
			title = ImageIO.read(new File("resources/Sprites_400/Title/main titles/game name.png"));
			princess = ImageIO.read(new File("resources/Sprites_400/Cutscenes/princess/in story/frame17.png"));
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void draw(Graphics2D g) {
		bg.draw(g);
		g.drawImage(title, Game.WIDTH/2 - title.getWidth()/2,
				Game.HEIGHT/4 - title.getHeight()/2, null);
		
		
		g.setColor(titleColor);
		g.setFont(font);
		
		for (int i = 0; i < options.length; i++) {
			if ( i == currentChoice){
				g.setColor(Color.BLACK);
			}
			else{
				g.setColor(Color.RED);
			}
			g.drawString(options[i], 145, 140+i*15);
		}
	}
	
	public void select(){
		if(currentChoice == 0){
			//campaign
		}
		else if(currentChoice == 1){
			//multiplayer
		}
		else if(currentChoice == 2){
			//start settings
		}
		else{
			System.exit(0);
		}
	}

}
