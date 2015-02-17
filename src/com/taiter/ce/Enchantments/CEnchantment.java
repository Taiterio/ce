package com.taiter.ce.Enchantments;

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


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.taiter.ce.CBasic;
import com.taiter.ce.Main;
import com.taiter.ce.Tools;

public abstract class CEnchantment extends CBasic  {

	static public enum Cause {
		MOVE,		//Move event
		CLICK,		//Interact event
		INTERACT,	//Interact WITH ENTITY event
		DAMAGETAKEN,//Damage taken event
		DAMAGEGIVEN,//Damage caused event
		BOW,		//Bow shot event
		DEATH,		//Death event
		BLOCKPLACE,	//Block place event
		BLOCKBREAK	//Block break event
	}
	
	static public enum Application {
		ARMOR,
		GLOBAL,
		BOW,
		BOOTS,
		HELMET,
		TOOL
	}
	
	Cause				cause;
	Application			app;
	int 				enchantProbability;
	int 				enchantmentMaxLevel;
	int 				occurrenceChance;
	private boolean		hasRetriedConfig;
	public Application 	getApplication()		{   return this.app;				}
	public Cause 		getCause() 				{	return this.cause;				}
	public int 			getEnchantProbability()	{	return this.enchantProbability;	}
	public int			getEnchantmentMaxLevel(){	return this.enchantmentMaxLevel;}
	public int 			getOccurrenceChance() 	{	return this.occurrenceChance;	}

	@Override
	public double getCost() { return Double.parseDouble(Main.config.getString("Enchantments." + getOriginalName() + ".Cost"));}
	
	public CEnchantment(String originalName, Application app, Cause cause, int enchantProbability, int occurrenceChance) {
		this.typeString = "Enchantment";
		this.cause = cause;
		this.app = app;
		this.originalName = originalName; 
		this.occurrenceChance = occurrenceChance; 
		this.configEntries.add("DisplayName: " + originalName);
		this.configEntries.add("EnchantmentProbability: " + enchantProbability);
		this.configEntries.add("EnchantmentMaxLevel: 5");
		this.configEntries.add("OccurrenceChance: " + occurrenceChance);
		this.configEntries.add("Cost: 0");
	}
	
	public boolean getHasCooldown(Player p) {
		if(cooldown.contains(p))
			return true;
		return false;
	}
	public void generateCooldown(final Player p, long time) {
		cooldown.add(p);
		new BukkitRunnable() {
			@Override
			public void run() {
				cooldown.remove(p);
			}
		}.runTaskLater(main, time);
	}
	
	
	public void finalizeEnchantment() {
		if(!getConfig().contains("Enchantments." + getOriginalName()))
			Tools.writeConfigEntry(this);
		try {
			this.displayName         = Main.lorePrefix +Main.config.getString("Enchantments." + getOriginalName() + ".DisplayName");
			this.enchantProbability  = Integer.parseInt(Main.config.getString("Enchantments." + getOriginalName() + ".EnchantmentProbability"));
			this.enchantmentMaxLevel = Integer.parseInt(Main.config.getString("Enchantments." + getOriginalName() + ".EnchantmentMaxLevel"));
			this.occurrenceChance    = Integer.parseInt(Main.config.getString("Enchantments." + getOriginalName() + ".OccurrenceChance"));
			initConfigEntries();
		} catch(Exception e) {
			if(!hasRetriedConfig) {
				Tools.writeConfigEntry(this);
				hasRetriedConfig = true;
				finalizeEnchantment();
			} else {
					Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[CE] WARNING: Could not configurate the CE '" + getOriginalName() + "',");
					Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[CE]          please check the config for any errors, the enchantment is now disabled. ");
					Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[CE] 	  Explicit error: " + e.getMessage());
					Main.enchantments.remove(this);
			}
		}
	}
	
	
	public abstract void effect(Event event, ItemStack triggerItem, int level);
	public abstract void initConfigEntries();
	
}
