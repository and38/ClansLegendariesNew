package me.libraryaddictfan.Utilities;

import org.bukkit.ChatColor;

public class UtilChat {
	public static String header()
	{
		return ChatColor.BLUE + "Clans> " + ChatColor.GRAY;
	}

	public static String green(String str, boolean whiteEnd)
	{
		return ChatColor.GREEN + str + (whiteEnd ? ChatColor.WHITE : ChatColor.RESET);
	}

	public static String yellow(String str, boolean whiteEnd)
	{
		return ChatColor.YELLOW + str + (whiteEnd ? ChatColor.WHITE : ChatColor.RESET);
	}

	public static String gray(String str)
	{
		return ChatColor.GRAY + str;
	}

	public static String white(String str)
	{
		return ChatColor.WHITE + str;
	}
}
