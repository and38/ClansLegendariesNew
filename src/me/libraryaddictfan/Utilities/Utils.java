package me.libraryaddictfan.Utilities;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
public class Utils {

	private static ArrayList<Object> particlesCache;
	private static ArrayList<Class<?>> classCache;
	private static HashMap<String, Method> methodCache;

	public static boolean getVersion() {
		String[] s = Bukkit.getServer().getVersion().split(":");
		String q = s[1].replace(" ", "");
		q = q.replace(")", "");
		return q.contains("1.8");

	}

	@SuppressWarnings("deprecation")
	public static ItemStack getItemInHand(Player p) {
		if (p == null) {

			return new ItemStack(Material.CARPET);

		}


		try {
			return p.getInventory().getItemInHand();
		} catch (NullPointerException e) {
			return new ItemStack(Material.CARPET);
		}
	}

	static {
		particlesCache = new ArrayList<Object>();
		classCache = new ArrayList<Class<?>>();
		methodCache = new HashMap<String, Method>();
		
	
		String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
		try {
			classCache.add(Class.forName("net.minecraft.server." + version + "." + "PacketPlayOutWorldParticles"));
			classCache.add(Class.forName("net.minecraft.server." + version + "." + "PacketPlayOutChat"));
			classCache.add(Class.forName("net.minecraft.server." + version + "." + "IChatBaseComponent"));
			classCache.add(Class.forName("net.minecraft.server." + version + "." + "EnumParticle"));
			classCache.add(Class.forName("net.minecraft.server." + version + "." + "Packet"));
			classCache.add(Class.forName("net.minecraft.server." + version + "." + "PlayerConnection"));
			classCache.add(Class.forName("net.minecraft.server." + version + "." + "EntityArrow"));
			try {
				classCache.add(Class.forName("net.minecraft.server." + version + "." + "ChatSerializer"));
			} catch(ClassNotFoundException e) {
				Bukkit.getServer().getConsoleSender().sendMessage("ClansLegendariesRelease> " + ChatColor.GREEN + 
						"Using Declared Classes");
			}
			

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		Object[] obj = getNMSClass("EnumParticle").getEnumConstants();

		for (Object object : obj) {
			particlesCache.add(object);
		}
		if (!Utils.getVersion()) {
			Method full = null;
			Method gethan = null;
			try {
				full = LivingEntity.class.getMethod("setGravity", boolean.class);
				gethan = PlayerInteractEvent.class.getMethod("getHand");
			} catch (NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
				Bukkit.getServer().getLogger().severe("ClansLegendariesRelease> Reflection Error (Method get)");
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + 
						"ClansLegendariesRelease> Reflection Error (Method get)");
			}

			methodCache.put("setGravity", full);
			methodCache.put("getHand", gethan);
		}


	}



	/**
	 * This method gets a method from a certain class and invokes it.
	 * It will return the output whether it be a value or just null.
	 * 
	 * 
	 * @param method - The Method you need.
	 * @param methodsClass - The Class of the requested method.
	 * @param argsAsClass - The arguments of the method as class types. E.x. : boolean.class
	 * @param instance - The instance you want to use the method on. Use null for static methods.
	 * @param argsReal - The real arguments for the method.
	 * @return Returns the output of the method requested. May be null.
	 */
	public static Object getAndInvoke(String method, Class<?> methodsClass, Class<?>[] argsAsClass,
			Object instance, Object... argsReal) {
		Method full = null;
		boolean done = false;
		for (String s : methodCache.keySet()) {
			if (method.equals(s)) {
				full = methodCache.get(s);
				done = true;
			}
		}


		if (done == false) {
			try {
				full = methodsClass.getMethod(method, argsAsClass);
			} catch (NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
				Bukkit.getServer().getLogger().severe("ClansLegendariesRelease> Reflection Error (Method get)");
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + 
						"ClansLegendariesRelease> Reflection Error (Method get)");
			}
		}


