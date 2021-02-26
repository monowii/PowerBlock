package gg.mc.network.packets;

import java.nio.ByteBuffer;

public class SPacketSpawnPlayer extends Packet {
	public SPacketSpawnPlayer(byte playerId, String playername, short x, short y, short z, byte yaw, byte pitch) {
		super((byte) 0x7);

		ByteBuffer bb = ByteBuffer.allocate(73);
		bb.put(playerId);
		bb.put(Packet.getBytes(playername));
		bb.putShort(x);
		bb.putShort(y);
		bb.putShort(z);
		bb.put(yaw);
		bb.put(pitch);
		this.payload = bb.array();
	}
}
