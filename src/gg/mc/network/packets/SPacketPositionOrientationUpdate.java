package gg.mc.network.packets;

public class SPacketPositionOrientationUpdate extends Packet {
	public SPacketPositionOrientationUpdate(byte playerId, byte dx, byte dy, byte dz, byte yaw, byte pitch) {
		super((byte) 0x9);

		this.payload = new byte[6];

		this.payload[0] = playerId;
		this.payload[1] = dx;
		this.payload[2] = dy;
		this.payload[3] = dz;
		this.payload[4] = yaw;
		this.payload[5] = pitch;
	}
}
