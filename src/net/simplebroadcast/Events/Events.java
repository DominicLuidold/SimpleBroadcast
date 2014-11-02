package net.simplebroadcast.Events;

import net.simplebroadcast.Main;
import net.simplebroadcast.Methods.Methods;
import net.simplebroadcast.Methods.UpdatingMethods;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class Events implements Listener {

	private Methods mt = new Methods();
	private UpdatingMethods um = new UpdatingMethods();

	/*
	 * PlayerJoinEvent
	 */
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		/*
		 * Checks if any updates are available and notifies the player.
		 * (Asynchronous task)
		 */
		if (Main.plugin.getConfig().getBoolean("checkforupdates")) {
			final Player p = event.getPlayer();
			Bukkit.getScheduler().runTaskAsynchronously(Main.plugin, new Runnable() {
				@Override
				public void run() {
					try {
						if ((p.isOp() || p.hasPermission("simplebroadcast.update")) && um.updateB()) {
							p.sendMessage("[Simple§cBroadcast]§r An update is available: " + um.updateN());
							p.sendMessage("[Simple§cBroadcast]§r Please download it from the BukkitDev page.");
						}
					} catch (NullPointerException npe) {
						Main.plugin.logW("Couldn't check for updates.");
					}
				}
			});
		}
		/*
		 * Checks if server was empty before the player joined and starts the broadcast if it's not running yet.
		 */
		if (Main.plugin.getConfig().getBoolean("requiresonlineplayers")) {
			if (Bukkit.getOnlinePlayers().length == 1 && Main.running == 0) {
				mt.broadcast();
				Main.running = 1;
			}
		}
	}

	/*
	 * PlayerQuitEvent
	 */
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		/*
		 * Checks if server is empty after the player left and stops the broadcast if it's running.
		 */
		if (Main.plugin.getConfig().getBoolean("requiresonlineplayers") && Bukkit.getServer().getOnlinePlayers().length == 1) {
			Bukkit.getServer().getScheduler().cancelTask(Main.messageTask);
			Main.running = 0;
		}
	}

	/*
	 * AsyncPlayerChatEvent
	 */
	@EventHandler
	public void onEasteregg(AsyncPlayerChatEvent event) {
		/*
		 * Broadcasts the easter egg message to every player if someone types "SimpleBroadcast" in the chat.
		 */
		if (event.getMessage().toLowerCase().contains("simplebroadcast")) {
			Bukkit.broadcastMessage("[Simple§cBroadcast]§r He, he.. Thank you for using SimpleBroadcast! :D");
		}
	}

}