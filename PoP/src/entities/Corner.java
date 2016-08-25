package entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import framework.Loader;

public class Corner extends Entity{
	
	private int[] cornerOriginalCenter;

	public Corner(int x, int y, int x_offset, int y_offset, Loader loader, String corner_type) {
		super("Corner_" + corner_type, x + x_offset, y + y_offset, loader);
		
		animations = loader.getAnimations("floor_panels");
		
		if (corner_type.contains("left")) {
			currentAnimation = animations.get("normal_left");
		}
		else if (corner_type.contains("right")) {
			currentAnimation = animations.get("normal_right");
		}

		// saves floor panel corner center as corner center
		cornerOriginalCenter = this.getInitialCenter();
		
		boundingBoxColor = Color.GREEN;
		
		/* Sets the bounding box */
		if(corner_type.contains("left")){
			enableBoundingBox(this.x + 25, this.y + currentAnimation.getImage().getHeight()/2 - 1,
						currentAnimation.getImage().getWidth() - 25,
						currentAnimation.getImage().getHeight()/2);
		} else if(corner_type.contains("right")){
			enableBoundingBox(this.x, this.y + currentAnimation.getImage().getHeight()/2 - 3,
					currentAnimation.getImage().getWidth() - 27,
					currentAnimation.getImage().getHeight()/2);
		}
		
		// changes the appearance if necessary after setting the bounding box
		if (corner_type.contains("opener")) {
			animations = loader.getAnimations("opener");
			currentAnimation = animations.get("unpressed_" + corner_type);
		}
		else if (corner_type.contains("closer")) {
			animations = loader.getAnimations("closer");
			currentAnimation = animations.get("unpressed");
		}
		else if (corner_type.contains("pillar")) {
			animations = loader.getAnimations("pillar");
			
			if (corner_type.contains("left")) {
				currentAnimation = animations.get("pillar_left");
			}
			else if (corner_type.contains("right")) {
				currentAnimation = animations.get("pillar_right_main");
			}
		}
	}

	public Corner() {
		super();
	}
	
	@Override
	public int[] getCenter() {
		return cornerOriginalCenter;
	}
	
	/**
	 * 
	 * @return the center coords of the entity
	 */
	public int[] getInitialCenter() {
		int width2 = currentAnimation.getImage().getWidth()/2;
		int height2 = currentAnimation.getImage().getHeight()/2;
		int xx = x - width2;
		int yy = y - height2;
		
		return new int[]{xx,yy};
	}
	
	@Override
	public Entity copy() {
		return null;
	}

	@Override	
	public void drawSelf(Graphics g) {
		
		/* Draws the entity */
//		BufferedImage img = currentAnimation.getImage();
//		g.drawImage(img, x - currentAnimation.getImage().getWidth(),
//				y - currentAnimation.getImage().getHeight(), null);
		
//		/* Draws the entity's bounding box */
//		if (boundingBox != null) {
//			g.setColor(boundingBoxColor);
//			g.drawRect((int) boundingBox.getX(),
//					(int) boundingBox.getY(),
//					(int) boundingBox.getWidth(),
//					(int) boundingBox.getHeight());
//			g.setColor(Color.BLACK);
//		}
		
	}
}
