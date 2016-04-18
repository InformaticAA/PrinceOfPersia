package map;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import game.Game;

public class Background {
	
	protected BufferedImage image;
	protected int x,y;
	protected int dx,dy;
	
	public Background(String s){
		
		try{
			image = ImageIO.read(new File(s));
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public Background (String s, int x, int y){
		try{
			image = ImageIO.read(new File(s));
			this.x = x;
			this.y = y;
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void setPosition(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	public void setVel(int dx, int dy){
		this.dx = dx;
		this.dy = dy;
	}
	
	public void update(long elapsedTime){
		this.x = (x + dx)%640;
		this.y = y + dy;
	}
	
	public void draw(Graphics2D g){
		g.drawImage(image, x, y, null);
		if(x < 0){
			g.drawImage(image, x + Game.WIDTH, y, null);
		}
		if(x > 0){
			g.drawImage(image, x - Game.WIDTH, y, null);
		}
	}

}
