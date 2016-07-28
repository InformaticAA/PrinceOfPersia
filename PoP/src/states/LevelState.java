package states;

import java.awt.Graphics2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;

import data.Level;
import data.Room;
import data.Square;
import entities.Entity;
import entities.Player;
import framework.Loader;
import framework.Writter;
import input.Key;

public class LevelState extends State{
	
	/* Constants */
	private final float INIT_TIME = 3600000;
	private final int INITIAL_HEALTH = 3;
	private final int INITIAL_LEVEL = 1;
	
	/* Variables */
	private boolean start;
	private float remainingTime;
	private Level currentLevel;
	private Room currentRoom;
	
	private Player player;
	
	
	public LevelState(GameStateManager gsm, ConcurrentLinkedQueue<Key> keys, 
			Hashtable<String,Integer> keys_mapped, Loader loader, boolean start, Writter writter) {
		super(gsm, keys, keys_mapped, loader, writter);

		this.start = start;
	}

	@Override
	public void init() {
		
		// TESTING ENEMY
//		currentRoom = currentLevel.getRoom(1, 9);
//		
////		for(String key : loader.getAnimations("wall").keySet()){
////			System.out.println("key "+ key + " - Animation " + loader.getAnimations("wall").get(key).getId() );
////		}
//		player = new Player(100,130,loader, 3, "right");
//		player.setySpeed(4);
//		
//		Enemy e = (Enemy)currentRoom.getCharacters().get(0);
//		
//		currentRoom.addCharacter(player);
//		player.isEnemySaw(true);
//		e.setPlayer(true, player);
		
		if(start){
			
			/* Start game */
			remainingTime = INIT_TIME;
			currentLevel = loader.loadLevel(INITIAL_LEVEL);
			currentRoom = currentLevel.getRoom(1, 7);
			
			player = new Player(500,60,loader, 3, "left");
			player.setCurrentAnimation("falling_left", 5);
			player.setySpeed(4);
			
			currentRoom.addCharacter(player);
		}
		
		else{
			
			/* Load game */
			File savegame = new File("savegame/save");
			if(savegame.exists() && !savegame.isDirectory()){
				
				/* There is actually a savegame -> resume game */
				Scanner save;
				try {
					save = new Scanner(new FileReader("savegame/save"));
					int line = 0;
					while(save.hasNextLine()){
						if(line == 0){
							currentLevel = loader.loadLevel(save.nextInt());
						} else if(line == 1){
							remainingTime = save.nextFloat();
						} else if(line == 2){
//							player = new Player(save.nextInt());
						}
						line++;
						save.nextLine();
					}
					save.close();
				} catch (FileNotFoundException e) {
					
					/* Exception */
					e.printStackTrace();

					/* There was not any savegame -> Start game */
					remainingTime = INIT_TIME;
					currentLevel = loader.loadLevel(INITIAL_LEVEL);
					currentRoom = currentLevel.getRoom(1, 7);
//					player = new Player(INITIAL_HEALTH);
				}
			}
			else{
				
				/* There was not any savegame -> Start game */
				remainingTime = INIT_TIME;
				currentLevel = loader.loadLevel(INITIAL_LEVEL);
				currentRoom = currentLevel.getRoom(1, 7);
//				player = new Player(100,300,loader);
			}
			
		}
		
	}

	@Override
	public void update(long elapsedTime) {
		remainingTime = remainingTime - elapsedTime;

		manageKeys();
		currentLevel.update(elapsedTime);
		checkPlayerCollisions(elapsedTime);
	}

	@Override
	public void draw(Graphics2D g) {
		currentRoom.draw(g);
	}

	@Override
	public void manageKeys() {
		Object[] keys_used = keys.toArray();
		keys.clear();
		Key e;
		if(keys_used.length!=0){
			for (int i = 0; i < keys_used.length; i++) {
				e = (Key)keys_used[i];
				if(e.isPressed()){
					
					/* key pressed */
					int key_pressed = e.getKeycode();
					
					if(key_pressed == keys_mapped.get(Key.ESCAPE)){
						
					} else if(key_pressed == keys_mapped.get(Key.CONTROL)){
						
					} else{
						player.manageKeyPressed(key_pressed, keys_mapped);
					}
					
					/* this has to be sent to the player */
					if(key_pressed == keys_mapped.get(Key.UP)){
						
					} else if(key_pressed == keys_mapped.get(Key.DOWN)){
						
					} else if(key_pressed == keys_mapped.get(Key.LEFT)){
						
					} else if(key_pressed == keys_mapped.get(Key.RIGHT)){
						
					} else if(key_pressed == keys_mapped.get(Key.ENTER)){
						
					} else if(key_pressed == keys_mapped.get(Key.SHIFT)){
						
					}
				} else{
					
					/* Key released */
					int key_released = e.getKeycode();
					
					if(key_released == keys_mapped.get(Key.ESCAPE)){
						
					} else if(key_released == keys_mapped.get(Key.CONTROL)){
						
					} else{
						player.manageKeyReleased(key_released, keys_mapped);
					}
				}
			}
		}
	}

