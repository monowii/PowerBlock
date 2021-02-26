package gg.mc.network.packets;

import java.nio.ByteBuffer;

public class SPacketLevelFinalize extends Packet {
	public SPacketLevelFinalize(short width, short height, short depth) {
		super((byte) 0x4);

		ByteBuffer bb = ByteBuffer.allocate(6);
		bb.putShort(width);
		bb.putShort(height);
		bb.putShort(depth);
		this.payload = bb.array();
	}
}
