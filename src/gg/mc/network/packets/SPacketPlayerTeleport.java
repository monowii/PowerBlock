package gg.mc.network.packets;

import gg.mc.Position;

import java.nio.ByteBuffer;

public class SPacketPlayerTeleport extends Packet {
	public SPacketPlayerTeleport(byte playerId, short x, short y, short z, byte yaw, byte pitch) {
		super((byte) 0x8);

		ByteBuffer bb = ByteBuffer.allocate(9);
		bb.put(playerId);
		bb.putShort(x);
		bb.putShort(y);
		bb.putShort(z);
		bb.put(yaw);
		bb.put(pitch);
		this.payload = bb.array();
	}

	public SPacketPlayerTeleport(Position pos) {
		this((byte) -1, pos.getX(), pos.getY(), pos.getZ(), pos.getYaw(), pos.getPitch());
	}
}