	private void checkPlayerCollisions(long elapsedTime) {
		boolean floorPanel = false;
		boolean looseFloor = false;
		boolean cornerFloor = false;
		Entity corner = null;
		Entity wall = null;
		
		int[] pc = player.getCenter();
		int[] ps = player.getSquare(pc[0], pc[1]);
//		System.out.println("ps: (" + ps[0] + ", " + ps[1] + "), pc: (" + pc[0] + ", " + pc[1] + ")");
		
		if ( player.isColliding() ) {
			
		}
		else if ( player.isClimbing() ) {
			
//			System.out.println("CLIMBING");
			
			/* Checks if */
			corner = checkCorner();
			
			if (player.startsClimbing() &&
					(corner != null) &&
					!player.isCornerPositionFixed() ) {
				
				/* Initial climb */
				int climbGap = 20;
				int[] playerCenter = player.getCenter();
//				int[] playerSquare = player.getSquare(playerCenter[0], playerCenter[1]);
//				int[] playerSquareCenter = getSquareCenter(playerSquare[0], playerSquare[1]);
				
				int[] cornerCenter = corner.getCenter();
				
				if (cornerCenter[0] < playerCenter[0]) {
					// left corner
					player.setX(cornerCenter[0] + (2 * climbGap) );
					System.out.println("LEFT CORNER FIX");
					
					player.setCornerPositionFixed(true);
				}
				else {
					// right corner
					player.setX(cornerCenter[0] - climbGap);
					System.out.println("RIGHT CORNER FIX");
					
					player.setCornerPositionFixed(true);
				}
				
//				System.out.println("pc: (" + pc[0] + ", " + pc[1] + ")");
//				System.out.println("ps: (" + ps[0] + ", " + ps[1] + ")");
//				System.out.println("STARTS CLIMBING (" + playerSquare[0] + ", " + playerSquare[1] + ")");
//				System.out.println("psc: (" + playerSquareCenter[0] + ", " + playerSquareCenter[1] + ")");
			}
			else if ( !player.startsClimbing() ){
				
				/* Normal climbing */
				// No need to check for collisions
				player.setCornerPositionFixed(false);
				
				// DEBUG
				if (corner != null) {
					int[] cc = corner.getCenter();
					System.out.println("Corner center: (" + cc[0] + ", " + cc[1] + ")");
				}
				// FIN DEBUG
			}
		}
		else if ( player.isJumping() ) {
			
			System.out.println("JUMPING");
			
			/* Checks if the player can land on the floor */
			floorPanel = checkFloorPanel();
			looseFloor = checkLooseFloor();
			wall = checkWall();
			
			if (floorPanel || looseFloor) {

				// player lands the jump
				player.setGrounded(true);
			}
			else if (wall != null) {
				
				// player has collided with a wall
				player.collide_jump();
			}
			
		}
		else if ( player.isFalling() ) {
			
//			System.out.println("FALLING");
			
			/* Increases player's fall distance */
			int prevFallDistance = player.getFallDistance();
			player.setFallDistance(prevFallDistance + 1);
			
			/* Checks if the player can walk over the floor */
			floorPanel = checkFloorPanel();
			looseFloor = checkLooseFloor();
			wall = checkWall();
			
			if ( (floorPanel || looseFloor) ) {
				
				if ( player.getFallDistance() <= 150 ) {
					
					// short fall, player lands nicely
					loader.getSound("landing soft").play();
					System.out.println("SAFE LAND");
					player.safeLand();
				}
				else if ( player.getFallDistance() <= 300 ) {
					
					// long fall, but not dying
					player.riskyLand();
					loader.getSound("landing medium").play();
				}
				else {
					
					// free fall, player dies
					player.die();
					loader.getSound("landing hard").play();
				}
				
				player.setFreeFall(false);
				player.setGrounded(true);
			}
			
			if (wall !=  null) {
				
				// player has collided with a wall
			}
			
		}
		else if (player.wasJumping()) {
			
			/* Checks if the player can stand on the floor */
			floorPanel = checkFloorPanel();
			looseFloor = checkLooseFloor();
			cornerFloor = checkCornerFloor();
			wall = checkWall();
			
			/* Check for corners */
			corner = checkCorner();
			
			if (!floorPanel && !looseFloor) {
				player.fall();
				player.notJumping();
			}
			
		}
		else { /* Player is grounded */
			
//			System.out.println("GROUNDED");
			
			/* Checks if the player can stand on the floor */
			floorPanel = checkFloorPanel();
			looseFloor = checkLooseFloor();
			cornerFloor = checkCornerFloor();
			wall = checkWall();
			
			/* Check for corners */
			corner = checkCorner();
			
			/* If there is a corner nearby, the player can climb it */
			if (corner != null) {
				player.setClimbing(true);
				player.setGrounded(true);
			}
			
			if (cornerFloor) {
				player.setGrounded(true);
			}
			else {
				
				// There is nothing beneath the player, it falls
				if (!player.isFalling()) {
					player.fall();
				}
			}
			
			if (wall != null) {
				
				// player has collided with a wall
				player.collide(wall);
			}
		}
	}
	
