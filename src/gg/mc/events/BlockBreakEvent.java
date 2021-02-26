package gg.mc.events;

import gg.mc.Player;
import gg.mc.Position;

public class BlockBreakEvent extends Event {

	private Player p;
	private Position pos;
	private byte blockBroken;
	private byte blockInHand;

	public BlockBreakEvent(Player p, Position pos, byte broken, byte hand) {
		this.p = p;
		this.blockBroken = broken;
		this.blockInHand = hand;
		this.pos = pos;
	}

	public Player getPlayer() {
		return p;
	}

	public Position getPosition() {
		return pos;
	}

	public byte getBlockBroken() {
		return blockBroken;
	}

	public byte getBlockInHand() {
		return blockInHand;
	}
}
