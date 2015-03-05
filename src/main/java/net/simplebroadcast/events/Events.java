package net.simplebroadcast.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import net.simplebroadcast.Main;
import net.simplebroadcast.MessageRunnable;
import net.simplebroadcast.methods.Methods;
import net.simplebroadcast.methods.UpdatingMethods;

public class Events implements Listener {

	private Methods methods = new Methods();
	private UpdatingMethods updatingMethods = new UpdatingMethods();

	/*
	 * PlayerJoinEvent
	 */
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		/*
		 * Checks if any updates are available and notifies the player.
		 * (Asynchronous task)
		 */
		if (Main.getPlugin().getConfig().getBoolean("checkforupdates")) {
			final Player p = event.getPlayer();
			Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), new Runnable() {
				@Override
				public void run() {
					try {
						if ((p.isOp() || p.hasPermission("simplebroadcast.update")) && updatingMethods.updateAvailable()) {
							p.sendMessage("[Simple§cBroadcast]§r An update is available: v" + updatingMethods.getUpdateNumber());
							p.sendMessage("[Simple§cBroadcast]§r Please download it from the BukkitDev page.");
						}
					} catch (NullPointerException npe) {
						Main.logWarning("Couldn't check for updates.");
					}
				}
			});
		}
		/*
		 * Checks if server was empty before the player joined and starts the broadcast if it's not running yet.
		 */
		if (Main.getPlugin().getConfig().getBoolean("requiresonlineplayers")) {
			if (Bukkit.getOnlinePlayers().size() == 1 && MessageRunnable.getRunning() == 0) {
				methods.broadcast();
				MessageRunnable.setRunning(1);
			}
		}
	}

	/*
	 * PlayerQuitEvent
	 */
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
	/*
	 * Checks if server is empty after the player left and stops the broadcast if it's running.
	 */
	if (Main.getPlugin().getConfig().getBoolean("requiresonlineplayers") && Bukkit.getServer().getOnlinePlayers().size() == 1) {
			Bukkit.getServer().getScheduler().cancelTask(MessageRunnable.getMessageTask());
			MessageRunnable.setRunning(0);
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