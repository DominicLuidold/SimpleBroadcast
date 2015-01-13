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

	public static int counter = 0;
	private Methods mt = new Methods();
	private boolean prefix_bool = Main.getPlugin().getConfig().getBoolean("prefix.enabled");
	private boolean suffix_bool = Main.getPlugin().getConfig().getBoolean("suffix.enabled");
	private String prefix = mt.addVariables(Main.getPlugin().getConfig().getString("prefix.prefix"));
	private String suffix = mt.addVariables(Main.getPlugin().getConfig().getString("suffix.suffix"));

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
		final String message = Main.globalMessages.get(counter);
		/*
		 * Loads the ignore.yml.
		 */
		File file = new File(Main.getPlugin().getDataFolder(), "ignore.yml");
		FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
		final List<String> ignoredPlayers = cfg.getStringList("players");
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
			Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), new Runnable() {
				@Override
				public void run() {
					for (Player p : Bukkit.getServer().getOnlinePlayers()) {
						if (!ignoredPlayers.contains(mt.getUUID(p.getName()))) {
							p.sendMessage(ChatColor.translateAlternateColorCodes('&', "§f" + (prefix_bool ? prefix + " " : "") + mt.addVariablesP(message, p) + (suffix_bool ? " " + suffix : "")));
						}
					}
				}
			});
			/*
			 * Checks if messages shall be broadcasted in the console.
			 */
			if (Main.getPlugin().getConfig().getBoolean("sendmessagestoconsole")) {
				ConsoleCommandSender console = Bukkit.getConsoleSender();
				console.sendMessage(ChatColor.translateAlternateColorCodes('&', "§f" + (prefix_bool ? prefix + " " : "") + mt.addVariables(message) + (suffix_bool ? " " + suffix : "")));
			}
			counter++;
		}
	}

	public static void setCounter(int counter) {
		MessageRunnable.counter = counter;
	}
}