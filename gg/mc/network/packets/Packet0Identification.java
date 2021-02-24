package gg.mc.network.packets;

public class Packet0Identification extends Packet {

	private byte version;
	private String username;
	private String verification;

	/**
	 * Used for constructing OUTOING packets only
	 *
	 * @param version    The current protocol version
	 * @param serverName The name of the server
	 * @param motd       The server message of the day
	 * @param userType   The type of user: 0x64: op, 0x00: regular
	 */
	public Packet0Identification(byte version, String serverName, String motd, byte userType) {
		super((byte) 0x00);
		byte[] buff1 = Packet.getBytes(serverName);
		byte[] buff2 = Packet.getBytes(motd);
		this.payload = new byte[130];
		this.payload[0] = version;
		System.arraycopy(buff1, 0, this.payload, 1, 64);
		System.arraycopy(buff2, 0, this.payload, 65, 64);
		this.payload[129] = userType;
	}

	public Packet0Identification(byte[] payload) {
		super((byte) 0x00, payload);
		byte[] user = new byte[64];
		byte[] ver = new byte[64];
		this.version = payload[0];
		System.arraycopy(payload, 1, user, 0, 64);
		System.arraycopy(payload, 65, ver, 0, 64);
		this.username = Packet.getString(user);
		this.verification = Packet.getString(ver);
	}

	public byte getProtocolVersion() {
		return version;
	}

	public String getUsername() {
		return username;
	}

	public String getVerificationKey() {
		return verification;
	}
}
