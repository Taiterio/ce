package com.taiter.ce;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.sk89q.worldguard.bukkit.RegionQuery;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.taiter.ce.CItems.CItem;
import com.taiter.ce.Enchantments.CEnchantment;
import com.taiter.ce.Enchantments.CEnchantment.Application;
import com.taiter.ce.Enchantments.CEnchantment.Cause;



public class Tools {

	public 			String	prefix	= "CE - ";
	public			Random  random = new Random();
	public          Boolean repeatPotionEffects = Boolean.parseBoolean(Main.config.getString("Global.Enchantments.RepeatPotionEffects"));
	public          int     repeatDelay         = Integer.parseInt(Main.config.getString("Global.Enchantments.RepeatDelay"));


	//ENCHANTMENTS
	public boolean isApplicationCorrect(Application app, Material matToApplyTo) {

		String mat = matToApplyTo.toString();
		
		if(app == Application.BOW && mat.equals(Material.BOW.toString()))
			return true;
		else if(app == Application.BOOTS && mat.endsWith("BOOTS"))
			return true;
		else if(app == Application.HELMET && mat.endsWith("HELMET"))
			return true;
		else if(app == Application.ARMOR && (mat.endsWith("HELMET") || mat.endsWith("CHESTPLATE") || mat.endsWith("LEGGINGS") || mat.endsWith("BOOTS")))
			return true;
		else if(app == Application.TOOL && (mat.endsWith("PICKAXE") || mat.endsWith("SPADE") || mat.endsWith("AXE") || mat.endsWith("HOE")))
			return true;
		return false;
	}

	public CEnchantment getEnchantmentByDisplayname(String name) {
		for(CEnchantment ce : Main.enchantments) {
			if(name.toLowerCase().contains(ChatColor.stripColor(ce.getDisplayName()).toLowerCase())){
				return ce;
			}
		}
		return null;
	}

	
	
	public CItem getItemByOriginalname(String name) {
		for(CItem ci : Main.items)
			if(ci.getOriginalName().equals(name))
				return ci;
		return null;
	}
	
	//INVENTORIES
	
	public CItem getItemByDisplayname(String name) {
		for(CItem ci : Main.items)
			if(ci.getDisplayName().equals(name))
				return ci;
		return null;
	}

	public Inventory getPreviousInventory(String name) {
		if(name.equals(prefix + "Enchantments") || name.equals(prefix + "Items") || name.equals(prefix + "Config"))
			return Main.CEMainMenu;
		else if(name.equals(prefix + "Enchanting") || name.equals(prefix + "Armor") || name.equals(prefix + "Bow") || name.equals(prefix + "Tool") || name.equals(prefix + "Global") || name.equals(prefix + "Helmet") || name.equals(prefix + "Boots"))
			return Main.CEEnchantmentMainMenu;
		else if(name.equals(prefix + "Item Creation"))
			return Main.CEItemMenu;
		return null;
	}

	public Inventory getNextInventory(String name) {

		if(name.equals("Enchantments"))
			return Main.CEEnchantmentMainMenu;
		else if(name.equals("Items"))
			return Main.CEItemMenu;
		else if(name.equals("Config"))
			return null;
		else if(name.equals("Global"))
			return Main.CEGlobalMenu;
		else if(name.equals("Bow"))
			return Main.CEBowMenu;
		else if(name.equals("Helmet"))
			return Main.CEHelmetMenu;
		else if(name.equals("Boots"))
			return Main.CEBootsMenu;
		else if(name.equals("Armor"))
			return Main.CEArmorMenu;
		else if(name.equals("Tool"))
			return Main.CEToolMenu;
		return null;
	}

	public void openCEMenu(Player p) {
		Inventory tempInv = Main.CEMainMenu;
		if(!p.isOp())
			tempInv.remove(6);
		p.openInventory(tempInv);
	}

	public Inventory getEnchantmentMenu(Player p, String name) {
		if(!p.isOp() && !p.hasPermission("ce.ench.*")) {
			Inventory lInv = getNextInventory(name);
			Inventory enchantments =  Bukkit.createInventory(null, lInv.getSize(), lInv.getTitle());
			enchantments.setContents(lInv.getContents());
			for(int i = 0; i < enchantments.getSize() - 2; i++) {
				ItemStack checkItem = enchantments.getItem(i);
				if(checkItem == null || checkItem.getType().equals(Material.AIR))
					continue;
				for(CEnchantment ce : Main.enchantments) {
					if(checkItem.getItemMeta().getDisplayName().equals(ce.getDisplayName()))
						if(!p.hasPermission("ce.ench." + ce.getOriginalName())) {
							ItemStack item = enchantments.getItem(i);
							ItemMeta im = item.getItemMeta();
							List<String> lore = new ArrayList<String>();
							if(im.hasLore())
								lore = im.getLore();
							lore.add(ChatColor.RED + "You are not permitted to use this");
							im.setLore(lore);
							item.setItemMeta(im);
							enchantments.setItem(i, item);
						}
				}
				}
			return enchantments;
		}

		return getNextInventory(name);
	}

	public Inventory getItemMenu(Player p) {
		if(!p.isOp() && !p.hasPermission("ce.item.*")) {
			Inventory lInv = Main.CEItemMenu;
			Inventory items =  Bukkit.createInventory(null, lInv.getSize(), lInv.getTitle());
			items.setContents(lInv.getContents());
			for(int i = 0; i < items.getSize() - 2; i++) {
				ItemStack item = items.getItem(i);
				if(item == null || item.getType().equals(Material.AIR))
					continue;
				for(CItem ci : Main.items)
					if(item.getItemMeta().getDisplayName().equals(ci.getDisplayName())) {
						if(!p.hasPermission("ce.item." + ci.getOriginalName())) {
							ItemMeta im = item.getItemMeta();
							List<String> lore = new ArrayList<String>();
							if(im.hasLore())
								lore = im.getLore();
							lore.add(ChatColor.RED + "You are not permitted to use this");
							im.setLore(lore);
							item.setItemMeta(im);
						}
				}
			}
			return items;
		}
		
		
		return Main.CEItemMenu;
	}

