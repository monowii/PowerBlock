package gg.mc;

import java.io.*;
import java.util.Properties;

public class Configuration {
	public static String CONFIG_FILE = System.getProperty("user.dir") + File.separator + "server.properties";

	private String serverName = "PowerBlockFork Server";
	private String serverMotd = "Building is fun!";
	private int serverPort = 25565;
	private int serverMaxPlayers = 10;
	private boolean serverVerifyUsername = true;
	private boolean heartbeatEnabled = true;
	private String heartbeatUrl = "https://classicube.net/heartbeat.jsp?";
	private boolean heartbeatIsPublic = false;
	private boolean heartbeatAllowWebClient = false;

	private Properties properties = new Properties();

	public Configuration() {
		load();
	}

	public void load() {
		try {
			FileInputStream fis = new FileInputStream(CONFIG_FILE);
			properties.load(fis);
		} catch (FileNotFoundException e) {
			System.out.println("Default config file not found, generating defaults");
		} catch (IOException e) {
			e.printStackTrace();
		}

		properties.putIfAbsent("server.name", serverName);
		properties.putIfAbsent("server.motd", serverMotd);
		properties.putIfAbsent("server.port", String.valueOf(serverPort));
		properties.putIfAbsent("server.maxPlayers", String.valueOf(serverMaxPlayers));
		properties.putIfAbsent("server.verifyUsername", String.valueOf(serverVerifyUsername));
		properties.putIfAbsent("heartbeat.enabled", String.valueOf(heartbeatEnabled));
		properties.putIfAbsent("heartbeat.url", heartbeatUrl);
		properties.putIfAbsent("heartbeat.isPublic", String.valueOf(heartbeatIsPublic));
		properties.putIfAbsent("heartbeat.allowWebClient", String.valueOf(heartbeatAllowWebClient));

		serverName = properties.getProperty("server.name");
		serverMotd = properties.getProperty("server.motd");
		serverPort = Integer.valueOf(properties.getProperty("server.port"));
		serverMaxPlayers = Integer.valueOf(properties.getProperty("server.maxPlayers"));
		serverVerifyUsername = Boolean.valueOf(properties.getProperty("server.verifyUsername"));
		heartbeatEnabled = Boolean.valueOf(properties.getProperty("heartbeat.enabled"));
		heartbeatUrl = properties.getProperty("heartbeat.url");
		heartbeatIsPublic = Boolean.valueOf(properties.getProperty("heartbeat.isPublic"));
		heartbeatAllowWebClient = Boolean.valueOf(properties.getProperty("heartbeat.allowWebClient"));
	}

	public void save() {
		try {
			FileOutputStream fos = new FileOutputStream(CONFIG_FILE);
			properties.store(fos, "Here you can't place raw : or =, you must escape them with \\: or \\=, see https://docs.oracle.com/javase/7/docs/api/java/util/Properties.html#load(java.io.Reader)");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public String getServerMotd() {
		return serverMotd;
	}

	public void setServerMotd(String serverMotd) {
		this.serverMotd = serverMotd;
	}

	public int getServerPort() {
		return serverPort;
	}

	public int getServerMaxPlayers() {
		return serverMaxPlayers;
	}

	public void setServerMaxPlayers(int serverMaxPlayers) {
		this.serverMaxPlayers = serverMaxPlayers;
	}

	public boolean isServerVerifyUsername() {
		return serverVerifyUsername;
	}

	public void setServerVerifyUsername(boolean serverVerifyUsername) {
		this.serverVerifyUsername = serverVerifyUsername;
	}

	public boolean isHeartbeatEnabled() {
		return heartbeatEnabled;
	}

	public void setHeartbeatEnabled(boolean heartbeatEnabled) {
		this.heartbeatEnabled = heartbeatEnabled;
	}

	public String getHeartbeatUrl() {
		return heartbeatUrl;
	}

	public void setHeartbeatUrl(String heartbeatUrl) {
		this.heartbeatUrl = heartbeatUrl;
	}

	public boolean isPublic() {
		return heartbeatIsPublic;
	}

	public void setPublic(boolean heartbeatIsPublic) {
		this.heartbeatIsPublic = heartbeatIsPublic;
	}

	public boolean isAllowWebClient() {
		return heartbeatAllowWebClient;
	}

	public void setAllowWebClient(boolean heartbeatAllowWebClient) {
		this.heartbeatAllowWebClient = heartbeatAllowWebClient;
	}
}
