package net.simplebroadcast.methods;

import java.io.File;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.simplebroadcast.Main;
import net.simplebroadcast.MessageRunnable;
import net.simplebroadcast.utils.UUIDFetcher;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class Methods {
	
	private String uuid;
	private int previousMessage;

	/**
	 * Executes a command.
	 * @param command which shall be executed.
	 */
	public void performCommand(String command) {
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
	}
	
	/**
	 * Counts the amount of operators who are online.
	 * @return amount of online operators.
	 */
	@SuppressWarnings("deprecation")
	public int opList() {
		int ops = 0;
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (p.isOp()) {
				ops++;
			}
		}
		return ops;
	}
	
	/**
	 * Gets a random player name.
	 * @return name of a random player.
	 */
	@SuppressWarnings("deprecation")
	public String randomPlayer() {
		if (Bukkit.getOnlinePlayers().length > 0) {
			int random = (int) (Math.random() * Bukkit.getOnlinePlayers().length);
			String randomPlayer = Bukkit.getServer().getOnlinePlayers()[random].getName();
			return randomPlayer;
		} else {
			String noPlayer = "UNKNOWN";
			return noPlayer;
		}
	}
	
	/**
	 * Shows an overview of all available commands.
	 * @param i page site.
	 * @param cs CommandSender
	 */
	public void helpList(int i, CommandSender cs) {
		cs.sendMessage("§e--------- §fHelp: SimpleBroadcast (" + i + "/3) §e---------");
		switch (i) {
			case 1: {
				cs.sendMessage("§6/simplebroadcast:§f Shows you information about the plugin.");
				cs.sendMessage("§6/simplebroadcast start:§f Starts the broadcast.");
				cs.sendMessage("§6/simplebroadcast stop:§f Stops the broadcast.");
				cs.sendMessage("§6/simplebroadcast reload:§f Reloads the config.");
				cs.sendMessage("§6/simplebroadcast bossbar help:§f Shows you the bossbar help pages.");
				cs.sendMessage("§6/simplebroadcast list:§f Shows you all messages.");
				break;
			} case 2: {
				cs.sendMessage("§6/simplebroadcast now:§f Broadcasts already existing msg.");
				cs.sendMessage("§6/simplebroadcast next:§f Skips the next message.");
				cs.sendMessage("§6/simplebroadcast add:§f Adds a msg to the config.");
				cs.sendMessage("§6/simplebroadcast remove:§f Removes a msg from the config.");
				cs.sendMessage("§6/simplebroadcast broadcast:§f Broadcasts the msg you enter.");
				cs.sendMessage("§6/simplebroadcast raw:§f Broadcasts the msg without formatting.");
				break;
			} case 3: {
				cs.sendMessage("§6/simplebroadcast ignore:§f Adds/removes the player from the ignore list.");
				cs.sendMessage("§6/simplebroadcast update:§f Toggles the update check function.");
				cs.sendMessage("§6/simplebroadcast help:§f Shows you the help pages.");
				cs.sendMessage("§6Instead of \"" + (cs instanceof Player ? "/" : "") + "simplebroadcast\" you can use \"/sb <command>\".");
				break;
			}
		}
	}
	
	/**
	 * Shows an overview of all available boss bar commands.
	 * @param i page site.
	 * @param cs CommandSender
	 */
	public void bossBarHelpList(int i, CommandSender cs) {
		cs.sendMessage("§e--------- §fHelp: SimpleBroadcast (" + i + "/3) §e---------");
		switch (i) {
			case 1: {
				cs.sendMessage("§6Instead of \"" + (cs instanceof Player ? "/" : "") + "simplebroadcast\" you can use \"/sb <command>\".");
				break;
			}
		}
	}
	
	/**
	 * Gets the UUID of the player.
	 * If server runs in "online-mode=false" it returns an an MD5 hash of the player name.
	 * @param player name.
	 * @return UUID of the player.
	 */
	public String getUUID(final String player) {
		if (!Bukkit.getServer().getOnlineMode()) {
			try {
				MessageDigest md = MessageDigest.getInstance("MD5");
				byte[] messageDigest = md.digest(player.getBytes());
				BigInteger number = new BigInteger(1, messageDigest);
				String hashedName = number.toString(16);
				while (hashedName.length() < 32) {
					hashedName = "0" + hashedName;
				}
				return hashedName;
			} catch (NoSuchAlgorithmException e) {}
		}
		UUIDFetcher fetcher = new UUIDFetcher(Arrays.asList(player));
		Map<String, UUID> response = null;
		try {
			response = fetcher.call();
		} catch (Exception e) {
		}
		if (response == null || response.isEmpty()) {
			return null;
		}
		for (UUID entry : response.values()) {
			uuid = entry.toString();
		}
		return uuid;
	}
	
	/**
	 * Replaces the variables in the messages.
	 * @param message where the variables shall be replaced.
	 * @return message with the replaced variables.
	 */
	public String addVariables(String message) {
		message = message.replace("%sq%", "'").
				replace("%n", "\n").replace("%online%", Bukkit.getServer().getOnlinePlayers().length + "").replace("%max_online%", Bukkit.getServer().getMaxPlayers() + "").
				replace("%unique%", Bukkit.getServer().getOfflinePlayers().length + "").replace("%year%", Calendar.getInstance().get(Calendar.YEAR) + "").
				replace("%month%", Calendar.getInstance().get(Calendar.MONTH) + 1 + "").replace("%day%", Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + "").
				replace("%hour_of_day%", Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + "").replace("%hour%", Calendar.getInstance().get(Calendar.HOUR) + "").
				replace("%minute%", Calendar.getInstance().get(Calendar.MINUTE) + "").replace("%second%", Calendar.getInstance().get(Calendar.SECOND) + "").
				replace("%motd%", Bukkit.getServer().getMotd()).replace("%player%", "UNKNOWN").replace("%biome%", "UNKNOWN").
				replace("%world%", "UNKNOWN").replace("%online_ops%", opList() + "").replace("%randomplayer%", randomPlayer()).
				replace("%raquo%", "\u00BB").replace("%laquo%", "\u00AB").replace("%bullet%", "\u2219");
		return message;
	}

	/**
	 * Replaces the variables in the messages.
	 * @param message where the variables shall be replaced.
	 * @param p player object (for getting the current world/biome/..)
	 * @return message with the replaced variables.
	 */
	public String addVariablesP(String message, Player p) {
		message = addVariables(message.replace("%player%", p.getName()).replace("%biome%", p.getLocation().getBlock().getBiome().toString()).replace("%world%", p.getWorld().getName()));
		return message;
	}
	
	/*
	 * The global broadcast function.
	 */
	public void broadcast() {
		if (MessageRunnable.getRunning() == 3) {
			return;
		}
		/*
		 * Decides if the messages shall be broadcasted in a random order or not.
		 */
		if (!Main.getPlugin().getConfig().getBoolean("randomizemessages")) {
			MessageRunnable.setMessageTask(Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(), new MessageRunnable(), 0L, Main.getPlugin().getConfig().getInt("delay") * 20L));
			return;
		}
		MessageRunnable.setMessageTask(Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(), new Runnable() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				/*
				 * Loads the ignore.yml file.
				 */
				File file = new File(Main.getPlugin().getDataFolder(), "ignore.yml");
				FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
				List<String> ignoredPlayers = cfg.getStringList("players");
				/*
				 * Gets and broadcasts the messages in a random order.
				 */
				int msg = (int) (Math.random() * Main.chatMessages.size());
				if (msg != previousMessage) {
					previousMessage = msg;
				} else {
					msg += (previousMessage < Main.chatMessages.size() - 1) ? 1 : ((previousMessage > 1) ? -1 : 0);
					previousMessage = msg;
				}
				String message = Main.chatMessages.get(msg);
				msg = 0;
				String prefix = ChatColor.translateAlternateColorCodes('&', addVariables(Main.getPlugin().getConfig().getString("prefix.prefix")));
				String suffix = ChatColor.translateAlternateColorCodes('&', addVariables(Main.getPlugin().getConfig().getString("suffix.suffix")));
				boolean prefix_bool = Main.getPlugin().getConfig().getBoolean("prefix.enabled");
				boolean suffix_bool = Main.getPlugin().getConfig().getBoolean("suffix.enabled");
				/*
				 * Starts broadcasting the messages.
				 * If message starts with "/" it's handled as a command.
				 */
				if (message.startsWith("/")) {
					if (message.contains("%n/")) {
						for (String c_msg : message.substring(1).split("%n/")) {
							performCommand(c_msg);
						}
					} else {
						message = message.substring(1);
						performCommand(message);
					}
					return;
				}
				for (Player p : Bukkit.getServer().getOnlinePlayers()) {
					if (!ignoredPlayers.contains(getUUID(p.getName()))) {
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', "§f" + (prefix_bool ? prefix + " " : "") + addVariablesP(message, p) + (suffix_bool ? " " + suffix : "")));
					}
				}
				/*
				 * Checks if messages shall be broadcasted in the console.
				 */
				if (Main.getPlugin().getConfig().getBoolean("sendmessagestoconsole")) {
					ConsoleCommandSender console = Bukkit.getConsoleSender();
					console.sendMessage(ChatColor.translateAlternateColorCodes('&', "§f" + (prefix_bool ? prefix + " " : "") + addVariables(message) + (suffix_bool ? " " + suffix : "")));
				}
			}
		}, 0L, Main.getPlugin().getConfig().getInt("delay") * 20L));
	}
}