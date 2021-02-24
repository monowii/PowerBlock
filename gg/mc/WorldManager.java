package gg.mc;

import gg.mc.exceptions.NoSuchWorldException;
import gg.mc.exceptions.ServerRunningException;
import gg.mc.exceptions.WorldExistsException;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class WorldManager {
	private static boolean alreadyStarted = false;

	private final ConcurrentHashMap<String, World> worlds = new ConcurrentHashMap<>();

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
		if (World.exists(name)) {
			Logger.getGlobal().info("World '" + name + "' already exists in hard drive, loading that instead!");
			worlds.put(name, new World(name));
			return;
		}
		PowerBlock.getServer().broadcastMessage(ChatColor.DARK_PURPLE + "World generation beginning!");
		worlds.put(name, new World(name, length, depth, height));
		PowerBlock.getServer().broadcastMessage(ChatColor.DARK_PURPLE + "World generation finished");
	}

	public void saveAllWorlds() {
		Collection<World> w = worlds.values();
		Iterator<World> iter = w.iterator();
		while (iter.hasNext()) {
			iter.next().save();
		}
	}

	public void unloadWorld(String name, boolean save) {
		if (save) {
			getWorld(name).save();
		}
		worlds.remove(name);
		Logger.getGlobal().info("Removed world '" + name + "'.");
	}

	public World getMainWorld() {
		return getWorld("world");
	}

	public int getTotalWorlds() {
		return worlds.size();
	}
}