	/**
	 * 
	 * @return true if there is any type of floor beneath of player
	 * where it can stand and stay grounded or land
	 */
	private boolean checkFloorPanel() {
		boolean leftPanel = false;
		boolean rightPanel = false;
		boolean leftCorner = false;
		boolean rightCorner = false;
		boolean leftFall = false;
		boolean rightFall = false;
		
		int securityGap = 5;
		
		/* Obtains the square where the center point of the player is placed */
		int playerWidth2 = player.getCurrentAnimation().getImage().getWidth()/2;
		int playerHeight2 = player.getCurrentAnimation().getImage().getHeight()/2;
		int[] playerCenter = player.getCenter();
		int[] playerSquare = player.getSquare(playerCenter[0], playerCenter[1]);
		int newPlayerX = playerCenter[0];
		
//		System.out.println("P_Coords: (" + playerCenter[0] + ", " + playerCenter[1] + ") - "  +
//					"(" + playerSquare[0] + ", " + playerSquare[1] + ")");
//		System.out.printf("SQUARE: (" + playerSquare[0] + ", " + playerSquare[1] + "): ");
		
		// Checks that the square is within the room
		if (playerSquare[0] >= 0 && playerSquare[1] >= 0 &&
				playerSquare[0] <= 3 && playerSquare[1] <= 9) {
			
			/* Checks if there is a panel floor type object in current square */
			ArrayList<Entity> bEntities = currentRoom.getSquare(
					playerSquare[0], playerSquare[1]).getBackground();
			
			ArrayList<Entity> fEntities = currentRoom.getSquare(
					playerSquare[0], playerSquare[1]).getForeground();
			
			ArrayList<Entity> bgEntities = new ArrayList<Entity>();
			
			bgEntities.addAll(bEntities);
			bgEntities.addAll(fEntities);
				
			for (Entity bgE : bgEntities) {
				String name = bgE.getTypeOfEntity();
				if ( name.startsWith("FloorPanel_") ||
					(name.startsWith("Pillar_") && !name.contains("shadow")) ) {
					
					int bgLeft = (int) bgE.getBoundingBox().getMinX();
					int bgRight = (int) bgE.getBoundingBox().getMaxX();
					int bgTop = (int) bgE.getBoundingBox().getMinY();
					int bgBottom = (int) bgE.getBoundingBox().getMaxY();
					
					int bgWidth = bgE.getCurrentAnimation().getImage().getWidth();
					
					int[] ec = bgE.getCenter();
					int[] es = bgE.getSquare();
					
					// DEBUG entity and player centers
//					System.out.println("ec: " + ec[0] + ", " + ec[1] + " -> " +
//										"pc: " + playerCenter[0] + ", " + playerCenter[1]);
//					System.out.println("ph2: " + playerHeight2);
	
					if (name.contains("left") &&
							((ec[1] - playerCenter[1]) <= playerHeight2) ) {
						
						// player lands on the floor (according to y axis)
						if ( (playerCenter[0] >= bgLeft) &&
								(playerCenter[0] <= bgRight) ) {
							
							// player lands on the floor (according to x axis)
//							System.out.println("LEFT LANDING");
							leftPanel = true;
							
							/* Corrects the player's position on the floor */
							int newPlayerY = ec[1] - 1;
							player.setY(newPlayerY);
						}
						else {
							// player falls, there is no floor beneath him
							leftFall = true;
							newPlayerX = ec[0] - bgWidth / 4;
						}
					}
					else if (name.contains("right") &&
							((ec[1] - playerCenter[1]) <= playerHeight2) ) {
						
						if ( (playerCenter[0] >= bgLeft) &&
								(playerCenter[0] <= bgRight) ) {
							
//							System.out.println("RIGHT LANDING");
							rightPanel = true;
							
							/* Corrects the player's position on the floor */
							int newPlayerY = ec[1] - 1;
							player.setY(newPlayerY);
						}
						else {
							rightFall = true;
							newPlayerX = ec[0] + bgWidth;
						}
					}
				}
				else if (name.startsWith("Corner_")) {
					if (name.contains("left")) {
						leftCorner = true;
					}
					else if (name.contains("right")) {
						rightCorner = true;
					}
				}
			}
			
			/* Player's behaviour */
			if (leftFall && !leftCorner) leftPanel = true;
			else if (leftFall && leftCorner) {
				leftPanel = false;
				player.setX(newPlayerX);
			}
			
			if (rightFall && !rightCorner) rightPanel = true;
			else if (rightFall && rightCorner) {
				rightPanel = false;
				player.setX(newPlayerX);
			}
		}
		
		return (leftPanel || rightPanel);
	}
	
