package net.simplebroadcast.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import net.simplebroadcast.broadcasts.Broadcast;
import net.simplebroadcast.broadcasts.BroadcastStatus;

public class PlayerJoinListener implements Listener {
	
	/*
	 * (non-Javadoc)
	 * @see net.simplebroadcast.broadcasts.Broadcast
	 */
	private Broadcast broadcast = new Broadcast();
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		/* Checks if chat broadcast status is not disabled. */
		if (Broadcast.getChatBroadcastStatus() == BroadcastStatus.DISABLED) {
			return;
		}
		/* TODO Comment */
		if (Broadcast.getChatBroadcastStatus() == BroadcastStatus.WAITING && Broadcast.getChatBroadcastStatus() != BroadcastStatus.DISABLED) {
			broadcast.broadcast();
		}
	}
}