	public void generateInventories() {

		ItemStack backButton = new ItemStack(Material.NETHER_STAR);

		ItemMeta tempMeta = backButton.getItemMeta();
		List<String> tempLore = new ArrayList<String>();

		tempMeta.setDisplayName(ChatColor.AQUA + "Back");
		backButton.setItemMeta(tempMeta);

		// All inventories are public and the owner is null

		// MAIN MENU
		Inventory MainMenu = Bukkit.createInventory(null, 9, prefix + "Main Menu");
		ItemStack Enchantments = new ItemStack(Material.ENCHANTED_BOOK);
		ItemStack Items        = new ItemStack(Material.ENDER_PORTAL_FRAME);
		ItemStack Config       = new ItemStack(Material.ACTIVATOR_RAIL);

		tempMeta.setDisplayName("Enchantments");
		tempLore.add(ChatColor.ITALIC + "" + ChatColor.GRAY + "You see a set of magic Runes imprinted");
		tempLore.add(ChatColor.ITALIC + "" + ChatColor.GRAY + "on the cover of the book");
		tempMeta.setLore(tempLore);
		tempLore.clear();

		Enchantments.setItemMeta(tempMeta);

		tempMeta.setDisplayName("Items");
		tempLore.add(ChatColor.ITALIC + "" + ChatColor.GRAY + "The Portal appears to be");
		tempLore.add(ChatColor.ITALIC + "" + ChatColor.GRAY + "a stash of Legendary Items");
		tempMeta.setLore(tempLore);
		tempLore.clear();

		Items.setItemMeta(tempMeta);

		tempMeta.setDisplayName("Config");
		tempLore.add(ChatColor.ITALIC + "" + ChatColor.GRAY + "Allows alteration of the very fabric");
		tempLore.add(ChatColor.ITALIC + "" + ChatColor.GRAY + "of Custom Enchantments");
		tempLore.add(ChatColor.ITALIC + "" + ChatColor.RED + "Currently disabled");
		tempMeta.setLore(tempLore);
		tempLore.clear();

		Config.setItemMeta(tempMeta);
		
		tempMeta.setLore(tempLore);

		MainMenu.setItem(2, Enchantments);
		MainMenu.setItem(4, Items);
		MainMenu.setItem(6, Config);

		Main.CEMainMenu = MainMenu;

		// MAIN MENU

		// ENCHANTMENTS MENU
		Inventory EnchantmentMenu = Bukkit.createInventory(null, 9, prefix + "Enchantments");
		EnchantmentMenu.setItem(8, backButton);
		ItemStack Global = new ItemStack(Material.ENCHANTED_BOOK);
		ItemStack Bow = new ItemStack(Material.BOW);
		ItemStack Armor = new ItemStack(Material.ANVIL);
		ItemStack Tool = new ItemStack(Material.SHEARS);
		ItemStack Helmet = new ItemStack(Material.DIAMOND_HELMET);
		ItemStack Boots = new ItemStack(Material.DIAMOND_BOOTS);

		tempMeta.setDisplayName("Global");

		Global.setItemMeta(tempMeta);

		tempMeta.setDisplayName("Bow");

		Bow.setItemMeta(tempMeta);

		tempMeta.setDisplayName("Armor");

		Armor.setItemMeta(tempMeta);
		
		tempMeta.setDisplayName("Tool");

		Tool.setItemMeta(tempMeta);

		tempMeta.setDisplayName("Helmet");

		Helmet.setItemMeta(tempMeta);

		tempMeta.setDisplayName("Boots");
		Boots.setItemMeta(tempMeta);

		EnchantmentMenu.setItem(1, Global);
		EnchantmentMenu.setItem(2, Bow);
		EnchantmentMenu.setItem(3, Armor);
		EnchantmentMenu.setItem(4, Tool);
		EnchantmentMenu.setItem(5, Helmet);
		EnchantmentMenu.setItem(6, Boots);

		Main.CEEnchantmentMainMenu = EnchantmentMenu;
		// ENCHANTMENTS MENU

		// SPECIFIC MENUS
		Inventory ArmorMenu = Bukkit.createInventory(null, 36, prefix + "Armor");
		ArmorMenu.setItem(35, backButton);

		int current = 0;
		ItemStack tempItem = new ItemStack(Material.ENCHANTED_BOOK);
		for(CEnchantment ce : Main.enchantments)
			if(ce.getApplication() == Application.ARMOR) {
				tempMeta.setDisplayName(ce.getDisplayName());
				if(Main.hasEconomy && ce.getCost() > 0) {
					tempLore.add(ChatColor.GRAY + "Cost: " + ce.getCost());
					tempMeta.setLore(tempLore);
				}
				tempItem.setItemMeta(tempMeta);
				ArmorMenu.setItem(current, tempItem);
				
				tempLore.clear();
				tempMeta.setLore(tempLore);
				
				current++;
			}
		Main.CEArmorMenu = ArmorMenu;

		Inventory GlobalMenu = Bukkit.createInventory(null, 36, prefix + "Global");
		GlobalMenu.setItem(35, backButton);

		current = 0;
		for(CEnchantment ce : Main.enchantments)
			if(ce.getApplication() == Application.GLOBAL) {
				tempMeta.setDisplayName(ce.getDisplayName());
				if(Main.hasEconomy && ce.getCost() > 0) {
					tempLore.add(ChatColor.GRAY + "Cost: " + ce.getCost());
					tempMeta.setLore(tempLore);
				}
				tempItem.setItemMeta(tempMeta);
				GlobalMenu.setItem(current, tempItem);

				tempLore.clear();
				tempMeta.setLore(tempLore);
				
				current++;
			}

		Main.CEGlobalMenu = GlobalMenu;
		
		Inventory ToolMenu = Bukkit.createInventory(null, 36, prefix + "Tool");
		ToolMenu.setItem(35, backButton);

		current = 0;
		for(CEnchantment ce : Main.enchantments)
			if(ce.getApplication() == Application.TOOL) {
				tempMeta.setDisplayName(ce.getDisplayName());
				if(Main.hasEconomy && ce.getCost() > 0) {
					tempLore.add(ChatColor.GRAY + "Cost: " + ce.getCost());
					tempMeta.setLore(tempLore);
				}
				tempItem.setItemMeta(tempMeta);
				ToolMenu.setItem(current, tempItem);
				
				tempLore.clear();
				tempMeta.setLore(tempLore);
				
				current++;
			}

		Main.CEToolMenu = ToolMenu;
		
		Inventory BowMenu = Bukkit.createInventory(null, 36, prefix + "Bow");
		BowMenu.setItem(35, backButton);

		current = 0;
		for(CEnchantment ce : Main.enchantments) {

			if(ce.getApplication() == Application.BOW) {
				tempMeta.setDisplayName(ce.getDisplayName());
				if(Main.hasEconomy && ce.getCost() > 0) {
					tempLore.add(ChatColor.GRAY + "Cost: " + ce.getCost());
					tempMeta.setLore(tempLore);
				}
				tempItem.setItemMeta(tempMeta);
				BowMenu.setItem(current, tempItem);
				
				tempLore.clear();
				tempMeta.setLore(tempLore);
				
				current++;
			}
		}

		Main.CEBowMenu = BowMenu;

		Inventory HelmetMenu = Bukkit.createInventory(null, 36, prefix + "Helmet");
		HelmetMenu.setItem(35, backButton);

		current = 0;
		for(CEnchantment ce : Main.enchantments)
			if(ce.getApplication() == Application.HELMET) {
				tempMeta.setDisplayName(ce.getDisplayName());
				if(Main.hasEconomy && ce.getCost() > 0) {
					tempLore.add(ChatColor.GRAY + "Cost: " + ce.getCost());
					tempMeta.setLore(tempLore);
				}
				tempItem.setItemMeta(tempMeta);
				HelmetMenu.setItem(current, tempItem);
				
				tempLore.clear();
				tempMeta.setLore(tempLore);
				
				current++;
			}

		Main.CEHelmetMenu = HelmetMenu;

		Inventory BootsMenu = Bukkit.createInventory(null, 36, prefix + "Boots");
		BootsMenu.setItem(35, backButton);

		current = 0;
		for(CEnchantment ce : Main.enchantments)
			if(ce.getApplication() == Application.BOOTS) {
				tempMeta.setDisplayName(ce.getDisplayName());
				if(Main.hasEconomy && ce.getCost() > 0) {
					tempLore.add(ChatColor.GRAY + "Cost: " + ce.getCost());
					tempMeta.setLore(tempLore);
				}
				tempItem.setItemMeta(tempMeta);
				BootsMenu.setItem(current, tempItem);
				
				tempLore.clear();
				tempMeta.setLore(tempLore);
				
				current++;
			}

		Main.CEBootsMenu = BootsMenu;
		// SPECIFIC MENUS
		


		// ITEM MENU
		Inventory ItemMenu = Bukkit.createInventory(null, 36, prefix + "Items");
		ItemMenu.setItem(35, backButton);

		for(int i = 0; i < Main.items.size(); i++) {
			CItem ci = Main.items.get(i);
			ItemStack newItem = new ItemStack(ci.getMaterial());
			tempMeta.setDisplayName(ci.getDisplayName());
			List<String> temp = ci.getDescription();

			if(Main.hasEconomy && ci.getCost() > 0)
				temp.add(ChatColor.GRAY + "Cost: " + ci.getCost());
			tempMeta.setLore(temp);
			newItem.setItemMeta(tempMeta);
			ItemMenu.setItem(i, newItem);
		}
		
		tempLore.clear();


		Main.CEItemMenu = ItemMenu;
		// ITEM MENU

		// ENCHANTING MENU
		Inventory EnchantingMenu = Bukkit.createInventory(null, 9, prefix + "Enchanting");
		EnchantingMenu.setItem(8, backButton);
		ItemStack EnchantmentInfo = new ItemStack(Material.ENCHANTED_BOOK);

		tempMeta.setDisplayName(ChatColor.MAGIC + "Enchant");
		tempLore.add(ChatColor.GRAY + "Leftclick any item to apply");
		tempLore.add(ChatColor.GRAY + "the enchantment you chose");
		tempMeta.setLore(tempLore);

		EnchantmentInfo.setItemMeta(tempMeta);

		EnchantingMenu.setItem(4, EnchantmentInfo);

		Main.CEEnchantingMenu = EnchantingMenu;
		// ENCHANTING MENU

		// ITEM CREATION MENU
		Inventory ItemApproveMenu = Bukkit.createInventory(null, 9, prefix + "Item Creation");
		EnchantingMenu.setItem(8, backButton);
		ItemStack ItemInfo = new ItemStack(Material.ANVIL);

		tempMeta.setDisplayName(ChatColor.MAGIC + "Item Creation");
		tempLore.add(ChatColor.GRAY + "Click any slot to generate your chosen item");
		tempLore.add(ChatColor.GRAY + "or abort Item Creation using the back-button");
		tempMeta.setLore(tempLore);

		ItemInfo.setItemMeta(tempMeta);

		ItemApproveMenu.setItem(4, ItemInfo);

		Main.CEApproveMenu = ItemApproveMenu;
		// ITEM CREATION MENU

	}

