package gg.mc.network.packets;

public class SPacketPositionUpdate extends Packet {
	public SPacketPositionUpdate(byte playerId, byte dx, byte dy, byte dz) {
		super((byte) 0xa);

		this.payload = new byte[4];

		this.payload[0] = playerId;
		this.payload[1] = dx;
		this.payload[2] = dy;
		this.payload[3] = dz;
	}
}
