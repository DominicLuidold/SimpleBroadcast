package net.simplebroadcast.methods;

import java.util.List;
import java.util.Random;

import me.confuser.barapi.BarAPI;
import net.simplebroadcast.Main;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class BossBarMethods {

	private int msg;
	private int previousMessage;
	private static int counter = 0;
	private static int barRunning = 1;
	private static int barTask;
	private Methods methods = new Methods();

	public void barBroadcast() {
		if (!Bukkit.getPluginManager().isPluginEnabled("BarAPI")) {
			return;
		}
		if (!Main.getBossBarConfig().getBoolean("enabled") && barRunning == 1) {
			barRunning = 3;
			for (Player p : Bukkit.getServer().getOnlinePlayers()) {
				BarAPI.removeBar(p);
			}
			return;
		}
		if (Main.getBossBarConfig().getBoolean("randomizemessages")) {
			barTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(), new Runnable() {
				@Override
				public void run() {
					/*
					 * Loads the ignore.yml file.
					 */
					final List<String> ignoredPlayers = Main.getIgnoreConfig().getStringList("players");
					/*
					 * Gets and broadcasts the messages in a random order.
					 */
					msg = new Random().nextInt(Main.bossBarMessages.size());
					if (msg != previousMessage) {
						previousMessage = msg;
					} else {
						msg += (previousMessage < Main.bossBarMessages.size() - 1) ? 1 : ((previousMessage > 1) ? -1 : 0);
						previousMessage = msg;
					}
					final String message = ChatColor.translateAlternateColorCodes('&', Main.bossBarMessages.get(msg));
					msg = 0;
					final Player[] onlinePlayers = Bukkit.getServer().getOnlinePlayers();
					Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), new Runnable() {
						@Override
						public void run() {					
							for (Player p : onlinePlayers) {
								if (!ignoredPlayers.contains(methods.getUUID(p.getName()))) {
									if (Main.getBossBarConfig().getBoolean("reducehealthbar")) {
										BarAPI.setMessage(p, methods.addVariablesP(message, p), Main.getBossBarConfig().getInt("delay"));
									} else {
										BarAPI.setMessage(p, methods.addVariablesP(message, p));
									}
								}
							}
						}
					});
				}
			}, 0L, Main.getBossBarConfig().getInt("delay") * 20L);
		} else {
			barTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(), new Runnable() {
				@Override
				public void run() {
					List<String> messages = Main.getBossBarConfig().getStringList("messages");
					if (counter < messages.size()) {
						broadcast();
					} else {
						counter = 0;
						broadcast();
					}
				}
			}, 0L, Main.getBossBarConfig().getInt("delay") * 20L);
		}
	}

	private void broadcast() {
		final List<String> messages = Main.getBossBarConfig().getStringList("messages");
		/*
		 * Loads the ignore.yml.
		 */
		final List<String> ignoredPlayers = Main.getIgnoreConfig().getStringList("players");
		/*
		 * Broadcasts the messages.
		 */
		Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), new Runnable() {
			@Override
			public void run() {
				for (Player p : Bukkit.getServer().getOnlinePlayers()) {
					if (!ignoredPlayers.contains(methods.getUUID(p.getName()))) {
						if (Main.getBossBarConfig().getBoolean("reducehealthbar")) {
							BarAPI.setMessage(p, ChatColor.translateAlternateColorCodes('&', methods.addVariablesP(messages.get(counter), p)), Main.getBossBarConfig().getInt("delay"));
						} else {
							BarAPI.setMessage(p, ChatColor.translateAlternateColorCodes('&', methods.addVariablesP(messages.get(counter), p)));
						}
					} else {
						BarAPI.removeBar(p);
					}
				}
				counter++;
			}
		});
	}

	/**
	 * Gets the running Integer
	 * @return the Integer
	 */
	public static int getBarRunning() {
		return barRunning;
	}
	
	/**
	 * Gets the task id of BossBarMethods
	 * @return the task id
	 */
	public static int getBarTask() {
		return barTask;
	}
	
	/**
	 * Gets the counter BossBarMethods
	 * @return the counter
	 */
	public static int getBarCounter() {
		return counter;
	}
	
	/**
	 * Sets the counter
	 * @param counter new counter
	 */
	public static void setBarCounter(int counter) {
		BossBarMethods.counter = counter;
	}
	
	/**
	 * Sets the running Integer
	 * @param running the new Integer
	 */
	public static void setBarRunning(int barRunning) {
		BossBarMethods.barRunning = barRunning;
	}

	/**
	 * Sets the task id of MessageRunnable
	 * @param messageTask the new task id
	 */
	public static void setBarTask(int barTask) {
		BossBarMethods.barTask = barTask;
	}
}