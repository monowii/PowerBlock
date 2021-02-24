package gg.mc.network.packets;

public class Packet {

	protected byte header;
	protected byte[] payload;

	public Packet(byte header) {
		this.header = header;
	}

	public Packet(byte header, byte[] payload) {
		this(header);
		setPayload(payload);
	}

	protected static byte[] getBytes(short s) {
		return new byte[]{
				(byte) ((s >> 8) & 0xff),
				(byte) (s & 0xff)
		};
	}

	protected static byte[] getBytes(String s) {
		char[] chars = s.toCharArray();
		byte[] buff = new byte[64];
		for (int i = 0; i < 64; i++) {
			if (i < chars.length) {
				buff[i] = (byte) chars[i];
			} else {
				buff[i] = (byte) 0x00;
			}
		}
		return buff;
	}

	protected static String getString(byte[] payload) {
		return new String(payload).trim();
	}

	public byte[] getPayload() {
		return this.payload;
	}

	public void setPayload(byte[] payload) {
		this.payload = payload;
	}

	public byte[] getBytes() {
		byte[] buff = new byte[this.payload.length + 1];
		buff[0] = this.header;
		System.arraycopy(this.payload, 0, buff, 1, this.payload.length);
		return buff;
	}

	public byte getHeader() {
		return header;
	}
}
