package net.simplebroadcast.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import net.simplebroadcast.broadcasts.Broadcast;
import net.simplebroadcast.broadcasts.BroadcastStatus;

public class PlayerJoinListener implements Listener {
	
	/*
	 * @see net.simplebroadcast.broadcasts.Broadcast
	 */
	private Broadcast broadcast = new Broadcast();
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		if (broadcast.getChatBroadcastStatus() == BroadcastStatus.WAITING) {
			broadcast.setChatBroadcastStatus(BroadcastStatus.STOPPED);
			broadcast.broadcast();
		}
	}
}