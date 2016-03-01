package framework;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Scanner;

import javax.imageio.ImageIO;

import data.Animations;
import data.Level;
import data.Room;
import data.Square;
import entities.Entity;
import entities.Fire;
import entities.LooseFloor;

public class Loader {

	private long frameTime;
	private Animations animations;
	
	public Loader(long frameTime) {
		this.frameTime = frameTime;
	}
	
	/**
	 * 
	 * Loads every animation of each entity in the resources folder
	 */
	public void loadAllAnimations(String path) {
		Hashtable<String, Animation> entityAnimations = null;
		
		/* Initialize animations estructure */
		animations = new Animations();
		
		/* Loads the animations in each folder */
		File dir = new File("resources/" + path);
		if (dir.isDirectory()) {
			File[] files = dir.listFiles();
			
			if (files != null) {
				for(File f : files) {
					
					entityAnimations = null;
					
					if (f.isDirectory()) {
						
						/* Folder f contains the animations of entity f */
						entityAnimations = loadEntityAnimations("resources/" + f.getName());
						animations.addEntityAnimations(f.getName(), entityAnimations);
					}
				}
			}
		}
		
	}
	
	/**
	 * 
	 * @param entityPath
	 * @return a list with every animation of an entity
	 */
	public Hashtable<String, Animation> loadEntityAnimations(String entityPath) {
		Hashtable<String, Animation> animations = new Hashtable<String, Animation>();
		
		/* Searches for .png files for each folder of characterPath */
		File dir = new File(entityPath);
		if (dir.isDirectory()) {
			File[] files = dir.listFiles();
			
			if (files != null) {
				for(File f : files) {
					if (f.isDirectory()) {
						
						/* folder f contains .png files */
						Animation anim = loadAnimation(f,false);
						animations.put(anim.getId(), anim);
					}
				}
			}
			
		}
		
		return animations;
	}
	
	/**
	 * 
	 * @param f directory containing all frames of
	 * one animation
	 * @return new animation loaded
	 */
	public Animation loadAnimation(File f, boolean infinite) {
		Animation animation = new Animation(f.getName(), infinite);
		
		File[] images = f.listFiles();
		for(File image : images) {
			
			/* Loads one image as a frame of the animation */
			String name = image.getName();
			if (name.substring(name.length() - 4, name.length()).equals(".png")) {
				
				Frame frame = loadFrame(image);
				animation.addFrame(frame, frameTime);
			}
		}
		
		return animation;
	}
	
	/**
	 * 
	 * @param image
	 * @return a new frame loaded from file image
	 */
	public Frame loadFrame(File image) {
		Frame frame = null;
		BufferedImage img = null;
		
		try{
			img = ImageIO.read(image);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		frame = new Frame(img, frameTime);
		return frame;
	}
	
	/**
	 * 
	 * @return a new level with its content loaded
	 */
	public Level loadLevel(int numLevel) {
		Level level = new Level(numLevel);
		String levelPath = "resources/Levels/level" + numLevel + ".txt";
		String roomContent = "";
		int row = 1;
		int col = 1;
		
		/* Reads the files that describes the level */
		File levelFile = new File(levelPath);
		Scanner readLevel;
		
		try {
			
			readLevel = new Scanner(levelFile);
			
			while (readLevel.hasNextLine()) {
				
				String line = readLevel.nextLine();
				
				if (line.contains("room")) {
					
					/* Reads all room content */
					Scanner readLine = new Scanner(line);
					readLine.next();
					row = readLine.nextInt();
					col = readLine.nextInt();
					readLine.close();
					
					roomContent += readLevel.nextLine() + "\n";
					roomContent += readLevel.nextLine() + "\n";
					roomContent += readLevel.nextLine() + "\n";
					roomContent += readLevel.nextLine();
					
					Room newRoom = loadRoom(row, col, roomContent);
					level.addRoom(newRoom);
					
					roomContent = "";
				}
			}
			
			readLevel.close();
		
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		return level;
	}
	
	/**
	 * 
	 * @return a new room with all the entities contained and
	 * background loaded
	 */
	public Room loadRoom(int row, int col,
			String roomContent) {
		Room room = new Room(row, col);
		int x = 0;
		int y = 0;
		Scanner readContent = new Scanner(roomContent);
		
		while (readContent.hasNextLine()) {
			
			/* Loads one floor's entities in a room */
			String floor = readContent.nextLine();
			Scanner readFloor = new Scanner(floor);
			readFloor.useDelimiter(";");
			
			while (readFloor.hasNext()) {
				
				/* Loads each square's entities in a floor */
				String squareContent = readFloor.next();
				Square square = new Square();
				square.setEntities(loadEntities(x, y, squareContent));
				room.setSquare(x, y, square);
				
				x++;
			}
			
			readFloor.close();
			y++;
		}
		
		readContent.close();
		
		return room;
	}
	
	/**
	 * TODO generate more types of entities
	 * @param x
	 * @param y
	 * @param squareContent
	 * @return a list of the entities defined by squareContent
	 */
	public ArrayList<Entity> loadEntities(int x, int y, String squareContent) {
		ArrayList<Entity> entities = new ArrayList<Entity>();
		Scanner readSquare = new Scanner(squareContent);
		boolean back = false;
		
		while (readSquare.hasNext()) {
			
			/* Loads each entity described in squareContent */
			String entity = readSquare.next();
			Entity newEntity = null;
			Hashtable<String, Animation> entityAnimations = null;
			
			if (entity.equals("torch")) {
				entityAnimations = animations.getAnimations("fire");
				newEntity = new Fire(x, y, true, entityAnimations);
			}
			else if (entity.equals("loose_floor")) {
				entityAnimations = animations.getAnimations("loose_floor");
				newEntity = new LooseFloor(x, y, true, entityAnimations);
			}
			
			entities.add(newEntity);
		}
		
		readSquare.close();
		
		return entities;
	}
	
}
