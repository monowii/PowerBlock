package gg.mc.network.packets;

import java.nio.ByteBuffer;

public class SPacketSetBlock extends Packet {
	public SPacketSetBlock(short x, short y, short z, byte block) {
		super((byte) 0x6);

		ByteBuffer bb = ByteBuffer.allocate(7);
		bb.putShort(x);
		bb.putShort(y);
		bb.putShort(z);
		bb.put(block);
		this.payload = bb.array();
	}
}
