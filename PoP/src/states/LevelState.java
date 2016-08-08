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
import entities.Closer;
import entities.Corner;
import entities.Door;
import entities.Enemy;
import entities.Entity;
import entities.FloorPanel;
import entities.Interface;
import entities.LooseFloor;
import entities.Opener;
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
	
	private Interface interfaz;
	
	private Player player;
	private Enemy enemy;
	
	private ArrayList<LooseFloor> falling_floor;
	private ArrayList<Door> doors;
	
	public LevelState(GameStateManager gsm, ConcurrentLinkedQueue<Key> keys, 
			Hashtable<String,Integer> keys_mapped, Loader loader, boolean start, Writter writter) {
		super(gsm, keys, keys_mapped, loader, writter);

		this.start = start;
	}

	@Override
	public void init() {
		
		falling_floor = new ArrayList<LooseFloor>();
		interfaz = new Interface(640, 400, 0, 0, loader);
		
//		// TESTING ENEMY
//		currentLevel = loader.loadLevel(INITIAL_LEVEL);
//		currentRoom = currentLevel.getRoom(1, 9);
//		
////		for(String key : loader.getAnimations("wall").keySet()){
////			System.out.println("key "+ key + " - Animation " + loader.getAnimations("wall").get(key).getId() );
////		}
//		player = new Player(100,250,loader, 1000, "right");
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
			doors = currentLevel.getDoors();
			
			player = new Player(500,100,loader, 3, "left");
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
		updateFallingFloor(elapsedTime);
		updateDoors(elapsedTime);
	}

	@Override
	public void draw(Graphics2D g) {
		currentRoom.draw(g);
		interfaz.drawSelf(g);
		player.drawLife(g);
		if(enemy!=null){
			enemy.drawLife(g);
		}
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
						currentRoom = currentLevel.getRoom(3, 7);
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
		Entity cornerFloor = null;
		Entity corner = null;
		Entity wall = null;
		
		int[] playerCenter = player.getCenter();
		int[] playerSquare = player.getSquare(playerCenter[0], playerCenter[1]);
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
				int[] cornerCenter = corner.getCenter();
				player.setCornerToClimb(corner);
				
				if ( (cornerCenter[0] < playerCenter[0]) &&
						corner.getTypeOfEntity().contains("right") &&
						player.getOrientation().equals("left") ) {
					// left corner
					player.setX(cornerCenter[0] + (2 * climbGap) );
					System.out.println("LEFT CORNER FIX");
					
					player.setCornerPositionFixed(true);
					player.setCanClimb(true);
				}
				else if ( (cornerCenter[0] > playerCenter[0]) &&
						corner.getTypeOfEntity().contains("left") &&
						player.getOrientation().equals("right") ) {
					// right corner
					player.setX(cornerCenter[0] - climbGap);
					System.out.println("RIGHT CORNER FIX");
					
					player.setCornerPositionFixed(true);
					player.setCanClimb(true);
				}
			}
			else if ( !player.startsClimbing() &&
						(player.isClimbing()) &&
						(player.getCornerToClimb() != null) ){
				
//				System.out.println("WE R IN");
				
				/* Normal climbing */
				// No need to check for collisions
				player.setCornerPositionFixed(false);
				
				if (player.getCurrentAnimation().getId().startsWith("scaling down_")) {
					Entity cornerToClimb = player.getCornerToClimb();
					int[] cc = cornerToClimb.getCenter();
					player.setX(cc[0] + 40);
					player.setY(cc[1] + 123);
				}
				else if (player.getCurrentAnimation().getId().startsWith("hanging idle_") &&
						!player.isCornerReached()) {
					Entity cornerToClimb = player.getCornerToClimb();
					int[] cc = cornerToClimb.getCenter();
					
					System.out.println("Corner center: (" + cc[0] + ", " + cc[1] + ")");
					System.out.println("Player center before: (" + playerCenter[0] + ", " + playerCenter[1] + ")");
					
					player.setX(cc[0] + 8);
					player.setY(cc[1] + 103);
					
//					System.out.println("Player center after: (" + pc[0] + ", " + pc[1] + ")");
					
					player.setCornerReached(true);
				}
				else if (!player.getCurrentAnimation().getId().startsWith("hanging idle_")) {
					player.setCornerReached(false);
				}
			}
		}
		else if ( player.isJumping() ) {
			
//			System.out.println("JUMPING");
			
			/* Checks if the player can land on the floor */
			floorPanel = checkFloorPanel();
			looseFloor = checkLooseFloor();
			if(looseFloor){
				System.out.println("LOOOOOOOSE");
			}
			wall = checkWall();
			
			if (floorPanel || looseFloor) {

				// player lands the jump
				player.setGrounded(true);
			}
			else if (wall != null) {
				
				// player has collided with a wall
//				player.collide_jump();
				player.fall();
				
				// corrects the player position after wall collision
				int wallxGap = 58;
				int wallyGap = 36;
				int[] wallCenter = wall.getCenter();
				
				//DEBUG
				System.out.println("TIPO DE MURO: " + wall.getTypeOfEntity());
				
				if (wall.getTypeOfEntity().contains("face")){ //wallCenter[0] < playerCenter[0]) {

					// left wall
					System.out.println("RIGHT WALL FIX");
					player.setX(wallCenter[0] + wallxGap);
					player.setY(wallCenter[1] + wallyGap);
				}
				else if (wall.getTypeOfEntity().contains("left")){ //wallCenter[0] > playerCenter[0]) {
					
					//right wall
					System.out.println("LEFT WALL FIX");
					player.setX(wallCenter[0] - wallxGap);
					player.setY(wallCenter[1] + wallyGap);
				}
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
			if(looseFloor){
				System.out.println("LOOOOOOOSE");
			}
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
			if(looseFloor){
				System.out.println("LOOOOOOOSE");
			}
			cornerFloor = checkCornerFloor();
			wall = checkWall();
			
			/* Check for corners */
			corner = checkCorner();
			
			if (!floorPanel && !looseFloor) {
				player.fall();
			}
			
		}
		else { /* Player is grounded */
			
//			System.out.println("GROUNDED");
			
			/* Checks if the player can stand on the floor */
			floorPanel = checkFloorPanel();
			looseFloor = checkLooseFloor();
			if(looseFloor){
				System.out.println("LOOOOOOOSE");
			}
			cornerFloor = checkCornerFloor();
			wall = checkWall();
			
			/* Check for corners */
			corner = checkCorner();
			
			/* If there is a corner nearby, the player can climb it */
			if (corner != null) {
				player.setGrounded(true);
			}
			
			// Corner climbing behaviour
			if (cornerFloor != null && 
					cornerFloor.getAnimations() != null) {
				
				player.setGrounded(true);
				
				// Checks if player can climb down the corner
				if (!player.isCornerPositionFixed() ) {
					
					int climbDownGap = 35;
					int safeWalkingGap = 80;
					
					player.setCornerToClimb(cornerFloor);
					int[] cornerCenter = cornerFloor.getCenter();
					
					if (Math.abs(cornerCenter[0] - playerCenter[0]) < climbDownGap &&
							player.getCurrentAnimation().getId().equals("idle_left") &&
							cornerFloor.getTypeOfEntity().contains("right")) {
						
						// left corner
						System.out.println("RIGHT CORNER DOWN FIX");

						Entity cornerToClimbDown = player.getCornerToClimb();
						int[] cc = cornerToClimbDown.getCenter();
						
//						System.out.println("Corner center: (" + cc[0] + ", " + cc[1] + ")");
//						System.out.println("Player center before: (" + playerCenter[0] + ", " + playerCenter[1] + ")");
//						
//						player.setX(cc[0] - 24);
//						player.setY(cc[1] - 44);
						
						player.setCanClimbDown(true);
					}
					else if (Math.abs(cornerCenter[0] - playerCenter[0]) < climbDownGap &&
							player.getCurrentAnimation().getId().equals("idle_right") &&
							cornerFloor.getTypeOfEntity().contains("left")) {
						
						// right corner
//						player.setX(cornerCenter[0] - climbDownGap);
						System.out.println("LEFT CORNER DOWN FIX");
						
						player.setCanClimbDown(true);
					}
					else {
						
						// player cannot climb down
						player.setCanClimbDown(false);
					}
					
//					System.out.println("walking: " + player.isWalkingAStep() +
//							", orientation: " + player.getOrientation().equals("left") +
//							", corner: " + cornerFloor.getTypeOfEntity().contains("left") +
//							", distance: " + (Math.abs(cornerCenter[0] - playerCenter[0]) < safeWalkingGap) );
					
					// controls walking on the edge behaviour
					if (player.isWalkingAStep() &&
							player.getOrientation().equals("right") &&
							cornerFloor.getTypeOfEntity().contains("right") &&
							Math.abs(cornerCenter[0] - playerCenter[0]) < safeWalkingGap) {
						
						// player is walking right, into a right corner
						if (player.isForcedToStop()) {
							
							System.out.println("VAMOH A CALMARNOH: " + cornerCenter[0] + " - " + playerCenter[0]);
							
							if ( (playerCenter[0] > cornerCenter[0] - 10) || player.isOnTheEdge()) {
								System.out.println("ole: " + safeWalkingGap/8);
								player.setX(cornerCenter[0] - safeWalkingGap/8 + player.getCurrentAnimation().getImage().getWidth()/2);
								player.setOnTheEdge(true);
							}
						}
						else {
							// player hasn't arrived yet to the corner edge
							player.setForcedToStop(true);
						}
					}
					else if (player.isWalkingAStep() && 
							player.getOrientation().equals("left") &&
							cornerFloor.getTypeOfEntity().contains("left") &&
							Math.abs(cornerCenter[0] - playerCenter[0]) < safeWalkingGap) {
						
						// player is walking left, into a left corner
						if (player.isForcedToStop()) {

							if ( (playerCenter[0] < cornerCenter[0] + 10) || player.isOnTheEdge()) {
								
//								System.out.println("STOPING PLAYER FROM FALLING OF THE EDGE: " + cornerCenter[0] + " - " + playerCenter[0]);
								player.setX(cornerCenter[0] + safeWalkingGap/8);
								player.setOnTheEdge(true);
							}
						}
						else {
							// player hasn't arrived yet to the corner edge
							player.setForcedToStop(true);
						}
					}
				}
			}
			else if (cornerFloor == null) {
				
				// There is nothing beneath the player, it falls
				if (!player.isFalling() && !player.isOnTheEdge()) {
					System.out.println("NOT AGAIN!!!");
					player.fall();
				}
			}
			else {
				
				player.setCanClimbDown(false);
			}
			
			// Wall collision behaviour
			if (wall != null) {
				
				// player has collided with a wall
				player.collide(wall);
				
				// corrects the player position after wall collision
				int wallxGap = 40;
				int[] wallCenter = wall.getCenter();
				
				if (wall.getTypeOfEntity().contains("face")) {

					// left wall
					System.out.println("WALKING RIGHT WALL FIX");
					player.setX(wallCenter[0] + wallxGap);
				}
				else if (wall.getTypeOfEntity().contains("left")) {
					
					//right wall
					System.out.println("WALKING LEFT WALL FIX");
					player.setX(wallCenter[0] - (wallxGap/4) );
				}
			}
		}	// END GROUNDED
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
					(name.startsWith("Pillar_") && !name.contains("shadow")) || 
					name.startsWith("Opener") || name.startsWith("Closer")) {
					
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
					
					
					if(name.startsWith("Opener")){
						if(((Opener) bgE).getDoor() == null){
							for(Door d : doors){
								if(d.getId() == ((Opener) bgE).getId()){
									((Opener) bgE).setDoor(d);
								}
							}
						}
						((Opener) bgE).openDoor(player);
					} else if(name.startsWith("Closer")){
						if(((Closer) bgE).getDoor() == null){
							for(Door d : doors){
								if(d.getId() == ((Closer) bgE).getId()){
									((Closer) bgE).setDoor(d);
								}
							}
						}
						((Closer) bgE).closeDoor(player);
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
	private Entity checkCornerFloor() {
		Entity corner = new Corner();
		
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
						
						// LEFT CORNER
						if ( (playerCenter[0] >= bgLeft) &&
								(playerCenter[0] <= bgRight) ) {
							
							corner = bgE;
						}
						else {
							
							// player falls
							corner = null;
						}
					}
					else if (name.contains("right") &&
							((bgTop - playerCenter[1]) <= playerHeight2) ) {
						
//						System.out.println("ALTURA CORRECTA     " + " " + bgLeft + " - " +  playerCenter[0] + " - " + bgRight);
						
						// RIGHT CORNER
						if ( (playerCenter[0] >= bgLeft) &&
								(playerCenter[0] <= bgRight) ) {
							
							corner = bgE;
						}
						else {
							
							// player falls
							corner = null;
						}
					}
				}
			}
		}
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
			
			LooseFloor toBeDeleted = null;
			for (Entity bgE : bgEntities) {
	
				String name = bgE.getTypeOfEntity();
				if ( name.startsWith("LooseFloor") ) {
					LooseFloor loose = (LooseFloor)bgE;
					if(!loose.isFalling() && !loose.isActivated()){
						loose.setActivated(true);
						loose.setRoom1(currentRoom.getRow() + 1);
						loose.setRoom2(currentRoom.getCol() + 1);
						loose.setRow(playerSquare[0]);
						loose.setCol(playerSquare[1]);
						System.out.println("SQUARE[0] " + playerSquare[0] + " SQUARE[1] " + playerSquare[1]);;
						toBeDeleted = loose;
						falling_floor.add(loose);
					}
					
//					int bgLeft = (int) bgE.getBoundingBox().getMinX();
//					int bgRight = (int) bgE.getBoundingBox().getMaxX();
//					int bgTop = (int) bgE.getBoundingBox().getMinY();
//					int bgBottom = (int) bgE.getBoundingBox().getMaxY();
//					
//					int[] ec = bgE.getCenter();
//					int[] es = bgE.getSquare();
//					
//					if ( (ec[1] - playerCenter[1]) <= playerHeight2 ) {
//						System.out.println("Aqui");
//						
//						int res = ec[1] - playerCenter[1];
//						
//						if ( (playerCenter[0] >= bgLeft) &&
//								(playerCenter[0] <= bgRight) ) {
//							
//							looseFloor = true;
//						}
//					}
				}
			}
			
			if(toBeDeleted != null){
				//currentRoom.deleteEntityBackground(toBeDeleted);
				toBeDeleted = null;
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
	 * @return true if there is a wall in front of the player
	 * where he will collide
	 */
	private Entity checkWall() {
		Entity wall = null;
		int wallGap = 20;
		
		/* Obtains the square where the center point of the player is placed */
		int[] playerCenter = player.getCenter();
		int[] playerSquare = player.getSquare(playerCenter[0], playerCenter[1]);
		
		// Checks that the square is within the room
		if (playerSquare[0] >= 0 && playerSquare[1] >= 0 &&
				playerSquare[0] <= 3 && playerSquare[1] <= 9) {
			
			/* Checks if there is a wall type object in current square */
			ArrayList<Entity> bgEntities = new ArrayList<Entity>();
			
			ArrayList<Entity> bEntities = currentRoom.getSquare(
					playerSquare[0], playerSquare[1]).getBackground();
			
			ArrayList<Entity> fEntities = currentRoom.getSquare(
					playerSquare[0], playerSquare[1]).getForeground();
			
			bgEntities.addAll(bEntities);
			bgEntities.addAll(fEntities);

			if (playerSquare[1] > 0) {
				
				// Left square
				ArrayList<Entity> bEntitiesLeft = currentRoom.getSquare(
						playerSquare[0], playerSquare[1] - 1).getBackground();
				
				ArrayList<Entity> fEntitiesLeft = currentRoom.getSquare(
						playerSquare[0], playerSquare[1] - 1).getForeground();
				
				bgEntities.addAll(bEntitiesLeft);
				bgEntities.addAll(fEntitiesLeft);
			}

			if (playerSquare[1] < 9) {
			
				// Right square
				ArrayList<Entity> bEntitiesRight = currentRoom.getSquare(
						playerSquare[0], playerSquare[1] + 1).getBackground();
				
				ArrayList<Entity> fEntitiesRight = currentRoom.getSquare(
						playerSquare[0], playerSquare[1] + 1).getForeground();
	
				bgEntities.addAll(bEntitiesRight);
				bgEntities.addAll(fEntitiesRight);
			}
			
			// Searches for wall type objects
			for (Entity bgE : bgEntities) {
				
				if (bgE.getBoundingBox() != null) {
					
					String name = bgE.getTypeOfEntity();
					if ( name.startsWith("Wall_") ) {
						
						// wall detected nearby
						int[] ec = bgE.getCenter();
						
						if ( Math.abs(ec[0] - playerCenter[0]) < wallGap) {

							// player is close to the wall
							wall = bgE;
						}
//						else if (ec[0] > playerCenter[0] &&
//								(Math.abs(ec[0] - playerCenter[0]) < 2*wallGap) &&
//								name.contains("face") ) {
//
//							// player has passed through the wall, must be fixed
//							wall = bgE;
//						}
//						else if (ec[0] < playerCenter[0] &&
//								(Math.abs(ec[0] - playerCenter[0]) < 2*wallGap) &&
//								name.contains("left")) {
//							
//							// player has passed through the wall, must be fixed
//							wall = bgE;
//						}
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
	
	public void updateFallingFloor(long elapsedTime){
		
		ArrayList<LooseFloor> toBeDeleted = new ArrayList<LooseFloor>();
		
		for(LooseFloor loose: falling_floor){
			
			if(!loose.isBroken()){
				loose.updateReal(elapsedTime);
				
				if(checkLooseCollision(loose)){
					/* Check collision */

					loose.setBroken();
					toBeDeleted.add(loose);
					Room looseRoom = currentLevel.getRoom(loose.getRoom1(), loose.getRoom2());
					looseRoom.deleteEntityBackground(loose);
					ArrayList<Entity> newEntities = new ArrayList<Entity>();
					
					/* Put broken */
					int[] looseCenter = loose.getCenter();
					int[] looseSquare = loose.getSquare(looseCenter[0], looseCenter[1]);
					int px = 64 + looseSquare[1] * 64;
					int py = (int)(6 + looseSquare[0] * 126);
					newEntities.add(new FloorPanel(px,py,0,-6,loader,"broken_left"));
					px = 64 + (looseSquare[1]+1) * 64;
					py = (int)(6 + looseSquare[0] * 126);
					newEntities.add(new FloorPanel(px,py,-12,-2,loader,"broken_right"));
					currentLevel.getRoom(loose.getRoom1(), loose.getRoom2()).addBackground(newEntities);
					
				} else{
					/* Didnt collided */
					
					if(loose.isLastFrameMoving()){
						
						System.out.println("AHORA CREARIAMOS ESQUINA");
						
						/* Create corners */
						Corner newCorner = returnCorner(currentRoom.getSquare(loose.getRow(), loose.getCol()));
						Corner newCornerRight = returnCorner(currentRoom.getSquare(loose.getRow(), loose.getCol() + 1));
						if(newCorner == null && newCornerRight == null){
							//Si no hay ninguna esquina -> se a�ade una derecha en la casilla actual y una izquierda en la casilla de la derecha
							newCorner = new Corner(getPX(loose.getCol()),getPY(loose.getRow()),-12,-2,loader,"normal_right");
							currentRoom.addToBackground(newCorner, currentRoom.getSquare(loose.getRow(), loose.getCol()));
							newCorner = new Corner(getPX(loose.getCol()+1),getPY(loose.getRow()),0,-6,loader,"normal_left");
							currentRoom.addToBackground(newCorner, currentRoom.getSquare(loose.getRow(), loose.getCol()+1));
						} else if(newCorner != null && newCorner.getTypeOfEntity().contains("left")){
							//Si hay esquina con nombre left -> se quita de la casilla actual y se mete una left en la casilla derecha
							currentRoom.deleteEntityBackground(newCorner, currentRoom.getSquare(loose.getRow(), loose.getCol()));
							newCorner = new Corner(getPX(loose.getCol()+1),getPY(loose.getRow()),0,-6,loader,"normal_left");
							currentRoom.addToBackground(newCorner, currentRoom.getSquare(loose.getRow(), loose.getCol()+1));
						} else if(newCornerRight.getTypeOfEntity().contains("right")){
							//Si hay una esquina con nombre right -> se quita de la casilla actual, y se mete una right en la casilla de la izq
							currentRoom.deleteEntityBackground(newCornerRight, currentRoom.getSquare(loose.getRow(), loose.getCol()+1));
							newCorner = new Corner(getPX(loose.getCol()),getPY(loose.getRow()),-12,-2,loader,"normal_right");
							currentRoom.addToBackground(newCorner, currentRoom.getSquare(loose.getRow(), loose.getCol()));
						}
						
					}
					
					if(loose.getY() > (400 + loose.getCurrentAnimation().getImage().getHeight())){
						/* Changed room */
						Room looseRoom = currentLevel.getRoom(loose.getRoom1(), loose.getRoom2());
						looseRoom.deleteEntityBackground(loose);
						
						loose.setY(0);
						loose.increaseRoom1();
						
						looseRoom = currentLevel.getRoom(loose.getRoom1(), loose.getRoom2());
						looseRoom.addToBackground(loose);
					}
				}
			} 
		}
		
		for(LooseFloor b : toBeDeleted){
			falling_floor.remove(b);
		}
		toBeDeleted = new ArrayList<LooseFloor>();
	}
	
	private boolean checkLooseCollision(LooseFloor loose){
		/* Obtains the square where the center point of the loose is placed */
		int looseWidth2 = loose.getCurrentAnimation().getImage().getWidth()/2;
		int looseHeight2 = loose.getCurrentAnimation().getImage().getHeight()/2;
		int[] looseCenter = loose.getCenter();
		int[] looseSquare = loose.getSquare(looseCenter[0], looseCenter[1]);
		
		Room looseRoom = currentLevel.getRoom(loose.getRoom1(), loose.getRoom2());
		
		boolean collision = false;

		if(looseSquare[0] != 4){
			ArrayList<Entity> bEntities = looseRoom.getSquare(
					looseSquare[0], looseSquare[1]).getBackground();
			
			for (Entity bgE : bEntities) {
				String name = bgE.getTypeOfEntity();
				if(name.startsWith("FloorPanel_normal_left") || name.startsWith("FloorPanel_broken_right")){
					int[] ec = bgE.getCenter();
					if(loose.getCenter()[1] - ec[1] > -10 && loose.getCenter()[1] - ec[1] <= 10){
						collision = true;	
					}
				}
			}
		}
		
		return collision;
	}
	
	private void updateDoors(long elapsedTime){
		for(Door d : doors){
			d.updateReal(elapsedTime);
		}
	}
	
	private Corner returnCorner(Square looseSquare){
		Corner toBeReturned = null;
		
		ArrayList<Entity> bg = looseSquare.getBackground();
		for (Entity bgE : bg) {
			if(bgE.getTypeOfEntity().startsWith("Corner")){
				toBeReturned = (Corner)bgE;
			}
		}
		
		return toBeReturned;
	}
	
	private int getPX(int col){
		return 64 + col * 64;
	}
	
	private int getPY(int row){
		return (int)(6 + row * 126);
	}
}
