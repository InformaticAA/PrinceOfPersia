package tests;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;

import framework.Animation;
import framework.Loader;
import states.GameStateManager;
import states.State;

public class Test extends State {

	private final long FRAME_TIME = 1000/30;
	private final String testPath = "resources/Sprites_400/Objects/fire/";
	private Animation animation;
	private GameStateManager gsm;
	
	public Test(GameStateManager gsm) {
		this.gsm = gsm;
	}
	
	@Override
	public void init() {
	
		Loader loader = new Loader(FRAME_TIME);
		animation = loader.loadAnimation(new File(testPath));

	}

	@Override
	public void update(long elapsedTime) {
		animation.update(elapsedTime);
	}

	@Override
	public void draw(Graphics2D g) {
		BufferedImage img = animation.getImage();
		g.clearRect(img.getMinX(), 0, img.getWidth(), img.getHeight());
		g.drawImage(img, 200, 200, null);
	}
}
