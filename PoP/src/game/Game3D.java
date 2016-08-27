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
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.SpotLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;

import data.Room;
import entities.Entity;
import input.Key;
import states.LevelState;
import states.MenuState;

/**
 * See: http://blog.xoppa.com/basic-3d-using-libgdx-2/
 * @author Xoppa
 */
public class Game3D implements ApplicationListener {
	
	private final int SCALE = 10;
	private final long TARGET_TIME = 1000/60;
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
	public Model model;
	public ModelInstance instance;
	public Texture texture;
	public static MenuState menu;
	public static LevelState level;
	public static ConcurrentLinkedQueue<Key> keys;
	public List<List<Hashtable<Entity,ModelInstance>>> entities;
	public Hashtable<String,Model> entityModels;
	
	
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
		
		Collection<ModelInstance> objects = entities.get(currRow).get(currCol).values();
		
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
 
        camController.update();
        modelBatch.begin(cam);
        modelBatch.render(objects, lights);
        modelBatch.end();
	}
	
	public void update() {
		
		// asigna una ultima posicion a cada objeto
		Map<Entity, int[]> lastPos = new HashMap<>();
		for (Entity e : entities.get(currRow).get(currCol).keySet()) {
			lastPos.put(e, e.getCenter());
			
//			if (e.getTypeOfEntity().contains("Player")) {
//				System.out.println("Player: " + e.getCenter()[0] + ", " + e.getCenter()[1]);
//			}
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
		
		// comprueba si se ha cambiado de habitacion
		if (currRow != prevRow || currCol != prevCol) {
			// se ha cambiado de habitacion
			
			// cambia de habitacion al player
			System.out.println("3D: CAMBIO DE ROOM:" +
								prevRow + ", " + prevCol +
								" -> " + currRow + ", " + currCol);
			changeEntityRoom("Player");
		}
		
		// actualiza cada objeto en funcion de su movimiento
		// (diferencia con la posicion anterior)
		for (Map.Entry<Entity, ModelInstance> entry : entities.get(currRow).get(currCol).entrySet()) {
			Entity key = entry.getKey();
			ModelInstance value = entry.getValue();

			int[] last = lastPos.get(key);
			if (last != null) {
				
//				System.out.println("Ha entrado: " + key.getTypeOfEntity());
				
				float x = (float) (key.getCenter()[0] - last[0]) / SCALE;
				float y = (float) -(key.getCenter()[1] - last[1]) / SCALE;
				value.transform.translate(x,y,0);
			}
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
	}
	
	@Override
	public void dispose() {
		modelBatch.dispose();
		model.dispose();
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
	}
	
	public void initEnvironment(){
		lights = new Environment();
		lights.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		lights.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
	}
	
	public void initCam(){
		cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.position.set(Game.WIDTH/(2*SCALE), Game.HEIGHT/(2*SCALE), 50f);
		cam.lookAt(Game.WIDTH/(2*SCALE),Game.HEIGHT/(2*SCALE),0);
		cam.near = 1f;
		cam.far = 300f;
		cam.update();
		camController = new CameraInputController(cam);
		Gdx.input.setInputProcessor(camController);
	}
	
	public void initModels(){
		modelBatch = new ModelBatch();
        ModelBuilder modelBuilder = new ModelBuilder();
        entityModels = new Hashtable<String, Model>();
        entities = new LinkedList<List<Hashtable<Entity, ModelInstance>>>();
        
        // inicializa el array de entidades
        for (int i = 0; i < NUM_ROWS; i++) {
        	
        	entities.add(i, new LinkedList<Hashtable<Entity, ModelInstance>>());
        	
        	for (int j = 0; j < NUM_COLS; j++) {
        		entities.get(i).add(j, new Hashtable<Entity, ModelInstance>());
        	}
    	}
        
        // Crear modelos para cada entidad y asociarlos
        
        // player
        Model player = modelBuilder.createCylinder(DEPTH/2,80f/SCALE,DEPTH/2,20,
    			new Material(ColorAttribute.createDiffuse(Color.CYAN)), Usage.Position | Usage.Normal);
        entityModels.put("player", player);
        
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
        
        // torch
        Model torch = modelBuilder.createBox(64f/(6*SCALE), 40f/SCALE, DEPTH/8,
    			new Material(ColorAttribute.createDiffuse(Color.ORANGE)), Usage.Position | Usage.Normal);
        entityModels.put("torch", torch);
        
        
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
			        	
			        	// Asocia cada suelo con su modelo 3D
			        	if(entityName.contains("Floor") && entityName.contains("left")){
			        		
			        		// crea instancia y la coloca en su posicion
			        		ModelInstance floorInstance = new ModelInstance(entityModels.get("leftFloor"));
			        		int sx = entity.getSquare()[0];
			        		int sy = entity.getSquare()[1];
			        		float x = (float) (64 + sy * 64) / SCALE;
			        		float y = (Game.HEIGHT - (float)(6 + sx * 126)) / SCALE;
			        		
			        		floorInstance.transform.translate(x,y,0);
			        		
			        		// asocia la nueva instancia 3D a su entidad
			        		entities.get(i).get(j).put(entity, floorInstance);
			        	}
			        	else if(entityName.contains("Floor") && entityName.contains("right")){
			        		
			        		ModelInstance floorInstance = new ModelInstance(entityModels.get("rightFloor"));
			        		int sx = entity.getSquare()[0];
			        		int sy = entity.getSquare()[1];
			        		float x = (float) (96 + sy * 64) / SCALE;
			        		float y = (Game.HEIGHT - (float)(6 + sx * 126)) / SCALE;
			        		
			        		floorInstance.transform.translate(x,y,0);
			        		entities.get(i).get(j).put(entity, floorInstance);
			        	}
			        	else if(entityName.startsWith("LooseFloor")){
			        		
			        		ModelInstance looseInstance = new ModelInstance(entityModels.get("looseFloor"));
			        		int sx = entity.getSquare()[0];
			        		int sy = entity.getSquare()[1];
			        		float x = (float) (64 + sy * 64) / SCALE;
			        		float y = (Game.HEIGHT - (float)(6 + sx * 126)) / SCALE;
			        		
			        		looseInstance.transform.translate(x,y,0);
			        		entities.get(i).get(j).put(entity, looseInstance);
			        	}
			        	else if(entityName.contains("stack_main") && !entityName.contains("face")){
			        		
			        		ModelInstance stackMainInstance = new ModelInstance(entityModels.get("stackMain"));
			        		int sx = entity.getSquare()[0];
			        		int sy = entity.getSquare()[1];
			        		float x = (float) (64 + 16 + sy * 64) / SCALE;
			        		float y = (Game.HEIGHT - (float)(6 - 63 + sx * 126)) / SCALE;
			        		
			        		stackMainInstance.transform.translate(x,y,0);
			        		entities.get(i).get(j).put(entity, stackMainInstance);
			        	}
			        	else if(entityName.contains("face_stack_main")){
			        		
			        		ModelInstance stackMainInstance = new ModelInstance(entityModels.get("stackMain"));
			        		int sx = entity.getSquare()[0];
			        		int sy = entity.getSquare()[1];
			        		if (sy == 0) {
				        		float x = (float) (64 + 16 + sy * 64) / SCALE;
				        		float y = (Game.HEIGHT - (float)(6 - 63 + sx * 126)) / SCALE;
				        		
				        		stackMainInstance.transform.translate(x,y,0);
				        		entities.get(i).get(j).put(entity, stackMainInstance);
			        		}
			        	}
			        	else if(entityName.contains("Base")){
			        		
			        		ModelInstance stackBaseInstance = new ModelInstance(entityModels.get("stackBase"));
			        		int sx = entity.getSquare()[0];
			        		int sy = entity.getSquare()[1];
			        		float x = (float) (64 + 16 + sy * 64) / SCALE;
			        		float y = (Game.HEIGHT - (float)(6 + sx * 126)) / SCALE;
			
			        		if (sx != 0) {
			        			y = (Game.HEIGHT - (float)(6 - 126 + sx * 126)) / SCALE;
			        		}
				    		stackBaseInstance.transform.translate(x,y,0);
				    		entities.get(i).get(j).put(entity, stackBaseInstance);
			        	}
			        	else if(entityName.equals("Pillar_pillar_left")){
			        		
			        		ModelInstance pillarInstance = new ModelInstance(entityModels.get("pillar"));
			        		int sx = entity.getSquare()[0];
			        		int sy = entity.getSquare()[1];
			        		float x = (float) (64 + 16 + sy * 64) / SCALE;
			        		float y = (Game.HEIGHT - (float)(6 - 63 + sx * 126)) / SCALE;
			
			        		pillarInstance.transform.translate(x,y,DEPTH/3);
			        		entities.get(i).get(j).put(entity, pillarInstance);
			        	}
			        	else if(entityName.equals("Pillar_pillar_right_main")){
			        		
			        		ModelInstance pillarInstance = new ModelInstance(entityModels.get("pillar"));
			        		int sx = entity.getSquare()[0];
			        		int sy = entity.getSquare()[1];
			        		float x = (float) (64 + 16 + sy * 64) / SCALE;
			        		float y = (Game.HEIGHT - (float)(6 - 63 + sx * 126)) / SCALE;
			
			        		pillarInstance.transform.translate(x,y,-DEPTH/3);
			        		entities.get(i).get(j).put(entity, pillarInstance);
			        	} 
			        	else if(entityName.equals("DoorFrame_door_frame_right")){
			        		
			        		ModelInstance doorFrameInstance = new ModelInstance(entityModels.get("doorFrame"));
			        		int sx = entity.getSquare()[0];
			        		int sy = entity.getSquare()[1];
			        		float x = (float) (128 - 32 + sy * 64) / SCALE;
			        		float y = (Game.HEIGHT - (float)(6 - 63 + sx * 126)) / SCALE;
			
			        		doorFrameInstance.transform.translate(x,y,-DEPTH/2);
			        		entities.get(i).get(j).put(entity, doorFrameInstance);
			        	} 
			        	else if(entityName.equals("DoorFrame_door_frame_left")){
			        		
//			        		ModelInstance pillarInstance = new ModelInstance(entityModels.get("doorFrame"));
//			        		int sx = entity.getSquare()[0];
//			        		int sy = entity.getSquare()[1];
//			        		float x = (float) (128 - 32 + sy * 64) / SCALE;
//			        		float y = (Game.HEIGHT - (float)(6 - 63 + sx * 126)) / SCALE;
//			
//			        		pillarInstance.transform.translate(x,y,-DEPTH/3);
//			        		entities.get(i).get(j).put(entity, pillarInstance);
			        	} 
			        	else if(entityName.equals("Door_normal")){
			        		
			        		ModelInstance normalDoorInstance = new ModelInstance(entityModels.get("normalDoor"));
			        		int sx = entity.getSquare()[0];
			        		int sy = entity.getSquare()[1];
			        		float x = (float) (128 - 32 + sy * 64) / SCALE;
			        		float y = (Game.HEIGHT - (float)(6 - 63 + sx * 126)) / SCALE;
			
			        		normalDoorInstance.transform.translate(x,y,0);
			        		entities.get(i).get(j).put(entity, normalDoorInstance);
			        	}
			        	else if(entityName.equals("Torch")){
			        		
			        		ModelInstance torchInstance = new ModelInstance(entityModels.get("torch"));
			        		int sx = entity.getSquare()[0];
			        		int sy = entity.getSquare()[1];
			        		float x = (float) (32 + sy * 64) / SCALE;
			        		float y = (Game.HEIGHT - (float)(6 - 63 + sx * 126)) / SCALE;
			
			        		torchInstance.transform.translate(x,y,-DEPTH/3);
			        		entities.get(i).get(j).put(entity, torchInstance);
			        	}
			        	else if (entityName.contains("Player")){
			        		
			        		ModelInstance playerInstance = new ModelInstance(entityModels.get("player"));
			        		int sx = entity.getSquare()[0];
			        		int sy = entity.getSquare()[1];
			        		float x = (float) (entity.getCenter()[0] + 64) / SCALE;
			        		float y = (Game.HEIGHT - (float) entity.getCenter()[1]) / SCALE;
			        		
			//        		System.out.println(entityName + " -> " + sx + ", " + sy + " -> " + x + ", " + y);
			        		
			        		playerInstance.transform.translate(x,y,0);
			        		entities.get(i).get(j).put(entity, playerInstance);
			        	}
			        }
	        	}
        	}
        }
	}
	
	/**
	 * Cambia la habitacion de una entidad
	 */
	private void changeEntityRoom(String entityName) {
		
		// obtiene las entidades de la habitacion anterior
		Room[][] rooms = level.getCurrentLevel().getRooms();
		Room room = rooms[currRow - 1][currCol - 1];
		
		List<Entity> roomEntities = new LinkedList<Entity>();
        roomEntities.addAll(room.getBackground());
        roomEntities.addAll(room.getForeground());
        roomEntities.addAll(room.getCharacters());
		
        for(Entity entity : roomEntities){
        	String entName = entity.getTypeOfEntity();
        	
        	if (entName.contains(entityName)) {
        		
        		ModelInstance entityInstance = 
        				entities.get(prevRow).get(prevCol).get(entity);
        		
//        		System.out.println("GETTING 3D MODEL (" + prevRow +  ", " + prevCol
//        				+ ") Modelo3D: " + entityInstance);
        		
        		// elimina la entidad de la habitacion anterior
        		// y la incluye en su nueva habitacion
        		
//        		System.out.println("TO BE ADDED: " + entName + " - " + entity);
        		
        		addEntityToRoom(entity, entityInstance, currRow, currCol);
        		
//        		System.out.println("TO BE DELETED: " + entName + " - " + entity);
        		
        		deleteEntityFromRoom(entity, prevRow, prevCol);
        		
//        		System.out.println("DELETED: " + entName + " - " + entity);
        		
        	}
        }
	}
	
	private void deleteEntityFromRoom(Entity entity, int row, int col) {
//		System.out.println("DELETING ENTITY (" + row +  ", " + col + ")" 
//				+ " -> Entity: " + entity);
		
		entities.get(row).get(col).remove(entity);
	}
	
	private void addEntityToRoom(Entity entity, ModelInstance entityInstance, int row, int col) {
//		System.out.println("ADDING ENTITY (" + row +  ", " + col + ")"
//				+ " -> Entity: " + entity + ", Modelo3D: " + entityInstance);

		entities.get(row).get(col).put(entity, entityInstance);
	}
}
