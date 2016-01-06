package net.simplebroadcast.commands;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.simplebroadcast.Main;
import net.simplebroadcast.broadcasts.Broadcast;
import net.simplebroadcast.broadcasts.BroadcastStatus;
import net.simplebroadcast.broadcasts.ChatBroadcast;
import net.simplebroadcast.util.IgnoreManager;
import net.simplebroadcast.util.MessageManager;

public class BroadcastCommand implements CommandExecutor {
	
	/*
	 * (non-Javadoc)
	 * @see net.simplebroadcast.broadcasts.Broadcast
	 * @see net.simpleboradcast.util.IgnoreManager
	 */
	private Broadcast broadcast = new Broadcast();
	private ChatBroadcast chatBroadcast = new ChatBroadcast();
	private IgnoreManager ignoreManager = new IgnoreManager();
	
	/**
	 * Message which gets shown when command sender doesn't have required permission.
	 */
	private String noAccessToCommand = "§cYou do not have access to this command.";
	
	/*
	 * (non-Javadoc)
	 * @see org.bukkit.command.CommandExecutor#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length == 0) {
			if (!sender.hasPermission("simplebroadcast.info")) {
				sender.sendMessage(noAccessToCommand);
				return true;
			}
			sender.sendMessage("§e--------- §fInfo: SimpleBroadcast §e---------");
			sender.sendMessage("§6Author:§f KingDome24");
			sender.sendMessage("§6Version:§f " + Main.getInstance().getDescription().getVersion());
			sender.sendMessage("§6Website:§f " + Main.getInstance().getDescription().getWebsite());
		/* Chat broadcast commands. */
		} else if (args.length > 1 && args[0].equalsIgnoreCase("chat")) {
			/* Start - command */
			if (args[1].equalsIgnoreCase("start")) {
				/* Checks if command sender has the required permission to start the chat broadcast. */
				if (!sender.hasPermission("simplebroadcast.chat.start")) {
					sender.sendMessage(noAccessToCommand);
					return true;
				}
				/* Checks chat broadcast status and performs action. */
				if (Broadcast.getChatBroadcastStatus() == BroadcastStatus.STOPPED) {
					broadcast.broadcast();
					sender.sendMessage("§2[SimpleBroadcast] Successfully started chat broadcast.");
				} else if (Broadcast.getChatBroadcastStatus() == BroadcastStatus.DISABLED) {
					sender.sendMessage("§c[SimpleBroadcast] Chat broadcast is disabled (as set in \"config.yml\").");
				} else {
					sender.sendMessage("§c[SimpleBroadcast] Chat broadcast is already started.");
				}
			/* Stop - command */
			} else if (args[1].equalsIgnoreCase("stop")) {
				/* Checks if command sender has the required permission to stop the chat broadcast. */
				if (!sender.hasPermission("simplebroadcast.chat.stop")) {
					sender.sendMessage(noAccessToCommand);
					return true;
				}
				/* Checks chat broadcast status and performs action. */
				if (Broadcast.getChatBroadcastStatus() == BroadcastStatus.RUNNING) {
					broadcast.cancelChatBroadcast();
					sender.sendMessage("§2[SimpleBroadcast] Successfully stopped chat broadcast.");
				} else if (Broadcast.getChatBroadcastStatus() == BroadcastStatus.DISABLED) {
					sender.sendMessage("§c[SimpleBroadcast] Chat broadcast is disabled (as set in \"config.yml\").");
				} else {
					sender.sendMessage("§c[SimpleBroadcast] Chat broadcast is already stopped.");
				}
			/* List - command */
			} else if (args[1].equalsIgnoreCase("list")) {
				/* Checks if command sender has the required permission to list the chat broadcast messages. */
				if (!sender.hasPermission("simplebroadcast.chat.list")) {
					sender.sendMessage(noAccessToCommand);
					return true;
				}
				/* Loads all required parts of broadcast message. */
				String prefix = MessageManager.getChatPrefix();
				String suffix = MessageManager.getChatSuffix();
				/* Shows all chat broadcast messages to command sender. */
				sender.sendMessage("§e--------- §fChat broadcast messages: §e---------");
				for (int messageID = 0; messageID < MessageManager.getChatMessages().size(); messageID++) {
					String message = MessageManager.getChatMessages().get(messageID).toString();
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "§6" + (messageID+1) + ".§f " + prefix + message + suffix));
				}
			/* Now - command */
			} else if (args[1].equalsIgnoreCase("now")) {
				/* Checks if command sender has the required permission to broadcast existing messages. */
				if (!sender.hasPermission("simplebroadcast.chat.now")) {
					sender.sendMessage(noAccessToCommand);
					return true;
				}
				/* Checks if length of arguments is correct. */
				if (args.length < 3) {
					sender.sendMessage("§c[SimpleBroadcast] Pleaser enter a message number.");
					return true;
				}
				/* Checks if length of arguments is correct. */
				if (args.length > 3) {
					sender.sendMessage("§c[SimpleBroadcast] Pleaser enter only one message number.");
					return true;
				}
				try {
					int messageID = Integer.parseInt(args[2]) - 1;
					HashMap<Integer, String> chatMessages = MessageManager.getChatMessages();
					if (messageID > -1 && messageID < chatMessages.size()) {
						chatBroadcast.broadcastExistingMessage(messageID);
						sender.sendMessage("§2[SimpleBroadcast] Successfully broadcasted message #" + args[2] + ".");
					} else {
						sender.sendMessage("§c[SimpleBroadcast] Please choose a number between 1 and " + chatMessages.size() + ".");
					}
				} catch(NumberFormatException nfe) {
					sender.sendMessage("§c[SimpleBroadcast] Please enter a valid number.");
					return true;
				}
			/* Next - command */
			} else if (args[1].equalsIgnoreCase("next")) {
				/* Checks if command sender has the required permission to skip broadcast messages. */
				if (!sender.hasPermission("simplebroadcast.chat.next")) {
					sender.sendMessage(noAccessToCommand);
					return true;
				}
				/* Checks if "randomizeMessages" is activated. */
				if (Main.getInstance().getConfig().getBoolean("chat.randomizeMessages")) {
					sender.sendMessage("§c[SimpleBroadcast] Skipping messages only is available when \"randomizeMessages\" is set to \"false\" in config.");
					return true;
				}
				/* Gets current message counter. */
				int messageCounter = ChatBroadcast.getMessageCounter();
				/* Skips message based on message counter. */
				if (messageCounter < MessageManager.getChatMessages().size()) {
					ChatBroadcast.setMessageCounter(messageCounter + 1);
					sender.sendMessage("§2[SimpleBroadcast] Successfully skipped message #" + (messageCounter + 1) + ".");
				} else {
					ChatBroadcast.setMessageCounter(1);
					sender.sendMessage("§2[SimpleBroadcast] Successfully skipped message #1.");
				}
			/* Ignore - command */
			} else if (args[1].equalsIgnoreCase("ignore")) {
				/* Checks if command sender has the required permission to add/remove players to ignore list. */
				if (!sender.hasPermission("simplebroadcast.chat.ignore")) {
					sender.sendMessage(noAccessToCommand);
					return true;
				}
				/* Checks if length of arguments is correct. */
				if (args.length < 3) {
					sender.sendMessage("§c[SimpleBroadcast] Pleaser enter a player name.");
					return true;
				}
				/* Checks if length of arguments is correct. */
				if (args.length > 3) {
					sender.sendMessage("§c[SimpleBroadcast] Pleaser enter only one player name.");
					return true;
				}
				/* Gets player specified in command. */
				Player player = Bukkit.getServer().getPlayerExact(args[2]);
				/* Checks if player is online. */
				if (player == null) {
					// TODO Rewrite message
					sender.sendMessage("§c[SimpleBroadcast] You can only add players who are currently online.");
					return true;
				}
				/* Checks if player already is listed in ignore list and performs action. */
				if (IgnoreManager.getChatIgnoreList().contains(player.getUniqueId().toString())) {
					IgnoreManager.getChatIgnoreList().remove(player.getUniqueId().toString());
					sender.sendMessage("§2[SimpleBroadcast] Successfully removed §7" + args[2] + "§2 from ignore list.");
				} else {
					IgnoreManager.getChatIgnoreList().add(player.getUniqueId().toString());
					sender.sendMessage("§2[SimpleBroadcast] Successfully added §7" + args[2] + "§2 to ignore list.");
				}
				ignoreManager.updateChatIgnoreList();
			}
		/* Boss bar broadcast commands. */
		} else if (args.length > 1 && args[0].equalsIgnoreCase("bossbar")) {
			
		}
		return false;
	}
}