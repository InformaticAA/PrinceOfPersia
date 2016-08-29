package game;
/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;

import data.Room;
import entities.Door;
import entities.Entity;
import entities.FloorPanel;
import entities.LooseFloor;
import entities.Player;
import input.Key;
import states.LevelState;
import states.MenuState;

/**
 * See: http://blog.xoppa.com/basic-3d-using-libgdx-2/
 * @author Xoppa
 */
public class Game3D implements ApplicationListener {
	
	// TODO: todavia en test (pero mola :D)
	private boolean FULL_LEVEL = false;
	private boolean FREE_CAM = false;
	private boolean DEBUG = true;
	
	private final int SCALE = 10;
	private final int UI_HEIGHT = 16; 		// 16 = sin espacios entre habitaciones (se resta al offset)
	private final long TARGET_TIME = 1000/60;
	private final float CAM_DISTANCE = 50f;
	private final float DEPTH = 10f;
	
	private final int NUM_ROWS = 4;
	private final int NUM_COLS = 10;
	
	// variables keys
	private boolean up = true;
	private boolean down = true;
	private boolean left = true;
	private boolean right = true;
	private boolean shift = true;
	private boolean space = true;
	
	// room variables
	private int currRow = 1;
	private int currCol = 7;
	private int prevRow = currRow;
	private int prevCol = currCol;
	
	public Environment lights;
	public PerspectiveCamera cam;
	public CameraInputController camController;
	public ModelBatch modelBatch;
	public SpriteBatch spriteBatch;
	public Texture texture;
	public static MenuState menu;
	public static LevelState level;
	public static ConcurrentLinkedQueue<Key> keys;
	public List<List<Hashtable<Entity,ModelInstance>>> entities;
	public List<List<Hashtable<Entity,ModelInstance>>> entitiesFullLevel;
	public Hashtable<String,Model> entityModels;
	
	private Player player;
	private List<LooseFloor> falling_floor;
	private List<Door> doors;
	private List<Entity> entitiesToBeDeleted;
	
	
	public Game3D(MenuState gameMenu, LevelState levelState){
		menu = gameMenu;
		level = levelState;
		keys = new ConcurrentLinkedQueue<Key>();
	}
	
