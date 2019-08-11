package me.libraryaddictfan.Utilities;

import java.lang.reflect.Constructor;

import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle.EnumTitleAction;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Display {

	private static final int BARS = 24;

	public static void display(String text, Player player) {
		sendJsonMessage(player, text, ChatAction.ACTION_BAR);
	}

	public static void sendJsonMessage(Player player, String text, ChatAction chatAction) {
		IChatBaseComponent chat = ChatSerializer.a("{\"text\":\"" + " " + text + " " + "\"}");
		PacketPlayOutChat packet = new PacketPlayOutChat(chat, chatAction.getValue());
		Utils.sendPacket(player, packet);
	}
	
	public static void displaySubTitle(Player player, String text, int fadeIn, int stay, int fadeOut) {
		IChatBaseComponent subtitle = ChatSerializer.a("{\"text\":\"" + " " + text + " " + "\"}");

		PacketPlayOutTitle packetSubTitle = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, subtitle, fadeIn, stay, fadeOut);

		Utils.sendPacket(player, packetSubTitle);
	}
	
	public static void displayTitle(Player player, String text, int fadeIn, int stay, int fadeOut) {
		IChatBaseComponent subtitle = ChatSerializer.a("{\"text\":\"" + " " + text + " " + "\"}");

		PacketPlayOutTitle packetSubTitle = new PacketPlayOutTitle(EnumTitleAction.TITLE, subtitle, fadeIn, stay, fadeOut);

		Utils.sendPacket(player, packetSubTitle);
	}
	
	public static void displayTitleAndSubtitle(Player player, 
			String titleText, String subTitleText, int fadeIn, int stay, int fadeOut) {
		displayTitle(player, titleText, fadeIn, stay, fadeOut);
		displaySubTitle(player, subTitleText, fadeIn, stay, fadeOut);
	}

	public static void customError(Exception e, boolean printStack) {
		if (printStack) {
			Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "------------------------------");
		}
		Bukkit.getServer().getLogger().info(ChatColor.RED + "ClansLegendariesRelease> " + e.getClass().toString() + "\n");
		e.printStackTrace();
		if (printStack) {
			Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "------------------------------");
		}
	}

	public static void displayProgress(String prefix, double percent, String suffix, boolean charge, 
			Player... players) {


		if (charge) {
			percent = 1 - percent;
		}

		String progressBar = ChatColor.GREEN + "";
		boolean colorChange = false;
		for (int i=0 ; i<BARS ; i++)
		{
			if (!colorChange && (float)i/(float)BARS >= percent)
			{
				progressBar += ChatColor.RED;
				colorChange = true;
			}

			progressBar += "▌";
		}

		for (Player player : players) {
			display((prefix == null ? "" : prefix + ChatColor.RESET + " ") + progressBar + 
					(suffix == null ? "" : suffix + ChatColor.RESET + " "), player);
		}
	 
	}
}
