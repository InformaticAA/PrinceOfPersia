package tests;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Iterator;
import java.util.Set;

import entities.Entity;
import framework.Animation;
import framework.Loader;
import states.GameStateManager;
import states.State;

public class Test extends State {

	private final long FRAME_TIME = 1000/14;
	private final String testPath = "resources/Sprites_400/Objects/fire/";
	private final String testPrincessPath = "resources/Sprites_400/Cutscenes/Princess/winning/";
	private final String testDastanPath = "resources/Sprites_400/Dastan/";
	
	private Iterator<Animation> iter;
	private Set<Animation> animations;
	private Animation animation;
	private Animation princessAnimation;
	private Entity princess;
	private Entity dastan;
	private GameStateManager gsm;
	
	public Test(GameStateManager gsm) {
		this.gsm = gsm;
	}
	
	@Override
	public void init() {
	
		Loader loader = new Loader(FRAME_TIME);
		
		/* Characters in scene */
		princess = new Entity(50,50);
		dastan = new Entity(500,50);
		
		/* Animations in scene */
		princessAnimation = loader.loadAnimation(new File(testPrincessPath), false);
		
		animations = loader.loadCharacterAnimations(testDastanPath);
		dastan.setAnimations(animations);
		iter = animations.iterator();
		animation = iter.next();
		
	}

	@Override
	public void update(long elapsedTime) {

		/* Updates dastan animation */
		if (animation.isOver()) {
			if (iter.hasNext()) {
				animation = iter.next();
			}
		}
		else {
			animation.update(elapsedTime);
		}
		
		/* Updates princess winning animation */
		if (!princessAnimation.isOver()) {
			princessAnimation.update(elapsedTime);
		}
		
		if (princessAnimation.getCurrentFrame() < 12) {
			princess.setX(princess.getX() + 8);
		}
	}

	@Override
	public void draw(Graphics2D g) {
		
		BufferedImage princessImg = princessAnimation.getImage();
		BufferedImage img = animation.getImage();
		
		g.drawImage(princessImg, princess.getX(), princess.getY(), null);
		g.drawImage(img, dastan.getX(), dastan.getY(), null);
	}

	@Override
	public void manageKeys() {
		// TODO Auto-generated method stub
		
	}
	
}
