package gg.mc.network.packets;

import gg.mc.exceptions.InvalidChunkException;

import java.nio.ByteBuffer;

public class SPacketLevelDataChunk extends Packet {
	public SPacketLevelDataChunk(short length, byte[] data, byte percent) {
		super((byte) 0x3);
		if (data.length > 1024) {
			throw new InvalidChunkException();
		}

		ByteBuffer bb = ByteBuffer.allocate(data.length + 3);
		bb.putShort(length);
		bb.put(data);
		bb.put(percent);
		this.payload = bb.array();
	}
}