	//CONFIG
	public void convertOldConfig() {
		Main.plugin.getConfig().set("Global.Enchantments.CEnchantmentColor", (Boolean.parseBoolean(Main.config.getString("enchantments.lore.disableItalic")) ? "" : "ITALIC;") + ChatColor.valueOf(Main.config.getString("enchantments.lore.color")));
		Main.plugin.getConfig().set("Global.Enchantments.CEnchantmentTable", Boolean.parseBoolean(Main.config.getString("enchantmentTable")));
		Main.plugin.getConfig().set("Global.Enchantments.CEnchantingProbability", Integer.parseInt(Main.config.getString("enchantmentTableProbability")));
		Main.plugin.getConfig().set("Global.Enchantments.MaximumCustomEnchantments", Integer.parseInt(Main.config.getString("maximumEnchants")));

		Main.plugin.getConfig().set("enchantments.requirePermissions", null);
		Main.plugin.getConfig().set("enchantmentTable", null);
		Main.plugin.getConfig().set("enchantmentTableProbability", null);
		Main.plugin.getConfig().set("commandBypass", null);
		Main.plugin.getConfig().set("AntiMcMMOrepair", null);
		Main.plugin.getConfig().set("restrictEnchantments", null);
		Main.plugin.getConfig().set("maximumEnchants", null);
		Main.plugin.getConfig().set("enchantments", null);
		Main.plugin.getConfig().set("items", null);

		Main.plugin.saveConfig();
		Main.config = Main.plugin.getConfig();
	}

