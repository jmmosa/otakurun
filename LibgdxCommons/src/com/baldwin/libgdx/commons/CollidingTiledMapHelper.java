package com.baldwin.libgdx.commons;

import static com.baldwin.libgdx.physics.Constants.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.tiled.SimpleTileAtlas;
import com.badlogic.gdx.graphics.g2d.tiled.TileAtlas;
import com.badlogic.gdx.graphics.g2d.tiled.TileMapRenderer;
import com.badlogic.gdx.graphics.g2d.tiled.TiledLayer;
import com.badlogic.gdx.graphics.g2d.tiled.TiledLoader;
import com.badlogic.gdx.graphics.g2d.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.World;
import com.baldwin.libgdx.physics.Box2DFactory;

/**
 * Naive collision TiledMapHelper (based on dpk's one, included in this project)
 * 	1. Collision layer must be layers[1]
 *  2. Colliding tiles must have the property collide=yes
 * @author markm
 */
public class CollidingTiledMapHelper {
	private static final int[] layersList = { 0 };
	private FileHandle resourceDirectory;

	private OrthographicCamera camera;

	private TileAtlas tileAtlas;
	private TileMapRenderer tileMapRenderer;

	private TiledMap map;
	
	/**
	 * Renders the part of the map that should be visible to the user.
	 */
	public void render() {
		tileMapRenderer.getProjectionMatrix().set(camera.combined);

		Vector3 tmp = new Vector3();
		tmp.set(0, 0, 0);
		camera.unproject(tmp);

		tileMapRenderer.render((int) tmp.x, (int) tmp.y,
				Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), layersList);
	}

	/**
	 * Get the height of the map in pixels
	 * 
	 * @return y
	 */
	public int getHeight() {
		return map.height * map.tileHeight;
	}

	/**
	 * Get the width of the map in pixels
	 * 
	 * @return x
	 */
	public int getWidth() {
		return map.width * map.tileWidth;
	}

	/**
	 * Get the map, useful for iterating over the set of tiles found within
	 * 
	 * @return TiledMap
	 */
	public TiledMap getMap() {
		return map;
	}

	/**
	 * Calls dispose on all disposable resources held by this object.
	 */
	public void dispose() {
		tileAtlas.dispose();
		tileMapRenderer.dispose();
	}

	/**
	 * Sets the directory that holds the game's pack files and tile sets.
	 * 
	 * @param resourceDirectory
	 */
	public void setResourceDirectory(String resourceDirectory) {
		this.resourceDirectory = Gdx.files.internal(resourceDirectory);
	}

	/**
	 * Loads the requested tmx map file in to the helper.
	 * 
	 * @param tmxFile
	 */
	public void loadMap(String tmxFile) {
		if (resourceDirectory == null) {
			throw new IllegalStateException("loadMap() called out of sequence");
		}

		map = TiledLoader.createMap(Gdx.files.internal(tmxFile));
		tileAtlas = new SimpleTileAtlas(map, resourceDirectory);

		tileMapRenderer = new TileMapRenderer(map, tileAtlas, 16, 16);
	}

	private void buildWall(World world, int start, int y_index, int length) {
		Box2DFactory.createWall(world, map.tileWidth*start / (2f*PIXELS_PER_METER),  (map.tileHeight*(map.height-y_index) / (2f*PIXELS_PER_METER)), map.tileWidth*(start+length) / (2f*PIXELS_PER_METER), (map.tileHeight*(map.height - (y_index+1f)) / (2f*PIXELS_PER_METER)), 0f);
	}
	
	public void loadCollisions(World world) {
		TiledLayer collisionLayer = map.layers.get(1);
		if(null == collisionLayer) {
			throw new IllegalArgumentException("Collision layer not found!");
		}
		
		String collide = "collide";
		String yes = "yes";
		
		int last = -1;
		int length = 0;
		
		for(int i = 0; i < collisionLayer.tiles.length; i++) {
			for(int j = 0; j < collisionLayer.tiles[i].length; j++) {
				if(0 == collisionLayer.tiles[i][j]) {
					if(last != -1) {
						buildWall(world, last, i, length);
						last = -1;
						length = 0;
					}
					continue;
				}
				
				String prop = map.getTileProperty(collisionLayer.tiles[i][j], collide);
				if(yes.equals(prop)) {
					//create colliding body here?
					System.out.println(map.height + " " + i);
					//Box2DFactory.createWall(world, map.tileWidth*j / (2f*PIXELS_PER_METER),  (map.tileHeight*(map.height-i) / (2f*PIXELS_PER_METER)), map.tileWidth*(j+1f) / (2f*PIXELS_PER_METER), (map.tileHeight*(map.height - (i+1f)) / (2f*PIXELS_PER_METER)), 0f);
					if(last == -1) {
						last = j;
					}
					length++;
				} else if(last != 0) {
					//build wall with start at index last and length = 0;
					buildWall(world, last, i, length);
					last = -1;
					length = 0;
				}
			}
			
			//if y changes start a new wall
			if(last != -1) {
				buildWall(world, last, i, length);
				last = -1;
				length = 0;
			}
		}
		
		/**
		 * Drawing a boundary around the entire map. We can't use a box because
		 * then the world objects would be inside and the physics engine would
		 * try to push them out.
		 */
		BodyDef groundBodyDef = new BodyDef();
		groundBodyDef.type = BodyDef.BodyType.StaticBody;
		Body groundBody = world.createBody(groundBodyDef);

		EdgeShape mapBounds = new EdgeShape();
		mapBounds.set(new Vector2(0.0f, 0.0f), new Vector2(getWidth() / PIXELS_PER_METER, 0.0f));
		groundBody.createFixture(mapBounds, 0);

		mapBounds.set(new Vector2(0.0f, getHeight() / PIXELS_PER_METER), new Vector2(getWidth() / PIXELS_PER_METER, getHeight() / PIXELS_PER_METER));
		groundBody.createFixture(mapBounds, 0);

		mapBounds.set(new Vector2(0.0f, 0.0f), new Vector2(0.0f, getHeight() / PIXELS_PER_METER));
		groundBody.createFixture(mapBounds, 0);

		mapBounds.set(new Vector2(getWidth() / PIXELS_PER_METER, 0.0f), new Vector2(getWidth() / PIXELS_PER_METER, getHeight() / PIXELS_PER_METER));
		groundBody.createFixture(mapBounds, 0);
	}


	/**
	 * Prepares the helper's camera object for use.
	 * 
	 * @param screenWidth
	 * @param screenHeight
	 */
	public Camera prepareCamera(int screenWidth, int screenHeight) {
		camera = new OrthographicCamera(screenWidth, screenHeight);
		camera.position.set(0, 0, 0);
		return camera;
	}

	/**
	 * Returns the camera object created for viewing the loaded map.
	 * 
	 * @return OrthographicCamera
	 */
	public OrthographicCamera getCamera() {
		if (camera == null) {
			throw new IllegalStateException("getCamera() called out of sequence");
		}
		return camera;
	}
}
