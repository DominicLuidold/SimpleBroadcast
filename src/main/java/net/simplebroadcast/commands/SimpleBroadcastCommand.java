package net.simplebroadcast.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import net.simplebroadcast.Main;

public class SimpleBroadcastCommand implements CommandExecutor {
	
	private String noAccessToCommand = "§cYou do not have access to this command.";

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
		} else if (args.length > 0) {
			//TODO
		}
		return false;
	}
}