	/**
	 * 
	 * @return false if there the player can stand in a corner,
	 * true if there is corner in the same square as the player, but
	 * the player cant be on it, the player will fall
	 */
	private boolean checkCornerFloor() {
		boolean corner = true;
		
		/* Obtains the square where the center point of the player is placed */
		int playerWidth2 = player.getCurrentAnimation().getImage().getWidth()/2;
		int playerHeight2 = player.getCurrentAnimation().getImage().getHeight()/2;
		int[] playerCenter = player.getCenter();
		int[] playerSquare = player.getSquare(playerCenter[0], playerCenter[1]);
		
		// Checks that the square is within the room
		if (playerSquare[0] >= 0 && playerSquare[1] >= 0 &&
				playerSquare[0] <= 3 && playerSquare[1] <= 9) {
			
			/* Checks if there is a panel floor type object in current square */
			ArrayList<Entity> bEntities = currentRoom.getSquare(
					playerSquare[0], playerSquare[1]).getBackground();
			
			ArrayList<Entity> fEntities = currentRoom.getSquare(
					playerSquare[0], playerSquare[1]).getForeground();
			
			ArrayList<Entity> bgEntities = new ArrayList<Entity>();
			
			bgEntities.addAll(bEntities);
			bgEntities.addAll(fEntities);
				
			for (Entity bgE : bgEntities) {
	
				String name = bgE.getTypeOfEntity();
				if ( name.startsWith("Corner_") ) {
					
					int bgLeft = (int) bgE.getBoundingBox().getMinX();
					int bgRight = (int) bgE.getBoundingBox().getMaxX();
					int bgTop = (int) bgE.getBoundingBox().getMinY();
					int bgBottom = (int) bgE.getBoundingBox().getMaxY();
					
					int[] ec = bgE.getCenter();
					int[] es = bgE.getSquare();
					
//					System.out.println(name.contains("right") + " - " + ((bgTop - playerCenter[1]) <= playerHeight2));
	
					if (name.contains("left") &&
							((bgTop - playerCenter[1]) <= playerHeight2) ) {
						
						if ( (playerCenter[0] >= bgLeft) &&
								(playerCenter[0] <= bgRight) ) {
							
	//						leftCorner = true;
						}
						else {
							corner = false;
						}
					}
					else if (name.contains("right") &&
							((bgTop - playerCenter[1]) <= playerHeight2) ) {
						
//						System.out.println("ESQUINA DERECHA");
						
						if ( (playerCenter[0] >= bgLeft) &&
								(playerCenter[0] <= bgRight) ) {
							
//							System.out.println("SUUUUUU D");
	//						rightCorner = true;
						}
						else {
							corner = false;
						}
					}
				}
			}
		}
		
		/* Player's behaviour */
		return corner;
	}
	
