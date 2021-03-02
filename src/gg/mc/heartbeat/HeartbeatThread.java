package gg.mc.heartbeat;

import gg.mc.Configuration;
import gg.mc.PowerBlock;
import gg.mc.events.HeartbeatEvent;
import gg.mc.exceptions.HeartbeatCancelledException;
import gg.mc.network.ConnectionThread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.logging.Logger;

public class HeartbeatThread extends Thread {

	private ConnectionThread connectionThread;
	private boolean hasSuccess = false;

	public HeartbeatThread(ConnectionThread connectionThread) {
		super("PowerBlock Heartbeat Thread");
		this.connectionThread = connectionThread;
	}

	@Override
	public void run() {
		try {
			while (true) {
				try {
					Configuration c = PowerBlock.getServer().getConfiguration();
					HeartbeatEvent e = new HeartbeatEvent(c.getServerPort(), c.getServerMaxPlayers(), c.getServerName(), c.isPublic(), PowerBlock.getServer().getPlayers().length, c.isAllowWebClient());
					PowerBlock.getServer().getPluginManager().callEvent(e);
					if (e.isCancelled()) {
						throw new HeartbeatCancelledException();
					}

					StringBuilder sb = new StringBuilder();
					sb.append(PowerBlock.getServer().getConfiguration().getHeartbeatUrl());
					sb.append("port=" + e.getPort());
					sb.append("&max=" + e.getMaxPlayers());
					sb.append("&name=" + e.getName());
					sb.append("&public=" + e.isPublic());
					sb.append("&version=7");
					sb.append("&salt=" + connectionThread.getSalt());
					sb.append("&users=" + e.getPlayerCount());
					sb.append("&software=PowerBlockFork");
					sb.append("&web=" + e.isAllowWebClient());
					URL url = new URL(sb.toString().replace(" ", "%20"));
					BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
					StringBuilder respo = new StringBuilder();
					String in = br.readLine();
					while (in != null) {
						respo.append(in);
						in = br.readLine();
					}
					br.close();
					if (!hasSuccess) {
						Logger.getGlobal().info("Heartbeat: Server says: " + respo.toString());
						hasSuccess = true;
					}
				} catch (IOException ex) {
					ex.printStackTrace();
					Logger.getGlobal().info("Failed to send heartbeat, is it down?");
				} catch (HeartbeatCancelledException ex) {
					// In the future print which plugin cancelled?
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				Thread.sleep(44000);
			}
		} catch (InterruptedException ex) {
			// Server shutting down
		}
	}
}
