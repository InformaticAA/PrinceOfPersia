package framework;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Scanner;
import javax.imageio.ImageIO;
import data.FrameList;
import data.FrameLists;
import data.Level;
import data.Room;
import data.Square;
import entities.*;

public class Loader {

	private long frameTime;
	private FrameLists totalAnimations;
	
	public Loader(long frameTime) {
		this.frameTime = frameTime;
		this.totalAnimations = new FrameLists();
	}
	
	/**
	 * Loads every sprite needed in the game
	 */
	public void loadAllSprites() {
//		loadAnimations("resources/Cutscenes/");
		loadAnimations("resources/Sprites_400/Dungeon/");
		loadAnimations("resources/Sprites_400/Objects/");
//		loadAnimations("resources/Characters/");
	}
	
	/**
	 * 
	 * Loads every animation of each entity in the folder path
	 */
	public void loadAnimations(String path) {
		Hashtable<String, FrameList> entityAnimations = null;
		
		/* Loads the animations in each folder */
		File dir = new File(path);
		if (dir.isDirectory()) {
			File[] files = dir.listFiles();
			
			if (files != null) {
				for(File f : files) {
					
					entityAnimations = null;
					
					if (f.isDirectory()) {
						
						/* Folder f contains the animations of entity f */
						entityAnimations = loadEntityAnimations(path + f.getName());
						if (entityAnimations != null) {
							totalAnimations.addEntityFrameLists(f.getName(), entityAnimations);
							System.out.println("ENTITY: " + f.getName());
//							for (String key : entityAnimations.keySet()) {
//								System.out.println(" -Key: " + key + " - Animations: " + entityAnimations.get(key).getId());
//							}
						}
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
	public Hashtable<String, FrameList> loadEntityAnimations(String entityPath) {
		Hashtable<String, FrameList> animations = new Hashtable<String, FrameList>();
		
		/* Searches for .png files for each folder of characterPath */
		File dir = new File(entityPath);
		if (dir.isDirectory()) {
			File[] files = dir.listFiles();
			
			if (files != null) {
				for(File f : files) {
					if (f.isDirectory()) {
						
						/* folder f contains .png files */
						FrameList anim = loadFrameList(f,false);
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
	public FrameList loadFrameList(File f, boolean infinite) {
		FrameList animation = new FrameList(f.getName());
		
		File[] images = f.listFiles();
		for(File image : images) {
			
			/* Loads one image as a frame of the animation */
			String name = image.getName();
			if (name.substring(name.length() - 4, name.length()).equals(".png")) {
				
				Frame frame = loadFrame(image);
				animation.addFrame(frame);
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
		Level level = null;
		String levelPath = "resources/Levels/level" + numLevel + ".txt";
		String roomContent = "";
		int row = 0;
		int col = 0;
		
		/* Reads the files that describes the level */
		File levelFile = new File(levelPath);
		Scanner readLevel;
		
		try {
			
			readLevel = new Scanner(levelFile);
			int rows = readLevel.nextInt();
			int cols = readLevel.nextInt();
			level = new Level(numLevel, rows, cols);
			
			while (readLevel.hasNextLine()) {
				
				String line = readLevel.nextLine();
				
				if (line.contains("room")) {
					
					System.out.println("Leemos");
					
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
					
					Room newRoom = loadRoom(row - 1, col - 1, roomContent);
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
		
		while (readContent.hasNextLine() && x < 4) {
		
			y = 0;
			
			/* Loads one floor's entities in a room */
			String floor = readContent.nextLine();
			Scanner readFloor = new Scanner(floor);
			readFloor.useDelimiter(";");
			
			while (readFloor.hasNext() && y < 10) {
				/* Loads each square's entities in a floor */
				String squareContent = readFloor.next();
				Square square = loadEntities(x, y, squareContent);
				room.setSquare(x, y, square);
				
				y++;
			}
			
			readFloor.close();
			x++;
		}
		
		readContent.close();
		
		return room;
	}
	
	/**
	 * TODO generate more types of entities
	 * @param x
	 * @param y
	 * @param squareContent
	 * @return a new square with its content loaded
	 */
	public Square loadEntities(int x, int y, String squareContent) {
		ArrayList<Entity> background = new ArrayList<Entity>();
		ArrayList<Entity> entities = new ArrayList<Entity>();
		ArrayList<Entity> foreground = new ArrayList<Entity>();
		Square square = new Square();
		Scanner readSquare = new Scanner(squareContent);
		
		int px = 64 + y * 64;
		int py = (int)(4 + x * 125.3);
		
		while (readSquare.hasNext()) {
			
			/* Loads each entity described in squareContent */
			String entity = readSquare.next();
			Entity newEntity = null;
			if (entity.equals("ls")) {
				newEntity = new Wall(px,py,this,"left_stack_main");
				foreground.add(newEntity);
			} else if(entity.equals("cs")){
				newEntity = new Wall(px,py,this,"centre_stack_main");
				background.add(newEntity);
			} else if(entity.equals("rs")){
				newEntity = new Wall(px,py,this,"right_stack_main");
				background.add(newEntity);
			} else if(entity.equals("fs")){
				newEntity = new Wall(px,py,this,"face_stack_main");
				background.add(newEntity);
			}
			
			
			
			else if (entity.equals("t")) {
				newEntity = new Torch(px, py, this);
				background.add(newEntity);
			} else if(entity.equals("lsb")) {
				newEntity = new Base(px,py,this,"left_stack_base");
				background.add(newEntity);
			} else if(entity.equals("b")){
				newEntity = new Base(px,py,this,"normal_base");
				background.add(newEntity);
			} else if(entity.equals("csb")){
				newEntity = new Base(px,py,this,"centre_stack_base");
				background.add(newEntity);
			} else if(entity.equals("rsb")){
				newEntity = new Base(px,py,this,"right_stack_base");
				background.add(newEntity);
			}
//			else if (entity.equals("loose_floor")) {
//				entityAnimations = animations.getAnimations("loose_floor");
//				newEntity = new LooseFloor(x, y, true, entityAnimations);
//			}
//			else if (entity.equals("loose_floor")) {
//				entityAnimations = animations.getAnimations("loose_floor");
//				newEntity = new LooseFloor(x, y, true, entityAnimations);
//			}
			
		}
		square.setBackground(background);
		square.setEntities(entities);
		square.setForeground(foreground);
		
		readSquare.close();
		
		return square;
	}
	
	public Hashtable<String, Animation> getAnimations(String entity){
		Hashtable<String, Animation> animations = new Hashtable<String, Animation>();
		Hashtable<String, FrameList> entityFrameLists = totalAnimations.getFrameLists(entity);
		for(String id : entityFrameLists.keySet()){
			animations.put(id, new Animation(id,entityFrameLists.get(id).getFrames(),true));
		}
		return animations;
	}
}
