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
import entities.Base;
import entities.Door;
import entities.DoorFrame;
import entities.Enemy;
import entities.Entity;
import entities.FloorPanel;
import entities.LooseFloor;
import entities.Pillar;
import entities.Torch;
import entities.Wall;
import entities.Character;
import kuusisto.tinysound.Sound;
import kuusisto.tinysound.TinySound;

public class Loader {

	private long frameTime;
	private int fps;
	private FrameLists totalAnimations;
	private Hashtable<String,Sound> totalSounds;
	
	public Loader(int fps) {
		this.fps = fps;
		this.frameTime = 1000/fps;
		this.totalAnimations = new FrameLists();
		this.totalSounds = new Hashtable<String,Sound>();
	}
	
	public int getFPS(){
		return this.fps;
	}
	
	/**
	 * Loads every sprite needed in the game
	 */
	public void loadAllSprites() {
		loadAnimations("resources/Sprites_400/Dungeon/");
		loadAnimations("resources/Sprites_400/Objects/");
		loadAnimations("resources/Sprites_400/Characters/");
		loadSounds("resources/Sounds/");
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
//							System.out.println("ENTITY: " + f.getName());
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
	 * Loads every sound in the folder path
	 */
	public void loadSounds(String path){
		TinySound.init();
		/* Loads the animations in each folder */
		File dir = new File(path);
		if (dir.isDirectory()) {
			File[] files = dir.listFiles();
			
			if (files != null) {
				for(File f : files) {
					if (f.isFile()) {
						totalSounds.put(getFileName(f), TinySound.loadSound(f));
					}
				}
			}
		}
	}
	
	public Sound getSound(String name){
		return totalSounds.get(name);
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
				room.addBackground(square.getBackground());
				room.addForeground(square.getForeground());
				room.addCharacters(square.getCharacters());
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
		ArrayList<Character> characters = new ArrayList<Character>();
		ArrayList<Entity> foreground = new ArrayList<Entity>();
		Square square = new Square();
		Scanner readSquare = new Scanner(squareContent);
		
		int px = 64 + y * 64;
		int py = (int)(6 + x * 126);
		
		while (readSquare.hasNext()) {
			
			/* Loads each entity described in squareContent */
			String entity = readSquare.next();
			Entity newEntity = null;
			
			/* Loads background elements */
			if(entity.startsWith("cs") && !entity.startsWith("csb")){
				newEntity = new Wall(px,py,0,-6,this,"centre_stack_main");
				background.add(newEntity);
				
				/* Chooses mark if necessary */
				if(entity.contains("mark")) {
					int numMark = Integer.parseInt(entity.substring(8,9));
					int voff = -104;
					int hoff = -2;
					if (numMark == 1) {
						// top left mark
						voff -= 12;
						hoff -= 34;
					} else if (numMark == 2) {
						// bottom left mark
						voff += 22;
						hoff -= 34;
					} else if (numMark == 3) {
						// top right mark
						voff -= 0;
						hoff -= 0;
					} else if (numMark == 4) {
						// bottom right mark
						voff += 20;
						hoff -= 0;
					}
					newEntity = new Wall(px,py,hoff,voff,this,"mark" + numMark);
					background.add(newEntity);
				}
				
			} else if(entity.startsWith("rs") && !entity.startsWith("rsb")){
				newEntity = new Wall(px,py,0,-6,this,"right_stack_main");
				background.add(newEntity);
				
				/* Chooses mark if necessary */
				if(entity.contains("mark")) {
					int numMark = Integer.parseInt(entity.substring(8,9));
					int voff = -104;
					int hoff = -2;
					if (numMark == 1) {
						// top left mark
						voff -= 12;
						hoff -= 34;
					} else if (numMark == 2) {
						// bottom left mark
						voff += 22;
						hoff -= 34;
					} else if (numMark == 3) {
						// top right mark
						voff -= 0;
						hoff -= 0;
					} else if (numMark == 4) {
						// bottom right mark
						voff += 20;
						hoff -= 0;
					}
					newEntity = new Wall(px,py,hoff,voff,this,"mark" + numMark);
					background.add(newEntity);
				}
				
			} else if(entity.equals("fs")){
				newEntity = new Wall(px,py,-14,-2,this,"face_stack_main");
				background.add(newEntity);
			} else if(entity.equals("random")){
				newEntity = new Wall(px,py,0,-84,this,"random_block");
				background.add(newEntity);
			} else if(entity.equals("fst")){
				newEntity = new Wall(px,py,-14,0,this,"face_stack_top");
				background.add(newEntity);
			} else if(entity.contains("bricks")){
				String numBrick = entity.substring(7,8);
				newEntity = new Wall(px,py,-8,-46,this,"brick" + numBrick);
				background.add(newEntity);
			} else if(entity.equals("prm")){
				newEntity = new Pillar(px,py,-12,-2,this,"pillar_right_main");
				background.add(newEntity);
			} else if (entity.equals("t")) {
				newEntity = new Torch(px, py, this,false);
				background.add(newEntity);
			} else if(entity.equals("lsb")) {
				newEntity = new Base(px,py,this,"left_stack_base");
				background.add(newEntity);
			} else if(entity.equals("csb")){
				newEntity = new Base(px,py,this,"centre_stack_base");
				background.add(newEntity);
			} else if(entity.equals("rsb")){
				newEntity = new Base(px,py,this,"right_stack_base");
				background.add(newEntity);
			} else if(entity.equals("lf")){
				newEntity = new FloorPanel(px,py,0,-6,this,"normal_left");
				background.add(newEntity);
			} else if(entity.equals("rf")){
				newEntity = new FloorPanel(px,py,-12,-2,this,"normal_right");
				background.add(newEntity);
			} else if(entity.equals("bl")){
				newEntity = new FloorPanel(px,py,0,-6,this,"broken_left");
				background.add(newEntity);
			} else if(entity.equals("br")){
				newEntity = new FloorPanel(px,py,-12,-2,this,"broken_right");
				background.add(newEntity);
			} else if(entity.equals("loose")){
				newEntity = new LooseFloor(px,py,-12,0,this,"idle");
				background.add(newEntity);
			} else if(entity.equals("doorfr")){
				newEntity = new DoorFrame(px,py,-12,-2,this,"door_frame_right");
				background.add(newEntity);
			} else if(entity.startsWith("door")){
				
				Scanner doortype = new Scanner(entity.substring(5,entity.length()-1));
				doortype.useDelimiter(",");
				int id = doortype.nextInt();
				String animation = doortype.next();
				int frame = doortype.nextInt();
				doortype.close();
				newEntity = new Door(px,py,-24,-6,this,animation, id, frame);
				
				/* Si principe a la izquierda -> foreground, si principe a la derecha -> background */
				background.add(newEntity);
			}
			
			/* Loads foreground elements */
			else if (entity.startsWith("ls")) {
				newEntity = new Wall(px,py,0,-6,this,"left_stack_main");
				foreground.add(newEntity);
				
				/* Chooses mark if necessary */
				if(entity.contains("mark")) {
					int numMark = Integer.parseInt(entity.substring(8,9));
					int voff = -104;
					int hoff = -2;
					if (numMark == 1) {
						// top left mark
						voff -= 12;
						hoff -= 34;
					} else if (numMark == 2) {
						// bottom left mark
						voff += 22;
						hoff -= 34;
					} else if (numMark == 3) {
						// top right mark
						voff -= 0;
						hoff -= 0;
					} else if (numMark == 4) {
						// bottom right mark
						voff += 20;
						hoff -= 0;
					}
					newEntity = new Wall(px,py,hoff,voff,this,"mark" + numMark);
					foreground.add(newEntity);
				}
				
			} else if(entity.startsWith("d")){
				
				/* Chooses divider */
				String numDiv = entity.substring(1,2);
				int voff = -126 + 42 * Integer.parseInt(entity.substring(3,4));
				int hoff = -56 + (int) (56 * Double.parseDouble(entity.substring(5,8)));
				
				newEntity = new Wall(px,py,hoff,voff,this,"divider" + numDiv);
				foreground.add(newEntity);
				
				/* Chooses mark if necessary */
				if(entity.contains("mark")) {
					int numMark = Integer.parseInt(entity.substring(14,15));
					if (numMark == 1) {
						// top left mark
						voff -= 32;
						hoff += 22;
					} else if (numMark == 2) {
						// bottom left mark
						voff += 2;
						hoff += 22;
					} else if (numMark == 3) {
						// top right mark
						voff -= 20;
						hoff -= 10;
					} else if (numMark == 4) {
						// bottom right mark
						voff -= 0;
						hoff -= 10;
					}
					newEntity = new Wall(px,py,hoff,voff,this,"mark" + numMark);
					foreground.add(newEntity);
				}
				
			} else if(entity.equals("pl")){
				newEntity = new Pillar(px,py,0,-6,this,"pillar_left");
				foreground.add(newEntity);
			} else if(entity.equals("pshadow")){
				newEntity = new Pillar(px,py,0,-6,this,"pillar_shadow");
				background.add(newEntity);
			} else if(entity.equals("b")){
				newEntity = new Base(px,py,this,"normal_base");
				foreground.add(newEntity);
			} else if(entity.startsWith("enemy")){
				Scanner enemyType = new Scanner(entity.substring(6,entity.length()-1));
				enemyType.useDelimiter(",");
				String colour = enemyType.next();
				String orientation = enemyType.next();
				int health = enemyType.nextInt();
				int difficulty = enemyType.nextInt();
				enemyType.close();
				System.out.println(py);
				Character enemy = new Enemy(px, py, this, orientation, colour, health, difficulty);
				/* Si principe a la izquierda -> foreground, si principe a la derecha -> background */
				characters.add(enemy);
			}
			
		}
		square.setBackground(background);
		square.setCharacters(characters);
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
	
	private String getFileName(File f){
		return f.getName().substring(0,f.getName().length()-4);
	}
}
