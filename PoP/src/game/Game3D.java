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

import java.util.Collection;
import java.util.Hashtable;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
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
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;

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
	public Hashtable<Entity,ModelInstance> entities;
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
		
		level.update(TARGET_TIME);
		Collection<ModelInstance> objects = entities.values();
		
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
 
        camController.update();
        modelBatch.begin(cam);
        modelBatch.render(objects, lights);
        modelBatch.end();
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
        entities = new Hashtable<Entity, ModelInstance>();
        
        // Crear modelos para cada entidad y asociarlos
		Model floorModel = modelBuilder.createBox(5f,1f, 10f,
    			new Material(ColorAttribute.createDiffuse(Color.GRAY)), Usage.Position | Usage.Normal);
        entityModels.put("floor", floorModel);
		
        // TODO: Definir modelos 3D para el resto de entidades
        // ...
		
        // Crea instancias 3D de cada entidad
        for(Entity entity : level.getCurrentRoom().getBackground()){
        	String entityName = entity.getTypeOfEntity();
        	
        	// Asocia cada suelo con su modelo 3D
        	if(entityName.contains("Floor")){
        		System.out.println("ENTRAMOS");
        		
        		// crea instancia y la coloca en su posicion
        		ModelInstance floorInstance = new ModelInstance(entityModels.get("floor"));
        		float x = entity.getX() / SCALE;
        		float y = entity.getY() / SCALE;
        		floorInstance.transform.translate(x,y,0);
        		
        		// asocia la nueva instancia 3D a su entidad
        		entities.put(entity, floorInstance);
        	}
        }
        
//        // CUBO
//        model = modelBuilder.createBox(5f, 5f, 5f,new Material(ColorAttribute.createDiffuse(Color.GREEN)),
//              Usage.Position | Usage.Normal);
//        instance = new ModelInstance(model);
	}
}
