package com.taiter.ce;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.taiter.ce.CItems.CItem;
import com.taiter.ce.Enchantments.CEnchantment;


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

public class CEventHandler {

	
	public static void handleArmor(Player toCheck, ItemStack toAdd, Boolean remove, Event event) {
		if(toAdd != null && toAdd.getType() != Material.AIR && toAdd.hasItemMeta() && toAdd.getItemMeta().hasLore())
				for(String s : toAdd.getItemMeta().getLore())
					for(CBasic c : Main.listener.wearItem) {
						if(c instanceof CEnchantment)
						if(Tools.checkForEnchantment(s, (CEnchantment) c)) {
							int level = Tools.getLevel(s);
							HashMap<PotionEffectType, Integer> potioneffects = c.getPotionEffectsOnWear();
							if(potioneffects.size() < 1)
								return;
							if(remove) {
								for(PotionEffectType pt : potioneffects.keySet())
								if(toCheck.hasPotionEffect(pt))
									toCheck.removePotionEffect(pt);
							} else
								for(PotionEffectType pt : potioneffects.keySet())
									toCheck.addPotionEffect(new PotionEffect(pt, 600000, potioneffects.get(pt) + level - 2), true);
						}
				}
	}
	
	
	public static void handleEvent(Player toCheck, Event e, HashSet<CBasic> list) {

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
	
	public static void handleEnchanting(EnchantItemEvent e) {

		Player p = e.getEnchanter();
		ItemStack i = e.getItem();
		
		if(i == null)
			return;
		
		if(i.getType().equals(Material.BOOK))
			return;
		
		List<CEnchantment> list = Tools.getEnchantList(Tools.getApplicationByMaterial(i.getType()), p);
		
		if(list.isEmpty())
			return;
		
		ItemMeta im = i.getItemMeta();
		List<String> lore = new ArrayList<String>();
		
		if(im.hasLore())
			lore = im.getLore();
		
		int numberOfEnchantments = Tools.random.nextInt(4) + 1;
		int maxTries = 10;
		int appliedEnchantments = 0;
		
		if(Main.maxEnchants < numberOfEnchantments)
			numberOfEnchantments = Main.maxEnchants;
		
		if(list.size() < numberOfEnchantments)
			numberOfEnchantments = list.size();
				
		while(numberOfEnchantments > 0 && maxTries >= 0) {
			for(CEnchantment ce : list) {
				if(numberOfEnchantments <= 0)
					break;
				else if(Tools.random.nextInt(100) < ce.getEnchantProbability()) {
					if(!lore.isEmpty()) {
						Boolean hasFound = false;
						for(String s : lore)
							if(s.startsWith(ce.getDisplayName()) || ChatColor.stripColor(s).startsWith(ce.getOriginalName()))
								hasFound = true;
						if(hasFound)
							continue;
					}
					
					int level = ce.getEnchantmentMaxLevel()-1;
					if(level > 0)
						level = Tools.random.nextInt(ce.getEnchantmentMaxLevel()-1)+1;
					else
						level = 1;
					lore.add(ce.getDisplayName() + " " + Tools.intToLevel(level));
					appliedEnchantments++;
					numberOfEnchantments--;
					
				}
			}
			maxTries--;
		}
		
		if(appliedEnchantments == 0)
			return;
		
		im.setLore(lore);
		i.setItemMeta(im);

		p.getWorld().playSound(p.getLocation(), Sound.AMBIENCE_THUNDER, 1f, 1f);

	}

	public static void handleMines(Player toCheck, PlayerMoveEvent e) {

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
				Tools.getItemByOriginalname(b.getMetadata("ce.mine").get(0).asString()).effect(e, toCheck);
		}

	}

	public static void handleBows(Player toCheck, EntityDamageByEntityEvent e) {
		if(e.getDamager().hasMetadata("ce.bow.item")) {
			Tools.getItemByOriginalname(e.getDamager().getMetadata("ce.bow.item").get(0).asString()).effect(e, toCheck);
			e.getDamager().removeMetadata("ce.bow.item", Main.plugin);
		}
		
		if(e.getDamager().hasMetadata("ce.bow.enchantment")) {
			String[] enchantments = e.getDamager().getMetadata("ce.bow.enchantment").get(0).asString().split(" ; ");
			for(String ench : enchantments) {
				String[] enchantment = ench.split(" : ");
				CEnchantment ce = Tools.getEnchantmentByOriginalname(enchantment[0]);
				if(Tools.random.nextDouble()*100 < ce.getOccurrenceChance())
					ce.effect(e, toCheck.getItemInHand(), Integer.parseInt(enchantment[1]));
			}
			e.getDamager().removeMetadata("ce.bow.enchantment", Main.plugin);
		}
	}
	

