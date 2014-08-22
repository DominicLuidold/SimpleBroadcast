package net.simplebroadcast;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.simplebroadcast.Events.Events;
import net.simplebroadcast.Methods.BossBarMethods;
import net.simplebroadcast.Methods.Methods;
import net.simplebroadcast.Utils.Metrics;
import net.simplebroadcast.Utils.Metrics.Graph;
import net.simplebroadcast.Utils.UUIDFetcher;
import net.simplebroadcast.Utils.UpdatingMethods;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	
	public static Main plugin;
	public static int running = 1;
	public static int messageTask;
	private Methods mt = new Methods();
	private BossBarMethods bmt = new BossBarMethods();
	private UpdatingMethods um = new UpdatingMethods();
	
	@Override
	public void onDisable() {
		Bukkit.getScheduler().cancelTask(messageTask);
		Bukkit.getScheduler().cancelTask(BossBarMethods.barTask);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onEnable() {
		
		plugin = this;
		
		File bossbar = new File("plugins/SimpleBroadcast", "bossbar.yml");
		FileConfiguration cfg_boss = YamlConfiguration.loadConfiguration(bossbar);
		
		/*
		 * Checks if the plugin shall be enabled and logs it if not.
		 */
		if (!plugin.getConfig().getBoolean("enabled")) {
			log("Messages aren't broadcasted as set in the config.");
			Bukkit.getScheduler().cancelTask(messageTask);
			running = 3;
		}
	
		/*
		 * Initializes the main command.
		 */
		plugin.getCommand("simplebroadcast").setExecutor(new SimpleBroadcastCommand(plugin));
				
		/*
		 * Connects to mcstats.org and sends data (see list below).
		 * - Prefix
		 * - Suffix
		 * - Update checker
		 * - Message randomizer
		 * - Onlineplayers required
		 * - Console messages
		 * - Bossbar
		 */
		if (plugin.getConfig().getBoolean("enabled") && plugin.getConfig().getBoolean("pluginmetrics")) {
			try {
			    Metrics metrics = new Metrics(plugin);
			    Graph enabledFeatures = metrics.createGraph("Enabled Features");
			    if (plugin.getConfig().getBoolean("prefix.enabled")) {
			    	enabledFeatures.addPlotter(new Metrics.Plotter("Prefix") {
			    		@Override
			            public int getValue() {
			    			return 1;
			            }
			    	});
			    }
			    if (plugin.getConfig().getBoolean("suffix.enabled")) {
			    	enabledFeatures.addPlotter(new Metrics.Plotter("Suffix") {
			    		@Override
			            public int getValue() {
			    			return 1;
			            }
			    	});
			    }
			    if (plugin.getConfig().getBoolean("checkforupdates")) {
			    	enabledFeatures.addPlotter(new Metrics.Plotter("Update checker") {
			    		@Override
			            public int getValue() {
			    			return 1;
			            }
			    	});
			    }
			    if (plugin.getConfig().getBoolean("randomizemessages")) {
			    	enabledFeatures.addPlotter(new Metrics.Plotter("Message randomizer") {
			    		@Override
			            public int getValue() {
			    			return 1;
			            }
			    	});
			    }
			    if (plugin.getConfig().getBoolean("requiresonlineplayers")) {
			    	enabledFeatures.addPlotter(new Metrics.Plotter("Onlineplayers required") {
			    		@Override
			            public int getValue() {
			    			return 1;
			            }
			    	});
			    }
			    if (plugin.getConfig().getBoolean("sendmessagestoconsole")) {
			    	enabledFeatures.addPlotter(new Metrics.Plotter("Console messages") {
			    		@Override
			            public int getValue() {
			    			return 1;
			            }
			    	});
			    }
			    if (cfg_boss.getBoolean("enabled")) {
			    	enabledFeatures.addPlotter(new Metrics.Plotter("Bossbar") {
			    		@Override
			            public int getValue() {
			    			return 1;
			            }
			    	});
			    }

			    metrics.start();
			} catch (IOException e) {
				logW(e.getMessage());
			}
		}
		
		/*
		 * Checks if updates are available.
		 * (Asynchronous task)
		 */
		if (plugin.getConfig().getBoolean("enabled") && plugin.getConfig().getBoolean("checkforupdates")) {
			Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
				@Override
				public void run() {
					um.update();
				}
			});
		}
		
		/*
		 * Register the events.
		 */		
		if (plugin.getConfig().getBoolean("enabled")) {
			getServer().getPluginManager().registerEvents(new Events(), plugin);
		}			
		
	    /*
	     * Save all configs and the readme
	     */		
		plugin.saveDefaultConfig();
		final File file = new File("plugins/SimpleBroadcast", "ignore.yml");
		if (!(file.exists())) {
			plugin.saveResource("ignore.yml", false);
		}
		if (!bossbar.exists()) {
			plugin.saveResource("bossbar.yml", false);
		}
		plugin.saveResource("readme.txt", true);
		
		/*
		 * Checks if the boss bar is enabled and BarAPI is installed.		
		 */
		if (cfg_boss.getBoolean("enabled") && Bukkit.getPluginManager().isPluginEnabled("BarAPI")) {
			log("BarAPI integration successfully enabled.");			
		} else {
			Bukkit.getScheduler().cancelTask(BossBarMethods.barTask);
			BossBarMethods.bar_running = 3;
		}
		
		/*
		 * Starts the chat broadcast task.
		 */
		if (plugin.getConfig().getBoolean("enabled")) {
			if (!plugin.getConfig().getBoolean("requiresonlineplayers")) {
				mt.broadcast();
			} else {
				if (Bukkit.getOnlinePlayers().length >= 1) {
					mt.broadcast();
				} else {
					running = 0;
				}
			}
		}
		
		/*
		 * Starts the boss bar broadcast task.
		 */
		if (cfg_boss.getBoolean("enabled") && BossBarMethods.bar_running != 0 && BossBarMethods.bar_running != 3) {
			bmt.barBroadcast();
		}
		
		/*
		 * Converts the old ignore.yml to the new format if existing.
		 * Converts the user names to UUID's (asynchronous task).
		 */
		File old = new File("plugins/SimpleBroadcast", "ignore.yml.OLD");
		final FileConfiguration cfg_old = YamlConfiguration.loadConfiguration(file);
		if (!old.exists() && cfg_old.get("format") == null) {
			file.renameTo(old);
			
			final FileConfiguration cfg_new = YamlConfiguration.loadConfiguration(file);
			final List<String> ignorePlayers = cfg_new.getStringList("players");
			
			final FileConfiguration cfg_converted = YamlConfiguration.loadConfiguration(old);
			
			try {
				cfg_new.save(file);
			} catch (IOException e) {
				logW("Couldn't save new ignore.yml.");
			}
			Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
				@Override
				public void run() {
					for (String player : cfg_converted.getStringList("players") ) {
						UUIDFetcher fetcher = new UUIDFetcher(Arrays.asList(player));
						Map<String, UUID> response = null;
						try {
							response = fetcher.call();
						} catch (Exception e) {
							logW("Couldn't convert ignore.yml.");
						}
						
						for (Map.Entry<String, UUID> entry : response.entrySet()) {
						    UUID uuid = entry.getValue();
						    ignorePlayers.add(uuid.toString());
						    cfg_new.set("players", ignorePlayers);
						    cfg_new.options().header("Since v1.7 the plugin uses the UUID of the player. Please DO NOT edit this file!");
						}
						cfg_new.addDefault("format", "uuid");
						cfg_new.options().copyDefaults(true);
						try {
					    	cfg_new.save(file);
						} catch (IOException e) {
							logW("Couldn't save new ignore.yml.");
						}
					}
				}
			});
		}
	}
	
	/**
	 * Logs the message with the status: info
	 * @param str The message which shall be logged. 
	 */
	public void log(String str) {
        getLogger().info(str);
    }

	/**
	 * Logs the message with the status: warning
	 * @param str The message which shall be logged. 
	 */
	public void logW(String str) {
        getLogger().warning(str);
    }
}