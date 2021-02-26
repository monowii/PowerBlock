package gg.mc.network.packets;

public class SPacketDisconnectPlayer extends Packet {
	public SPacketDisconnectPlayer(String kickReason) {
		super((byte) 0xe);

		this.payload = Packet.getBytes(kickReason);
	}
}
