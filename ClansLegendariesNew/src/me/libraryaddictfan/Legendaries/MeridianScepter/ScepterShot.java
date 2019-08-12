package me.libraryaddictfan.Legendaries.MeridianScepter;

import java.util.HashMap;

import me.libraryaddictfan.ClansLegendaries;
import me.libraryaddictfan.Utilities.Utils;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class ScepterShot {

	private boolean toRemove = false;


	private long timeAliveStart;
	private long totalTimeAlive;
	private Player shooter;
	private Location start;
	private Arrow fakeEnt;
	private boolean gone = false;
	@SuppressWarnings("unused")
	private ClansLegendaries main;
	private LivingEntity target;
	private Vector vec;

	public ScepterShot(ClansLegendaries mainn, Player player) {
		shooter = player;
		main = mainn;
		timeAliveStart = System.currentTimeMillis();
		totalTimeAlive = System.currentTimeMillis()-timeAliveStart;
	}

	public void update() {
		totalTimeAlive = System.currentTimeMillis()-timeAliveStart;
		if (((long)(totalTimeAlive/1000)) > 12 || fakeEnt.getTicksLived() > 220 || 
				fakeEnt.getLocation().distance(start) > 84 || fakeEnt.getVelocity() == null || fakeEnt.getVelocity()
				.equals(new Vector(0,0,0))) {
			delete();
		}
		//TODO make sound continuous.
		for (Player pla : Bukkit.getServer().getOnlinePlayers()) {
			if (toRemove != true) {
				int[] data = new int[0];
				Utils.sendParticles(
						pla,
						EnumParticle.REDSTONE, 
						false, 
						(float)fakeEnt.getLocation().getX(), 
						(float)fakeEnt.getLocation().getY(), 
						(float)fakeEnt.getLocation().getZ(), 
						0.77F, 
						0, 
						1,
						1, 
						0, 
						data);
				for (int i = 0; i<10; i++) {
					if (toRemove == true || gone || !pla.isOnline()) {
						return;
					}
					Utils.sendParticles(
							pla,
							EnumParticle.REDSTONE, 
							false, 
							(float)fakeEnt.getLocation().getX(), 
							(float)fakeEnt.getLocation().getY(), 
							(float)fakeEnt.getLocation().getZ(), 
							0.77F, 
							0, 
							1,  
							1, 
							0,
							data);
				}
			}				
		}


		if (target == null) {

			if (toRemove != true) {
				if (vec == null) {
					fakeEnt.setVelocity(fakeEnt.getVelocity());
				} else {
					fakeEnt.setVelocity(vec);
				}

				searchForTarget();

			} 
		} else {

			if (!target.isDead() && target.hasLineOfSight(fakeEnt) && target.getLocation().
					distance(fakeEnt.getLocation()) < 64) {
				if (toRemove != true) {
					Vector toTarget = target.getEyeLocation().clone().
							subtract(fakeEnt.getLocation()).toVector();

					Vector dirVelocity = fakeEnt.getVelocity().clone().normalize();
					Vector dirToTarget = toTarget.clone().normalize();



					Vector newVelocity;

					Vector newDir = dirVelocity.clone().add(dirToTarget.clone());
					newDir.normalize();
					newVelocity = newDir.clone().multiply(0.4D);
					fakeEnt.setVelocity(newVelocity.add(new Vector(0.0D, 0.01D, 0.0D)));
				}
			} else {
				if (toRemove != true) {

					target = null;
				}

			}
		}


		if (toRemove == true) { 
			MeridianScepter.removeShot(this);
			fakeEnt.remove();

			gone = true;
			toRemove = false;
			try {
				this.finalize();

			} catch (Throwable e) {

				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "ClansLegendariesRelease> Error when trying"
						+ " to de-lag scepter.");
			}

		}

	}

	public boolean isGone() {
		return gone;
	}

	public void delete() {
		toRemove = true;
	}

	public void launch() {
		Arrow arrow = shooter.launchProjectile(Arrow.class, shooter.getLocation().getDirection().multiply(0.5D));

		fakeEnt = arrow;
		hideArrow(arrow);
		start = fakeEnt.getLocation();
		fakeEnt.setVelocity(shooter.getLocation().getDirection().multiply(0.67));
		vec = fakeEnt.getVelocity().clone().multiply(0.67);
	}

	public Arrow getArrow() {
		return fakeEnt;
	}

	public Player getShooter() {
		return shooter;
	}

	public LivingEntity getTarget() {
		return target;
	}

	private void searchForTarget() {
		HashMap<LivingEntity, Double> entities = new HashMap<LivingEntity, Double>();
		LivingEntity curLowest = null;

		for (Entity ent : fakeEnt.getNearbyEntities(30, 15, 30)) {
			if (ent instanceof LivingEntity) {

				if (ent == shooter || ent instanceof Arrow || (!MeridianScepter.isTargetEnt() && !(ent instanceof Player)) || ent instanceof ArmorStand
						|| ent.isDead() || fakeEnt.getLocation().distance(ent.getLocation()) > 40) {
					continue;
				}

				if (ent instanceof Player) {
					Player pl = (Player) ent;
					if (!shooter.canSee(pl)) {
						continue;
					}
					if (pl.getGameMode() == GameMode.CREATIVE || pl.getGameMode() == GameMode.SPECTATOR) {
						continue;
					}
				}

				LivingEntity entLiving = (LivingEntity) ent;

				entities.put(entLiving, Math.abs(entLiving.getLocation().distance(fakeEnt.getLocation())));
			}
			continue;
		}

		for (LivingEntity ent : entities.keySet()) {
			if (curLowest == null || entities.get(ent) <= entities.get(curLowest)) {
				curLowest = ent;
			} 
		}

		target = curLowest;
	}

	public boolean isToRemove() {
		if (toRemove == true) {
			return true;
		}
		return false;
	}

	private void hideArrow(Arrow arrow) {
		PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(new int[]{arrow.getEntityId()});
		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			Utils.sendPacket(p, packet);
		}
	}
}
