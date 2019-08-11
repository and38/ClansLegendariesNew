package me.libraryaddictfan.Legendaries;

import java.util.Arrays;

import me.libraryaddictfan.ClansLegendaries;
import me.libraryaddictfan.Utilities.UtilChat;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class ScytheOfTheFallenLord extends Legendary {

	
	public ScytheOfTheFallenLord(ClansLegendaries clanslegendariess, ItemStack item, String name, int damage) {
		super(clanslegendariess, item, name, 
				Arrays.asList(
						UtilChat.white("An old blade fashioned of nothing more"),
						UtilChat.white("than bones and cloth which served no"),
						UtilChat.white("purpose. Brave adventurers however have"),
						UtilChat.white("imbued it with the remnant powers of a"),
						UtilChat.white("dark and powerful foe."),
						"",
						UtilChat.yellow("Attack", true) + " to use " + UtilChat.green("Leach", false)
					)
				, damage);
	}
	
	
	@EventHandler
	public void onDmg(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Player) {
			Player p = (Player) e.getDamager();
			ItemStack item = p.getItemInHand();
			if (isCorrectItem(item)) {
				e.setDamage(getDamage());
				p.setHealth(Math.min(20.0, p.getHealth()+2));
			}
		}
	}
	
	@Override
	public void rel() {
		
		
	}

	@Override
	public void loop() {
		
		
	}


	@Override
	public void quit(Player player) {
		
	}


	@Override
	public void clearMem() {
		
	}

}
