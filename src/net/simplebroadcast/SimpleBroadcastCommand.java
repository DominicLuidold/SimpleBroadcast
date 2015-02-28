package net.simplebroadcast;

import java.io.File;
import java.io.IOException;
import java.util.List;

import me.confuser.barapi.BarAPI;
import net.simplebroadcast.methods.BossBarMethods;
import net.simplebroadcast.methods.Methods;
import net.simplebroadcast.methods.UpdatingMethods;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class SimpleBroadcastCommand implements CommandExecutor {
	
	private Methods methods = new Methods();
	private BossBarMethods bossBarMethods = new BossBarMethods();
	private UpdatingMethods updatingMethods = new UpdatingMethods();
	private String err_need_Perm = "§cYou do not have access to that command.";
	private boolean prefix_bool = Main.getPlugin().getConfig().getBoolean("prefix.enabled");
	private boolean suffix_bool = Main.getPlugin().getConfig().getBoolean("suffix.enabled");
	private String prefix = methods.addVariables(Main.getPlugin().getConfig().getString("prefix.prefix"));
	private String suffix = methods.addVariables(Main.getPlugin().getConfig().getString("suffix.suffix"));

	public boolean onCommand(final CommandSender cs, Command cmd, String label, final String[] args) {
		if (args.length == 0) {
			/*
			 * SB
			 * Shows information about the plugin.
			 */
			if (!cs.hasPermission("simplebroadcast.info")) {
				cs.sendMessage(err_need_Perm);
				return true;
			}
			cs.sendMessage("§e--------- §fInfo: SimpleBroadcast §e---------");
			cs.sendMessage("§6Author:§f KingDome24");
			cs.sendMessage("§6Version:§f " + Main.getPlugin().getDescription().getVersion());
			cs.sendMessage("§6Website:§f " + Main.getPlugin().getDescription().getWebsite());
			if (Main.getPlugin().getConfig().getBoolean("checkforupdates")) {
				Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), new Runnable() {
					@Override
					public void run() {
						if (updatingMethods.updateAvailable()) {
							cs.sendMessage("§6Update:§f An update is available: v" + updatingMethods.getUpdateNumber());
						} else {
							cs.sendMessage("§6Update:§f There are no updates available.");
						}
					}
				});
			} else {
				cs.sendMessage("§6Update:§f Checking for updates is disabled.");
			}
			if (Bukkit.getPluginManager().isPluginEnabled("BarAPI") && Main.getBossBarConfig().getBoolean("enabled") && BossBarMethods.getBarRunning() == 1) {
				cs.sendMessage("§6Boss bar broadcast:§f The boss bar integration is enabled and broadcasts.");
			} else if (Bukkit.getPluginManager().isPluginEnabled("BarAPI") && Main.getBossBarConfig().getBoolean("enabled") && BossBarMethods.getBarRunning() == 0) {
				cs.sendMessage("§6Boss bar broadcast:§f The boss bar integration is enabled.");
			} else {
				cs.sendMessage("§6Boss bar broadcast:§f The boss bar integration is disabled.");
			}
		} else if (args.length >= 1) {
			/*
			 * START
			 * Starts the chat broadcast.
			 */
			if (args[0].equalsIgnoreCase("start")) {
				if (!cs.hasPermission("simplebroadcast.start")) {
					cs.sendMessage(err_need_Perm);
					return true;
				}
				if (MessageRunnable.getRunning() == 0) {
					methods.broadcast();
					cs.sendMessage("[Simple§cBroadcast]§r Started broadcast.");
					MessageRunnable.setRunning(1);
				} else if (MessageRunnable.getRunning() == 3) {
					cs.sendMessage("[Simple§cBroadcast]§r Broadcast is disabled (as set in the config).");
				} else {
					cs.sendMessage("[Simple§cBroadcast]§r Broadcast is already started!");
				}
			/*
			 * STOP
			 * Stops the chat broadcast.
			 */
			} else if (args[0].equalsIgnoreCase("stop")) {
				if (!cs.hasPermission("simplebroadcast.stop")) {
					cs.sendMessage(err_need_Perm);
					return true;
				}
				if (MessageRunnable.getRunning() == 1) {
					Bukkit.getServer().getScheduler().cancelTask(MessageRunnable.getMessageTask());
					cs.sendMessage("[Simple§cBroadcast]§r Cancelled broadcast.");
					MessageRunnable.setRunning(0);
				} else if (MessageRunnable.getRunning() == 3) {
					cs.sendMessage("[Simple§cBroadcast]§r Broadcast is already disabled (as set in the config)!");
				} else {
					cs.sendMessage("[Simple§cBroadcast]§r Broadcast is already cancelled!");
				}
			/*
			 * RELOAD
			 * Reloads the configs and all the broadcasts.
			 */
			} else if (args[0].equalsIgnoreCase("reload")) {
				if (!cs.hasPermission("simplebroadcast.relaod")) {
					cs.sendMessage(err_need_Perm);
					return true;
				}
				Bukkit.getServer().getScheduler().cancelTask(BossBarMethods.getBarTask());
				Bukkit.getServer().getScheduler().cancelTask(MessageRunnable.getMessageTask());
				
				Main.getPlugin().reloadConfig();
				Main.loadChatMessages();
				Main.loadBossBarMessages();
				BossBarMethods.setBarRunning(1);
				bossBarMethods.barBroadcast();
				
				if (!Main.getPlugin().getConfig().getBoolean("requiresonlineplayers")) {
					methods.broadcast();
					cs.sendMessage("[Simple§cBroadcast]§r Reloaded the config(s) successfully.");
					if (MessageRunnable.getRunning() != 3)
						MessageRunnable.setRunning(1);
				} else {
					cs.sendMessage("[Simple§cBroadcast]§r Reloaded the config(s) successfully.");
					if (Bukkit.getOnlinePlayers().size() >= 1) {
						methods.broadcast();
						if (!(MessageRunnable.getRunning() == 3))
							MessageRunnable.setRunning(1);
					} else {
						if (MessageRunnable.getRunning() != 3)
							MessageRunnable.setRunning(0);
					}
				}
			/*
			 * LIST
			 * Lists all the chat messages.
			 */
			} else if (args[0].equalsIgnoreCase("list")) {
				if (!cs.hasPermission("simplebroadcast.list")) {
					cs.sendMessage(err_need_Perm);
					return true;
				}
				cs.sendMessage("§e--------- §fMessages: SimpleBroadcast §e---------");
				for (int messageID = 0; messageID < Main.chatMessages.size(); messageID++) {
					if (cs instanceof Player) {
						Player p = (Player) cs;
						cs.sendMessage(ChatColor.translateAlternateColorCodes('&', "§6" + (messageID+1) + ".§f " + (prefix_bool ? prefix + " " : "") + methods.addVariablesP(Main.chatMessages.get(messageID), p) + (suffix_bool ? " " + suffix : "")));
					} else {
						cs.sendMessage(ChatColor.translateAlternateColorCodes('&', "§6" + (messageID+1) + ".§f " + (prefix_bool ? prefix + " " : "") + methods.addVariables(Main.chatMessages.get(messageID)) + (suffix_bool ? " " + suffix : "")));
					}
				}
			/*
			 * NOW
			 * Broadcasts the entered message (which already exists).
			 */
			} else if (args[0].equalsIgnoreCase("now")) {
				if (!cs.hasPermission("simplebroadcast.now")) {
					cs.sendMessage(err_need_Perm);
					return true;
				}
				if (args.length == 1) {
					cs.sendMessage("§cPlease enter a message number.");
					return true;
				}
				List<String> ignoredPlayers = Main.getIgnoreConfig().getStringList("players");
				/*
				 * Broadcasts the message.
				 */
				try {
					if (Integer.parseInt(args[1])-1 > -1 && Integer.parseInt(args[1])-1 < Main.chatMessages.size()) {
						String message = Main.chatMessages.get(Integer.parseInt(args[1])-1);
						for (Player p : Bukkit.getOnlinePlayers()) {
							if (!ignoredPlayers.contains(methods.getUUID(p.getName()))) {
								cs.sendMessage(ChatColor.translateAlternateColorCodes('&', (prefix_bool ? prefix + " " : "") + methods.addVariablesP(message, p) + (suffix_bool ? " " + suffix : "")));
							}
						}
						if (Main.getPlugin().getConfig().getBoolean("sendmessagestoconsole")) {
							Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', (prefix_bool ? prefix + " " : "") + methods.addVariables(message) + (suffix_bool ? " " + suffix : "")));
						}
						cs.sendMessage("§2Successfully broadcasted message.");
					} else {
						cs.sendMessage("§cThere are only " + Main.chatMessages.size() + " messages available which you can broadcast.");
					}
				} catch (NumberFormatException nfe) {
					cs.sendMessage("§cPlease enter a valid number.");
				}
			/*
			 * NEXT
			 * Skips the next message in the queue.
			 * Only applicable if "randomizemessages" is set to "false" in the config.yml.
			 */
			} else if (args[0].equalsIgnoreCase("next")) {
				if (!cs.hasPermission("simplebroadcast.next")) {
					cs.sendMessage(err_need_Perm);
					return true;
				}
				if (Main.getPlugin().getConfig().getBoolean("randomizemessages")) {
					cs.sendMessage("§cSkipping messages only works if \"randomizemessages\" is set to \"false\" in the config.yml");
					return true;
				}
				if (MessageRunnable.getCounter() < Main.chatMessages.size()) {
					cs.sendMessage("§2Successfully skipped message " + (MessageRunnable.getCounter()+1) + ".");
					MessageRunnable.setCounter(MessageRunnable.getCounter()+1);
				} else {
					cs.sendMessage("§2Successfully skipped message 1.");
					MessageRunnable.setCounter(1);
				}
			/*
			 * ADD
			 * Adds the entered message to the chat message list. 
			 */
			} else if (args[0].equalsIgnoreCase("add")) {
				if (!cs.hasPermission("simplebroadcast.add")) {
					cs.sendMessage(err_need_Perm);
					return true;
				}
				if (args.length < 1) {
					cs.sendMessage("§cPlease enter a message which you want to add.");
					return true;
				}
				List<String> messages = Main.getPlugin().getConfig().getStringList("messages");
				StringBuilder message = new StringBuilder();
				for (int i = 1; i < args.length; i++) {
					message.append(" ").append(args[i]);
				}
				messages.add(message.substring(1));
				Main.getPlugin().getConfig().set("messages", messages);
				Main.getPlugin().saveConfig();
				Main.loadChatMessages();
				cs.sendMessage("§2Successfully added message");
			/*
			 * REMOVE
			 * Removes the message with the entered message id.
			 */
			} else if (args[0].equalsIgnoreCase("remove")) {
				if (!cs.hasPermission("simplebroadcast.remove")) {
					cs.sendMessage(err_need_Perm);
					return true;
				}
				if (args.length < 1) {
					cs.sendMessage("§cPlease enter the number of a message.");
					return true;
				}
				Main.getPlugin().reloadConfig();
				List<String> messages = Main.getPlugin().getConfig().getStringList("messages");
				try {
					if (Integer.parseInt(args[1])-1 > -1 && Integer.parseInt(args[1])-1 < messages.size()) {
						messages.remove(Integer.parseInt(args[1])-1);
						Main.getPlugin().getConfig().set("messages", messages);
						Main.getPlugin().saveConfig();
						Main.loadChatMessages();
						cs.sendMessage("§cSuccessfully removed message.");
					} else {
						cs.sendMessage("§cPlease choose a number between 1 and " + messages.size() + ".");
					}
				} catch (NumberFormatException nfe) {
					cs.sendMessage("§cPlease enter a valid number.");
				}
			/*
			 * BROADCAST
			 * Broadcasts the entered message.
			 */
			} else if (args[0].equalsIgnoreCase("broadcast")) {
				if (!cs.hasPermission("simplebroadcast.broadcast")) {
					cs.sendMessage(err_need_Perm);
					return true;
				}
				if (args.length < 1) {
					cs.sendMessage("§cPlease enter a message which you want to broadcast.");
					return true;
				}
				StringBuilder message = new StringBuilder();
				if (prefix_bool) {
					message.append(ChatColor.translateAlternateColorCodes('&', prefix));
				}
				for (int i = 1; i < args.length; i++) {
					message.append(" ").append(ChatColor.translateAlternateColorCodes('&', args[i]));
				}
				if (suffix_bool) {
					message.append(" " + ChatColor.translateAlternateColorCodes('&', suffix));
				}
				for (Player p : Bukkit.getOnlinePlayers()) {
					p.sendMessage(methods.addVariablesP(message.toString(), p));
				}
				if (Main.getPlugin().getConfig().getBoolean("sendmessagestoconsole")) {
					Bukkit.getConsoleSender().sendMessage(methods.addVariables(message.toString()));
				}
				cs.sendMessage("§2Successfully broadcasted message.");
			/*
			 * RAW
			 * Broadcasts the message without any formatting.
			 */
			} else if (args[0].equalsIgnoreCase("raw")) {
				if (!cs.hasPermission("simplebroadcast.raw")) {
					cs.sendMessage(err_need_Perm);
					return true;
				}
				if (args.length < 1) {
					cs.sendMessage("§cPlease enter a message which you want to broadcast.");
					return true;
				}
				StringBuilder message = new StringBuilder();
				for (int i = 1; i < args.length; i++) {
					message.append(" ").append(ChatColor.translateAlternateColorCodes('&', args[i]));
				}
				for (Player p : Bukkit.getOnlinePlayers()) {
					p.sendMessage(methods.addVariablesP(message.toString().substring(1), p));
				}
				if (Main.getPlugin().getConfig().getBoolean("sendmessagestoconsole")) {
					Bukkit.getConsoleSender().sendMessage(methods.addVariables(message.toString().substring(1)));
				}
				cs.sendMessage("§2Successfully broadcasted message.");
			/*
			 * IGNORE
			 * Adds/removes the player from the ignore list.
			 */
			} else if (args[0].equalsIgnoreCase("ignore")) {
				if (!cs.hasPermission("simplebroadcast.ignore")) {
					cs.sendMessage(err_need_Perm);
					return true;
				}
				if (args.length == 1) {
					cs.sendMessage("§cPlease enter a name.");
					return true;
				}
				if (args.length > 2) {
					cs.sendMessage("§cPlease enter only one name.");
					return true;
				}
				if (args[1].equalsIgnoreCase("me") && !(cs instanceof Player)) {
					cs.sendMessage("§cPlease use the option in the config to turn of the messages in console.");
					return true;
				}
				final List<String> ignorePlayers = Main.getIgnoreConfig().getStringList("players");
				Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), new Runnable() {
					@Override
					public void run() {
						String uuidCheck = null;
						/*
						 * Adds/removes the player from the ignore list.
						 * Only applicable for "/sb ignore me".
						 */
						if (args[1].equalsIgnoreCase("me")) {
							Player p = (Player) cs;
							uuidCheck = methods.getUUID(cs.getName());
							if (uuidCheck == null) {
								cs.sendMessage("§cCouldn't check UUID for player \"" + cs.getName() + "\", please try again later or check spelling.");
								return;
							}
							if (!ignorePlayers.contains(uuidCheck)) {
								ignorePlayers.add(uuidCheck);
								if (Bukkit.getPluginManager().isPluginEnabled("BarAPI") && Main.getBossBarConfig().getBoolean("enabled")) {
									BarAPI.removeBar(p);
								}
								cs.sendMessage("[Simple§cBroadcast]§r Now you don't receive any messages.");
							} else {
								ignorePlayers.remove(uuidCheck);
								cs.sendMessage("[Simple§cBroadcast]§r Now you receive the messages again.");
							}
						/*
						 * Adds/removes the entered player from the list.
						 * Only applicable for "/sb ignore PLAYER".
						 */
						} else {
							uuidCheck = methods.getUUID(args[1]);
							if (uuidCheck == null) {
								cs.sendMessage("§cCouldn't check UUID for player \"" + args[1] + "\", please try again later or check spelling.");
								return;
							}
							if (!ignorePlayers.contains(uuidCheck)) {
								ignorePlayers.add(uuidCheck);
								if (Bukkit.getPluginManager().isPluginEnabled("BarAPI") && Main.getBossBarConfig().getBoolean("enabled") && Bukkit.getServer().getPlayer(args[1]) != null) {
									BarAPI.removeBar(Bukkit.getPlayer(args[1]));
								}
								cs.sendMessage("[Simple§cBroadcast]§f The player §7" + args[1] + "§f now doesn't receive any messages.");
							} else {
								ignorePlayers.remove(uuidCheck);
								cs.sendMessage("[Simple§cBroadcast]§f The player §7" + args[1] + "§f now receives the messages again.");
							}
						}
						/*
						 * Saves the ignore file.
						 */
						File ignore = new File (Main.getPlugin().getDataFolder(), "ignore.yml");
						FileConfiguration ignoreConfig = YamlConfiguration.loadConfiguration(ignore);
						ignoreConfig.set("players", ignorePlayers);
						try {
							ignoreConfig.save(ignore);
						} catch (IOException e) {
							Main.logWarning("Couldn't save the ignore.yml. Error!");
						}
					}
				});
			/*
			 * UPDATE
			 * Toggles the update boolean in the config.
			 */
			} else if (args[0].equalsIgnoreCase("update")) {
				if (!cs.hasPermission("simplebroadcast.update.toggle")) {
					cs.sendMessage(err_need_Perm);
					return true;
				}
				Main.getPlugin().getConfig().set("checkforupdates", !Main.getPlugin().getConfig().getBoolean("checkforupdates"));
				Main.getPlugin().saveConfig();
				Main.getPlugin().reloadConfig();
				cs.sendMessage("[Simple§cBroadcast]§r The update check function is now " + (Main.getPlugin().getConfig().getBoolean("checkforupdates") ? "enabled." : "disabled."));
			/*
			 * HELP
			 * Shows the help pages.
			 */
			} else if (args[0].equalsIgnoreCase("help")) {
				if (!cs.hasPermission("simplebroadcast.help")) {
					cs.sendMessage(err_need_Perm);
					return true;
				}
				if (args.length == 0 || args.length == 1) {
					methods.helpList(1, cs);
				} else {
					try {
						if (Integer.parseInt(args[1]) > 0 && Integer.parseInt(args[1]) <= 3) {
							methods.helpList(Integer.parseInt(args[1]), cs);
						} else {
							cs.sendMessage("§cThere are only 3 pages available.");
						}
					} catch (NumberFormatException nfe) {
						cs.sendMessage("§cPlease enter a valid number.");
					}
				}
			/*
			 * UNKNOWN COMMAND
			 * Shows the "Unknown command" message.
			 */
			} else {
				if (!(args.length >= 2 && args[0].equalsIgnoreCase("bossbar"))) {
					if (!cs.hasPermission("simplebroadcast.help")) {
						cs.sendMessage(err_need_Perm);
						return true;
					}
					cs.sendMessage("§cUnknown command. Type \"" + (cs instanceof Player ? "/" : "") + "simplebroadcast help\" for help.");
				}
			}
		}
		if (args.length >= 2 && args[0].equalsIgnoreCase("bossbar")) {
			if (!Bukkit.getPluginManager().isPluginEnabled("BarAPI")) {
				if (!cs.hasPermission("simplebroadcast.bossbar.help")) {
					cs.sendMessage(err_need_Perm);
					return true;
				}
				cs.sendMessage("§cTo use the boss bar, please download the BarAPI.");
				cs.sendMessage("§chttp://dev.bukkit.org/bukkit-plugins/bar-api");
				return true;
			}
			/*
			 * START
			 * Starts the boss bar broadcast.
			 */
			if (args[1].equalsIgnoreCase("start")) {
				if (!cs.hasPermission("simplebroadcast.bossbar.start")) {
					cs.sendMessage(err_need_Perm);
					return true;
				}
				if (BossBarMethods.getBarRunning() == 0) {
					BossBarMethods.setBarRunning(1);
					bossBarMethods.barBroadcast();
					cs.sendMessage("[Simple§cBroadcast]§r Started boss bar broadcast.");
				} else if (BossBarMethods.getBarRunning() == 3) {
					cs.sendMessage("[Simple§cBroadcast]§r Boss bar broadcast is disabled (as set in the bossbar.yml)!");
				} else {
					cs.sendMessage("[Simple§cBroadcast]§r Boss bar broadcast is already started!");
				}
			/*
			 * STOP
			 * Stops the chat broadcast.
			 */
			} else if (args[1].equalsIgnoreCase("stop")) {
				if (!cs.hasPermission("simplebroadcast.bossbar.stop")) {
					cs.sendMessage(err_need_Perm);
					return true;
				}
				if (BossBarMethods.getBarRunning() == 1) {
					Bukkit.getScheduler().cancelTask(BossBarMethods.getBarTask());
					BossBarMethods.setBarRunning(0);
					for (Player p : Bukkit.getServer().getOnlinePlayers()) {
						BarAPI.removeBar(p);
					}
					cs.sendMessage("[Simple§cBroadcast]§r Cancelled boss bar broadcast.");
				} else if (BossBarMethods.getBarRunning() == 3) {
					cs.sendMessage("[Simple§cBroadcast]§r Boss bar broadcast is already disabled (as set in the bossbar.yml)!");
				} else {
					cs.sendMessage("[Simple§cBroadcast]§r Boss bar broadcast is already cancelled!");
				}
			/*
			 * LIST
			 * Shows all (boss bar broadcast) messages.
			 */
			} else if (args[1].equalsIgnoreCase("list")) {
				if (!cs.hasPermission("simplebroadcast.bossbar.list")) {
					cs.sendMessage(err_need_Perm);
					return true;
				}
				cs.sendMessage("§e------------- §fBoss bar messages: SimpleBroadcast §e-------------");
				for (int messageID = 0; messageID < Main.bossBarMessages.size(); messageID++) {
					if (cs instanceof Player) {
						Player p = (Player) cs;
						cs.sendMessage(ChatColor.translateAlternateColorCodes('&', "§6" + (messageID+1) + ".§f " + methods.addVariablesP(Main.bossBarMessages.get(messageID), p)));
					} else {
						cs.sendMessage(ChatColor.translateAlternateColorCodes('&', "§6" + (messageID+1) + ".§f " + methods.addVariables(Main.bossBarMessages.get(messageID))));
					}
				}
			/* 
			 * NEXT
			 * Skips the next message in the list.
			 * Only applicable if "randomizemessages" is set to "false".
			 */
			} else if (args[1].equalsIgnoreCase("next")) {
				if (!cs.hasPermission("simplebroadcast.bossbar.next")) {
					cs.sendMessage(err_need_Perm);
					return true;
				}
				if (BossBarMethods.getBarRunning() == 3) {
					cs.sendMessage("[Simple§cBroadcast]§r Boss bar broadcast is disabled (as set in the bossbar.yml)!");
					cs.sendMessage("[Simple§cBroadcast]§r Please enable it to use this command.");
					return true;
				}
				if (Main.getBossBarConfig().getBoolean("randomizemessages")) {
					cs.sendMessage("§cSkipping messages only works if \"randomizemessages\" is set to \"false\" in the bossbar.yml.");
					return true;
				}
				if (BossBarMethods.getBarCounter() < Main.getBossBarConfig().getStringList("messages").size()) {
					cs.sendMessage("§2Successfully skipped message " + (BossBarMethods.getBarCounter()+1) + ".");
					BossBarMethods.setBarCounter(BossBarMethods.getBarCounter()+1);
				} else {
					BossBarMethods.setBarCounter(1);
					cs.sendMessage("§2Successfully skipped message 1.");
				}
			} else {
				if (!cs.hasPermission("simplebroadcast.bossbar.help")) {
					cs.sendMessage(err_need_Perm);
					return true;
				}
				cs.sendMessage("§cUnknown command. Type \"" + (cs instanceof Player ? "/" : "") + "simplebroadcast help\" or \"" + (cs instanceof Player ? "/" : "") + "simplebroadcast bossbar help\" for help.");
			}
		}
		return false;
	}
}