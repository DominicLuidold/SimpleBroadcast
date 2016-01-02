package net.simplebroadcast.util;

import java.util.ArrayList;
import java.util.List;

import net.simplebroadcast.Main;

public class IgnoreManager {
	
	/**
	 * ArrayLists which contain UUIDs of players who ignore chat and/or boss bar broadcast.
	 */
	private static List<String> chatIgnoreList = new ArrayList<String>();
	private static List<String> bossBarIgnoreList = new ArrayList<String>();
	
	/**
	 * Loads UUIDs into ArrayList.
	 */
	public void loadChatIgnoreList() {
		getChatIgnoreList().clear();
		/* UUIDs from ignore.yml */
		if (!Main.getInstance().getConfig().getBoolean("mysql.use")) {
			for (String uuid : Main.getInstance().getIgnoreConfig().getStringList("chat.players")) {
				getChatIgnoreList().add(uuid);
			}
		/* UUIDs from database. */
		} else {
			// TODO
		}
	}
	
	/**
	 * Loads UUIDs into ArrayList.
	 */
	public void loadBossBarIgnoreList() {
		bossBarIgnoreList.clear();
		/* UUIDs from ignore.yml */
		if (!Main.getInstance().getConfig().getBoolean("mysql.use")) {
			for (String uuid : Main.getInstance().getIgnoreConfig().getStringList("bossbar.players")) {
				getBossBarIgnoreList().add(uuid);
			}
		/* UUIDs from database. */
		} else {
			// TODO
		}
	}
	
	public void updateChatIgnoreList() {
		// TODO
	}
	
	/**
	 * Returns chat ignore list.
	 * 
	 * @return the chatIgnoreList
	 */
	public static List<String> getChatIgnoreList() {
		return chatIgnoreList;
	}
	
	/**
	 * Returns boss bar ignore list.
	 * 
	 * @return the bossBarIgnoreList
	 */
	public static List<String> getBossBarIgnoreList() {
		return bossBarIgnoreList;
	}
}