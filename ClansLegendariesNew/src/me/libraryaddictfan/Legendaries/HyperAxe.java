package me.libraryaddictfan.Legendaries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import me.libraryaddictfan.ClansLegendaries;
import me.libraryaddictfan.Utilities.ConfigSections;
import me.libraryaddictfan.Utilities.ConfigUtils;
import me.libraryaddictfan.Utilities.Display;
import me.libraryaddictfan.Utilities.UtilChat;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;



public class HyperAxe extends Legendary{

	private HashMap<String, Long> cooldown;
	private HashMap<LivingEntity, Integer> toRemove;
	public static int DAMAGE_TICKS;

	public HyperAxe(ClansLegendaries clanslegendariess, ItemStack item, String name, int damage) {
		super(clanslegendariess, item, name, 
				Arrays.asList(
						UtilChat.white("Of all the weapons known to man,"),
						UtilChat.white("none is more prevalant than the"),
						UtilChat.white("Hyper Axe. Infused with rabbit's"),
						UtilChat.white("speed and pigman's ferocity, this"),
						UtilChat.white("blade can rip through any opponent."),
						"",
						UtilChat.white("Hit delay is reduced by " + UtilChat.yellow("50%", false)),
						UtilChat.white("Deals " + UtilChat.yellow("3 Damage", true) + " with attack"),
						UtilChat.yellow("Right-Click", true) + " to use " + UtilChat.green("Dash", false)
						)
						, damage);
		cooldown = new HashMap<String, Long>();
		toRemove = new HashMap<LivingEntity, Integer>();
	}



	public void rel() {
		DAMAGE_TICKS = ConfigUtils.getIntegerSection(ConfigSections.HYPERAXE_DAMAGE_DELAY);
		setDamage(ConfigUtils.getIntegerSection(ConfigSections.HYPERAXE_DAMAGE));
	}

	@EventHandler(priority=EventPriority.HIGH)
	public void onNormalDmg(EntityDamageEvent e) {
		if (e.isCancelled()) {
			return;
		}
		if (e.getCause() != DamageCause.ENTITY_ATTACK) {
			if (e.getEntity() instanceof LivingEntity) {
				LivingEntity ent = (LivingEntity) e.getEntity();
				if (toRemove.containsKey(ent)) {
					ent.setMaximumNoDamageTicks(20);
					toRemove.remove(ent);
				}
			}
		}
	}

