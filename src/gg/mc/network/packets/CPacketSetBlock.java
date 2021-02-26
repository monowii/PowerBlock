package gg.mc.network.packets;

import java.nio.ByteBuffer;

public class CPacketSetBlock extends Packet {
	public static int PACKET_LENGTH = 8;
	private short x;
	private short y;
	private short z;
	private byte mode;
	private byte blockType;

	public CPacketSetBlock(byte[] payload) {
		super((byte) 0x5, payload);

		ByteBuffer bb = ByteBuffer.wrap(payload, 0, 6);
		this.x = bb.getShort();
		this.y = bb.getShort();
		this.z = bb.getShort();
		this.mode = payload[6];
		this.blockType = payload[7];
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

	public byte getMode() {
		return mode;
	}

	public byte getBlockType() {
		return blockType;
	}
}