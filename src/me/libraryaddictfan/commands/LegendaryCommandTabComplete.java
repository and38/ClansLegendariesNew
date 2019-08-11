package me.libraryaddictfan.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.libraryaddictfan.ClansLegendaries;
import me.libraryaddictfan.Legendaries.Legendary;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class LegendaryCommandTabComplete implements TabCompleter {
	
	private ClansLegendaries main;
	
	public LegendaryCommandTabComplete(ClansLegendaries mainn) {
		main = mainn;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (cmd.getName().equalsIgnoreCase("legendary")) {
				if (args.length == 1) {
					if (sender instanceof Player) {
						Player p = (Player) sender;
						if (!p.hasPermission(ClansLegendaries.legendaryCommandUse)) {
							return null;
						}
					}
					
					ArrayList<String> leggieTypes = new ArrayList<String>();

					if (!args[0].equals("")) {
						for (Legendary type : main.getLegendaries()) {

							String nName = type.getName().replace(" ", "");

							if (nName.toLowerCase().startsWith(args[0].toLowerCase())) {
								leggieTypes.add(nName);
							}
						}



					}

					else {
						for (Legendary type : main.getLegendaries()) {
							String nName = type.getName().replace(" ", "");
							leggieTypes.add(nName);
						}
					}

					Collections.sort(leggieTypes);
					if ("all".startsWith(args[0].toLowerCase()) || args[0].equals("")) {
						leggieTypes.add(leggieTypes.size(), "all");
					}
					return leggieTypes;
				}
		}
		return null;
	}
}
