package gg.mc;

import gg.mc.events.*;
import gg.mc.events.PlayerKickEvent.Reason;
import gg.mc.network.ConnectionThread;
import gg.mc.network.PacketInputStream;
import gg.mc.network.PacketOutputStream;
import gg.mc.network.packets.*;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.logging.Logger;
import java.util.zip.GZIPOutputStream;

public class Player {

	private ConnectionThread connectionThread;
	private String inetAddress;
	private PacketInputStream packetInputStream;
	private PacketOutputStream packetOutputStream;

	private String username;
	private boolean loggedIn = false;
	// In the future, just remove them. This is used to hide logged off spam.
	private boolean disconnected = false;

	private byte entityId;
	private World world;
	private Position position = new Position(0, 0, 0, (byte) 0, (byte) 0);

	public Player(ConnectionThread connectionThread, Socket socket) throws IOException {
		this.connectionThread = connectionThread;
		this.packetInputStream = new PacketInputStream(socket.getInputStream());
		this.packetOutputStream = new PacketOutputStream(socket.getOutputStream());
		this.inetAddress = socket.getRemoteSocketAddress().toString();
	}

	public void tick() {
		try {
			if (!packetInputStream.hasPacket()) {
				return;
			}
			Packet incoming = packetInputStream.nextPacket();

			if (!loggedIn) {
				if (!(incoming instanceof CPacketPlayerIdentification)) {
					kick("Must send identification packet, smart one", Reason.LOST_CONNECTION);
					return;
				}
				CPacketPlayerIdentification ident = (CPacketPlayerIdentification) incoming;
				MessageDigest md5 = MessageDigest.getInstance("MD5");
				byte[] token = (connectionThread.getSalt() + ident.getUsername()).getBytes(StandardCharsets.UTF_8);
				md5.update(token);
				String verificationToken = new BigInteger(1, md5.digest()).toString(16);
				if (!verificationToken.equals(ident.getVerificationKey()) && PowerBlock.getServer().getConfiguration().isVerifyUsername()) {
					kick("Failed to verify username!", Reason.LOST_CONNECTION);
					return;
				}
				username = ident.getUsername().trim();
				loggedIn = true;
				Configuration config = PowerBlock.getServer().getConfiguration();
				packetOutputStream.writePacket(new SPacketServerIdentification((byte) 7, config.getServerName(), config.getMotd(), (byte) 0x0));
				connectionThread.addPlayer(this);
				sendWorld(PowerBlock.getServer().getWorldManager().getMainWorld());

				// Event
				PlayerLoginEvent e = new PlayerLoginEvent(this);
				PowerBlock.getServer().getPluginManager().callEvent(e);
				if (e.getJoinMessage() != null) {
					PowerBlock.getServer().broadcastMessage(e.getJoinMessage());
				}

				if (ident.getPayload()[ident.getPayload().length - 1] == 0x42) {
					Logger.getGlobal().info(ident.getUsername() + " has Classic Protocol Extension enabled, ignoring it");
				}
				return;
			}

			if (incoming instanceof CPacketSetBlock) {
				CPacketSetBlock packet = (CPacketSetBlock) incoming;

				Position pos = new Position(packet.getX(), packet.getY(), packet.getZ());
				byte b1 = getWorld().getBlockAt(packet.getX(), packet.getY(), packet.getZ());
				if (packet.getMode() == 0x1) {
					// Event
					BlockPlaceEvent e = new BlockPlaceEvent(this, pos, packet.getBlockType());
					PowerBlock.getServer().getPluginManager().callEvent(e);
					if (e.isCancelled()) {
						packetOutputStream.writePacket(new SPacketSetBlock(packet.getX(), packet.getY(), packet.getZ(), b1));
						return;
					}
					getWorld().setBlockAt(e.getPosition(), packet.getBlockType());
				} else {
					// Event
					BlockBreakEvent e = new BlockBreakEvent(this, pos, b1, packet.getBlockType());
					PowerBlock.getServer().getPluginManager().callEvent(e);
					if (e.isCancelled()) {
						packetOutputStream.writePacket(new SPacketSetBlock(packet.getX(), packet.getY(), packet.getZ(), b1));
						return;
					}
					getWorld().setBlockAt(e.getPosition(), Block.Air);
				}
			} else if (incoming instanceof CPacketPositionOrientation) {
				CPacketPositionOrientation packet = (CPacketPositionOrientation) incoming;

				boolean positionUpdated = false;
				if (position.getX() != packet.getX() || position.getY() != packet.getY() || position.getZ() != packet.getZ()) {
					positionUpdated = true;
				}

				boolean orientationUpdated = false;
				if (position.getPitch() != packet.getPitch() || position.getYaw() != packet.getYaw()) {
					orientationUpdated = true;
				}

				if (!positionUpdated && !orientationUpdated) {
					return;
				}

				// Event
				PlayerMoveEvent e = new PlayerMoveEvent(this, position, packet.getX(), packet.getY(), packet.getZ(), packet.getYaw(), packet.getPitch());
				PowerBlock.getServer().getPluginManager().callEvent(e);
				if (e.isCancelled()) {
					packetOutputStream.writePacket(new SPacketPlayerTeleport((byte) -1, position.getX(), position.getY(), position.getZ(), position.getYaw(), position.getPitch()));
					return;
				}

				// Update pos to all
				byte dx = (byte) (packet.getX() - position.getX());
				byte dy = (byte) (packet.getY() - position.getY());
				byte dz = (byte) (packet.getZ() - position.getZ());
				this.position = new Position(packet.getX(), packet.getY(), packet.getZ(), packet.getYaw(), packet.getPitch());

				if (positionUpdated && !orientationUpdated) {
					world.broadcastWorldPacket(new SPacketPositionUpdate(entityId, dx, dy, dz), this);
				} else if (orientationUpdated && !positionUpdated) {
					world.broadcastWorldPacket(new SPacketOrientationUpdate(entityId, position.getYaw(), position.getPitch()), this);
				} else {
					world.broadcastWorldPacket(new SPacketPlayerTeleport(entityId, position.getX(), position.getY(), position.getZ(), position.getYaw(), position.getPitch()), this);
				}

			} else if (incoming instanceof CPacketMessage) {
				CPacketMessage packet = (CPacketMessage) incoming;

				if (packet.getMessage().startsWith("/")) {
					String[] rawStuff = packet.getMessage().substring(1).split(" ");
					String cmd = rawStuff[0];
					String[] cmdArgs = new String[rawStuff.length - 1];
					System.arraycopy(rawStuff, 1, cmdArgs, 0, rawStuff.length - 1);
					Logger.getGlobal().info("[CMD] " + getUsername() + ": " + packet.getMessage());
					PowerBlock.getServer().getPluginManager().callPlayerCommand(this, cmd, cmdArgs);
				} else {
					// Event
					PlayerChatEvent e = new PlayerChatEvent(this, packet.getMessage());
					PowerBlock.getServer().getPluginManager().callEvent(e);
					if (!e.isCancelled()) {
						PowerBlock.getServer().broadcastMessage(e.getFormat());
					}
				}
			}

		} catch (Exception ex) {
			kick(ex.getMessage(), Reason.LOST_CONNECTION);
		}
	}

