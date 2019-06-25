package EngineInitializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import Shaders.StaticShader;
import entities.Camera;
import entities.Entity;
import entities.Light;
import models.RawModel;
import models.TextureModel;
import renderEngine.DataLoader;
import renderEngine.EntityRenderEngine;
import textures.ModelTexture;
import renderEngine.DisplayManager;
import renderEngine.MasterRenderEngine;
import renderEngine.OBJLoader;
import terrains.Terrain;

public class MainInitLoop {

	public static void main(String[] args) {

		DisplayManager.createDisplay();
		
		DataLoader dataloader = new DataLoader();

		  RawModel model = OBJLoader.loadObjModel("tree", dataloader);
		  
		  TextureModel staticModel = new TextureModel(model, new ModelTexture(dataloader.loadTexture("tree")));
		  TextureModel grass = new TextureModel(model, new ModelTexture(dataloader.loadTexture("grass")));
		  TextureModel fern = new TextureModel(model, new ModelTexture(dataloader.loadTexture("fern")));
		  ModelTexture texture = staticModel.getTexture();
		  grass.getTexture().setHasTransparency(true);
		  grass.getTexture().setUseFakeLighting(true);
		  fern.getTexture();
		  texture.setShineDamper(10);
		  texture.setReflectivity(1);
		  List<Entity> entities = new ArrayList<Entity>();
		  Random random = new Random();
	        for(int i=0;i<500;i++){
	            entities.add(new Entity(staticModel, new Vector3f(
	            		random.nextFloat()*800 - 400,0,random.nextFloat() * -600),0,0,0,3));
	            entities.add(new Entity(grass, new Vector3f(
	            		random.nextFloat()*800 - 400,0,random.nextFloat() * -600),0,0,0,3));
	            entities.add(new Entity(fern, new Vector3f(
	            		random.nextFloat()*800 - 400,0,random.nextFloat() * -600),0,0,0,3));
	        }
		  
		  Entity entity = new Entity(staticModel, new Vector3f(0,0,-25),0,0,0,1);
		  Light light = new Light(new Vector3f(3000,2000,2000),new Vector3f(1,1,1));
		  
		  Terrain terrain = new Terrain(-1,-1,dataloader,new ModelTexture(dataloader.loadTexture("grass")));
		  Terrain terrain2 = new Terrain(1,0,dataloader,new ModelTexture(dataloader.loadTexture("grass")));
		  
		  Camera camera = new Camera();
		  
		  MasterRenderEngine rendererEngine = new MasterRenderEngine();
		while(!Display.isCloseRequested()) {
			entity.increaseRotation(0,1,0);
			camera.move();
			
			rendererEngine.processTerrain(terrain);
			rendererEngine.processTerrain(terrain2);
			rendererEngine.processEntity(entity);
			
			rendererEngine.render(light, camera);
			DisplayManager.updateDisplay();
		}
		
		rendererEngine.cleanUp();
		dataloader.cleanBuffers();
		DisplayManager.closeDisplay();

	}

}
