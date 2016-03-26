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
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.taiter.ce.Tools;


public class LivefireBoots extends CItem {

	int FlameDuration;
	int FireResistanceLevel;
	
	public LivefireBoots(String originalName, ChatColor color, String lDescription, long lCooldown, Material mat) {
		super(originalName, color, lDescription, lCooldown, mat);
		this.configEntries.put("FlameDuration", 200);
		this.configEntries.put("FireResistanceLevel", 5);
		triggers.add(Trigger.MOVE);
		triggers.add(Trigger.WEAR_ITEM);
	}

	@Override
	public boolean effect(Event event, final Player player) {
		  final PlayerMoveEvent e = (PlayerMoveEvent) event;
		  final Block b = e.getTo().getBlock();
		  		  
		  if(!Tools.checkWorldGuard(e.getTo(), player, "PVP", false))
			  return false;
		  
		  if(b.getType().equals(Material.AIR)) {
			b.setType(Material.FIRE);
			new BukkitRunnable() {
				@Override
				public void run() {
					if(b.getType().equals(Material.FIRE)) {
						player.getWorld().playEffect(e.getTo(), Effect.EXTINGUISH, 60);
						b.setType(Material.AIR);
					}
				}
			}.runTaskLater(main, FlameDuration);
		
		}
		return true;
	}

	@Override
	public void initConfigEntries() {
		FireResistanceLevel = Integer.parseInt(getConfig().getString("Items." + getOriginalName() + ".FireResistanceLevel"))-1;
		FlameDuration = Integer.parseInt(getConfig().getString("Items." + getOriginalName() + ".FlameDuration"));
		this.potionsOnWear.put(PotionEffectType.FIRE_RESISTANCE, FireResistanceLevel);
	}

}
