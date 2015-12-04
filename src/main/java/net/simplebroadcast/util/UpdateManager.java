package net.simplebroadcast.util;

import java.util.logging.Level;

import org.bukkit.Bukkit;

import net.gravitydevelopment.updater.Updater;
import net.simplebroadcast.Main;

public class UpdateManager {
	
	/**
	 * TODO
	 */
	public void update() {
		if (!Main.getInstance().getConfig().getBoolean("general.updater.checkForUpdates")) {
			return;
		}
		if (Main.getInstance().getConfig().getBoolean("general.updater.auto-download")) {
			new Updater(Main.getInstance(), 54358, Main.getInstance().getFile(), Updater.UpdateType.DEFAULT, true);
		} else {
			Updater updater = new Updater(Main.getInstance(), 54358, Main.getInstance().getDataFolder(), Updater.UpdateType.NO_DOWNLOAD, false);
			Bukkit.getLogger().log(Level.INFO, "An update is available: v" + updater.getLatestName().substring(17)); //TODO
		}
		// TODO
	}
}