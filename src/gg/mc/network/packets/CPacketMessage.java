package gg.mc.network.packets;

public class CPacketMessage extends Packet {
	public static final int PACKET_LENGTH = 65;
	private String message;

	public CPacketMessage(byte[] payload) {
		super((byte) 0xd, payload);

		byte[] buff_message = new byte[64];
		System.arraycopy(payload, 1, buff_message, 0, buff_message.length);

		this.message = Packet.getString(buff_message);
	}

	public String getMessage() {
		return message;
	}
}
