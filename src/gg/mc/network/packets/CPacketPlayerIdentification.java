package gg.mc.network.packets;

public class CPacketPlayerIdentification extends Packet {
	public static final int PACKET_LENGTH = 130;
	private byte protocolVersion;
	private String username;
	private String verificationKey;

	public CPacketPlayerIdentification(byte[] payload) {
		super((byte) 0x0, payload);

		byte[] buff_username = new byte[64];
		byte[] buff_verification = new byte[64];
		System.arraycopy(payload, 1, buff_username, 0, buff_username.length);
		System.arraycopy(payload, 65, buff_verification, 0, buff_verification.length);

		this.protocolVersion = payload[0];
		this.username = Packet.getString(buff_username);
		this.verificationKey = Packet.getString(buff_verification);
	}

	public byte getProtocolVersion() {
		return protocolVersion;
	}

	public String getUsername() {
		return username;
	}

	public String getVerificationKey() {
		return verificationKey;
	}
}