	public void sendWorld(World world) {
		try {
			if (this.world != null) {
				world.reclaimEid(entityId);
			}
			byte[] worldData = world.getWorldData();
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			GZIPOutputStream gos = new GZIPOutputStream(bos);
			DataOutputStream dos = new DataOutputStream(gos);

			dos.writeInt(worldData.length);
			dos.write(worldData);
			dos.close();
			gos.close();
			byte[] gzip = bos.toByteArray();
			bos.close();

			packetOutputStream.writePacket(new SPacketLevelInitialize());

			int position = 0;
			int length;
			int percent;
			byte[] buffer = new byte[1024];
			while (position != gzip.length) {
				length = Math.min(gzip.length - position, 1024);
				System.arraycopy(gzip, position, buffer, 0, length);
				percent = (int) (((double) (position + length) / (double) gzip.length) * 100);
				packetOutputStream.writePacket(new SPacketLevelDataChunk((short) length, buffer, (byte) percent));
				position += length;
			}

			packetOutputStream.writePacket(new SPacketLevelFinalize(world.getLength(), world.getHeight(), world.getDepth()));
			Position spawn = world.getSpawn();
			teleport(spawn);

			this.entityId = world.requestEntityId();
			world.broadcastWorldPacket(new SPacketSpawnPlayer(entityId, getUsername(), spawn.getX(), spawn.getY(), spawn.getZ(), spawn.getYaw(), spawn.getPitch()), this);
			this.push(new SPacketSpawnPlayer((byte) -1, getUsername(), spawn.getX(), spawn.getY(), spawn.getZ(), spawn.getYaw(), spawn.getPitch()));
			Player[] players = PowerBlock.getServer().getPlayers();
			for (int i = 0; i < players.length; i++) {
				if (players[i].getWorld() == world) {
					Position p = players[i].getPosition();
					packetOutputStream.writePacket(new SPacketSpawnPlayer(players[i].getEntityId(), players[i].getUsername(), p.getX(), p.getY(), p.getZ(), p.getYaw(), p.getPitch()));
				}
			}
			this.world = world;
			this.position = spawn;
		} catch (Exception ex) {
			kick(ex.getMessage(), Reason.LOST_CONNECTION);
		}
	}

	public void sendMessage(String message) {
		if (disconnected || !loggedIn) {
			return;
		}
		try {
			packetOutputStream.writePacket(new SPacketMessage(message));
		} catch (Exception ex) {
			kick(ex.getMessage(), Reason.LOST_CONNECTION);
		}
	}

	public void kick(String message) {
		kick(message, Reason.PLUGIN_KICK);
	}

	public void kick(String message, Reason reason) {
		if (reason != Reason.LOST_CONNECTION) {
			if (loggedIn && !disconnected) {
				PlayerKickEvent e = new PlayerKickEvent("Server", this, message);
				PowerBlock.getServer().getPluginManager().callEvent(e);
				if (e.isCancelled()) {
					return;
				}
				if (e.getReason() == null) {
					e.setReason("You were kicked from the server!");
				}
				message = e.getReason();
			}
		}
		try {
			packetOutputStream.writePacket(new SPacketDisconnectPlayer(message));
		} catch (Exception ex) {
			// Well hell, they were getting kicked anyway
		}
		disconnected = true;
		if (loggedIn) {
			PlayerQuitEvent ev = new PlayerQuitEvent(this);
			PowerBlock.getServer().getPluginManager().callEvent(ev);
			if (ev.getQuitMessage() != null) {
				PowerBlock.getServer().broadcastMessage(ev.getQuitMessage());
			}
		}

		connectionThread.removePlayer(this);

		if (world != null) {
			world.reclaimEid(entityId);
		}
	}

	public void teleport(Position pos) {
		push(new SPacketPlayerTeleport(pos));
	}

	public void push(Packet pack) {
		try {
			packetOutputStream.writePacket(pack);
		} catch (IOException ex) {
			kick(ex.getMessage(), Reason.LOST_CONNECTION);
		}
	}

	public World getWorld() {
		return world;
	}

	public String getUsername() {
		return username;
	}

	public String getInetAddress() {
		return inetAddress;
	}

	public byte getEntityId() {
		return entityId;
	}

	public Position getPosition() {
		return position;
	}
}
