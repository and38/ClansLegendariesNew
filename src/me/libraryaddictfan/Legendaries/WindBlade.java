package me.libraryaddictfan.Legendaries;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;

import me.libraryaddictfan.ClansLegendaries;
import me.libraryaddictfan.Utilities.ConfigSections;
import me.libraryaddictfan.Utilities.ConfigUtils;
import me.libraryaddictfan.Utilities.Display;
import me.libraryaddictfan.Utilities.UtilChat;
import me.libraryaddictfan.Utilities.UtilGeneric;
import me.libraryaddictfan.Utilities.Utils;
import net.minecraft.server.v1_8_R3.EnumParticle;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class WindBlade extends Legendary {

	public static final double MAX_COUNTS = 5;
	public static double VEL_MULT;
	public static final double CHARGE_COST = 0.02;
	public static boolean isInfinite = false;

	private HashMap<String, Float> charges;
	private HashMap<String, Float> groundCounts;
	private HashMap<String, Long> cooldown;
	private HashMap<String, Float> smoother;
	private HashMap<String, Vector> veccs;

	public WindBlade(ClansLegendaries main, ItemStack itemm, String namee, int damage) {
		super(main, itemm, namee, 
				Arrays.asList(
						UtilChat.white("Long ago, a race of cloud dwellers"),
						UtilChat.white("terrorized the skies. A remnant of"),
						UtilChat.white("their tyranny, this airy blade is"),
						UtilChat.white("the last surviving memorium from"),
						UtilChat.white("their final battle againts the Titans."),
						"",
						UtilChat.yellow("Right-Click", true) + " to use " + UtilChat.green("Fly", false)
						)
						, damage);
		charges = new HashMap<String, Float>();
		groundCounts = new HashMap<String, Float>();
		cooldown = new HashMap<String, Long>();
		smoother = new HashMap<String, Float>();
		veccs = new HashMap<String, Vector>();
		VEL_MULT = ConfigUtils.getSection(ConfigSections.WINDBLADE_VELOCITY);
		isInfinite = ConfigUtils.getBooleanSection(ConfigSections.WINDBLADE_INFINITE);
	}

	public void rel() {
		isInfinite = ConfigUtils.getBooleanSection(ConfigSections.WINDBLADE_INFINITE);
		VEL_MULT = ConfigUtils.getDoubleSection(ConfigSections.WINDBLADE_VELOCITY);
	}

	@EventHandler
	public void onDmg(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Player) {
			Player p = (Player) e.getDamager();
			ItemStack item = UtilGeneric.getItemInHand(p);
			if (isCorrectItem(item)) {
				e.setDamage(getDamage());
			}

		}
	}

	@EventHandler
	public void dmg(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();

			if (isCorrectItem(UtilGeneric.getItemInHand(p))) {

				if (e.getCause() == DamageCause.FALL) {
					e.setCancelled(true);
				}

			}
		}
	}


	public void onUpdate(Player p) {

		if (!charges.containsKey(p.getName()))
			charges.put(p.getName(), 0f);

		if (UtilGeneric.onGround(p)) {
			Charge(p);
		}


		if (!veccs.containsKey(p.getName())) {
			veccs.put(p.getName(), null);
		}

		if (!smoother.containsKey(p.getName())) {
			smoother.put(p.getName(), 0F);
		} else {
			if (smoother.get(p.getName()) != null && smoother.get(p.getName()) > 0) {
				if (veccs.get(p.getName()) != null) {
					if (!isInfinite) {
						charges.put(p.getName(),
								(float) Math.max(0F, (GetCharge(p) - CHARGE_COST)));
					}
					p.setVelocity(veccs.get(p.getName()));
					p.getWorld().playSound(p.getLocation(), Sound.FIZZ, 1.2F, 1.5F);
				}

			} else {
				if (p.getGameMode() == GameMode.CREATIVE || p.getGameMode() == GameMode.SPECTATOR) {
					p.setAllowFlight(true);		
				} else {
					p.setAllowFlight(false);
				}

			}

		}


		if (!(groundCounts.containsKey(p.getName()))) {
			groundCounts.put(p.getName(), 0F);
		}

		groundCounts.put(p.getName(), Math.max(0, groundCounts.get(p.getName())-0.15F));
		smoother.put(p.getName(), (float) Math.max(0, smoother.get(p.getName())-.5));

		if (GetCharge(p) == 0) {
			Display.displayProgress(null, 0f, null, false, p);
		} else {
			Display.displayProgress(null, GetCharge(p), null, false, p);
		}
	}

	public boolean Charge(Player player) {
		if (!charges.containsKey(player.getName()))
			charges.put(player.getName(), 0f);

		float charge = charges.get(player.getName());

		charge = (float) Math.min(1f, charge + 0.01);
		charges.put(player.getName(), charge);

		Display.displayProgress(null, charge, null, false, player);

		return charge >= 1;
	}

	public float GetCharge(Player player) {
		if (!charges.containsKey(player.getName())) {
			charges.put(player.getName(), 0f);
		}


		return charges.get(player.getName());
	}


	@EventHandler
	public void onClick(PlayerInteractEvent e) {
		Player p = e.getPlayer();

		ItemStack item = UtilGeneric.getItemInHand(p);

		if (e.getAction() == Action.RIGHT_CLICK_AIR
				|| e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (isCorrectItem(item)) {
				if (p.getLocation().getBlock().isLiquid()) {
					p.sendMessage(ChatColor.BLUE + getName() + ChatColor.BLUE
							+ "> " + ChatColor.GRAY + "You cannot use "
							+ ChatColor.GREEN + "Wind Rider" + ChatColor.GRAY
							+ " in water.");
					return;
				}

				if (GetCharge(p) <= 0) {
					return;
				}

				if (cooldown.containsKey(p.getName())) {
					double x = 3.0 - ((System.currentTimeMillis() - cooldown.get(p.getName())) / 1000d);
					DecimalFormat format = new DecimalFormat("0.0");

						p.sendMessage(ChatColor.BLUE + getName() + "> " + ChatColor.GRAY +
								"Your flight powers will recharge in " + ChatColor.GREEN +
								format.format(Math.abs(x)) + " Seconds");
					return;
				}

				windLaunch(p);
			}
		}
	}

	@EventHandler
	public void onToggleFlight(PlayerToggleFlightEvent e) {
		if (e.getPlayer().getGameMode() == GameMode.SURVIVAL || e.getPlayer().getGameMode() == GameMode.ADVENTURE) {
			if (isCorrectItem(UtilGeneric.getItemInHand(e.getPlayer()))) {
				if (smoother.get(e.getPlayer().getName()) == null) {
					return;
				} else if (smoother.get(e.getPlayer().getName()) == 0) {
					return;
				} else {
					e.setCancelled(true);
				}
			}
		}
	}

	private void windLaunch(Player p) {
		Vector vec = p.getLocation().getDirection();

		if (Double.isNaN(vec.getX()) || Double.isNaN(vec.getY())
				|| Double.isNaN(vec.getZ()) || vec.length() == 0) {
			return;
		}

		vec.normalize();
		vec.multiply(VEL_MULT);

		if (UtilGeneric.onGround(p)) {
			if (!(groundCounts.containsKey(p.getName()))) {
				groundCounts.put(p.getName(), 1F);
			} else {
				groundCounts.put(p.getName(), groundCounts.get(p.getName())+1);
				if (UtilGeneric.onGround(p)) {
					p.playSound(p.getLocation(), Sound.valueOf("LAVA_POP"), 13, 2);
				}
			}

		}	

		p.setVelocity(vec);
		for (Player pl : Bukkit.getServer().getOnlinePlayers()) {
			if (pl.getLocation().distance(p.getLocation()) < 64) {
				doParticles(pl, (float)p.getLocation().getX(),
						(float)p.getLocation().getY()+1,
						(float)p.getLocation().getZ());
			}
		}

		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(getMain(), () -> {
			if (UtilGeneric.onGround(p)) {
				p.playSound(p.getLocation(), Sound.valueOf("LAVA_POP"), 13, 2);
			}
			for (Player pl : Bukkit.getServer().getOnlinePlayers()) {
				if (pl.getLocation().distance(p.getLocation()) < 64) {
					doParticles(pl, (float)p.getLocation().getX(),
							(float)p.getLocation().getY()+1,
							(float)p.getLocation().getZ());

				}
			}
			p.setVelocity(vec);
		}, 2);

		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(getMain(), () -> {
			if (UtilGeneric.onGround(p)) {
				p.playSound(p.getLocation(), Sound.valueOf("LAVA_POP"), 13, 2);
			}

			for (Player pl : Bukkit.getServer().getOnlinePlayers()) {
				if (pl.getLocation().distance(p.getLocation()) < 64) {
					doParticles(pl, (float)p.getLocation().getX(),
							(float)p.getLocation().getY()+1,
							(float)p.getLocation().getZ());

				}
			}
			p.setVelocity(vec);
		}, 3);



		if (groundCounts.get(p.getName()) > MAX_COUNTS) {
			groundCounts.put(p.getName(), 0F);
			p.sendMessage(ChatColor.BLUE + getName() + ChatColor.BLUE + "> " + ChatColor.GRAY + 
					"Flight powers diminished whilst scraping the ground. Recharging in " + ChatColor.GREEN + 
					"3.0 Seconds");
			
			p.playSound(p.getLocation(), Sound.valueOf("ANVIL_USE"), 3, 1);
			cooldown.put(p.getName(), System.currentTimeMillis());
			return;
		}


		veccs.put(p.getName(), vec);

		if (!smoother.containsKey(p.getName())) {
			smoother.put(p.getName(), 0F);
		}

		smoother.put(p.getName(), Math.min(5, smoother.get(p.getName())+1));
		if (p.getGameMode() == GameMode.SURVIVAL || p.getGameMode() == GameMode.ADVENTURE) {
			p.setAllowFlight(true);
		}
	}




	@Override
	public void loop() {
		for (Player p : Bukkit.getServer().getOnlinePlayers()) {	
			doCoolDowns();

			ItemStack item = UtilGeneric.getItemInHand(p);


			if (isCorrectItem(item)) {
				onUpdate(p);
			} else {
				memoryRemove(p, false);

			}

		} 
	}


	private void memoryRemove(Player p, boolean logged) {

		if (logged == true) {
			if (cooldown.containsKey(p.getName())) {
				cooldown.remove(p.getName());			
			}
			if (charges.containsKey(p.getName())) {
				charges.remove(p.getName());
			}

		}
		if (veccs.containsKey(p.getName())) {
			veccs.remove(p.getName());
		}
		if (smoother.containsKey(p.getName())) {
			if (p.getGameMode() == GameMode.SPECTATOR || p.getGameMode() == GameMode.CREATIVE) {
				p.setAllowFlight(true);
				smoother.remove(p.getName());
			} else {
				p.setAllowFlight(false);
			}

			smoother.remove(p.getName());
		}
		if (groundCounts.containsKey(p.getName())) {
			if (logged == true) {
				if (groundCounts.containsKey(p.getName())) {
					groundCounts.remove(p.getName());
				}
				return;
			}
			if (!p.getInventory().contains(getFullItem())) {
				if (groundCounts.containsKey(p.getName())) {
					groundCounts.remove(p.getName());
				}
			}
		}
	}

	@EventHandler
	public void onDie(PlayerDeathEvent e) {
		Player p = e.getEntity();
		memoryRemove(p, true);
	}

	private void doCoolDowns() {
		
		for (String s : cooldown.keySet()) {
			if (System.currentTimeMillis() - cooldown.get(s) >= 3000) {

				Player p = Bukkit.getServer().getPlayer(s);
				cooldown.remove(s);
				Player pl = Bukkit.getServer().getPlayer(s);
				pl.sendMessage(ChatColor.BLUE + getName() + ChatColor.BLUE + "> " +
						ChatColor.GRAY + "Your flight powers have replenished!");
				p.playSound(p.getLocation(), Sound.valueOf("NOTE_PLING"), 5, 1);
			} 
		}
	}


	public void doParticles(Player pl, float x, float y, float z) {
		Utils.sendParticles(pl,
				EnumParticle.EXPLOSION_NORMAL,
				false,
				x,
				y,
				z,
				0.012F,
				0.2F,
				0.012F,
				0.1F,
				4,
				new int[0]);
	}

	@Override
	public void quit(Player player) {
		memoryRemove(player, true);
	}

	@Override
	public void clearMem() {
		charges.clear();
		groundCounts.clear();
		cooldown.clear();
		smoother.clear();
		veccs.clear();
	}


}
