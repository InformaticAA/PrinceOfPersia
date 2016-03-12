package map;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class Background {
	
	private BufferedImage image;
	private int x,y;
	
	public Background(String s){
		
		try{
			image = ImageIO.read(new File("resources/Sprites_400/Menu/room_won.png"));
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void setPosition(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	public void draw(Graphics2D g){
		g.drawImage(image, x, y, null);
	}

}
