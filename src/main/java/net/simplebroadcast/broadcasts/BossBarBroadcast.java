package net.simplebroadcast.broadcasts;

import me.confuser.barapi.BarAPI;
import net.simplebroadcast.Main;
import net.simplebroadcast.methods.Methods;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class BossBarBroadcast {

	private int msg;
	private static int barTask;
	private int previousMessage;
	private static int barCounter = 0;
	private static int barRunning = 1;
	private Methods methods = new Methods();

	public void barBroadcast() {
		if (!Bukkit.getPluginManager().isPluginEnabled("BarAPI")) {
			return;
		}
		if (!Main.getBossBarConfig().getBoolean("enabled") && getBarRunning() == 1) {
			setBarRunning(3);
			for (Player p : Bukkit.getServer().getOnlinePlayers()) {
				BarAPI.removeBar(p);
			}
			return;
		}
		if (Main.getBossBarConfig().getBoolean("randomizemessages")) {
			setBarTask(Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(), new Runnable() {
				@Override
				public void run() {
					/*
					 * Gets and broadcasts the messages in a random order.
					 */
					msg = (int) (Math.random() * Main.bossBarMessages.size());
					if (msg != previousMessage) {
						previousMessage = msg;
					} else {
						msg += (previousMessage < Main.bossBarMessages.size() - 1) ? 1 : ((previousMessage > 1) ? -1 : 0);
						previousMessage = msg;
					}
					final String message = ChatColor.translateAlternateColorCodes('&', Main.bossBarMessages.get(msg));
					msg = 0;
					Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), new Runnable() {
						@Override
						public void run() {					
							for (Player p : Bukkit.getServer().getOnlinePlayers()) {
								if (!Main.ignoredPlayers.contains(methods.getUUID(p.getName()))) {
									if (Main.getBossBarConfig().getBoolean("reducehealthbar")) {
										BarAPI.setMessage(p, methods.addPlayerVariables(message, p), Main.getBossBarConfig().getInt("delay"));
									} else {
										BarAPI.setMessage(p, methods.addPlayerVariables(message, p));
									}
								}
							}
						}
					});
				}
			}, 0L, Main.getBossBarConfig().getInt("delay") * 20L));
		} else {
			setBarTask(Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(), new Runnable() {
				@Override
				public void run() {
					if (barCounter < Main.bossBarMessages.size()) {
						broadcast();
					} else {
						setBarCounter(0);
						broadcast();
					}
				}
			}, 0L, Main.getBossBarConfig().getInt("delay") * 20L));
		}
	}

	private void broadcast() {
		/*
		 * Broadcasts the messages.
		 */
		Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), new Runnable() {
			@Override
			public void run() {
				for (Player p : Bukkit.getServer().getOnlinePlayers()) {
					if (!Main.ignoredPlayers.contains(methods.getUUID(p.getName()))) {
						if (Main.getBossBarConfig().getBoolean("reducehealthbar")) {
							BarAPI.setMessage(p, ChatColor.translateAlternateColorCodes('&', methods.addPlayerVariables(Main.bossBarMessages.get(barCounter), p)), Main.getBossBarConfig().getInt("delay"));
						} else {
							BarAPI.setMessage(p, ChatColor.translateAlternateColorCodes('&', methods.addPlayerVariables(Main.bossBarMessages.get(barCounter), p)));
						}
					} else {
						BarAPI.removeBar(p);
					}
				}
				barCounter++;
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
		return barCounter;
	}

	/**
	 * Sets the counter
	 * @param counter new counter
	 */
	public static void setBarCounter(int barCounter) {
		BossBarBroadcast.barCounter = barCounter;
	}

	/**
	 * Sets the running Integer
	 * @param running the new Integer
	 */
	public static void setBarRunning(int barRunning) {
		BossBarBroadcast.barRunning = barRunning;
	}

	/**
	 * Sets the task id of MessageRunnable
	 * @param barTask the new task id
	 */
	public static void setBarTask(int barTask) {
		BossBarBroadcast.barTask = barTask;
	}
}