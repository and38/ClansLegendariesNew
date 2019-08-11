package me.libraryaddictfan.commands;

import java.util.ArrayList;
import java.util.HashMap;

import me.libraryaddictfan.ClansLegendaries;
import me.libraryaddictfan.Legendaries.Legendary;
import me.libraryaddictfan.Utilities.ConfigSections;
import me.libraryaddictfan.Utilities.ConfigUtils;
import me.libraryaddictfan.Utilities.UtilChat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LegendariesCommand implements CommandExecutor {

	private ClansLegendaries main;
	private static HashMap<String, String> arguments;

	public LegendariesCommand(ClansLegendaries mainn) {
		main = mainn;
		arguments = new HashMap<String, String>();
		arguments.put("reloadConfig", "reloadConfig");
		arguments.put("testConfig", "testConfig");
		arguments.put("setSection", "setSection <configSection> <value>");
		arguments.put("help", "help <argument>");
		arguments.put("fixHyperDelay", "fixHyperDelay");
	}

	public static HashMap<String, String> getArguments() {
		return arguments;
	}


	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {

		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (!p.hasPermission(ClansLegendaries.legendariesCommandUse)) {
				p.sendMessage(UtilChat.header() + "Insufficient permissions.");
				return true;
			}
		}

		boolean found = false;
		for (String s : arguments.keySet()) {
			if (args.length == 0) {
				sender.sendMessage(UtilChat.header() + "Please add at least one argument! Use <tab>"
						+ " to search for arguments!");
				return true;
			}
			if (!args[0].equalsIgnoreCase(s)) {
				continue;
			} else {
				found = true;
			}
		}

		if (found == false) {
			sender.sendMessage(UtilChat.header() + "This argument could not be found! Press <tab>"
					+ " in the second argument slot for completions!");
			
			return true;
		}

		if (args[0].equalsIgnoreCase("reloadConfig")) {
			reloadConfig(sender);
		} else if (args[0].equalsIgnoreCase("testConfig")) {
			testConfig(sender);
		} else if (args[0].equalsIgnoreCase("setSection")) {
			setSection(sender, args);
		} else if (args[0].equalsIgnoreCase("help")) {
			help(sender, args);
		} else if(args[0].equalsIgnoreCase("fixHyperDelay")) {
			fixHyperDelay(sender);
		}
		return true;
	}
	
	private boolean fixHyperDelay(CommandSender sender) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (!p.hasPermission(ClansLegendaries.legendariesCommandFixHyperDelay)) {
				p.sendMessage(UtilChat.header() + "Insufficient permissions.");
				return true;
			}
			
			for (Player pl : Bukkit.getServer().getOnlinePlayers()) {
				pl.setMaximumNoDamageTicks(20);
			}
			p.sendMessage(UtilChat.header() + "Reset all player's maximum damage delay to 20");
			return true;
		}
		return true;
	}

	private boolean help(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (!p.hasPermission(ClansLegendaries.legendariesCommandHelp)) {
				p.sendMessage(UtilChat.header() + "Insufficient permissions.");
				return true;
			}
		}

		if (args.length != 2) {
			sender.sendMessage(UtilChat.header() + "This command requires two arguments! Use"
					+ " <tab> in the second argument slot for completions!");
			return true;
		}

		for (String str : arguments.keySet()) {
			if (str.equalsIgnoreCase(args[1])) {
				sender.sendMessage(UtilChat.header() + "Help for this argument (" + str + "):");
				sender.sendMessage(ChatColor.BLUE + "/legendaries " + ChatColor.GRAY + arguments.get(str));
				return true;
			}
		}
		return true;

	}

	private boolean setSection(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (!p.hasPermission(ClansLegendaries.legendariesCommandSetSection)) {
				p.sendMessage(UtilChat.header() + "Insufficient permissions.");
				return true;
			}
		}

		if (args.length != 3) {
			sender.sendMessage(UtilChat.header() + "This command requires three arguments! Use"
					+ " <tab> in the second argument slot for completions!");
			return true;
		}
		ArrayList<String> argsNew = new ArrayList<String>();
		for (ConfigSections l : ConfigSections.values()) {
			argsNew.add(l.toString());
		}
		for (String str : argsNew) {
			if (args[1].equalsIgnoreCase(str)) {
				

				if (args[1].equalsIgnoreCase(ConfigSections.HYPERAXE_DAMAGE.toString()) ||
						args[1].equalsIgnoreCase(ConfigSections.HYPERAXE_DAMAGE_DELAY.toString())) {
					try {
						ConfigUtils.setSection(ConfigSections.valueOf(str.toUpperCase()), 
								Integer.parseInt(args[2]));
					} catch (NumberFormatException e) {
						sender.sendMessage(UtilChat.header() + 
								"Invalid value in argument slot 3! Use <tab> in the third"
								+ " argument slot for the value type.");
						return true;
					}
				} else if (args[1].equalsIgnoreCase(ConfigSections.SCEPTER_TARGET_ENT.toString()) ||
						args[1].equalsIgnoreCase(ConfigSections.WINDBLADE_INFINITE.toString())) {
					ConfigUtils.setBooleanSection(ConfigSections.valueOf(str.toUpperCase()), 
							Boolean.parseBoolean(args[2]));
					
				} else if (args[1].equalsIgnoreCase(ConfigSections.WINDBLADE_VELOCITY.toString())) {
					try {
						ConfigUtils.setDoubleSection(ConfigSections.valueOf(str.toUpperCase()), Double.parseDouble(args[2]));
					} catch (NumberFormatException e) {
						sender.sendMessage(UtilChat.header() + "Invalid value in argument slot 3! Use <tab> in the third"
								+ " argument slot for the value type.");
						return true;
					}
					
				}
				sender.sendMessage(UtilChat.header() + "Succesfully set section " + ChatColor.GREEN + str.toLowerCase()
						+ ChatColor.GRAY + " to " + ChatColor.BLUE + args[2]);
				sender.sendMessage(UtilChat.header() + "If the changes do not take effect, you may need to "
						+ ChatColor.GREEN + "reload your server" + ChatColor.GRAY + ".");
				main.saveConfig();
				for (Legendary leg : main.getLegendaries()) {
					leg.rel();
				}
				return true;
			}
			continue;
		}
		sender.sendMessage(UtilChat.header() + "Your second argument could not be found! Use <tab>"
				+ " in the second argument slot for completions.");
		return true;

	}

	private boolean testConfig(CommandSender sender) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (!p.hasPermission(ClansLegendaries.legendariesCommandTestConfig)) {
				p.sendMessage(UtilChat.header() + "Insufficient permissions.");
				return true;
			}
		}
		sender.sendMessage(UtilChat.header() + "Listing Configuration Sections and Values:");
		for (String loop : main.getConfig().getKeys(false)) {
			sender.sendMessage(ChatColor.BLUE + loop + ": " + ChatColor.GRAY + String.valueOf(main.getConfig().get(loop)));
		}
		return true;
	}


	private boolean reloadConfig(CommandSender sender) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (!p.hasPermission(ClansLegendaries.legendariesCommandReloadConfig)) {
				p.sendMessage(UtilChat.header() + "Insufficient permissions.");
				return true;
			}
		}
		main.reloadConfig();
		ConfigUtils.reload();
		for (Legendary leg : main.getLegendaries()) {
			leg.rel();
		}
		sender.sendMessage(UtilChat.header() + "The config has been reloaded!");
		return true;
	}

}
