package states;

import java.awt.Graphics2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;

import data.Level;
import data.Room;
import data.Square;
import data.Text;
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
import entities.SpikeFloor;
import framework.Loader;
import framework.Writter;
import game.Game;
import input.Key;
import kuusisto.tinysound.Music;
import kuusisto.tinysound.TinySound;

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
	
	private List<LooseFloor> falling_floor;
	private List<Door> doors;
	
	private Music win_song;
	private Music death_song;
	private Music credits_song;
	private Music end_song;
	
	private boolean over;
	private boolean credits;
	private long counter;
	
	private List<Text> texts;
	
	public LevelState(GameStateManager gsm, ConcurrentLinkedQueue<Key> keys, 
			Hashtable<String,Integer> keys_mapped, Loader loader, boolean start, Writter writter) {
		super(gsm, keys, keys_mapped, loader, writter);

		this.start = start;
		win_song = TinySound.loadMusic(new File("resources/Music/guard_death_and_obtaining_the_sword.ogg"));
		death_song = TinySound.loadMusic(new File("resources/Music/fight_death.ogg"));
		end_song = TinySound.loadMusic(new File("resources/Music/killed_Jaffar.ogg"));
		credits_song = TinySound.loadMusic(new File("resources/Music/won.ogg"));
	}

	@Override
	public void init() {
		
		falling_floor = new LinkedList<LooseFloor>();
		interfaz = new Interface(640, 400, 0, 0, loader);
		over = false;
		credits = false;
		texts = new LinkedList<Text>();
		counter = 0;
		
//		// TESTING ENEMY
//		currentLevel = loader.loadLevel(INITIAL_LEVEL);
//		currentRoom = currentLevel.getRoom(1, 9);
//		
////		for(String key : loader.getAnimations("wall").keySet()){
////			//System.out.println("key "+ key + " - Animation " + loader.getAnimations("wall").get(key).getId() );
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

//			player = new Player(100,110,loader, INITIAL_HEALTH, "right"); // primer piso
			player = new Player(100,240,loader, INITIAL_HEALTH, "right"); // segundo piso
//			player = new Player(200,370,loader, INITIAL_HEALTH, "left"); // tercer piso
			player.setCurrentAnimation("idle_right", 5);
//			player.setCurrentAnimation("falling_left", 5);
			player.setySpeed(6);
			
			// DEBUG POTIONS
			player.setHp(3);
			
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
		//currentLevel.update(elapsedTime);
		currentRoom.update(elapsedTime);
		if(!over && player.isDead()){
			over = true;
			death_song.play(false);
			String message = "Press space to restart";
			texts.add(Writter.createText(message, (Game.WIDTH/2) - (16* message.length()/2) , Game.HEIGHT - 16));
		} if(!over && player.hasFinished()){
			over = true;
			end_song.play(false);
		}
		if(!over){
			checkPlayerCollisions(elapsedTime);
		}
		if(!credits && over && player.hasFinished() && end_song.done() && counter < 1000){
			counter = counter + elapsedTime;
		}
		if(!credits && over && player.hasFinished() && end_song.done() && counter > 1000){
			credits_song.play(false);
			credits = true;
			currentRoom = currentLevel.getRoom(1, 8);
			String message = "To be continued";
			texts.add(Writter.createText(message, (Game.WIDTH/2) - (16* message.length()/2) , (Game.HEIGHT/2) - 20));
			message = "Press space to return to menu";
			texts.add(Writter.createText(message, (Game.WIDTH/2) - (16* message.length()/2) , (Game.HEIGHT) - 16));
		}
		updateFallingFloor(elapsedTime);
		updateDoors(elapsedTime);
		checkPlayerSquare();
		checkPlayerFinnish();
	}

	@Override
	public void draw(Graphics2D g) {
		currentRoom.draw(g);
		interfaz.drawSelf(g);
		if(!credits){
			player.drawLife(g);
		}
		if(enemy!=null){
			enemy.drawLife(g);
		}
		for(Text t : texts){
			t.drawSelf(g);
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
						
					} else if(key_pressed == keys_mapped.get(Key.SPACE)){
						if(over && player.isDead()){
							win_song.stop();
							death_song.stop();
							gsm.setState(GameStateManager.MAINGAMESTATE);
						} else if(over || credits){
							win_song.stop();
							death_song.stop();
							credits_song.stop();
							end_song.stop();
							gsm.setState(GameStateManager.MENUSTATE);
						}
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
		Entity floorPanel = null;
		Entity looseFloor = null;
		Entity floorBeneath = null;
		Entity cornerFloor = null;
		Entity cornerFloorEntity = null;
		Entity corner = null;
		Entity cornerJumping = null;
		Entity wall = null;
		Entity longLandFloor = null;
		Entity potion = null;
		Entity sword = null;
		Entity door = null;
		
		int[] playerCenter = player.getCenter();
		int[] playerSquare = player.getSquare(playerCenter[0], playerCenter[1]);
//		//System.out.println("ps: (" + ps[0] + ", " + ps[1] + "), pc: (" + pc[0] + ", " + pc[1] + ")");
		
		if ( player.isColliding() ) {
			
			// resets some climb conditions
			player.setCanClimb(false);
			player.setCanClimbDown(false);
			player.setCornerToClimb(null);
			player.setCornerToClimbDown(null);
		}
		else if ( player.isClimbing() ) {
			
			Entity debugCorner = player.getCornerToClimb();
			
			if (debugCorner != null) {
				System.out.println("debugCorner: " + debugCorner.getTypeOfEntity() + ": " +
						debugCorner.getCenter()[0] + ", " + debugCorner.getCenter()[1]);
			}
			
//			System.out.println("CLIMBING");
			
			/* Checks if */
			corner = checkCorner();
			cornerFloor = checkCornerFloor();
			floorPanel = checkFloorPanel();
			looseFloor = checkLooseFloor();
			floorBeneath = checkFloorBeneath();
			door = checkClimbingDoor();
			
			if (player.startsClimbing() &&
					(corner != null) &&
					!player.isCanClimb() &&
					!player.isCornerPositionFixed() ) {
				
				/* Initial climb */
				int climbGap = 20;
				int[] cornerCenter = corner.getCenter();
				player.setCornerToClimb(corner);
				player.setCanClimbDown(false);
				
				if ( (Math.abs(cornerCenter[0] - playerCenter[0]) < climbGap*4) &&
						corner.getTypeOfEntity().contains("right") &&
						player.getOrientation().equals("left") ) {
					
					// left corner
					System.out.println("LEFT CORNER FIX");
					if (cornerFloor != null && cornerFloor.getAnimations() != null){
						
						// player is climbing from the edge
						player.setX(cornerCenter[0] + (climbGap) );
					}
					else {
						player.setX(cornerCenter[0] + (2 * climbGap) );
					}
					
					player.setCornerPositionFixed(true);
					player.setCanClimb(true);
				}
				else if ( (Math.abs(cornerCenter[0] - playerCenter[0]) < climbGap*4) &&
						corner.getTypeOfEntity().contains("left") &&
						player.getOrientation().equals("right") ) {
					
					// right corner
					System.out.println("RIGHT CORNER FIX");
					
					if (cornerFloor != null){
						
						// player is climbing from the edge
						player.setX(cornerCenter[0] - climbGap/2);
					}
					else {
						player.setX(cornerCenter[0] - climbGap);
					}
					
					player.setCornerPositionFixed(true);
					player.setCanClimb(true);
				}
			}
			else if ( !player.startsClimbing() &&
						player.isClimbing() &&
						( (player.isCanClimb() && player.getCornerToClimb() != null) ||
						  (player.isCanClimbDown() && player.getCornerToClimbDown() != null) )
						){
				
				/* Normal climbing */
				// No need to check for collisions
				player.setCornerPositionFixed(false);
				
				// choses the right corner entity
				Entity currentCorner = null;
				Entity cornerToClimb = player.getCornerToClimb();
				Entity cornerToClimbDown = player.getCornerToClimbDown();
				
				if (cornerToClimb != null) {
					currentCorner = cornerToClimb;
				}
				else if (cornerToClimbDown != null) {
					currentCorner = cornerToClimbDown;
				}
				
				if (currentCorner != null) {
					
					if (player.getCurrentAnimation().getId().startsWith("scaling down_") &&
							player.isCornerReached()) {
						int[] cc = currentCorner.getCenter();
						
						if (currentCorner.getTypeOfEntity().contains("right")) {
							player.setX(cc[0] + 40);
							player.setY(cc[1] + 125);
						}
						else if (currentCorner.getTypeOfEntity().contains("left")) {
							player.setX(cc[0] - 16);
							player.setY(cc[1] + 126);
						}
						player.setCornerReached(false);
						
						// checks if there is floor panels beneath the player to scale down or fall
						if (floorBeneath == null) {
							player.setCanLandScalingDown(false);
							System.out.println("Vooooy a caer " + player.getCenter()[0] + " - " + player.getCenter()[1] + "    -    " + player.getSquare()[0] + " - " + player.getSquare()[1]);
							player.setStraightFall(true);
							player.fall();
						}
						else {
							player.setCanLandScalingDown(true);
						}
					}
					else if (player.getCurrentAnimation().getId().startsWith("hanging idle_") &&
							!player.isCornerReached()) {
						int[] cc = currentCorner.getCenter();
						
						if (currentCorner.getTypeOfEntity().contains("right")) {
							player.setX(cc[0] + 8);
							player.setY(cc[1] + 103);
							System.out.println("HANGEADA RIGHT: " + player.getX() + ", " + player.getY());
						}
						else if (currentCorner.getTypeOfEntity().contains("left")) {
							player.setX(cc[0] + 10);
							player.setY(cc[1] + 106);
							System.out.println("HANGEADA LEFT: " + player.getX() + ", " + player.getY());
						}
						player.setCornerReached(true);
					}
					else if (player.getCurrentAnimation().getId().startsWith("clipping_")) {
						
						// player is getting up, have to check if there is a closed door
						if (door != null && door.getCurrentAnimation().getId().contains("closed")) {
							int[] cc = currentCorner.getCenter();
							
							if (currentCorner.getTypeOfEntity().contains("right")) {
								player.setX(cc[0] + 40);
								player.setY(cc[1] + 125);
							}
							else if (currentCorner.getTypeOfEntity().contains("left")) {
								player.setX(cc[0] - 16);
								player.setY(cc[1] + 126);
							}
							player.scaleDown();
						}
					}
					else if (!player.getCurrentAnimation().getId().startsWith("hanging idle_")) {
						player.setCornerReached(false);
					}
				}
			}
			else if (!player.isClimbing() && corner == null) {
				player.setCornerToClimb(null);
			}
		}
		else if ( player.isJumping() ) {
			
//			System.out.println("JUMPING");
//			System.out.println("canLand: " + player.isCanLand());
//			System.out.println("canLongLand: " + player.isCanLongLand());
			
			/* Checks if the player can land on the floor */
			floorPanel = checkFloorPanel();
			looseFloor = checkLooseFloor();
			cornerJumping = checkCornerJumping();
			wall = checkWall();
			door = checkDoor();
			
			if (!player.isLongLand() && 
					player.getCurrentAnimation().getId().contains("jump_") &&
					player.getCurrentAnimation().isLastSprite() &&
					player.getCurrentAnimation().isLastFrame() ){
				longLandFloor = checkLongJumpFloor();
			}
			
			if (player.getCurrentAnimation().getId().contains("landing")) {
				if (floorPanel != null) {
					player.setY( (int) floorPanel.getBoundingBox().getMinY());
				}
				
				player.setCanLongLand(false);
				player.setLongLand(false);
			}

			if (floorPanel != null || looseFloor != null) {
				
				// player lands the jump
				player.setCanLand(true);
				player.setGrounded(true);
			}
			else {
				
				// player falls while trying to jump
				player.setCanLand(false);
				player.setGrounded(false);
			}
			
			// checks if player can land the starting jump
//			System.out.println("isLongLand: " + player.isLongLand());
			if ( longLandFloor != null && //!player.isLongLand() &&
					player.getCurrentAnimation().getId().contains("jump_") &&
					player.getCurrentAnimation().isLastSprite() &&
					player.getCurrentAnimation().isLastFrame()) {
				
				System.out.println("GONNA LAND LIKE A CHARM :D");
				
				// player can land the jump
				player.setCanLongLand(true);
				player.setLongLand(true);
			}
			else if ( longLandFloor == null && //!player.isLongLand() &&
					player.getCurrentAnimation().getId().contains("jump_") &&
					player.getCurrentAnimation().isLastSprite() &&
					player.getCurrentAnimation().isLastFrame()) {
				
				System.out.println("GONNA FALL");
				
				// player cannot land the jump, he will fall
				player.setCanLongLand(false);
				player.setLongLand(true);
			}
			
			if (cornerJumping != null) {
				
				int[] cornerCenter = cornerJumping.getCenter();
				int xReachDistance = 40;
				int yReachDistance = -10;
				
				// player can reach a corner to hang in mid air
				if ( (Math.abs(playerCenter[0] - cornerCenter[0]) < xReachDistance) &&
					 ( (playerCenter[1] - cornerCenter[1]) > yReachDistance) ) {
					
					// player is close enough in both axis to reach the corner
					player.setCanClimb(true);
					player.setCornerToClimb(cornerJumping);
				}
				else {
					
					// player is too far to reach the corner in mid air
					player.setCanClimb(false);
					player.setCornerToClimb(null);
				}
			}
			
			if (wall != null) {
				
				// player has collided with a wall
//				player.collide_jump();
				System.out.println("Vooooy a caer 2 " + player.getCenter()[0] + " - " + player.getCenter()[1] + "    -    " + player.getSquare()[0] + " - " + player.getSquare()[1]);

				player.fall();
				
				// corrects the player position after wall collision
				int wallxGap = 58;
				int wallyGap = 36;
				int[] wallCenter = wall.getCenter();
				
				//DEBUG
				//System.out.println("TIPO DE MURO: " + wall.getTypeOfEntity());
				
				if (wall.getTypeOfEntity().contains("face")){ //wallCenter[0] < playerCenter[0]) {

					// left wall
					//System.out.println("RIGHT WALL FIX");
					player.setX(wallCenter[0] + wallxGap);
					player.setY(wallCenter[1] + wallyGap);
				}
				else if (wall.getTypeOfEntity().contains("left") || 
						 wall.getTypeOfEntity().contains("single") ){ //wallCenter[0] > playerCenter[0]) {
					
					//right wall
					//System.out.println("LEFT WALL FIX");
					player.setX(wallCenter[0] - wallxGap/4);
					player.setY(wallCenter[1] + wallyGap);
				}
			}
			
			if (door != null && door.getTypeOfEntity().contains("normal")) {
	
				// player has collided with a door
				player.fall();
				
				// corrects the player position after wall collision
				int doorxGap = 58;
				int dooryGap = 36;
				int[] doorCenter = door.getCenter();
				
				if (doorCenter[0] < playerCenter[0]) {

					// left wall
					player.setX(doorCenter[0] + doorxGap);
					player.setY(doorCenter[1] + dooryGap);
				}
				else if (doorCenter[0] > playerCenter[0]) {
					
					//right wall
					player.setX(doorCenter[0] - doorxGap/4);
					player.setY(doorCenter[1] + dooryGap);
				}
			}
			
		}
		else if ( player.isFalling()) {
			
//			System.out.println("FALLING");
			
			/* Increases player's fall distance */
			int prevFallDistance = player.getFallDistance();
			player.setFallDistance(prevFallDistance + player.getFallingSpeed()/4);
			
			/* Checks if the player can walk over the floor */
			floorPanel = checkFloorPanel();
			looseFloor = checkLooseFloor();
			cornerJumping = checkCornerJumping();
			wall = checkWall();
			
			if ( (floorPanel != null/*|| looseFloor*/) ) {
				
				System.out.println("fallDistance: " + player.getFallDistance());
				player.setFallingSpeed(0);
				
				if( player.isDead()){
					// player is dead D:
				}
				else if ( player.isSafeFall() ) {
					
					// short fall, player lands nicely
					loader.getSound("landing soft").play();
					player.safeLand();
					System.out.println("SAFE LAND");
				}
				else if ( player.isRiskyFall() ) {
					
					// long fall, but not dying
					player.riskyLand();
					loader.getSound("landing medium").play();
					System.out.println("RISKY LAND");
				}
				else {
					
					// free fall, player dies
					player.die();
					loader.getSound("falling").stop();
					loader.getSound("landing hard").play();
					player.setY((int) floorPanel.getBoundingBox().getMaxY()); 
					System.out.println("DEATH LAND");
				}
				
				player.setFreeFall(false);
				player.setFallCollided(false);
				player.setStraightFall(false);
				player.setGrounded(true);
				
				// resets some climb conditions
				player.setCanClimb(false);
				player.setCanClimbDown(false);
				player.setCornerToClimb(null);
				player.setCornerToClimbDown(null);
				
				/* Check loose floors in that row to shake it baby */
				checkLoosesToShake();
			}
			else {
				if(!player.isSafeFall() && !player.isRiskyFall() &&
						!player.isScreaming()) {
					
					// player screams when the fall is going to kill him
					loader.getSound("falling").play();
					player.setScreaming(true);
				}
			}
			
			// Checks for corner to reach in mid air
			if (cornerJumping != null) {
				
				int[] cornerCenter = cornerJumping.getCenter();
				int xReachDistance = 40;
				int yReachDistance = -10;
				
				// player can reach a corner to hang in mid air
				if ( (Math.abs(playerCenter[0] - cornerCenter[0]) < xReachDistance) &&
					 ( (playerCenter[1] - cornerCenter[1]) > yReachDistance) ) {
					
					// player is close enough in both axis to reach the corner
					player.setCanClimb(true);
					player.setCornerToClimb(cornerJumping);
					System.out.println(cornerJumping.getTypeOfEntity() + ": " + 
								cornerJumping.getCenter()[0] + ", " + 
								cornerJumping.getCenter()[1]);
				}
				else {
					
					// player is too far to reach the corner in mid air
					player.setCanClimb(false);
					player.setCornerToClimb(null);
				}
			}
			
			if (wall !=  null) {
				
				// player has collided with a wall
				if ( player.getOrientation().equals("left") &&
					 wall.getTypeOfEntity().contains("face") ) {
					
					player.setX(wall.getCenter()[0] + 40);
				}
				else if (player.getOrientation().equals("left")) {
					
					player.setX(wall.getCenter()[0] - 40);
				}
				else if ( player.getOrientation().equals("right") &&
					 wall.getTypeOfEntity().contains("face") ) {
				
					player.setX(wall.getCenter()[0] + 40);
				}
				else if (player.getOrientation().equals("right")) {
					
					player.setX(wall.getCenter()[0] - 40);
				}
				player.setFallCollided(true);
			}
		}
		else { /* Player is grounded */
			
//			System.out.println("GROUNDED");
			player.setLongLand(false);
			
			/* Checks if the player can stand on the floor */
			floorPanel = checkFloorPanel();
			looseFloor = checkLooseFloor();
			cornerFloor = checkCornerFloor();
			cornerFloorEntity = checkCornerFloorEntity();
			wall = checkWall();
			potion = checkPotion();
			sword = checkSword();
			door = checkDoor();
			
			/* Check for corners */
			corner = checkCorner();
			
			//checks if player can take the sword from floor
				
			// checks if player can drink a nearby potion
			player.setCanDrink(potion != null);
			
			player.setCanPickSword(sword!=null);
			
			if (player.isDrinkingPotion() && potion != null) {
				deletePotion(potion);
			}
			
			if(player.isPickingSword() && sword != null && player.hasSword()){
				win_song.play(false);
				deleteSword(sword);
			}
			
			if (floorPanel != null) {
				player.setY((int) floorPanel.getBoundingBox().getMinY());
			}
			
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
					
					player.setCornerToClimbDown(cornerFloor);
					int[] cornerCenter = cornerFloor.getCenter();
					
					if (Math.abs(cornerCenter[0] - playerCenter[0]) < climbDownGap &&
							player.getCurrentAnimation().getId().equals("idle_left") &&
							cornerFloor.getTypeOfEntity().contains("right")) {
						
						// left corner
						System.out.println("RIGHT CORNER DOWN FIX");

						Entity cornerToClimbDown = player.getCornerToClimbDown();
						int[] cc = cornerToClimbDown.getCenter();
						
						player.setCanClimbDown(true);
						
						player.setCanClimb(false);
						player.setCornerToClimb(null);
					}
					else if (Math.abs(cornerCenter[0] - playerCenter[0]) < climbDownGap &&
							player.getCurrentAnimation().getId().equals("idle_right") &&
							cornerFloor.getTypeOfEntity().contains("left")) {
						
						// right corner
//						player.setX(cornerCenter[0] - climbDownGap);
						//System.out.println("LEFT CORNER DOWN FIX");
						
						player.setCanClimbDown(true);
						
						player.setCanClimb(false);
						player.setCornerToClimb(null);
					}
					else {
						
						// player cannot climb down
						player.setCanClimbDown(false);
					}
					
//					//System.out.println("walking: " + player.isWalkingAStep() +
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
							
							if ( (playerCenter[0] > cornerCenter[0] - 10) || player.isOnTheEdge()) {
								//System.out.println("ole: " + safeWalkingGap/8);
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
								
//								//System.out.println("STOPING PLAYER FROM FALLING OF THE EDGE: " + cornerCenter[0] + " - " + playerCenter[0]);
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
				
				// there is no corner near the player that he can climb down
				// or walk to it's edge
				player.setCornerToClimbDown(null);
				
				// There is nothing beneath the player, it falls
				if (!player.isFalling() && !player.isOnTheEdge() && !checkFloorSides()) {
					System.out.println("Vooooy a caer 4 " + player.getCenter()[0] + " - " + player.getCenter()[1] + "    -    " + player.getSquare()[0] + " - " + player.getSquare()[1]);
					player.setStraightFall(true);
					player.fall();
					
					// corrects player position before fall
					if (cornerFloorEntity != null) {
						int[] cornerCenter = cornerFloorEntity.getCenter();
						int cornerGap = 40;
						
						if (cornerFloorEntity.getTypeOfEntity().contains("left")) {
							player.setX(cornerCenter[0] - cornerGap/3);
							System.out.println("LEFT CORNER FALL FIX");
						}
						else if (cornerFloorEntity.getTypeOfEntity().contains("right")) {
							player.setX(cornerCenter[0] + cornerGap);
							System.out.println("RIGHT CORNER FALL FIX");
						}
					}
					
				}
			}
			else {
				player.setCanClimbDown(false);
			}
			
			// Wall collision behaviour
			if (wall != null) {

				// player has collided with a wall
				System.out.println("Wall Collision: " + wall.getTypeOfEntity());
				
				// corrects the player position after wall collision
				int wallxGap = 40;
				int[] wallCenter = wall.getCenter();
				
				if (wall.getTypeOfEntity().contains("face")) {

					// left wall
					//System.out.println("WALKING RIGHT WALL FIX");
					player.setOrientation("left");
					player.collide(wall);
					player.setX(wallCenter[0] + wallxGap);
				}
				else if (wall.getTypeOfEntity().contains("left") ||
						 wall.getTypeOfEntity().contains("single") ) {
					
					//right wall
					//System.out.println("WALKING LEFT WALL FIX");
					player.setOrientation("right");
					player.collide(wall);
					player.setX(wallCenter[0] - (wallxGap/4) );
				}
			}
			
			// Door collision behaviour
			if (door != null && door.getTypeOfEntity().contains("normal")) {

				if ( (door.getCurrentAnimation().getId().contains("closed")) ||
					 (door.getCurrentAnimation().getId().contains("half") &&
						!player.getCurrentAnimation().getId().contains("crouching")) ) {
					
					// player has collided with a door
					// corrects the player position after wall collision
					int doorxGap = 40;
					int[] doorCenter = door.getCenter();
					
					if (doorCenter[0] < playerCenter[0]) {
		
						// left side door
						player.setOrientation("left");
						player.collide(door);
						player.setX(doorCenter[0] + doorxGap);
					}
					else if (doorCenter[0] > playerCenter[0]) {
						
						//right side door
						player.setOrientation("right");
						player.collide(door);
						player.setX(doorCenter[0] - (doorxGap/2) );
					}
				}
				else {
					// door is opened enough to player to go through
					
				}
			}
		}	// END GROUNDED
	}
	
	
	/**
	 * 
	 * @return true if there is any type of floor beneath of player
	 * where it can stand and stay grounded or land
	 */
	private Entity checkFloorPanel() {
		Entity floorPanel = null;
		Entity finalFloor = null;
		boolean leftPanel = false;
		boolean rightPanel = false;
		boolean leftCorner = false;
		boolean rightCorner = false;
		boolean leftFall = false;
		boolean rightFall = false;
		
		int securityGap = 10;
		
		/* Obtains the square where the center point of the player is placed */
		int playerHeight2 = player.getCurrentAnimation().getImage().getHeight()/2;
		int[] playerCenter = player.getCenter();
		int[] playerSquare = player.getSquare(playerCenter[0], playerCenter[1]);
		
		// Checks that the square is within the room
		if (playerSquare[0] >= 0 && playerSquare[1] >= 0 &&
				playerSquare[0] <= 3 && playerSquare[1] <= 9) {
			
			/* Checks if there is a panel floor type object in current square */
			List<Entity> bEntities = currentRoom.getSquare(
					playerSquare[0], playerSquare[1]).getBackground();
			
			List<Entity> fEntities = currentRoom.getSquare(
					playerSquare[0], playerSquare[1]).getForeground();
			
			List<Entity> bgEntities = new LinkedList<Entity>();
			
			bgEntities.addAll(bEntities);
			bgEntities.addAll(fEntities);
				
			for (Entity bgE : bgEntities) {
				String name = bgE.getTypeOfEntity();
				if ( (name.startsWith("FloorPanel_") && !name.contains("skeleton")) ||
					(name.startsWith("Pillar_") && !name.contains("shadow") && !name.contains("top")) || 
					name.startsWith("Opener") || name.startsWith("Closer") || 
					name.startsWith("SpikeFloor")){
					
					int bgLeft = (int) bgE.getBoundingBox().getMinX();
					int bgRight = (int) bgE.getBoundingBox().getMaxX();
					int bgTop = (int) bgE.getBoundingBox().getMinY();
					int[] ec = bgE.getCenter();
					
					if (name.contains("left") &&
							((ec[1] - playerCenter[1]) <= playerHeight2 + securityGap) ) {
						
						floorPanel = bgE;
						
						// player lands on the floor (according to y axis)
						if ( (playerCenter[0] >= bgLeft) &&
								(playerCenter[0] <= bgRight) ) {
							
							// player lands on the floor (according to x axis)
//							//System.out.println("LEFT LANDING");
							leftPanel = true;
						}
						else {
							// player falls, there is no floor beneath him
							leftFall = true;
//							newPlayerX = ec[0] - bgWidth / 4;
						}
					}
					else if (name.contains("right")  &&
							((ec[1] - playerCenter[1]) <= playerHeight2 + securityGap) ) {
						
						floorPanel = bgE;
						
						if ( (playerCenter[0] >= bgLeft) &&
								(playerCenter[0] <= bgRight) ) {
							
//							//System.out.println("RIGHT LANDING");
							rightPanel = true;
						}
						else {
							rightFall = true;
//							newPlayerX = ec[0] + bgWidth;
						}
					}
					
					if(name.startsWith("Opener") && player.isGrounded()){
						finalFloor = bgE;
						if(((Opener) bgE).getDoor() == null){
							for(Door d : doors){
								if(d.getId() == ((Opener) bgE).getId()){
									((Opener) bgE).setDoor(d);
								}
							}
						}
						((Opener) bgE).openDoor(player);
					} else if(name.startsWith("Closer") && player.isGrounded()){
						finalFloor = bgE;
						if(((Closer) bgE).getDoor() == null){
							for(Door d : doors){
								if(d.getId() == ((Closer) bgE).getId()){
									((Closer) bgE).setDoor(d);
								}
							}
						}
						((Closer) bgE).closeDoor(player);
					}
					
					if(name.startsWith("SpikeFloor")){
						finalFloor = bgE;
						if(!((SpikeFloor) bgE).isActivated()){
							((SpikeFloor) bgE).activate(player);
							if(player.isFalling() || player.isFreeFall() || (player.getCurrentAnimation().getId().startsWith("running") && !player.isJumping())){
								if(player.isFalling() || player.isFreeFall()){
									player.setGrounded(true);
									player.setFreeFall(false);
									player.setFalling(false);
									
								}
								
								bgRight = (int) bgE.getBoundingBox().getMaxX();
								bgTop = (int) bgE.getBoundingBox().getMaxY();
								player.setY(bgTop - 16);
								player.setSpiked();
								if(player.getOrientation().equals("right")){
									player.setX(bgRight - 12);
								} else{
									player.setX(bgRight - 30);
								}
							}
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
			}
			
			if (rightFall && !rightCorner) rightPanel = true;
			else if (rightFall && rightCorner) {
				rightPanel = false;
			}
		}
		
		if (leftPanel || rightPanel) {
			finalFloor = floorPanel;
		}
		
		return finalFloor;
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
			List<Entity> bEntities = currentRoom.getSquare(
					playerSquare[0], playerSquare[1]).getBackground();
			
			List<Entity> fEntities = currentRoom.getSquare(
					playerSquare[0], playerSquare[1]).getForeground();
			
			List<Entity> bgEntities = new LinkedList<Entity>();
			
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
					
					if (name.contains("left") &&
							(ec[1] > playerCenter[1]) ) {
						
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
							(ec[1] > playerCenter[1]) ) {
						
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
	 * @return a corner located near the player on the floor
	 * beneath him
	 */
	private Entity checkCornerFloorEntity() {
		Entity corner = new Corner();
		
		/* Obtains the square where the center point of the player is placed */
		int[] playerCenter = player.getCenter();
		int[] playerSquare = player.getSquare(playerCenter[0], playerCenter[1]);
		
		// Checks that the square is within the room
		if (playerSquare[0] >= 0 && playerSquare[1] >= 0 &&
				playerSquare[0] <= 3 && playerSquare[1] <= 9) {
			
			/* Checks if there is a panel floor type object in current square */
			List<Entity> bEntities = currentRoom.getSquare(
					playerSquare[0], playerSquare[1]).getBackground();
			
			List<Entity> fEntities = currentRoom.getSquare(
					playerSquare[0], playerSquare[1]).getForeground();
			
			List<Entity> bgEntities = new LinkedList<Entity>();
			
			bgEntities.addAll(bEntities);
			bgEntities.addAll(fEntities);
				
			for (Entity bgE : bgEntities) {
	
				String name = bgE.getTypeOfEntity();
				if ( name.startsWith("Corner_") ) {
					int[] ec = bgE.getCenter();

					if (ec[1] > playerCenter[1]) {
						
						// the corner is beneath the player
						corner = bgE;
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
	private Entity checkLooseFloor() {
		Entity looseFloor = null;
		
		/* Obtains the square where the center point of the player is placed */
		int playerWidth2 = player.getCurrentAnimation().getImage().getWidth()/2;
		int playerHeight2 = player.getCurrentAnimation().getImage().getHeight()/2;
		int[] playerCenter = player.getCenter();
		int[] playerSquare = player.getSquare(playerCenter[0], playerCenter[1]);
		
		// Checks that the square is within the room
		if (playerSquare[0] >= 0 && playerSquare[1] >= 0 &&
				playerSquare[0] <= 3 && playerSquare[1] <= 9) {
		
			/* Checks if there is a loose floor type object in current square */
			List<Entity> bgEntities = currentRoom.getSquare(
					playerSquare[0], playerSquare[1]).getBackground();
			
			LooseFloor toBeDeleted = null;
			Entity rightFloor = null;
			Entity leftFloor = null;
			for (Entity bgE : bgEntities) {
	
				String name = bgE.getTypeOfEntity();
				if (player.isGrounded() && name.startsWith("LooseFloor") ) {
					LooseFloor loose = (LooseFloor)bgE;
					if(!loose.isFalling() && !loose.isActivated()){
						System.out.println(playerSquare[0] + " - " + playerSquare[1]);
						leftFloor = currentRoom.returnNamedEntityBackground("FloorPanel_normal_left", currentRoom.getSquare(playerSquare[0], playerSquare[1]));
						rightFloor = currentRoom.returnNamedEntityBackground("FloorPanel_normal_right", currentRoom.getSquare(playerSquare[0], playerSquare[1]));
						loose.setActivated(true);
						loose.setRoom1(currentRoom.getRow() + 1);
						loose.setRoom2(currentRoom.getCol() + 1);
						loose.setRow(playerSquare[0]);
						loose.setCol(playerSquare[1]);
						//System.out.println("SQUARE[0] " + playerSquare[0] + " SQUARE[1] " + playerSquare[1]);;
						toBeDeleted = loose;
						falling_floor.add(loose);

						// returns the current loose floor
						looseFloor = bgE;
					}
//					else if (!loose.isFalling()) {
//						
//						// returns the current loose floor
//						looseFloor = bgE;
//					}
//					else {
//						
//						// loose is falling, player falls
//						looseFloor = null;
//					}
					
//					int bgLeft = (int) bgE.getBoundingBox().getMinX();
//					int bgRight = (int) bgE.getBoundingBox().getMaxX();
//					int bgTop = (int) bgE.getBoundingBox().getMinY();
//					int bgBottom = (int) bgE.getBoundingBox().getMaxY();
//					
//					int[] ec = bgE.getCenter();
//					int[] es = bgE.getSquare();
//					
//					if ( (ec[1] - playerCenter[1]) <= playerHeight2 ) {
//						//System.out.println("Aqui");
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
				currentRoom.deleteEntityBackground(leftFloor, currentRoom.getSquare(playerSquare[0], playerSquare[1]));
				currentRoom.deleteEntityBackground(rightFloor, currentRoom.getSquare(playerSquare[0], playerSquare[1]));
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
		int[] playerCenter = player.getCenter();
		int[] playerSquare = player.getSquare(playerCenter[0], playerCenter[1]);
		
		// Checks that the square is within the room
		if (playerSquare[0] > 0 && playerSquare[1] >= 0 &&
				playerSquare[0] <= 3 && playerSquare[1] < 9) {
			
			/* Checks if there is a corner type object in upleft square */
			List<Entity> bEntities = new LinkedList<Entity>();
			List<Entity> fEntities = new LinkedList<Entity>();
			
			if (playerSquare[1] > 0) {
				bEntities = currentRoom.getSquare(
						playerSquare[0] - 1, playerSquare[1] - 1).getBackground();
				fEntities = currentRoom.getSquare(
						playerSquare[0] - 1, playerSquare[1] - 1).getForeground();
			}
			
			/* Checks if there is a corner type object in top square */
			List<Entity> topBgEntities = currentRoom.getSquare(
					playerSquare[0] - 1, playerSquare[1]).getBackground();
			
			List<Entity> bgEntities = new LinkedList<Entity>();
			bgEntities.addAll(bEntities);
			bgEntities.addAll(fEntities);
			bgEntities.addAll(topBgEntities);
			
			for (Entity bgE : bgEntities) {
				String name = bgE.getTypeOfEntity();
				if ( name.startsWith("Corner") ) {
					
					int[] ec = bgE.getCenter();
//					if ( (ec[0] < playerCenter[0]) &&
							if ((ec[1] < playerCenter[1]) &&
							(player.getOrientation().equals("left")) ) {
					
						// corner is at player's left side and player is looking left
						corner = bgE;
//						//System.out.println("LEFT CORNER DETECTED - (" + ec[0] + ", " + ec[1] + ")");
					}
				} else if( name.startsWith("Loose") && player.isClimbingLastFrame()){
					((LooseFloor)bgE).justShakeItBaby();
				}
			}
			
			/* Checks if there is a corner type object in upright square */
			bgEntities = currentRoom.getSquare(
					playerSquare[0] - 1, playerSquare[1] + 1).getBackground();
			
			fEntities = currentRoom.getSquare(
					playerSquare[0] - 1, playerSquare[1] + 1).getForeground();
			
			bgEntities.addAll(fEntities);
			
			for (Entity bgE : bgEntities) {

				String name = bgE.getTypeOfEntity();
				if ( name.startsWith("Corner") ) {
					
					int[] ec = bgE.getCenter();
					
//					if ( (ec[0] > playerCenter[0]) &&
							if((player.getOrientation().equals("right")) ) {
					
						// corner is at player's right side and player is looking right
						corner = bgE;
//						//System.out.println("RIGHT CORNER DETECTED");
					}
				}
			}
		}
		
		return corner;
	}
	
	/**
	 * 
	 * @return true if there is a corner that the player can reach
	 * doing a horizontal jump
	 */
	private Entity checkCornerJumping() {
		Entity corner = null;
		
		/* Obtains the square where the center point of the player is placed */
		int playerWidth2 = player.getCurrentAnimation().getImage().getWidth()/2;
		int playerHeight2 = player.getCurrentAnimation().getImage().getHeight()/2;
		int[] playerCenter = player.getCenter();
		int[] playerSquare = player.getSquare(playerCenter[0], playerCenter[1]);
		
		// Checks that the square is within the room
		if (playerSquare[0] > 0 && playerSquare[1] >= 0 &&
				playerSquare[0] <= 3 && playerSquare[1] < 9) {
			
			/* Checks if there is a corner type object in left square */
			List<Entity> bEntities = new LinkedList<Entity>();
			List<Entity> fEntities = new LinkedList<Entity>();
			
			if (playerSquare[1] > 0) {
				bEntities = currentRoom.getSquare(
						playerSquare[0], playerSquare[1] - 1).getBackground();
				fEntities = currentRoom.getSquare(
						playerSquare[0], playerSquare[1] - 1).getForeground();
			}
			
			/* Checks if there is a corner type object in current square */
			List<Entity> currBgEntities = currentRoom.getSquare(
					playerSquare[0], playerSquare[1]).getBackground();
			
			List<Entity> bgEntities = new LinkedList<Entity>();
			bgEntities.addAll(bEntities);
			bgEntities.addAll(fEntities);
			bgEntities.addAll(currBgEntities);
			
			for (Entity bgE : bgEntities) {

				String name = bgE.getTypeOfEntity();
				if ( name.startsWith("Corner") ) {
					
					int[] ec = bgE.getCenter();
					int[] es = bgE.getSquare();

					if ( (ec[0] < playerCenter[0]) &&
							(bgE.getTypeOfEntity().contains("right")) &&
							(player.getOrientation().equals("left")) ) {
					
						// corner is at player's left side and player is looking left
						corner = bgE;
					}
				}
			}
			
			/* Checks if there is a corner type object in right square */
			bgEntities = currentRoom.getSquare(
					playerSquare[0] - 1, playerSquare[1] + 1).getBackground();
			
			fEntities = currentRoom.getSquare(
					playerSquare[0] - 1, playerSquare[1] + 1).getForeground();
			
			bgEntities.addAll(fEntities);
			
			/* Checks if there is a corner type object in current square */
			currBgEntities = currentRoom.getSquare(
					playerSquare[0], playerSquare[1]).getBackground();
			
			bgEntities = new LinkedList<Entity>();
			bgEntities.addAll(bEntities);
			bgEntities.addAll(fEntities);
			bgEntities.addAll(currBgEntities);
			
			for (Entity bgE : bgEntities) {

				String name = bgE.getTypeOfEntity();
				if ( name.startsWith("Corner") ) {
					
					int[] ec = bgE.getCenter();
					
					if ( (ec[0] > playerCenter[0]) &&
							bgE.getTypeOfEntity().contains("left") &&
							(player.getOrientation().equals("right")) ) {
					
						// corner is at player's right side and player is looking right
						corner = bgE;
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
			List<Entity> bgEntities = new LinkedList<Entity>();
			
			List<Entity> bEntities = currentRoom.getSquare(
					playerSquare[0], playerSquare[1]).getBackground();
			
			List<Entity> fEntities = currentRoom.getSquare(
					playerSquare[0], playerSquare[1]).getForeground();
			
			bgEntities.addAll(bEntities);
			bgEntities.addAll(fEntities);

			if (playerSquare[1] > 0) {
				
				// Left square
				List<Entity> bEntitiesLeft = currentRoom.getSquare(
						playerSquare[0], playerSquare[1] - 1).getBackground();
				
				List<Entity> fEntitiesLeft = currentRoom.getSquare(
						playerSquare[0], playerSquare[1] - 1).getForeground();
				
				bgEntities.addAll(bEntitiesLeft);
				bgEntities.addAll(fEntitiesLeft);
			}

			if (playerSquare[1] < 9) {
			
				// Right square
				List<Entity> bEntitiesRight = currentRoom.getSquare(
						playerSquare[0], playerSquare[1] + 1).getBackground();
				
				List<Entity> fEntitiesRight = currentRoom.getSquare(
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
	
	/**
	 * 
	 * @return true if there is any type of floor in the jump
	 * target square where the player can land
	 */
	private Entity checkLongJumpFloor() {
		Entity floorPanel = null;
		
		/* Obtains the square where the center point of the player is placed */
		int[] playerCenter = player.getCenter();
		int[] playerSquare = player.getSquare(playerCenter[0], playerCenter[1]);

		// Checks that the square is within the room
		if (playerSquare[0] >= 0 && playerSquare[1] >= 0 &&
				playerSquare[0] <= 3 && playerSquare[1] <= 9) {
			
			/* Checks if there is a panel floor type object
			 *  in the landing target square (left jump) */
			List<Entity> bEntities; 
			List<Entity> fEntities;
			
			System.out.println(playerSquare[0] + " - " + playerSquare[1]);
			
			if (playerSquare[1] == 0 || playerSquare[1] == 9) {
				bEntities = currentRoom.getSquare(
						playerSquare[0], playerSquare[1]).getBackground();
				fEntities = currentRoom.getSquare(
						playerSquare[0], playerSquare[1]).getBackground();
			}
			else if (player.getOrientation().equals("left") ) {
				
				bEntities = currentRoom.getSquare(
						playerSquare[0], playerSquare[1] - 1).getBackground();
				
				fEntities = currentRoom.getSquare(
						playerSquare[0], playerSquare[1] - 1).getForeground();
			}
			else if (player.getOrientation().equals("right") ) {
				
				bEntities = currentRoom.getSquare(
						playerSquare[0], playerSquare[1] + 1).getBackground();
				
				fEntities = currentRoom.getSquare(
						playerSquare[0], playerSquare[1] + 1).getForeground();
			}
			else if (playerSquare[1] == 0 || playerSquare[1] == 9) {
				bEntities = currentRoom.getSquare(
						playerSquare[0], playerSquare[1]).getBackground();
				fEntities = currentRoom.getSquare(
						playerSquare[0], playerSquare[1]).getBackground();
			}
			else {
				bEntities = null;
				fEntities = null;
			}
			
			List<Entity> bgEntities = new LinkedList<Entity>();
			bgEntities.addAll(bEntities);
			bgEntities.addAll(fEntities);
				
			for (Entity bgE : bgEntities) {
				String name = bgE.getTypeOfEntity();
				System.out.println("entities: " + name);
				if ( name.startsWith("FloorPanel_") ||
					name.startsWith("Loose") ||
					name.startsWith("Corner") ||
					(name.startsWith("Pillar_") && !name.contains("shadow")) || 
					name.startsWith("Opener") || name.startsWith("Closer") || 
					name.startsWith("SpikeFloor")){
					
					floorPanel = bgE;
				}
			}
		}
			
		return floorPanel;
	}
	
	/**
	 * 
	 * @return true if there is a door in front of the player
	 * where he will collide
	 */
	private Entity checkDoor() {
		Entity door = null;
		int doorGap = 20;
		
		/* Obtains the square where the center point of the player is placed */
		int[] playerCenter = player.getCenter();
		int[] playerSquare = player.getSquare(playerCenter[0], playerCenter[1]);
		
		// Checks that the square is within the room
		if (playerSquare[0] >= 0 && playerSquare[1] >= 0 &&
				playerSquare[0] <= 3 && playerSquare[1] <= 9) {
			
			/* Checks if there is a wall type object in current square */
			List<Entity> bgEntities = new LinkedList<Entity>();
			
			List<Entity> bEntities = currentRoom.getSquare(
					playerSquare[0], playerSquare[1]).getBackground();
			
			List<Entity> fEntities = currentRoom.getSquare(
					playerSquare[0], playerSquare[1]).getForeground();
			
			bgEntities.addAll(bEntities);
			bgEntities.addAll(fEntities);

			if (playerSquare[1] > 0) {
				
				// Left square
				List<Entity> bEntitiesLeft = currentRoom.getSquare(
						playerSquare[0], playerSquare[1] - 1).getBackground();
				
				List<Entity> fEntitiesLeft = currentRoom.getSquare(
						playerSquare[0], playerSquare[1] - 1).getForeground();
				
				bgEntities.addAll(bEntitiesLeft);
				bgEntities.addAll(fEntitiesLeft);
			}

			if (playerSquare[1] < 9) {
			
				// Right square
				List<Entity> bEntitiesRight = currentRoom.getSquare(
						playerSquare[0], playerSquare[1] + 1).getBackground();
				
				List<Entity> fEntitiesRight = currentRoom.getSquare(
						playerSquare[0], playerSquare[1] + 1).getForeground();
	
				bgEntities.addAll(bEntitiesRight);
				bgEntities.addAll(fEntitiesRight);
			}
			
			// Searches for wall type objects
			for (Entity bgE : bgEntities) {
				
				if (bgE.getBoundingBox() != null) {
					
					String name = bgE.getTypeOfEntity();
					if ( name.startsWith("Door") ) {
						
						// wall detected nearby
						int[] ec = bgE.getCenter();
						
						if ( Math.abs(ec[0] - playerCenter[0]) < doorGap) {

							// player is close to the door
							door = bgE;
						}
					}
				}
			}
		}		
		return door;
	}
	
	/**
	 * 
	 * @return true if there is a door in in the middle
	 * of player's climbing animation
	 */
	private Entity checkClimbingDoor() {
		Entity door = null;
		int doorGap = 20;
		
		/* Obtains the square where the center point of the player is placed */
		int[] playerCenter = player.getCenter();
		int[] playerSquare = player.getSquare(playerCenter[0], playerCenter[1]);
		
		// Checks that the square is within the room
		if (playerSquare[0] >= 0 && playerSquare[1] >= 0 &&
				playerSquare[0] <= 3 && playerSquare[1] <= 9) {
			
			/* Checks if there is a wall type object in current square */
			List<Entity> bgEntities = new LinkedList<Entity>();
			
			List<Entity> bEntities = currentRoom.getSquare(
					playerSquare[0], playerSquare[1]).getBackground();
			
			List<Entity> fEntities = currentRoom.getSquare(
					playerSquare[0], playerSquare[1]).getForeground();
			
			bgEntities.addAll(bEntities);
			bgEntities.addAll(fEntities);

			if (player.getOrientation().equals("left") &&
					playerSquare[1] > 0 && playerSquare[0] > 0) {
				
				// Left square
				List<Entity> bEntitiesLeft = currentRoom.getSquare(
						playerSquare[0] - 1, playerSquare[1] - 1).getBackground();
				
				List<Entity> fEntitiesLeft = currentRoom.getSquare(
						playerSquare[0] - 1, playerSquare[1] - 1).getForeground();
				
				bgEntities.addAll(bEntitiesLeft);
				bgEntities.addAll(fEntitiesLeft);
			}

			if (player.getOrientation().equals("right") &&
					playerSquare[1] < 9 && playerSquare[0] > 0) {
			
				// Right square
				List<Entity> bEntitiesRight = currentRoom.getSquare(
						playerSquare[0] - 1, playerSquare[1] + 1).getBackground();
				
				List<Entity> fEntitiesRight = currentRoom.getSquare(
						playerSquare[0] - 1, playerSquare[1] + 1).getForeground();
	
				bgEntities.addAll(bEntitiesRight);
				bgEntities.addAll(fEntitiesRight);
			}
			
			// Searches for wall type objects
			for (Entity bgE : bgEntities) {
				
				if (bgE.getBoundingBox() != null) {
					
					String name = bgE.getTypeOfEntity();
					if ( name.startsWith("Door") ) {
						
						// wall detected nearby
						int[] ec = bgE.getCenter();
						
						if (ec[1] < playerCenter[1]) {

							// player is close to the door
							door = bgE;
						}
					}
				}
			}
		}		
		return door;
	}
	
	/**
	 * 
	 * @return true if there is any type of floor beneath the player
	 * so he can scale down from a corner without falling
	 */
	private Entity checkFloorBeneath() {
		Entity floorPanel = null;
		
		/* Obtains the square where the center point of the player is placed */
		int[] playerCenter = player.getCenter();
		int[] playerSquare = player.getSquare(playerCenter[0], playerCenter[1]);
		
		// Checks that the square is within the room
		if (playerSquare[0] >= 0 && playerSquare[1] >= 0 &&
				playerSquare[0] <= 3 && playerSquare[1] <= 9) {
			
			/* Checks if there is a panel floor type object beneath the player */
			List<Entity> bEntities = currentRoom.getSquare(
					playerSquare[0], playerSquare[1]).getBackground();
			
			List<Entity> fEntities = currentRoom.getSquare(
					playerSquare[0], playerSquare[1]).getForeground();
			
			List<Entity> bgEntities = new LinkedList<Entity>();
			bgEntities.addAll(bEntities);
			bgEntities.addAll(fEntities);
			
			for (Entity bgE : bgEntities) {
				String name = bgE.getTypeOfEntity();
				if ( name.startsWith("FloorPanel_") ||
					name.startsWith("Corner") ||
					name.startsWith("Loose") ||
					(name.startsWith("Pillar_") && !name.contains("shadow") && !name.contains("top")) || 
					name.startsWith("Opener") || name.startsWith("Closer") || 
					name.startsWith("SpikeFloor")){
					
					floorPanel = bgE;
				}
			}
		}
			
		return floorPanel;
	}
	
	public int[] getSquareCenter(int i, int j) {
		
		/* Calculates the center of the square */
		int px = 64 + j * 64;
		int py = (int)(6 + 63 + (i-1) * 126);
		
		return new int[]{px, py};
	}
	
	public void updateFallingFloor(long elapsedTime){
		
		List<LooseFloor> toBeDeleted = new LinkedList<LooseFloor>();
		
		for(LooseFloor loose: falling_floor){
			
			if(!loose.isBroken()){
				loose.updateReal(elapsedTime);
				
				if(checkLooseCollision(loose)){
					/* Check collision */

					loose.setBroken();
					toBeDeleted.add(loose);
					Room looseRoom = currentLevel.getRoom(loose.getRoom1(), loose.getRoom2());
					looseRoom.deleteEntityBackground(loose);
					List<Entity> newEntities = new LinkedList<Entity>();
					
					/* Put broken */
					int[] looseCenter = loose.getCenter();
					int[] looseSquare = loose.getSquare(looseCenter[0], looseCenter[1]);
					System.out.println("SE VA A ROMPEEEER " + looseSquare[0] + " - " + looseSquare[1]);
					int px = 64 + looseSquare[1] * 64;
					int py = (int)(6 + looseSquare[0] * 126);
					newEntities.add(new FloorPanel(px,py,0,-6,loader,"broken_left"));
					px = 64 + (looseSquare[1]+1) * 64;
					py = (int)(6 + looseSquare[0] * 126);
					System.out.println("ROTIIIISIMA " + px + " - " + py);
					newEntities.add(new FloorPanel(px,py,-12,-2,loader,"broken_right"));
					currentLevel.getRoom(loose.getRoom1(), loose.getRoom2()).addBackground(newEntities);
					
				} else{
					/* Didnt collided */
					
					if(loose.isLastFrameMoving()){
						
						//System.out.println("AHORA CREARIAMOS ESQUINA");
						
						/* Create corners */
						Entity newCorner = returnCorner(currentRoom.getSquare(loose.getRow(), loose.getCol()));
						Entity newCornerRight = returnCorner(currentRoom.getSquare(loose.getRow(), loose.getCol() + 1));
						if(newCorner == null && newCornerRight == null){
							//Si no hay ninguna esquina -> mirar que haya pared
							
							//Pared izquierda
							newCorner = currentRoom.returnNamedEntityBackground("Wall_face_stack_main", currentRoom.getSquare(loose.getRow(), loose.getCol()));
							
							//Pared derecha
							newCornerRight = currentRoom.returnNamedEntityForeground("Wall_left_stack_main", currentRoom.getSquare(loose.getRow(), loose.getCol()+1));
							
							
							if(newCorner == null && newCornerRight == null){
								
								System.out.println("CASO 1 - NO ESQUINAS NO PARED");
								
								//No hay pared
								newCorner = new Corner(getPX(loose.getCol()),getPY(loose.getRow()),-12,-2,loader,"normal_right");
								currentRoom.addToBackground(newCorner, currentRoom.getSquare(loose.getRow(), loose.getCol()));
								newCorner = new Corner(getPX(loose.getCol()+1),getPY(loose.getRow()),0,-6,loader,"normal_left");
								currentRoom.addToBackground(newCorner, currentRoom.getSquare(loose.getRow(), loose.getCol()+1));
								
								/* Comprobar si es en la ultima fila => eliminar base y crear corners en el piso de abajo tambien */
								if(loose.getRow() == 3){
									Room newRoom = currentLevel.getRoom(currentRoom.getRow() + 2, currentRoom.getCol() + 1);
									Square underSquare = newRoom.getSquare(0, loose.getCol());
									Entity looseUnder = newRoom.returnNamedEntityBackground("LooseFloor", underSquare);
									newRoom.deleteEntityBackground(looseUnder, underSquare);
									looseUnder = newRoom.returnNamedEntityBackground("FloorPanel_normal_left", underSquare);
									newRoom.deleteEntityBackground(looseUnder, underSquare);
									newCorner = new Corner(getPX(loose.getCol()),getPY(0),-12,-2,loader,"normal_right");
									newRoom.addToBackground(newCorner, underSquare);
									newCorner = new Corner(getPX(loose.getCol()+1),getPY(0),0,-6,loader,"normal_left");
									newRoom.addToBackground(newCorner, newRoom.getSquare(0,loose.getCol()+1));
								}
							} else if(newCorner != null && newCornerRight == null){
								
								System.out.println("CASO 2 - NO ESQUINAS PARED IZQUIERDA");
								
								//Hay pared izquierda y no derecha -> Crear solo esquina a la derecha
								newCorner = new Corner(getPX(loose.getCol()+1),getPY(loose.getRow()),0,-6,loader,"normal_left");
								currentRoom.addToBackground(newCorner, currentRoom.getSquare(loose.getRow(), loose.getCol()+1));
								
								/* Comprobar si es en la ultima fila => eliminar base y crear corners en el piso de abajo tambien */
								if(loose.getRow() == 3){
									Room newRoom = currentLevel.getRoom(currentRoom.getRow() + 2, currentRoom.getCol() + 1);
									Square underSquare = newRoom.getSquare(0, loose.getCol());
									Entity looseUnder = newRoom.returnNamedEntityBackground("LooseFloor", underSquare);
									newRoom.deleteEntityBackground(looseUnder, underSquare);
									looseUnder = newRoom.returnNamedEntityBackground("FloorPanel_normal_left", underSquare);
									newRoom.deleteEntityBackground(looseUnder, underSquare);
									newCorner = new Corner(getPX(loose.getCol()+1),getPY(0),0,-6,loader,"normal_left");
									newRoom.addToBackground(newCorner, newRoom.getSquare(0,loose.getCol()+1));
								}
							} else if(newCorner == null && newCornerRight != null){
								
								System.out.println("CASO 3 - NO ESQUINAS PARED DERECHA");
								
								//Hay pared en la derecha y no en la izquierda -> crear solo esquina a la izquierda
								newCorner = new Corner(getPX(loose.getCol()),getPY(loose.getRow()),-12,-2,loader,"normal_right");
								currentRoom.addToBackground(newCorner, currentRoom.getSquare(loose.getRow(), loose.getCol()));
								if(loose.getRow() == 3){
									Room newRoom = currentLevel.getRoom(currentRoom.getRow() + 2, currentRoom.getCol() + 1);
									Square underSquare = newRoom.getSquare(0, loose.getCol());
									Entity looseUnder = newRoom.returnNamedEntityBackground("LooseFloor", underSquare);
									newRoom.deleteEntityBackground(looseUnder, underSquare);
									looseUnder = newRoom.returnNamedEntityBackground("FloorPanel_normal_left", underSquare);
									newRoom.deleteEntityBackground(looseUnder, underSquare);
									newCorner = new Corner(getPX(loose.getCol()),getPY(0),-12,-2,loader,"normal_right");
									newRoom.addToBackground(newCorner, underSquare);
								}
							}
							
							//Si no hay ninguna esquina -> se añade una derecha en la casilla actual y una izquierda en la casilla de la derecha
							
						} else if(newCorner != null && newCorner.getTypeOfEntity().contains("left")){
							//Si hay esquina con nombre left -> se quita de la casilla actual y se mete una left en la casilla derecha salvo que haya pared
							currentRoom.deleteEntityBackground(newCorner, currentRoom.getSquare(loose.getRow(), loose.getCol()));
							
							//Pared derecha
							newCornerRight = currentRoom.returnNamedEntityForeground("Wall_left_stack_main", currentRoom.getSquare(loose.getRow(), loose.getCol()+1));
							
							if(newCornerRight == null){
								System.out.println("CASO 4 - ESQUINA IZQUIERDA NO PARED DERECHA");
								newCorner = new Corner(getPX(loose.getCol()+1),getPY(loose.getRow()),0,-6,loader,"normal_left");
								currentRoom.addToBackground(newCorner, currentRoom.getSquare(loose.getRow(), loose.getCol()+1));
							} else{
								System.out.println("CASO 5 - ESQUINA IZQUIERDA PARED DERECHA");
								
								// makes the player fall if he is in the same square as the falling loose floor
								int looseSquareX = loose.getRow();
								int looseSquareY = loose.getCol();
								int[] newPlayerCenter = player.getCenter();
								int[] newPlayerSquare = player.getSquare(newPlayerCenter[0], newPlayerCenter[1]);
								if (newPlayerSquare[0] == looseSquareX &&
										newPlayerSquare[1] == looseSquareY) {
									player.setStraightFall(true);
									player.fall();
								}
							}
							
							/* Comprobar si es en la ultima fila => eliminar base y esquina izquierda, y colocar esquina izquierda en la siguiente */
							if(loose.getRow() == 3){
								Room newRoom = currentLevel.getRoom(currentRoom.getRow() + 2, currentRoom.getCol() + 1);
								Square underSquare = newRoom.getSquare(0, loose.getCol());
								Entity looseUnder = newRoom.returnNamedEntityBackground("Loose", underSquare);
								newRoom.deleteEntityBackground(looseUnder, underSquare);
								looseUnder = newRoom.returnNamedEntityBackground("FloorPanel_normal_left", underSquare);
								newRoom.deleteEntityBackground(looseUnder, underSquare);
								looseUnder = newRoom.returnNamedEntityBackground("Corner", underSquare);
								newRoom.deleteEntityBackground(looseUnder, underSquare);
								
								if(newCornerRight == null){
									newCorner = new Corner(getPX(loose.getCol()+1),getPY(0),0,-6,loader,"normal_left");
									newRoom.addToBackground(newCorner, newRoom.getSquare(0,loose.getCol()+1));
								}
							}
						} else if(newCornerRight.getTypeOfEntity().contains("right")){
							//Si hay una esquina con nombre right -> se quita de la casilla actual, y se mete una right en la casilla de la izq salvo que haya pared
							currentRoom.deleteEntityBackground(newCornerRight, currentRoom.getSquare(loose.getRow(), loose.getCol()+1));
							
							//Pared izquierda
							newCorner = currentRoom.returnNamedEntityBackground("Wall_face_stack_main", currentRoom.getSquare(loose.getRow(), loose.getCol()));
							
							if(newCorner == null){
								System.out.println("CASO 6 - ESQUINA DERECHA NO PARED IZQUIERDA");
								newCornerRight = new Corner(getPX(loose.getCol()),getPY(loose.getRow()),-12,-2,loader,"normal_right");
								currentRoom.addToBackground(newCornerRight, currentRoom.getSquare(loose.getRow(), loose.getCol()));
							} else{
								System.out.println("CASO 7 - ESQUINA DERECHA PARED IZQUIERDA");
								
								// makes the player fall if he is in the same square as the falling loose floor
								int looseSquareX = loose.getRow();
								int looseSquareY = loose.getCol();
								int[] newPlayerCenter = player.getCenter();
								int[] newPlayerSquare = player.getSquare(newPlayerCenter[0], newPlayerCenter[1]);
								if (newPlayerSquare[0] == looseSquareX &&
										newPlayerSquare[1] == looseSquareY) {
									player.setStraightFall(true);
									player.fall();
								}
							}
						
							/* Comprobar si es en la ultima fila => eliminar base y crear corners en el piso de abajo tambien */
							if(loose.getRow() == 3){
								Room newRoom = currentLevel.getRoom(currentRoom.getRow() + 2, currentRoom.getCol() + 1);
								Square underSquare = newRoom.getSquare(0, loose.getCol());
								Entity looseUnder = newRoom.returnNamedEntityBackground("Loose", underSquare);
								newRoom.deleteEntityBackground(looseUnder, underSquare);
								looseUnder = newRoom.returnNamedEntityBackground("FloorPanel_normal_left", underSquare);
								newRoom.deleteEntityBackground(looseUnder, underSquare);
								
								if(newCorner == null){
									newCornerRight = new Corner(getPX(loose.getCol()),getPY(0),-12,-2,loader,"normal_right");
									newRoom.addToBackground(newCornerRight, underSquare);
								}
							}
						}
						
					}
					
					if(loose.getY() > (400 + loose.getCurrentAnimation().getImage().getHeight())){
						System.out.println("CAMBIO DE ROOOOOM");
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
		toBeDeleted = new LinkedList<LooseFloor>();
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
			List<Entity> bEntities = looseRoom.getSquare(
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
		
		List<Entity> bg = looseSquare.getBackground();
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
	
	public void checkPlayerSquare(){
		int[] playerCenter = player.getCenter();
		int[] playerSquare = player.getSquare(playerCenter[0], playerCenter[1]);
		
		//System.out.println("Casilla " + playerSquare[0] + " - " +  playerSquare[1]);
		
		if (!(playerSquare[0] > 0 && playerSquare[1] >= 0 &&
				playerSquare[0] <= 3 && playerSquare[1] <= 9)) {
			
			if(playerSquare[0] <= 0){

				/* Arriba */
				if(player.isClimbing()){
					System.out.println("arriba");
					System.out.println("NEW ROOM : " + currentRoom.getRow() + " - " + (currentRoom.getCol() + 1));
					Room newRoom = currentLevel.getRoom(currentRoom.getRow(), currentRoom.getCol() + 1);
					Entity actualCorner = player.getCornerToClimb();
					if(actualCorner == null){
						actualCorner = player.getCornerToClimbDown();
					}
					int yDistanceCorner = actualCorner.getCenter()[1] - player.getY();
					List<Entity> entities = newRoom.getSquare(3, playerSquare[1]).getBackground();
					for(Entity e : entities){
						if(e.getTypeOfEntity().startsWith("Corner")){
							System.out.println("found corner arriba");
							player.setY(e.getCenter()[1] - yDistanceCorner);
							player.setCornerToClimbDown(null);
							player.setCornerToClimb(e);
						}
					}
					if(enemy != null){
						enemy.setPlayer(false, player);
					}
					currentRoom.deleteCharacter(player);
					enemy = (Enemy)newRoom.getGuard();
					newRoom.addCharacter(player);
					if(enemy != null){
						if(!enemy.isDead()){
							enemy.setPlayer(true, player);
							if(player.hasSword()){
								player.isEnemySaw(true);
							}
						}
					}
					currentRoom = newRoom;
				}
			} else if(playerSquare[0] > 3){

				/* Abajo */
				Room newRoom = currentLevel.getRoom(currentRoom.getRow() + 2, currentRoom.getCol() + 1);
				if(player.isClimbing()){
					System.out.println("abajo");
					Entity actualCorner = player.getCornerToClimbDown();
					if(actualCorner == null){
						actualCorner = player.getCornerToClimb();
					}
					int yDistanceCorner = actualCorner.getCenter()[1] - player.getY();
					List<Entity> entities = newRoom.getSquare(0, playerSquare[1]).getBackground();
					for(Entity e : entities){
						if(e.getTypeOfEntity().startsWith("Corner")){
							System.out.println("found corner abajo");
							player.setY(e.getCenter()[1] - yDistanceCorner);
							player.setCornerToClimb(null);
							player.setCornerToClimbDown(e);
						}
					}
					currentRoom.deleteCharacter(player);
					if(enemy != null){
						enemy.setPlayer(false, player);
					}
					enemy = (Enemy)newRoom.getGuard();
					newRoom.addCharacter(player);
					if(enemy != null){
						if(!enemy.isDead()){
							enemy.setPlayer(true, player);
							if(player.hasSword()){
								player.isEnemySaw(true);
							}
						}
					}
					currentRoom = newRoom;
				} else{
					currentRoom.deleteCharacter(player);
					newRoom.addCharacter(player);
					player.setY(40);
					currentRoom = newRoom;
				}
			} else if(playerSquare[1] < 0){

				/* Izquierda */
				currentRoom.deleteCharacter(player);
				Room newRoom = currentLevel.getRoom(currentRoom.getRow() + 1, currentRoom.getCol());
				if(enemy != null){
					enemy.setPlayer(false, player);
				}
				enemy = (Enemy)newRoom.getGuard();
				newRoom.addCharacter(player);
				if(enemy != null){
					if(!enemy.isDead()){
						enemy.setPlayer(true, player);
						if(player.hasSword()){
							player.isEnemySaw(true);
						}
					}
				}
				player.setX(620);
				currentRoom = newRoom;
			} else if(playerSquare[1] > 9){

				/* Derecha */
				currentRoom.deleteCharacter(player);
				Room newRoom = currentLevel.getRoom(currentRoom.getRow() + 1, currentRoom.getCol() + 2);
				if(enemy != null){
					enemy.setPlayer(false, player);
				}
				enemy = (Enemy)newRoom.getGuard();
				newRoom.addCharacter(player);
				if(enemy != null){
					if(!enemy.isDead()){
						enemy.setPlayer(true, player);
						if(player.hasSword()){
							player.isEnemySaw(true);
						}
					}
				}
				player.setX(20);
				currentRoom = newRoom;
			}
		}
	}
	
	public void checkLoosesToShake(){
		int row = player.getSquare()[0];
		if(row < 4){
			for(int i = 0; i < 10; i++){
				List<Entity> bg = currentRoom.getSquare(row, i).getBackground();
				for(Entity e : bg){
					if(e.getTypeOfEntity().startsWith("LooseFloor")){
						((LooseFloor)e).justShakeItBaby();
					}
				}
			}
		}
	}
	
	public boolean checkFloorSides(){
		boolean isFloor = false;
		/* Comprobar que la ultima casilla de la hab izquierda tiene un suelo si estamos en la primera */
		if(player.getSquare()[1] == 0 && player.getOrientation().equals("left")){
			
			Room leftRoom = currentLevel.getRoom(currentRoom.getRow() + 1, currentRoom.getCol());
			List<Entity> bg = leftRoom.getSquare(player.getSquare()[0],9).getBackground();
			for(Entity e : bg){
				System.out.println("BORDES");
				String name = e.getTypeOfEntity();
				if ( name.startsWith("FloorPanel_") ||
					(name.startsWith("Pillar_") && !name.contains("shadow") && !name.contains("top")) || 
					name.startsWith("Opener") || name.startsWith("Closer") || 
					name.startsWith("SpikeFloor")){
					isFloor = true;
				}
			}
		} else if(player.getSquare()[1] == 0 && player.getOrientation().equals("right")){
			
			Room leftRoom = currentLevel.getRoom(currentRoom.getRow() + 1, currentRoom.getCol());
			List<Entity> bg = leftRoom.getSquare(player.getSquare()[0],9).getBackground();
			for(Entity e : bg){
				System.out.println("BORDES");
				String name = e.getTypeOfEntity();
				if ( name.startsWith("FloorPanel_") ||
					(name.startsWith("Pillar_") && !name.contains("shadow") && !name.contains("top")) || 
					name.startsWith("Opener") || name.startsWith("Closer") || 
					name.startsWith("SpikeFloor")){
					isFloor = true;
				}
			}
		} else if(player.getSquare()[1] == 9 && player.getOrientation().equals("right")){
			
			Room leftRoom = currentLevel.getRoom(currentRoom.getRow() + 1, currentRoom.getCol() + 2);
			List<Entity> bg = leftRoom.getSquare(player.getSquare()[0],9).getBackground();
			for(Entity e : bg){
				System.out.println("BORDES");
				String name = e.getTypeOfEntity();
				if ( name.startsWith("FloorPanel_") ||
					(name.startsWith("Pillar_") && !name.contains("shadow") && !name.contains("top")) || 
					name.startsWith("Opener") || name.startsWith("Closer") || 
					name.startsWith("SpikeFloor")){
					isFloor = true;
				}
			}
		}
		return isFloor;
	}
	
	/**
	 * 
	 * @return true if there is a sword in front of the player
	 * that he can pick
	 */
	private Entity checkSword() {
		Entity sword = null;
		int swordGap = 50;
		
		/* Obtains the square where the center point of the player is placed */
		int[] playerCenter = player.getCenter();
		int[] playerSquare = player.getSquare(playerCenter[0], playerCenter[1]);
		
		// Checks that the square is within the room
		if (playerSquare[0] >= 0 && playerSquare[1] >= 0 &&
				playerSquare[0] <= 3 && playerSquare[1] <= 9) {
			
			if (player.getOrientation().equals("left")) {
				
				/* Checks if there is a potion type object in current square */
				List<Entity> bgEntities = new LinkedList<Entity>();
				
				List<Entity> fEntities = currentRoom.getSquare(
						playerSquare[0], playerSquare[1]).getForeground();
				
				List<Entity> fEntities2;
				
				if (playerSquare[1] > 0) {
					fEntities2 = currentRoom.getSquare(
							playerSquare[0], playerSquare[1] - 1).getForeground();
					bgEntities.addAll(fEntities2);
				}
				
				bgEntities.addAll(fEntities);
	
				// Searches for potion type objects
				for (Entity bgE : bgEntities) {
					
					String name = bgE.getTypeOfEntity();
					int[] ec = bgE.getCenter();
					if ( name.startsWith("SwordFloor") &&
						ec[0] < playerCenter[0] &&
						(Math.abs(ec[0] - playerCenter[0]) < swordGap) ) {
						
						// player is close to the potion
						sword = bgE;
					}
				}
			}
			else if (player.getOrientation().equals("right")) {
				/* Checks if there is a potion type object in current square */
				List<Entity> bgEntities = new LinkedList<Entity>();
				
				List<Entity> fEntities = currentRoom.getSquare(
						playerSquare[0], playerSquare[1]).getForeground();

				List<Entity> fEntities2;
				if (playerSquare[1] < 9) {
					fEntities2 = currentRoom.getSquare(
							playerSquare[0], playerSquare[1] + 1).getForeground();
					bgEntities.addAll(fEntities2);
				}
				
				bgEntities.addAll(fEntities);
	
				// Searches for potion type objects
				for (Entity bgE : bgEntities) {
					
					String name = bgE.getTypeOfEntity();
					int[] ec = bgE.getCenter();
					if ( name.startsWith("Sword") &&
						ec[0] > playerCenter[0] &&
						(Math.abs(ec[0] - playerCenter[0]) < swordGap) ) {
						
						// player is close to the potion
						sword = bgE;
					}
				}
			}		
		}
		return sword;
	}
	
	
	/**
	 * 
	 * @return deletes the potion that the player is drinking
	 */
	private Entity deleteSword(Entity sword) {
		
		/* Obtains the square where the center point of the player is placed */
		int[] playerCenter = player.getCenter();
		int[] playerSquare = player.getSquare(playerCenter[0], playerCenter[1]);
		
		// Checks that the square is within the room
		if (playerSquare[0] >= 0 && playerSquare[1] >= 0 &&
				playerSquare[0] <= 3 && playerSquare[1] <= 9) {
			
			/* Checks if there is a potion type object in current square */
			List<Entity> bgEntities = new LinkedList<Entity>();
			Square currentSquare = currentRoom.getSquare(
					playerSquare[0], playerSquare[1]);
			List<Entity> fEntities = currentSquare.getForeground();
			bgEntities.addAll(fEntities);
			
			/* Checks if there is a potion type object in left square */
			List<Entity> bgEntitiesLeft = new LinkedList<Entity>();
			Square currentSquareLeft = currentRoom.getSquare(
					playerSquare[0], playerSquare[1] - 1);
			List<Entity> fEntitiesLeft = currentSquareLeft.getForeground();
			bgEntitiesLeft.addAll(fEntitiesLeft);
			
			/* Checks if there is a potion type object in right square */
			List<Entity> bgEntitiesRight = new LinkedList<Entity>();
			Square currentSquareRight = currentRoom.getSquare(
					playerSquare[0], playerSquare[1] + 1);
			List<Entity> fEntitiesRight = currentSquareRight.getForeground();
			bgEntitiesRight.addAll(fEntitiesRight);
			
			if (player.getOrientation().equals("left")) {
				
				// Searches for potion type objects
				for (Entity bgE : bgEntities) {
					
					String name = bgE.getTypeOfEntity();
					if ( name.startsWith("SwordFloor")) {
						
						// player is close to the potion
						currentRoom.deleteEntityForeground(bgE, currentSquare);
					}
				}
				
				// Searches for potion type objects
				for (Entity bgE : bgEntitiesLeft) {
					
					String name = bgE.getTypeOfEntity();
					if ( name.startsWith("SwordFloor")) {
						
						// player is close to the potion
						currentRoom.deleteEntityForeground(bgE, currentSquareLeft);
					}
				}
			}
			else if (player.getOrientation().equals("right")) {

				// Searches for potion type objects
				for (Entity bgE : bgEntities) {
					
					String name = bgE.getTypeOfEntity();
					if ( name.startsWith("SwordFloor")) {
						
						// player is close to the potion
						currentRoom.deleteEntityForeground(sword, currentSquare);
					}
				}
				
				// Searches for potion type objects
				for (Entity bgE : bgEntitiesRight) {
					
					String name = bgE.getTypeOfEntity();
					if ( name.startsWith("SwordFloor")) {
						
						// player is close to the potion
						currentRoom.deleteEntityForeground(sword, currentSquareRight);
					}
				}
			}
		}
		return sword;
	}
	
	
	
	/**
	 * 
	 * @return true if there is a potion in front of the player
	 * that he can drink
	 */
	private Entity checkPotion() {
		Entity potion = null;
		int potionGap = 50;
		
		/* Obtains the square where the center point of the player is placed */
		int[] playerCenter = player.getCenter();
		int[] playerSquare = player.getSquare(playerCenter[0], playerCenter[1]);
		
		// Checks that the square is within the room
		if (playerSquare[0] >= 0 && playerSquare[1] >= 0 &&
				playerSquare[0] <= 3 && playerSquare[1] <= 9) {
			
			if (player.getOrientation().equals("left")) {
				
				/* Checks if there is a potion type object in current square */
				List<Entity> bgEntities = new LinkedList<Entity>();
				
				List<Entity> fEntities = currentRoom.getSquare(
						playerSquare[0], playerSquare[1]).getForeground();
				
				List<Entity> fEntities2;
				
				if (playerSquare[1] > 0) {
					fEntities2 = currentRoom.getSquare(
							playerSquare[0], playerSquare[1] - 1).getForeground();
					bgEntities.addAll(fEntities2);
				}
				
				bgEntities.addAll(fEntities);
	
				// Searches for potion type objects
				for (Entity bgE : bgEntities) {
					
					String name = bgE.getTypeOfEntity();
					int[] ec = bgE.getCenter();
					if ( name.startsWith("Potion_") &&
						ec[0] < playerCenter[0] &&
						(Math.abs(ec[0] - playerCenter[0]) < potionGap) ) {
						
						// player is close to the potion
						potion = bgE;
					}
				}
			}
			else if (player.getOrientation().equals("right")) {
				/* Checks if there is a potion type object in current square */
				List<Entity> bgEntities = new LinkedList<Entity>();
				
				List<Entity> fEntities = currentRoom.getSquare(
						playerSquare[0], playerSquare[1]).getForeground();

				List<Entity> fEntities2;
				if (playerSquare[1] < 9) {
					fEntities2 = currentRoom.getSquare(
							playerSquare[0], playerSquare[1] + 1).getForeground();
					bgEntities.addAll(fEntities2);
				}
				
				bgEntities.addAll(fEntities);
	
				// Searches for potion type objects
				for (Entity bgE : bgEntities) {
					
					String name = bgE.getTypeOfEntity();
					int[] ec = bgE.getCenter();
					if ( name.startsWith("Potion_") &&
						ec[0] > playerCenter[0] &&
						(Math.abs(ec[0] - playerCenter[0]) < potionGap) ) {
						
						// player is close to the potion
						potion = bgE;
					}
				}
			}		
		}
		return potion;
	}
	
	/**
	 * 
	 * @return deletes the potion that the player is drinking
	 */
	private Entity deletePotion(Entity potion) {
		
		/* Obtains the square where the center point of the player is placed */
		int[] playerCenter = player.getCenter();
		int[] playerSquare = player.getSquare(playerCenter[0], playerCenter[1]);
		
		// Checks that the square is within the room
		if (playerSquare[0] >= 0 && playerSquare[1] >= 0 &&
				playerSquare[0] <= 3 && playerSquare[1] <= 9) {
			
			/* Checks if there is a potion type object in current square */
			List<Entity> bgEntities = new LinkedList<Entity>();
			Square currentSquare = currentRoom.getSquare(
					playerSquare[0], playerSquare[1]);
			List<Entity> fEntities = currentSquare.getForeground();
			bgEntities.addAll(fEntities);
			
			/* Checks if there is a potion type object in left square */
			List<Entity> bgEntitiesLeft = new LinkedList<Entity>();
			Square currentSquareLeft = currentRoom.getSquare(
					playerSquare[0], playerSquare[1] - 1);
			List<Entity> fEntitiesLeft = currentSquareLeft.getForeground();
			bgEntitiesLeft.addAll(fEntitiesLeft);
			
			/* Checks if there is a potion type object in right square */
			List<Entity> bgEntitiesRight = new LinkedList<Entity>();
			Square currentSquareRight = currentRoom.getSquare(
					playerSquare[0], playerSquare[1] + 1);
			List<Entity> fEntitiesRight = currentSquareRight.getForeground();
			bgEntitiesRight.addAll(fEntitiesRight);
			
			if (player.getOrientation().equals("left")) {
				
				// Searches for potion type objects
				for (Entity bgE : bgEntities) {
					
					String name = bgE.getTypeOfEntity();
					if ( name.startsWith("Potion_")) {
						
						// player is close to the potion
						currentRoom.deleteEntityForeground(bgE, currentSquare);
					}
				}
				
				// Searches for potion type objects
				for (Entity bgE : bgEntitiesLeft) {
					
					String name = bgE.getTypeOfEntity();
					if ( name.startsWith("Potion_")) {
						
						// player is close to the potion
						currentRoom.deleteEntityForeground(bgE, currentSquareLeft);
					}
				}
			}
			else if (player.getOrientation().equals("right")) {

				// Searches for potion type objects
				for (Entity bgE : bgEntities) {
					
					String name = bgE.getTypeOfEntity();
					if ( name.startsWith("Potion_")) {
						
						// player is close to the potion
						currentRoom.deleteEntityForeground(potion, currentSquare);
					}
				}
				
				// Searches for potion type objects
				for (Entity bgE : bgEntitiesRight) {
					
					String name = bgE.getTypeOfEntity();
					if ( name.startsWith("Potion_")) {
						
						// player is close to the potion
						currentRoom.deleteEntityForeground(potion, currentSquareRight);
					}
				}
			}
		}
		return potion;
	}
	
	public void checkPlayerFinnish(){
		
		/* Obtains the square where the center point of the player is placed */
		int[] playerCenter = player.getCenter();
		int[] playerSquare = player.getSquare(playerCenter[0], playerCenter[1]);
		
		// Checks that the square is within the room
		if (playerSquare[0] >= 0 && playerSquare[1] >= 0 &&
				playerSquare[0] <= 3 && playerSquare[1] <= 9) {
			
			/* Checks if there is a panel floor type object beneath the player */
			Entity door = currentRoom.returnNamedEntityBackground("Door_final", currentRoom.getSquare(playerSquare[0], playerSquare[1]));
			if(door != null && ((Door)door).isOpenedFinal()){
				player.finalDoorOpened(true, door.getXCentre() - 29, door.getY() + 30);
			} else{
				player.finalDoorOpened(false, 0,0);
			}
		}
	}
}
