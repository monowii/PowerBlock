package gg.mc.network.packets;

import java.nio.ByteBuffer;

public class SPacketServerIdentification extends Packet {
	public SPacketServerIdentification(byte protocolVersion, String name, String motd, byte userType) {
		super((byte) 0x0);

		ByteBuffer bb = ByteBuffer.allocate(130);
		bb.put(protocolVersion);
		bb.put(Packet.getBytes(name));
		bb.put(Packet.getBytes(motd));
		bb.put(userType);
		this.payload = bb.array();
	}
}