	/**
	 * 
	 * @return true if there is a loose floor panel beneath the player
	 */
	private boolean checkLooseFloor() {
		boolean looseFloor = false;
		
		/* Obtains the square where the center point of the player is placed */
		int playerWidth2 = player.getCurrentAnimation().getImage().getWidth()/2;
		int playerHeight2 = player.getCurrentAnimation().getImage().getHeight()/2;
		int[] playerCenter = player.getCenter();
		int[] playerSquare = player.getSquare(playerCenter[0], playerCenter[1]);
		
		// Checks that the square is within the room
		if (playerSquare[0] >= 0 && playerSquare[1] >= 0 &&
				playerSquare[0] <= 3 && playerSquare[1] <= 9) {
		
			/* Checks if there is a loose floor type object in current square */
			ArrayList<Entity> bgEntities = currentRoom.getSquare(
					playerSquare[0], playerSquare[1]).getBackground();
			
			for (Entity bgE : bgEntities) {
	
				String name = bgE.getTypeOfEntity();
				if ( name.startsWith("LooseFloor") ) {
					
					int bgLeft = (int) bgE.getBoundingBox().getMinX();
					int bgRight = (int) bgE.getBoundingBox().getMaxX();
					int bgTop = (int) bgE.getBoundingBox().getMinY();
					int bgBottom = (int) bgE.getBoundingBox().getMaxY();
					
					int[] ec = bgE.getCenter();
					int[] es = bgE.getSquare();
					
					if ( (ec[1] - playerCenter[1]) <= playerHeight2 ) {
						
						int res = ec[1] - playerCenter[1];
						
						if ( (playerCenter[0] >= bgLeft) &&
								(playerCenter[0] <= bgRight) ) {
							
							looseFloor = true;
						}
					}
				}
			}
		}
		return looseFloor;
	}
	
	/**
		 * 
		 * @return true if there is a corner that the player can reach
		 * doing a vertical jump
		 */
		private Entity checkCorner() {
			Entity corner = null;
			
			/* Obtains the square where the center point of the player is placed */
			int playerWidth2 = player.getCurrentAnimation().getImage().getWidth()/2;
			int playerHeight2 = player.getCurrentAnimation().getImage().getHeight()/2;
			int[] playerCenter = player.getCenter();
			int[] playerSquare = player.getSquare(playerCenter[0], playerCenter[1]);
			
			// Checks that the square is within the room
			if (playerSquare[0] > 0 && playerSquare[1] >= 0 &&
					playerSquare[0] <= 3 && playerSquare[1] < 9) {
				
				/* Checks if there is a corner type object in upleft square */
				ArrayList<Entity> bEntities = new ArrayList<Entity>();
				
				if (playerSquare[1] > 0) {
					bEntities = currentRoom.getSquare(
							playerSquare[0] - 1, playerSquare[1] - 1).getBackground();
				}
				
				/* Checks if there is a corner type object in top square */
				ArrayList<Entity> topBgEntities = currentRoom.getSquare(
						playerSquare[0] - 1, playerSquare[1]).getBackground();
				
				ArrayList<Entity> bgEntities = new ArrayList<Entity>();
				bgEntities.addAll(bEntities);
				bgEntities.addAll(topBgEntities);
				
				for (Entity bgE : bgEntities) {
	
					String name = bgE.getTypeOfEntity();
					if ( name.startsWith("Corner") ) {
						
						int[] ec = bgE.getCenter();
						int[] es = bgE.getSquare();
	
	//					System.out.println(bgE.getTypeOfEntity() + "(" + es[0] + ", " + es[1] + "):");
	//					System.out.println("	E_Coords: (" + ec[0] + ", " + ec[1] + ")" +
	//										"/(" + playerSquare[0] + ", " + playerSquare[1] + ")");
						
						if ( (ec[0] < playerCenter[0]) &&
								(ec[1] < playerCenter[1]) &&
								(player.getOrientation().equals("left")) ) {
						
							// corner is at player's left side and player is looking left
							corner = bgE;
	//						System.out.println("LEFT CORNER DETECTED - (" + ec[0] + ", " + ec[1] + ")");
						}
					}
				}
				
				/* Checks if there is a corner type object in upright square */
				bgEntities = currentRoom.getSquare(
						playerSquare[0] - 1, playerSquare[1] + 1).getBackground();
				
				for (Entity bgE : bgEntities) {
	
					String name = bgE.getTypeOfEntity();
					if ( name.startsWith("Corner") ) {
						
						int[] ec = bgE.getCenter();
						
						if ( (ec[0] > playerCenter[0]) &&
								(player.getOrientation().equals("right")) ) {
						
							// corner is at player's right side and player is looking right
							corner = bgE;
	//						System.out.println("RIGHT CORNER DETECTED");
						}
					}
				}
			}
			
			return corner;
		}

