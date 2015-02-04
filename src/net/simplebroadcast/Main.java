package net.simplebroadcast;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.simplebroadcast.Events.Events;
import net.simplebroadcast.Methods.BossBarMethods;
import net.simplebroadcast.Methods.Methods;
import net.simplebroadcast.Methods.UpdatingMethods;
import net.simplebroadcast.Utils.Metrics;
import net.simplebroadcast.Utils.Metrics.Graph;
import net.simplebroadcast.Utils.UUIDFetcher;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

	private static Main plugin;
	private int running = 1;
	private int messageTask;
	private Methods mt = new Methods();
	private BossBarMethods bmt = new BossBarMethods();
	private UpdatingMethods um = new UpdatingMethods();
	public static HashMap<Integer, String> globalMessages = new HashMap<Integer, String>(); 

	@Override
	public void onDisable() {
		Bukkit.getScheduler().cancelTask(messageTask);
		Bukkit.getScheduler().cancelTask(BossBarMethods.getBarTask());
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onEnable() {

		plugin = this;

		/*
		 * Saves all configs and the readme
		 */
		plugin.saveDefaultConfig();
		final File ignore = new File(getDataFolder(), "ignore.yml");
		if (!(ignore.exists())) {
			plugin.saveResource("ignore.yml", false);
		}
		File bossbar = new File(getDataFolder(), "bossbar.yml");
		FileConfiguration cfg_boss = YamlConfiguration.loadConfiguration(bossbar);
		if (!bossbar.exists()) {
			plugin.saveResource("bossbar.yml", false);
		}
		plugin.saveResource("readme.txt", true);

		/*
		 * Checks if the plugin shall be enabled and logs it if not.
		 */
		if (!plugin.getConfig().getBoolean("enabled")) {
			log("Messages don't get broadcasted as set in the config.");
			Bukkit.getScheduler().cancelTask(messageTask);
			setRunning(3);
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
		 * - Online players required
		 * - Console messages
		 * - Bossbar
		 */		
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
				if (cfg_boss.getBoolean("reducehealthbar")) {
					enabledFeatures.addPlotter(new Metrics.Plotter("Reduce health bar") {
						@Override
						public int getValue() {
							return 1;
						}
					});
				}
			}

			metrics.start();
		} catch (IOException e) {
			logW(e.getMessage());
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
		 * Registers the events.
		 */
		if (plugin.getConfig().getBoolean("enabled")) {
			getServer().getPluginManager().registerEvents(new Events(), plugin);
		}

		/*
		 * Checks if the boss bar is enabled and BarAPI is installed.
		 */
		if (cfg_boss.getBoolean("enabled") && getServer().getPluginManager().isPluginEnabled("BarAPI")) {
			log("BarAPI integration successfully enabled.");
		} else {
			getServer().getScheduler().cancelTask(BossBarMethods.getBarTask());
			BossBarMethods.setBarRunning(3);
		}

		/*
		 * Starts the chat broadcast task.
		 */
		if (plugin.getConfig().getBoolean("enabled")) {
			loadMessages();
			if (!plugin.getConfig().getBoolean("requiresonlineplayers")) {
				mt.broadcast();
			} else {
				if (Bukkit.getOnlinePlayers().length >= 1) {
					mt.broadcast();
				} else {
					setRunning(0);
				}
			}
		}

		/*
		 * Starts the boss bar broadcast task.
		 */
		if (cfg_boss.getBoolean("enabled") && BossBarMethods.getBarRunning() != 0 && BossBarMethods.getBarRunning() != 3) {
			bmt.barBroadcast();
		}

		/*
		 * Converts the old ignore.yml to the new format if existing.
		 * Converts the user names to UUID's (asynchronous task).
		 */
		File old = new File(getDataFolder(), "ignore.yml.OLD");
		final FileConfiguration cfg_old = YamlConfiguration.loadConfiguration(ignore);
		if (!old.exists() && cfg_old.get("format") == null) {
			ignore.renameTo(old);

			final FileConfiguration cfg_new = YamlConfiguration.loadConfiguration(ignore);
			final List<String> ignorePlayers = cfg_new.getStringList("players");

			final FileConfiguration cfg_converted = YamlConfiguration.loadConfiguration(old);

			try {
				cfg_new.save(ignore);
			} catch (IOException e) {
				logW("Couldn't save new ignore.yml.");
			}
			Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
				@Override
				public void run() {
					UUIDFetcher fetcher = new UUIDFetcher(cfg_converted.getStringList("players"));
					Map<String, UUID> response = null;
					try {
						response = fetcher.call();
					} catch (Exception e) {
						logW("Couldn't convert ignore.yml.");
					}

					for (UUID uuid : response.values()) {
						ignorePlayers.add(uuid.toString());
					}
					cfg_new.set("players", ignorePlayers);
					cfg_new.options().header("Since v1.7 the plugin uses the UUID of the player. Please DO NOT edit this file!");
					cfg_new.addDefault("format", "uuid");
					cfg_new.options().copyDefaults(true);
					try {
						cfg_new.save(ignore);
					} catch (IOException e) {
						logW("Couldn't save new ignore.yml.");
					}
				}
			});
		}
	}

	/**
	 * Loads all the messages into a globally available HashMap.
	 */
	public void loadMessages() {
		int messageIndex = 0;
		globalMessages.clear();
		for (String message : plugin.getConfig().getStringList("messages")) {
			globalMessages.put(messageIndex, message);
			messageIndex++;
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
	
	/**
	 * Gets the instance of this Main class
	 * @return the instance
	 */
	public static Main getPlugin() {
		return plugin;
	}
	
	/**
	 * Gets the running Integer
	 * @return the Integer
	 */
	public int getRunning() {
		return running;
	}
	
	/**
	 * Gets the task id of the {@link MessageRunnable}
	 * @return the task id
	 */
	public int getMessageTask() {
		return messageTask;
	}
	
	/**
	 * Sets the running Integer
	 * @param running the new Integer
	 */
	public void setRunning(int running) {
		this.running = running;
	}
	
	/**
	 * Sets the task id of the {@link MessageRunnable}
	 * @param messageTask the new task id
	 */
	public void setMessageTask(int messageTask) {
		this.messageTask = messageTask;
	}
}