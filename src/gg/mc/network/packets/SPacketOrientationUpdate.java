package gg.mc.network.packets;

public class SPacketOrientationUpdate extends Packet {
	public SPacketOrientationUpdate(byte playerId, byte yaw, byte pitch) {
		super((byte) 0xb);

		this.payload = new byte[3];

		this.payload[0] = playerId;
		this.payload[1] = yaw;
		this.payload[2] = pitch;
	}
}
