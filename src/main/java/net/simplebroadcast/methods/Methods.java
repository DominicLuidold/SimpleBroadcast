package net.simplebroadcast.methods;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Map;
import java.util.UUID;

import net.simplebroadcast.utils.UUIDFetcher;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Methods {

	private String uuid;

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
	public String randomPlayer() {
		if (Bukkit.getOnlinePlayers().size() > 0) {
			int random = (int) (Math.random() * Bukkit.getOnlinePlayers().size());
			Player randomPlayer = (Player) Bukkit.getServer().getOnlinePlayers().toArray()[random];
			return randomPlayer.getName();
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
				cs.sendMessage("§6/simplebroadcast reload:§f Reloads the configs.");
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
		cs.sendMessage("§e--------- §fHelp: SimpleBroadcast (" + i + "/1) §e---------");
		switch (i) {
			case 1: {
				cs.sendMessage("§6/sb bossbar start:§f Starts the boss bar broadcast.");
				cs.sendMessage("§6/sb bossbar stop:§f Stops the boss bar broadcast.");
				cs.sendMessage("§6/sb bossbar list:§f Shows you all boss bar messages.");
				cs.sendMessage("§6/sb bossbar next:§f Skips the next message.");
				cs.sendMessage("§6/sb bossbar help:§f Shows you the boss bar help pages.");
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
				replace("%n", "\n").replace("%online%", Bukkit.getServer().getOnlinePlayers().size() + "").replace("%max_online%", Bukkit.getServer().getMaxPlayers() + "").
				replace("%unique%", Bukkit.getServer().getOfflinePlayers().length + "").replace("%year%", Calendar.getInstance().get(Calendar.YEAR) + "").
				replace("%month%", Calendar.getInstance().get(Calendar.MONTH) + 1 + "").replace("%day%", Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + "").
				replace("%hour_of_day%", Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + "").replace("%hour%", Calendar.getInstance().get(Calendar.HOUR) + "").
				replace("%minute%", Calendar.getInstance().get(Calendar.MINUTE) + "").replace("%second%", Calendar.getInstance().get(Calendar.SECOND) + "").
				replace("%motd%", Bukkit.getServer().getMotd()).replace("%player%", "UNKNOWN").replace("%biome%", "UNKNOWN").
				replace("%world%", "UNKNOWN").replace("%online_ops%", opList() + "").replace("%random_player%", randomPlayer()).
				replace("%raquo%", "\u00BB").replace("%laquo%", "\u00AB").replace("%bullet%", "\u2219");
		return message;
	}

	/**
	 * Replaces the variables in the messages.
	 * @param message where the variables shall be replaced.
	 * @param p player object (for getting the current world/biome/..)
	 * @return message with the replaced variables.
	 */
	public String addPlayerVariables(String message, Player p) {
		message = addVariables(message.replace("%player%", p.getName()).replace("%biome%", p.getLocation().getBlock().getBiome().toString()).replace("%world%", p.getWorld().getName()));
		return message;
	}
}