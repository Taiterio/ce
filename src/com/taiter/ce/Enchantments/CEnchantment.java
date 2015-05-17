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
	
	static public enum Application {
		ARMOR,
		GLOBAL,
		BOW,
		BOOTS,
		HELMET,
		TOOL
	}
	
	private static int  amountGlobal = -1;
	private static int  amountBow = 0;
	private static int  amountTool = 0;
	private static int  amountArmor = 0;
	private static int  amountHelmet = 0;
	private static int  amountBoots = 0;
	
	Application			app;
	double 				enchantProbability;
	int 				enchantmentMaxLevel;
	int 				occurrenceChance;
	private boolean		hasRetriedConfig;
	public Application 	getApplication()		{   return this.app;				}
	public double		getEnchantProbability()	{	return this.enchantProbability;	}
	public int			getEnchantmentMaxLevel(){	return this.enchantmentMaxLevel;}
	public int 			getOccurrenceChance() 	{	return this.occurrenceChance;	}

	@Override
	public double getCost() { return Double.parseDouble(Main.config.getString("Enchantments." + getOriginalName() + ".Cost"));}
	
	public CEnchantment(Application app) {
		this.typeString = "Enchantment";
		this.app = app;
		
		this.originalName = this.getClass().getSimpleName();
		char[] nameChars = originalName.toCharArray();
		
		for(int i = 3; i < nameChars.length; i++) { //Go through the classname, start at the third char (Minimum enchantment length) and check for an uppercase letter
			if(Character.isUpperCase(nameChars[i]))
				this.originalName = originalName.substring(0, i) + " " + originalName.substring(i, nameChars.length);
		}
		
		this.occurrenceChance = 100; 
		this.configEntries.add("DisplayName: " + originalName);
		this.configEntries.add("EnchantmentMaxLevel: 5");
		this.configEntries.add("OccurrenceChance: 100");
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
	
	private void writeEnchantmentAmounts() {
		amountGlobal = 0;
		for(CEnchantment ce : Main.enchantments)
			if(ce.getApplication() == Application.GLOBAL) {
				amountGlobal++;
				if(ce.triggers.contains(Trigger.SHOOT_BOW))
					amountBow++;
			} else if(ce.getApplication() == Application.BOW)
				amountBow++;
			else if(ce.getApplication() == Application.TOOL)
				amountTool++;
			else if(ce.getApplication() == Application.BOOTS)
				amountBoots++;
			else if(ce.getApplication() == Application.HELMET)
				amountHelmet++;
			else if(ce.getApplication() == Application.ARMOR) {
				amountArmor++;
				amountHelmet++;
				amountBoots++;
			}
	}
	
	private double getEnchantmentProbability() {
		double enchantmentAmount = 0; //Amount of possible custom enchantments that COULD be applied with this enchantments application
		if(this.app == Application.ARMOR) 
			enchantmentAmount =  amountArmor;
		else if(this.app == Application.HELMET) 
			enchantmentAmount =  amountHelmet;
		else if(this.app == Application.BOOTS) 
			enchantmentAmount =  amountBoots;
		else if(this.app == Application.TOOL) 
			enchantmentAmount =  amountTool;
		else if(this.app == Application.BOW) 
			enchantmentAmount =  amountBow;
		else
			enchantmentAmount = amountGlobal;
		enchantmentAmount = 100/enchantmentAmount;
		return enchantmentAmount;
	}
	
	public void finalizeEnchantment() {
			if(amountGlobal < 0)
				writeEnchantmentAmounts();
			double enchantmentProbability = getEnchantmentProbability();
			this.configEntries.add("EnchantmentProbability: " + enchantmentProbability);
		
		if(!getConfig().contains("Enchantments." + getOriginalName()))
			Tools.writeConfigEntries(this);
		try {
			this.displayName         = Main.lorePrefix +Main.config.getString("Enchantments." + getOriginalName() + ".DisplayName");
			if(!Boolean.parseBoolean(getConfig().getString("Global.Enchantments.UseCustomEnchantmentProbability")))
				this.enchantProbability  = enchantmentProbability;
			else
				this.enchantProbability  = Double.parseDouble(Main.config.getString("Enchantments." + getOriginalName() + ".EnchantmentProbability"));
			this.enchantmentMaxLevel = Integer.parseInt(Main.config.getString("Enchantments." + getOriginalName() + ".EnchantmentMaxLevel"));
			this.occurrenceChance    = Integer.parseInt(Main.config.getString("Enchantments." + getOriginalName() + ".OccurrenceChance"));
			for(String entry :this.configEntries) {
				String[] split = entry.split(": ");
				if(split[1].equalsIgnoreCase("true") || split[1].equalsIgnoreCase("false"))
					if(!getConfig().contains("Enchantments." + getOriginalName() + "." + split[0])) {
						Tools.writeConfigEntries(this);
						break;
					}
			}
			initConfigEntries();
		} catch(Exception e) {
			if(!hasRetriedConfig) {
				Tools.writeConfigEntries(this);
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
	
	protected void resetMaxLevel() {
		this.enchantmentMaxLevel = 1;
	}
	
	public abstract void effect(Event event, ItemStack triggerItem, int level);
	public abstract void initConfigEntries();
	
}
