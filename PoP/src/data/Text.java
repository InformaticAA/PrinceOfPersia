package data;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import framework.Writter;

public class Text {
	
	private BufferedImage[] texto;
	private int width;
	private int height;
	private int x;
	private int y;
	
	public Text(BufferedImage[] texto, int x, int y){
		this.texto = texto;
		this.width = Writter.WIDTH * texto.length;
		this.height = Writter.HEIGTH;
		this.x = x;
		this.y = y;
	}
	
	public void drawSelf(Graphics2D g){
		for(int i = 0; i < texto.length; i++){
			g.drawImage(texto[i], x + i*Writter.WIDTH, y, Writter.WIDTH, Writter.HEIGTH, null);
		}
	}
}
