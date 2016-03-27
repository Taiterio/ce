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
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.taiter.ce.EffectManager;


public class AssassinsBlade extends CItem {

	int InvisibilityDuration;
	int AmbushDmgMultiplier;
	int WeaknessLevel;
	int WeaknessLength;
	
	public AssassinsBlade(String originalName, ChatColor color, String lDescription, long lCooldown, Material mat) {
		super(originalName, color, lDescription, lCooldown, mat);
		this.configEntries.put("InvisibilityDuration", 400);
		this.configEntries.put("AmbushDmgMultiplier", 2);
		this.configEntries.put("WeaknessLevel", 4);
		this.configEntries.put("WeaknessLength", 100);
		triggers.add(Trigger.INTERACT_RIGHT);
		triggers.add(Trigger.DAMAGE_GIVEN);

	}

	@Override
	public boolean effect(Event event, final Player player) {
		if(event instanceof PlayerInteractEvent) {
			if(!player.hasMetadata("ce.assassin"))
				if(player.isSneaking())
						  player.setMetadata("ce.assassin", new FixedMetadataValue(main, null));
						  player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, InvisibilityDuration, 0, true), true);
						  player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "You hide in the shadows.");
						  new BukkitRunnable() {
							  @Override
							  public void run() {
								  if(player.hasMetadata("ce.assassin")) {
									  player.removeMetadata("ce.assassin", main);
									  player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "You are no longer hidden!");
								  }
							  }
						  }.runTaskLater(main, InvisibilityDuration);
						  return true;
					  }
		if(event instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent e = ((EntityDamageByEntityEvent) event);
			if(e.getDamager() == player && player.hasMetadata("ce.assassin")) {
				  e.setDamage(e.getDamage() * AmbushDmgMultiplier);
				  player.removeMetadata("ce.assassin", main);
				  player.removePotionEffect(PotionEffectType.INVISIBILITY);
				  EffectManager.playSound(e.getEntity().getLocation(), "BLOCK_PISTON_EXTEND", 0.4f, 0.1f);
				  player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, WeaknessLength, WeaknessLevel, false), true);
				  player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "You are no longer hidden!");
		   }
		}
		 return false;
	}

	@Override
	public void initConfigEntries() {
		InvisibilityDuration = Integer.parseInt(getConfig().getString("Items." + getOriginalName() + ".InvisibilityDuration"));
		AmbushDmgMultiplier = Integer.parseInt(getConfig().getString("Items." + getOriginalName() + ".AmbushDmgMultiplier"));
		WeaknessLevel = Integer.parseInt(getConfig().getString("Items." + getOriginalName() + ".WeaknessLevel"))-1;
		WeaknessLength = Integer.parseInt(getConfig().getString("Items." + getOriginalName() + ".WeaknessLength"));
	
	}

}
