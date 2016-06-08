package framework;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import data.Text;

public class Writter {
	
	private static BufferedImage font;
	public final static int WIDTH = 16;
	public final static int HEIGTH = 16;
	private static ArrayList<String> equivalences;
	
	public Writter(){
		equivalences = new ArrayList<String>();
		
		for(int i = 65; i < 91; i++){
			equivalences.add((char)i + "");
		}
		
		for(int i = 0; i < 10; i++){
			equivalences.add(i + "");
		}
		equivalences.add(" ");
		
		try {
			font = ImageIO.read(new File("resources/font.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static Text createText(String text, int x, int y){
		text =  text.toUpperCase();
		BufferedImage[] texto = new BufferedImage[text.length()];
		for(int i = 0; i < text.length(); i++){
			int numChar = (equivalences.indexOf(text.charAt(i) + "")) * Writter.HEIGTH;
			texto[i] = font.getSubimage((numChar)%256, ((int)(numChar)/256) * Writter.HEIGTH, Writter.WIDTH,Writter.HEIGTH);
		}
		Text image = new Text(texto, x, y);
		return image;
	}

}
