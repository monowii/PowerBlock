package gg.mc.network.packets;

public class SPacketDespawnPlayer extends Packet {
	public SPacketDespawnPlayer(byte playerId) {
		super((byte) 0xc);

		this.payload = new byte[]{playerId};
	}
}
