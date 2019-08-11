package me.libraryaddictfan.Utilities;


import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class UtilGeneric {


	public static boolean onGround(Player p) {
		if (p.getLocation().clone().subtract(0, 1, 0).getBlock().getType() != Material.AIR
				&& p.getLocation().clone().subtract(0, 1, 0).getBlock()
				.getType() != Material.WATER
				&& p.getLocation().clone().subtract(0, 1, 0).getBlock()
				.getType() != Material.STATIONARY_WATER
				&& p.getLocation().clone().subtract(0, 1, 0).getBlock()
				.getType() != Material.LAVA
				&& p.getLocation().clone().subtract(0, 1, 0).getBlock()
				.getType() != Material.STATIONARY_LAVA
				&& p.getLocation().getBlock().isLiquid() != true) {
			
			return true;
		} else {
			return false;
		}
	}
	
	@SuppressWarnings("deprecation")
	public static ItemStack getItemInHand(Player p) {
		if (p == null) {
			throw new NullPointerException("Player was null");
		}

		try {
			return p.getInventory().getItemInHand();
		} catch (NullPointerException e) {
			return new ItemStack(Material.AIR);
		}
	}
}
