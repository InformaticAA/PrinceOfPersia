package entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import framework.Loader;

public class Corner extends Entity{

	public Corner(int x, int y, int x_offset, int y_offset, Loader loader, String corner_type) {
		super("Corner_" + corner_type, x + x_offset, y + y_offset, loader);
		animations = loader.getAnimations("floor_panels");
		currentAnimation = animations.get(corner_type);
		
		boundingBoxColor = Color.GREEN;
		
		/* Sets the bounding box */
		if(corner_type.equals("normal_left")){
			enableBoundingBox(this.x + 25, this.y + currentAnimation.getImage().getHeight()/2 - 1,
						currentAnimation.getImage().getWidth() - 25,
						currentAnimation.getImage().getHeight()/2);
		} else if(corner_type.equals("normal_right")){
			enableBoundingBox(this.x, this.y + currentAnimation.getImage().getHeight()/2 - 3,
					currentAnimation.getImage().getWidth() - 27,
					currentAnimation.getImage().getHeight()/2);
		}
	}

	public Corner() {
		super();
	}
	
	@Override
	public Entity copy() {
		return null;
	}

@Override	
public void drawSelf(Graphics g) {
		
		/* Draws the entity */
		BufferedImage img = currentAnimation.getImage();
		g.drawImage(img, x - currentAnimation.getImage().getWidth(),
				y - currentAnimation.getImage().getHeight(), null);
		
		/* Draws the entity's bounding box */
		if (boundingBox != null) {
			g.setColor(boundingBoxColor);
			g.drawRect((int) boundingBox.getX(),
					(int) boundingBox.getY(),
					(int) boundingBox.getWidth(),
					(int) boundingBox.getHeight());
			g.setColor(Color.BLACK);
		}
		
	}
}
