package me.libraryaddictfan.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import me.libraryaddictfan.ClansLegendaries;
import me.libraryaddictfan.Utilities.ConfigSections;
import me.libraryaddictfan.Utilities.UtilChat;

import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class LegendariesCommandTabComplete implements TabCompleter{

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd,
			String commandLabel, String[] args) {
		if (cmd.getName().equalsIgnoreCase("legendaries")) {

			if (sender instanceof Player) {
				Player p = (Player) sender;
				if (!p.hasPermission(ClansLegendaries.legendariesCommandUse)) {
					return null;
				}
			}

			if (args.length == 1) {
				return oneArg(sender, args);
			} else if (args.length == 2) {
				return twoArgs(sender, args);
			} else if (args.length == 3) {
				return threeArgs(sender, args);
			}
		} 
		return null;
	}


	private List<String> oneArg(CommandSender sender, String[] args) {
		Set<String> arguments = LegendariesCommand.getArguments().keySet();
		ArrayList<String> argTypes = new ArrayList<String>();

		if (!args[0].equals("")) {
			for (String nName : arguments) {
				if (nName.toLowerCase().startsWith(args[0].toLowerCase())) {
					argTypes.add(nName);
				}
			}

		} else {
			for (String nName : arguments) {
				argTypes.add(nName);
			}
		}

		Collections.sort(argTypes);

		return argTypes;
	}

	private List<String> twoArgs(CommandSender sender, String[] args) {
		if (args[0].equalsIgnoreCase("help")) {
			Set<String> arguments = LegendariesCommand.getArguments().keySet();
			ArrayList<String> argTypes = new ArrayList<String>();

			if (!args[1].equals("")) {
				for (String nName : arguments) {
					if (nName.toLowerCase().startsWith(args[1].toLowerCase())) {
						argTypes.add(nName);
					}
				}

			} else {
				for (String nName : arguments) {
					argTypes.add(nName);
				}
			}

			Collections.sort(argTypes);

			return argTypes;
		} else if (args[0].equalsIgnoreCase("setSection")) {
			ConfigSections[] argumentss = ConfigSections.values();
			ArrayList<String> arguments = new ArrayList<String>();
			for (ConfigSections sec : argumentss) {
				arguments.add(sec.toString().toLowerCase());
			}
			ArrayList<String> argTypes = new ArrayList<String>();

			if (!args[1].equals("")) {
				for (String nName : arguments) {
					if (nName.toLowerCase().startsWith(args[1].toLowerCase())) {
						argTypes.add(nName);
					}
				}

			} else {
				for (String nName : arguments) {
					argTypes.add(nName);
				}
			}

			Collections.sort(argTypes);

			return argTypes;
		}
		return null;
	}

	@SuppressWarnings("unused")
	private List<String> threeArgs(CommandSender sender, String[] args) {
		if (args[0].equalsIgnoreCase("setSection")) {
			ConfigSections[] argumentss = ConfigSections.values();
			ArrayList<String> arguments = new ArrayList<String>();
			for (ConfigSections sec : argumentss) {
				arguments.add(sec.getDefaultValue().toString());
			}
			ArrayList<String> argTypes = new ArrayList<String>();

			if (!args[2].equals("")) {
				for (String nName : arguments) {
					try {
						if (ConfigSections.valueOf(args[1].toUpperCase()) != null) {
							String combine;
							combine = "<insert_" + 
									ConfigSections.valueOf(args[1].toUpperCase()).getDefaultValue().getClass().
									getSimpleName() + "_here>";
							argTypes.add(combine);
						} else {
							sender.sendMessage(UtilChat.header() + "Your Config Section (argument 2)"
									+ " could not be found.");
							playWarnSound(sender);
							return null;
						}
					} catch (IllegalArgumentException e) {
						sender.sendMessage(UtilChat.header() + "Your Config Section (argument 2)"
								+ " could not be found.");
						playWarnSound(sender);
						return null;
					}
				}

			} else {
				for (String nName : arguments) {
					try {
						if (ConfigSections.valueOf(args[1].toUpperCase()) != null) {
							String combine;
							combine = "<insert_" + 
									ConfigSections.valueOf(args[1].toUpperCase()).getDefaultValue().getClass().
									getSimpleName() + "_here>";
							argTypes.add(combine);

						} else {
							sender.sendMessage(UtilChat.header() + "Your Config Section (argument 2)"
									+ " could not be found.");
							playWarnSound(sender);
							return null;
						}
					} catch (IllegalArgumentException e) {
						sender.sendMessage(UtilChat.header() + "Your Config Section (argument 2)"
								+ " could not be found.");
						playWarnSound(sender);
						return null;
					}

				}
			}

			Collections.sort(argTypes);

			return argTypes;
		}
		return null;
	}
	
	private void playWarnSound(CommandSender sender) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			p.playSound(p.getLocation(), Sound.valueOf("NOTE_PLING"), 10, 1);
		}
	}

}