	public void writeConfigEntry(CBasic ce) {
		for(String entry : ce.configEntries) {
			int start = entry.indexOf(": ");
			String path = entry.substring(0, start);
			String actualData = entry.substring(start+2, entry.length());
			String fullPath = (ce.getType() == "Enchantment" ? "Enchantments" : ce.getType()) + "." + ce.getOriginalName() + "." + path;
			if(!Main.plugin.getConfig().contains(fullPath))
				Main.plugin.getConfig().set(fullPath, actualData);
		}
		Main.plugin.saveConfig();
		Main.config = Main.plugin.getConfig();
	}

	//MISC
	public String resolveEnchantmentColor() {
		String color = Main.plugin.getConfig().getString("Global.Enchantments.CEnchantmentColor");
		if(color.contains(";")) {
			String[] temp = color.split(";");
			color = "";
			for(String c : temp)
				try {
					color += ChatColor.valueOf(c.toUpperCase());
				} catch (Exception e) {
					 Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + "[CE] ERROR: The ChatColor '" + c + "' was not found, please check the list of Bukkit ChatColors and update the ChatColor Section. The ChatColor will be ignored to ensure that CE is still working.");
				}
		} else {
			try {
			color = ChatColor.valueOf(Main.config.getString("Global.Enchantments.CEnchantmentColor")) + "";
			} catch (Exception e){
				 Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + "[CE] ERROR: The ChatColor '" + color + "' was not found, please check the list of Bukkit ChatColors and update the ChatColor Section. The ChatColor will be ignored to ensure that CE is still working.");
			}
		}
		return color;
	}

	public void resolveEnchantmentLists() {
		for(CEnchantment ce : Main.enchantments)
			getAppropriateList(ce.getCause()).add(ce);
	}
	
	public static boolean checkWorldGuard(Location l, Player p, StateFlag f) {
    	//TODO: This has to be commented out to allow versions below WorldGuard 6.0 to be compatible 
			//WORLDGUARD 6.0+
				if(Main.getWorldGuard() != null && Main.query != null && !((RegionQuery) Main.query).testState(l, p, f))
					return false;
			//WORLDGUARD Pre-6.0
//				if(Main.getWorldGuard() != null) {
//					ApplicableRegionSet rm = Main.getWorldGuard().getRegionManager(l.getWorld()).getApplicableRegions(l);
//					if(rm != null && !rm.allows(f))
//					return false;
//				}
		return true;
	}
	
	public static boolean checkBuildPermission(Location l, Player p) {
    	//TODO: This has to be commented out to allow versions below WorldGuard 6.0 to be compatible 
			//WORLDGUARD 6.0+
				if(Main.getWorldGuard() != null && Main.query != null && !((RegionQuery) Main.query).testBuild(l, p, DefaultFlag.BLOCK_BREAK))
					return false;
			//WORLDGUARD Pre-6.0
//				if(Main.getWorldGuard() != null) {
//					ApplicableRegionSet rm = Main.getWorldGuard().getRegionManager(l.getWorld()).getApplicableRegions(l);
//					if(rm != null && !rm.canBuild((LocalPlayer) p))
//						return false;
//				}
	    return true;
	}

	private List<CEnchantment> getAppropriateList(Cause c) {

		if(c == Cause.BLOCKBREAK)
			return Main.listener.blockBreakEnchantments;
		else if(c == Cause.BLOCKPLACE)
			return Main.listener.blockPlaceEnchantments;
		else if(c == Cause.BOW)
			return Main.listener.bowEnchantments;
		else if(c == Cause.CLICK)
			return Main.listener.clickEnchantments;
		else if(c == Cause.DAMAGEGIVEN || c == Cause.BOW)
			return Main.listener.damageGivenEnchantments;
		else if(c == Cause.DAMAGETAKEN)
			return Main.listener.damageTakenEnchantments;
		else if(c == Cause.DEATH)
			return Main.listener.deathEnchantments;
		else if(c == Cause.INTERACT)
			return Main.listener.interactEnchantments;
		else if(c == Cause.MOVE)
			return Main.listener.moveEnchantments;

		return null;
	}


	
	

	private List<CEnchantment> getEnchantList(Application app, Player p) {
		List<CEnchantment> list = new ArrayList<CEnchantment>();

		for(CEnchantment ce : Main.enchantments)
			if(ce.getApplication() == app)
				if(p.hasPermission("ce.ench." + ce.getOriginalName()))
					list.add(ce);

		return list;
	}

	private Application getApplicationByMaterial(Material material) {
		
		String mat = material.toString();
		
		if(mat.equals(Material.BOW.toString()))
			return Application.BOW;
		else if(mat.endsWith("BOOTS"))
			return Application.BOOTS;
		else if(mat.endsWith("HELMET"))
			return Application.HELMET;
		else if(mat.endsWith("BOOTS") || mat.endsWith("LEGGINGS") || mat.endsWith("CHESTPLATE") || mat.endsWith("HELMET"))
			return Application.ARMOR;
		else if(mat.endsWith("PICKAXE") || mat.endsWith("SPADE") || mat.endsWith("AXE") || mat.endsWith("HOE"))
			return Application.TOOL;
		return Application.GLOBAL;
	}
	
	private Boolean checkForEnchantment(String toTest, CEnchantment ce) {
		String next = "";
		if(toTest.startsWith(Main.lorePrefix + ce.getOriginalName()))
			next = Main.lorePrefix + ce.getOriginalName();
		if(toTest.startsWith(ce.getDisplayName()))
			next = ce.getDisplayName();
		if(next.isEmpty())
			return false;
		String nextTest = toTest.replace(next, "");
		
		if(nextTest.startsWith(" ") || nextTest.isEmpty())
			return true;
		
		return false;
	}

	public void handleEvent(Player toCheck, Event e, List<CEnchantment> list) {

		long time = System.currentTimeMillis();
		
			for(ItemStack i : toCheck.getInventory().getArmorContents())
				if(i.getType() != Material.AIR) 
				  handleEventMain(toCheck, i, e, list);
			handleEventMain(toCheck, toCheck.getItemInHand(), e, list);

		if(Boolean.parseBoolean(Main.config.getString("Global.Logging.Enabled")) && Boolean.parseBoolean(Main.config.getString("Global.Logging.LogEvents"))) {
			long timeF = (System.currentTimeMillis() - time);
			if(timeF > Integer.parseInt(Main.config.getString("Global.Logging.MinimumMsForLog")))
				Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[CE] Event " + e.getEventName() + " took " + timeF + "ms to process CE-Events.");
		 }
	}
	
	public void handleEnchanting(EnchantItemEvent e, Random r) {

		Player p = e.getEnchanter();
		ItemStack i = e.getItem();
		
		if(i.getType().equals(Material.BOOK))
			return;

		int numberOfEnchantments = r.nextInt(4) + 1;
		List<String> lore = new ArrayList<String>();
				
		while(numberOfEnchantments != 0) {
		for(CEnchantment ce : getEnchantList(getApplicationByMaterial(i.getType()), p))
			if(numberOfEnchantments == 0)
				break;
			else if(r.nextInt(100) < ce.getEnchantProbability()) {
					ItemMeta im = i.getItemMeta();
					if(im.hasLore()) {
						lore = im.getLore();
						for(String s : lore)
							if(s.startsWith(ce.getDisplayName()))
								continue;
					}
					lore.add(ce.getDisplayName() + " " + intToLevel(r.nextInt(ce.getEnchantmentMaxLevel()-1)+1));
					im.setLore(lore);
					i.setItemMeta(im);
					lore.clear();
					numberOfEnchantments--;
				}
		}

		p.getWorld().playSound(p.getLocation(), Sound.AMBIENCE_THUNDER, 1f, 1f);

	}

	public void handleMines(Player toCheck, PlayerMoveEvent e) {

		Block b = toCheck.getLocation().getBlock();

		if(b.hasMetadata("ce.mine") || b.hasMetadata("ce.mine.secondary")) {

			String locString = b.getX() + " " + b.getY() + " " + b.getZ();

			if(b.hasMetadata("ce.mine.secondary")) {
				locString = b.getMetadata("ce.mine.secondary").get(0).asString();
				String[] s = locString.split(" ");
				b = new Location(toCheck.getWorld(), Integer.parseInt(s[0]), Integer.parseInt(s[1]), Integer.parseInt(s[2])).getBlock();
			}
			
			if(b.getType().equals(Material.AIR)) {
				b.removeMetadata("ce.mine", Main.plugin);
				Block[] blocks = {
						b.getRelative(0,1,0),
						b.getRelative(1,0,0),
						b.getRelative(-1,0,0),
						b.getRelative(0,0,1),
						b.getRelative(0,0,-1)
						};
				
				for(Block block : blocks) {
					if(block.hasMetadata("ce.mine.secondary")) {
						String[] s = block.getMetadata("ce.mine.secondary").get(0).asString().split(" ");
						Location loc = new Location(e.getPlayer().getWorld(), Integer.parseInt(s[0]), Integer.parseInt(s[1]), Integer.parseInt(s[2]));
						Location blockLoc = b.getLocation();
						if(loc.getBlockX() == blockLoc.getBlockX() && loc.getBlockY() == blockLoc.getBlockY() && loc.getBlockZ() == blockLoc.getBlockZ())
							block.removeMetadata("ce.mine.secondary", Main.plugin);
					}
				}
			}
			toCheck.setMetadata("ce.mine", new FixedMetadataValue(Main.plugin, locString));
			if(b.hasMetadata("ce.mine"))
				getItemByOriginalname(b.getMetadata("ce.mine").get(0).asString()).effect(e, toCheck);
		}

	}

	public void handleBows(Player toCheck, EntityDamageByEntityEvent e) {
		getItemByOriginalname(e.getDamager().getMetadata("ce.bow").get(0).asString()).effect(e, toCheck);
	}

	private void handleEventMain(Player toCheck, ItemStack i, Event e, List<CEnchantment> list) {
		if(i.hasItemMeta()) {
			ItemMeta im = i.getItemMeta();
			if(!list.isEmpty())
			 if(im.hasLore())
				for(String s : im.getLore())
					if(s.length() > 3) {
						for(CEnchantment ce : list)
							if(isApplicable(i, ce)) {
								if(checkForEnchantment(s, ce)) {
									int level = getLevel(s);
									if(!Boolean.parseBoolean(Main.config.getString("Global.Enchantments.RequirePermissions")) || toCheck.hasPermission("ce.ench." + ce.getOriginalName()))
										if(!toCheck.hasMetadata("ce." + ce.getOriginalName() + ".lock")) 
										if(!ce.getHasCooldown(toCheck))
											try {
												long time = System.currentTimeMillis();
												if(random.nextInt(100) < ce.getOccurrenceChance())
													ce.effect(e, i, level);
												if(Boolean.parseBoolean(Main.config.getString("Global.Logging.Enabled")) && Boolean.parseBoolean(Main.config.getString("Global.Logging.LogEnchantments"))) {
													long timeF = (System.currentTimeMillis() - time);
													if(timeF > Integer.parseInt(Main.config.getString("Global.Logging.MinimumMsForLog")))
														Bukkit.getConsoleSender().sendMessage("[CE] Event " + e.getEventName() + " took " + timeF + "ms to process " + ce.getDisplayName() + " (" + ce.getOriginalName() + ")" + ChatColor.RESET + ".");
												}
											} catch (Exception ex) {
												if(!(ex instanceof ClassCastException))
													Bukkit.getConsoleSender().sendMessage(ex.getMessage());
											}
								}
							}
					}
			if(im.hasDisplayName())
				for(CItem ci : Main.items)
					if(im.getDisplayName().equals(ci.getDisplayName())) {
						if(!Boolean.parseBoolean(Main.config.getString("Global.Enchantments.RequirePermissions")) || toCheck.hasPermission("ce.item." + ci.getOriginalName()))
							if(!ci.getHasCooldown(toCheck))
								if(!toCheck.hasMetadata("ce." + ci.getOriginalName() + ".lock")) {
									if(e instanceof PlayerMoveEvent && (ci.getOriginalName().equals("Landmine") || ci.getOriginalName().equals("Bear Trap") || ci.getOriginalName().equals("Piranha Trap") || ci.getOriginalName().equals("Poison Ivy") || ci.getOriginalName().equals("Prickly Block")))
										return;
									try {
										long time = System.currentTimeMillis();
										if(ci.effect(e, toCheck))
											ci.generateCooldown(toCheck, ci.cooldownTime);
										if(Boolean.parseBoolean(Main.config.getString("Global.Logging.Enabled")) && Boolean.parseBoolean(Main.config.getString("Global.Logging.LogItems"))) {
											long timeF = (System.currentTimeMillis() - time);
											if(timeF > Integer.parseInt(Main.config.getString("Global.Logging.MinimumMsForLog")))
												Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[CE] Event " + e.getEventName() + " took " + timeF + "ms to process " + ci.getDisplayName() + " (" + ci.getOriginalName() + ")" + ChatColor.RESET + ".");
										}
									} catch (Exception ex) {
										if(!(ex instanceof ClassCastException))
											Bukkit.getConsoleSender().sendMessage(ex.getMessage());
									}
								}
						return; // Stops going through the list of items as it
								// is not needed anymore
					}
		}
	}
	
	private boolean isApplicable(ItemStack i, CEnchantment ce) {
		if( (ce.getApplication() == Application.ARMOR 			&& 
				(i.getType().toString().endsWith("HELMET") 		|| 
				 i.getType().toString().endsWith("CHESTPLATE")	|| 
				 i.getType().toString().endsWith("LEGGINGS") 	|| 
				 i.getType().toString().endsWith("BOOTS"))) 	
				 				 								|| 
			(ce.getApplication() == Application.TOOL 			&& 
				(i.getType().toString().endsWith("PICKAXE")		||
				 i.getType().toString().endsWith("SPADE")		|| 
				 i.getType().toString().endsWith("AXE") 		|| 
				 i.getType().toString().endsWith("HOE"))) 	
				 												|| 
			(ce.getApplication() == Application.HELMET 			&& 		i.getType().toString().endsWith("HELMET")) 	
																|| 
			(ce.getApplication() == Application.BOOTS 			&& 		i.getType().toString().endsWith("BOOTS")) 
																|| 
			(ce.getApplication() == Application.BOW 			&& 		i.getType().equals(Material.BOW)) 
																|| 
			ce.getApplication() == Application.GLOBAL)
			return true;
		return false;
	}

	// General stuff

	public int Positive(int i) {
		if(i < 0) {
			int b = i * (-1);
			return b;
		}
		return i;
	}

	public void repeatPotionEffect(final ItemStack i, final Player p, final PotionEffectType type, final int strength, final CItem ci) {
		if(p.hasPotionEffect(type))
			return;
		int slot = 0;
		for(int x = 0; x < p.getInventory().getSize(); x++)
			if(i.equals(p.getInventory().getItem(x)))
				slot = x;
		final int lSlot = slot;
		new BukkitRunnable() {

			@Override
			public void run() {
				ItemStack item = p.getInventory().getItem(lSlot);
				if(p != null && !p.isDead() && item != null && item.hasItemMeta() && item.getItemMeta().equals(i.getItemMeta()))
					p.addPotionEffect(new PotionEffect(type, repeatDelay+100, strength, true), true);
				else {
					this.cancel();
				}
			}
			
		}.runTaskTimer(Main.plugin, 0l, repeatDelay);
	}
	
	public void repeatPotionEffect(final ItemStack i, final Player p, final PotionEffectType type, final int strength, final boolean lock, final CEnchantment ce) {
		int slot = -1;
		boolean isArmor = false;
		
		ItemStack[] list = p.getInventory().getContents();
		for(int x = 0; x < list.length; x++)
			if(i.equals(list[x]))
				slot = x;
		
		if(slot == -1) {
			isArmor = true;
			ItemStack[] aList = p.getInventory().getArmorContents();
			for(int x = 0; x < aList.length; x++)
				if(i.equals(aList[x]))
					slot = x;
		}
		
		final int lSlot = slot;
		final boolean lIsArmor = isArmor;

		if(lock)
			p.setMetadata("ce." + ce.getOriginalName() + ".lock", new FixedMetadataValue(Main.plugin, null));
		new BukkitRunnable() {

			@Override
			public void run() {
				ItemStack item = p.getInventory().getItem(lSlot);
				if(lIsArmor)
					item = p.getInventory().getArmorContents()[lSlot];
				if(p != null && !p.isDead() && item != null && item.hasItemMeta() && item.getItemMeta().equals(i.getItemMeta()))
					p.addPotionEffect(new PotionEffect(type, repeatDelay+100, strength, true), true);
				else {
					if(lock)
						p.removeMetadata("ce." + ce.getOriginalName() + ".lock", Main.plugin);
					this.cancel();
				}
			}
			
		}.runTaskTimer(Main.plugin, 0l, repeatDelay);
	}
	
	public void applyBleed(final Player target, final int bleedDuration) {
		target.sendMessage(ChatColor.RED + "You are Bleeding!");
		target.setMetadata("ce.bleed", new FixedMetadataValue(Main.plugin, null));
		new BukkitRunnable() {

			int	seconds	= bleedDuration;

			@Override
			public void run() {
				if(seconds >= 0) {
					if(!target.isDead() && target.hasMetadata("ce.bleed")) {
						target.damage(1 + (((Damageable)target).getHealth() / 15));
						seconds--;
					} else {
						target.removeMetadata("ce.bleed", Main.plugin);
						this.cancel();
					}
				} else {
					target.removeMetadata("ce.bleed", Main.plugin);
					target.sendMessage(ChatColor.GREEN + "You have stopped Bleeding!");
					this.cancel();
				}
			}
		}.runTaskTimer(Main.plugin, 0l, 20l);

	}

	// Firework

	private Color fireworkColor(int i) {
		switch (i) {
			default:
			case 1:
				return Color.SILVER;
			case 2:
				return Color.AQUA;
			case 3:
				return Color.BLACK;
			case 4:
				return Color.BLUE;
			case 5:
				return Color.FUCHSIA;
			case 6:
				return Color.GRAY;
			case 7:
				return Color.GREEN;
			case 8:
				return Color.LIME;
			case 9:
				return Color.MAROON;
			case 10:
				return Color.NAVY;
			case 11:
				return Color.OLIVE;
			case 12:
				return Color.ORANGE;
			case 13:
				return Color.PURPLE;
			case 14:
				return Color.RED;
			case 15:
				return Color.YELLOW;
			case 16:
				return Color.TEAL;

		}
	}

	public Firework shootFirework(Location loc, Random rand) {
		int type = rand.nextInt(5) + 1;
		Firework firework = loc.getWorld().spawn(loc, Firework.class);
		FireworkMeta meta = firework.getFireworkMeta();
		Type ft = null;
		switch (type) {
			case 1:
				ft = Type.BALL;
				break;
			case 2:
				ft = Type.BALL_LARGE;
				break;
			case 3:
				ft = Type.BURST;
				break;
			case 4:
				ft = Type.CREEPER;
				break;
			case 5:
				ft = Type.STAR;
				break;
		}
		FireworkEffect effect = FireworkEffect.builder().flicker(rand.nextBoolean()).withColor(fireworkColor(rand.nextInt(16) + 1)).withFade(fireworkColor(rand.nextInt(16) + 1)).trail(rand.nextBoolean()).with(ft).trail(rand.nextBoolean()).build();
		meta.addEffect(effect);
		firework.setFireworkMeta(meta);
		return firework;
	}

	// Position

	private String getPlayerDirection(Location loc) {
		double rotation = (loc.getYaw() - 90) % 360;
		if(rotation < 0) {
			rotation += 360.0;
		}
		if(0 <= rotation && rotation < 22.5) {
			return "W";
		} else if(22.5 <= rotation && rotation < 67.5) {
			return "NW";
		} else if(67.5 <= rotation && rotation < 112.5) {
			return "N";
		} else if(112.5 <= rotation && rotation < 157.5) {
			return "NE";
		} else if(157.5 <= rotation && rotation < 202.5) {
			return "E"; // E
		} else if(202.5 <= rotation && rotation < 247.5) {
			return "SE";
		} else if(247.5 <= rotation && rotation < 292.5) {
			return "S";
		} else if(292.5 <= rotation && rotation < 337.5) {
			return "SW";
		} else if(337.5 <= rotation && rotation < 360.0) {
			return "W";
		} else {
			return null;
		}
	}

	public List<Location> getLinePlayer(Player player, int length) {
		List<Location> list = new ArrayList<Location>();
		for(int amount = length; amount > 0; amount--) {
			list.add(player.getTargetBlock(null, amount).getLocation());
		}
		return list;
	}

	public List<Location> getCone(Location loc) {
		List<Location> locs = new ArrayList<Location>();
		String direction = getPlayerDirection(loc);

		Location loc1 = loc.clone();
		Location loc2 = loc.clone();
		Location loc3 = loc.clone();
		if(direction.equals("N")) {
			loc1.setZ(loc.getZ() - 1);
			locs.add(loc1);
			loc2.setZ(loc.getZ() - 2);
			locs.add(loc2);
			loc3.setZ(loc.getZ() - 3);
			locs.add(loc3);
			Location loc4 = loc2.clone();
			Location loc5 = loc2.clone();
			Location loc6 = loc3.clone();
			Location loc7 = loc3.clone();
			Location loc8 = loc3.clone();
			Location loc9 = loc3.clone();
			loc4.setX(loc2.getX() - 1);
			locs.add(loc4);
			loc5.setX(loc2.getX() + 1);
			locs.add(loc5);
			loc6.setX(loc3.getX() + 2);
			locs.add(loc6);
			loc7.setX(loc3.getX() + 1);
			locs.add(loc7);
			loc8.setX(loc3.getX() - 1);
			locs.add(loc8);
			loc9.setX(loc3.getX() - 2);
			locs.add(loc9);
		} else if(direction.equals("S")) {
			loc1.setZ(loc.getZ() + 1);
			locs.add(loc1);
			loc2.setZ(loc.getZ() + 2);
			locs.add(loc2);
			loc3.setZ(loc.getZ() + 3);
			locs.add(loc3);
			Location loc4 = loc2.clone();
			Location loc5 = loc2.clone();
			Location loc6 = loc3.clone();
			Location loc7 = loc3.clone();
			Location loc8 = loc3.clone();
			Location loc9 = loc3.clone();
			loc4.setX(loc2.getX() - 1);
			locs.add(loc4);
			loc5.setX(loc2.getX() + 1);
			locs.add(loc5);
			loc6.setX(loc3.getX() + 2);
			locs.add(loc6);
			loc7.setX(loc3.getX() + 1);
			locs.add(loc7);
			loc8.setX(loc3.getX() - 1);
			locs.add(loc8);
			loc9.setX(loc3.getX() - 2);
			locs.add(loc9);
		} else if(direction.equals("E")) {
			loc1.setX(loc.getX() + 1);
			locs.add(loc1);
			loc2.setX(loc1.getX() + 1);
			locs.add(loc2);
			loc3.setX(loc2.getX() + 1);
			locs.add(loc3);
			Location loc4 = loc2.clone();
			Location loc5 = loc2.clone();
			Location loc6 = loc3.clone();
			Location loc7 = loc3.clone();
			Location loc8 = loc3.clone();
			Location loc9 = loc3.clone();
			loc4.setZ(loc2.getZ() - 1);
			locs.add(loc4);
			loc5.setZ(loc2.getZ() + 1);
			locs.add(loc5);
			loc6.setZ(loc3.getZ() + 2);
			locs.add(loc6);
			loc7.setZ(loc3.getZ() + 1);
			locs.add(loc7);
			loc8.setZ(loc3.getZ() - 1);
			locs.add(loc8);
			loc9.setZ(loc3.getZ() - 2);
			locs.add(loc9);
		} else if(direction.equals("W")) {
			loc1.setX(loc.getX() - 1);
			locs.add(loc1);
			loc2.setX(loc1.getX() - 1);
			locs.add(loc2);
			loc3.setX(loc2.getX() - 1);
			locs.add(loc3);
			Location loc4 = loc2.clone();
			Location loc5 = loc2.clone();
			Location loc6 = loc3.clone();
			Location loc7 = loc3.clone();
			Location loc8 = loc3.clone();
			Location loc9 = loc3.clone();
			loc4.setZ(loc2.getZ() - 1);
			locs.add(loc4);
			loc5.setZ(loc2.getZ() + 1);
			locs.add(loc5);
			loc6.setZ(loc3.getZ() + 2);
			locs.add(loc6);
			loc7.setZ(loc3.getZ() + 1);
			locs.add(loc7);
			loc8.setZ(loc3.getZ() - 1);
			locs.add(loc8);
			loc9.setZ(loc3.getZ() - 2);
			locs.add(loc9);
		} else if(direction.equals("NW")) {
			loc1.setZ(loc.getZ() - 1);
			loc1.setX(loc.getX() - 1);
			locs.add(loc1);
			loc2.setZ(loc.getZ() - 2);
			loc2.setX(loc.getX() - 2);
			locs.add(loc2);
			loc3 = loc1.clone();
			loc3.setZ(loc1.getZ() - 1);
			locs.add(loc3);
			Location loc4 = loc1.clone();
			Location loc5 = loc1.clone();
			Location loc6 = loc1.clone();
			loc4.setZ(loc1.getZ() - 2);
			locs.add(loc4);
			loc5.setX(loc1.getX() - 1);
			locs.add(loc5);
			loc6.setX(loc1.getX() - 2);
			locs.add(loc6);
		} else if(direction.equals("NE")) {
			loc1.setZ(loc.getZ() - 1);
			loc1.setX(loc.getX() + 1);
			locs.add(loc1);
			loc2.setZ(loc.getZ() - 2);
			loc2.setX(loc.getX() + 2);
			locs.add(loc2);
			loc3 = loc1.clone();
			loc3.setZ(loc1.getZ() - 1);
			locs.add(loc3);
			Location loc4 = loc1.clone();
			Location loc5 = loc1.clone();
			Location loc6 = loc1.clone();
			loc4.setZ(loc1.getZ() - 2);
			locs.add(loc4);
			loc5.setX(loc1.getX() + 1);
			locs.add(loc5);
			loc6.setX(loc1.getX() + 2);
			locs.add(loc6);
		} else if(direction.equals("SW")) {
			loc1.setZ(loc.getZ() + 1);
			loc1.setX(loc.getX() - 1);
			locs.add(loc1);
			loc2.setZ(loc.getZ() + 2);
			loc2.setX(loc.getX() - 2);
			locs.add(loc2);
			loc3 = loc1.clone();
			Location loc4 = loc1.clone();
			Location loc5 = loc1.clone();
			Location loc6 = loc1.clone();
			loc3.setZ(loc1.getZ() + 1);
			locs.add(loc3);
			loc4.setZ(loc1.getZ() + 2);
			locs.add(loc4);
			loc5.setX(loc1.getX() - 1);
			locs.add(loc5);
			loc6.setX(loc1.getX() - 2);
			locs.add(loc6);
		} else if(direction.equals("SE")) {
			loc1.setZ(loc.getZ() + 1);
			loc1.setX(loc.getX() + 1);
			locs.add(loc1);
			loc2.setZ(loc.getZ() + 2);
			loc2.setX(loc.getX() + 2);
			locs.add(loc2);
			loc3 = loc1.clone();
			loc3.setZ(loc1.getZ() + 1);
			locs.add(loc3);
			Location loc4 = loc1.clone();
			Location loc5 = loc1.clone();
			Location loc6 = loc1.clone();
			loc4.setZ(loc1.getZ() + 2);
			locs.add(loc4);
			loc5.setX(loc1.getX() + 1);
			locs.add(loc5);
			loc6.setX(loc1.getX() + 2);
			locs.add(loc6);
		}

		return locs;
	}

	// Enchantments

	/* 
	 * This returns the enchantment level of the CE 'checkEnchant'.
	 */
	public int getLevel(String checkEnchant) {
		int level = 1;
		if(checkEnchant.contains(" ")) {
			String[] splitName = checkEnchant.split(" ");
			String possibleLevel = splitName[splitName.length - 1];
			level = levelToInt(possibleLevel);
		}
		return level;
	}

	public String intToLevel(int i) {
		String level = "";

		if(i == 1)
			level = "I";
		else if(i == 2)
			level = "II";
		else if(i == 3)
			level = "III";
		else if(i == 4)
			level = "IV";
		else if(i == 5)
			level = "V";
		else if(i == 6)
			level = "VI";
		else if(i == 7)
			level = "VII";
		else if(i == 8)
			level = "VIII";
		else if(i == 9)
			level = "IX";
		else if(i == 10)
			level = "X";

		return level;
	}
	
	public int levelToInt(String level) {
		int levelI = 1;

		if(level.equals("II"))
			levelI = 2;
		else if(level.equals("III"))
			levelI = 3;
		else if(level.equals("IV"))
			levelI = 4;
		else if(level.equals("V"))
			levelI = 5;
		else if(level.equals("VI"))
			levelI = 6;
		else if(level.equals("VII"))
			levelI = 7;
		else if(level.equals("VIII"))
			levelI = 8;
		else if(level.equals("IX"))
			levelI = 9;
		else if(level.equals("X"))
			levelI = 10;

		return levelI;
	}

}