		return invokeMethod(full, instance, argsReal);

	}

	/**
	 * This method invokes a method you already have.
	 * It will return the output whether it be a value or just null.
	 * 
	 * 
	 * @param method - The Method you have.
	 * @param instance - The instance you want to use the method on. Use null for static methods.
	 * @param argsReal - The real arguments for the method.
	 * @return Returns the output of the method requested. May be null.
	 */
	public static Object invokeMethod(Method method,
			Object instance, Object... argsReal) {

		if (argsReal.length == 0) {
			argsReal = new Object[method.getParameters().length];
		}

		try {
			return method.invoke(instance, argsReal);
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
			Bukkit.getServer().getLogger().severe("ClansLegendariesRelease> Reflection Error (Method invoke)");
			Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + 
					"ClansLegendariesRelease> Reflection Error (Method invoke)");
		}
		return null;
	}

	public static boolean onGround(Player p) {
		if (p.getLocation().clone().subtract(0, 1, 0).getBlock().getType() != Material.AIR
				&& p.getLocation().clone().subtract(0, 1, 0).getBlock()
				.getType() != Material.WATER
				&& p.getLocation().clone().subtract(0, 1, 0).getBlock()
				.getType() != Material.STATIONARY_WATER
				&& p.getLocation().clone().subtract(0, 1, 0).getBlock()
				.getType() != Material.LAVA
				&& p.getLocation().clone().subtract(0, 1, 0).getBlock()
				.getType() != Material.STATIONARY_LAVA
				&& p.getLocation().getBlock().isLiquid() != true) {
			return true;
		} else {
			return false;
		}
	}

	public static void sendParticles(Player player, EnumParticle particle, boolean distEx, float x, float y, float z, 
			float xOffset, float yOffset, float zOffset, float speed, int amount, int[] moreData)  {
		PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(particle, distEx, x, y, z, xOffset, yOffset, zOffset, speed, amount, moreData);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
	}


	public static void sendPacket(Player player, Object packet) {
		try {
			Object handle = player.getClass().getMethod("getHandle").invoke(player);
			Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
			playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
		}

		catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static Class<?> getNMSClass(String name) {
		String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

		if (!classCache.isEmpty()) {
			for (Class<?> obj : classCache) {
				if (name.equals("Packet") || name.equals("IChatBaseComponent") || name.equals("INetworkManager")) {
					if (!obj.toString().contains("interface")) {
						continue;
					}
					String split = splitForCache(obj.toString(), true);
					if (split.equals("net.minecraft.server." + version + "." + name)) {
						return obj;
					} 
					continue;
				} else if (name.equals("ChatSerializer")) {
					String split = splitForCache(obj.toString(), false);
					split.replace("IChatBaseComponent.", "");
					if (split.equals("net.minecraft.server." + version + "." + name)) {
						return obj;
					}
						
					
				} else {
					String split = splitForCache(obj.toString(), false);
					if (split.equals("net.minecraft.server." + version + "." + name)) {
						return obj;
					}
				}
			}

		} 

		try {
			return Class.forName("net.minecraft.server." + version + "." + name);
		}

		catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String splitForCache(String string, boolean interfacee) {
		if (interfacee) {
			String split = string.replace("interface", "");
			split.replace(" ", "");
			split = split.substring(1, split.length());
			return split;
		} else {
			String split = string.replace("class", "");
			split.replace(" ", "");
			split = split.substring(1, split.length());
			return split;
		}
	}

	public static Object getEnumParticleConst(String name) {


		if (!particlesCache.isEmpty()) {
			for (Object object : particlesCache) {
				if (object.toString().equalsIgnoreCase(name)) {
					return object;
				} else {
					continue;
				}
			}
			return null;
		} else {
			Object[] obj = getNMSClass("EnumParticle").getEnumConstants();

			for (Object object : obj) {
				if (object.toString().toLowerCase().equals(name.toLowerCase())) {
					return object;
				} else {
					continue;
				}
			}
			return null;
		}
	}

	public static String header() {
		return ChatColor.BLUE + "Clans> " + ChatColor.GRAY;
	}


}
