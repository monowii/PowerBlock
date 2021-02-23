package gg.mc;

import java.util.ArrayList;
import java.util.logging.Logger;

import gg.mc.network.ConnectionThread;
import gg.mc.network.packets.Packet1Ping;

public class ServerThread extends Thread {

	private ConnectionThread connectionThread;
	private ArrayList<String> consoleCommandsLeft = new ArrayList<String>();
	private long ticks = 0;
	
	public ServerThread(ConnectionThread connectionThread) {
		super("PowerBlock Server Thread");
		this.connectionThread = connectionThread;
	}
	
	@Override
	public void run() {
		PowerBlock.getServer().getPluginManager().load();
		try {
			while (true) {
				connectionThread.tickLogin();
				Player[] players = PowerBlock.getServer().getOnlinePlayers();
				boolean isPingTick = ticks % 40 == 0;
				for (int i = 0; i < players.length; i++) {
					players[i].tick();
					if (isPingTick) {
						players[i].push(new Packet1Ping());
					}
				}
				
				// Scheduler
				ArrayList<Task> tasks = PowerBlock.getServer().getScheduler().checkFunctions();
				for (int i = 0; i < tasks.size(); i++) {
					Task t = tasks.get(i);
					t.getPlugin().callFunction(t.getFunctionName());
				}
				
				// Console commands. Do it here to keep all script calls on same thread
				synchronized (consoleCommandsLeft) {
					for (int i = 0; i < consoleCommandsLeft.size(); i++) {
						String[] cmdRaw = consoleCommandsLeft.get(i).split(" ");
						String cmd = cmdRaw[0];
						String[] cmdArgs = new String[cmdRaw.length - 1];
						System.arraycopy(cmdRaw, 1, cmdArgs, 0, cmdArgs.length);
						if (cmd.equalsIgnoreCase("stop")) {
							PowerBlock.getServer().stop();
							break;
						} else if (cmd.equalsIgnoreCase("reload")) {
							Logger.getGlobal().info("Reloading plugins");
							PowerBlock.getServer().getPluginManager().unload();
							PowerBlock.getServer().getPluginManager().load();
							break;
						} else if (cmd.equalsIgnoreCase("say") && cmdArgs.length > 0) {
							PowerBlock.getServer().broadcastMessage(ChatColor.YELLOW + "[SERVER] " + ChatColor.WHITE + consoleCommandsLeft.get(i).substring(cmd.length()));
							break;
						} else if (cmd.equalsIgnoreCase("help")) {
							System.out.println("console commands: stop, reload, say <message>");
							break;
						} else if (cmd.equalsIgnoreCase("players")) {
							String players_name = "";
							for (Player p : players) {
								players_name += p.getUsername() + ", ";
							}
							System.out.println("player list: " + players_name);
							break;
						}
						PowerBlock.getServer().getPluginManager().callConsoleCommand(cmd, cmdArgs);
					}
					consoleCommandsLeft.clear();
				}
				
				// Let's not kill the processor
				Thread.sleep(25);
				ticks++;
			}
		}
		catch (InterruptedException ex) {
			// Server is shutting down
		}
		catch (Exception ex) {
			ex.printStackTrace();
			Logger.getGlobal().warning("A fatal error occurred - please restart your server and report this to dreadiscool!");
			PowerBlock.getServer().getPluginManager().unload();
			run();
		}
	}
	
	public void dispatchCommand(String command) {
		synchronized (consoleCommandsLeft) {
			consoleCommandsLeft.add(command);
		}
	}
}
