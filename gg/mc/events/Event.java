package gg.mc.events;

public class Event {

	protected boolean cancelled = false;

	public final boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean cancel) {
		cancelled = cancel;
	}
}
