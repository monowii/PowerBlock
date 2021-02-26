package gg.mc.events;

import gg.mc.Player;
import gg.mc.Position;

public class BlockPlaceEvent extends Event {

	private Player player;
	private Position position;
	private byte block;

	public BlockPlaceEvent(Player player, Position pos, byte block) {
		this.player = player;
		this.position = pos;
		this.block = block;
	}

	public Player getPlayer() {
		return player;
	}

	public byte getBlockPlaced() {
		return block;
	}

	public Position getPosition() {
		return position;
	}
}
