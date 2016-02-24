package tests;

import java.awt.Graphics2D;
import java.io.File;
import java.util.Hashtable;
import java.util.concurrent.ConcurrentLinkedQueue;

import entities.Character;
import framework.Animation;
import framework.Loader;
import states.GameStateManager;
import states.State;
import types.Key;

public class Test extends State {

	private final long FRAME_TIME = 1000/14;
	private final String testPrincessPath = "resources/Sprites_400/Cutscenes/Princess/winning/";
	private final String testDastanPath = "resources/Sprites_400/Dastan/";
	
	private Hashtable<String, Animation> animations;
	private Animation animation;
	private Animation princessAnimation;
	private Character princess;
	private Character dastan;
	
	private boolean reverse = false;
	
	public Test(GameStateManager gsm, ConcurrentLinkedQueue<Key> keys) {
		super(gsm,keys);
	}
	
	@Override
	public void init() {
	
		Loader loader = new Loader(FRAME_TIME);
		
		/* Characters in scene */
		princess = new Character(50,50);
		dastan = new Character(200,400);
		
		/* Animations in scene */
		princessAnimation = loader.loadAnimation(new File(testPrincessPath), false);

		animations = loader.loadCharacterAnimations(testDastanPath);
		dastan.setAnimations(animations);
		dastan.setCurrentAnimation("clipping");
		animation = dastan.getCurrentAnimation();
	}

	@Override
	public void update(long elapsedTime) {

		dastan.getCurrentAnimation().update(elapsedTime, reverse);
		
		if (dastan.getCurrentAnimation().isOver(reverse)) {
			reverse = !reverse;
		}
		
		/* Updates dastan's animation */
//		if (animation.isOver()) {
//			
//		}
//		else {
//			animation.update(elapsedTime);
//		}
		
		/* Updates princess winning animation */
//		if (!princessAnimation.isOver()) {
//			princessAnimation.update(elapsedTime);
//		}
//		
//		if (princessAnimation.getCurrentFrame() < 12) {
//			princess.setX(princess.getX() + 8);
//		}
	}

	@Override
	public void draw(Graphics2D g) {
		
//		BufferedImage princessImg = princessAnimation.getImage();
//		BufferedImage img = animation.getImage();
//		
//		g.drawImage(princessImg, princess.getX(), princess.getY(), null);
//		g.drawImage(img, dastan.getX(), dastan.getY(), null);
		
		dastan.drawSelf(g);
		
	}

	@Override
	public void manageKeys() {
		// TODO Auto-generated method stub
		
	}
	
}