package net.simplebroadcast.commands;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class BroadcastTabCompleter implements TabCompleter {

	/*
	 * (non-Javadoc)
	 * @see org.bukkit.command.TabCompleter#onTabComplete(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
	 */
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> list = new ArrayList<String>();
		/* List of all available broadcast types. */
		if (args.length == 1) {
			list.add("chat");
		}
		/* List of all available commands. */
		if (args.length > 1) {
			/* List of all available chat broadcast commands. */
			if (args[0].equalsIgnoreCase("chat") && args.length < 3) {
				list.add("ignore");
				list.add("list");
				list.add("next");
				list.add("now");
				list.add("start");
				list.add("stop");
			}
			/* Adds player names to list if user executes ignore command. */
			if (args[1].equalsIgnoreCase("ignore")) {
				for (Player player : Bukkit.getOnlinePlayers()) {
					list.add(player.getName());
				}
			}
		}
		/* Removes incongruous tab completions. */
		Iterator<String> iterator = list.iterator();
		while (iterator.hasNext()) {
			String command = iterator.next().toLowerCase();
			if (!command.startsWith(args[args.length - 1].toLowerCase())) {
				iterator.remove();
			}
		}
		return list;
	}
}