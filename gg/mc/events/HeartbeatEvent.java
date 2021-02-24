package gg.mc.events;

public class HeartbeatEvent extends Event {

	private int port;
	private int maxPlayers;
	private String name;
	private boolean isPublic;
	private int onlinePlayers;
	private boolean allowWebClient;

	public HeartbeatEvent(int port, int maxPlayers, String name, boolean isPublic, int onlinePlayers, boolean allowWebClient) {
		this.port = port;
		this.maxPlayers = maxPlayers;
		this.name = name;
		this.isPublic = isPublic;
		this.onlinePlayers = onlinePlayers;
		this.allowWebClient = allowWebClient;
	}

	public int getPort() {
		return port;
	}

	public int getMaxPlayers() {
		return maxPlayers;
	}

	public void setMaxPlayers(int maxPlayers) {
		this.maxPlayers = maxPlayers;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isPublic() {
		return isPublic;
	}

	public int getPlayerCount() {
		return onlinePlayers;
	}

	public void setPlayerCount(int players) {
		this.onlinePlayers = players;
	}

	public void setIsPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}

	public boolean isAllowWebClient() {
		return allowWebClient;
	}

	public void setAllowWebClient(boolean allowWebClient) {
		this.allowWebClient = allowWebClient;
	}
}
