package net.simplebroadcast.Methods;

import net.simplebroadcast.Main;
import net.simplebroadcast.Utils.Updater;

public class UpdatingMethods {

	/**
	 * Gets the plugin version of the update.
	 * @return (Integer) Returns the plugin version of the update (removed points for comparison).
	 */
	public int getUpdateVersion() {
		Updater updater = new Updater(Main.getPlugin(), 54358, Main.getPlugin().getDataFolder(), Updater.UpdateType.NO_DOWNLOAD, false);
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
	public int getPluginVersion() {
		if (Main.getPlugin().getDescription().getVersion().length() == 3) {
			int pVersion = Integer.parseInt(Main.getPlugin().getDescription().getVersion().replace(".", "") + "0");
			return pVersion;
		} else if (Main.getPlugin().getDescription().getVersion().length() == 5) {
			int pVersion = Integer.parseInt(Main.getPlugin().getDescription().getVersion().replace(".", ""));
			return pVersion;
		} else {
			return 0;
		}
	}

	/**
	 * Notifies the player if updates are available.
	 */
	public void update() {
		try {
			if (updateAvailable()) {
				Main.getPlugin().log("An update is available: v" + getUpdateNumber());
				Main.getPlugin().log("Please download it from the BukkitDev page.");
			}
		} catch (NullPointerException npe) {
			Main.getPlugin().logW("Couldn't check for updates.");
		}
	}

	/**
	 * Checks for updates.
	 * @return (Boolean) Return either true if updates are available or false if no updates are available.
	 */
	public boolean updateAvailable() {
		if (getPluginVersion() >= getUpdateVersion()) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Gets the latest version number.
	 * @return (String) Returns the latest version number.
	 */
	public String getUpdateNumber() {
		if (Main.getPlugin().getConfig().getBoolean("checkforupdates")) {
			Updater updater = new Updater(Main.getPlugin(), 54358, Main.getPlugin().getDataFolder(), Updater.UpdateType.NO_DOWNLOAD, false);
			String uNumber = updater.getLatestName().substring(17);
			return uNumber;
		} else {
			return "UNKNOWN";
		}
	}
}