	/**
	 * 
	 * @return true if there is a corner that the player can reach
	 * doing a vertical jump
	 */
	private Entity checkWall() {
		Entity wall = null;
		int gap = 15;
		
		/* Obtains the square where the center point of the player is placed */
		int playerWidth2 = player.getCurrentAnimation().getImage().getWidth()/2;
		int playerHeight2 = player.getCurrentAnimation().getImage().getHeight()/2;
		int[] playerCenter = player.getCenter();
		int[] playerSquare = player.getSquare(playerCenter[0], playerCenter[1]);
		
		// Checks that the square is within the room
		if (playerSquare[0] >= 0 && playerSquare[1] >= 0 &&
				playerSquare[0] <= 3 && playerSquare[1] <= 9) {
			
			/* Checks if there is a panel floor type object in current square */
			ArrayList<Entity> bEntities = currentRoom.getSquare(
					playerSquare[0], playerSquare[1]).getBackground();
			
			ArrayList<Entity> fEntities = currentRoom.getSquare(
					playerSquare[0], playerSquare[1]).getForeground();
			
			ArrayList<Entity> bgEntities = new ArrayList<Entity>();
			
			bgEntities.addAll(bEntities);
			bgEntities.addAll(fEntities);
			
			for (Entity bgE : bgEntities) {
				
				if (bgE.getBoundingBox() != null) {
					
					int bgLeft = (int) bgE.getBoundingBox().getMinX();
					int bgRight = (int) bgE.getBoundingBox().getMaxX();
					int bgTop = (int) bgE.getBoundingBox().getMinY();
					int bgBottom = (int) bgE.getBoundingBox().getMaxY();
					int bgWidth = bgE.getCurrentAnimation().getImage().getWidth();
					
					String name = bgE.getTypeOfEntity();
					if ( name.startsWith("Wall_") ) {
						
						int[] ec = bgE.getCenter();
						int[] es = bgE.getSquare();
						
						if ( ((playerCenter[0] - bgRight) <= playerWidth2) ) {
							
							int newPlayerX = 0;
							
							if (player.getOrientation().equals("right")) {
								
								// wall is at player's right side and player is looking right
								Square targetSquare = currentRoom.getSquare(playerSquare[0], playerSquare[1]);
								int[] newCoords = targetSquare.getCenter(playerSquare[0], playerSquare[1] + 1);
								newPlayerX = newCoords[0];
							}
							else {
								/* Corrects the player's distance from the wall*/
								newPlayerX = ec[0] + bgWidth/2 + gap;
							}
							player.setX(newPlayerX);
							wall = bgE;
						}
					}
				}
			}
			
			/* Checks if there is a panel floor type object in current square */
			bEntities = currentRoom.getSquare(
					playerSquare[0], playerSquare[1]).getBackground();
			
			fEntities = currentRoom.getSquare(
					playerSquare[0], playerSquare[1]).getForeground();
			
			bgEntities.addAll(fEntities);
			
			for (Entity bgE : bgEntities) {
				
				if (bgE.getBoundingBox() != null) {
					
					int bgLeft = (int) bgE.getBoundingBox().getMinX();
					int bgRight = (int) bgE.getBoundingBox().getMaxX();
					int bgTop = (int) bgE.getBoundingBox().getMinY();
					int bgBottom = (int) bgE.getBoundingBox().getMaxY();
					int bgWidth = bgE.getCurrentAnimation().getImage().getWidth();
					
					String name = bgE.getTypeOfEntity();
					if ( name.startsWith("Wall_") ) {
						
						int[] ec = bgE.getCenter();
						
						if ( ((playerCenter[0] - bgLeft) <= playerWidth2) ) {
							
							int newPlayerX = 0;
							
							if (player.getOrientation().equals("left")) {
								
								// wall is at player's right side and player is looking right
								Square targetSquare = currentRoom.getSquare(playerSquare[0], playerSquare[1]);
								int[] newCoords = targetSquare.getCenter(playerSquare[0], playerSquare[1]-1);
								newPlayerX = newCoords[0] + 32;
							}
							else {
								/* Corrects the player's distance from the wall*/
								newPlayerX = ec[0] - gap/2;
							}
							player.setX(newPlayerX);
							wall = bgE;
						}
					}
				}
			}
		}
		
		return wall;
	}
	
	public int[] getSquareCenter(int i, int j) {
		
		/* Calculates the center of the square */
		int px = 64 + j * 64;
		int py = (int)(6 + 63 + (i-1) * 126);
		
		return new int[]{px, py};
	}
}
