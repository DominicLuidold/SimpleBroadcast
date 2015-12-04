package net.simplebroadcast.broadcasts;

import org.bukkit.Bukkit;

import net.simplebroadcast.Main;

public class Broadcast {

	/**
	 * Current status of chat and boss bar broadcast.
	 */
	private BroadcastStatus chatBroadcastStatus, bossBarBroadcastStatus;

	/*
	 * @see net.simplebroadcast.broadcasts.BossBarBroadcast
	 * @see net.simplebroadcast.broadcasts.ChatBroadcast
	 */
	private BossBarBroadcast bossBarBroadcast = new BossBarBroadcast();
	private ChatBroadcast chatBroadcast = new ChatBroadcast();
	
	/**
	 * Starts chat and boss bar broadcast.
	 */
	public void broadcast() {
		updateBroadcastStatus();
		/* Chat broadcast */
		if (getChatBroadcastStatus() == BroadcastStatus.STOPPED) {
			if (!Main.getInstance().getConfig().getBoolean("chat.randomizeMessages")) {
				chatBroadcast.broadcast();
			} else {
				chatBroadcast.randomBroadcast();
			}
		}
		/* Boss bar broadcast */
		if (getBossBarBroadcastStatus() == BroadcastStatus.STOPPED) {
			if (!Main.getInstance().getBossBarConfig().getBoolean("randomizeMessages")) {
				bossBarBroadcast.broadcast(Main.getInstance().getBossBarConfig().getBoolean("bossbar.reduceHealthBar"));
			} else {
				bossBarBroadcast.randomBroadcast(Main.getInstance().getBossBarConfig().getBoolean("bossbar.reduceHealthBar"));
			}
		}
	}
	
	/**
	 * Updates status of broadcasts depending on various (configurable) options.
	 */
	private void updateBroadcastStatus() {
		/* Chat broadcast */
		if (Main.getInstance().getConfig().getBoolean("chat.enabled") && getChatBroadcastStatus() != BroadcastStatus.STOPPED) {
			if (!Main.getInstance().getConfig().getBoolean("chat.requireOnlinePlayers")) {
				setChatBroadcastStatus(BroadcastStatus.RUNNING);
			} else if (Main.getInstance().getConfig().getBoolean("chat.requireOnlinePlayers") && Bukkit.getOnlinePlayers().size() >= 1) {
				setChatBroadcastStatus(BroadcastStatus.RUNNING);
			} else {
				setChatBroadcastStatus(BroadcastStatus.WAITING);
			}
		} else {
			setChatBroadcastStatus(BroadcastStatus.DISABLED);
		}
		/* Boss bar broadcast */
		if (!Bukkit.getPluginManager().isPluginEnabled("BarAPI")) {
			setBossBarBroadcastStatus(BroadcastStatus.NOT_AVAILABLE);
		} else if (Main.getInstance().getBossBarConfig().getBoolean("bossbar.enabled") && getBossBarBroadcastStatus() != BroadcastStatus.STOPPED) {
			setBossBarBroadcastStatus(BroadcastStatus.RUNNING);
		} else if (!Main.getInstance().getBossBarConfig().getBoolean("bossbar.enabled")) {
			setBossBarBroadcastStatus(BroadcastStatus.DISABLED);
		}
	}
	
	/**
	 * Stops chat broadcast.
	 */
	public void cancelChatBroadcast() {
		Bukkit.getScheduler().cancelTask(chatBroadcast.getSchedulerTask());
		setChatBroadcastStatus(BroadcastStatus.STOPPED);
	}
	
	/**
	 * Stops boss bar broadcast.
	 */
	public void cancelBossBarBroadcast() {
		Bukkit.getScheduler().cancelTask(bossBarBroadcast.getSchedulerTask());
		setBossBarBroadcastStatus(BroadcastStatus.STOPPED);
	}

	/**
	 * Returns current status of chat broadcast.
	 * 
	 * @return the chatBroadcastStatus
	 */
	public BroadcastStatus getChatBroadcastStatus() {
		return chatBroadcastStatus;
	}

	/**
	 * Sets new status of chat broadcast.
	 * 
	 * @param status the chatBroadcastStatus to set
	 */
	public void setChatBroadcastStatus(BroadcastStatus status) {
		this.chatBroadcastStatus = status;
	}

	/**
	 * Returns current status of boss bar broadcast.
	 * 
	 * @return the bossBarBroadcastStatus
	 */
	public BroadcastStatus getBossBarBroadcastStatus() {
		return bossBarBroadcastStatus;
	}

	/**
	 * Sets new status of boss bar broadcast.
	 * 
	 * @param status the bossBarBroadcastStatus to set
	 */
	public void setBossBarBroadcastStatus(BroadcastStatus status) {
		this.bossBarBroadcastStatus = status;
	}
}