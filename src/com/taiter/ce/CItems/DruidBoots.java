package com.taiter.ce.CItems;

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


import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;


public class DruidBoots extends CItem {

	int SpeedDuration;
	int SpeedLevel;
	int RegenerationDuration;
	int RegenerationLevel;
	
	public DruidBoots(String originalName, ChatColor color, String lDescription, long lCooldown, Material mat) {
		super(originalName, color, lDescription, lCooldown, mat);
		this.configEntries.put("SpeedDuration", 100);
		this.configEntries.put("SpeedLevel", 5);
		this.configEntries.put("RegenerationDuration", 100);
		this.configEntries.put("RegenerationLevel", 5);
		triggers.add(Trigger.MOVE);
	}

	@Override
	public boolean effect(Event event, final Player player) {
		
		Material t = player.getLocation().getBlock().getRelative(0,-1,0).getLocation().getBlock().getType();
		if(t.equals(Material.GRASS) || t.equals(Material.DIRT) || t.toString().contains("LEAVES")) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, SpeedDuration , SpeedLevel), true);
			player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, RegenerationDuration , RegenerationLevel), true);
		}
			
		return true;
	}

	@Override
	public void initConfigEntries() {
		SpeedDuration = Integer.parseInt(getConfig().getString("Items." + getOriginalName() + ".SpeedDuration"));
		SpeedLevel = Integer.parseInt(getConfig().getString("Items." + getOriginalName() + ".SpeedLevel")) - 1;
		RegenerationDuration = Integer.parseInt(getConfig().getString("Items." + getOriginalName() + ".RegenerationDuration"));
		RegenerationLevel = Integer.parseInt(getConfig().getString("Items." + getOriginalName() + ".RegenerationLevel"))-1;
	}

}
