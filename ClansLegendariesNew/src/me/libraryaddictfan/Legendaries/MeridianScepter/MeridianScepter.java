package me.libraryaddictfan.Legendaries.MeridianScepter;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import me.libraryaddictfan.ClansLegendaries;
import me.libraryaddictfan.Legendaries.Legendary;
import me.libraryaddictfan.Utilities.ConfigSections;
import me.libraryaddictfan.Utilities.ConfigUtils;
import me.libraryaddictfan.Utilities.Display;
import me.libraryaddictfan.Utilities.UtilChat;
import me.libraryaddictfan.Utilities.UtilNms;
import me.libraryaddictfan.Utilities.Utils;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.MovingObjectPosition;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class MeridianScepter extends Legendary {

	private static boolean targetEnt;
	private static ArrayList<ScepterShot> shots;
	private HashMap<String, Long> cooldown;
	private int damage;

	public MeridianScepter(ClansLegendaries mainn, ItemStack itemm,
			String namee, int damagee) {
		super(mainn, itemm, namee, Arrays.asList(
				UtilChat.white("Legend says that this scepter"),
				UtilChat.white("was found, and retrieved from"),
				UtilChat.white("the deepest trench in all of"),
				UtilChat.white("Minecraftia. It is said that he"),
				UtilChat.white("wields this scepter holds"),
				UtilChat.white("the power of Poseidon himself."),
				"",
				UtilChat.yellow("Right-Click", true) + " to use "
						+ UtilChat.green("Scepter", false)), damagee);
		shots = new ArrayList<ScepterShot>();
		cooldown = new HashMap<String, Long>();
		damage = ConfigUtils.getIntegerSection(ConfigSections.SCEPTER_DAMAGE);
	}

	public static boolean isTargetEnt() {
		return targetEnt;
	}

	@Override
	public void loop() {

		if (!shots.isEmpty()) {

			@SuppressWarnings("unchecked")
			ArrayList<ScepterShot> copy = (ArrayList<ScepterShot>) shots
					.clone();
			for (ScepterShot shot : copy) {
				if ((!shot.getArrow().isDead()) && shot.isGone() == false) {
					shot.update();
				}
			}
		}

		for (String s : cooldown.keySet()) {

			Player p = Bukkit.getServer().getPlayer(s);

			if (((System.currentTimeMillis() - cooldown.get(s)) / 1000) > 1) {
				cooldown.remove(s);
				p.sendMessage(ChatColor.BLUE + getName() + ChatColor.BLUE
						+ "> " + ChatColor.GRAY + "You can use "
						+ ChatColor.GREEN + getName());
				if (isCorrectItem(p.getItemInHand())) {
					Display.display(ChatColor.GREEN + "§l" + getName()
							+ " Recharged", p);
				}

			} else {
				if (isCorrectItem(p.getItemInHand())) {
					Double x = ((2.0 - (double) (Math.pow(10, -1) * ((System
							.currentTimeMillis() - cooldown.get(p.getName())) / 100))));
					double divide = (double) (System.currentTimeMillis() - cooldown
							.get(s)) / (double) 2000.0;
					String[] zz = x.toString().replace('.', '-').split("-");
					String concat = zz[0] + "." + zz[1].substring(0, 1);
					Display.displayProgress("§l" + getName(), divide,
							ChatColor.WHITE + " " + concat + " Seconds", false,
							p);
				}

			}
		}

	}

	public static void removeShot(ScepterShot shot) {
		if (!shots.contains(shot)) {
			return;
		}
		shots.remove(shot);

	}

	@EventHandler
	public void onRightClick(PlayerInteractEvent e) {
		Player p = (Player) e.getPlayer();

		if (e.getAction() == Action.RIGHT_CLICK_AIR
				|| e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (isCorrectItem(p.getItemInHand())) {
				if (p.getLocation().getBlock().isLiquid()) {
					p.sendMessage(ChatColor.BLUE + getName() + ChatColor.BLUE
							+ "> " + ChatColor.GRAY + "You cannot use "
							+ ChatColor.GREEN + getName() + ChatColor.GRAY
							+ " in water.");
					return;
				}

				if (cooldown.containsKey(p.getName())) {
					return;
				} else {
					p.playSound(p.getLocation(), Sound.valueOf("BLAZE_BREATH"),
							1F, 0F);

					ScepterShot shot = new ScepterShot(getMain(), p);
					cooldown.put(p.getName(), System.currentTimeMillis());
					shots.add(shot);
					shot.launch();
				}
			}
		}

	}

	@EventHandler()
	public void onArrowHit(ProjectileHitEvent e) {
		if (e.getEntity() instanceof Arrow) {
			Arrow arrow = (Arrow) e.getEntity();

			for (ScepterShot shot : shots) {
				if (shot.getArrow() == arrow) {
					Block block = null;
					Block hitBlock = null;
					block = shot.getArrow().getLocation().getBlock();
					Material type = null;
					type = block.getType();
					hitBlock = null;

					Entity nmsArrow = ((org.bukkit.craftbukkit.v1_8_R3.entity.CraftArrow) arrow).getHandle();
					MovingObjectPosition position = new MovingObjectPosition(nmsArrow);
					
					Object posType = UtilNms.getFieldAndValue(MovingObjectPosition.class, "type", position);
					if (posType == MovingObjectPosition.EnumMovingObjectType.BLOCK) {
						BlockPosition pos = position.a();

						hitBlock = e.getEntity().getWorld().getBlockAt(new Location(e.getEntity().getWorld(),
												pos.getX(), pos.getY(), pos.getZ()));

					}

					if (hitBlock != null && hitBlock.getType() != Material.AIR) {

						if (type == Material.STATIONARY_LAVA
								|| type == Material.STATIONARY_WATER
								|| type == Material.WATER
								|| type == Material.LAVA) {
							return;
						}

						arrow.teleport(new Location(arrow.getWorld(), 0, -10, 0));

						if (shot.isToRemove()) {
							continue;
						}
						shot.delete();
						continue;
					}
				}
			}
		}
	}

	@EventHandler
	public void onDeath(EntityDeathEvent e) {
		if (e.getEntity() instanceof Arrow) {
			Arrow arrow = (Arrow) e.getEntity();
			for (ScepterShot shot : shots) {
				if (shot.getArrow() == arrow) {
					if (shot.isToRemove()) {
						return;
					} else {
						shot.delete();
					}
				}
			}
		}
	}

	@EventHandler()
	public void onDmg(EntityDamageByEntityEvent e) {
		if (e.isCancelled()) {
			if (e.getDamager() instanceof Arrow) {
				Arrow arrow = (Arrow) e.getDamager();
				for (ScepterShot shot : shots) {
					if (shot.getArrow() == arrow) {
						arrow.teleport(new Location(arrow.getWorld(), 0, -10, 0));
						shot.delete();
					}
				}

			}
			return;
		}

		if (e.getDamager() instanceof Arrow) {
			if (e.getEntity() instanceof LivingEntity) {
				Arrow arrow = (Arrow) e.getDamager();
				LivingEntity struckEnt = (LivingEntity) e.getEntity();
				if (struckEnt.isDead()) {
					return;
				}
				for (ScepterShot shot : shots) {
					if (shot.getArrow() == arrow) {
						if (shot.getShooter() == struckEnt) {
							e.setCancelled(true);

						} else {
							arrow.teleport(new Location(arrow.getWorld(), 0,
									-10, 0));
							if (struckEnt instanceof Player) {
								Player struck = (Player) struckEnt;

								shot.getShooter().sendMessage(
										ChatColor.BLUE + "Clans> "
												+ ChatColor.GRAY
												+ "You struck "
												+ ChatColor.YELLOW
												+ struck.getName()
												+ ChatColor.GRAY
												+ " with your "
												+ ChatColor.YELLOW + getName()
												+ ChatColor.GRAY + ".");

								struck.sendMessage(ChatColor.BLUE + "Clans> "
										+ ChatColor.YELLOW
										+ shot.getShooter().getName()
										+ ChatColor.GRAY + " hit you with a "
										+ ChatColor.YELLOW + getName()
										+ ChatColor.GRAY + ".");
							} else {
								String string = struckEnt.getType().toString()
										.toLowerCase().replace("_", " ");

								shot.getShooter().sendMessage(
										ChatColor.BLUE + "Clans> "
												+ ChatColor.GRAY
												+ "You struck "
												+ ChatColor.YELLOW + string
												+ ChatColor.GRAY
												+ " with your "
												+ ChatColor.YELLOW + getName()
												+ ChatColor.GRAY + ".");
							}

							e.setCancelled(true);

							Player p = shot.getShooter();

							shot.delete();
							Bukkit.getServer()
									.getScheduler()
									.scheduleSyncDelayedTask(
											getMain(),
											() -> {
												if (struckEnt.isDead()) {
													return;
												}

												struckEnt
														.addPotionEffect(new PotionEffect(
																PotionEffectType.BLINDNESS,
																50, 0));
												arrow.getWorld()
														.strikeLightningEffect(
																struckEnt
																		.getLocation());
												struckEnt.damage(damage, p);

											}, 60);

						}
					}
				}

			}
		}

	}

	@Override
	public void rel() {
		targetEnt = ConfigUtils
				.getBooleanSection(ConfigSections.SCEPTER_TARGET_ENT);
		damage = ConfigUtils.getIntegerSection(ConfigSections.SCEPTER_DAMAGE);
	}

	@Override
	public void quit(Player player) {

	}

	@Override
	public void clearMem() {
		cooldown.clear();
		shots.clear();
	}

}
