package net.simplebroadcast.broadcasts;

import net.simplebroadcast.Main;
import net.simplebroadcast.methods.Methods;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class ChatBroadcast {
	
	private int msg;
	private int previousMessage;
	private static int messageTask;
	private static int counter = 0;
	private static int running = 1;
	private Methods methods = new Methods();
	private boolean prefix_bool = Main.getPlugin().getConfig().getBoolean("prefix.enabled");
	private boolean suffix_bool = Main.getPlugin().getConfig().getBoolean("suffix.enabled");
	private String prefix = methods.addVariables(Main.getPlugin().getConfig().getString("prefix.prefix"));
	private String suffix = methods.addVariables(Main.getPlugin().getConfig().getString("suffix.suffix"));
	
	public void chatBroadcast() {
		if (getRunning() == 3) {
			return;
		}
		if (Main.getPlugin().getConfig().getBoolean("randomizemessages")) {
			setMessageTask(Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(), new Runnable() {
				@Override
				public void run() {
					/*
					 * Gets and broadcasts the messages in a random order.
					 */
					msg = (int) (Math.random() * Main.chatMessages.size());
					if (msg != previousMessage) {
						previousMessage = msg;
					} else {
						msg += (previousMessage < Main.chatMessages.size() - 1) ? 1 : ((previousMessage > 1) ? -1 : 0);
						previousMessage = msg;
					}
					String message = Main.chatMessages.get(msg);
					msg = 0;
					/*
					 * Starts broadcasting the messages.
					 * If message starts with "/" it's handled as a command.
					 */
					if (message.startsWith("/")) {
						if (message.contains("%n/")) {
							for (String c_msg : message.substring(1).split("%n/")) {
								methods.performCommand(c_msg);
							}
						} else {
							message = message.substring(1);
							methods.performCommand(message);
						}
						return;
					}
					if (message.startsWith("JSON:")) {
						JsonMessage.sendJSONText(message.replace("JSON:", ""));
						return;
					}
					for (Player p : Bukkit.getServer().getOnlinePlayers()) {
						if (!Main.ignoredPlayers.contains(methods.getUUID(p.getName()))) {
							p.sendMessage(ChatColor.translateAlternateColorCodes('&', "§f" + (prefix_bool ? prefix + " " : "") + methods.addPlayerVariables(message, p) + (suffix_bool ? " " + suffix : "")));
						}
					}
					/*
					 * Checks if messages shall be broadcasted in the console.
					 */
					if (Main.getPlugin().getConfig().getBoolean("sendmessagestoconsole")) {
						ConsoleCommandSender console = Bukkit.getConsoleSender();
						console.sendMessage(ChatColor.translateAlternateColorCodes('&', "§f" + (prefix_bool ? prefix + " " : "") + methods.addVariables(message) + (suffix_bool ? " " + suffix : "")));
					}
				}
			}, 0L, Main.getPlugin().getConfig().getInt("delay") * 20L));
		} else {
			setMessageTask(Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(), new Runnable() {
				@Override
				public void run() {
					if (counter < Main.chatMessages.size()) {
						broadcast();
					} else {
						setCounter(0);
						broadcast();
					}
				}
			}, 0L, Main.getPlugin().getConfig().getInt("delay") * 20L));
		}
	}
	
	private void broadcast() {
		final String message = Main.chatMessages.get(counter);
		/*
		 * Starts broadcasting the messages.
		 * If message starts with "/" it's handled as a command.
		 */
		if (message.startsWith("/")) {
			if (message.contains("%n")) {
				for (String msg : message.substring(1).split("%n/")) {
					methods.performCommand(msg);
				}
				counter++;
			} else {
				String command = message.substring(1);
				methods.performCommand(command);
				counter++;
			}
			return;
		}
		if (message.startsWith("JSON:")) {
			JsonMessage.sendJSONText(message.replace("JSON:", ""));
			counter++;
			return;
		}
		Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), new Runnable() {
			@Override
			public void run() {
				for (Player p : Bukkit.getServer().getOnlinePlayers()) {
					if (!Main.ignoredPlayers.contains(methods.getUUID(p.getName()))) {
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', "§f" + (prefix_bool ? prefix + " " : "") + methods.addPlayerVariables(message, p) + (suffix_bool ? " " + suffix : "")));
					}
				}
			}
		});
		/*
		 * Checks if messages shall be broadcasted in the console.
		 */
		if (Main.getPlugin().getConfig().getBoolean("sendmessagestoconsole")) {
			ConsoleCommandSender console = Bukkit.getConsoleSender();
			console.sendMessage(ChatColor.translateAlternateColorCodes('&', "§f" + (prefix_bool ? prefix + " " : "") + methods.addVariables(message) + (suffix_bool ? " " + suffix : "")));
		}
		counter++;
	}

	/**
	 * Gets the running Integer
	 * @return the Integer
	 */
	public static int getRunning() {
		return running;
	}

	/**
	 * Gets the task id of MessageRunnable
	 * @return the task id
	 */
	public static int getMessageTask() {
		return messageTask;
	}

	/**
	 * Gets the counter MessageRunnable
	 * @return the counter
	 */
	public static int getCounter() {
		return counter;
	}

	/**
	 * Sets the counter
	 * @param counter new counter
	 */
	public static void setCounter(int counter) {
		ChatBroadcast.counter = counter;
	}

	/**
	 * Sets the running Integer
	 * @param running the new Integer
	 */
	public static void setRunning(int running) {
		ChatBroadcast.running = running;
	}

	/**
	 * Sets the task id of MessageRunnable
	 * @param messageTask the new task id
	 */
	public static void setMessageTask(int messageTask) {
		ChatBroadcast.messageTask = messageTask;
	}
}