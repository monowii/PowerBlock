package gg.mc.network;

import gg.mc.Player;
import gg.mc.PowerBlock;
import gg.mc.events.PlayerKickEvent.Reason;
import gg.mc.exceptions.NoSuchPlayerException;
import gg.mc.network.packets.Packet;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class ConnectionThread extends Thread {

	private ServerSocket serverSocket;
	private ConcurrentHashMap<String, Player> clients = new ConcurrentHashMap<>();
	private ArrayList<Player> loginQueue = new ArrayList<>();
	private String salt;

	public ConnectionThread() {
		super("PowerBlock Connection Thread");
	}

	@Override
	public void run() {
		try {
			serverSocket = new ServerSocket(PowerBlock.getServer().getConfiguration().getServerPort());
			String alphabet = "abcdefghijklmnopqrstuvwxyz";
			Random random = new Random();
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < 16; i++) {
				sb.append(alphabet.charAt(random.nextInt(alphabet.length())));
			}
			this.salt = sb.toString();
		} catch (Exception ex) {
			Logger.getGlobal().warning("Failed to bind to port 25565 - Is it available? " + ex.getMessage());
			System.exit(1);
			return;
		}
		try {
			while (true) {
				Thread.sleep(1);
				Socket s = null;
				try {
					s = serverSocket.accept();
					Logger.getGlobal().info(s.getRemoteSocketAddress().toString() + " connected");
					synchronized (loginQueue) {
						loginQueue.add(new Player(this, s));
					}
				} catch (InterruptedIOException ex) {
					// Pass it off to lower exception handler
					throw new InterruptedException();
				} catch (IOException ex) {
					if (s != null) {
						Logger.getGlobal().info("Failed to handle connection from " + s.getRemoteSocketAddress().toString());
					}
				}
			}
		} catch (InterruptedException ex) {
			// Server is aborting
			try {
				// Exception inception
				serverSocket.close();
			} catch (IOException e) {
				Logger.getGlobal().info("Socket was already closed, wth, gypsy magic");
			}
		}
	}

	public void tickLogin() {
		synchronized (loginQueue) {
			for (int i = 0; i < loginQueue.size(); i++) {
				loginQueue.get(i).tick();
			}
		}
	}

	public void broadcastPacket(Packet packet) {
		Player[] players = getPlayers();
		for (int i = 0; i < players.length; i++) {
			players[i].push(packet);
		}
	}

	public Player[] getPlayers() {
		Collection<Player> cache = clients.values();
		Player[] players = new Player[cache.size()];
		int i = 0;
		Iterator<Player> iter = cache.iterator();
		while (iter.hasNext()) {
			players[i] = iter.next();
			i++;
		}
		return players;
	}

	public void removePlayer(Player p) {
		try {
			synchronized (loginQueue) {
				loginQueue.remove(p);
			}
			clients.remove(p.getUsername());
		} catch (Exception ex) {
			// Were never in, or already removed.
		}
	}

	public void addPlayer(Player p) {
		synchronized (loginQueue) {
			if (loginQueue.remove(p)) {
				if (clients.get(p.getUsername()) != null) {
					clients.get(p.getUsername()).kick("You logged in from another location!", Reason.LOST_CONNECTION);
					Logger.getGlobal().info(p.getUsername() + " logged from another location");
				}
				clients.put(p.getUsername(), p);
				Logger.getGlobal().info(p.getUsername() + " has logged in");
			} else {
				throw new NoSuchPlayerException();
			}
		}
	}

	public Player getPlayer(String name) {
		return clients.get(name);
	}

	public String getSalt() {
		return salt;
	}
}
