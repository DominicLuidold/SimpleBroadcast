package net.simplebroadcast.Methods;

import java.io.File;
import java.util.List;
import java.util.Random;

import me.confuser.barapi.BarAPI;
import net.simplebroadcast.Main;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class BossBarMethods {

	private int msg;
	private int previousMessage;
	private static int counter = 0;
	private static int barRunning = 1; //TODO: Maybe make this class a singleton instead of using static variables etc.
	private static int barTask;
	private Methods mt = new Methods();

	public void barBroadcast() {
		final File bossbar = new File(Main.getPlugin().getDataFolder(), "bossbar.yml");
		FileConfiguration cfg_boss = YamlConfiguration.loadConfiguration(bossbar);
		if (!Bukkit.getPluginManager().isPluginEnabled("BarAPI")) {
			return;
		}
		if (!cfg_boss.getBoolean("enabled") && barRunning == 1) {
			barRunning = 3;
			for (Player p : Bukkit.getServer().getOnlinePlayers()) {
				BarAPI.removeBar(p);
			}
			return;
		}
		if (cfg_boss.getBoolean("randomizemessages")) {
			barTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(), new Runnable() {
				@Override
				public void run() {
					FileConfiguration cfg_boss = YamlConfiguration.loadConfiguration(bossbar);
					/*
					 * Loads the ignore.yml file.
					 */
					File ignore = new File(Main.getPlugin().getDataFolder(), "ignore.yml");
					FileConfiguration cfg_ignore = YamlConfiguration.loadConfiguration(ignore);
					List<String> ignoredPlayers = cfg_ignore.getStringList("players");
					/*
					 * Gets and broadcasts the messages in a random order.
					 */
					msg = new Random().nextInt(cfg_boss.getStringList("messages").size());
					if (msg != previousMessage) {
						previousMessage = msg;
					} else {
						if (previousMessage < cfg_boss.getStringList("messages").size()-1) {
							msg++;
						} else if (previousMessage > 1) {
							msg--;
						}
					}
					String message = ChatColor.translateAlternateColorCodes('&', cfg_boss.getStringList("messages").get(msg));
					msg = 0;
					Player[] onlinePlayers = Bukkit.getServer().getOnlinePlayers();
					for (Player p : onlinePlayers) {
						if (!ignoredPlayers.contains(mt.getUUID(p.getName()))) BarAPI.setMessage(p, mt.addVariablesP(message, p));
					}
				}
			}, 0L, cfg_boss.getInt("delay") * 20L);
		} else {
			barTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(), new Runnable() {
				@Override
				public void run() {
					FileConfiguration cfg_boss = YamlConfiguration.loadConfiguration(bossbar);
					List<String> messages = cfg_boss.getStringList("messages");
					if (counter < messages.size()) {
						broadcast();
					} else {
						counter = 0;
						broadcast();
					}
				}
			}, 0L, cfg_boss.getInt("delay") * 20L);
		}
	}

	private void broadcast() {
		File bossbar = new File(Main.getPlugin().getDataFolder(), "bossbar.yml");
		FileConfiguration cfg_boss = YamlConfiguration.loadConfiguration(bossbar);
		List<String> messages = cfg_boss.getStringList("messages");
		/*
		 * Loads the ignore.yml.
		 */
		File ignore = new File(Main.getPlugin().getDataFolder(), "ignore.yml");
		FileConfiguration cfg_ignore = YamlConfiguration.loadConfiguration(ignore);
		List<String> ignoredPlayers = cfg_ignore.getStringList("players");
		/*
		 * Broadcasts the messages.
		 */
		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			if (!ignoredPlayers.contains(mt.getUUID(p.getName()))) {
				BarAPI.setMessage(p, ChatColor.translateAlternateColorCodes('&', mt.addVariablesP(messages.get(counter), p)));
			} else {
				BarAPI.removeBar(p);
			}
		}
		counter++;
	}

	public static int getCounter() {
		return counter;
	}

	public static int getBarRunning() {
		return barRunning;
	}

	public static int getBarTask() {
		return barTask;
	}

	public static void setCounter(int counter) {
		BossBarMethods.counter = counter;
	}

	public static void setBarRunning(int barRunning) {
		BossBarMethods.barRunning = barRunning;
	}

	public void setBarTask(int barTask) {
		BossBarMethods.barTask = barTask;
	}
	
}