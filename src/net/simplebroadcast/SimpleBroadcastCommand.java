package net.simplebroadcast;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import me.confuser.barapi.BarAPI;
import net.simplebroadcast.Methods.BossBarMethods;
import net.simplebroadcast.Methods.Methods;
import net.simplebroadcast.Methods.UpdatingMethods;
import net.simplebroadcast.Utils.UUIDFetcher;

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
	File config = new File("plugins/SimpleBroadcast", "config.yml");
	private String err_need_Perm = "§cYou do not have access to that command.";
	
	
	public SimpleBroadcastCommand(Main plugin) {
		this.plugin = plugin;
	}
	
	boolean prefix_bool = Main.plugin.getConfig().getBoolean("prefix.enabled");
	boolean suffix_bool = Main.plugin.getConfig().getBoolean("suffix.enabled");
	String prefix = mt.addVariables(Main.plugin.getConfig().getString("prefix.prefix"));
	String suffix = mt.addVariables(Main.plugin.getConfig().getString("suffix.suffix"));
	
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
			cs.sendMessage("§6Author:§f KingDome24 and yonascode");
			cs.sendMessage("§6Version:§f " + plugin.getDescription().getVersion());
			cs.sendMessage("§6Website:§f " + plugin.getDescription().getWebsite());
			if (plugin.getConfig().getBoolean("checkforupdates")) {
				Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
					@Override
					public void run() {
						try {
							if (um.updateB()) {
								cs.sendMessage("§6Upate:§f An update is available: " + um.updateN());
							} else {
								cs.sendMessage("§6Upate:§f There are no updates available.");
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
			File bossbar = new File("plugins/SimpleBroadcast", "bossbar.yml");
			FileConfiguration cfg_boss = YamlConfiguration.loadConfiguration(bossbar);
			if (Bukkit.getPluginManager().isPluginEnabled("BarAPI") && cfg_boss.getBoolean("enabled") && BossBarMethods.bar_running == 1) {
				cs.sendMessage("§6Boss bar broadcast:§f The boss bar integration is enabled and broadcasts.");
			} else if (Bukkit.getPluginManager().isPluginEnabled("BarAPI") && cfg_boss.getBoolean("enabled") && BossBarMethods.bar_running == 0) {
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
				if (Main.running == 0) {
					mt.broadcast();
					cs.sendMessage("[Simple§cBroadcast]§r Started broadcast.");
					Main.running = 1;
				} else if (Main.running == 3) {
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
				if (Main.running == 1) {
					Bukkit.getServer().getScheduler().cancelTask(Main.messageTask);
					cs.sendMessage("[Simple§cBroadcast]§r Cancelled broadcast.");
					Main.running = 0;
				} else if (Main.running == 3) {
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
				Bukkit.getServer().getScheduler().cancelTask(Main.messageTask);
				Bukkit.getServer().getScheduler().cancelTask(BossBarMethods.barTask);
				
				BossBarMethods.counter = 0;				
				plugin.reloadConfig();
				BossBarMethods.bar_running = 1;
				bmt.barBroadcast();
				
				if (!plugin.getConfig().getBoolean("requiresonlineplayers")) {
					mt.broadcast();
					cs.sendMessage("[Simple§cBroadcast]§r Reloaded the config(s) successfully.");
					if (!(Main.running == 3)) 
						Main.running = 1;
				} else {
					cs.sendMessage("[Simple§cBroadcast]§r Reloaded the config(s) successfully.");
					if (Bukkit.getOnlinePlayers().length >= 1) {
						mt.broadcast();
						if (!(Main.running == 3)) 
							Main.running = 1;
					} else {
						if (!(Main.running == 3)) 
							Main.running = 0;
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
					if (BossBarMethods.bar_running == 0) {
						BossBarMethods.bar_running = 1;
						bmt.barBroadcast();
						cs.sendMessage("[Simple§cBroadcast]§r Started (boss bar) broadcast.");
					} else if (BossBarMethods.bar_running == 3) {
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
					if (BossBarMethods.bar_running == 1) {
						Bukkit.getScheduler().cancelTask(BossBarMethods.barTask);
						BossBarMethods.bar_running = 0;
						for (Player p : Bukkit.getServer().getOnlinePlayers()) {
							BarAPI.removeBar(p);
						}
						cs.sendMessage("[Simple§cBroadcast]§r Cancelled boss bar broadcast.");
					} else if (BossBarMethods.bar_running == 3) {
						cs.sendMessage("[Simple§cBroadcast]§r Boss bar broadcast is already disabled (as set in the bossbar.yml)!");
					} else {
						cs.sendMessage("[Simple§cBroadcast]§r Boss bar broadcast is already cancelled!");
					}
				} else {
					if (!cs.hasPermission("simplebroadcast.bossbar.start") && !cs.hasPermission("simplebroadcast.bossbar.stop")) {
						cs.sendMessage(err_need_Perm);
						return true;
					}
					if (cs instanceof Player) {
						cs.sendMessage("§cPlease use either \"/sb bossbar start\" or \"/sb bossbar stop\".");
					} else {
						cs.sendMessage("§cPlease use either \"sb bossbar start\" or \"sb bossbar stop\".");
					}
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
				FileConfiguration main_cfg = YamlConfiguration.loadConfiguration(config);				
				cs.sendMessage("§e--------- §fMessages: SimpleBroadcast §e-------------");
				for (String msg : main_cfg.getStringList("messages")) {
					if (cs instanceof Player) {
						Player p = (Player) cs;
						cs.sendMessage(ChatColor.translateAlternateColorCodes('&', "§6" + message_number + ".§f" + (prefix_bool ? " " + prefix : "") + " " + mt.addVariablesP(msg, p) + (suffix_bool ? " " + suffix : "")));
					} else {
						cs.sendMessage(ChatColor.translateAlternateColorCodes('&', "§6" + message_number + ".§f" + (prefix_bool ? " " + prefix : "") + " " + mt.addVariables(msg) + (suffix_bool ? " " + suffix : "")));
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
				try {
					if (Integer.parseInt(args[1])-1 > -1 && Integer.parseInt(args[1])-1 < plugin.getConfig().getStringList("messages").size()) {
						for (Player p : Bukkit.getOnlinePlayers()) {
							p.sendMessage(ChatColor.translateAlternateColorCodes('&', (prefix_bool ? prefix + " " : "") + mt.addVariablesP(plugin.getConfig().getStringList("messages").get(Integer.parseInt(args[1])-1), p) + (suffix_bool ? " " + suffix : "")));
						}
						if (plugin.getConfig().getBoolean("sendmessagestoconsole")) {
							ConsoleCommandSender console = Bukkit.getConsoleSender();
							console.sendMessage(ChatColor.translateAlternateColorCodes('&', (prefix_bool ? prefix + " " : "") + mt.addVariables(plugin.getConfig().getStringList("messages").get(Integer.parseInt(args[1])-1)) + (suffix_bool ? " " + suffix : "")));
						}
					} else {
						cs.sendMessage("§cThere are only " + plugin.getConfig().getStringList("messages").size() + " messages available which you can broadcast.");
					}
				} catch (NumberFormatException nfe) {
					cs.sendMessage("§cPlease enter a valid number.");
				}
			/*
			 * ADD
			 * Adds a message to the (chat) config.
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
				FileConfiguration main_cfg = YamlConfiguration.loadConfiguration(config);
				List<String> addMessage= plugin.getConfig().getStringList("messages");
				StringBuilder message = new StringBuilder();
				for (int i = 1; i < args.length; i++) {
					message.append(" ").append(ChatColor.translateAlternateColorCodes('&', args[i]));
				}
				addMessage.add(message.substring(1).toString());
				main_cfg.set("messages", addMessage);
				try {
					main_cfg.save(config);
					cs.sendMessage("§2Successfully added message.");
				} catch (IOException e) {
					plugin.logW("Couldn't add message: ");
					plugin.logW(e.getMessage());
				}
			/*
			 * REMOVE
			 * Removes a message.
			 */
			} else if (args[0].equalsIgnoreCase("remove")) {
				if (!cs.hasPermission("simplebroadcast.remove")) {
					cs.sendMessage(err_need_Perm);
					return true;
				}
				plugin.reloadConfig();
				FileConfiguration main_cfg = YamlConfiguration.loadConfiguration(config);
				List<String> removeMessage= plugin.getConfig().getStringList("messages");
				try {
					if (Integer.parseInt(args[1])-1 > -1 && Integer.parseInt(args[1])-1 < plugin.getConfig().getStringList("messages").size()) {
						removeMessage.remove(Integer.parseInt(args[1])-1);
					} else {
						cs.sendMessage("§cPlease choose a number between 1 and " + plugin.getConfig().getStringList("messages").size() + ".");
					}
				} catch (NumberFormatException nfe) {
					cs.sendMessage("§cPlease enter a valid number.");
				}
				main_cfg.set("messages", removeMessage);
				try {
					main_cfg.save(config);
					cs.sendMessage("§2Successfully removed message.");
				} catch (IOException e) {
					plugin.logW("Couldn't remove message: ");
					plugin.logW(e.getMessage());
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
					if (cs instanceof Player) {
						cs.sendMessage("§cPlease use \"/simplebroadcast raw\" instead.");
					} else {
						cs.sendMessage("§cPlease use \"simplebroadcast raw\" instead.");
					}
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
			 * Adds the player to the ignore.yml file.
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
				/*
				 * Loads the ignore.yml file.
				 */
				final File file = new File("plugins/SimpleBroadcast", "ignore.yml");
				final FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
				final List<String> ignorePlayers = cfg.getStringList("players");
				File bossbar = new File("plugins/SimpleBroadcast", "bossbar.yml");
				final FileConfiguration cfg_boss = YamlConfiguration.loadConfiguration(bossbar);
				/*
				 * Checks if the server runs in "online-mode=false".
				 */
				if (!Bukkit.getServer().getOnlineMode()) {
					/*
					 * Checks if the player already doesn't receive any messages.
					 */
					if (!ignorePlayers.contains(mt.getUUID(args[1]))) {
						/* 
						 *(ADDEN)
						 * Checks if the player entered "me" or a player name.
						 */
						if (args[1].equalsIgnoreCase("me")) {
							if (cs instanceof Player) {
								Player p = (Player) cs;							
								ignorePlayers.add(mt.getUUID(p.getName()));
								if (cfg_boss.getBoolean("enabled") && Bukkit.getPluginManager().isPluginEnabled("BarAPI")) {
									BarAPI.removeBar(p);
								}
								cs.sendMessage("[Simple§cBroadcast]§r Now you don't receive any messages.");
							} else {
								cs.sendMessage("§cPlease use the option in the config to turn off the messages in the console.");
							}
						} else {
							ignorePlayers.add(mt.getUUID(args[1]));
							if (cfg_boss.getBoolean("enabled") && Bukkit.getPluginManager().isPluginEnabled("BarAPI")) {
								BarAPI.removeBar(Bukkit.getServer().getPlayer(args[1]));
							}
							cs.sendMessage("[Simple§cBroadcast]§r The player §7" + args[1] + "§f now doesn't receive any messages.");
						}
					} else {
						if (args[1].equalsIgnoreCase("me")) {
							if (cs instanceof Player) {
								Player p = (Player) cs;
								ignorePlayers.remove(mt.getUUID(p.getName()));
								cs.sendMessage("[Simple§cBroadcast]§r Now you receive the messages again.");
							} else {
								cs.sendMessage("§cPlease use the option in the config to turn off the messages in the console.");
							}
						} else {
							ignorePlayers.remove(mt.getUUID(args[1]));
							cs.sendMessage("[Simple§cBroadcast]§r The player §7" + args[1] + "§f now receives the messages again.");
						}
					}
					cfg.set("players", ignorePlayers);
					try {
						cfg.save(file);
					} catch (IOException e) {
						plugin.logW("Couldn't save the ignore.yml. Error: ");
						plugin.logW(e.getMessage());
					}
					return true;
				}
				/*
				 * The following is only applicable for "online-mode=true"!
				 */
				Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
					@Override
					public void run() {
						/*
						 * ADDEN
						 */
						String check_uuid;
						UUIDFetcher fetcher = new UUIDFetcher(Arrays.asList(args[1]));
						Map<String, UUID> response = null;
						try {
							response = fetcher.call();
						} catch (Exception e) {
						}
						/*
						 * Checks if entered player exists.
						 */
						try {
							if (response.isEmpty() && !args[1].equalsIgnoreCase("me")) 
								cs.sendMessage("§cThe player, who you have entered, doesn't exist!");
							/* 
							 *(ADDEN)
							 * Checks if the player entered "me".
							 */
							if (response.isEmpty() && args[1].equalsIgnoreCase("me")) {
								if (cs instanceof Player) {
									Player p = (Player) cs;
									response.put(p.getName(), p.getUniqueId());
								} else {
									cs.sendMessage("§cPlease use the option in the config to turn off the messages in the console.");
								}
							}						
							for (Map.Entry<String, UUID> entry : response.entrySet()) {
								UUID uuid = entry.getValue();										
							    check_uuid = uuid.toString();
							    /*
							     * Checks if ignore.yml already contains the uuid of the player.
							     */
								if (!ignorePlayers.contains(check_uuid) && !ignorePlayers.contains(mt.getUUID(cs.getName()))) {
									if (args[1].equalsIgnoreCase("me")) {
										Player p = (Player) cs;
										ignorePlayers.add(mt.getUUID(p.getName()));
										if (cfg_boss.getBoolean("enabled") && Bukkit.getPluginManager().isPluginEnabled("BarAPI")) {
											BarAPI.removeBar(p);
										}
										cs.sendMessage("[Simple§cBroadcast]§r Now you don't receive any messages.");
									} else {
										String add_uuid = uuid.toString();						        
										ignorePlayers.add(add_uuid);
										if (cfg_boss.getBoolean("enabled") && Bukkit.getPluginManager().isPluginEnabled("BarAPI")) {
											BarAPI.removeBar(Bukkit.getServer().getPlayer(args[1]));
										}
										cs.sendMessage("[Simple§cBroadcast]§r The player §7" + args[1] + "§f now doesn't receive any messages.");
									}
								/*
								 * Removes the UUID of the player.
								 */
								} else {
									if (args[1].equalsIgnoreCase("me")) {
										if (cs instanceof Player) {
											Player p = (Player) cs;
											ignorePlayers.remove(mt.getUUID(p.getName()));
											cs.sendMessage("[Simple§cBroadcast]§r Now you receive the messages again.");
										} else {
											cs.sendMessage("§cPlease use the option in the config to turn off the messages in the console.");
										}
									} else {
										String rem_uuid = uuid.toString();						        
										ignorePlayers.remove(rem_uuid);
										cs.sendMessage("[Simple§cBroadcast]§r The player §7" + args[1] + "§f now receives the messages again.");
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
						} catch (NullPointerException npe) {
							if (args[1].equalsIgnoreCase("me")) {
								plugin.logW("Couldn't check UUID for player \"" + cs.getName() + "\".");
								cs.sendMessage("§cCouldn't check UUID for player \"" + cs.getName() + "\", please try again later or check spelling.");
							} else {
								plugin.logW("Couldn't check UUID for player \"" + args[1] + "\".");
								cs.sendMessage("§cCouldn't check UUID for player \"" + args[1] + "\", please try again later or check spelling.");
							}
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
				FileConfiguration main_cfg = YamlConfiguration.loadConfiguration(config);
				if (plugin.getConfig().getBoolean("checkforupdates")) {
					cs.sendMessage("[Simple§cBroadcast]§r The update check function is now disabled.");
					main_cfg.set("checkforupdates", false);
					try {
						cs.sendMessage("[Simple§cBroadcast]§r The update check function is now enabled.");
						main_cfg.save(config);
					} catch (IOException e) {
						plugin.logW("Couldn't change update status ");
						plugin.logW(e.getMessage());
					}
				} else {
					main_cfg.set("checkforupdates", false);
					try {
						cs.sendMessage("[Simple§cBroadcast]§r The update check function is now enabled.");
						main_cfg.save(config);
					} catch (IOException e) {
						plugin.logW("Couldn't change update status ");
						plugin.logW(e.getMessage());
					}
				}
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
				if (cs instanceof Player) {
					cs.sendMessage("§cUnknown command. Type \"/simplebroadcast help\" for help.");
				} else {
					cs.sendMessage("§cUnknown command. Type \"simplebroadcast help\" for help.");
				}
			}
		}
		return false;
	}
}