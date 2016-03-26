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
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.taiter.ce.EffectManager;
import com.taiter.ce.Tools;


public class ThorsAxe extends CItem {

	int FireDuration;
	
	public ThorsAxe(String originalName, ChatColor color, String lDescription, long lCooldown, Material mat) {
		super(originalName, color, lDescription, lCooldown, mat);
		this.configEntries.put("FireDuration", 200);
		triggers.add(Trigger.INTERACT_RIGHT);
	}

	@Override
	public boolean effect(Event event, Player player) {
		if(event instanceof PlayerInteractEvent) {
			final PlayerInteractEvent e = (PlayerInteractEvent) event;
					if(e.getClickedBlock() != null) {
						Location loc = e.getClickedBlock().getLocation();
						loc.setY(loc.getY() +1);
					if(Tools.checkWorldGuard(loc, player, "PVP", true)) {
						player.getWorld().strikeLightning(loc);
						if(loc.getBlock().getType().equals(Material.AIR)) {
							loc.getBlock().setType(Material.FIRE);
						}
						loc.setX(loc.getX() +1);
						if(loc.getBlock().getType().equals(Material.AIR) && Tools.checkWorldGuard(loc, player, "PVP", false)) 
							loc.getBlock().setType(Material.FIRE);
						loc.setX(loc.getX() -2);
						if(loc.getBlock().getType().equals(Material.AIR) && Tools.checkWorldGuard(loc, player, "PVP", false)) 
							loc.getBlock().setType(Material.FIRE);
						loc.setX(loc.getX() +1);
						loc.setZ(loc.getZ() +1);
						if(loc.getBlock().getType().equals(Material.AIR) && Tools.checkWorldGuard(loc, player, "PVP", false)) 
							loc.getBlock().setType(Material.FIRE);
						loc.setZ(loc.getZ() -2);
						if(loc.getBlock().getType().equals(Material.AIR) && Tools.checkWorldGuard(loc, player, "PVP", false)) 
							loc.getBlock().setType(Material.FIRE);
					
						EffectManager.playSound(e.getClickedBlock().getLocation(), "ENTITY_ENDERDRAGON_GROWL", 0.75f, 1f);
					
						player.getItemInHand().setDurability((short) (player.getItemInHand().getDurability() + 1));
					
						new BukkitRunnable() {
							@Override
							public void run() {
								Location loc = e.getClickedBlock().getLocation();
								loc.setY(loc.getY() +1);
								if(loc.getBlock().getType().equals(Material.FIRE)) {
								loc.getBlock().setType(Material.AIR);
								}
								loc.setX(loc.getX() +1);
								if(loc.getBlock().getType().equals(Material.FIRE)) {
								loc.getBlock().setType(Material.AIR);
								}
								loc.setX(loc.getX() -2);
								if(loc.getBlock().getType().equals(Material.FIRE)) {
									loc.getBlock().setType(Material.AIR);
								}
								loc.setX(loc.getX() +1);
									loc.setZ(loc.getZ() +1);
								if(loc.getBlock().getType().equals(Material.FIRE)) {
									loc.getBlock().setType(Material.AIR);
								}
								loc.setZ(loc.getZ() -2);
								if(loc.getBlock().getType().equals(Material.FIRE)) {
									loc.getBlock().setType(Material.AIR);
								}
								this.cancel();
								}
							}.runTaskLater(main, FireDuration);
							
							return true;
							
						}
					}
		}
		return false;
	}

	@Override
	public void initConfigEntries() {
		FireDuration = Integer.parseInt(getConfig().getString("Items." + getOriginalName() + ".FireDuration"));
	}

}
