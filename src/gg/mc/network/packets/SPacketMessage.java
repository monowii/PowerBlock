package gg.mc.network.packets;

import java.nio.ByteBuffer;

public class SPacketMessage extends Packet {
	public SPacketMessage(String message) {
		super((byte) 0xd);

		ByteBuffer bb = ByteBuffer.allocate(65);
		bb.put((byte) 0);
		bb.put(Packet.getBytes(message));
		this.payload = bb.array();
	}
}
