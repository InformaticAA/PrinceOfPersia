package states;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import game.Game;
import map.Background;

public class MenuState extends State{
	
	private Background bg;
	private BufferedImage title;
	private BufferedImage sword;
	
	private BufferedImage[] options;
	
	private int currentChoice = 0;
	
	public MenuState(GameStateManager gsm){
		this.gsm = gsm;
		
		try{
			bg = new Background("/Sprites_400/Menu/room_won.png");
			title = ImageIO.read(new File("resources/Sprites_400/Title/main titles/game name.png"));
			options = new BufferedImage[4];
			options[0] = ImageIO.read(new File("resources/Sprites_400/Menu/campaign.png"));
			options[1] = ImageIO.read(new File("resources/Sprites_400/Menu/versus.png"));
			options[2] = ImageIO.read(new File("resources/Sprites_400/Menu/settings.png"));
			options[3] = ImageIO.read(new File("resources/Sprites_400/Menu/exit.png"));
			sword = ImageIO.read(new File("resources/Sprites_400/Menu/sword.png"));
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void init() {
		
	}

	@Override
	public void update(long elapsedTime) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void draw(Graphics2D g) {
		bg.draw(g);
		g.drawImage(title, Game.WIDTH/2 - title.getWidth()/2,
				Game.HEIGHT/4 - title.getHeight()/2, null);
		
		for (int i = 0; i < options.length; i++) {
			g.drawImage(options[i], Game.WIDTH/2 - options[i].getWidth()/2,
					Game.HEIGHT/2 - 55*Game.SCALE + i*20*Game.SCALE + options[0].getHeight(),
					null);
		}
		
		g.drawImage(sword, 
				Game.WIDTH/2 - options[currentChoice].getWidth()/2 - sword.getWidth() - 10*Game.SCALE,
				Game.HEIGHT/2 - 47*Game.SCALE + currentChoice*20*Game.SCALE,null);
		
//		g.drawImage(campaign, Game.WIDTH/2 - campaign.getWidth()/2, Game.HEIGHT/2,null);
//		g.drawImage(campaign, Game.WIDTH/2 - campaign.getWidth()/2, 
//				Game.HEIGHT/2 + campaign.getHeight() + 10*Game.SCALE, null);
//		g.drawImage(sword, Game.WIDTH/2 - sword.getWidth() - 10*Game.SCALE, 
//				Game.HEIGHT/2 - sword.getHeight()/2,null);
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
