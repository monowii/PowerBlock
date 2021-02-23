package gg.mc.events;

import gg.mc.ChatColor;
import gg.mc.Player;

public class PlayerChatEvent extends Event {

	private Player player;
	private String message;
	private String format;
	
	public PlayerChatEvent(Player player, String message) {
		this.player = player;
		this.message = message;
		StringBuilder sb = new StringBuilder();
		sb.append(ChatColor.YELLOW + player.getUsername());
		sb.append(ChatColor.WHITE + ": ");
		sb.append(message);
		this.format = sb.toString();
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public String getMessage() {
		return message;
	}
	
	public String getFormat() {
		return format;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public void setFormat(String format) {
		this.format = format;
	}
}
