package gg.mc.network.packets;

import java.nio.ByteBuffer;

public class CPacketPositionOrientation extends Packet {
	public static int PACKET_LENGTH = 9;
	private byte playerId;
	private short x;
	private short y;
	private short z;
	private byte yaw;
	private byte pitch;

	public CPacketPositionOrientation(byte[] payload) {
		super((byte) 0x8, payload);

		this.playerId = payload[0];
		ByteBuffer bb = ByteBuffer.wrap(payload, 1, 6);
		this.x = bb.getShort();
		this.y = bb.getShort();
		this.z = bb.getShort();
		this.yaw = payload[7];
		this.pitch = payload[8];
	}

	public byte getPlayerId() {
		return playerId;
	}

	public short getX() {
		return x;
	}

	public short getY() {
		return y;
	}

	public short getZ() {
		return z;
	}

	public byte getYaw() {
		return yaw;
	}

	public byte getPitch() {
		return pitch;
	}
}