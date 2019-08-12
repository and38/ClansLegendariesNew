package me.libraryaddictfan;

import java.util.ArrayList;
import java.util.Arrays;

import me.libraryaddictfan.Legendaries.AlligatorsTooth;
import me.libraryaddictfan.Legendaries.GiantsBroadsword;
import me.libraryaddictfan.Legendaries.HyperAxe;
import me.libraryaddictfan.Legendaries.Legendary;
import me.libraryaddictfan.Legendaries.MagneticMaul;
import me.libraryaddictfan.Legendaries.RunedPickaxe;
import me.libraryaddictfan.Legendaries.ScytheOfTheFallenLord;
import me.libraryaddictfan.Legendaries.WindBlade;
import me.libraryaddictfan.Legendaries.MeridianScepter.MeridianScepter;
import me.libraryaddictfan.Utilities.ConfigSections;
import me.libraryaddictfan.Utilities.ConfigUtils;
import me.libraryaddictfan.Utilities.UtilGeneric;
import me.libraryaddictfan.commands.LegendariesCommand;
import me.libraryaddictfan.commands.LegendariesCommandTabComplete;
import me.libraryaddictfan.commands.LegendaryCommand;
import me.libraryaddictfan.commands.LegendaryCommandTabComplete;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;

public class ClansLegendaries extends JavaPlugin implements Listener {

	
	
	private ArrayList<Legendary> legendaries;
	public static Permission legendaryCommandUse = new Permission("clanslegendaries.legendary-command.use", "Use the legendary command.");
	public static Permission legendaryCommandGive = new Permission("clanslegendaries.legendary-command.giveLegendary", "Give a legendary to another player.");
	public static Permission legendariesCommandUse = new Permission("clanslegendaries.legendaries-command.use", "Use the legendaries command");
	public static Permission legendariesCommandHelp = new Permission("clanslegendaries.legendaries-command.help", "Use the help feature of the legendaries command");
	public static Permission legendariesCommandSetSection = new Permission("clanslegendaries.legendaries-command.setSection", "Use the setSection feature of the legendaries command");
	public static Permission legendariesCommandTestConfig = new Permission("clanslegendaries.legendaries-command.testConfig", "Use the testConfig feature of the legendaries command");
	public static Permission legendariesCommandReloadConfig = new Permission("clanslegendaries.legendaries-command.reloadConfig", "Use the reloadConfig feature of the legendaries command");
	public static Permission legendariesCommandFixHyperDelay = new Permission("clanslegendaries.legendaries-command.fixHyperDelay", "Use the fixHyperDelay feature of the legendaries command");


	@Override
	public void onEnable() {
		saveDefaultConfig();
		
		getConfig().options().header("Default settings: false, 0.66, true, 7 (in ticks), 4.");
		getConfig().options().copyHeader(true);
		ConfigUtils.register(this);

		legendaryCommandUse.setDefault(PermissionDefault.TRUE);
		Bukkit.getServer().getPluginManager().addPermission(legendaryCommandUse);
		Bukkit.getServer().getPluginManager().addPermission(legendaryCommandGive);
		Bukkit.getServer().getPluginManager().addPermission(legendariesCommandUse);
		Bukkit.getServer().getPluginManager().addPermission(legendariesCommandHelp);
		Bukkit.getServer().getPluginManager().addPermission(legendariesCommandSetSection);
		Bukkit.getServer().getPluginManager().addPermission(legendariesCommandTestConfig);
		Bukkit.getServer().getPluginManager().addPermission(legendariesCommandReloadConfig);
		Bukkit.getServer().getPluginManager().addPermission(legendariesCommandFixHyperDelay);

		legendaries = new ArrayList<Legendary>();
		getCommand("legendaries").setExecutor(new LegendariesCommand(this));
		getCommand("legendaries").setTabCompleter(new LegendariesCommandTabComplete());
		getCommand("legendary").setExecutor(new LegendaryCommand(this));
		getCommand("legendary").setTabCompleter(new LegendaryCommandTabComplete(this));
		getCommand("legendary").setAliases(Arrays.asList("leg"));
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
		registerLeggies();
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, () -> {
				for (Legendary leg : legendaries) {
					leg.loop();
				}
			}, 2, 0);


		for (Legendary leg : legendaries) {
			leg.rel();
		}

	}

	@Override
	public void onDisable() {
		saveConfig();
		for (Legendary l : legendaries) {
			l.clearMem();
		}
	}


	public ArrayList<Legendary> getLegendaries() {
		return legendaries;
	}

	private void registerLeggies() {	

		AlligatorsTooth tooth = new AlligatorsTooth(this, new ItemStack(Material.RECORD_4), "Alligators Tooth", 7);    
		GiantsBroadsword broad = new GiantsBroadsword(this, new ItemStack(Material.GOLD_RECORD), "Giants Broadsword", 10);    
		WindBlade blade = new WindBlade(this, new ItemStack(Material.GREEN_RECORD), "Wind Blade", 7);
		MeridianScepter scep = new MeridianScepter(this, new ItemStack(Material.RECORD_6), "Meridian Scepter", 3);
		HyperAxe hyper = new HyperAxe(this, new ItemStack(Material.RECORD_3), "Hyper Axe",
				(int) ConfigSections.HYPERAXE_DAMAGE.getDefaultValue());
		MagneticMaul maul = new MagneticMaul(this, new ItemStack(Material.RECORD_5), "Magnetic Maul", 8);
		RunedPickaxe pick = new RunedPickaxe(this, new ItemStack(Material.RECORD_7), "Runed Pickaxe", 1);
		ScytheOfTheFallenLord scythe = new ScytheOfTheFallenLord(this, new ItemStack(Material.RECORD_8), "Scythe of the Fallen Lord", 8);



		legendaries.add(blade);
		legendaries.add(scep);
		legendaries.add(hyper);
		legendaries.add(maul);
		legendaries.add(broad);
		legendaries.add(scythe);
		legendaries.add(tooth);
		legendaries.add(pick);


	}
}
