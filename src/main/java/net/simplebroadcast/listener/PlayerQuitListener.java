package net.simplebroadcast.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import net.simplebroadcast.Main;
import net.simplebroadcast.broadcasts.Broadcast;
import net.simplebroadcast.broadcasts.BroadcastStatus;

public class PlayerQuitListener implements Listener {
	
	/*
	 * @see net.simplebroadcast.broadcasts.Broadcast
	 */
	private Broadcast broadcast = new Broadcast();
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		if (Main.getInstance().getConfig().getBoolean("chat.requireOnlinePlayers") && Bukkit.getServer().getOnlinePlayers().size() == 1) {
			broadcast.setChatBroadcastStatus(BroadcastStatus.WAITING);
			broadcast.broadcast();
		}
	}
}