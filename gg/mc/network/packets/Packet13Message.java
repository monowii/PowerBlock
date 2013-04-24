package gg.mc.network.packets;

public class Packet13Message extends Packet {

	private String message;
	
	public Packet13Message(byte[] payload) {
		super((byte) 0x0d, payload);
		byte[] msg = new byte[64];
		// Start with offset of 1 to ignore Unused Byte
		for (int i = 1; i < 65; i++) {
			msg[i - 1] = payload[i];
		}
		this.message = Packet.getString(msg);
	}
	
	public Packet13Message(String message) {
		this(Packet.getBytes(message));
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}
}
