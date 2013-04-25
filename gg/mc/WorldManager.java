package gg.mc;

import gg.mc.exceptions.NoSuchWorldException;
import gg.mc.exceptions.ServerRunningException;
import gg.mc.exceptions.WorldExistsException;

import java.util.concurrent.ConcurrentHashMap;

public class WorldManager {

	public static final int FLATMAP = 0;
	public static final int EMPTY = 1;
	public static final int PIXEL_ART = 2;
	
	private static boolean alreadyStarted = false;
	
	private ConcurrentHashMap<String, World> worlds = new ConcurrentHashMap<String, World>();
	
	public WorldManager() throws ServerRunningException {
		if (alreadyStarted) {
			throw new ServerRunningException();
		}
		alreadyStarted = true;
	}
	
	public World getWorld(String name) {
		World w = worlds.get(name);
		if (w == null) {
			throw new NoSuchWorldException(name);
		}
		return w;
	}
	
	public void createWorld(String name, int length, int depth, int height) {
		createWorld(name, (short) length, (short) depth, (short) height);
	}
	
	public void createWorld(String name, short length, short depth, short height) {
		if (worlds.get(name) != null) {
			throw new WorldExistsException(name);
		}
		PowerBlock.getServer().broadcastMessage(ChatColor.DARK_PURPLE + "World generation beginning!");
		worlds.put(name, new World(name, length, depth, height));
		PowerBlock.getServer().broadcastMessage(ChatColor.DARK_PURPLE + "World generation finished");
	}
	
	public World getMainWorld() {
		return getWorld("world");
	}
	
	public int getTotalWorlds() {
		return worlds.size();
	}
}
