package me.libraryaddictfan.Legendaries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import me.libraryaddictfan.ClansLegendaries;
import me.libraryaddictfan.Utilities.Display;
import me.libraryaddictfan.Utilities.UtilGeneric;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;


public class MagneticMaul extends Legendary {

	private HashMap<String, Float> charges;
	private HashMap<String, Float> smoother;
	public MagneticMaul(ClansLegendaries mainn, ItemStack itemStack, String name,
			int damagee) {
		super(mainn, itemStack, name, 
				Arrays.asList(
						""
						)
						, damagee);
		charges = new HashMap<String, Float>();
		smoother = new HashMap<String, Float>();
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onEntityPunchEntity(EntityDamageByEntityEvent e) {
		if (e.isCancelled()) {
			return;
		}

		if (e.getEntity().getLastDamageCause() != null && e.getEntity().getLastDamageCause().getCause() != null) {
			if (e.getEntity().getLastDamageCause().getCause() == DamageCause.SUICIDE) {
				e.getEntity().setLastDamageCause(new EntityDamageEvent(e.getDamager(), DamageCause.ENTITY_ATTACK, getDamage()));
				return;
			}
		}
		
		if (e.getDamager() instanceof Player) {
			Player p = (Player) e.getDamager();
			ItemStack item = UtilGeneric.getItemInHand(p);
			if (isCorrectItem(item)) {

				if (e.getEntity() instanceof LivingEntity) {
					if (e.getEntity() instanceof Player) {
						if (((Player)e.getEntity()).getGameMode() == GameMode.CREATIVE 
								|| ((Player)e.getEntity()).getGameMode() == GameMode.SPECTATOR) {
							return;
						}
					}
					e.setCancelled(true);
					LivingEntity l = (LivingEntity) e.getEntity();
					l.setLastDamageCause(new EntityDamageEvent(p, DamageCause.SUICIDE, getDamage()));
					l.damage(getDamage(), p);

					Vector vec = p.getLocation().toVector()
							.subtract(e.getEntity().getLocation().toVector()).normalize().add(new Vector(0,0.4,0)).multiply(0.4D);
					e.getEntity().setVelocity(vec);
				}

			}

		}
	}

	public void onUpdate(Player p) {
		if (!charges.containsKey(p.getName()) && isCorrectItem(UtilGeneric.getItemInHand(p)))
			charges.put(p.getName(), 0f);

		if (!smoother.containsKey(p.getName()) && isCorrectItem(UtilGeneric.getItemInHand(p))) {
			smoother.put(p.getName(), 0F);
		}

		if (smoother.get(p.getName()) == 0 || charges.get(p.getName()) <= 0.13) {
			Charge(p);
		} else if (smoother.get(p.getName()) != 0) {
			charges.put(p.getName(),
					(float) Math.max(0F, (getCharge(p) - 0.017)));
		}

		/*if (!veccs.containsKey(p.getName())) {
			veccs.put(p.getName(), null);
		}*/



		if (isCorrectItem(UtilGeneric.getItemInHand(p))) {
			smoother.put(p.getName(), (float) Math.max(0, smoother.get(p.getName())-.5));
			Display.displayProgress(null, getCharge(p), null, false, p);
		}
	}

	

	@Override
	public void loop() {
		for (Player p : Bukkit.getServer().getOnlinePlayers()) {	
			ItemStack item = UtilGeneric.getItemInHand(p);

			if (!p.getInventory().contains(getFullItem())) {
				charges.remove(p.getName());
			}
			try {
				if (smoother.get(p.getName()) > 0.6F) {
					for (Entity e : p.getNearbyEntities(7, 7, 7)) {
						if (e == p) {
							continue;
						}
						if (e.isDead()) {
							continue;
						}
						if (!(e instanceof LivingEntity)) {
							continue;
						}
						if (e instanceof Player) {
							Player pl = (Player) e;
							if (!p.canSee(pl)) {
								continue;
							}
						}
						
						if (getLookingAt(p, e)) {
							Vector vec = p.getLocation().toVector().subtract(e.getLocation().toVector()).
									normalize();
							Vector newVec = vec.multiply(0.7D);
							Vector useCharge = newVec.multiply(1.2D).multiply(Math.min(0.34D, Math.max(getCharge(p), 0.23D)));
							e.setVelocity(useCharge);
						}

					}
				}
			} catch (NullPointerException e) {

			}


			if (isCorrectItem(item)) {
				onUpdate(p);
			} else {
				memoryRemove(p);
			}

		} 



	}
	
	
	
	@EventHandler
	public void onClick(PlayerInteractEvent e) {

		if (e.getAction() == Action.RIGHT_CLICK_AIR ||
				e.getAction() == Action.RIGHT_CLICK_BLOCK) {

			if (isCorrectItem(UtilGeneric.getItemInHand(e.getPlayer()))) {
				List<Entity> tempList = new ArrayList<Entity>();
				for (Entity ent : e.getPlayer().getNearbyEntities(7, 7, 7)) {

					if (!(ent instanceof LivingEntity)) {
						continue;
					}
					if (ent.isDead()) {
						continue;
					}
					if (getLookingAt(e.getPlayer(), ent)) {
						Vector vec = e.getPlayer().getLocation().toVector().subtract(ent.getLocation().toVector()).
								normalize();
						Vector newVec = vec.multiply(0.7D);
						Vector useCharge = newVec.multiply(1.2D).multiply(Math.min(0.15D, getCharge(e.getPlayer())));
						ent.setVelocity(useCharge);
						tempList.add(ent);


					}

				}
				smoother.put(e.getPlayer().getName(), Math.min(5, smoother.get(e.getPlayer().getName())+2));


			}
		}
	}

	public boolean Charge(Player player) {
		if (!charges.containsKey(player.getName()))
			charges.put(player.getName(), 0f);



		float charge = charges.get(player.getName());

		charge = (float) Math.min(1f, charge + 0.013);
		charges.put(player.getName(), charge);

		Display.displayProgress(null, charge, null, false, player);

		return charge >= 1;
	}

	public float getCharge(Player player) {
		if (!charges.containsKey(player.getName()))
			return 0f;

		return charges.get(player.getName());
	}

	private boolean getLookingAt(Player player, Entity ent)
	{
		Location eye = player.getLocation();
		Vector toEntity = ent.getLocation().toVector().subtract(eye.toVector());
		double dot = toEntity.normalize().dot(eye.getDirection());

		return dot > 0.7D;
	}

	private void memoryRemove(Player p) {
		/*if (veccs.containsKey(p.getName())) {
			veccs.remove(p.getName());
		}*/
		if (smoother.containsKey(p.getName())) {
			smoother.remove(p.getName());
		}
	}


	@Override
	public void rel() {

	}


	@Override
	public void quit(Player player) {
		charges.remove(player.getName());
		smoother.remove(player.getName());

	}


	@Override
	public void clearMem() {
		charges.clear();
		smoother.clear();
	}

}
