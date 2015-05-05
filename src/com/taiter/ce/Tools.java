package com.taiter.ce;

/*
* This file is part of Custom Enchantments
* Copyright (C) Taiterio 2015
*
* This program is free software: you can redistribute it and/or modify it
* under the terms of the GNU Lesser General Public License as published by the
* Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful, but WITHOUT
* ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
* FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
* for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/



import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.sk89q.worldguard.protection.GlobalRegionManager;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.taiter.ce.CBasic.Trigger;
import com.taiter.ce.CItems.CItem;
import com.taiter.ce.Enchantments.CEnchantment;
import com.taiter.ce.Enchantments.CEnchantment.Application;



public class Tools {

	public static String prefix = "CE - ";
	public static Random random = new Random();
	
	
	//ENCHANTMENTS
	public static boolean isApplicationCorrect(Application app, Material matToApplyTo) {

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

	public static CEnchantment getEnchantmentByDisplayname(String name) {
		for(CEnchantment ce : Main.enchantments) {
			name = ChatColor.stripColor(name).toLowerCase();
			String enchantment = ChatColor.stripColor(ce.getDisplayName()).toLowerCase();
			if(name.equals(enchantment))
					return ce;
		}
		return null;
	}

	public static CEnchantment getEnchantmentByOriginalname(String name) {
		for(CEnchantment ce : Main.enchantments) {
			name = ChatColor.stripColor(name).toLowerCase();
			String enchantment = ChatColor.stripColor(ce.getOriginalName()).toLowerCase();
			if(name.equals(enchantment))
					return ce;
		}
		return null;
	}
	
	public static CItem getItemByOriginalname(String name) {
		for(CItem ci : Main.items)
			if(ci.getOriginalName().equals(name))
				return ci;
		return null;
	}
	
	//INVENTORIES
	
	public static CItem getItemByDisplayname(String name) {
		for(CItem ci : Main.items)
			if(ci.getDisplayName().equals(name))
				return ci;
		return null;
	}
	
	public static Boolean checkForEnchantments(List<String> toTest) {
		for(String s : toTest)
			if(checkForEnchantments(s))
				return true;
		return false;
	}
	
	public static Boolean checkForEnchantments(String toTest) {
		for(CEnchantment ce : Main.enchantments) {
		   if(checkForEnchantment(toTest, ce))
			   return true;
		}
		return false;
	}
	
	public static Boolean checkForEnchantments(List<String> toTest, CEnchantment ce) {
		for(String s : toTest)
			if(checkForEnchantment(s, ce))
				return true;
		return false;
	}
	
	
	public static Boolean checkForEnchantment(String toTest, CEnchantment ce) {
		if(toTest.startsWith(ChatColor.YELLOW + "" + ChatColor.ITALIC + "\""))
			toTest = Main.lorePrefix + ChatColor.stripColor(toTest.replace("\"", ""));
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

	public static Inventory getPreviousInventory(String name) {
		if(name.equals(prefix + "Enchantments") || name.equals(prefix + "Items") || name.equals(prefix + "Config"))
			return Main.CEMainMenu;
		else if(name.equals(prefix + "Enchanting") || name.equals(prefix + "Armor") || name.equals(prefix + "Bow") || name.equals(prefix + "Tool") || name.equals(prefix + "Global") || name.equals(prefix + "Helmet") || name.equals(prefix + "Boots"))
			return Main.CEEnchantmentMainMenu;
		else if(name.equals(prefix + "Item Creation"))
			return Main.CEItemMenu;
		return null;
	}

	public static Inventory getNextInventory(String name) {

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

	public static void openCEMenu(Player p) {
		Inventory tempInv = Main.CEMainMenu;
		if(!p.isOp())
			tempInv.remove(6);
		p.openInventory(tempInv);
	}
	
	public static boolean checkPermission(CBasic cb, Player p) {
		String name = "ce.";
		if(cb instanceof CItem)
			name += "item.";
		else
			name += "ench.";
		if(p.hasPermission(name + "*"))
			return true;
		name += cb.getOriginalName();
		if(p.hasPermission(name))
			return true;
		name = name.replace(" ", "");
		if(p.hasPermission(name))
			return true;
		name = name.replace("'", "");
		if(p.hasPermission(name))
			return true;
		return false;
	}

	public static Inventory getEnchantmentMenu(Player p, String name) {
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
						if(!checkPermission(ce, p)) {
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

	public static Inventory getItemMenu(Player p) {
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
						if(!checkPermission(ci, p)) {
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

	public static void generateInventories() {

		ItemStack backButton = new ItemStack(Material.NETHER_STAR);

		ItemMeta tempMeta = backButton.getItemMeta();
		List<String> tempLore = new ArrayList<String>();

		tempMeta.setDisplayName(ChatColor.AQUA + "Back");
		backButton.setItemMeta(tempMeta);

		// All inventories are public static and the owner is null

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

		int currentItemSlot = 0;
		
		for(CItem ci : Main.items) {
			ItemStack newItem = new ItemStack(ci.getMaterial());
			tempMeta.setDisplayName(ci.getDisplayName());
			List<String> temp = ci.getDescription();

			if(Main.hasEconomy && ci.getCost() > 0)
				temp.add(ChatColor.GRAY + "Cost: " + ci.getCost());
			tempMeta.setLore(temp);
			newItem.setItemMeta(tempMeta);
			ItemMenu.setItem(currentItemSlot, newItem);
			currentItemSlot++;
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
		ItemApproveMenu.setItem(8, backButton);
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
	public static void convertOldConfig() {
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

	public static void writeConfigEntry(CBasic ce) {
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
	public static String resolveEnchantmentColor() {
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

	public static void resolveLists() {
		for(CEnchantment ce : Main.enchantments)
			for(Trigger t : ce.getTriggers())
				getAppropriateList(t).add(ce);
		for(CItem ci : Main.items)
			for(Trigger t : ci.getTriggers())
				getAppropriateList(t).add(ci);
	}
	
	public static boolean checkWorldGuard(Location l, Player p, String fs) {
		if(Main.getWorldGuard() != null) {
			GlobalRegionManager grm = Main.getWorldGuard().getGlobalRegionManager();
			
			if(grm == null)
				return true;
			
			StateFlag f = null;
			for(Flag<?> df : DefaultFlag.flagsList)
				if(fs.equalsIgnoreCase(df.getName())) 
					f = (StateFlag) df;
			
			if(f.equals(DefaultFlag.BUILD)) {
				if(!grm.canBuild(p, l))
					return false;
			} else if(!grm.allows(f, l, Main.getWorldGuard().wrapPlayer(p)))
				return false;
		}	
		return true;
	}

	private static HashSet<CBasic> getAppropriateList(Trigger t) {

		if(t == Trigger.BLOCK_BROKEN)
			return Main.listener.blockBroken;
		else if(t == Trigger.BLOCK_PLACED)
			return Main.listener.blockPlaced;
		else if(t == Trigger.INTERACT)
			return Main.listener.interact;
		else if(t == Trigger.INTERACT_ENTITY)
			return Main.listener.interactE;
		else if(t == Trigger.INTERACT_LEFT)
			return Main.listener.interactL;
		else if(t == Trigger.INTERACT_RIGHT)
			return Main.listener.interactR;
		else if(t == Trigger.DEATH)
			return Main.listener.death;
		else if(t == Trigger.DAMAGE_GIVEN)
			return Main.listener.damageGiven;
		else if(t == Trigger.DAMAGE_TAKEN)
			return Main.listener.damageTaken;
		else if(t == Trigger.DAMAGE_NATURE)
			return Main.listener.damageNature;
		else if(t == Trigger.SHOOT_BOW)
			return Main.listener.shootBow;
		else if(t == Trigger.PROJECTILE_HIT)
			return Main.listener.projectileHit;
		else if(t == Trigger.PROJECTILE_THROWN)
			return Main.listener.projectileThrow;
		else if(t == Trigger.EQUIP_ITEM)
			return Main.listener.equipItem;
		else if(t == Trigger.MOVE)
			return Main.listener.move;
		return null;
	}

	

	public static List<CEnchantment> getEnchantList(Application app, Player p) {
		List<CEnchantment> list = new ArrayList<CEnchantment>();
		for(CEnchantment ce : Main.enchantments) 
			if(ce.getApplication() == app) 
					list.add(ce);
		
		return correctList(list, p);
	}
	
	private static List<CEnchantment> correctList(List<CEnchantment> list, Player p) {
	
		int max = list.size() - 1;
		
		while(max >= 0) {
			if(!checkPermission(list.get(max), p))
				list.remove(max);
			max--;
		}
		return list;
	}

	public static Application getApplicationByMaterial(Material material) {
		
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


	
	
	public static boolean isApplicable(ItemStack i, CEnchantment ce) {
		if( (ce.getApplication() == Application.ARMOR			&&
				ce.getApplication() != Application.GLOBAL       &&
				(i.getType().toString().endsWith("HELMET")		|| 
				 i.getType().toString().endsWith("CHESTPLATE")	|| 
				 i.getType().toString().endsWith("LEGGINGS")	|| 
				 i.getType().toString().endsWith("BOOTS")))		
				 												|| 
			(ce.getApplication() == Application.TOOL			&& 
				(i.getType().toString().endsWith("PICKAXE")		||
				 i.getType().toString().endsWith("SPADE")		|| 
				 i.getType().toString().endsWith("_AXE")		|| 
				 i.getType().toString().endsWith("HOE")))
				 												|| 
			(ce.getApplication() == Application.HELMET			&&	i.getType().toString().endsWith("HELMET")) 	
																|| 
			(ce.getApplication() == Application.BOOTS			&&	i.getType().toString().endsWith("BOOTS")) 
																|| 
			(ce.getApplication() == Application.BOW 			&&	i.getType().equals(Material.BOW)) 
																|| 
			ce.getApplication() == Application.GLOBAL)
			return true;
		return false;
	}

	// General stuff

	public static int Positive(int i) {
		if(i < 0) {
			int b = i * (-1);
			return b;
		}
		return i;
	}

	

	// Firework

	private static Color fireworkColor(int i) {
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

	public static Firework shootFirework(Location loc, Random rand) {
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
		FireworkEffect effect =  FireworkEffect.builder().flicker(rand.nextBoolean()).withColor(fireworkColor(rand.nextInt(16) + 1)).withFade(fireworkColor(rand.nextInt(16) + 1)).trail(rand.nextBoolean()).with(ft).trail(rand.nextBoolean()).build();
		meta.addEffect(effect);
		firework.setFireworkMeta(meta);
		return firework;
	}
	
	public static void repeatPotionEffect(final ItemStack i, final Player p, final PotionEffectType type, final int strength, final CItem ci) {
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
				if(p != null && !p.isDead() && item != null && !item.getType().equals(Material.AIR) && item.hasItemMeta() && item.getItemMeta().equals(i.getItemMeta()))
					p.addPotionEffect(new PotionEffect(type, Main.repeatDelay+200, strength, true), true);
				else {
					this.cancel();
				}
			}
			
		}.runTaskTimer(Main.plugin, 0l, Main.repeatDelay);
	}
	
	public static void repeatPotionEffect(final ItemStack i, final Player p, final PotionEffectType type, final int strength, final boolean lock, final CEnchantment ce) {
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
			ce.lockList.add(p);
		new BukkitRunnable() {

			@Override
			public void run() {
				ItemStack item = p.getInventory().getItem(lSlot);
				if(lIsArmor)
					item = p.getInventory().getArmorContents()[lSlot];
				if(p != null && !p.isDead() && item != null && !item.getType().equals(Material.AIR) && item.hasItemMeta() && item.getItemMeta().equals(i.getItemMeta()))
					p.addPotionEffect(new PotionEffect(type, Main.repeatDelay+200, strength, true), true);
				else {
					if(lock)
						ce.lockList.remove(p);
					this.cancel();
				}
			}
			
		}.runTaskTimer(Main.plugin, 0l, Main.repeatDelay);
	}

	public static void applyBleed(final Player target, final int bleedDuration) {
		target.sendMessage(ChatColor.RED + "You are Bleeding!");
		target.setMetadata("ce.bleed", new FixedMetadataValue(Main.plugin, null));
		new BukkitRunnable() {

			int	seconds = bleedDuration;

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
	
	// Position

	private static String getPlayerDirection(Location loc) {
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

	public static List<Location> getLinePlayer(Player player, int length) {
		List<Location> list = new ArrayList<Location>();
		for(int amount = length; amount > 0; amount--) {
			list.add(player.getTargetBlock((HashSet<Material>)null, amount).getLocation());
		}
		return list;
	}

	public static List<Location> getCone(Location loc) {
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
	public static int getLevel(String checkEnchant) {
		int level = 1;
		if(checkEnchant.contains(" ")) {
			String[] splitName = checkEnchant.split(" ");
			String possibleLevel = splitName[splitName.length - 1];
			level = levelToInt(possibleLevel);
		}
		return level;
	}

	public static String intToLevel(int i) {
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
	
	public static int levelToInt(String level) {
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
