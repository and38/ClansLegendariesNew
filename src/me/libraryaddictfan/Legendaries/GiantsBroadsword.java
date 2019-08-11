package me.libraryaddictfan.Legendaries;

import java.util.ArrayList;
import java.util.Arrays;

import me.libraryaddictfan.ClansLegendaries;
import me.libraryaddictfan.Utilities.UtilChat;
import me.libraryaddictfan.Utilities.Utils;
import net.minecraft.server.v1_8_R3.EnumParticle;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class GiantsBroadsword extends Legendary {
	private ArrayList<String> hitStop;
	
	public GiantsBroadsword(ClansLegendaries main, ItemStack itemm, String namee, int damage) {
		super(main, itemm, namee, 
				Arrays.asList(
						UtilChat.white("Forged in the godly mined of Plagieus"),
						UtilChat.white("this sword has endured thousands of"),
						UtilChat.white("wars. It is sure to grant certain"),
						UtilChat.white("victory in battle."),
						"",
						UtilChat.white("Deals " + UtilChat.yellow("10 Damage", true) + " with attack"),
						UtilChat.yellow("Right-Click", true) + " to use " + UtilChat.green("Shield", false)
					)
				, damage);
		hitStop = new ArrayList<String>();
	}

	

	@EventHandler
	public void onDmg(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Player) {
			Player p = (Player) e.getDamager();
			ItemStack item = p.getItemInHand();
			
			if (hitStop.contains(p.getName())) {
				e.setCancelled(true);
			}
			
			
			if (isCorrectItem(item)) {
				e.setDamage(getDamage());
			}
			
		}
	}

	@EventHandler
	public void onClick(PlayerInteractEvent e) {
		Player p = e.getPlayer();


		ItemStack item = p.getItemInHand();

		if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (isCorrectItem(item)) {
				hitStop.add(p.getName());
				p.removePotionEffect(PotionEffectType.REGENERATION);
				p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 15, 3));
				
				p.removePotionEffect(PotionEffectType.SLOW);
				p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 15, 255));
				
				new BukkitRunnable() {
					int times = 0;
					public void run() {
						if (p != null && p.isOnline() == true && times < 3) {
							p.playSound(p.getLocation(), Sound.valueOf("LAVA_POP"), 13, 2);
							times++;
						} else {
							this.cancel();
						}
						
					}
				}.runTaskTimer(getMain(), 0, 1);
				
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(getMain(), () -> {
					if (p == null || !p.isOnline()) {
						return;
					}
					
					if (p.getActivePotionEffects().stream()
							.filter(pot -> pot.getType() == PotionEffectType.SLOW)
							.filter(pot -> pot.getAmplifier() == 255).findFirst().isPresent()) {
						return;
					}
					
					hitStop.remove(p.getName());
				}, 5);

				Location block = p.getLocation().clone().add(0,2,0);

				double x = (double) block.getX();
				double y = (double) block.getY();
				double z = (double) block.getZ();

				for (Player pla : Bukkit.getServer().getOnlinePlayers()) {
					if (pla.getLocation().distance(new Location(pla.getWorld(), (double)x, (double)y, (double)z)) > 64) {
						return;
					}
					
						Utils.sendParticles(pla,
								EnumParticle.HEART,
								false,
								(float)x,
								(float)y,
								(float)z,
								0.1F,
								0.04F,
								0.1F,
								0.05F,
								2,
								new int[0]);
				}
			}
		}

	}


	
	@Override
	public void loop() {
		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			ItemStack item = p.getItemInHand();
			if (p.isDead()) {
				return;
			}
			if (isCorrectItem(item)) {
				Location block = p.getLocation().clone().add(0,1,0);

				double x = (double) block.getX();
				double y = (double) block.getY();
				double z = (double) block.getZ();

				for (Player pla : Bukkit.getServer().getOnlinePlayers()) {

						Utils.sendParticles(pla, 
								EnumParticle.CRIT, 
								false, 
								(float)x, 
								(float)y, 
								(float)z, 
								0.2F, 
								0.2F, 
								0.2F, 
								0.005F, 
								1, 
								new int[0]);	
				}
			}
		}
	}


	@Override
	public void rel() {
		
	}



	@Override
	public void quit(Player player) {
		hitStop.remove(player.getName());	
	}



	@Override
	public void clearMem() {
		hitStop.clear();
	}




}
