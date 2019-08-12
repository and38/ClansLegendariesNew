package me.libraryaddictfan.Legendaries;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import me.libraryaddictfan.ClansLegendaries;
import me.libraryaddictfan.Utilities.UtilChat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class Legendary implements Listener {


	private ItemStack item;
	private String name;
	private List<String> lore;
	private int damage;
	private ClansLegendaries clanslegendaries;
	
	public Legendary() {
		
	}
	
	public Legendary(ClansLegendaries clanslegendariess, ItemStack itemm, String namee, List<String> loree, int damagee) {
		Bukkit.getServer().getPluginManager().registerEvents(this, clanslegendariess);
		clanslegendaries = clanslegendariess;
		item = itemm;	
		name = namee;
		lore = loree;	
		damage = damagee;
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		if (e.getPlayer().getInventory().contains(getFullItem())) {
			quit(e.getPlayer());
		}
	}
	
	public abstract void quit(Player player);
	
	public ItemStack getItem() {
		return item;
	}
	
	public abstract void rel();
	
	public String specialHeader() {
		return ChatColor.BLUE + name + "> " + ChatColor.GRAY;
	}
	
	public boolean isCorrectItem(ItemStack item) {
		
		if (item == null) {
			return false;
		}

		
		String meta;
		
		try {
			meta = item.getItemMeta().getDisplayName();
		} catch (NullPointerException e) {
			return false;
		}

		if (meta == null) {
			return false;
		}

		if (item.getType() == getItem().getType() && meta.equals(ChatColor.GOLD + getName())) {
			return true;
		} else {
			return false;
		}
	}

	public String getName() {
		return name;
	}

	public List<String> getLore() {
		return lore != null ? lore : Arrays.asList("");
	}

	public void setLore(List<String> loree) {
		lore = loree;
	}

	public ItemStack getFullItem() {
		ItemStack item = new ItemStack(getItem().getType());
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.GOLD + getName());
		meta.setLore(getLore());
		List<String> list = meta.getLore();
		list.add(UtilChat.yellow("UUID: ", true) + UUID.randomUUID().toString());
		meta.setLore(list);
		item.setItemMeta(meta);
		return item;
	}
	
	
	public ClansLegendaries getMain() {
		return clanslegendaries;
	}
	
	public int getDamage() {
		return damage;
	}
	
	public void setDamage(int damagee) {
		damage = damagee;
	}
	
	@EventHandler
	public void onRightClickJukebox(PlayerInteractEvent e) {
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (e.getClickedBlock().getType() == Material.JUKEBOX && isCorrectItem(e.getPlayer().getItemInHand())) {
				e.setCancelled(true);
			}
		}
	}
	
	public abstract void loop();

	public abstract void clearMem();
	
	
}
