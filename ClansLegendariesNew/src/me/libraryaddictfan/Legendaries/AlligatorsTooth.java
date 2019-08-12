package me.libraryaddictfan.Legendaries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

import me.libraryaddictfan.ClansLegendaries;
import me.libraryaddictfan.Utilities.UtilChat;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class AlligatorsTooth extends Legendary{

	public AlligatorsTooth(ClansLegendaries clanslegendariess, ItemStack item, String name, int damage) {
		super(clanslegendariess, item, name, 
				Arrays.asList(
						UtilChat.white("This deadly tooth was stolen from"),
						UtilChat.white("a best of reptillian beasts long"),
						UtilChat.white("ago. Legends say that the holder"),
						UtilChat.white("is granted the underwater agility"),
						UtilChat.white("of an Alligator"),
						"",
						UtilChat.yellow("Right-Click", true) + " to use " + UtilChat.green("Swim", false)
					)
				, damage);

	}

	@EventHandler
	public void onDmg(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Player) {
			Player p = (Player) e.getDamager();
			ItemStack item = p.getItemInHand();
			if (isCorrectItem(item)) {
				if (p.getLocation().getBlock().isLiquid()) {
					e.setDamage(12);
				} else {
					e.setDamage(7);
				}
			}
			
		}
	}

	@EventHandler
	public void onClick(PlayerInteractEvent e) {
		Player p = e.getPlayer();

		ItemStack item = p.getItemInHand();
		if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (isCorrectItem(item)) {
				
				if (p.getLocation().getBlock().getType() == Material.WATER ||
						p.getLocation().getBlock().getType() == Material.STATIONARY_WATER) {

					p.setVelocity(p.getLocation().getDirection().multiply(1.3));

					Block block = p.getLocation().getBlock();

					block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, 8);
					block.getWorld().playSound(block.getLocation(), Sound.valueOf("SPLASH"), 0.4f, 1f);
					
				}
			}
		}
	}

	@Override
	public void loop() {
		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			ItemStack item = p.getItemInHand();			
			if (isCorrectItem(item)) {
				p.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, Integer.MAX_VALUE, 255));
			} else {
				if (p.getActivePotionEffects().stream()
						.filter(e -> e.getType().getName().equals(PotionEffectType.WATER_BREATHING.getName()))
						.filter(e -> e.getAmplifier() == 255)
						.findFirst().isPresent()) {
					p.removePotionEffect(PotionEffectType.WATER_BREATHING);
				}
			}
		}
	}

	@Override
	public void rel() {
		
		
	}

	@Override
	public void quit(Player player) {
		
	}

	@Override
	public void clearMem() {
		
	}

}
