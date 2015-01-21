package net.simplebroadcast;

import java.io.File;
import java.io.IOException;
import java.util.List;

import me.confuser.barapi.BarAPI;
import net.simplebroadcast.Methods.BossBarMethods;
import net.simplebroadcast.Methods.Methods;
import net.simplebroadcast.Methods.UpdatingMethods;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class SimpleBroadcastCommand implements CommandExecutor {

	private Main plugin;
	private Methods mt = new Methods();
	private BossBarMethods bmt = new BossBarMethods();
	private UpdatingMethods um = new UpdatingMethods();
	private String err_need_Perm = "§cYou do not have access to that command.";
	boolean prefix_bool;
	boolean suffix_bool;
	String prefix;
	String suffix;


	public SimpleBroadcastCommand(Main plugin) {
		this.plugin = plugin;
		prefix_bool = plugin.getConfig().getBoolean("prefix.enabled");
		suffix_bool = plugin.getConfig().getBoolean("suffix.enabled");
		prefix = mt.addVariables(plugin.getConfig().getString("prefix.prefix"));
		suffix = mt.addVariables(plugin.getConfig().getString("suffix.suffix"));
	}

	@SuppressWarnings("deprecation")
	@Override
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
			cs.sendMessage("§e--------- §fInfo: SimpleBroadcast §e------------");
			cs.sendMessage("§6Author:§f KingDome24");
			cs.sendMessage("§6Version:§f " + plugin.getDescription().getVersion());
			cs.sendMessage("§6Website:§f " + plugin.getDescription().getWebsite());
			if (plugin.getConfig().getBoolean("checkforupdates")) {
				Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
					@Override
					public void run() {
						try {
							if (um.updateB()) {
								cs.sendMessage("§6Update:§f An update is available: " + um.updateN());
							} else {
								cs.sendMessage("§6Update:§f There are no updates available.");
							}
						} catch (NullPointerException npe) {
							cs.sendMessage("§6Upate:§f Couldn't check for updates.");
						}
					}
				});
			} else {
				cs.sendMessage("§6Update:§f You disabled the update check function.");
			}
			if (plugin.getConfig().getBoolean("pluginmetrics")) {
				cs.sendMessage("§6PluginMetrics:§f PluginMetrics is enabled and sends data.");
			} else {
				cs.sendMessage("§6PluginMetrics:§f PluginMetrics is disabled and sends no data.");
			}
			File bossbar = new File(Main.getPlugin().getDataFolder(), "bossbar.yml");
			FileConfiguration cfg_boss = YamlConfiguration.loadConfiguration(bossbar);
			if (Bukkit.getPluginManager().isPluginEnabled("BarAPI") && cfg_boss.getBoolean("enabled") && BossBarMethods.getBarRunning() == 1) {
				cs.sendMessage("§6Boss bar broadcast:§f The boss bar integration is enabled and broadcasts.");
			} else if (Bukkit.getPluginManager().isPluginEnabled("BarAPI") && cfg_boss.getBoolean("enabled") && BossBarMethods.getBarRunning() == 0) {
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
				if (plugin.getRunning() == 0) {
					mt.broadcast();
					cs.sendMessage("[Simple§cBroadcast]§r Started broadcast.");
					plugin.setRunning(1);
				} else if (plugin.getRunning() == 3) {
					cs.sendMessage("[Simple§cBroadcast]§r Broadcast is disabled as set in the config.");
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
				if (plugin.getRunning() == 1) {
					Bukkit.getServer().getScheduler().cancelTask(plugin.getMessageTask());
					cs.sendMessage("[Simple§cBroadcast]§r Cancelled broadcast.");
					plugin.setRunning(0);
				} else if (plugin.getRunning() == 3) {
					cs.sendMessage("[Simple§cBroadcast]§r Broadcast is already disabled (as set in the config)!");
				} else {
					cs.sendMessage("[Simple§cBroadcast]§r Broadcast is already cancelled!");
				}
			/*
			 * RELOAD
			 * Reloads the configs.
			 */
			} else if (args[0].equalsIgnoreCase("reload")) {
				if (!cs.hasPermission("simplebroadcast.reload")) {
					cs.sendMessage(err_need_Perm);
					return true;
				}
				Bukkit.getServer().getScheduler().cancelTask(plugin.getMessageTask());
				Bukkit.getServer().getScheduler().cancelTask(BossBarMethods.getBarTask());

				BossBarMethods.setCounter(0);
				MessageRunnable.setCounter(0);
				plugin.reloadConfig();
				plugin.loadMessages();
				BossBarMethods.setBarRunning(1);
				bmt.barBroadcast();

				if (!plugin.getConfig().getBoolean("requiresonlineplayers")) {
					mt.broadcast();
					cs.sendMessage("[Simple§cBroadcast]§r Reloaded the config(s) successfully.");
					if (plugin.getRunning() != 3)
						plugin.setRunning(1);
				} else {
					cs.sendMessage("[Simple§cBroadcast]§r Reloaded the config(s) successfully.");
					if (Bukkit.getOnlinePlayers().length >= 1) {
						mt.broadcast();
						if (!(plugin.getRunning() == 3))
							plugin.setRunning(1);
					} else {
						if (plugin.getRunning() != 3)
							plugin.setRunning(0);
					}
				}
			/*
			 * BOSSBAR
			 * Starts/stops the boss bar broadcast.
			 */
			} else if (args[0].equalsIgnoreCase("bossbar")) {
				if (!Bukkit.getPluginManager().isPluginEnabled("BarAPI")) {
					if (!cs.hasPermission("simplebroadcast.bossbar.start") && !cs.hasPermission("simplebroadcast.bossbar.stop")) {
						cs.sendMessage(err_need_Perm);
						return true;
					}
					cs.sendMessage("§cTo use the boss bar, please download the BarAPI.");
					cs.sendMessage("§chttp://dev.bukkit.org/bukkit-plugins/bar-api");
					return true;
				}
				/*
				 * Starts the boss bar broadcast.
				 */
				if (args.length >= 2 && args[1].equalsIgnoreCase("start")) {
					if (!cs.hasPermission("simplebroadcast.bossbar.start")) {
						cs.sendMessage(err_need_Perm);
						return true;
					}
					if (BossBarMethods.getBarRunning() == 0) {
						BossBarMethods.setBarRunning(1);
						bmt.barBroadcast();
						cs.sendMessage("[Simple§cBroadcast]§r Started (boss bar) broadcast.");
					} else if (BossBarMethods.getBarRunning() == 3) {
						cs.sendMessage("[Simple§cBroadcast]§r Boss bar broadcast is disabled (as set in the bossbar.yml)!");
					} else {
						cs.sendMessage("[Simple§cBroadcast]§r Boss bar broadcast is already started!");
				}
				/*
				 * Stops the boss bar broadcast.
				 */
				} else if (args.length >= 2 && args[1].equalsIgnoreCase("stop")) {
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
				} else {
					if (!cs.hasPermission("simplebroadcast.bossbar.start") && !cs.hasPermission("simplebroadcast.bossbar.stop")) {
						cs.sendMessage(err_need_Perm);
						return true;
					}
					cs.sendMessage("§cPlease use either \"" + (cs instanceof Player ? "/" : "") + "sb bossbar start\" or \"" + (cs instanceof Player ? "/" : "") + "sb bossbar stop\".");
				}
			/*
			 * LIST
			 * Shows all (chat broadcast) messages.
			 */
			} else if (args[0].equalsIgnoreCase("list")) {
				if (!cs.hasPermission("simplebroadcast.list")) {
					cs.sendMessage(err_need_Perm);
					return true;
				}
				int message_number = 1;
				plugin.reloadConfig();
				cs.sendMessage("§e--------- §fMessages: SimpleBroadcast §e-------------");
				for (String message : plugin.getConfig().getStringList("messages")) {
					if (cs instanceof Player) {
						Player p = (Player) cs;
						cs.sendMessage(ChatColor.translateAlternateColorCodes('&', "§6" + message_number + ".§f" + (prefix_bool ? " " + prefix : "") + " " + mt.addVariablesP(message, p) + (suffix_bool ? " " + suffix : "")));
					} else {
						cs.sendMessage(ChatColor.translateAlternateColorCodes('&', "§6" + message_number + ".§f" + (prefix_bool ? " " + prefix : "") + " " + mt.addVariables(message) + (suffix_bool ? " " + suffix : "")));
					}
					message_number++;
				}
			/*
			 * NOW
			 * Broadcasts an already existing message.
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
				/*
				 * Loads the ignore.yml.
				 */
				File file = new File(Main.getPlugin().getDataFolder(), "ignore.yml");
				FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
				List<String> ignoredPlayers = cfg.getStringList("players");
				/*
				 * Broadcasts the message.
				 */
				try {
					if (Integer.parseInt(args[1])-1 > -1 && Integer.parseInt(args[1])-1 < Main.globalMessages.size()) {
						String message = Main.globalMessages.get(Integer.parseInt(args[1])-1);
						for (Player p : Bukkit.getOnlinePlayers()) {
							if (!ignoredPlayers.contains(mt.getUUID(p.getName()))) {
								p.sendMessage(ChatColor.translateAlternateColorCodes('&', (prefix_bool ? prefix + " " : "") + mt.addVariablesP(message, p) + (suffix_bool ? " " + suffix : "")));
							}
						}
						if (plugin.getConfig().getBoolean("sendmessagestoconsole")) {
							ConsoleCommandSender console = Bukkit.getConsoleSender();
							console.sendMessage(ChatColor.translateAlternateColorCodes('&', (prefix_bool ? prefix + " " : "") + mt.addVariables(message) + (suffix_bool ? " " + suffix : "")));
						}
						cs.sendMessage("§2Successfully broadcasted message.");
					} else {
						cs.sendMessage("§cThere are only " + Main.globalMessages.size() + " messages available which you can broadcast.");
					}
				} catch (NumberFormatException nfe) {
					cs.sendMessage("§cPlease enter a valid number.");
				}
			/* 
			 * NEXT
			 * Skips the next message in the list.
			 * Only applicable if "randomizemessages" is set to "false".
			 */
			} else if (args[0].equalsIgnoreCase("next")) {
				if (!cs.hasPermission("simplebroadcast.next")) {
					cs.sendMessage(err_need_Perm);
					return true;
				}
				if (plugin.getConfig().getBoolean("randomizemessages")) {
					cs.sendMessage("§cSkipping messages only works if \"randomizemessages\" is set to \"false\" in the config.yml.");
					return true;
				}
				if (MessageRunnable.counter < Main.globalMessages.size()) {
					cs.sendMessage("§2Successfully skipped message " + (MessageRunnable.counter+1) + ".");
					MessageRunnable.counter++;
				} else {
					MessageRunnable.counter = 1;
					cs.sendMessage("§2Successfully skipped message 1.");
				}
			/*
			 * ADD
			 * Adds a message to the (chat) config.
			 * It automatically adds the message to the default messages list.
			 */
			} else if (args[0].equalsIgnoreCase("add")) {
				if (!cs.hasPermission("simplebroadcast.add")) {
					cs.sendMessage(err_need_Perm);
					return true;
				}
				if (!(args.length > 1)) {
					cs.sendMessage("§cPlease enter a message which you want to add.");
					return true;
				}
				List<String> addMessage= plugin.getConfig().getStringList("messages");
				StringBuilder message = new StringBuilder();
				for (int i = 1; i < args.length; i++) {
					message.append(" ").append(args[i]);
				}
				addMessage.add(message.substring(1).toString());
				plugin.getConfig().set("messages", addMessage);
				plugin.saveConfig();
				plugin.loadMessages();
				cs.sendMessage("§2Successfully added message.");
			/*
			 * REMOVE
			 * Removes a message.
			 */
			} else if (args[0].equalsIgnoreCase("remove")) {
				if (!cs.hasPermission("simplebroadcast.remove")) {
					cs.sendMessage(err_need_Perm);
					return true;
				}
				if (!(args.length > 1)) {
					cs.sendMessage("§cPlease enter a message number.");
					return true;
				}
				plugin.reloadConfig();
				List<String> removeMessage = plugin.getConfig().getStringList("messages");
				try {
					if (Integer.parseInt(args[1])-1 > -1 && Integer.parseInt(args[1])-1 < removeMessage.size()) {
						removeMessage.remove(Integer.parseInt(args[1])-1);
						plugin.getConfig().set("messages", removeMessage);
						plugin.saveConfig();
						plugin.loadMessages();
						cs.sendMessage("§2Successfully removed message.");
					} else {
						cs.sendMessage("§cPlease choose a number between 1 and " + removeMessage.size() + ".");
					}
				} catch (NumberFormatException nfe) {
					cs.sendMessage("§cPlease enter a valid number.");
				}
			/*
			 * BROADCAST
			 * Broadcasts the entered text with the prefix.
			 */
			} else if (args[0].equalsIgnoreCase("broadcast")) {
				if (!cs.hasPermission("simplebroadcast.broadcast")) {
					cs.sendMessage(err_need_Perm);
					return true;
				}
				if (!plugin.getConfig().getBoolean("prefix.enabled")) {
					cs.sendMessage("§cPlease use \"" + (cs instanceof Player ? "/" : "") + "simplebroadcast raw\" instead.");
					return true;
				}
				if (!(args.length > 1)) {
					cs.sendMessage("§cPlease enter a message which you want to broadcast.");
					return true;
				}
				StringBuilder message = new StringBuilder();
				if (plugin.getConfig().getBoolean("prefix.enabled")) {
					message.append(ChatColor.translateAlternateColorCodes('&', prefix));
				}
				for (int i = 1; i < args.length; i++) {
					message.append(" ").append(ChatColor.translateAlternateColorCodes('&', args[i]));
				}
				for (Player p : Bukkit.getServer().getOnlinePlayers()) {
					p.sendMessage(mt.addVariablesP(message.toString(), p));
				}
				if (plugin.getConfig().getBoolean("sendmessagestoconsole")) {
					ConsoleCommandSender console = Bukkit.getConsoleSender();
					console.sendMessage(mt.addVariables(message.toString()));
				}
			/*
			 * RAW
			 * Broadcasts the text without any formatting.
			 */
			} else if (args[0].equalsIgnoreCase("raw")) {
				if (!cs.hasPermission("simplebroadcast.broadcast")) {
					cs.sendMessage(err_need_Perm);
					return true;
				}
				if (!(args.length > 1)) {
					cs.sendMessage("§cPlease enter a message which you want to broadcast.");
					return true;
				}
				StringBuilder message = new StringBuilder();
				for (int i = 1; i < args.length; i++) {
					message.append(" ").append(ChatColor.translateAlternateColorCodes('&', args[i]));
				}
				for (Player p : Bukkit.getServer().getOnlinePlayers()) {
					p.sendMessage(mt.addVariablesP(message.toString().substring(1), p));
				}
				if (plugin.getConfig().getBoolean("sendmessagestoconsole")) {
					ConsoleCommandSender console = Bukkit.getConsoleSender();
					console.sendMessage(mt.addVariables(message.toString().substring(1)));
				}
			/*
			 * IGNORE
			 * Adds/removes the player to the ignore.yml file.
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
					cs.sendMessage("§cPlease use the option in the config to turn off the messages in the console.");
					return true;
				}
				/*
				 * Loads the config files.
				 */
				final File file = new File(Main.getPlugin().getDataFolder(), "ignore.yml");
				final FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
				final List<String> ignorePlayers = cfg.getStringList("players");
				File bossbar = new File(Main.getPlugin().getDataFolder(), "bossbar.yml");
				final FileConfiguration cfg_boss = YamlConfiguration.loadConfiguration(bossbar);
				Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
					@Override
					public void run() {
						String check_uuid = null;
						/*
						 * Adds/removes the player from the list.
						 * Only applicable for "/sb ignore me".  
						 */
						if (args[1].equalsIgnoreCase("me")) {
							Player p = (Player) cs;
							check_uuid = mt.getUUID(p.getName());
							if (check_uuid == null) {
								cs.sendMessage("§cCouldn't check UUID for player \"" + cs.getName() + "\", please try again later or check spelling.");
								return;
							}
							if (!ignorePlayers.contains(check_uuid)) {
								ignorePlayers.add(check_uuid);
								if (cfg_boss.getBoolean("enabled") && Bukkit.getPluginManager().isPluginEnabled("BarAPI")) {
									BarAPI.removeBar(p);
								}
								cs.sendMessage("[Simple§cBroadcast]§r Now you don't receive any messages.");
							} else {
								ignorePlayers.remove(check_uuid);
								cs.sendMessage("[Simple§cBroadcast]§r Now you receive the messages again.");
							}
						/*
						 * Adds/removes the player declared in the command from the list.
						 * Only applicable for "/sb ignore PLAYER".  
						 */
						} else {
							check_uuid = mt.getUUID(args[1]);
							if (check_uuid == null) {
								cs.sendMessage("§cCouldn't check UUID for player \"" + args[1] + "\", please try again later or check spelling.");
								return;
							}
							if (!ignorePlayers.contains(check_uuid)) {
								ignorePlayers.add(check_uuid);
								if (cfg_boss.getBoolean("enabled") && Bukkit.getPluginManager().isPluginEnabled("BarAPI")) {
									BarAPI.removeBar(Bukkit.getServer().getPlayer(args[1]));
								}
								cs.sendMessage("[Simple§cBroadcast]§f The player §7" + args[1] + "§f now doesn't receive any messages.");
							} else {
								ignorePlayers.remove(check_uuid);
								cs.sendMessage("[Simple§cBroadcast]§f The player §7" + args[1] + "§f now receives the messages again.");
							}
						}
						/*
						 * Saves the config.
						 */
						cfg.set("players", ignorePlayers);
						try {
							cfg.save(file);
						} catch (IOException e) {
							plugin.logW("Couldn't save the ignore.yml. Error: ");
							plugin.logW(e.getMessage());
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
				plugin.getConfig().set("checkforupdates", !plugin.getConfig().getBoolean("checkforupdates"));
				plugin.saveConfig();
				plugin.reloadConfig();
				cs.sendMessage("[Simple§cBroadcast]§r The update check function is now " + (plugin.getConfig().getBoolean("checkforupdates") ? "enabled." : "disabled."));
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
					mt.helpList(1, cs);
				} else {
					try {
						if (Integer.parseInt(args[1]) > 0 && Integer.parseInt(args[1]) <= 2) {
							mt.helpList(Integer.parseInt(args[1]), cs);
						} else {
							cs.sendMessage("§cThere are only 2 pages available.");
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
				if (!cs.hasPermission("simplebroadcast.help")) {
					cs.sendMessage(err_need_Perm);
					return true;
				}
				cs.sendMessage("§cUnknown command. Type \"" + (cs instanceof Player ? "/" : "") + "simplebroadcast help\" for help.");
			}
		}
		return false;
	}
}