	public static LwjglApplication main(MenuState gameMenu, LevelState levelState) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.foregroundFPS = 60;
		config.forceExit = false;
		config.resizable = false;
		config.height = Game.HEIGHT;
		config.width = Game.WIDTH;
		config.title = "Prince of Persia 3D";
		return new LwjglApplication(new Game3D(gameMenu, levelState), config);
	}

	@Override
	public void create() {
		
		initLevel();
		initEnvironment();
		initCam();
		initModels();
	}

	@Override
	public void render() {
		update();
		
		Collection<ModelInstance> objects;
		
		if (FULL_LEVEL) {
			
			// Obtain all entities from all rooms so it can render them all at once
			objects  = new LinkedList<ModelInstance>();
			for (int i = 0; i < NUM_ROWS; i++) {
				for (int j = 0; j < NUM_COLS; j++) {
					if (entitiesFullLevel.get(i).get(j) != null) {
						objects.addAll(entitiesFullLevel.get(i).get(j).values());
					}
				}
			}
		}
		else {
			objects = entities.get(currRow).get(currCol).values();
		}

		// limpia la pantalla
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
 
        // dibuja los modelos 3D
        modelBatch.begin(cam);
        modelBatch.render(objects, lights);
        modelBatch.end();
        
        // dibuja los sprites 2D
        spriteBatch.begin();

        float stateStart = 90f;
        float stateWidth = 35f;
        writeDebug("Debug mode:", Color.WHITE, 0f, 1);
        if (DEBUG) writeDebug("ON", Color.GREEN, stateStart, 1);
    	else writeDebug("OFF", Color.RED, stateStart, 1);
        writeDebug("(Press 'T' to toggle)", Color.WHITE, stateStart + stateWidth, 1);
        
        if (DEBUG) {
        	
        	// dibuja los valores de las variables de debug
        	
        	// camera mode debug
//        	stateStart = 90f;
        	writeDebug("Free camera:", Color.WHITE, 0f, 2);
        	if (FREE_CAM) writeDebug("ON", Color.GREEN, stateStart, 2);
        	else writeDebug("OFF", Color.RED, stateStart, 2);
        	writeDebug("(Press 'C' to toggle)", Color.WHITE, stateStart + stateWidth, 2);
        	
        	// level mode debug
//        	stateStart = 80f;
        	writeDebug("Full level:", Color.WHITE, 0f, 3);
        	if (FULL_LEVEL) writeDebug("ON", Color.GREEN, stateStart, 3);
        	else writeDebug("OFF", Color.RED, stateStart, 3);
        	writeDebug("(Press 'L' to toggle)", Color.WHITE, stateStart + stateWidth, 3);
        }
        
        spriteBatch.end();
	}
	
	private void writeDebug(String string, Color color, float start, int line) {
		
		// variables
		BitmapFont bmFont;
    	bmFont = new BitmapFont();
    	float charHeight = 18f;
    	float y = Game.HEIGHT - (charHeight * (line - 1) );
    	
    	// calcula la nueva posicion
    	bmFont.setColor(color);
    	bmFont.draw(spriteBatch, string, start, y);
	}
	
	public void update() {
		
		// asigna una ultima posicion a cada objeto
		Map<Entity, int[]> lastPos = new HashMap<>();
		for (int i = 0; i < NUM_ROWS; i++) {
			for (int j = 0; j < NUM_COLS; j++) {
				for (Entity e : entities.get(i).get(j).keySet()) {
					lastPos.put(e, e.getCenter());
				}
			}
		}
		
		
		
		// asigna una ultima posicion a cada objeto
		Map<Entity, int[]> lastPosFullLevel = new HashMap<>();
		for (int i = 0; i < NUM_ROWS; i++) {
			for (int j = 0; j < NUM_COLS; j++) {
				for (Entity e : entitiesFullLevel.get(i).get(j).keySet()) {
					lastPosFullLevel.put(e, e.getCenter());
				}
			}
		}
		
		
		// gestiona la entrada de teclas del usuario
		manageKeys();

		// prev room
		prevRow = currRow;
		prevCol = currCol;

		// actualiza la logica del juego 2D
		level.update(TARGET_TIME);
		currRow = level.getCurrentRoom().getRow() + 1;
		currCol = level.getCurrentRoom().getCol() + 1;
		
		checkLooses();
		checkPlayer();
		checkDoors();
		
		// actualiza cada objeto en funcion de su movimiento
		// (diferencia con la posicion anterior)
		for (int i = 0; i < NUM_ROWS; i++) {
			for (int j = 0; j < NUM_COLS; j++) {
				for (Map.Entry<Entity, ModelInstance> entry : entities.get(i).get(j).entrySet()) {
					Entity key = entry.getKey();
					ModelInstance value = entry.getValue();
		
					int[] last = lastPos.get(key);
					if (last != null) {
						float x = (float) (key.getCenter()[0] - last[0]) / SCALE;
						float y = (float) -(key.getCenter()[1] - last[1]) / SCALE;
						value.transform.translate(x,y,0);
					}
				}
			}
		}
		
		// actualiza cada objeto en funcion de su movimiento
		// (diferencia con la posicion anterior)
		for (int i = 0; i < NUM_ROWS; i++) {
			for (int j = 0; j < NUM_COLS; j++) {
				for (Map.Entry<Entity, ModelInstance> entry : entitiesFullLevel.get(i).get(j).entrySet()) {
					Entity key = entry.getKey();
					ModelInstance value = entry.getValue();
		
					int[] last = lastPosFullLevel.get(key);
					if (last != null) {
						float x = (float) (key.getCenter()[0] - last[0]) / SCALE;
						float y = (float) -(key.getCenter()[1] - last[1]) / SCALE;
						value.transform.translate(x,y,0);
					}
				}
			}
		}
		
		checkEntitiesToBeDeleted();
		
		// updates camera's position
		updateCamera();
	}
	
	public void updateCamera() {
		
		// actualiza la posicion de la camara
		if (FREE_CAM) {
			
			// modo camara libre
			// saves last position of camera
			Vector3 camMov = new Vector3(0,0,0);
			Vector3 camNewPos = cam.position;
			Vector3 lastPos = cam.position;
			float speed = 1f;
			float camX = 0;
			float camY = 0;
			float camZ = 0;
			
			/* key handling */
			if (Gdx.input.isKeyPressed(Input.Keys.A)) {
				camX = -speed;
			}
			if (Gdx.input.isKeyPressed(Input.Keys.D)) {
				camX = speed;
			}
			if (Gdx.input.isKeyPressed(Input.Keys.W)) {
				camY = speed;
			}
			if (Gdx.input.isKeyPressed(Input.Keys.S)) {
				camY = -speed;
			}
			if (Gdx.input.isKeyPressed(Input.Keys.F)) {
				camZ = -speed;
			}
			if (Gdx.input.isKeyPressed(Input.Keys.G)) {
				camZ = speed;
			}
			
			// updates camera's position
			camMov = new Vector3(camX,camY,camZ);
			camNewPos = lastPos.add(camMov);
			cam.position.set(camNewPos);
		}
		else {
			
			// modo camara fija (centrada en la habitacion actual)
			resetFreeCamera();
		}
		
		// aplica los cambios en la posicion de la camara
		cam.update();
	}
	
	private void resetCamera() {
		if (FULL_LEVEL) {
			cam.position.set( (Game.WIDTH * (currCol - 1) ) / SCALE + (Game.WIDTH/(2*SCALE)),
					(Game.HEIGHT / SCALE) - ((Game.HEIGHT * (currRow - 1) ) / SCALE - (Game.HEIGHT/(2*SCALE))),
					CAM_DISTANCE);
			cam.lookAt(	(Game.WIDTH * (currCol - 1) ) / SCALE + (Game.WIDTH/(2*SCALE)),
					(Game.HEIGHT / SCALE) - ((Game.HEIGHT * (currRow - 1) ) / SCALE - (Game.HEIGHT/(2*SCALE))),
					0);
		}
		else {
			cam.position.set(Game.WIDTH/(2*SCALE), Game.HEIGHT/(2*SCALE), 50f);
			cam.lookAt(Game.WIDTH/(2*SCALE),Game.HEIGHT/(2*SCALE),0);
		}
	}
	
	private void resetFreeCamera() {
		float z = cam.position.z;
		if (FULL_LEVEL) {
			cam.position.set( (Game.WIDTH * (currCol - 1) ) / SCALE + (Game.WIDTH/(2*SCALE)),
					(Game.HEIGHT / SCALE) - ((Game.HEIGHT * (currRow - 1) ) / SCALE - (Game.HEIGHT/(2*SCALE))),
					z);
			cam.lookAt(	(Game.WIDTH * (currCol - 1) ) / SCALE + (Game.WIDTH/(2*SCALE)),
					(Game.HEIGHT / SCALE) - ((Game.HEIGHT * (currRow - 1) ) / SCALE - (Game.HEIGHT/(2*SCALE))),
					0);
		}
		else {
			cam.position.set(Game.WIDTH/(2*SCALE), Game.HEIGHT/(2*SCALE), z);
			cam.lookAt(Game.WIDTH/(2*SCALE),Game.HEIGHT/(2*SCALE),0);
		}
	}
	
	private void manageKeys() {
		
		// up
		if (Gdx.input.isKeyPressed(Input.Keys.UP)
				|| !up) {
			if(Gdx.input.isKeyPressed(Input.Keys.UP)){
				keys.add(new Key(true, KeyEvent.VK_UP));
				up = true;
			}
		} else if (up) {
			keys.add(new Key(false, KeyEvent.VK_UP));
			up = false;
		}
		
		// down
		if (Gdx.input.isKeyPressed(Input.Keys.DOWN)
				|| !down) {
			if(Gdx.input.isKeyPressed(Input.Keys.DOWN)){
				keys.add(new Key(true, KeyEvent.VK_DOWN));
				down = true;
			}
		} else if (down) {
			keys.add(new Key(false, KeyEvent.VK_DOWN));
			down = false;
		}
		
		// left
		if (Gdx.input.isKeyPressed(Input.Keys.LEFT)
				|| !left) {
			if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
				keys.add(new Key(true, KeyEvent.VK_LEFT));
				left = true;
			}
		} else if (left) {
			keys.add(new Key(false, KeyEvent.VK_LEFT));
			left = false;
		}
		
		// right
		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)
				|| !right) {
			if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
				keys.add(new Key(true, KeyEvent.VK_RIGHT));
				right = true;
			}
			
		} else if (right) {
			keys.add(new Key(false, KeyEvent.VK_RIGHT));
			right = false;
		}
		
		// shift
		if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)
				|| !shift) {
			if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)){
				keys.add(new Key(true, KeyEvent.VK_SHIFT));
				shift = true;
			}
		} else if (shift) {
			keys.add(new Key(false, KeyEvent.VK_SHIFT));
			shift = false;
		}
		
		// space
		if (Gdx.input.isKeyPressed(Input.Keys.SPACE)
				|| !space) {
			if(Gdx.input.isKeyPressed(Input.Keys.SPACE)){
				keys.add(new Key(true, KeyEvent.VK_SPACE));
				space = true;
			}
		} else if (space) {
			keys.add(new Key(false, KeyEvent.VK_SPACE));
			space = false;
		}
		
		// toggles debug mode
		if (Gdx.input.isKeyJustPressed(Input.Keys.T)) {
			if (DEBUG) {
				DEBUG = false;
			}
			else {
				DEBUG = true;
			}
		}
		
		// toggle full_level mode
		if (Gdx.input.isKeyJustPressed(Input.Keys.L)) {
			if (FULL_LEVEL) {
				FULL_LEVEL = false;
			}
			else {
				FULL_LEVEL = true;
			}
			
			if (FREE_CAM) {
				resetFreeCamera();
			}
		}
		
		// toggle free camera mode
		if (Gdx.input.isKeyJustPressed(Input.Keys.C)) {
			if (FREE_CAM) {
				FREE_CAM = false;
			}
			else {
				FREE_CAM = true;
				resetFreeCamera();
			}
		}
		
		// resets camera position to the current room center
		// it does not fix it
		if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
			resetCamera();
		}
	}
	
	@Override
	public void dispose() {
		modelBatch.dispose();
		for(String m : entityModels.keySet()){
			entityModels.get(m).dispose();
		}
		menu.set3d(false);
	}

	@Override
	public void pause() {
		
	}

	@Override
	public void resume() {
	}

	@Override
	public void resize(int arg0, int arg1) {
		
	}
	
	public void initLevel(){
		level.setKeyList(keys);
		level.init();
		this.player = level.getPlayer();
		this.doors = level.getDoors();
		this.falling_floor = level.getFalling_floor();
		this.entitiesToBeDeleted = level.getEntitiesToBeDeleted();
	}
	
	public void initEnvironment(){
		lights = new Environment();
		lights.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		lights.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
	}
	
	public void initCam(){
		cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		// inicializa la posicion de la camara
		resetCamera();
		
		cam.near = 1f;
		cam.far = 500f;
		cam.update();
		camController = new CameraInputController(cam);
		
		// camera custom settings
		camController.scrollFactor = 3f;
		
		Gdx.input.setInputProcessor(camController);
	}
	
	public void initModels(){
		modelBatch = new ModelBatch();
		spriteBatch = new SpriteBatch();
        ModelBuilder modelBuilder = new ModelBuilder();
        entityModels = new Hashtable<String, Model>();
        entities = new LinkedList<List<Hashtable<Entity, ModelInstance>>>();
        entitiesFullLevel = new LinkedList<List<Hashtable<Entity, ModelInstance>>>();
        
        // inicializa el array de entidades
        for (int i = 0; i < NUM_ROWS; i++) {
        	
        	entities.add(i, new LinkedList<Hashtable<Entity, ModelInstance>>());
        	entitiesFullLevel.add(i, new LinkedList<Hashtable<Entity, ModelInstance>>());
        	
        	for (int j = 0; j < NUM_COLS; j++) {
        		entities.get(i).add(j, new Hashtable<Entity, ModelInstance>());
        		entitiesFullLevel.get(i).add(j, new Hashtable<Entity, ModelInstance>());
        	}
    	}
        
        // Crear modelos para cada entidad y asociarlos
        
        // player
        Model player = modelBuilder.createCylinder(DEPTH/2,80f/SCALE,DEPTH/2,20,
    			new Material(ColorAttribute.createDiffuse(Color.CYAN)), Usage.Position | Usage.Normal);
        entityModels.put("player", player);
        
        // enemy
        Model enemy = modelBuilder.createCylinder(DEPTH/2,80f/SCALE,DEPTH/2,20,
    			new Material(ColorAttribute.createDiffuse(Color.RED)), Usage.Position | Usage.Normal);
        entityModels.put("enemy", enemy);
        
        // left floor
		Model leftFloorModel = modelBuilder.createBox(32f/SCALE,6f/SCALE, DEPTH,
    			new Material(ColorAttribute.createDiffuse(Color.YELLOW)), Usage.Position | Usage.Normal);
        entityModels.put("leftFloor", leftFloorModel);
        
        // right floor
        Model rightFloorModel = modelBuilder.createBox(32f/SCALE,6f/SCALE, DEPTH,
    			new Material(ColorAttribute.createDiffuse(Color.YELLOW)), Usage.Position | Usage.Normal);
        entityModels.put("rightFloor", rightFloorModel);
        
        // loose floor
 		Model looseFloorModel = modelBuilder.createBox(64f/SCALE,6f/SCALE, DEPTH,
     			new Material(ColorAttribute.createDiffuse(Color.RED)), Usage.Position | Usage.Normal);
        entityModels.put("looseFloor", looseFloorModel);
         
        // opener
  		Model openerModel = modelBuilder.createBox(64f/SCALE,6f/SCALE, DEPTH,
      			new Material(ColorAttribute.createDiffuse(Color.ORANGE)), Usage.Position | Usage.Normal);
        entityModels.put("opener", openerModel);
          
        // closer
		Model closerModel = modelBuilder.createBox(64f/SCALE,6f/SCALE - 1f/SCALE, DEPTH,
    			new Material(ColorAttribute.createDiffuse(Color.ORANGE)), Usage.Position | Usage.Normal);
        entityModels.put("closer", closerModel);
        
        // closer
 		Model spikeFloorModel = modelBuilder.createBox(64f/SCALE,6f/SCALE - 1f/SCALE, DEPTH,
     			new Material(ColorAttribute.createDiffuse(Color.DARK_GRAY)), Usage.Position | Usage.Normal);
        entityModels.put("spike", spikeFloorModel);
    
        // wall stack
        Model stackMain = modelBuilder.createBox(64f/SCALE,120f/SCALE, DEPTH,
    			new Material(ColorAttribute.createDiffuse(Color.GRAY)), Usage.Position | Usage.Normal);
        entityModels.put("stackMain", stackMain);
		
        // base stack
        Model stackBase = modelBuilder.createBox(64f/SCALE,6f/SCALE, DEPTH,
    			new Material(ColorAttribute.createDiffuse(Color.YELLOW)), Usage.Position | Usage.Normal);
        entityModels.put("stackBase", stackBase);
        
        // pillar
        Model pillar = modelBuilder.createBox(64f/(3*SCALE), 120f/SCALE, DEPTH/5,
    			new Material(ColorAttribute.createDiffuse(Color.GRAY)), Usage.Position | Usage.Normal);
        entityModels.put("pillar", pillar);
        
        // doorFrame
        Model doorFrame = modelBuilder.createCylinder(DEPTH/5,120f/SCALE, DEPTH/5,20,
    			new Material(ColorAttribute.createDiffuse(Color.LIGHT_GRAY)), Usage.Position | Usage.Normal);
        entityModels.put("doorFrame", doorFrame);
        
        // door
        Model normalDoor = modelBuilder.createBox(64f/(12*SCALE), 120f/SCALE, DEPTH,
    			new Material(ColorAttribute.createDiffuse(Color.LIGHT_GRAY)), Usage.Position | Usage.Normal);
        entityModels.put("normalDoor", normalDoor);
        
        // final door
        Model finalDoor = modelBuilder.createBox(2*64f/(SCALE), 120f/SCALE, DEPTH/8,
    			new Material(ColorAttribute.createDiffuse(Color.GOLD)), Usage.Position | Usage.Normal);
        entityModels.put("finalDoor", finalDoor);
        
        // torch
        Model torch = modelBuilder.createBox(64f/(6*SCALE), 40f/SCALE, DEPTH/8,
    			new Material(ColorAttribute.createDiffuse(Color.ORANGE)), Usage.Position | Usage.Normal);
        entityModels.put("torch", torch);
        
        // potion
        Model potion = modelBuilder.createCone(64f/(2*SCALE), 40f/SCALE, DEPTH/8, 20,
    			new Material(ColorAttribute.createDiffuse(Color.RED)), Usage.Position | Usage.Normal);
        entityModels.put("potion", potion);
        
        // sword
        Model sword = modelBuilder.createBox(64f/(6*SCALE), 40f/SCALE, DEPTH/8,
    			new Material(ColorAttribute.createDiffuse(Color.LIGHT_GRAY)), Usage.Position | Usage.Normal);
        entityModels.put("sword", sword);
        
        
        // TODO: Definir modelos 3D para el resto de entidades
        // ...
		
        // Crea instancias 3D de cada entidad en cada habitacion
        Room[][] rooms = level.getCurrentLevel().getRooms();
        for (int i = 1; i <= NUM_ROWS - 1; i++) {
        	for (int j = 1; j <= NUM_COLS - 1; j++) {
        		Room room = rooms[i-1][j-1];
//        		System.out.println("Room: " + room + " -> " + i + ", " + j);

        		if (room != null) {
        			
			        List<Entity> roomEntities = new LinkedList<Entity>();
			        roomEntities.addAll(room.getBackground());
			        roomEntities.addAll(room.getForeground());
			        roomEntities.addAll(room.getCharacters());
			        
			        for(Entity entity : roomEntities){
			        	String entityName = entity.getTypeOfEntity();
			        	ModelInstance entityInstance = null;
			        	int sx = entity.getSquare()[0];
		        		int sy = entity.getSquare()[1];
			        	float x = 0f;
			        	float y = 0f;
			        	float z = 0f;
			        	
			        	// Asocia cada suelo con su modelo 3D
			        	if(entityName.contains("Floor") && entityName.contains("left")){
			        		if(!((FloorPanel) entity).isInvisible()){

			        			// crea instancia y la coloca en su posicion
				        		ModelInstance floorInstance = new ModelInstance(entityModels.get("leftFloor"));
				        		x = (float) (64 + sy * 64) / SCALE;
				        		y = (Game.HEIGHT - (float)(6 + sx * 126)) / SCALE;
				        		entityInstance = floorInstance;
			        		}
			        	}
			        	else if(entityName.contains("Floor") && entityName.contains("right")){
			        		if(!((FloorPanel) entity).isInvisible()){
				        		ModelInstance floorInstance = new ModelInstance(entityModels.get("rightFloor"));
				        		x = (float) (64 + 32 + sy * 64) / SCALE;
				        		y = (Game.HEIGHT - (float)(6 + sx * 126)) / SCALE;
				        		entityInstance = floorInstance;
			        		}
			        	}
			        	else if(entityName.startsWith("LooseFloor")){
			        		ModelInstance looseInstance = new ModelInstance(entityModels.get("looseFloor"));
			        		x = (float) (64 + 16 + sy * 64) / SCALE;
			        		if(sx == 0){
			        			y = (Game.HEIGHT - (float)(6 + sx * 126)) / SCALE;
			        		} else{
			        			y = (Game.HEIGHT - (float)(6 - 126 + sx * 126)) / SCALE;
			        		}
			        		if (sy == 0) {
				        		y = (Game.HEIGHT - (float)(6 + sx * 126)) / SCALE;
			        		}
			        		entityInstance = looseInstance;
			        	} 
			        	else if(entityName.startsWith("Opener")){
			        		ModelInstance openerInstance = new ModelInstance(entityModels.get("opener"));
			        		x = (float) (64 + 16 + sy * 64) / SCALE;
			        		y = (Game.HEIGHT - (float)(5 - 126 + sx * 126)) / SCALE;
			        		entityInstance = openerInstance;
			        	} 
			        	else if(entityName.startsWith("Closer")){
			        		ModelInstance closerInstance = new ModelInstance(entityModels.get("closer"));
			        		x = (float) (64 + 16 + sy * 64) / SCALE;
			        		y = (Game.HEIGHT - (float)(6 - 126 -(1f/2) + sx * 126)) / SCALE;
			        		entityInstance = closerInstance;
			        	}
			        	else if(entityName.startsWith("SpikeFloor")){
			        		ModelInstance spikeInstance = new ModelInstance(entityModels.get("spike"));
			        		x = (float) (64 + 16 + sy * 64) / SCALE;
			        		y = (Game.HEIGHT - (float)(6 - 126 + sx * 126)) / SCALE;
			        		entityInstance = spikeInstance;
			        	}
			        	else if(entityName.contains("stack_main") && !entityName.contains("face")){
			        		ModelInstance stackMainInstance = new ModelInstance(entityModels.get("stackMain"));
			        		x = (float) (64 + 16 + sy * 64) / SCALE;
			        		y = (Game.HEIGHT - (float)(6 - 63 + sx * 126)) / SCALE;
			        		entityInstance = stackMainInstance;
			        	}
			        	else if(entityName.contains("face_stack_main")){
			        		ModelInstance stackMainInstance = new ModelInstance(entityModels.get("stackMain"));
			        		if (sy == 0) {
				        		x = (float) (64 + 16 + sy * 64) / SCALE;
				        		y = (Game.HEIGHT - (float)(6 - 63 + sx * 126)) / SCALE;
				        		entityInstance = stackMainInstance;
			        		}
			        	}
			        	else if(entityName.contains("Base")){
			        		ModelInstance stackBaseInstance = new ModelInstance(entityModels.get("stackBase"));
			        		x = (float) (64 + 16 + sy * 64) / SCALE;
			        		y = (Game.HEIGHT - (float)(6 + sx * 126)) / SCALE;
			        		if (sx != 0) {
			        			y = (Game.HEIGHT - (float)(6 - 126 + sx * 126)) / SCALE;
			        		}
				    		entityInstance = stackBaseInstance;
			        	}
			        	else if(entityName.equals("Pillar_pillar_left")){
			        		ModelInstance pillarInstance = new ModelInstance(entityModels.get("pillar"));
			        		x = (float) (64 + 16 + sy * 64) / SCALE;
			        		y = (Game.HEIGHT - (float)(6 - 63 + sx * 126)) / SCALE;
			        		z = DEPTH/3;
			        		entityInstance = pillarInstance;
			        	}
			        	else if(entityName.equals("Pillar_pillar_right_main")){
			        		ModelInstance pillarInstance = new ModelInstance(entityModels.get("pillar"));
			        		x = (float) (64 + 16 + sy * 64) / SCALE;
			        		y = (Game.HEIGHT - (float)(6 - 63 + sx * 126)) / SCALE;
			        		z = -DEPTH/3;
			        		entityInstance = pillarInstance;
			        	} 
			        	else if(entityName.equals("DoorFrame_door_frame_right")){
			        		ModelInstance doorFrameRightInstance = new ModelInstance(entityModels.get("doorFrame"));
			        		x = (float) (128 - 32 + sy * 64) / SCALE;
			        		y = (Game.HEIGHT - (float)(6 - 63 + sx * 126)) / SCALE;
			        		z = -DEPTH/2;
			        		entityInstance = doorFrameRightInstance;
			        	} 
			        	else if(entityName.equals("DoorFrame_door_frame_left")){
			        		ModelInstance doorFrameLeftInstance = new ModelInstance(entityModels.get("doorFrame"));
			        		x = (float) (128 - 32 + sy * 64) / SCALE;
			        		y = (Game.HEIGHT - (float)(6 - 63 + sx * 126)) / SCALE;
			        		z = DEPTH/2;
			        		entityInstance = doorFrameLeftInstance;
			        	} 
			        	else if(entityName.equals("Door_normal")){
			        		ModelInstance normalDoorInstance = new ModelInstance(entityModels.get("normalDoor"));
			        		x = (float) (128 - 32 + sy * 64) / SCALE;
			        		y = (Game.HEIGHT - (float)(6 - 63 + sx * 126)) / SCALE;
			        		entityInstance = normalDoorInstance;
			        	}
			        	else if(entityName.equals("Door_final")){
			        		ModelInstance finalDoorInstance = new ModelInstance(entityModels.get("finalDoor"));
			        		x = (float) (64 + sy * 64) / SCALE;
			        		y = (Game.HEIGHT - (float)(6 - 63 + sx * 126)) / SCALE;
			        		z = -DEPTH/3;
			        		entityInstance = finalDoorInstance;
			        	}
			        	else if(entityName.equals("Torch")){
			        		ModelInstance torchInstance = new ModelInstance(entityModels.get("torch"));
			        		x = (float) (32 + sy * 64) / SCALE;
			        		y = (Game.HEIGHT - (float)(6 - 63 + sx * 126)) / SCALE;
			        		z = -DEPTH/3;
			        		entityInstance = torchInstance;
			        	}
			        	else if(entityName.startsWith("Potion_")){
			        		ModelInstance potionInstance = new ModelInstance(entityModels.get("potion"));
			        		x = (float) (64 + sy * 64) / SCALE;
			        		y = (Game.HEIGHT - (float)(-186f/SCALE + sx * 126)) / SCALE;
			        		z = 0;
			        		entityInstance = potionInstance;
			        	}
			        	else if(entityName.equals("SwordFloor")){
			        		ModelInstance swordFloorInstance = new ModelInstance(entityModels.get("sword"));
			        		x = (float) (32 + sy * 64) / SCALE;
			        		y = (Game.HEIGHT - (float)(6 - 63 + sx * 126)) / SCALE;
			        		z = 0;
			        		entityInstance = swordFloorInstance;
			        	}
			        	else if (entityName.contains("Player")){
			        		ModelInstance playerInstance = new ModelInstance(entityModels.get("player"));
			        		x = (float) (entity.getCenter()[0] + 64) / SCALE;
			        		y = (Game.HEIGHT - (float) entity.getCenter()[1]) / SCALE;
			        		entityInstance = playerInstance;
			        	}
			        	else if (entityName.contains("Enemy")){
			        		ModelInstance enemyInstance = new ModelInstance(entityModels.get("enemy"));
			        		x = (float) (entity.getCenter()[0] + 64) / SCALE;
			        		y = (Game.HEIGHT - (float) entity.getCenter()[1]) / SCALE;
			        		entityInstance = enemyInstance;
			        	}
			        	
			        	// asocia la nueva instancia 3D a su entidad
		        		if (entityInstance != null) {
		        			ModelInstance entityInstanceFullLevel = entityInstance.copy();
		        			
		        			// inicializa entidades para el modo de una habitacion
		        			entityInstance.transform.translate(x,y,z);
		        			entities.get(i).get(j).put(entity, entityInstance);

		        			// when drawing complete level
//		        			if (FULL_LEVEL) {
		        				x = x + (Game.WIDTH / SCALE) * (j - 1);
		        				y = y + (Game.HEIGHT / SCALE) - ( ((Game.HEIGHT - UI_HEIGHT) / SCALE) * (i - 1) );
//		        			}
	        				entityInstanceFullLevel.transform.translate(x,y,z);
		        			entitiesFullLevel.get(i).get(j).put(entity, entityInstanceFullLevel);
		        		}
			        }
	        	}
        	}
        }
	}
	
	/**
	 * Cambia la habitacion de una entidad
	 */
	private void changeEntityRoom(Entity entityToChange, int currentRow, int currentCol, int previousRow, int previousCol) {
		
		// obtiene las entidades de la habitacion anterior
		Room[][] rooms = level.getCurrentLevel().getRooms();
		Room room = rooms[currentRow - 1][currentCol - 1];
		
		List<Entity> roomEntities = new LinkedList<Entity>();
        roomEntities.addAll(room.getBackground());
        roomEntities.addAll(room.getForeground());
        roomEntities.addAll(room.getCharacters());
		
        for(Entity entity : roomEntities){
        	
        	if (entity.equals(entityToChange)) {
        		
        		ModelInstance entityInstance = 
        				entities.get(previousRow).get(previousCol).get(entity);

        		ModelInstance entityInstanceFullLevel = 
        				entitiesFullLevel.get(previousRow).get(previousCol).get(entity);
        		
        		// incluye la entidad en la nueva habitacion
        		// y la elimina de la anterior habitacion
        		System.out.println(entity.getTypeOfEntity() + " - AÑADIR FULL LEVEL    (" + currentRow + " - " + currentCol + ")");
        		addEntityToRoomFullLevel(entity, entityInstanceFullLevel, currentRow, currentCol);
        		addEntityToRoom(entity, entityInstance, currentRow, currentCol);
        		System.out.println("DELETE    (" + previousRow + " - " + previousCol + ")");
        		deleteEntityFromRoom(entity, previousRow, previousCol);

//        		if (FULL_LEVEL) {
        			moveEntityToNextRoom(entityInstanceFullLevel, currentRow, currentCol, previousRow, previousCol);
//        		}
        	}
        }
        
	}
	
	private void moveEntityToNextRoom(ModelInstance entityInstance, int currentRow, int currentCol, int previousRow, int previousCol) {
		float x = 0f;
		float y = 0f;
		
		// comprueba hacia que habitacion se ha movido la entidad
		if (currentRow < previousRow) {
			
			// arriba
			y = y + ((Game.HEIGHT - UI_HEIGHT) / SCALE);
		}
		else if (currentRow > previousRow) {
			
			// abajo
			y = y - ((Game.HEIGHT - UI_HEIGHT) / SCALE);
		}
		else if (currentCol < previousCol) {
			
			// izquierda
			x = x - (Game.WIDTH / SCALE);
		}
		else if (currentCol > previousCol) {
	
			// derecha
			x = x + (Game.WIDTH / SCALE);
		}
		
		// aplica el offset calculado a la entidad
		entityInstance.transform.translate(x,y,0f);
	}
	
	private void deleteEntityFromRoom(Entity entity, int row, int col) {
		entities.get(row).get(col).remove(entity);
		entitiesFullLevel.get(row).get(col).remove(entity);
	}
	
	private void addEntityToRoom(Entity entity, ModelInstance entityInstance, int row, int col) {
		entities.get(row).get(col).put(entity, entityInstance);
	}
	
	private void addEntityToRoomFullLevel(Entity entity, ModelInstance entityInstance, int row, int col) {
		entitiesFullLevel.get(row).get(col).put(entity, entityInstance);
	}
	
	private void checkPlayer(){
		// comprueba si se ha cambiado de habitacion
		if (currRow != prevRow || currCol != prevCol) {
			// se ha cambiado de habitacion
			
			// cambia de habitacion al player

			changeEntityRoom(player,currRow,currCol,prevRow,prevCol);
		}
	}
	
	private void checkLooses(){
		for(LooseFloor loose : this.falling_floor){
			if(!isEntityInRoom(loose, loose.getRoom1(), loose.getRoom2())){
				changeEntityRoom(loose,loose.getRoom1(),loose.getRoom2(),loose.getRoom1()-1,loose.getRoom2());
			}
		}
	}
	
	private void checkEntitiesToBeDeleted(){
		for(Entity e : this.entitiesToBeDeleted){
			for (int i = 0; i < NUM_ROWS; i++) {
				for (int j = 0; j < NUM_COLS; j++) {
					if (entities.get(i).get(j).get(e) != null) {
						entities.get(i).get(j).remove(e);
					}
					if (entitiesFullLevel.get(i).get(j).get(e) != null) {
						entitiesFullLevel.get(i).get(j).remove(e);
					}
				}
			}
		}
		
		entitiesToBeDeleted.clear();
	}
	
	private void checkDoors(){
		for(Door d : this.doors){
			if(d.getTypeOfEntity().contains("normal")){
				if(d.getCurrentAnimation().isLastFrame()){
					switch (d.getCurrentAnimation().getId()){
					case "door_half_opening":
						if(d.getCurrentAnimation().getCurrentFrame() == 1 && d.getCurrentAnimation().isLastFrame()){
							entities.get(d.getRoomRow() + 1).get(d.getRoomCol() + 1).get(d).transform.scale(1f, 0.5f, 1f);
							entitiesFullLevel.get(d.getRoomRow() + 1).get(d.getRoomCol() + 1).get(d).transform.scale(1f, 0.5f, 1f);
						}
						break;
					default:
						
						break;
					}
				}
			}
		}
	}
	
	/**
	 * 
	 * @param entity entity to find in the room
	 * @param row row index of the room
	 * @param col col index of the room
	 * @return true if the entity is in that room
	 */
	private boolean isEntityInRoom(Entity entity, int row, int col){
		return entities.get(row).get(col).get(entity) != null;
	}
}
