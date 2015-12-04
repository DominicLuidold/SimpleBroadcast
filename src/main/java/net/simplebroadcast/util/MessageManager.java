package net.simplebroadcast.util;

import java.util.HashMap;

import net.simplebroadcast.Main;

public class MessageManager {
	
	/**
	 * Prefix and suffix (configured in config).
	 */
	private String chatPrefix, chatSuffix;
	
	/**
	 * HashMaps which contain chat and boss bar messages.
	 */
	private HashMap<Integer, String> chatMessages = new HashMap<Integer, String>();
	private HashMap<Integer, String> bossBarMessages = new HashMap<Integer, String>();
	
	/**
	 * HashMaps which contain all permissions required for each chat and boss bar message.
	 */
	private HashMap<Integer, String> chatMessagePermissions = new HashMap<Integer, String>();
	private HashMap<Integer, String> bossBarMessagePermissions = new HashMap<Integer, String>();
	
	/**
	 * Loads all needed values.
	 */
	public void loadAll() {
		loadChatPrefix();
		loadChatSuffix();
		loadChatMessages();
		loadBossBarMessages();
	}
	
	/**
	 * Loads and sets chat prefix.
	 */
	private void loadChatPrefix() {
		setChatPrefix(Main.getInstance().getConfig().getString("chat.prefix.value"));
	}
	
	/**
	 * Loads and sets chat suffix.
	 */
	private void loadChatSuffix() {
		setChatSuffix(Main.getInstance().getConfig().getString("chat.suffix.value"));
	}
	
	/**
	 * Loads chat messages into HashMap.
	 */
	private void loadChatMessages() {
		chatMessages.clear();
		chatMessagePermissions.clear();
		/* Messages from config */
		if (!Main.getInstance().getConfig().getBoolean("mysql.use")) {
			int index = 0;
			for (String permission : Main.getInstance().getConfig().getConfigurationSection("chat.messages").getKeys(true)) {
				for (String message : Main.getInstance().getConfig().getStringList("chat.messages." + permission)) {
					getChatMessages().put(index, message);
					getChatMessagePermissions().put(index, permission);
					index++;
				}
			}
		/* Messages from database */
		} else {
			// TODO
		}
	}
	
	/**
	 * Loads boss bar messages into HashMap.
	 */
	private void loadBossBarMessages() {
		getBossBarMessages().clear();
		getBossBarMessagePermissions().clear();
		/* Messages from boss bar config */
		if (!Main.getInstance().getConfig().getBoolean("mysql.use")) {
			int index = 0;
			for (String permission : Main.getInstance().getBossBarConfig().getConfigurationSection("bossbar.messages").getKeys(true)) {
				for (String message : Main.getInstance().getBossBarConfig().getStringList("bossbar.messages." + permission)) {
					getBossBarMessages().put(index, message);
					getBossBarMessagePermissions().put(index, permission);
					index++;
				}
			}
		/* Messages from database */
		} else {
			// TODO
		}
	}

	/**
	 * Returns chat prefix.
	 * 
	 * @return the chatPrefix
	 */
	public String getChatPrefix() {
		return chatPrefix;
	}

	/**
	 * Sets chat prefix.
	 * 
	 * @param chatPrefix the chatPrefix to set
	 */
	public void setChatPrefix(String chatPrefix) {
		this.chatPrefix = chatPrefix;
	}
	
	/**
	 * Returns chat suffix.
	 * 
	 * @return the chatSuffix
	 */
	public String getChatSuffix() {
		return chatSuffix;
	}

	/**
	 * Sets chat suffix.
	 * 
	 * @param chatSuffix the chatSuffix to set
	 */
	public void setChatSuffix(String chatSuffix) {
		this.chatSuffix = chatSuffix;
	}

	/**
	 * Returns HashMap which contains all chat messages.
	 * 
	 * @return the chatMessages
	 */
	public HashMap<Integer, String> getChatMessages() {
		return chatMessages;
	}

	/**
	 * Returns HashMap which contains all permissions required for each chat message.
	 * 
	 * @return the chatMessagePermissions
	 */
	public HashMap<Integer, String> getChatMessagePermissions() {
		return chatMessagePermissions;
	}

	/**
	 * Returns HashMap which contains all boss bar messages.
	 * 
	 * @return the bossBarMessages
	 */
	public HashMap<Integer, String> getBossBarMessages() {
		return bossBarMessages;
	}

	/**
	 * Returns HashMap which contains all permissions required for each boss bar message.
	 * 
	 * @return the bossBarMessagePermissions
	 */
	public HashMap<Integer, String> getBossBarMessagePermissions() {
		return bossBarMessagePermissions;
	}
}