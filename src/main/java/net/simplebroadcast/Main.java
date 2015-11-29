package net.simplebroadcast;

import java.io.File;

import org.bukkit.plugin.java.JavaPlugin;

import net.simplebroadcast.commands.SimpleBroadcastCommand;

public class Main extends JavaPlugin {
	
	/**
	 * Instance of this class.
	 */
	private static Main instance = null;
	
	@Override
	public void onEnable() {
		instance = this;
		
		/* Saves all required resources */
		saveDefaultConfig();
		if (!new File(getDataFolder(), "ignore.yml").exists()) {
			saveResource("ignore.yml", false);			
		}
		saveResource("readme.txt", true);
		
		/* Sets command executor */
		getCommand("simplebroadcast").setExecutor(new SimpleBroadcastCommand());
	}
	
	/**
	 * Returns initialized instance of this class.
	 * 
	 * @return the instance
	 */
	public static Main getInstance() {
		return instance;
	}
}