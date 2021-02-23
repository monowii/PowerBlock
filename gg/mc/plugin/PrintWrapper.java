package gg.mc.plugin;

import java.io.PrintStream;
import java.util.logging.Logger;

public class PrintWrapper extends PrintStream {
	
	private Plugin plugin;
	
	public PrintWrapper(Plugin plugin, PrintStream printStream) {
		super(printStream);
		this.plugin = plugin;
	}
	
	@Override
	public void println(String stuff) {
		this.print(stuff + "\n");
	}
	
	@Override
	public void print(String stuff) {
		Logger.getGlobal().info("[" + plugin.getPluginName() + "] " + stuff);
	}
}
