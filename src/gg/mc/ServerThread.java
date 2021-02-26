package gg.mc;

import gg.mc.network.ConnectionThread;
import gg.mc.network.packets.SPacketPing;

import java.util.ArrayList;
import java.util.logging.Logger;

public class ServerThread extends Thread {

	private ConnectionThread connectionThread;
	private ArrayList<String> consoleCommandsLeft = new ArrayList<>();
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
				Player[] players = PowerBlock.getServer().getPlayers();
				boolean isPingTick = ticks % 40 == 0;
				for (int i = 0; i < players.length; i++) {
					players[i].tick();
					if (isPingTick) {
						players[i].push(new SPacketPing());
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
						} else if (cmd.equalsIgnoreCase("save")) {
							Logger.getGlobal().info("Saving all loaded worlds");
							PowerBlock.getServer().getWorldManager().saveAllWorlds();
							break;
						} else if (cmd.equalsIgnoreCase("reload")) {
							Logger.getGlobal().info("Reloading plugins");
							PowerBlock.getServer().getPluginManager().unload();
							PowerBlock.getServer().getPluginManager().load();
							break;
						} else if (cmd.equalsIgnoreCase("players")) {
							String[] names = new String[players.length];
							for (int j = 0; j < names.length; ++j) {
								names[j] = players[j].getUsername();
							}
							System.out.println("Players connected: " + String.join(", ", names));
							break;
						} else if (cmd.equalsIgnoreCase("say") && cmdArgs.length > 0) {
							PowerBlock.getServer().broadcastMessage(ChatColor.YELLOW + "[SERVER] " + ChatColor.WHITE + consoleCommandsLeft.get(i).substring(cmd.length()));
							break;
						} else if (cmd.equalsIgnoreCase("help")) {
							System.out.println("Console commands: stop, save, reload, players, say <message>");
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
		} catch (InterruptedException ex) {
			// Server is shutting down
		} catch (Exception ex) {
			ex.printStackTrace();
			Logger.getGlobal().warning("A fatal error occurred - please restart your server and report this to dreadiscool!");
		}
	}

	public void dispatchCommand(String command) {
		synchronized (consoleCommandsLeft) {
			consoleCommandsLeft.add(command);
		}
	}
}
