package gg.mc;

import gg.mc.events.PlayerKickEvent.Reason;
import gg.mc.exceptions.ServerRunningException;
import gg.mc.heartbeat.HeartbeatThread;
import gg.mc.network.ConnectionThread;
import gg.mc.plugin.PluginManager;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class PowerBlock {
	public static final String WORLDS_DIR = System.getProperty("user.dir") + File.separator + "worlds" + File.separator;
	public static final String PLUGINS_DIR = System.getProperty("user.dir") + File.separator + "plugins" + File.separator;
	public static final String LOGS_DIR = System.getProperty("user.dir") + File.separator + "logs" + File.separator;


	private static PowerBlock instance;
	private Configuration configuration = new Configuration();
	private Thread connectionThread = new ConnectionThread();
	private Thread serverThread = new ServerThread((ConnectionThread) connectionThread);
	private Thread heartbeatThread = new HeartbeatThread((ConnectionThread) connectionThread);
	private Thread shutdownThread;
	private WorldManager worldManager;
	private PluginManager pluginManager;
	private Scheduler scheduler;

	public static PowerBlock getServer() {
		return instance;
	}

	public static void main(String[] args) throws ServerRunningException {
		if (instance != null) {
			throw new ServerRunningException();
		}

		System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tF %1$tT] %5$s %n");

		File dirWorlds = new File(WORLDS_DIR);
		File dirPlugins = new File(PLUGINS_DIR);
		File dirLogs = new File(LOGS_DIR);
		if (!dirWorlds.exists()) {
			Logger.getGlobal().info("Creating directory /worlds");
			dirWorlds.mkdir();
		}
		if (!dirPlugins.exists()) {
			Logger.getGlobal().info("Creating directory /plugins");
			dirPlugins.mkdir();
		}
		if (!dirLogs.exists()) {
			Logger.getGlobal().info("Creating directory /logs");
			dirLogs.mkdir();
		}
		instance = new PowerBlock();
		instance.startServer();

		Scanner s = new Scanner(System.in);
		s.useDelimiter(System.getProperty("line.separator"));
		while (s.hasNextLine()) {
			((ServerThread) PowerBlock.getServer().serverThread).dispatchCommand(s.nextLine());
		}
		s.close();
	}

	private void startServer() {
		FileHandler fh;
		SimpleDateFormat format = new SimpleDateFormat("d-M-y_HH-mm-ss");
		final String file_path = LOGS_DIR + "log_" + format.format(Calendar.getInstance().getTime()) + ".log";
		try {
			fh = new FileHandler(file_path);
			Logger.getGlobal().addHandler(fh);
			SimpleFormatter sf = new SimpleFormatter();
			fh.setFormatter(sf);
		} catch (IOException e) {
			e.printStackTrace();
		}

		connectionThread.start();
		try {
			worldManager = new WorldManager();
		} catch (ServerRunningException ex) {
			ex.printStackTrace();
			System.exit(1);
		}
		if (worldManager.getTotalWorlds() == 0) {
			Logger.getGlobal().info("Generating default world...");
			worldManager.createWorld("main", 128, 128, 128);
		}
		pluginManager = new PluginManager();
		scheduler = new Scheduler();
		serverThread.start();
		if (configuration.isHearbeatEnabled()) {
			Logger.getGlobal().info("starting Heartbeat thread");
			heartbeatThread.start();
		}

		shutdownThread = new Thread(() -> PowerBlock.getServer().stop());

		Runtime.getRuntime().addShutdownHook(shutdownThread);
	}

	public void broadcastMessage(String message) {
		Player[] players = getPlayers();
		for (int i = 0; i < players.length; i++) {
			players[i].sendMessage(message);
		}
		Logger.getGlobal().info("[CHAT] " + message);
	}

	public void stop() {
		Runtime.getRuntime().removeShutdownHook(shutdownThread);
		Logger.getGlobal().info("Server shutting down...");
		worldManager.saveAllWorlds();
		Player[] players = getPlayers();
		for (int i = 0; i < players.length; i++) {
			players[i].kick("Server is shutting down!", Reason.SERVER_SHUTDOWN);
		}
		pluginManager.unload();
		connectionThread.interrupt();
		serverThread.interrupt();
		heartbeatThread.interrupt();
		Logger.getGlobal().info("Server stopped!");
		System.exit(0);
	}

	public Player getPlayer(String name) {
		return ((ConnectionThread) connectionThread).getPlayer(name);
	}

	public Player[] getPlayers() {
		return ((ConnectionThread) connectionThread).getPlayers();
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public WorldManager getWorldManager() {
		return worldManager;
	}

	public PluginManager getPluginManager() {
		return pluginManager;
	}

	public Scheduler getScheduler() {
		return scheduler;
	}
}
