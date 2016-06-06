package framework;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import data.Text;

public class Writter {
	
	private BufferedImage font;
	public final int WIDTH = 15;
	public final int HEIGTH = 15;
	
	public Writter(){
		try {
			font = ImageIO.read(new File("resources/good_font.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Text createText(String text){
		BufferedImage[] texto = new BufferedImage[text.length()];
		for(int i = 0; i < text.length(); i++){
			int numChar = (((int)text.charAt(i)) - 48)*15;
			font.getSubimage((i*numChar)%254, (i*numChar)/254, this.WIDTH,this.HEIGTH);
		}
		
	}

}
