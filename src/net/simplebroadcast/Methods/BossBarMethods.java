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
	public static int counter = 0;
	public static int bar_running = 1;
	public static int barTask;
	private Methods mt = new Methods();
	
	@SuppressWarnings("deprecation")
	public void barBroadcast() {
		final File bossbar = new File("plugins/SimpleBroadcast", "bossbar.yml");
		FileConfiguration cfg_boss = YamlConfiguration.loadConfiguration(bossbar);
		if (!Bukkit.getPluginManager().isPluginEnabled("BarAPI")) {
			return;
		}
		if (!cfg_boss.getBoolean("enabled") && bar_running == 1) {
			bar_running = 3;
			for (Player p : Bukkit.getServer().getOnlinePlayers()) {
				BarAPI.removeBar(p);
			}
			return;
		}
		if (cfg_boss.getBoolean("randomizemessages")) {
			barTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
				@Override
				public void run() {
					FileConfiguration cfg_boss = YamlConfiguration.loadConfiguration(bossbar);							
						/*
						 * Loads the ignore.yml file.
						 */
						File ignore = new File("plugins/SimpleBroadcast", "ignore.yml");
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
			barTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
				@Override
				public void run() {							
					FileConfiguration cfg_boss = YamlConfiguration.loadConfiguration(bossbar);
					List<String> messages = cfg_boss.getStringList("messages");
					if (counter < messages.size()) {
						broadCast();
					} else {
						counter = 0;
						broadCast();
					}
				}
			}, 0L, cfg_boss.getInt("delay") * 20L);
		}
	}
	
	@SuppressWarnings("deprecation")
	private void broadCast() {
		File bossbar = new File("plugins/SimpleBroadcast", "bossbar.yml");
		FileConfiguration cfg_boss = YamlConfiguration.loadConfiguration(bossbar);
		List<String> messages = cfg_boss.getStringList("messages");
		/*
		 * Loads the ignore.yml.
		 */
		File ignore = new File("plugins/SimpleBroadcast", "ignore.yml");
		FileConfiguration cfg_ignore = YamlConfiguration.loadConfiguration(ignore);
		List<String> ignoredPlayers = cfg_ignore.getStringList("players");
		/*
		 * Broadcasts the messages.
		 */
		Player[] onlinePlayers = Bukkit.getServer().getOnlinePlayers();
		for (Player p : onlinePlayers) {
			if (!ignoredPlayers.contains(mt.getUUID(p.getName()))) {
				BarAPI.setMessage(p, ChatColor.translateAlternateColorCodes('&', mt.addVariablesP(messages.get(counter), p)));
			} else {
				BarAPI.removeBar(p);
			}
		}
		counter++;
	}
}