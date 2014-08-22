package net.simplebroadcast.Utils;

import java.io.File;

import net.simplebroadcast.Main;

public class UpdatingMethods {
	
	/**
	 * Gets the plugin version of the update.
	 * @return (Integer) Returns the plugin version of the update (removed points for comparison).
	 */
	public int uVersion() {
		Updater updater = new Updater(Main.plugin, 54358, new File("plugins/SimpleBroadcast"), Updater.UpdateType.NO_DOWNLOAD, false);
		if (updater.getLatestName().substring(17).length() == 3) {
			int uVersion = Integer.parseInt(updater.getLatestName().substring(17).replace(".", "") + "0");
			return uVersion;
		} else if (updater.getLatestName().substring(17).length() == 5) {
			int uVersion = Integer.parseInt(updater.getLatestName().substring(17).replace(".", ""));
			return uVersion;
		} else {
			return 1;
		}
	}
		
	/**
	 * Gets the plugin version.
	 * @return (Integer) Returns the plugin version (removed points for comparison).
	 */
	public int pVersion() {
		if (Main.plugin.getDescription().getVersion().length() == 3) {
			int pVersion = Integer.parseInt(Main.plugin.getDescription().getVersion().replace(".", "") + "0");
			return pVersion;
		} else if (Main.plugin.getDescription().getVersion().length() == 5) {
			int pVersion = Integer.parseInt(Main.plugin.getDescription().getVersion().replace(".", ""));
			return pVersion;
		} else {
			return 0;
		}
	}

	/*
	 * Notifies the player if updates are available.
	 */
	public void update() {
		try {			
			if (updateB()) {
				Main.plugin.log("An update is available: " + updateN());
				Main.plugin.log("Please download it from the BukkitDev page.");
			}
		} catch (NullPointerException npe) {
			Main.plugin.logW("Couldn't check for updates.");
		}
	}
	
	/**
	 * Checks for updates.
	 * @return (Boolean) Return either true if updates are available or false if no updates are available.
	 */
	public boolean updateB() {				
		if (pVersion() >= uVersion()) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Gets the latest version number.
	 * @return (String) Returns the latest version number.
	 */
	public String updateN() {
		if (Main.plugin.getConfig().getBoolean("checkforupdates")) {
			Updater updater = new Updater(Main.plugin, 54358, new File("plugins/SimpleBroadcast"), Updater.UpdateType.NO_DOWNLOAD, false);
			String UNumber = updater.getLatestName().substring(17);
			return UNumber;
		} else {
			return "UNKNOWN";
		}
	}

}
