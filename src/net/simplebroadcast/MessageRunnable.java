package net.simplebroadcast;

import net.simplebroadcast.Methods.Methods;
import net.simplebroadcast.MultiMap.MultiMapResource;

import java.io.File;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class MessageRunnable implements Runnable {

	private int counter = 0;
	private String message;
	private String permission;
	private Methods mt = new Methods();
	private boolean prefix_bool = Main.plugin.getConfig().getBoolean("prefix.enabled");
	private boolean suffix_bool = Main.plugin.getConfig().getBoolean("suffix.enabled");
	private String prefix = mt.addVariables(Main.plugin.getConfig().getString("prefix.prefix"));
	private String suffix = mt.addVariables(Main.plugin.getConfig().getString("suffix.suffix"));

	@Override
	public void run() {
		if (counter < Main.globalMessages.size()) {
			broadCast();
		} else {
			counter = 0;
			broadCast();
		}
	}
	
	@SuppressWarnings("deprecation")
	private void broadCast() {
		MultiMapResource<Integer, String, String> entry = Main.globalMessages.getResource(counter);
		permission = entry.getFirstValue();
		message = entry.getSecondValue();
		/*
		 * Loads the ignore.yml.
		 */
		File file = new File("plugins/SimpleBroadcast", "ignore.yml");
		FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
		List<String> ignoredPlayers = cfg.getStringList("players");
		/*
		 * Starts broadcasting the messages.
		 * If message starts with "/" it's handled as a command.
		 */
		if (message.startsWith("/")) {
			if (message.contains("%n")) {
				for (String msg : message.substring(1).split("%n/")) {
					mt.performCommand(msg);
				}
				counter++;
			} else {
				String command = message.substring(1);
				mt.performCommand(command);
				counter++;
			}
		} else {
			/*
			 * EXPERIMENTAL
			 * Checks if the user has to have the permission to receive the message.
			 * (Still in development - don't use this!)
			 */
			if (Main.plugin.getConfig().getBoolean("usepermissions")) {
				for (Player p : Bukkit.getOnlinePlayers()) {
					if (!ignoredPlayers.contains(mt.getUUID(p.getName()))) {
						if (p.hasPermission(permission) ||  permission.equalsIgnoreCase("default")) {					
							p.sendMessage(ChatColor.translateAlternateColorCodes('&', "§f" + (prefix_bool ? prefix + " " : "") + mt.addVariablesP(message, p) + (suffix_bool ? " " + suffix : "")));
						} else {
							//TODO
						}
					}
				}
			} else {
				for (Player p : Bukkit.getServer().getOnlinePlayers()) {
					if (!ignoredPlayers.contains(mt.getUUID(p.getName()))) {
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', "§f" + (prefix_bool ? prefix + " " : "") + mt.addVariablesP(message, p) + (suffix_bool ? " " + suffix : "")));
					}
				}
			}
			/*
			 * Checks if messages shall be broadcasted in the console.
			 */
			if (Main.plugin.getConfig().getBoolean("sendmessagestoconsole")) {
				ConsoleCommandSender console = Bukkit.getConsoleSender();
				console.sendMessage(ChatColor.translateAlternateColorCodes('&', "§f" + (prefix_bool ? prefix + " " : "") + mt.addVariables(message) + (suffix_bool ? " " + suffix : "")));
			}
			counter++;
		}
	}
}