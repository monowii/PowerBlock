package gg.mc.network.packets;

public class SPacketUpdateUserType extends Packet {
	public SPacketUpdateUserType(byte userType) {
		super((byte) 0xf);

		this.payload = new byte[]{userType};
	}
}
