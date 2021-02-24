package gg.mc.network.packets;

public class Packet4Finalize extends Packet {

	public Packet4Finalize(short width, short height, short depth) {
		super((byte) 0x04);
		byte[] w = Packet.getBytes(width);
		byte[] h = Packet.getBytes(height);
		byte[] d = Packet.getBytes(depth);
		this.payload = new byte[6];
		System.arraycopy(w, 0, this.payload, 0, 2);
		System.arraycopy(h, 0, this.payload, 2, 2);
		System.arraycopy(d, 0, this.payload, 4, 2);
	}
}
