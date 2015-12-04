package net.simplebroadcast;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import net.simplebroadcast.broadcasts.Broadcast;
import net.simplebroadcast.commands.BroadcastCommand;
import net.simplebroadcast.util.MessageManager;
import net.simplebroadcast.util.UpdateManager;

public class Main extends JavaPlugin {
	
	/**
	 * Instance of Main class.
	 */
	private static Main instance = null;
	
	/*
	 * @see net.simplebroadcast.broadcasts.Broadcast
	 * @see net.simplebroadcast.util.MessageManager
	 * @see net.simplebroadcast.util.MetricsData
	 * @see net.simplebroadcast.util.UpdateManager
	 */
	private Broadcast broadcast = new Broadcast();
	private MessageManager messageManager = new MessageManager();
	//private MetricsUtil metricsUtil = new MetricsUtil();
	private UpdateManager updateManager = new UpdateManager();
	
	@Override
	public void onEnable() {
		instance = this;
		
		/* Saves all required resources */
		saveDefaultConfig();
		if (!new File(getDataFolder(), "bossbar.yml").exists()) {
			saveResource("bossbar.yml", false);			
		}
		if (!new File(getDataFolder(), "ignore.yml").exists()) {
			saveResource("ignore.yml", false);			
		}
		saveResource("readme.txt", true);
		
		/* Sets command executor */
		getCommand("simplebroadcast").setExecutor(new BroadcastCommand());
		
		/* Loads chat and boss bar messages and permissions as well as chat prefix and suffix */
		messageManager.loadAll();
		
		/* Starts broadcast(s) after checking their status. */
		broadcast.broadcast();
		
		/* Generates data for mcstats.org */
		//TODO Activate later to prevent plugin from creating any unwanted data.
		//metricsUtil.generateData();
		
		/* Checks for updates and automatically downloads it (if user enabled this in the config). */
		updateManager.update();
	}
	
	@Override
	public void onDisable() {
		/* Cancels broadcasts. */
		broadcast.cancelChatBroadcast();
		broadcast.cancelBossBarBroadcast();
	}
	
	/**
	 * Returns initialized instance of Main class.
	 * 
	 * @return the instance
	 */
	public static Main getInstance() {
		return instance;
	}
	
	/**
	 * Returns boss bar config file.
	 * 
	 * @return the bossBarConfig
	 */
	public FileConfiguration getBossBarConfig() {
		File bossBarConfigFile = new File(this.getDataFolder(), "bossbar.yml");
		FileConfiguration bossBarConfig = YamlConfiguration.loadConfiguration(bossBarConfigFile);
		return bossBarConfig;
	}
	
	/**
	 * Returns ignore config file.
	 * 
	 * @return the ignoreConfig
	 */
	public FileConfiguration getIgnoreConfig() {
		File ignoreConfigFile = new File(this.getDataFolder(), "ignore.yml");
		FileConfiguration ignoreConfig = YamlConfiguration.loadConfiguration(ignoreConfigFile);
		return ignoreConfig;
	}
	
	/*
	 * @see org.bukkit.plugin.java.JavaPlugin#getFile()
	 */
	@Override
	public File getFile() {
		return super.getFile();
	}
}