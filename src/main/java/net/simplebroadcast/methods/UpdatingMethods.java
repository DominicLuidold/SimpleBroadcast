package net.simplebroadcast.methods;

import net.gravitydevelopment.updater.Updater;
import net.simplebroadcast.Main;

public class UpdatingMethods {

	/**
	 * Notifies the player about available updates.
	 */
	public void update() {
		try {
			if (updateAvailable()) {
				Main.logInfo("An update is available: v" + getUpdateNumber());
				Main.logInfo("Please download it from the BukkitDev page.");
			}
		} catch (NullPointerException npe) {
			Main.logWarning("Couldn't check for updates.");
		}
	}

	/**
	 * Returns if any updates are available.
	 * @return if an update is available or not
	 */
	public boolean updateAvailable() {
		if (getPluginVersion() >= getUpdateVersion()) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Gets the version of the available update.
	 * @return version of available update
	 */
	public int getUpdateVersion() {
		Updater updater = new Updater(Main.getPlugin(), 54358, Main.getPlugin().getDataFolder(), Updater.UpdateType.NO_DOWNLOAD, false);
		if (updater.getLatestName().substring(17).length() == 3) {
			int updateVersion = Integer.parseInt(updater.getLatestName().substring(17).replace(".", "") + "0");
			return updateVersion;
		} else if (updater.getLatestName().substring(17).length() == 5) {
			int updateVersion = Integer.parseInt(updater.getLatestName().substring(17).replace(".", ""));
			return updateVersion;
		} else {
			return 1;
		}
	}

	/**
	 * Gets the plugin version.
	 * @return plugin version
	 */
	public int getPluginVersion() {
		if (Main.getPlugin().getDescription().getVersion().length() == 3) {
			int pluginVersion = Integer.parseInt(Main.getPlugin().getDescription().getVersion().replace(".", "") + "0");
			return pluginVersion;
		} else if (Main.getPlugin().getDescription().getVersion().length() == 5) {
			int pluginVersion = Integer.parseInt(Main.getPlugin().getDescription().getVersion().replace(".", ""));
			return pluginVersion;
		} else {
			return 0;
		}
	}

	/**
	 * Gets the latest version number.
	 * @return the latest version number.
	 */
	public String getUpdateNumber() {
		if (Main.getPlugin().getConfig().getBoolean("checkforupdates")) {
			Updater updater = new Updater(Main.getPlugin(), 54358, Main.getPlugin().getDataFolder(), Updater.UpdateType.NO_DOWNLOAD, false);
			String updateNumber = updater.getLatestName().substring(17);
			return updateNumber;
		} else {
			return "UNKNOWN";
		}
	}
}