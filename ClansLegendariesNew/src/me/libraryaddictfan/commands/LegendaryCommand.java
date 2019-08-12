package me.libraryaddictfan.commands;

import me.libraryaddictfan.ClansLegendaries;
import me.libraryaddictfan.Legendaries.Legendary;
import me.libraryaddictfan.Utilities.UtilChat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class LegendaryCommand implements CommandExecutor {

	private ClansLegendaries main;

	public LegendaryCommand(ClansLegendaries mainn) {
		main = mainn;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("legendary")) {
			if (sender instanceof Player) {
				Player p = (Player) sender;


				if (!p.hasPermission(ClansLegendaries.legendaryCommandUse)) {
					p.sendMessage(UtilChat.header() + "Insufficient Permissions.");
					return true;
				}

				if (args.length != 1) {
					if (args.length == 2) {

						twoArgs(p, args);
						return true;
					}

					p.sendMessage(UtilChat.header() + "Too many or too few arguments! Use <tab>"
							+ " to search for arguments!");
					return true;
				}
				boolean found = false;
				
				for (Legendary l : main.getLegendaries()) {

					String s = l.getName().replace(" ", "");

					if (args[0].equalsIgnoreCase(s)) {
						ItemStack it = l.getFullItem();
						p.getInventory().addItem(it);
						
						p.sendMessage(UtilChat.header() + "You have recieved a(n) " + 
								ChatColor.GREEN + l.getName());
						found = true;
					} else if (args[0].equalsIgnoreCase("all")) {
						for (Legendary ll : main.getLegendaries()) {
							ItemStack it = ll.getFullItem();
							p.getInventory().addItem(it);

						}
						p.sendMessage(UtilChat.header() + "You have recieved every legendary!");
						return true;
					} else {
						continue;
					}

				}
				if (found == false) {
					p.sendMessage(UtilChat.header() + 
							"This argument could not be found! Use <tab>"
							+ " to search for arguments!");
					return true;
				}

			} else {
				sender.sendMessage(ChatColor.RED + "This command is only for players!");
			}
			return true;

		}



		return true;
	}

	private boolean twoArgs(Player p, String[] args) {

		if (!p.hasPermission(ClansLegendaries.legendaryCommandGive)) {
			p.sendMessage(UtilChat.header() + "Insufficient Permissions.");
			return true;
		}

		Player target = null;
		for (Player pl : Bukkit.getServer().getOnlinePlayers()) {
			if (pl.getName().equalsIgnoreCase(args[1])) {
				target = pl;
			}
		}

		if (target == null) {
			p.sendMessage(UtilChat.header() + "The player, "+ ChatColor.GREEN + args[1] + ChatColor.GRAY + 
					", could not be found!");
			return true;
		}

		boolean found = false;
		for (Legendary l : main.getLegendaries()) {
			String s = l.getName().replace(" ", "");

			if (args[0].equalsIgnoreCase(s)) {
				ItemStack it = l.getFullItem();
				target.getInventory().addItem(it);
				p.sendMessage(UtilChat.header() + "You have given a(n) " + ChatColor.GREEN + l.getName() + ChatColor.GRAY + 
						" to " + ChatColor.GREEN + target.getName());
				target.sendMessage(UtilChat.header() + "You have recieved a(n) " + 
						ChatColor.GREEN + l.getName());
				found = true;
			} else if (args[0].equalsIgnoreCase("all")) {
				for (Legendary ll : main.getLegendaries()) {
					ItemStack it = ll.getFullItem();
					
					target.getInventory().addItem(it);



				}
				p.sendMessage(UtilChat.header() + "You have given every legendary to " + ChatColor.GREEN + target.getName());
				target.sendMessage(UtilChat.header() + "You have recieved every legendary!");
				return true;
			} else {
				continue;
			}

		}
		if (found == false) {
			p.sendMessage(UtilChat.header() + 
					"This argument could not be found! Use <tab>"
					+ " to search for arguments!");
			return true;
		}
		return true;
	}


}