	@EventHandler(priority=EventPriority.NORMAL)
	public void onDmg(EntityDamageByEntityEvent e) {
		if (e.isCancelled()) {
			return;
		}
		if (e.getDamager() instanceof Player) {
			Player p = (Player) e.getDamager();
			ItemStack item = p.getItemInHand();
			if (isCorrectItem(item)) {
				if (e.getEntity() instanceof LivingEntity) {
					if (e.getEntity() instanceof Player) {
						if (((Player) e.getEntity()).getGameMode() == GameMode.CREATIVE || 
								((Player) e.getEntity()).getGameMode() == GameMode.SPECTATOR) {
							return;
						}
					}
					LivingEntity entLiv = (LivingEntity) e.getEntity();
					entLiv.setMaximumNoDamageTicks(DAMAGE_TICKS);

					entLiv.setVelocity(new Vector(0, 0.12, 0));
					toRemove.put(entLiv, 7);
				}
				e.setDamage(getDamage());
			} else {
				if (e.getEntity() instanceof LivingEntity) {
					LivingEntity entLiv = (LivingEntity) e.getEntity();
					if (toRemove.containsKey(e.getEntity())) {
						entLiv.setMaximumNoDamageTicks(20);
						toRemove.remove(e.getEntity());
					}
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

				if (p.getLocation().getBlock().isLiquid()) {
					p.sendMessage(ChatColor.BLUE + getName() + ChatColor.BLUE
							+ "> " + ChatColor.GRAY + "You cannot use "
							+ ChatColor.GREEN + getName() + ChatColor.GRAY
							+ " in water.");
					return;
				}

				if (cooldown.containsKey(p.getName())) {

					Double x = ((16.0-(double) (Math.pow(10, -1)*((System.currentTimeMillis() - cooldown.get(p.getName()))/100))));

					String[] zz = x.toString().replace('.', '-').split("-");
					String concat = zz[0] + "." + zz[1].substring(0, 1);


					try {
						p.sendMessage(ChatColor.BLUE + getName() + "> " + ChatColor.GRAY +
								"Your cannot use " + ChatColor.GREEN + "Hyper Rush" + ChatColor.GRAY + 
								" for " + ChatColor.GREEN +
								concat + " Seconds");
					} catch(IndexOutOfBoundsException exc) {
						Bukkit.getServer().getLogger().warning("Index out of bounds in Hyper Axe msg. "
								+ "Should have been canceled");
					}


					return;
				}

				cooldown.put(p.getName(), System.currentTimeMillis());
				p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 2));
				p.sendMessage(ChatColor.BLUE + getName() + "> " + ChatColor.GRAY + "You used " + ChatColor.GREEN + 
						"Hyper Rush");
			}
		}



	}


	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		if (toRemove.containsKey(e.getPlayer())) {
			toRemove.remove(e.getPlayer());
			e.getPlayer().setMaximumNoDamageTicks(20);

		} else {
			e.getPlayer().setMaximumNoDamageTicks(20);
		}
	}

	@EventHandler
	public void onDeath(EntityDeathEvent e) {
		Entity ent = e.getEntity();
		if (toRemove.containsKey(ent)) {
			if (ent instanceof LivingEntity) {
				((LivingEntity) ent).setMaximumNoDamageTicks(20);
			}
			toRemove.remove(ent);
		}
	}

	@Override
	public void loop() {
		ArrayList<LivingEntity> remoeee = new ArrayList<LivingEntity>();
		for (LivingEntity ent : toRemove.keySet()) {
			if (ent.isDead()) {
				continue;
			}
			if (toRemove.get(ent) > 0) {
				if (toRemove.containsKey(ent)) {
					toRemove.put(ent, toRemove.get(ent)-1);
					continue;
				}

			} else {
				ent.setMaximumNoDamageTicks(20);
				if (toRemove.containsKey(ent)) {
					remoeee.add(ent);
					continue;
				}

			}

		}

		for (LivingEntity entt : remoeee) {
			toRemove.remove(entt);
		}


		for (String s : cooldown.keySet()) {

			Player p = Bukkit.getServer().getPlayer(s);
			if (p == null || p.isOnline() == false) {
				cooldown.remove(s);
				continue;
			}
			if (((System.currentTimeMillis()-cooldown.get(s))/1000) > 15) {
				cooldown.remove(s);
				p.sendMessage(ChatColor.BLUE + getName() + ChatColor.BLUE + "> " + ChatColor.GRAY + 
						"You can use " + ChatColor.GREEN + "Hyper Rush");
				if (isCorrectItem(p.getItemInHand())) {
					Display.display(ChatColor.GREEN + "§l" + "Hyper Rush" + " Recharged", p);
				}
				p.playSound(p.getLocation(), Sound.valueOf("NOTE_PLING"), 5, 1);

			} else {
				if (isCorrectItem(p.getItemInHand())) {
					Double x = ((16.0-(double) (Math.pow(10, -1)*((System.currentTimeMillis() - cooldown.get(p.getName()))/100))));
					double divide = (double)(System.currentTimeMillis() - cooldown.get(s))/(double)16000.0;
					String[] zz = x.toString().replace('.', '-').split("-");
					String concat = zz[0] + "." + zz[1].substring(0, 1);
					Display.displayProgress("§l" + "Hyper Rush", divide
							,ChatColor.WHITE + " " + concat + " Seconds", false, p);
				}

			}
		}
	}



	@Override
	public void quit(Player player) {
		
	}



	@Override
	public void clearMem() {
		toRemove.clear();
		cooldown.clear();		
	}

}
