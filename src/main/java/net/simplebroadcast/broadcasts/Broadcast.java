package net.simplebroadcast.broadcasts;

import org.bukkit.Bukkit;

import net.simplebroadcast.Main;

public class Broadcast {
	
	/**
	 * Status of chat and boss bar broadcast.
	 */
	private static BroadcastStatus chatBroadcastStatus = BroadcastStatus.STOPPED;
	private static BroadcastStatus bossBarBroadcastStatus = BroadcastStatus.STOPPED;
	
	/*
	 * (non-Javadoc)
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
		if (getChatBroadcastStatus() == BroadcastStatus.READY) {
			ChatBroadcast.setMessageCounter(0);
			if (!Main.getInstance().getConfig().getBoolean("chat.randomizeMessages")) {
				chatBroadcast.broadcast();
			} else {
				chatBroadcast.randomBroadcast();
			}
			setChatBroadcastStatus(BroadcastStatus.RUNNING);
		}
		/* Boss bar broadcast */
		if (getBossBarBroadcastStatus() == BroadcastStatus.READY) {
			if (!Main.getInstance().getBossBarConfig().getBoolean("randomizeMessages")) {
				bossBarBroadcast.broadcast(Main.getInstance().getBossBarConfig().getBoolean("bossbar.reduceHealthBar"));
			} else {
				bossBarBroadcast.randomBroadcast(Main.getInstance().getBossBarConfig().getBoolean("bossbar.reduceHealthBar"));
			}
			setBossBarBroadcastStatus(BroadcastStatus.RUNNING);
		}
	}
	
	/**
	 * Updates status of broadcasts depending on various (configurable) options.
	 */
	private void updateBroadcastStatus() {
		/* Chat broadcast */
		if (Main.getInstance().getConfig().getBoolean("chat.enabled") && (getChatBroadcastStatus() == BroadcastStatus.STOPPED || getChatBroadcastStatus() == BroadcastStatus.WAITING)) {
			if (!Main.getInstance().getConfig().getBoolean("chat.requireOnlinePlayers")) {
				setChatBroadcastStatus(BroadcastStatus.READY);
			} else if (Main.getInstance().getConfig().getBoolean("chat.requireOnlinePlayers") && Bukkit.getOnlinePlayers().size() > 0) {
				setChatBroadcastStatus(BroadcastStatus.READY);
			} else {
				setChatBroadcastStatus(BroadcastStatus.WAITING);
			}
		} else if (!Main.getInstance().getConfig().getBoolean("chat.enabled")) {
			setChatBroadcastStatus(BroadcastStatus.DISABLED);
		}
		/* Boss bar broadcast */
		if (!Bukkit.getPluginManager().isPluginEnabled("BarAPI")) {
			setBossBarBroadcastStatus(BroadcastStatus.NOT_AVAILABLE);
		} else if (Main.getInstance().getBossBarConfig().getBoolean("bossbar.enabled") && getBossBarBroadcastStatus() == BroadcastStatus.STOPPED) {
			setBossBarBroadcastStatus(BroadcastStatus.READY);
		} else if (!Main.getInstance().getBossBarConfig().getBoolean("bossbar.enabled")) {
			setBossBarBroadcastStatus(BroadcastStatus.DISABLED);
		}
	}
	
	/**
	 * Stops chat broadcast.
	 */
	public void cancelChatBroadcast() {
		Bukkit.getScheduler().cancelTask(ChatBroadcast.getSchedulerTask());
		setChatBroadcastStatus(BroadcastStatus.STOPPED);
	}
	
	/**
	 * Stops boss bar broadcast.
	 */
	public void cancelBossBarBroadcast() {
		Bukkit.getScheduler().cancelTask(BossBarBroadcast.getSchedulerTask());
		setBossBarBroadcastStatus(BroadcastStatus.STOPPED);
	}
	
	/**
	 * Returns current status of chat broadcast.
	 * 
	 * @return the chatBroadcastStatus
	 */
	public static BroadcastStatus getChatBroadcastStatus() {
		return chatBroadcastStatus;
	}
	
	/**
	 * Sets new status of chat broadcast.
	 * 
	 * @param status the chatBroadcastStatus to set
	 */
	public static void setChatBroadcastStatus(BroadcastStatus status) {
		Broadcast.chatBroadcastStatus = status;
	}
	
	/**
	 * Returns current status of boss bar broadcast.
	 * 
	 * @return the bossBarBroadcastStatus
	 */
	public static BroadcastStatus getBossBarBroadcastStatus() {
		return bossBarBroadcastStatus;
	}
	
	/**
	 * Sets new status of boss bar broadcast.
	 * 
	 * @param status the bossBarBroadcastStatus to set
	 */
	public static void setBossBarBroadcastStatus(BroadcastStatus status) {
		Broadcast.bossBarBroadcastStatus = status;
	}
}