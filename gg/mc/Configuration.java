package gg.mc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Scanner;
import java.util.logging.Logger;

public class Configuration {

	private String serverName = "PowerBlockFork Server";
	private int serverPort = 25565;
	private int maxPlayers = 10;
	private String motd = "Building is fun!";
	private boolean isPublic = false;
	private boolean allowWebClient = false;
	private String heartbeatUrl = "https://classicube.net/heartbeat.jsp?";
	private boolean hearbeatEnabled = true;
	private boolean verifyUsername = true;
	
	public Configuration() {
		try {
			Scanner s = new Scanner(new FileReader(new File(System.getProperty("user.dir") + File.separator + "server.properties")));
			s.useDelimiter(System.getProperty("line.separator"));
			while (s.hasNext()) {
				String[] params = s.next().split("=");
				if (params[0].equals("serverName")) {
					serverName = params[1];
				}
				else if (params[0].equals("serverPort")) {
					serverPort = Integer.parseInt(params[1]);
				}
				else if (params[0].equals("maxPlayers")) {
					maxPlayers = Integer.parseInt(params[1]);
				}
				else if (params[0].equals("motd")) {
					motd = params[1];
				}
				else if (params[0].equals("isPublic")) {
					isPublic = Boolean.parseBoolean(params[1]);
				}
				else if (params[0].equals("allowWebClient")) {
					allowWebClient = Boolean.parseBoolean(params[1]);
				}
				else if (params[0].equals("heartbeatUrl")) {
					heartbeatUrl = params[1];
				}
				else if (params[0].equals("hearbeatEnabled")) {
					hearbeatEnabled = Boolean.parseBoolean(params[1]);
				}
				else if (params[0].equals("verifyUsername")) {
					verifyUsername = Boolean.parseBoolean(params[1]);
				}
			}
			s.close();
		}
		catch (Exception ex) {
			Logger.getGlobal().info("No config!");
			generateConfig();
		}
	}
	
	private void generateConfig() {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(System.getProperty("user.dir") + File.separator + "server.properties")));
			String s = System.getProperty("line.separator");
			bw.write("serverName=" + serverName + s);
			bw.write("serverPort=" + serverPort + s);
			bw.write("maxPlayers=" + maxPlayers + s);
			bw.write("motd=" + motd + s);
			bw.write("isPublic=" + isPublic + s);
			bw.write("allowWebClient=" + allowWebClient + s);
			bw.write("heartbeatUrl=" + heartbeatUrl + s);
			bw.write("hearbeatEnabled=" + hearbeatEnabled + s);
			bw.write("verifyUsername=" + verifyUsername + s);
			bw.flush();
			bw.close();
		}
		catch (Exception ex) {
			Logger.getGlobal().info("Failed to generate config :-(");
			ex.printStackTrace();
		}
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public int getServerPort() {
		return serverPort;
	}

	public int getMaxPlayers() {
		return maxPlayers;
	}

	public void setMaxPlayers(int maxPlayers) {
		this.maxPlayers = maxPlayers;
	}

	public String getMotd() {
		return motd;
	}

	public void setMotd(String motd) {
		this.motd = motd;
	}

	public boolean isPublic() {
		return isPublic;
	}

	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}

	public boolean isAllowWebClient() {
		return allowWebClient;
	}

	public void setAllowWebClient(boolean allowWebClient) {
		this.allowWebClient = allowWebClient;
	}

	public String getHeartbeatUrl() {
		return heartbeatUrl;
	}

	public void setHeartbeatUrl(String heartbeatUrl) {
		this.heartbeatUrl = heartbeatUrl;
	}

	public boolean isHearbeatEnabled() {
		return hearbeatEnabled;
	}

	public boolean isVerifyUsername() {
		return verifyUsername;
	}

	public void setVerifyUsername(boolean verifyUsername) {
		this.verifyUsername = verifyUsername;
	}
	
}
