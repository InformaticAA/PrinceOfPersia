package entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import framework.Loader;

public class Pillar extends Entity{

	public Pillar(int x, int y, int x_offset, int y_offset, Loader loader, String type) {
		super("Pillar_" + type, x + x_offset, y + y_offset, loader);
		animations = loader.getAnimations("pillar");
		currentAnimation = animations.get(type);
		
		/* Sets the bounding box */
		if(type.equals("pillar_left")){
			boundingBoxColor = Color.YELLOW;
			enableBoundingBox(this.x + 25, this.y + currentAnimation.getImage().getHeight() - 14,
						currentAnimation.getImage().getWidth() - 25,
						13);
		} else if(type.equals("pillar_right_main")){
			boundingBoxColor = Color.WHITE;
			enableBoundingBox(this.x, this.y + currentAnimation.getImage().getHeight() - 18,
					currentAnimation.getImage().getWidth() -27,
					17);
		}
	}

	/**
	 * 
	 * @return the center coords of the entity
	 */
	@Override
	public int[] getCenter() {
		int width2 = currentAnimation.getImage().getWidth()/2;
		int height2 = (int) (this.getBoundingBox().getMaxY() - this.getBoundingBox().getMinY()) / 2;
		int xx = x - width2;
		int yy = y - height2;
		
		return new int[]{xx,yy};
	}
	
	@Override
	public Entity copy() {
		return null;
	}
	
//	@Override	
//	public void drawSelf(Graphics g) {
//			
//		/* Draws the entity */
//		BufferedImage img = currentAnimation.getImage();
//		g.drawImage(img, x - currentAnimation.getImage().getWidth(),
//				y - currentAnimation.getImage().getHeight(), null);
//		
//		/* Draws the entity's bounding box */
//		if (boundingBox != null) {
//			g.setColor(boundingBoxColor);
//			g.drawRect((int) boundingBox.getX(),
//					(int) boundingBox.getY(),
//					(int) boundingBox.getWidth(),
//					(int) boundingBox.getHeight());
//			g.setColor(Color.BLACK);
//		}
//	}
}
