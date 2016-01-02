package net.simplebroadcast.broadcasts;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;
import net.simplebroadcast.Main;
import net.simplebroadcast.util.IgnoreManager;
import net.simplebroadcast.util.MessageManager;

public class ChatBroadcast {
	
	/**
	 * Repeating scheduler.
	 */
	private static int schedulerTask;
	
	/**
	 * TODO Comment
	 */
	private int messageCounter = 0;
	
	/**
	 * TODO Comment
	 */
	public void broadcast() {
		setSchedulerTask(Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), new Runnable() {
			@Override
			public void run() {
				/* Resets counter when message limit gets reached. */
				if (getMessageCounter() >= MessageManager.getChatMessages().size()) {
					setMessageCounter(0);
				}
				/* Loads all required parts of broadcast message. */
				String prefix = MessageManager.getChatPrefix();
				String suffix = MessageManager.getChatSuffix();
				String message = MessageManager.getChatMessages().get(getMessageCounter()).toString();
				String permission = MessageManager.getChatMessagePermissions().get(getMessageCounter()).toString();
				/* Broadcasts message to every player online on the server. */
				for (Player player : Bukkit.getOnlinePlayers()) {
					/* Checks if player has required permission to view the message and doesn't ignore it. */
					if (player.hasPermission(permission) && !IgnoreManager.getChatIgnoreList().contains(player.getUniqueId().toString())) {
						player.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + message + suffix));
					}
				}
				/* Sends message to console (if activated in config). */
				if (Main.getInstance().getConfig().getBoolean("chat.showMessagesInConsole")) {
					ConsoleCommandSender console = Bukkit.getConsoleSender();
					console.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + message + suffix));
				}
				
				/* TODO
				 * Add command executor if message starts with "/".
				 * Add JSON broadcast method if message starts with "JSON:".
				 * */
				
				/* Increases message counter to load next message. */
				setMessageCounter(getMessageCounter() + 1);
			}
		}, 0L, Main.getInstance().getConfig().getInt("chat.delay") * 20L));
	}
	
	/**
	 * TODO Comment
	 */
	public void randomBroadcast() {
		setSchedulerTask(Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), new Runnable() {
			@Override
			public void run() {
				// TODO
			}
		}, 0L, Main.getInstance().getConfig().getInt("chat.delay") * 20L));
	}
	
	/**
	 * Returns scheduler task.
	 * 
	 * @return the schedulerTask
	 */
	public static int getSchedulerTask() {
		return schedulerTask;
	}
	
	/**
	 * Sets scheduler task.
	 * 
	 * @param schedulerTask the schedulerTask to set
	 */
	public static void setSchedulerTask(int schedulerTask) {
		ChatBroadcast.schedulerTask = schedulerTask;
	}
	
	/**
	 * Returns message counter.
	 * 
	 * @return the messageCounter
	 */
	public int getMessageCounter() {
		return messageCounter;
	}
	
	/**
	 * Sets message counter.
	 * 
	 * @param messageCounter the messageCounter to set
	 */
	public void setMessageCounter(int messageCounter) {
		this.messageCounter = messageCounter;
	}
}