package me.libraryaddictfan.Legendaries;

import java.util.Arrays;
import java.util.HashMap;

import me.libraryaddictfan.ClansLegendaries;
import me.libraryaddictfan.Utilities.Display;
import me.libraryaddictfan.Utilities.UtilChat;
import net.minecraft.server.v1_8_R3.BlockPosition;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class RunedPickaxe extends Legendary {

	private HashMap<String, Long> cooldown;
	private HashMap<String, Long> instantMining;


	public RunedPickaxe(ClansLegendaries clanslegendariess, ItemStack item, String name, int damage) {
		super(clanslegendariess, item, name, 
				Arrays.asList(
						UtilChat.white("What an interesting design this"),
						UtilChat.white("pickaxe seems to have!"),
						"",
						UtilChat.white("Deals " + UtilChat.yellow("3 Damage", true) + " with attack"),
						UtilChat.yellow("Right-Click", true) + " to use " + UtilChat.green("Instant", false),
						UtilChat.green("Mine", false)
						)
						, damage);
		cooldown = new HashMap<String, Long>();
		instantMining = new HashMap<String, Long>();
	}

	@Override
	public void rel() {


	}

	@EventHandler(priority=EventPriority.HIGHEST)
	public void onBlockMine(BlockBreakEvent e) {
		if (e.isCancelled()) {
			return;
		}
		Player p = e.getPlayer();
		Block b = e.getBlock();
		if (isCorrectItem(p.getItemInHand())) {
			if (b.getType() ==  Material.BEDROCK) {
				return;
			}
			
			b.getWorld().playEffect(b.getLocation(), Effect.STEP_SOUND, 
					net.minecraft.server.v1_8_R3.Block.getCombinedId(((CraftPlayer) p).getHandle().getWorld().getType(
							new BlockPosition(b.getLocation().getX(), b.getLocation().getY(), b.getLocation().getZ()))));
			b.breakNaturally();
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

					Double x = ((15.0-(double) (Math.pow(10, -1)*((System.currentTimeMillis() - cooldown.get(p.getName()))/100))));

					String[] zz = x.toString().replace('.', '-').split("-");
					String concat = zz[0] + "." + zz[1].substring(0, 1);


					try {
						p.sendMessage(ChatColor.BLUE + getName() + "> " + ChatColor.GRAY +
								"Your cannot use " + ChatColor.GREEN + "Instant Mine" + ChatColor.GRAY + 
								" for " + ChatColor.GREEN +
								concat + " Seconds");
					} catch(IndexOutOfBoundsException exc) {
						Bukkit.getServer().getLogger().warning("Index out of bounds in Runed Pickaxe msg. "
								+ "Should have been canceled");
					}


					return;
				} else if (instantMining.containsKey(p.getName())) {
					return;
				}


				instantMining.put(p.getName(), System.currentTimeMillis());
				p.removePotionEffect(PotionEffectType.FAST_DIGGING);
				Display.displayTitleAndSubtitle(p, " ", ChatColor.WHITE + "Instant mine enabled for " + ChatColor.YELLOW
						+ "12 Seconds", 5, 30, 5);
				p.sendMessage(ChatColor.BLUE + getName() + "> " + ChatColor.GRAY + "You used " + ChatColor.GREEN + 
						"Instant Mine");
			}
		} else if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
			if (isCorrectItem(item)) {

				if (instantMining.containsKey(p.getName())) {
					
					if (e.isCancelled()) {
						return;
					}

					BlockBreakEvent newEvent = new BlockBreakEvent(e.getClickedBlock(), e.getPlayer());
					Bukkit.getServer().getPluginManager().callEvent(newEvent);
				}

			}

		}



	}


	@Override
	public void loop() {

		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			if (instantMining.containsKey(p.getName())) {

			} else {
				if (isCorrectItem(p.getItemInHand())) {
					p.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 20 * 4, 103));
				}
			}
		}


		for (String s : instantMining.keySet()) {
			Player p = Bukkit.getServer().getPlayer(s);
			if (p == null || p.isOnline() == false) {
				instantMining.remove(s);
				continue;
			}
			if (((System.currentTimeMillis()-instantMining.get(s))/1000) > 11) {
				instantMining.remove(s);
				cooldown.put(p.getName(), System.currentTimeMillis());

			} else {
				if (isCorrectItem(p.getItemInHand())) {
					double divide = (double)(System.currentTimeMillis() - instantMining.get(s))/(double)12000.0;

					Display.displayProgress("§l" + "Instant Mine", divide
							, null, true, p);
				}
			}
		}

		for (String s : cooldown.keySet()) {


			Player p = Bukkit.getServer().getPlayer(s);
			if (p == null || p.isOnline() == false) {
				cooldown.remove(s);
				continue;
			}
			if (((System.currentTimeMillis()-cooldown.get(s))/1000) > 14) {
				cooldown.remove(s);
				p.sendMessage(ChatColor.BLUE + getName() + ChatColor.BLUE + "> " + ChatColor.GRAY + 
						"You can use " + ChatColor.GREEN + "Instant Mine");
				if (isCorrectItem(p.getItemInHand())) {
					Display.display(ChatColor.GREEN + "§l" + "Instant Mine" + " Recharged", p);
				}
				p.playSound(p.getLocation(), Sound.valueOf("NOTE_PLING"), 5, 1);

			} else {
				if (isCorrectItem(p.getItemInHand())) {
					Double x = ((15.0-(double) (Math.pow(10, -1)*((System.currentTimeMillis() - cooldown.get(p.getName()))/100))));
					double divide = (double)(System.currentTimeMillis() - cooldown.get(s))/(double)15000.0;
					String[] zz = x.toString().replace('.', '-').split("-");
					String concat = zz[0] + "." + zz[1].substring(0, 1);
					Display.displayProgress("§l" + "Instant Mine", divide
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
		cooldown.clear();
		instantMining.clear();
	}



}
