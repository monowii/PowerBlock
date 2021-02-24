package gg.mc;

import gg.mc.plugin.Plugin;

import java.util.ArrayList;

public class Scheduler {

	private ArrayList<Task> queue = new ArrayList<>();

	public void queue(Plugin plugin, String functionName, long timeToWait) {
		synchronized (queue) {
			queue.add(new Task(plugin, functionName, timeToWait));
		}
	}

	public ArrayList<Task> checkFunctions() {
		ArrayList<Task> remove = new ArrayList<>();
		synchronized (queue) {
			for (int i = 0; i < queue.size(); i++) {
				Task t = queue.get(i);
				if (System.currentTimeMillis() - t.getTimeToCall() < 0) {
					remove.add(t);
				}
			}
			for (int i = 0; i < remove.size(); i++) {
				queue.remove(remove.get(i));
			}
		}
		return remove;
	}
}

class Task {

	long created;
	long call;
	private Plugin plugin;
	private String funcName;

	public Task(Plugin plugin, String functionName, long toWait) {
		this.plugin = plugin;
		this.funcName = functionName;
		created = System.currentTimeMillis();
		call = created + toWait;
	}

	public Plugin getPlugin() {
		return plugin;
	}

	public String getFunctionName() {
		return funcName;
	}

	public long getTimeToCall() {
		return call;
	}
}
