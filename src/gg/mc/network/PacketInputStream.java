package gg.mc.network;

import gg.mc.network.packets.*;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class PacketInputStream {

	private DataInputStream inputStream;
	private byte currentPacketId = (byte) -1;
	private byte[] payload;

	public PacketInputStream(InputStream inputStream) {
		this.inputStream = new DataInputStream(inputStream);
	}

	public static int getPacketLength(byte packetId) {
		int waitingLength = -1;
		switch (packetId) {
			case 0x0:
				waitingLength = CPacketPlayerIdentification.PACKET_LENGTH;
				break;
			case 0x5:
				waitingLength = CPacketSetBlock.PACKET_LENGTH;
				break;
			case 0x8:
				waitingLength = CPacketPositionOrientation.PACKET_LENGTH;
				break;
			case 0xd:
				waitingLength = CPacketMessage.PACKET_LENGTH;
				break;
		}
		return waitingLength;
	}

	public boolean hasPacket() throws IOException {
		if (currentPacketId == -1) {
			if (inputStream.available() > 0) {
				currentPacketId = inputStream.readByte();
			} else {
				return false;
			}
		}
		if (inputStream.available() >= getPacketLength(currentPacketId)) {
			return true;
		}
		return false;
	}

	public Packet nextPacket() throws IOException {
		while (!hasPacket()) {
			try {
				Thread.sleep(10);
			} catch (Exception ex) {
			}
		}
		payload = new byte[getPacketLength(currentPacketId)];
		inputStream.read(payload);
		Packet packet = null;
		switch (currentPacketId) {
			case 0x0:
				packet = new CPacketPlayerIdentification(payload);
				break;
			case 0x5:
				packet = new CPacketSetBlock(payload);
				break;
			case 0x8:
				packet = new CPacketPositionOrientation(payload);
				break;
			case 0xd:
				packet = new CPacketMessage(payload);
				break;
		}
		currentPacketId = -1;
		return packet;
	}
}
