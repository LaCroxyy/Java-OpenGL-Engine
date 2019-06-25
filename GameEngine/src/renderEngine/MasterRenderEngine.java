package renderEngine;
 
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
 
import models.TextureModel;
 
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
 
import Shaders.StaticShader;
import Shaders.TerrainShader;
import terrains.Terrain;
import entities.Camera;
import entities.Entity;
import entities.Light;
 
public class MasterRenderEngine {
     
    private static final float FOV = 70;
    private static final float NEAR_PLANE = 0.1f;
    private static final float FAR_PLANE = 1000;
    
    private static final float RED = 0.5f;
    private static final float GREEN = 0.5f;
    private static final float BLUE = 0.5f;
     
    private Matrix4f projectionMatrix;
     
    private StaticShader shader = new StaticShader();
    private EntityRenderEngine renderEngine;
     
    private TerrainRenderEngine terrainRenderEngine;
    private TerrainShader terrainShader = new TerrainShader();
     
     
    private Map<TextureModel,List<Entity>> entities = new HashMap<TextureModel,List<Entity>>();
    private List<Terrain> terrains = new ArrayList<Terrain>();
     
    public MasterRenderEngine(){
    	enableCulling();
        createProjectionMatrix();
        renderEngine = new EntityRenderEngine(shader,projectionMatrix);
        terrainRenderEngine = new TerrainRenderEngine(terrainShader,projectionMatrix);
    }
    
    public static void enableCulling() {
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
    }
    
    public static void disableCulling() {
    	GL11.glDisable(GL11.GL_CULL_FACE);
    }
     
    public void render(Light sun,Camera camera){
        prepare();
        shader.start();
        shader.loadSkyColour(RED, GREEN, BLUE);
        shader.loadLight(sun);
        shader.loadViewMatrix(camera);
        renderEngine.render(entities);
        shader.stop();
        terrainShader.start();
        terrainShader.loadSkyColour(RED, GREEN, BLUE);
        terrainShader.loadLight(sun);
        terrainShader.loadViewMatrix(camera);
        terrainRenderEngine.render(terrains);
        terrainShader.stop();
        terrains.clear();
        entities.clear();
    }
     
    public void processTerrain(Terrain terrain){
        terrains.add(terrain);
    }
     
    public void processEntity(Entity entity){
        TextureModel entityModel = entity.getModel();
        List<Entity> batch = entities.get(entityModel);
        if(batch!=null){
            batch.add(entity);
        }else{
            List<Entity> newBatch = new ArrayList<Entity>();
            newBatch.add(entity);
            entities.put(entityModel, newBatch);        
        }
    }
     
    public void cleanUp(){
        shader.cleanUp();
        terrainShader.cleanUp();
    }
     
    public void prepare() {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(RED, GREEN,BLUE, 1);
    }
     
    private void createProjectionMatrix() {
        float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
        float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))) * aspectRatio);
        float x_scale = y_scale / aspectRatio;
        float frustum_length = FAR_PLANE - NEAR_PLANE;
 
        projectionMatrix = new Matrix4f();
        projectionMatrix.m00 = x_scale;
        projectionMatrix.m11 = y_scale;
        projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
        projectionMatrix.m23 = -1;
        projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
        projectionMatrix.m33 = 0;
    }
     
 
}