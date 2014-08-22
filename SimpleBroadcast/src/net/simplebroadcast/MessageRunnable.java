package net.simplebroadcast;

import net.simplebroadcast.Methods.Methods;

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
	private Methods mt = new Methods();
	private List<String> messages = Main.plugin.getConfig().getStringList("messages");
	private boolean prefix_bool = Main.plugin.getConfig().getBoolean("prefix.enabled");
	private boolean suffix_bool = Main.plugin.getConfig().getBoolean("suffix.enabled");
	private String prefix = mt.addVariables(Main.plugin.getConfig().getString("prefix.prefix"));
	private String suffix = mt.addVariables(Main.plugin.getConfig().getString("suffix.suffix"));	
	
	@Override
	public void run() {
		if (counter < messages.size()) {
			broadCast();
		} else {
			counter = 0;
			broadCast();
		}
	}
	
	@SuppressWarnings("deprecation")
	private void broadCast() {
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
		if (messages.get(counter).startsWith("/")) {
			if (messages.get(counter).contains("%n")) {
				for (String msg : messages.get(counter).substring(1).split("%n/")) {
					mt.performCommand(msg);
				}
				counter++;
			} else {
				String message = messages.get(counter).substring(1);
				mt.performCommand(message);
				counter++;
			}
		} else {
			for (Player p : Bukkit.getServer().getOnlinePlayers()) {
				if (!ignoredPlayers.contains(mt.getUUID(p.getName()))) {
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', "§f" + (prefix_bool ? prefix + " " : "") + mt.addVariablesP(messages.get(counter), p) + (suffix_bool ? " " + suffix : "")));
				}
			}
			/*
			 * Checks if messages shall be broadcasted in the console.
			 */
			if (Main.plugin.getConfig().getBoolean("sendmessagestoconsole")) {
				ConsoleCommandSender console = Bukkit.getConsoleSender();
				console.sendMessage(ChatColor.translateAlternateColorCodes('&', "§f" + (prefix_bool ? prefix + " " : "") + mt.addVariables(messages.get(counter)) + (suffix_bool ? " " + suffix : "")));
			}
			counter++;
		}
	}
}