	public static void handleEventMain(Player toCheck, ItemStack i, Event e, HashSet<CBasic> list) {
		if(i.hasItemMeta()) {
		ItemMeta im = i.getItemMeta();
		if(!list.isEmpty()) {
			
			Boolean checkLore = im.hasLore();
			Boolean checkName = im.hasDisplayName();
						
			List<String> lore = im.getLore();
			String name = im.getDisplayName();
			
			for(CBasic cb : list) {
		      if(checkLore)
		        if(cb instanceof CEnchantment) {
		        CEnchantment ce = (CEnchantment) cb;

		        for(String s : lore)
		         if(Tools.isApplicable(i, ce)) {

		          if(Tools.checkForEnchantment(s, ce)) {
		        	  		        
		          int level = Tools.getLevel(s);

				if(!Boolean.parseBoolean(Main.config.getString("Global.Enchantments.RequirePermissions")) || Tools.checkPermission(ce, toCheck))
				if(!ce.lockList.contains(toCheck)) 
				if(!ce.getHasCooldown(toCheck))
				 try {
				  long time = System.currentTimeMillis();
				  if(Tools.random.nextInt(100) < ce.getOccurrenceChance()) {
					  //BOWS
					  	if(e instanceof EntityShootBowEvent) {
						  String enchantments = ce.getOriginalName() + " : " + level;
						  if(((EntityShootBowEvent) e).getProjectile().hasMetadata("ce.bow.enchantment"))
							  enchantments += " ; " + ((EntityShootBowEvent) e).getProjectile().getMetadata("ce.bow.enchantment").get(0).asString();
						  ((EntityShootBowEvent) e).getProjectile().setMetadata("ce.bow.enchantment", new FixedMetadataValue(Main.plugin, enchantments));
					  	}
					  //BOWS
					  
					 ce.effect(e, i, level);
				  }
				  if(Boolean.parseBoolean(Main.config.getString("Global.Logging.Enabled")) && Boolean.parseBoolean(Main.config.getString("Global.Logging.LogEnchantments"))) {
					long timeF = (System.currentTimeMillis() - time);
					if(timeF > Integer.parseInt(Main.config.getString("Global.Logging.MinimumMsForLog")))
					  Bukkit.getConsoleSender().sendMessage("[CE] Event " + e.getEventName() + " took " + timeF + "ms to process " + ce.getDisplayName() + ChatColor.RESET +  "(" + ce.getOriginalName() + ").");
					}
				 } catch (Exception ex) {
					if(!(ex instanceof ClassCastException))
						for(StackTraceElement element : ex.getStackTrace()) {
						 String className = element.getClassName();
						 if(className.contains("com.taiter.ce")) {
						  Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[CE] An error occurred in " + element.getFileName() + " on line " + element.getLineNumber() + ": " + ex.getCause());
						  break;
						 }
						}
				 }
				}
				} 
			   }
			if(checkName)
				if(cb instanceof CItem) {
				CItem ci = (CItem) cb;
				if(name.equals(ci.getDisplayName())) {
				if(!Boolean.parseBoolean(Main.config.getString("Global.Enchantments.RequirePermissions")) || Tools.checkPermission(ci, toCheck))
				if(!ci.getHasCooldown(toCheck))
				if(!ci.lockList.contains(toCheck)) {
				 if(e instanceof PlayerMoveEvent && (ci.getOriginalName().equals("Landmine") || ci.getOriginalName().equals("Bear Trap") || ci.getOriginalName().equals("Piranha Trap") || ci.getOriginalName().equals("Poison Ivy") || ci.getOriginalName().equals("Prickly Block")))
					return;
				 try {
					if(e instanceof EntityShootBowEvent) 
					  ((EntityShootBowEvent) e).getProjectile().setMetadata("ce.bow.item", new FixedMetadataValue(Main.plugin, ci.getOriginalName()));
					if(e instanceof ProjectileLaunchEvent) 
						 ((ProjectileLaunchEvent) e).getEntity().setMetadata("ce.projectile.item", new FixedMetadataValue(Main.plugin, ci.getOriginalName()));
					long time = System.currentTimeMillis();

					if(ci.effect(e, toCheck))
					  ci.generateCooldown(toCheck, ci.getCooldown());
					if(Boolean.parseBoolean(Main.config.getString("Global.Logging.Enabled")) && Boolean.parseBoolean(Main.config.getString("Global.Logging.LogItems"))) {
					  long timeF = (System.currentTimeMillis() - time);
					  if(timeF > Integer.parseInt(Main.config.getString("Global.Logging.MinimumMsForLog")))
						Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[CE] Event " + e.getEventName() + " took " + timeF + "ms to process " + ci.getDisplayName() + " (" + ci.getOriginalName() + ")" + ChatColor.RESET + ".");
					}
				 } catch (Exception ex) {
					if(!(ex instanceof ClassCastException))
						for(StackTraceElement element : ex.getStackTrace()) {
						 String className = element.getClassName();
						 if(className.contains("com.taiter.ce")) {
						  Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[CE] An error occurred in " + element.getFileName() + " on line " + element.getLineNumber() + ": " + ex.getCause());
						  break;
						 }
						}
					}
				}
				return; // Stops going through the list of items as it
						// is not needed anymore
				}
			}
			}
			}
		}
	}
	
}
